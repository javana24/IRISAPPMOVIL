# AGENTS.md — IRIS App móvil

Instrucciones para agentes de código en el cliente Android de IRIS.

| | |
|---|---|
| **Producto** | IRIS — app móvil (chat con IA, simulador de pareja celosa, Dojo, emergencia) |
| **Stack** | Kotlin, Jetpack Compose, Hilt, Room, Retrofit, Firebase Auth/Firestore |
| **minSdk** | 26 · **targetSdk** 35 |
| **Flavors** | `dev`, `stage`, `prod` |
| **Backend** | [iris-node](https://github.com/javana24/iris-node) (Vercel) |

## Comandos frecuentes

```bash
./gradlew :app:assembleDevDebug
./gradlew :app:installDevDebug
./gradlew :app:testDevDebugUnitTest
```

## Reglas

- No hardcodear URLs del API en pantallas: usar `R.string.iris_api_base_url` (Gradle `resValue`).
- No commitear `local.properties`, `keystore.properties`, keystores ni `app/google-services.json`.
