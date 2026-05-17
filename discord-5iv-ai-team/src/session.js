import { agents } from "./agents.js";
import { apiKeyForAgent, config } from "./config.js";
import { generateWithGemini } from "./gemini.js";
import { loadState, saveState } from "./storage.js";

function clean(text) {
  return text
    .replace(/^["'`]+|["'`]+$/g, "")
    .replace(new RegExp(`^(${agents.map((agent) => agent.name).join("|")}):\\s*`, "i"), "")
    .trim();
}

function buildPrompt({ agent, state, topic, channelContext, roundTranscript }) {
  return `
Eres ${agent.name}, ${agent.role}, dentro del equipo privado de IAs de Shin5hi / 5iV.

Contexto fijo:
- Shin5hi se pronuncia Sin-si.
- Debes hablarle siempre en femenino. No uses lenguaje neutro con ella.
- Shin5hi no es programadora: explica lo tecnico en lenguaje natural.
- El servidor Discord es privado, para gestion propia, clientes y posible tienda conectada a Tebex/web.
- No lo plantees como comunidad publica.
- Marca artistica: 5iV. Nombre de artista: Shin5hi.

Tu forma de pensar:
${agent.brief}

Memoria del equipo:
${state.memories.map((memory) => `- ${memory}`).join("\n")}

Tema actual:
${topic || state.topic || "Organizar el servidor privado de 5iV."}

Mensajes recientes del canal:
${channelContext || "(sin mensajes recientes)"}

Lo que ya han dicho otras IAs en esta ronda:
${roundTranscript || "(eres la primera de esta ronda)"}

Responde como una persona del equipo, no como asistente generica.
Maximo ${config.maxAgentWords} palabras.
No hagas listas largas salvo que sea necesario.
Si discrepas, dilo claro y ofrece una alternativa mejor.
Termina con una idea accionable o una pregunta corta para otra IA cuando encaje.
`.trim();
}

export async function runTeamRound({ topic, channelContext, sendAgentMessage }) {
  const state = await loadState();
  if (topic) {
    state.topic = topic;
  }

  const round = [];

  for (const agent of agents) {
    const prompt = buildPrompt({
      agent,
      state,
      topic,
      channelContext,
      roundTranscript: round.map((item) => `${item.name}: ${item.text}`).join("\n\n")
    });

    let text;
    try {
      text = clean(await generateWithGemini({ apiKey: apiKeyForAgent(agent.id), prompt }));
    } catch (error) {
      text = `Ahora mismo no puedo responder porque hay un problema con mi API: ${error.details || error.message}`;
    }

    round.push({ id: agent.id, name: agent.name, text });
    await sendAgentMessage(agent, text);
  }

  state.lastRoundAt = new Date().toISOString();
  await saveState(state);
  return round;
}
