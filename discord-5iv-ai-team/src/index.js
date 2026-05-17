import {
  ChannelType,
  Client,
  GatewayIntentBits,
  Partials,
  PermissionFlagsBits
} from "discord.js";
import { agents } from "./agents.js";
import { planAdminProposal } from "./adminPlanner.js";
import { config, validateConfig } from "./config.js";
import { applyAdminActions, guildSnapshot } from "./discordAdmin.js";
import { runTeamRound } from "./session.js";
import {
  addMemory,
  createProposal,
  findProposal,
  listProposals,
  loadState,
  saveState,
  updateProposal
} from "./storage.js";

const missing = validateConfig();
if (missing.length > 0) {
  console.error(`Falta configurar: ${missing.join(", ")}`);
  console.error("Copia .env.example a .env y rellena esos valores.");
  process.exit(1);
}

const client = new Client({
  intents: [
    GatewayIntentBits.Guilds,
    GatewayIntentBits.GuildMessages,
    GatewayIntentBits.MessageContent
  ],
  partials: [Partials.Channel]
});

function isOwner(message) {
  if (config.ownerUserIds.length === 0) return true;
  return config.ownerUserIds.includes(message.author.id);
}

function isTeamChannel(message) {
  return message.channelId === config.teamChannelId;
}

function formatProposal(proposal) {
  const actions = proposal.actions
    .map((action, index) => `${index + 1}. ${action.type}: ${action.name || action.channelName || action.channelId || "sin nombre"}`)
    .join("\n");

  return [
    `Propuesta #${proposal.id}: ${proposal.title}`,
    proposal.summary,
    "",
    actions,
    "",
    `Para ejecutar: \`${config.commandPrefix}aprobar ${proposal.id}\``,
    `Para descartar: \`${config.commandPrefix}rechazar ${proposal.id}\``
  ].join("\n");
}

async function getRecentChannelContext(channel) {
  const messages = await channel.messages.fetch({ limit: config.maxContextMessages });
  return [...messages.values()]
    .reverse()
    .filter((message) => !message.author.bot || agents.some((agent) => message.author.username?.includes(agent.name)))
    .map((message) => {
      const author = message.member?.displayName || message.author.username;
      return `${author}: ${message.content}`;
    })
    .join("\n");
}

async function getTeamWebhook(channel) {
  if (channel.type !== ChannelType.GuildText) {
    throw new Error("El canal del equipo debe ser un canal de texto normal del servidor.");
  }

  const permissions = channel.permissionsFor(client.user);
  if (!permissions?.has(PermissionFlagsBits.ManageWebhooks)) {
    throw new Error("El bot necesita permiso Manage Webhooks en este canal.");
  }

  const webhooks = await channel.fetchWebhooks();
  const existing = webhooks.find((hook) => hook.owner?.id === client.user.id && hook.name === "5iV AI Team");
  if (existing) return existing;

  return channel.createWebhook({
    name: "5iV AI Team",
    reason: "Permite que cada IA hable con su propio nombre."
  });
}

async function sendAgentMessage(channel, agent, content) {
  const webhook = await getTeamWebhook(channel);
  await webhook.send({
    username: agent.name,
    avatarURL: agent.avatarUrl || undefined,
    content,
    allowedMentions: { parse: [] }
  });
}

async function startRound(message, topic) {
  const state = await loadState();
  if (state.paused) {
    await message.reply("El equipo está en pausa. Usa `!reanudar` antes de pedir otra ronda.");
    return;
  }

  await message.channel.send("Arranco una ronda del equipo 5iV.");
  const context = await getRecentChannelContext(message.channel);
  await runTeamRound({
    topic,
    channelContext: context,
    sendAgentMessage: (agent, text) => sendAgentMessage(message.channel, agent, text)
  });
}

async function createAdminProposalFromMessage(message, request) {
  if (!request) {
    await message.reply("Dime qué cambio quieres preparar después de `!admin`.");
    return;
  }

  await message.channel.send("Rune está preparando una propuesta administrativa. No tocaré el servidor hasta que la apruebes.");
  const snapshot = await guildSnapshot(message.guild);
  const plan = await planAdminProposal({ request, guildSnapshot: snapshot });
  const proposal = await createProposal({
    ...plan,
    createdBy: message.author.id
  });

  await message.reply(formatProposal(proposal));
}

async function approveProposal(message, body) {
  const id = Number.parseInt(body, 10);
  if (!Number.isFinite(id)) {
    await message.reply("Dime el número de propuesta. Ejemplo: `!aprobar 1`.");
    return;
  }

  const proposal = await findProposal(id);
  if (!proposal) {
    await message.reply(`No encuentro la propuesta #${id}.`);
    return;
  }
  if (proposal.status !== "pending") {
    await message.reply(`La propuesta #${id} ya está en estado: ${proposal.status}.`);
    return;
  }

  await message.channel.send(`Ejecutando propuesta #${id}: ${proposal.title}`);
  const results = await applyAdminActions(message, proposal.actions);
  await updateProposal(id, (current) => ({
    ...current,
    status: "approved",
    decidedAt: new Date().toISOString(),
    result: results
  }));

  await message.reply(["Hecho:", ...results.map((result) => `- ${result}`)].join("\n"));
}

