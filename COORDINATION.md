# COORDINATION.md — Grid Bot Hub (Discord Bot Maker for Android)

## Brand Identity — Grid

**Product Name:** Grid — Discord Bot Maker
**Brand Mark:** Minimalist geometric "G" — clean lines within a rounded square.
**Primary Color:** Discord Blurple (`#5865F2`)
**Dark Tone:** Deep Charcoal (`#1E1F22`)
**Typeface:** Inter / Roboto (system sans-serif). No monospace.

The Grid branding is documented in `ui/AppTheme.kt` via a descriptive header comment block. The splash screen (`ui/SplashScreen.kt`) features a scale-in animated "G" logo on a Blurple-to-Charcoal gradient.

---

## UI Direction: High-Fidelity Discord Official

**STATUS: Active — all screens now targeting HiFi Discord Official style.**

All agents and contributors must follow the updated spec. See color palette, typography, and component style sections below.

---

## Art Direction

**Theme:** Discord Design Language (Dark) — High-Fidelity alignment

### Color Palette — Discord Official (HiFi Verified)
| Token | Hex | Usage |
|---|---|---|
| Background | `#313338` | Discord dark — root background |
| Surface | `#2B2D31` | Cards, sheets, headers, nav bars |
| SurfaceVariant | `#232428` | Elevated surfaces, sidebars |
| Primary (Blurple) | `#5865F2` | Buttons, links, active states, CTAs |
| Success | `#23A559` | Online status, confirmations |
| Warning | `#FAA81A` | Caution badges |
| Error | `#F23F43` | Destructive actions |
| TextPrimary | `#FFFFFF` | High-contrast text |
| TextSecondary | `#B5BAC1` | Subtitles, labels |
| TextMuted | `#80848E` | Hints, disabled text |

### Module Accent Colors
| Token | Hex | Module | Icon |
|---|---|---|---|
| AccentRocket | `#57F287` | Launch & Deploy | Rocket |
| AccentShield | `#FEE75C` | Moderation | Shield |
| AccentBrain | `#EB459E` | AI & Intelligence | Brain |
| AccentBolt | `#5865F2` | Commands & Utilities | Bolt |
| AccentMusic | `#ED4245` | Music & Audio | Music |
| AccentGear | `#99AAB5` | Configuration | Gear |
| AccentChart | `#3BA55D` | Analytics | Chart |
| AccentWelcome | `#FAA81A` | Welcome/Onboarding | Wave |

### Typography
- **Font family:** System Sans-Serif (`FontFamily.SansSerif`) across all screens.
- **No Monospace anywhere.**

