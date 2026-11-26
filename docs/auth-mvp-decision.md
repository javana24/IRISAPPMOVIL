# Decisión auth MVP — IRIS App móvil (SCRUM-76)

## Contexto

El piloto necesita identidad fiable sin duplicar credenciales en Room ni en el API Node de chat.

## Decisión

| Ámbito | Elección |
|--------|----------|
| **Sesión de producto** | **Firebase Authentication** (email/contraseña, Google). |
| **Exploración sin cuenta** | **Invitado**: splash → Home sin obligar login. |
| **Chat y Dojo** | Requieren sesión Firebase (`isSignedIn`); overlay con enlace a login/registro. |
| **API Node (`POST /api/chat`)** | Sin credenciales de usuario; solo URL del flavor. |
| **Room `users`** | Caché de perfil tras login Firebase; **no** almacena contraseñas. |
| **Login local Room** | **Eliminado** (`loginLocal` / `registerLocal`). |

## Implicaciones

- `UserRepository.isSignedIn()` refleja `FirebaseAuth.currentUser != null`.
- `getCurrentUser()` (Room) sigue sirviendo para XP, nombre y sync Firestore; no define si Chat/Dojo están desbloqueados.
- Perfil sin Firebase: solo nombre/correo local opcional (`createOrUpdateLocalUser`); no desbloquea Chat/Dojo.
- Backend Node no valida JWT en esta fase; la puerta es solo en cliente.

## Fuera de alcance (post-piloto)

- Forzar login en splash.
- Auth en cabeceras del API Node.
- Migración de usuarios solo-Room legacy.

## Referencias

- `UserRepository.kt`, `ChatViewModel.kt`, `DojoViewModel.kt`
- Épica móvil: **SCRUM-35**
