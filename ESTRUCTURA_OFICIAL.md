# 📐 ESTRUCTURA OFICIAL DEL PROYECTO - Grid Discord Bot Maker

**Basado en**: `design/OFFICIAL_MOCKUP.md` + `design/branding.md` + `design/visual-tree.md`

---

## 🎯 VISIÓN GENERAL

Este es un proyecto Android **100% completamente diseñado** pero **50% implementado**.

**Mockup Oficial**: https://customer-assets.emergentagent.com/wingman/85fd1fa5-923c-402f-b2c2-f78866b3e4b5/attachments/5c703d3ae1ea419b82c37f9ef848a102_photo.jpg

---

## 🏗️ ARQUITECTURA OFICIAL REQUERIDA

### **FLUJO DE NAVEGACIÓN CORRECTO**

```
SplashScreen (2-3 seg)
    ↓
OriginLoadingScreen (Grid Origin animated logo)
    ↓
MainDashboardScreen (Hub central - "Hola, Shin5hi")
    ├─→ Proyectos (Counter)
    ├─→ Tareas (Counter)
    ├─→ Herramientas (Counter)
    ├─→ Notas (Counter)
    └─→ Accesos Rápidos:
        ├─ CommandBuilderScreen
        ├─ AutoModScreen
        ├─ BotCreationScreen
        ├─ LiveConsoleScreen
        └─ MusicPlayerScreen
    
    + ToolLibraryScreen (Right Sidebar / Split view)
        ├─ ORGANIZE (AutoMod, Logs, Security)
        ├─ CREATE (Welcome, Polls, Giveaways)
        ├─ AUTOMATE (Levels, Ranks, Shop)
        └─ DEVELOP (Custom Commands, Tasks)

DoubtAssistantScreen (Asistente Orión - Modal/Push)
StatsDashboardScreen (Estadísticas - New)
```

---

## 🎨 IDENTITY SYSTEM (MANDATORY)

### **Colors - Discord Official HiFi**

```kotlin
object AppColors {
    // Backgrounds (Discord Dark Theme)
    Background       = #313338  // Main
    Surface          = #2B2D31  // Cards
    SurfaceVariant   = #232428  // Elevated
    InputBackground  = #1E1F22  // Console/Inputs
    
    // PRIMARY ACCENT
    Primary          = #5865F2  // Blurple (Grid Brand Color)
    PrimaryLight     = #7984F5
    PrimaryDim       = #4752C4
    
    // Semantic
    Success          = #23A559
    Error            = #F23F43
    Warning          = #FAA81A
    
    // Text Hierarchy
    TextPrimary      = #FFFFFF
    TextSecondary    = #B5BAC1
    TextMuted        = #80848E
}
```

### **Typography**
- **Font**: System SansSerif ONLY (Inter/Roboto)
- **NO Monospace** anywhere
- **Display**: Bold 36sp
- **Heading**: SemiBold 22sp
- **Body**: Normal 14sp

---

## 📱 SCREENS REQUERIDAS (CANONICAL PATHS)

| Screen | Path | Status | Notes |
|--------|------|--------|-------|
| **SplashScreen** | `ui/splash/SplashScreen.kt` | ❌ FALTA | 2-3 seg entry point |
| **OriginLoadingScreen** | `ui/splash/OriginLoadingScreen.kt` | ⚠️ Existe en `ui/` | Grid Origin animated logo + slogan "Crea, Organiza, Avanza." |
| **MainDashboardScreen** | `ui/dashboard/MainDashboardScreen.kt` | ❌ FALTA | Hub central con contadores |
| **ToolLibraryScreen** | `ui/library/ToolLibraryScreen.kt` | ⚠️ Existe en `ui/` | Tree view con hierarchy lines |
| **CommandBuilderScreen** | `app/src/.../ui/commands/CommandBuilderScreen.kt` | ✅ Existe | Funcional |
| **AutoModScreen** | `app/src/.../ui/automod/AutoModScreen.kt` | ✅ Existe | Funcional |
| **BotCreationScreen** | `app/src/.../ui/launch/BotCreationScreen.kt` | ✅ Existe | Funcional |
| **LiveConsoleScreen** | `app/src/.../ui/console/LiveConsoleScreen.kt` | ✅ Existe | Funcional |
| **MusicPlayerScreen** | Existe en root | ⚠️ Parcial | Necesita integración |
| **DoubtAssistantScreen** | `ui/DoubtAssistantScreen.kt` | ✅ Existe | Asistente Orión |
| **StatsDashboardScreen** | ❌ FALTA | ❌ NO EXISTE | Stats/Analytics module |
| **SettingsScreen** | ❌ FALTA | ❌ NO EXISTE | Settings placeholder |

