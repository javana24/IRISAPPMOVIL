# IRIS — App Android

Cliente Android del proyecto IRIS (prevención de la violencia de género en parejas jóvenes). Kotlin, Jetpack Compose, Hilt, Room, Retrofit y Firebase (Auth + Firestore).

- **Chat con IRIS** (consejero) y **simulador de pareja celosa** con 3 niveles de dificultad, **banderas rojas** en cada mensaje de la pareja y **informe final** de la conversación.
- **Dojo de Defensa**: 4 módulos con bancos de preguntas (las mismas que la web).
- **Emergencia**: llamada al 016 / 112 y compartir ubicación.

Backend: [javana24/iris-node](https://github.com/javana24/iris-node) · Web: [javana24/Iris](https://github.com/javana24/Iris)

## Requisitos

- Android Studio reciente, JDK 17.
- `app/google-services.json` del proyecto Firebase `irisaiapp-e2263` (no está en el repo: descárgalo de la consola de Firebase → Configuración del proyecto → app Android `com.iris.app`).

## Compilar

```bash
./gradlew :app:assembleDevDebug      # APK de pruebas
./gradlew :app:testDevDebugUnitTest  # tests unitarios
```

Con varios flavors (`dev`, `stage`, `prod`) usa siempre la variante completa (`assembleDevDebug`, `assembleProdRelease`...).

GitHub Actions compila el APK en cada push (`.github/workflows/android.yml`) y lo deja como artefacto descargable en la pestaña *Actions*. Necesita el secreto de repositorio `GOOGLE_SERVICES_JSON` con el contenido de `app/google-services.json`.

## URL del backend

Gradle escribe `R.string.iris_api_base_url` según el flavor. Por defecto todos apuntan a `https://irisapp-api.vercel.app/`. Para cambiarla sin tocar código, define en `gradle.properties` o `local.properties`:

```properties
iris.api.baseUrl.dev=https://otra-url.vercel.app/
```

(la URL debe terminar en `/`).

## Release firmada

Copia `keystore.properties.example` a `keystore.properties` con los datos de tu keystore (no se sube al repo) y ejecuta `./gradlew :app:assembleProdRelease`.

## Dojo: preguntas

Las preguntas están en `data/local/dojo/Dojoquestionbank.kt`, generado a partir de la web con `scripts/gen-dojo-kt.cjs`. Si cambias las preguntas de la web, vuelve a generarlo.

## Nota

Proyecto educativo. No sustituye a los servicios de emergencia: en una situación real, 016 / 112.