### Component Style (HiFi Spec)
- **Cards:** Flat `Card` (M3) with `0.dp` elevation, `8-12dp` rounded corners.
- **Buttons:** Solid Blurple fill, `0.dp` elevation, `8.dp` corners.
- **Bottom nav:** NavBarBackground (#1E1F22), active tab gets Blurple bar indicator.

### Screen Architecture
```
SplashScreen -> Dashboard (Home tab)
                +-- Tool Library (Templates tab) — "The Tree"
                +-- Settings tab
                +-- Live Console (push)
                +-- AI AutoMod (push)
                +-- Command Builder (push)
                +-- Bot Creation (push)
                +-- Asistente Orion (push) — AI query chat
```

---

## File Inventory

| File | Purpose |
|------|--------|
| `ui/AppTheme.kt` | HiFi Discord color palette, Material3 dark scheme, typography |
| `ui/SplashScreen.kt` | Animated splash with scale-in logo, gradient, tagline |
| `ui/MainDashboardScreen.kt` | Dashboard + Login card + Bot status + Module grid + Bottom nav |
| `ui/ToolLibraryScreen.kt` | The "Discord Tree" — collapsible tool library with 7 categories |
| `ui/AppNavigation.kt` | Nav graph with Scaffold, bottom tabs, all routes |
| `ui/DoubtAssistantScreen.kt` | AI-powered chat assistant for bot setup queries |
| `ui/OrionBubble.kt` | Global draggable FAB for Asistente Orion |
| `ui/StatsDashboardScreen.kt` | Server analytics dashboard |
| `AutoModScreen.kt` | AI moderation configuration screen |
| `BotCreationScreen.kt` | 3-step bot creation wizard |
| `CommandBuilderScreen.kt` | Visual slash-command editor |
| `LiveConsoleScreen.kt` | Real-time log console |
| `MusicPlayerScreen.kt` | Music player with queue |
| `backend_api.py` | FastAPI backend bridge |
| `COORDINATION.md` | This file |

---

## For Codex / Other Agents

If you add or modify any UI screen:
1. Import colors from `AppColors` in `ui/AppTheme.kt` — never hardcode hex values.
2. Use `FontFamily.SansSerif` only — zero monospace.
3. Cards: `0.dp` elevation, `8-12dp` `RoundedCornerShape`.
4. Buttons: Blurple fill, `0.dp` elevation, `8.dp` corners.
5. Follow the accent color mapping for module-specific tints.
6. Bottom nav is handled by `GridBottomNavBar` in `MainDashboardScreen.kt`.

---

## Asistente Orion Module

**Route:** `AppRoutes.DOUBT_ASSISTANT` (`"doubt_assistant"`)
**File:** `ui/DoubtAssistantScreen.kt`
**Package:** `com.discordbotmaker.android.ui.doubt`

### What It Does
- Chat-like interface for user queries about bot setup.
- User messages: right-aligned Blurple bubbles.
- AI responses: left-aligned flat cards with "Orion" branding.
- Animated typing indicator. Sticky bottom input bar.

### Integration Points
- Featured in Tool Library (banner + Commands & Utilities category).
- Navigable via `AppRoutes.DOUBT_ASSISTANT`.

### Upcoming: LLM Backend API Endpoint

**STATUS: Mock responses only — awaiting backend integration.**

Requirements:
1. Create `/api/doubt/query` POST endpoint in `backend_api.py`.
2. Accept `{ "query": string, "conversation_history": [...] }`.
3. Forward to LLM provider. Return `{ "response": string, "sources": [...] }`.
4. System prompt: "You are Orion, the in-app assistant for Grid Bot Hub."
5. Rate-limit to 20 queries/minute per user.
6. Replace `generateMockResponse()` with real API call.

**Priority:** HIGH

---

## In-App Assistant Identity: Orion

**Official Name:** Orion
**Role:** The in-app AI assistant persona for Grid Bot Hub.

### Naming Convention
- Always refer to the assistant as **Orion** in user-facing text.
- Module listed as **"Asistente Orion"** in Tool Library.
- AI response cards branded with label **"Orion"**.

---

## Minimalist Persona — Orion Response Style (Updated 2026-05-03)

**STATUS: Active — all Orion responses must follow this guideline.**

### Principle
Orion speaks in **ultra-concise, direct fragments**. No filler. No long introductions. Think terminal brevity with a helpful tone.

### Response Rules
1. **No greeting preambles.** Jump straight to the answer.
2. **Fragment-style replies.** Short sentences, bullet fragments, structured one-liners.
3. **Bold key terms** with Markdown `**bold**` for scanability.
4. **Max ~2-3 lines per response** unless detail requested.
5. **Action-oriented.** Tell the user what to do.
6. **Numbered steps** only for sequential workflows.
7. **Fallback response** is a single-line topic list.

### Examples
| User Says | Old (Verbose) | New (Minimalist) |
|---|---|---|
| "How do I create a slash command?" | "To create a slash command, head to the Command Builder module..." | "Open **Command Builder**. Define name -> options -> response." |
| "Tell me about moderation" | "The AI AutoMod module uses Google Gemini to filter..." | "**AutoMod** -> Gemini-powered filter. Set sensitivity, actions." |
| "What can you help with?" | "Great question! Here's what I can help you with..." | "I cover: commands, moderation, music, deploy, analytics, config. Just ask." |

### Welcome Message
`"Orion online. Ask me anything about your bot. ⚡"`

### For Codex / Other Agents
When writing or modifying Orion responses:
1. Follow the Minimalist Persona rules above.
2. LLM system prompt: "Reply in ultra-concise fragments. No filler. Direct and helpful."
3. `generateMockResponse()` in `DoubtAssistantScreen.kt` matches this style.
4. Response delay: 600-1400ms (matches brevity feel).

---

## Donna Icon Set — Audio, Bunker, Hash (Added 2026-05-03)

**STATUS: Integrated into ToolLibraryScreen.kt**

### New ChannelType Enum Values
| Type | Icon | Usage |
|------|------|-------|
| `AUDIO` | headphones | Music & Audio channels |
| `BUNKER` | castle | Security/protection channels |
| `HASH` | hash-key | Reference/routing channels |

### Channels Using New Icons
- `token-connect` -> HASH
- `bot-packager` -> BUNKER
- `audit-logger` -> HASH
- `toxicity-filter` -> BUNKER
- `slash-commands` -> HASH
- `music-player` -> AUDIO
- `radio-mode` -> AUDIO
- `soundboard` -> AUDIO
- `permissions` -> BUNKER
- `channel-router` -> HASH

### For Codex / Other Agents
When adding new channels:
- Use `ChannelType.AUDIO` for audio/voice-related streaming tools.
- Use `ChannelType.BUNKER` for security, safety, or protection-related tools.
- Use `ChannelType.HASH` for reference, logging, or routing-related tools.

---

## Server Tree View

The Tool Library uses Discord's Server Tree View aesthetic:
- Categories: uppercase section headers with collapse chevrons.
- Channels: compact 30dp rows with kebab-case names.
- Branch lines drawn via Canvas + drawLine.
- OrionBubble is a global FAB — do not add per-screen FABs.
