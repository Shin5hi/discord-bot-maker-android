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

### Prerequisites

- Android Studio Hedgehog (2024.1) or later
- Python 3.11+
- Redis 7.x running locally or via Docker
- A Discord Bot Token ([Discord Developer Portal](https://discord.com/developers/applications))
- Google Gemini API Key (for AI moderation)

### Backend Setup

```bash
# Clone the repo
git clone https://github.com/BMA/discord-bot-maker-android.git
cd discord-bot-maker-android/backend

# Create virtual environment
python -m venv .venv && source .venv/bin/activate

# Install dependencies
pip install fastapi uvicorn redis aioredis websockets pydantic

# Start Redis (Docker)
docker run -d --name redis-dbm -p 6379:6379 redis:7.4-alpine

# Run the backend
uvicorn backend_api:app --host 0.0.0.0 --port 8000 --reload
```

### Android App

1. Open the project root in Android Studio.
2. Sync Gradle and let dependencies resolve.
3. Update `local.properties` with your backend URL:
   ```properties
   BACKEND_WS_URL=ws://YOUR_SERVER_IP:8000/ws/logs
   BACKEND_API_URL=http://YOUR_SERVER_IP:8000
   ```
4. Build and run on your device or emulator.

---

## License

MIT License
