<div align="center">

# 🤖 Discord Bot Maker for Android

**Build, deploy, and manage Discord bots — straight from your phone.**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose_Multiplatform-1.7-4285F4?logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.115-009688?logo=fastapi&logoColor=white)](https://fastapi.tiangolo.com)
[![Redis](https://img.shields.io/badge/Redis-7.4-DC382D?logo=redis&logoColor=white)](https://redis.io)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

</div>

---

## 🎯 Mission

Discord Bot Maker for Android eliminates the need for a desktop setup to build and manage Discord bots. Whether you're commuting, on break, or away from your workstation — you have full control over your bot's behavior, moderation rules, and real-time logs right from your Android device.

We believe bot development should be **accessible, mobile-first, and visually satisfying**.

---

## 🏗️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Frontend** | Kotlin Multiplatform + Jetpack Compose | Cross-platform UI with a neon/hacker aesthetic |
| **Backend** | FastAPI (Python) | REST + WebSocket API for bot orchestration |
| **Cache / PubSub** | Redis 7.4 | Real-time log streaming, session state, rate limiting |
| **AI Moderation** | Google Gemini API | Toxicity detection for AutoMod filters |
| **Bot Runtime** | discord.py / Pycord | Discord gateway connection and event handling |

---

## ✨ Key Features

- **📟 Live Console** — Real-time, neon-styled terminal that streams bot logs over WebSocket. Auto-scroll, color-coded severity levels, and a true hacker vibe.
- **🛡️ AI AutoMod** — Configure AI-powered toxicity filtering (Gemini), link blocking, and spam protection with intuitive sliders and switches.
- **⚡ Instant Deploy** — Push bot configuration changes from your phone and see them take effect in seconds.
- **🔌 WebSocket Streaming** — Low-latency, bidirectional communication between the Android client and the FastAPI backend via Redis Pub/Sub.
- **🎨 Dark Neon UI** — A carefully crafted dark theme with neon green, cyan, and magenta accents across every screen.

---

## 📂 Project Structure

```
discord-bot-maker-android/
├── README.md                    # You are here
├── app/
│   └── src/main/kotlin/
│       ├── LiveConsoleScreen.kt # Real-time log terminal UI
│       └── AutoModScreen.kt    # AI moderation config screen
├── backend/
│   └── backend_api.py          # FastAPI WebSocket + Redis integration
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2024.1) or later
- Python 3.11+
- Redis 7.x running locally or via Docker
- A Discord Bot Token ([Discord Developer Portal](https://discord.com/developers/applications))
- Google Gemini API Key (for AI moderation)

### Backend Setup

```bash
# Clone the repo
git clone https://github.com/Shin5hi/discord-bot-maker-android.git
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

## 🔧 Configuration

| Environment Variable | Description | Default |
|---------------------|-------------|---------|
| `REDIS_URL` | Redis connection string | `redis://localhost:6379` |
| `DISCORD_BOT_TOKEN` | Your Discord bot token | — |
| `GEMINI_API_KEY` | Google Gemini API key for toxicity analysis | — |
| `LOG_CHANNEL` | Redis Pub/Sub channel for log streaming | `bot:logs` |

---

## 🤝 Contributing

We welcome contributions! Please read our [Contributing Guide](CONTRIBUTING.md) before submitting a PR.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/awesome-feature`)
3. Commit your changes (`git commit -m 'Add awesome feature'`)
4. Push to the branch (`git push origin feature/awesome-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Built with 💚 for mobile-first bot developers.**

</div>