async function rejectProposal(message, body) {
  const id = Number.parseInt(body, 10);
  if (!Number.isFinite(id)) {
    await message.reply("Dime el número de propuesta. Ejemplo: `!rechazar 1`.");
    return;
  }

  const proposal = await updateProposal(id, (current) => ({
    ...current,
    status: "rejected",
    decidedAt: new Date().toISOString()
  }));

  if (!proposal) {
    await message.reply(`No encuentro la propuesta #${id}.`);
    return;
  }

  await message.reply(`Propuesta #${id} rechazada.`);
}

async function showProposals(message) {
  const proposals = await listProposals("pending");
  if (proposals.length === 0) {
    await message.reply("No hay propuestas pendientes.");
    return;
  }

  await message.reply(
    proposals
      .map((proposal) => `#${proposal.id} - ${proposal.title}\n${proposal.summary}`)
      .join("\n\n")
  );
}

async function handleCommand(message) {
  const raw = message.content.slice(config.commandPrefix.length).trim();
  const [commandName, ...rest] = raw.split(/\s+/);
  const command = commandName?.toLowerCase();
  const body = rest.join(" ").trim();

  if (
    ![
      "ayuda",
      "equipo",
      "ronda",
      "admin",
      "propuestas",
      "aprobar",
      "rechazar",
      "estado",
      "pausa",
      "reanudar",
      "memoria",
      "memorias"
    ].includes(command)
  ) {
    return;
  }

  if (!isOwner(message)) {
    await message.reply("Este equipo privado solo lo puede manejar Shin5hi o las IDs configuradas en `OWNER_USER_IDS`.");
    return;
  }

  if (command === "ayuda") {
    await message.reply(
      [
        "`!equipo <tema>` arranca una ronda con las 4 IAs.",
        "`!admin <cambio>` prepara una propuesta para crear, mover, borrar canales/categorias o tocar permisos.",
        "`!propuestas` muestra las propuestas pendientes.",
        "`!aprobar <id>` ejecuta una propuesta.",
        "`!rechazar <id>` descarta una propuesta.",
        "`!ronda` hace otra ronda sobre el tema actual.",
        "`!estado` muestra pausa, tema y ultima ronda.",
        "`!pausa` y `!reanudar` paran o activan el equipo.",
        "`!memoria <dato>` guarda algo importante para las IAs.",
        "`!memorias` muestra lo guardado."
      ].join("\n")
    );
    return;
  }

  if (command === "admin") {
    await createAdminProposalFromMessage(message, body);
    return;
  }

  if (command === "propuestas") {
    await showProposals(message);
    return;
  }

  if (command === "aprobar") {
    await approveProposal(message, body);
    return;
  }

  if (command === "rechazar") {
    await rejectProposal(message, body);
    return;
  }

  if (command === "equipo" || command === "ronda") {
    await startRound(message, body);
    return;
  }

  if (command === "pausa") {
    const state = await loadState();
    state.paused = true;
    await saveState(state);
    await message.reply("Equipo pausado.");
    return;
  }

  if (command === "reanudar") {
    const state = await loadState();
    state.paused = false;
    await saveState(state);
    await message.reply("Equipo reanudado.");
    return;
  }

  if (command === "memoria") {
    if (!body) {
      await message.reply("Dime qué quieres guardar después de `!memoria`.");
      return;
    }
    await addMemory(body);
    await message.reply("Guardado en la memoria del equipo.");
    return;
  }

  if (command === "memorias") {
    const state = await loadState();
    await message.reply(state.memories.map((memory, index) => `${index + 1}. ${memory}`).join("\n"));
    return;
  }

  if (command === "estado") {
    const state = await loadState();
    await message.reply(
      [
        `Pausa: ${state.paused ? "sí" : "no"}`,
        `Tema: ${state.topic || "(sin tema)"}`,
        `Última ronda: ${state.lastRoundAt || "(todavía ninguna)"}`,
        `Auto charla: ${config.autoTalkEnabled ? `sí, cada ${config.autoTalkIntervalMinutes} min` : "no"}`
      ].join("\n")
    );
  }
}

client.once("ready", async () => {
  console.log(`Bot conectado como ${client.user.tag}`);
  const channel = await client.channels.fetch(config.teamChannelId).catch(() => null);
  if (!channel) {
    console.error("No puedo encontrar TEAM_CHANNEL_ID. Revisa el .env.");
    return;
  }

  if (config.autoTalkEnabled) {
    const intervalMs = Math.max(config.autoTalkIntervalMinutes, 5) * 60 * 1000;
    setInterval(async () => {
      try {
        const state = await loadState();
        if (state.paused) return;
        const context = await getRecentChannelContext(channel);
        await runTeamRound({
          topic: state.topic || "Revisad si hay algo importante que organizar en el servidor privado de 5iV.",
          channelContext: context,
          sendAgentMessage: (agent, text) => sendAgentMessage(channel, agent, text)
        });
      } catch (error) {
        console.error("Error en ronda automatica:", error);
      }
    }, intervalMs);
  }
});

client.on("messageCreate", async (message) => {
  if (message.author.bot) return;
  if (!isTeamChannel(message)) return;
  if (!message.content.startsWith(config.commandPrefix)) return;

  try {
    await handleCommand(message);
  } catch (error) {
    console.error(error);
    await message.reply(`He tenido un error: ${error.message}`);
  }
});

client.login(config.discordToken);
