import { ChannelType, PermissionFlagsBits } from "discord.js";

const permissionMap = {
  ViewChannel: PermissionFlagsBits.ViewChannel,
  SendMessages: PermissionFlagsBits.SendMessages,
  ReadMessageHistory: PermissionFlagsBits.ReadMessageHistory,
  ManageMessages: PermissionFlagsBits.ManageMessages,
  ManageChannels: PermissionFlagsBits.ManageChannels,
  ManageWebhooks: PermissionFlagsBits.ManageWebhooks,
  Connect: PermissionFlagsBits.Connect,
  Speak: PermissionFlagsBits.Speak,
  UseVAD: PermissionFlagsBits.UseVAD
};

function normalizeName(name) {
  return String(name || "")
    .trim()
    .toLowerCase();
}

function permissions(names = []) {
  return names.map((name) => {
    const value = permissionMap[name];
    if (!value) throw new Error(`Permiso no soportado: ${name}`);
    return value;
  });
}

function permissionOptions({ allow = [], deny = [] }) {
  const options = {};
  for (const name of allow) {
    if (!permissionMap[name]) throw new Error(`Permiso no soportado: ${name}`);
    options[name] = true;
  }
  for (const name of deny) {
    if (!permissionMap[name]) throw new Error(`Permiso no soportado: ${name}`);
    options[name] = false;
  }
  return options;
}

function requireGuild(message) {
  if (!message.guild) throw new Error("Este comando solo funciona dentro de un servidor.");
  return message.guild;
}

async function findCategory(guild, action) {
  if (action.categoryId) {
    const found = await guild.channels.fetch(action.categoryId).catch(() => null);
    if (found?.type === ChannelType.GuildCategory) return found;
  }

  if (!action.categoryName) return null;
  return guild.channels.cache.find(
    (channel) =>
      channel.type === ChannelType.GuildCategory &&
      normalizeName(channel.name) === normalizeName(action.categoryName)
  ) ?? null;
}

async function findChannel(guild, action) {
  const id = action.channelId || action.id;
  if (id) {
    const found = await guild.channels.fetch(id).catch(() => null);
    if (found) return found;
  }

  const name = action.channelName || action.name;
  if (!name) return null;
  return guild.channels.cache.find((channel) => normalizeName(channel.name) === normalizeName(name)) ?? null;
}

function privateOverwrites(guild, privateChannel) {
  if (!privateChannel) return [];
  return [
    {
      id: guild.roles.everyone.id,
      deny: [PermissionFlagsBits.ViewChannel]
    }
  ];
}

function resolveTarget(guild, target) {
  if (!target) throw new Error("Falta target en set_channel_permissions.");
  if (target === "everyone") return guild.roles.everyone.id;
  if (target.startsWith("roleId:")) return target.slice("roleId:".length);
  if (target.startsWith("userId:")) return target.slice("userId:".length);
  if (target.startsWith("role:")) {
    const roleName = target.slice("role:".length);
    const role = guild.roles.cache.find((item) => normalizeName(item.name) === normalizeName(roleName));
    if (!role) throw new Error(`No encuentro el rol: ${roleName}`);
    return role.id;
  }
  throw new Error(`Target de permisos no soportado: ${target}`);
}

async function createCategory(guild, action) {
  const existing = guild.channels.cache.find(
    (channel) =>
      channel.type === ChannelType.GuildCategory &&
      normalizeName(channel.name) === normalizeName(action.name)
  );
  if (existing) return `La categoria "${action.name}" ya existia.`;

  await guild.channels.create({
    name: action.name,
    type: ChannelType.GuildCategory,
    permissionOverwrites: privateOverwrites(guild, action.private)
  });
  return `Categoria creada: ${action.name}`;
}

async function createTextChannel(guild, action) {
  const category = await findCategory(guild, action);
  const existing = guild.channels.cache.find(
    (channel) =>
      channel.type === ChannelType.GuildText &&
      normalizeName(channel.name) === normalizeName(action.name) &&
      (!category || channel.parentId === category.id)
  );
  if (existing) return `El canal de texto "${action.name}" ya existia.`;

  await guild.channels.create({
    name: action.name,
    type: ChannelType.GuildText,
    parent: category?.id,
    topic: action.topic || undefined,
    permissionOverwrites: privateOverwrites(guild, action.private)
  });
  return `Canal de texto creado: ${action.name}`;
}

