# Release Checklist

Use this checklist before shipping meaningful changes to the backend or Android app.

## Scope The Release

- confirm what changed
- confirm whether the release affects `backend`, `android`, `docs`, or `build`
- identify any user-visible behavior change
- identify any API contract change

## Backend Checks

- [ ] `backend/tests/test_api.py` passes
- [ ] bot register/start/stop flow still works
- [ ] AutoMod save/load still works
- [ ] WebSocket log stream still connects
- [ ] no secret or token handling behavior regressed

Suggested command:

```powershell
backend\.venv\Scripts\python.exe -m pytest backend\tests\test_api.py -q
```

## Android Checks

- [ ] Android unit tests pass
- [ ] first-run backend URL flow still works
- [ ] bot registration flow still works
- [ ] bot home reflects runtime state correctly
- [ ] live console still renders logs correctly
- [ ] AutoMod screen loads and saves correctly

Suggested command:

```powershell
.\gradlew.bat -p E:\Users\aph97\discord-bot-maker-android :app:testDebugUnitTest
```

## Build Checks

- [ ] debug APK still builds
- [ ] release signing plan is defined before store upload
- [ ] no required local config was silently broken
- [ ] CI build still passes `:app:assembleDebug`

Suggested command:

```powershell
.\gradlew.bat -p E:\Users\aph97\discord-bot-maker-android :app:assembleDebug
```

## Review Workflow

- [ ] PR description explains behavior change and risk
- [ ] `CodeRabbit` review requested for risky or cross-cutting changes
- [ ] `Test Android Apps` used or explicitly deferred for visible Android changes
- [ ] docs updated if workflow, API, or setup changed
- [ ] GitHub Actions CI is green for backend and Android unit tests
- [ ] GitHub Actions CI is green for the Android debug build

## Security Checks

- [ ] no real tokens in code, tests, docs, logs, or screenshots
- [ ] no secret files added to git
- [ ] masking behavior still protects token display
- [ ] key handling assumptions still match [SECURITY.md](/E:/Users/aph97/discord-bot-maker-android/SECURITY.md)

## Play Store Readiness

- [ ] final app icon matches the approved branding
- [ ] store screenshots use the final visual system
- [ ] privacy policy exists and matches the app behavior
- [ ] Google Play Data Safety answers are prepared
- [ ] release build is signed with a non-debug key
- [ ] backend/self-hosting requirements are explained clearly in the listing or onboarding
- [ ] internal testing track has been used before production release

## Release Notes

- summarize user-visible changes
- summarize developer workflow changes
- note follow-up work not included in the release
