# IRIS App móvil (Android)

Cliente Android del proyecto IRIS (Compose, Hilt, Room, Retrofit).

## Requisitos (S6 / SCRUM-38)

- **Android Studio** reciente (AGP 8.5+ del proyecto).
- **JDK 11** (configuración del módulo `app`).
- **minSdk 26**, **targetSdk 35**.
- Cable **USB** y depuración USB en el dispositivo, o un emulador compatible.
- Red hasta el backend que corresponda al **flavor** compilado.

## Entornos y URL del backend (S3 / SCRUM-35)

Las URLs **no van hardcodeadas en Kotlin de pantallas**: Gradle escribe `R.string.iris_api_base_url` (y `R.string.iris_environment`) con **`resValue`** según el **product flavor** `dev`, `stage` o `prod`. `NetworkModule` lee la URL con `context.getString(R.string.iris_api_base_url)`.

| Flavor  | Propiedad opcional en Gradle    | Valor por defecto si no definís nada   |
|---------|----------------------------------|----------------------------------------|
| `dev`   | `iris.api.baseUrl.dev`          | `http://10.0.2.2:3001/` (emulador → PC) |
| `stage` | `iris.api.baseUrl.stage`        | `http://10.0.2.2:3001/`                |
| `prod`  | `iris.api.baseUrl.prod`         | `http://10.0.2.2:3001/`                |

Podéis definir las propiedades en **`gradle.properties`** (o en `local.properties` con la misma clave para no commitear) o con `-Piris.api.baseUrl.dev=...` en la línea de comandos.

### Cómo apuntar a la IP LAN (comentario corto para Jira / compañeros)

1. Backend Node escuchando en `0.0.0.0` y puerto correcto (ej. `3001`).
2. IP del PC en la Wi‑Fi/LAN (`ipconfig` en Windows → IPv4).
3. En `gradle.properties`, por ejemplo:  
   `iris.api.baseUrl.dev=http://192.168.1.50:3001/`  
   (la URL debe **terminar en /**).
4. Compilar, por ejemplo:  
   `gradlew.bat :app:assembleDevDebug` (Windows) o `./gradlew :app:assembleDevDebug`.

**Emulador:** `10.0.2.2` es el host. **Móvil físico:** usad la IP LAN del PC, no `10.0.2.2`.

### HTTP y cleartext

Para pruebas **HTTP** en LAN el manifest usa `usesCleartextTraffic` y `network_security_config`. Para HTTPS de producción, coordinad la política de red con integración / Antonio.

## Instalación en dispositivo de prueba

1. Abrir el proyecto y sincronizar Gradle.
2. **Build Variants** → `dev` / `stage` / `prod` + `debug` o `release`.
3. Conectar teléfono (opciones de desarrollador + depuración USB) o AVD.
4. Ejecutar Run o `gradlew.bat :app:installDevDebug`.

Con varios flavors, la tarea genérica `assembleDebug` no basta: usad **`assembleDevDebug`**, `assembleStageRelease`, etc.

## Emergencia y ubicación (S4)

No se comparten coordenadas falsas. Con permiso se usa la última ubicación conocida del sistema; sin permiso o sin posición, el texto compartido es claro y remite a **016 / 112**. Las acciones de notificación que comparten ubicación reutilizan la misma lógica (`EmergencyLocationShare`).

## Troubleshooting

| Síntoma | Qué mirar |
|--------|------------|
| **No compila** / `Unable to locate a Java Runtime` | En terminal: `./gradlew :app:assembleDevDebug` (el script usa el JDK de Android Studio en Mac si no hay `JAVA_HOME`). En Android Studio: **Settings → Build → Gradle → Gradle JDK** = *Embedded JDK* o *jbr-17*. Variante: `devDebug`, no solo `debug` genérico. |
| **Móvil físico (Xiaomi, etc.)** — chat no responde | 1) **Inicia sesión** (Firebase): Chat/Dojo lo exigen. 2) Móvil y PC en la **misma Wi‑Fi**. 3) En el navegador del móvil abre `http://TU_IP_PC:3001/api/health` (la IP de `iris.api.baseUrl.dev` en `gradle.properties`, no `10.0.2.2`). 4) Backend Node en `0.0.0.0:3001`. 5) MIUI: permitir instalación USB y datos en segundo plano para IRIS. Instalar: `./gradlew :app:installDevDebug`. |
| Timeout / errores de API | URL del flavor, backend encendido, IP/puerto, firewall. |
| Solo funciona en emulador | Sustituir `10.0.2.2` por la **IP LAN** en `gradle.properties`. |
| Cleartext / HTTP bloqueado | Backend en HTTP vs HTTPS y `network_security_config`. |

Opcional en `local.properties` (no commitear) si Gradle no encuentra Java:

```properties
org.gradle.java.home=/Applications/Android Studio.app/Contents/jbr/Contents/Home
```

## Pantallas con estados carga / vacío / error (S5) — para comentario Jira + capturas

- **Home** (`Homescreen.kt`): carga, sin perfil activo.
- **Chat** (`Chatscreen.kt`): carga de historial, sin mensajes, error visible, “IRIS escribe…”, banner de seguridad del API (`safetyAlert` / `safetyMessage` de `POST /api/chat`, SCRUM-71).

### Verificación SCRUM-71 (sin inventar resultados E2E)

**Automático (local):** `./gradlew :app:testDevDebugUnitTest --tests com.example.irisappmovil.data.api.ChatSafetyContractTest`

**Manual (requiere backend real en la URL del flavor `dev`):**

1. `GET /api/health` responde OK.
2. Enviar mensaje en chat → si el JSON trae `safetyAlert` y/o `safetyMessage`, aparece el banner encima del hilo; la burbuja muestra solo `response`.
3. `safetyAlert: true` sin `safetyMessage` → texto fallback de `chat_safety_banner_alert_only`.
4. Cerrar banner (X) → desaparece; mensajes siguen.
5. Cambiar IRIS ↔ Partner → banner se oculta.

Si el backend no está levantado, no marcar la issue como probada en dispositivo.
- **Dojo** (`Dojoscreen.kt`): carga inicial, aviso si falla la sync remota, sin módulos.

*(Adjuntad capturas en Jira cuando probéis en dispositivo físico.)*

## Tests unitarios (SCRUM-78)

```bash
./gradlew :app:testDevDebugUnitTest
```

Clases principales:

- `ChatSafetyContractTest` — contrato chat / `safetyAlert` / modos API (SCRUM-71).
- `AuthInputValidationTest` — reglas mínimas de contraseña en registro (SCRUM-78).

## Autenticación (piloto / SCRUM-76)

- **Firebase** para login y registro; **invitado** puede usar Home sin sesión.
- Chat y Dojo exigen sesión Firebase (no login local en Room).
- Detalle: [docs/auth-mvp-decision.md](docs/auth-mvp-decision.md).

## Épicas Jira

- **SCRUM-35** — Móvil (S3–S5).
- **SCRUM-38** — Documentación piloto (S6).