async function createVoiceChannel(guild, action) {
  const category = await findCategory(guild, action);
  const existing = guild.channels.cache.find(
    (channel) =>
      channel.type === ChannelType.GuildVoice &&
      normalizeName(channel.name) === normalizeName(action.name) &&
      (!category || channel.parentId === category.id)
  );
  if (existing) return `El canal de voz "${action.name}" ya existia.`;

  await guild.channels.create({
    name: action.name,
    type: ChannelType.GuildVoice,
    parent: category?.id,
    userLimit: Number.isInteger(action.userLimit) ? action.userLimit : undefined,
    permissionOverwrites: privateOverwrites(guild, action.private)
  });
  return `Canal de voz creado: ${action.name}`;
}

async function deleteChannel(guild, action) {
  const channel = await findChannel(guild, action);
  if (!channel) throw new Error(`No encuentro el canal a eliminar: ${action.channelName || action.channelId}`);
  await channel.delete("Propuesta administrativa aprobada por Shin5hi.");
  return `Canal eliminado: ${channel.name}`;
}

async function moveChannel(guild, action) {
  const channel = await findChannel(guild, action);
  if (!channel) throw new Error(`No encuentro el canal a mover: ${action.channelName || action.channelId}`);
  const category = await findCategory(guild, action);
  if (!category) throw new Error(`No encuentro la categoria destino: ${action.categoryName || action.categoryId}`);
  await channel.setParent(category.id, { lockPermissions: false });
  return `Canal movido: ${channel.name} -> ${category.name}`;
}

async function setChannelPermissions(guild, action) {
  const channel = await findChannel(guild, action);
  if (!channel) throw new Error(`No encuentro el canal: ${action.channelName || action.channelId}`);
  const targetId = resolveTarget(guild, action.target);
  await channel.permissionOverwrites.edit(targetId, permissionOptions(action));
  return `Permisos actualizados en ${channel.name} para ${action.target}`;
}

export async function guildSnapshot(guild) {
  await guild.channels.fetch();
  await guild.roles.fetch();

  const categories = guild.channels.cache
    .filter((channel) => channel.type === ChannelType.GuildCategory)
    .map((category) => {
      const children = guild.channels.cache
        .filter((channel) => channel.parentId === category.id)
        .map((channel) => `  - ${channel.name} (${channel.type})`)
        .join("\n");
      return `- ${category.name}\n${children || "  (sin canales)"}`;
    })
    .join("\n");

  const uncategorized = guild.channels.cache
    .filter((channel) => channel.type !== ChannelType.GuildCategory && !channel.parentId)
    .map((channel) => `- ${channel.name} (${channel.type})`)
    .join("\n");

  const roles = guild.roles.cache
    .filter((role) => role.name !== "@everyone")
    .map((role) => role.name)
    .slice(0, 30)
    .join(", ");

  return [
    `Nombre: ${guild.name}`,
    `Roles: ${roles || "(sin roles relevantes)"}`,
    "Categorias y canales:",
    categories || "(sin categorias)",
    "Canales sin categoria:",
    uncategorized || "(ninguno)"
  ].join("\n");
}

export async function applyAdminActions(message, actions) {
  const guild = requireGuild(message);
  const results = [];

  for (const action of actions) {
    if (action.type === "create_category") {
      results.push(await createCategory(guild, action));
    } else if (action.type === "create_text_channel") {
      results.push(await createTextChannel(guild, action));
    } else if (action.type === "create_voice_channel") {
      results.push(await createVoiceChannel(guild, action));
    } else if (action.type === "delete_channel") {
      results.push(await deleteChannel(guild, action));
    } else if (action.type === "move_channel") {
      results.push(await moveChannel(guild, action));
    } else if (action.type === "set_channel_permissions") {
      results.push(await setChannelPermissions(guild, action));
    } else {
      throw new Error(`Accion no soportada: ${action.type}`);
    }
  }

  return results;
}