---

## 🎭 LOGO & BRANDING (CRITICAL)

### **Grid Origin Logo**
- **Shape**: Rounded square (20dp radius)
- **Fill**: Blurple `#5865F2`
- **Glyph**: Bold "G" in white, 48sp, SansSerif
- **Size**: 96×96dp logical

### **Loading Animation Ring**
- **Diameter**: 140dp (around 96dp logo)
- **Stroke**: 3dp, round cap, Blurple with opacity sweep
- **Speed**: 1400ms per revolution (linear)
- **Glow**: Alpha oscillates 25%-60% at half rotation period

### **Slogan**
```
"Crea, Organiza, Avanza."
```
- **Font**: 14sp, SansSerif, Normal
- **Color**: TextSecondary `#B5BAC1`

---

## 📂 ESTRUCTURA DE DIRECTORIOS (CORRECTA)

```
discord-bot-maker-android/
├── app/src/main/
│   ├── kotlin/com/discordbotmaker/android/
│   │   ├── MainActivity.kt
│   │   ├── ui/
│   │   │   ├── theme/
│   │   │   │   ├── AppTheme.kt ✅ (existe)
│   │   │   │   ├── AppColors.kt (opcional - está en AppTheme)
│   │   │   │   └── Typography.kt (opcional)
│   │   │   │
│   │   │   ├── navigation/
│   │   │   │   └── AppNavGraph.kt ✅ (ESTA es la oficial)
│   │   │   │
│   │   │   ├── splash/
│   │   │   │   ├── SplashScreen.kt ❌
│   │   │   │   └── OriginLoadingScreen.kt ✅
│   │   │   │
│   │   │   ├── dashboard/
│   │   │   │   └── MainDashboardScreen.kt ❌
│   │   │   │
│   │   │   ├── library/
│   │   │   │   └── ToolLibraryScreen.kt ⚠️
│   │   │   │
│   │   │   ├── commands/
│   │   │   │   └── CommandBuilderScreen.kt ✅
│   │   │   │
│   │   │   ├── automod/
│   │   │   │   └── AutoModScreen.kt ✅
│   │   │   │
│   │   │   ├── launch/
│   │   │   │   └── BotCreationScreen.kt ✅
│   │   │   │
│   │   │   ├── console/
│   │   │   │   ├── LiveConsoleScreen.kt ✅
│   │   │   │   └── LiveConsoleViewModel.kt ✅
│   │   │   │
│   │   │   ├── doubt/ (NEW)
│   │   │   │   └── DoubtAssistantScreen.kt ✅
│   │   │   │
│   │   │   ├── stats/ (NEW)
│   │   │   │   └── StatsDashboardScreen.kt ❌
│   │   │   │
│   │   │   ├── music/
│   │   │   │   └── MusicPlayerScreen.kt ⚠️
│   │   │   │
│   │   │   └── components/
│   │   │       ├── OrionBubble.kt
│   │   │       ├── GridBottomNavBar.kt
│   │   │       └── Common composables
│   │   │
│   │   └── models/
│   │       ├── BotCommand.kt
│   │       ├── AutoModConfig.kt
│   │       ├── BotStats.kt (NEW)
│   │       └── etc.
│   │
│   └── res/
│       ├── values/
│       │   ├── strings.xml ❌
│       │   ├── colors.xml ⚠️ (minimal)
│       │   └── themes.xml ❌
│       └── drawable/
│           ├── ic_launcher_background.xml ✅
│           └── ic_launcher_foreground.xml ✅
│
├── backend/
│   └── backend_api.py ✅ (FastAPI completo)
│
├── design/
│   ├── OFFICIAL_MOCKUP.md ✅ (Imagen+Directivas)
│   ├── OFFICIAL_BRANDING.md ✅
│   ├── branding.md ✅ (Logo spec)
│   ├── visual-tree.md ✅ (Tree structure)
│   └── perspectives.md ✅ (HiFi refs)
│
├── build.gradle.kts ✅
├── app/build.gradle.kts ✅
└── settings.gradle.kts ✅
```

---

## ✅ STATUS POR COMPONENTE

