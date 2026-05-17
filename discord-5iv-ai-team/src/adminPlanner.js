import { apiKeyForAgent } from "./config.js";
import { generateWithGemini } from "./gemini.js";
import { loadState } from "./storage.js";

const allowedActions = [
  "create_category",
  "create_text_channel",
  "create_voice_channel",
  "delete_channel",
  "set_channel_permissions",
  "move_channel"
];

function extractJson(text) {
  const trimmed = text.trim();
  if (trimmed.startsWith("{")) return trimmed;
  const match = trimmed.match(/```(?:json)?\s*([\s\S]*?)```/i);
  if (match) return match[1].trim();
  const first = trimmed.indexOf("{");
  const last = trimmed.lastIndexOf("}");
  if (first !== -1 && last !== -1 && last > first) {
    return trimmed.slice(first, last + 1);
  }
  return trimmed;
}

function validatePlan(plan) {
  if (!plan || typeof plan !== "object") {
    throw new Error("La IA no ha devuelto una propuesta valida.");
  }
  if (!Array.isArray(plan.actions) || plan.actions.length === 0) {
    throw new Error("La propuesta no contiene acciones administrativas.");
  }

  for (const action of plan.actions) {
    if (!allowedActions.includes(action.type)) {
      throw new Error(`Accion no permitida: ${action.type}`);
    }
  }

  return {
    title: String(plan.title || "Propuesta administrativa").slice(0, 120),
    summary: String(plan.summary || "Sin resumen.").slice(0, 1200),
    actions: plan.actions
  };
}

function buildPrompt({ request, guildSnapshot }) {
  return `
Eres Rune, la tecnica del equipo privado de Shin5hi / 5iV.

Convierte la peticion de Shin5hi en una propuesta administrativa para Discord.
No ejecutes nada. Devuelve solo JSON valido, sin markdown.

Contexto:
- Servidor privado de Shin5hi / 5iV.
- No es una comunidad publica.
- Hay clientes, gestion interna y posible tienda Tebex/web.
- La propuesta debe ser prudente, ordenada y facil de revisar.
- Si algo implica borrar, usa delete_channel solo cuando la peticion lo pida claramente.

Servidor actual:
${guildSnapshot}

Peticion:
${request}

Formato obligatorio:
{
  "title": "titulo corto",
  "summary": "explicacion humana de lo que se va a cambiar",
  "actions": [
    {
      "type": "create_category",
      "name": "nombre-categoria",
      "private": true
    },
    {
      "type": "create_text_channel",
      "name": "nombre-canal",
      "categoryName": "nombre-categoria",
      "topic": "tema opcional",
      "private": true
    },
    {
      "type": "create_voice_channel",
      "name": "nombre-voz",
      "categoryName": "nombre-categoria",
      "private": true,
      "userLimit": 4
    },
    {
      "type": "set_channel_permissions",
      "channelName": "nombre-canal",
      "target": "everyone",
      "allow": [],
      "deny": ["ViewChannel"]
    }
  ]
}

Acciones permitidas:
- create_category: name, private
- create_text_channel: name, categoryName, topic, private
- create_voice_channel: name, categoryName, userLimit, private
- delete_channel: channelId o channelName
- move_channel: channelId o channelName, categoryId o categoryName
- set_channel_permissions: channelId o channelName, target, allow, deny

Targets de permisos:
- "everyone"
- "role:<nombre del rol>"
- "roleId:<id del rol>"
- "userId:<id de usuario>"

Permisos utiles:
ViewChannel, SendMessages, ReadMessageHistory, ManageMessages, ManageChannels, ManageWebhooks, Connect, Speak, UseVAD.
`.trim();
}

export async function planAdminProposal({ request, guildSnapshot }) {
  const state = await loadState();
  const prompt = [
    buildPrompt({ request, guildSnapshot }),
    "",
    "Memoria del equipo:",
    state.memories.map((memory) => `- ${memory}`).join("\n")
  ].join("\n");

  const text = await generateWithGemini({
    apiKey: apiKeyForAgent("rune"),
    prompt,
    temperature: 0.25
  });

  const plan = JSON.parse(extractJson(text));
  return validatePlan(plan);
}
