<div align="center">

# Discord Bot Maker for Android

**Build, deploy, and manage Discord bots — straight from your phone.**

</div>

---

## Mission

Discord Bot Maker for Android eliminates the need for a desktop setup to build and manage Discord bots. Full control over your bot's behavior, moderation rules, and real-time logs right from your Android device.

Bot development should be **accessible, mobile-first, and professionally polished**.

---

## Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | Kotlin Multiplatform + Jetpack Compose | Cross-platform UI with a clean, professional dark theme |
| **Backend** | FastAPI (Python) | REST + WebSocket API for bot orchestration |
| **Cache / PubSub** | Redis 7.4 | Real-time log streaming, session state, rate limiting |
| **AI Moderation** | Google Gemini API | Toxicity detection for AutoMod filters |
| **Bot Runtime** | discord.py / Pycord | Discord gateway connection and event handling |

---

## Key Features

- **Live Console** — Real-time terminal that streams bot logs over WebSocket.
- **AI AutoMod** — Configure AI-powered toxicity filtering, link blocking, and spam protection.
- **Instant Deploy** — Push bot configuration changes from your phone.
- **WebSocket Streaming** — Low-latency bidirectional communication via Redis Pub/Sub.
- **Professional Dark UI** — Clean, minimalist Material Design 3 dark theme with a professional SaaS aesthetic.
- **Music Player** — Stream music to Discord voice channels with playback controls and queue management.
- **Custom Command Builder** — Create and manage custom bot commands visually.

---

## Project Structure

```
discord-bot-maker-android/
├── README.md
├── COORDINATION.md              # Art direction & file ownership
├── ui/
│   ├── AppTheme.kt             # Material 3 theme, colors, typography
│   ├── MainDashboardScreen.kt  # Landing hub with bot status & module grid
│   └── AppNavigation.kt        # Compose Navigation graph
├── LiveConsoleScreen.kt
├── AutoModScreen.kt
├── BotCreationScreen.kt
├── CommandBuilderScreen.kt
├── MusicPlayerScreen.kt
├── backend_api.py
└── test_backend_api.py
```

---

## Getting Started

See COORDINATION.md for art direction and development conventions.

---

## License

MIT License
