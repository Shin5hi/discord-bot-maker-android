# Discord Bot Maker for Android - Developer Guide

## Project Setup

This is an Android application built with **Kotlin** and **Jetpack Compose** that allows users to create, manage, and deploy Discord bots directly from their Android device.

### Prerequisites

- Android Studio Hedgehog (2024.1) or later
- JDK 17 or later
- Android SDK API 34
- Gradle 8.2+

### Build Instructions

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Shin5hi/discord-bot-maker-android.git
   cd discord-bot-maker-android
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory and select it

3. **Sync Gradle:**
   - Android Studio should automatically sync Gradle
   - If not, click "File" → "Sync Project with Gradle Files"

4. **Run the App:**
   - Connect an Android device (API 24+) or start an emulator
   - Click the "Run" button (green play icon)
   - Select your target device

### Project Structure

```
discord-bot-maker-android/
├── app/
│   ├── build.gradle.kts          # App module Gradle build file
│   ├── proguard-rules.pro        # ProGuard rules for code optimization
│   └── src/main/
│       ├── AndroidManifest.xml   # App manifest with permissions
│       ├── kotlin/
│       │   └── com/discordbotmaker/android/
│       │       ├── MainActivity.kt              # Main app entry point
│       │       └── ui/
│       │           ├── theme/                   # Centralized theme (NeonColors)
│       │           ├── dashboard/               # Main dashboard UI
│       │           ├── navigation/              # App navigation graph
│       │           ├── console/                 # Live console screen
│       │           ├── automod/                 # AutoMod configuration
│       │           ├── commands/                # Command builder
│       │           └── launch/                  # Bot deployment UI
│       └── res/                                 # Android resources
├── backend/                       # FastAPI backend (separate)
├── build.gradle.kts              # Root Gradle build file
├── settings.gradle.kts           # Gradle settings
└── gradle.properties             # Gradle configuration

```

## Architecture

### Frontend (Android)
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Architecture:** MVVM (Model-View-ViewModel)
- **Navigation:** Jetpack Navigation Compose
- **Theme:** Centralized NeonColors theme for consistent dark/neon aesthetic

### Backend (Python)
- **Framework:** FastAPI
- **Database:** Redis for state management
- **WebSocket:** Real-time log streaming
- **AI:** Google Gemini API for toxicity moderation

## Key Features

1. **Live Console:** Real-time bot log streaming via WebSocket
2. **AI AutoMod:** Gemini-powered toxicity filtering and spam protection
3. **Command Builder:** Visual interface for creating custom Discord commands
4. **Bot Deployment:** Deploy bots to cloud infrastructure
5. **Neon Theme:** Beautiful dark theme with neon accents throughout

## Development Notes

### Theme System
All UI components use the centralized `NeonColors` object from `ui/theme/NeonTheme.kt`. Do not create duplicate theme definitions.

### Code Style
- Follow Kotlin coding conventions
- Use Jetpack Compose best practices
- Keep composables small and focused
- Use preview annotations for UI development

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Building Release APK
```bash
./gradlew assembleRelease
```
The APK will be generated in `app/build/outputs/apk/release/`

## Backend Setup (Optional)

The Android app can work standalone, but for full functionality, you need to run the backend:

```bash
cd backend
python -m venv .venv
source .venv/bin/activate  # On Windows: .venv\Scripts\activate
pip install fastapi uvicorn redis pydantic

# Start Redis
docker run -d -p 6379:6379 redis:7.4-alpine

# Run backend
uvicorn backend_api:app --host 0.0.0.0 --port 8000 --reload
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

MIT License - see LICENSE file for details

## Support

For issues or questions, please open an issue on GitHub.
