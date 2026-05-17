import { config } from "./config.js";

export class GeminiError extends Error {
  constructor(message, details = "") {
    super(message);
    this.name = "GeminiError";
    this.details = details;
  }
}

export async function generateWithGemini({ apiKey, prompt, temperature = 0.85 }) {
  if (!apiKey) {
    throw new GeminiError("Falta una API key de Google AI Studio para este agente.");
  }

  const model = encodeURIComponent(config.geminiModel);
  const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${apiKey}`;

  const response = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      contents: [
        {
          role: "user",
          parts: [{ text: prompt }]
        }
      ],
      generationConfig: {
        temperature,
        topP: 0.95,
        maxOutputTokens: 420
      }
    })
  });

  const payload = await response.json().catch(() => null);
  if (!response.ok) {
    const message = payload?.error?.message ?? response.statusText;
    throw new GeminiError(`Google AI Studio ha devuelto error ${response.status}.`, message);
  }

  const text = payload?.candidates?.[0]?.content?.parts
    ?.map((part) => part.text)
    .filter(Boolean)
    .join("\n")
    .trim();

  if (!text) {
    throw new GeminiError("Gemini no ha devuelto texto util.");
  }

  return text;
}