| Área | Completitud | Prioridad | Bloqueador |
|------|-------------|-----------|-----------|
| **Backend API** | 100% ✅ | — | NO |
| **Build System** | 100% ✅ | — | NO |
| **Theme (AppTheme.kt)** | 100% ✅ | — | NO |
| **Navigation** | 50% ⚠️ | HIGH | **SÍ - Duplicada** |
| **Logo/Branding** | 80% ⚠️ | MEDIUM | Espera navegación |
| **Dashboard** | 10% ⚠️ | HIGH | **SÍ - No existe** |
| **Tree View** | 30% ⚠️ | HIGH | **SÍ - Incompleta** |
| **Screens (5)** | 60% ⚠️ | MEDIUM | Espera Nav |
| **Resources** | 20% ❌ | MEDIUM | **SÍ - Faltan XML** |
| **Tests** | 40% ⚠️ | LOW | — |

---

## 🔴 PROBLEMAS BLOQUEADORES

### **1. NAVEGACIÓN DUPLICADA** (CRÍTICO)

**Situación:**
- `ui/AppNavigation.kt` (OLD) - con SplashScreen, OriginLoadingScreen
- `app/src/.../ui/navigation/AppNavigation.kt` (NEW) - simplificada

**Solución:**
- ✅ Mantener SOLO: `app/src/main/kotlin/com/discordbotmaker/android/ui/navigation/AppNavigation.kt`
- ❌ Eliminar: `ui/AppNavigation.kt`
- Actualizar `MainActivity.kt` para importar de la ruta correcta

---

### **2. SCREENS FALTANTES** (CRÍTICO)

```kotlin
❌ SplashScreen.kt
❌ MainDashboardScreen.kt  // El hub central más importante
❌ StatsDashboardScreen.kt
❌ GridBottomNavBar.kt (componente)
```

**Flujo correcto:**
```
MainActivity.kt
  → AppNavGraph() 
    → startDestination = SplashScreen
      → (2-3s delay)
      → OriginLoadingScreen (animated logo)
        → (loading animation completes)
        → MainDashboardScreen (THE HUB)
```

---

### **3. RESOURCES FALTANTES** (ALTO)

```xml
❌ app/src/main/res/values/strings.xml
❌ app/src/main/res/values/themes.xml
❌ app/src/main/res/values/colors.xml (EXPANDIDO)
```

**Necesarios para:**
- Strings externalizados
- Material 3 theme configuration
- Color references desde XML

---

## 🚀 PLAN DE ACCIÓN ORDENADO

### **FASE 1: Estructuración (1 hora)**
1. ✅ Consolidar navegación (eliminar `ui/AppNavigation.kt`)
2. ✅ Crear structure de directorios correcta
3. ✅ Crear archivos Android Resources

### **FASE 2: Screens Base (2-3 horas)**
1. `SplashScreen.kt` - Entry point simple
2. `MainDashboardScreen.kt` - Hub central (CON contadores)
3. `StatsDashboardScreen.kt` - Placeholder
4. `GridBottomNavBar.kt` - Componente nav

### **FASE 3: Integración (1 hora)**
1. Conectar `MainActivity.kt` → `AppNavGraph()`
2. Verifica `./gradlew :app:assembleDebug`
3. Test en emulador

### **FASE 4: Refinamiento (2-3 horas)**
1. Detalles visuales según mockup
2. Animaciones de transición
3. Orión bubble flotante
4. Pull-to-refresh, etc.

---

## 📊 EXPECTED RESULT

**Cuando termines FASE 2:**
- ✅ App compila sin errores
- ✅ Splash 2-3 seg
- ✅ Logo animado Grid Origin
- ✅ Dashboard central funcional
- ✅ Navegación bottom bar
- ✅ Accesos a 5 screens principales

**Cuando termines FASE 3:**
- ✅ **App funciona en emulador** 📱
- ✅ Puedes navegar todas las screens
- ✅ Backend conecta para logs/deploy

**Cuando termines FASE 4:**
- ✅ **App lista para Play Store** 🎉

---

## 📖 REFERENCIAS OBLIGATORIAS

1. `design/OFFICIAL_MOCKUP.md` - Mockup maestro (VERDAD ABSOLUTA)
2. `design/branding.md` - Colores + Logo spec exacto
3. `ui/AppTheme.kt` - Colors ya están aquí ✅
4. `COORDINATION.md` - Convenciones de desarrollo

---

**Estado**: Estructuralmente 100% definido. Implementación 50% completa.
**Próximo paso**: Consolidar navegación y crear MainDashboardScreen.

