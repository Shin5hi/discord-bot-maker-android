import "dotenv/config";

function list(value) {
  if (!value) return [];
  return value
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

function int(name, fallback) {
  const parsed = Number.parseInt(process.env[name] ?? "", 10);
  return Number.isFinite(parsed) ? parsed : fallback;
}

function bool(name, fallback) {
  const value = process.env[name];
  if (value === undefined || value === "") return fallback;
  return ["1", "true", "yes", "si", "sí"].includes(value.toLowerCase());
}

export const config = {
  discordToken: process.env.DISCORD_TOKEN?.trim() ?? "",
  teamChannelId: process.env.TEAM_CHANNEL_ID?.trim() ?? "",
  ownerUserIds: list(process.env.OWNER_USER_IDS),
  commandPrefix: process.env.COMMAND_PREFIX || "!",
  maxContextMessages: int("MAX_CONTEXT_MESSAGES", 24),
  maxAgentWords: int("MAX_AGENT_WORDS", 120),
  autoTalkEnabled: bool("AUTO_TALK_ENABLED", false),
  autoTalkIntervalMinutes: int("AUTO_TALK_INTERVAL_MINUTES", 45),
  geminiModel: process.env.GEMINI_MODEL || "gemini-2.5-flash",
  geminiApiKey: process.env.GEMINI_API_KEY?.trim() ?? "",
  agentApiKeys: {
    freyja: process.env.GEMINI_API_KEY_FREYJA?.trim() ?? "",
    sigrid: process.env.GEMINI_API_KEY_SIGRID?.trim() ?? "",
    eira: process.env.GEMINI_API_KEY_EIRA?.trim() ?? "",
    rune: process.env.GEMINI_API_KEY_RUNE?.trim() ?? ""
  },
  avatars: {
    freyja: process.env.AVATAR_FREYJA?.trim() ?? "",
    sigrid: process.env.AVATAR_SIGRID?.trim() ?? "",
    eira: process.env.AVATAR_EIRA?.trim() ?? "",
    rune: process.env.AVATAR_RUNE?.trim() ?? ""
  }
};

export function validateConfig() {
  const missing = [];
  if (!config.discordToken || config.discordToken.includes("pon_aqui")) {
    missing.push("DISCORD_TOKEN");
  }
  if (!config.teamChannelId || config.teamChannelId.includes("pon_aqui")) {
    missing.push("TEAM_CHANNEL_ID");
  }

  const hasSharedKey = Boolean(config.geminiApiKey);
  const hasAnyAgentKey = Object.values(config.agentApiKeys).some(Boolean);
  if (!hasSharedKey && !hasAnyAgentKey) {
    missing.push("GEMINI_API_KEY o claves GEMINI_API_KEY_*");
  }

  return missing;
}

export function apiKeyForAgent(agentId) {
  return config.agentApiKeys[agentId] || config.geminiApiKey;
}
