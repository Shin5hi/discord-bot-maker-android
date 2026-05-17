# Discord 5iV AI Team

Bot nuevo desde cero para un servidor privado de Discord de Shin5hi / 5iV.

La idea es sencilla: un solo bot de Discord coordina 4 IAs que hablan entre ellas en un canal de texto usando nombres separados. Sirve para organizar el servidor, pensar tareas, preparar estructura para clientes y mantener una conversación de gestión interna sin convertirlo en una comunidad pública.

## Las 4 IAs

- Freyja: dirección creativa, marca 5iV, criterio artístico y tono.
- Sigrid: organización del servidor, procesos, canales, prioridades y orden.
- Eira: experiencia de clientes, claridad, trato humano y mensajes.
- Rune: parte técnica, automatizaciones, riesgos, integraciones y Tebex/web.

Cada una puede tener su propia API key de Google AI Studio:

```env
GEMINI_API_KEY_FREYJA=...
GEMINI_API_KEY_SIGRID=...
GEMINI_API_KEY_EIRA=...
GEMINI_API_KEY_RUNE=...
```

Si quieres probar más rápido, puedes usar una sola:

```env
GEMINI_API_KEY=...
```

## Instalación

```powershell
cd E:\Users\aph97\discord-bot-maker-android\discord-5iv-ai-team
npm install
Copy-Item .env.example .env
notepad .env
```

Rellena:

```env
DISCORD_TOKEN=...
TEAM_CHANNEL_ID=...
OWNER_USER_IDS=...
```

## Permisos del bot en Discord

Activa `Message Content Intent` en el Discord Developer Portal.

Permisos recomendados:

- View Channels
- Send Messages
- Read Message History
- Manage Channels
- Manage Webhooks
- Embed Links

No le des `Administrator` para empezar.

Para crear, mover, borrar canales/categorías y cambiar permisos necesita `Manage Channels`.

## Uso

```powershell
npm start
```

En el canal configurado:

```text
!ayuda
!equipo Estamos montando el servidor privado de 5iV conectado a Tebex. Organizad la primera estructura.
!admin Crea una categoria privada para clientes con un canal de texto de bienvenida, un canal de entregas y una sala de voz
!propuestas
!aprobar 1
!rechazar 1
!ronda
!estado
!pausa
!reanudar
!memoria Shin5hi prefiere que le hablen en femenino.
!memorias
```

## Administración del servidor

Las IAs pueden preparar cambios administrativos, pero no los ejecutan directamente. El flujo es:

1. Tú escribes `!admin <cambio que quieres>`.
2. Rune convierte eso en una propuesta revisable.
3. Tú decides con `!aprobar ID` o `!rechazar ID`.

Acciones soportadas ahora:

- Crear categorías.
- Crear canales de texto.
- Crear canales de voz.
- Mover canales a otra categoría.
- Borrar canales.
- Cambiar permisos por canal para `@everyone`, roles o usuarios concretos.

Ejemplos:

```text
!admin Crea una zona privada llamada clientes-5iv con un canal info, un canal entregas y una sala de voz para reuniones.
!admin Haz que el canal entregas solo lo pueda ver el rol Clientes 5iV.
!admin Mueve briefing-cliente dentro de la categoria clientes-5iv.
```

Para permisos, la IA puede usar permisos como `ViewChannel`, `SendMessages`, `ReadMessageHistory`, `Connect` o `Speak`.

## Sobre Vercel

Vercel no es la mejor opción para este bot porque un bot de Discord necesita quedarse conectado todo el tiempo. Vercel va mejor para una web, dashboard o panel de configuración. Para tener este bot 24/7 conviene más un PC encendido, VPS, Render, Railway o similar.

## Sobre Windsor.ai

Windsor.ai tendría sentido más adelante si quieres cruzar datos de marketing, tienda, ventas o campañas. Para esta primera versión no hace falta: el objetivo ahora es que el equipo de IAs converse y se organice dentro de Discord.
