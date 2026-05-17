import { mkdir, readFile, writeFile } from "node:fs/promises";
import path from "node:path";

const dataDir = path.resolve("data");
const stateFile = path.join(dataDir, "state.json");

const defaultState = {
  paused: false,
  topic: "",
  nextProposalId: 1,
  proposals: [],
  memories: [
    "Shin5hi es una persona no binaria y prefiere que le hablen siempre en femenino.",
    "El servidor de Discord es privado, propio y orientado a gestion con clientes, no a comunidad publica.",
    "5iV es la marca de artista/creadora de contenido de Shin5hi.",
    "La tienda puede conectarse a Tebex o a la web mas adelante."
  ],
  lastRoundAt: null
};

export async function loadState() {
  await mkdir(dataDir, { recursive: true });
  try {
    const raw = await readFile(stateFile, "utf8");
    return { ...defaultState, ...JSON.parse(raw) };
  } catch (error) {
    if (error.code !== "ENOENT") throw error;
    await saveState(defaultState);
    return { ...defaultState };
  }
}

export async function saveState(state) {
  await mkdir(dataDir, { recursive: true });
  await writeFile(stateFile, `${JSON.stringify(state, null, 2)}\n`, "utf8");
}

export async function addMemory(memory) {
  const state = await loadState();
  state.memories.push(memory);
  await saveState(state);
  return state;
}

export async function createProposal({ title, summary, actions, createdBy }) {
  const state = await loadState();
  const proposal = {
    id: state.nextProposalId,
    status: "pending",
    title,
    summary,
    actions,
    createdBy,
    createdAt: new Date().toISOString(),
    decidedAt: null,
    result: null
  };

  state.nextProposalId += 1;
  state.proposals.push(proposal);
  await saveState(state);
  return proposal;
}

export async function listProposals(status = "pending") {
  const state = await loadState();
  if (!status || status === "all") return state.proposals;
  return state.proposals.filter((proposal) => proposal.status === status);
}

export async function findProposal(id) {
  const state = await loadState();
  return state.proposals.find((proposal) => proposal.id === id) ?? null;
}

export async function updateProposal(id, updater) {
  const state = await loadState();
  const index = state.proposals.findIndex((proposal) => proposal.id === id);
  if (index === -1) return null;

  const updated = updater({ ...state.proposals[index] });
  state.proposals[index] = updated;
  await saveState(state);
  return updated;
}
