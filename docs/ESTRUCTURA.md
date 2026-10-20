# PanalSuite Mobile — Qué hace cada archivo

> Guía rápida del proyecto. Explica, en palabras simples, para qué sirve cada archivo.
> El código está en inglés; este documento está en español.

---

## Las cuatro carpetas

El proyecto está ordenado en cuatro carpetas. Cada una tiene un trabajo distinto:

| Carpeta | Qué es | En pocas palabras |
|---|---|---|
| `model/` | Los datos | Las "recetas": definen cómo son un ticket, un usuario, un comentario. Solo dicen qué campos tiene cada cosa, no hacen nada. |
| `repository/` | De dónde salen los datos | La "bodega": guarda los datos en el teléfono y los entrega. Además tiene la capa que habla con la API de PanalSuite, que en este MVP responde dentro del propio teléfono. |
| `viewmodel/` | La memoria y las decisiones | La "cocina": guarda lo que el usuario está viendo, decide qué pasa cuando toca un botón y avisa si algo está mal escrito. |
| `ui/` | Lo que se ve | El "salón": dibuja las pantallas y los botones. Solo dibuja y avisa; nunca decide ni calcula. |

**La regla de orden:** cada carpeta conoce a la de abajo, nunca al revés.
`ui` → `viewmodel` → `repository` → `model`. Así nadie se enreda.

---

## 1. Carpeta `model/` — cómo son los datos

| Archivo | Para qué sirve |
|---|---|
| `Ticket.kt` | Describe cómo es un ticket: su número, título, categoría, prioridad, estado, sucursal, quién lo pidió, quién lo está viendo, y la lista de comentarios, evidencias y cambios. |
| `TicketStatus.kt` | Los estados posibles de un ticket: Abierto, En progreso, Pendiente, Resuelto, Cerrado y Cancelado. |
| `TicketPriority.kt` | Las cuatro prioridades: Baja, Media, Alta y Crítica. |
| `TicketFilter.kt` | Los filtros de la bandeja: por estado, por prioridad y por texto de búsqueda. |
| `User.kt` | Cómo es un usuario: su nombre, su correo y su rol. |
| `UserRole.kt` | Los cinco roles del sistema y qué puede hacer cada uno. Por ejemplo, solo el administrador puede administrar usuarios. |
| `AuthSession.kt` | Los datos de la sesión iniciada, para no tener que entrar cada vez que se abre la app. |
| `Comment.kt` | Cómo es un comentario: quién lo escribió, qué dice y cuándo. |
| `Evidence.kt` | Cómo es una evidencia: su descripción, dónde se tomó y cuándo. |
| `ChangeEntry.kt` | Una línea del historial: qué campo se cambió, de qué valor a qué valor, quién lo hizo y cuándo. |
| `DashboardStats.kt` | Los números del panel de indicadores: cuántos tickets hay abiertos, en curso, resueltos, la carga de cada equipo, etc. |
| `TicketCatalog.kt` | Las listas de opciones que se ofrecen en los formularios: los tipos de solicitud, las categorías y las sucursales. |
| `ThemeMode.kt` | Las tres maneras de ver la aplicación: siguiendo el modo del teléfono, siempre claro o siempre oscuro. |
| `UserPreferences.kt` | Lo que la aplicación recuerda del usuario entre una ejecución y otra: su nombre, su correo, el último nombre de acceso usado, la prioridad que prefiere y el modo de color. |

---

## 2. Carpeta `repository/` — de dónde salen los datos

| Archivo | Para qué sirve |
|---|---|
| `PanalApiService.kt` | **El punto de acceso a la API de PanalSuite.** Cada operación se toma su tiempo para simular que hay internet y responde en el formato de texto que usaría el backend real. Trae una lista de 10 tickets y 5 usuarios de ejemplo, todos inventados. |
| `JsonMappers.kt` | Traduce entre los datos de la app y el formato de texto que usa la API. Sirve para que el resto del código nunca tenga que ver ese formato. |
| `LocalStore.kt` | **Guarda en el teléfono los datos del trabajo**: la sesión, los tickets y los usuarios. Para que sigan ahí aunque cierres la app. |
| `UserPreferencesRepository.kt` | **Guarda en el teléfono las preferencias de la aplicación**: los datos de contacto, el último acceso, la prioridad preferida y el modo de color. Usa DataStore, la herramienta de Android para datos sueltos y pequeños. |
| `PanalSuiteRepository.kt` | **El intermediario.** Es el único que sabe dónde están las cosas: pide los datos, si ya están guardados los entrega al instante y si no los busca en la API. También guarda los cambios que hace el usuario. |

---

## 3. Carpeta `viewmodel/` — la memoria de cada pantalla

Cada pantalla tiene dos archivos: uno con **el estado** (qué se está mostrando) y otro con
**las decisiones** (qué pasa cuando el usuario toca algo).

### Pantalla de acceso

| Archivo | Para qué sirve |
|---|---|
| `LoginUiState.kt` | Lo que el usuario ha escrito en el formulario y si ya se están mostrando los errores. También revisa si el usuario y la contraseña están bien escritos. |
| `LoginViewModel.kt` | La lógica del acceso. Revisa el formulario, avisa si algo está mal y pide el acceso. Recuerda el último nombre de acceso usado, así que la próxima vez el campo ya viene escrito. |
| `SettingsViewModel.kt` | La lógica de la pantalla de Ajustes. Guarda el modo de color, la prioridad por defecto y los datos de contacto, y avisa cuando se borran los datos guardados. |

### Bandeja de trabajo

| Archivo | Para qué sirve |
|---|---|
| `InboxUiState.kt` | La lista de tickets, el filtro aplicado y el usuario conectado. Calcula qué tickets se ven según el rol de cada uno y según lo que se buscó. |
| `InboxViewModel.kt` | Carga la bandeja, aplica los filtros y avisa cuando se pide actualizar. |

### Detalle de un ticket

| Archivo | Para qué sirve |
|---|---|
| `TicketDetailUiState.kt` | Los datos del ticket abierto, más lo que el usuario está escribiendo en el comentario y en la evidencia. Revisa si lo escrito está bien. |
| `TicketDetailViewModel.kt` | Abre el ticket, guarda el comentario, cambia el estado, registra la evidencia y avisa si algo falló. |

### Nueva solicitud

| Archivo | Para qué sirve |
|---|---|
| `NewTicketUiState.kt` | Lo que el usuario escribió en el formulario de solicitud, y la revisión de que el título y el detalle tengan lo suficiente. |
| `NewTicketViewModel.kt` | La lógica del formulario. Parte con la prioridad que el usuario eligió en Ajustes y, si el usuario la cambia allá, el formulario se entera solo. |

### Indicadores

| Archivo | Para qué sirve |
|---|---|
| `DashboardUiState.kt` | Los números del panel y el orden en que se muestran los equipos. |
| `DashboardViewModel.kt` | Recalcula los indicadores cada vez que se entra a la pantalla, para que nunca estén desactualizados. |

### Administración de usuarios

| Archivo | Para qué sirve |
|---|---|
| `AdminUiState.kt` | La lista de usuarios y lo que se está escribiendo en el formulario de usuario nuevo, con sus revisiones. |
| `AdminViewModel.kt` | Carga los usuarios, crea uno nuevo y los activa o desactiva. |

### Perfil

| Archivo | Para qué sirve |
|---|---|
| `ProfileUiState.kt` | Los datos de la sesión y un resumen de cuánto hay guardado en el teléfono. |
| `ProfileViewModel.kt` | Lee la sesión y cierra el acceso. |

---

## 4. Carpeta `ui/` — lo que se ve

### Las pantallas

| Archivo | Para qué sirve |
|---|---|
| `LoginScreen.kt` | La pantalla de acceso, con el formulario, la contraseña que se puede esconder y la lista de cuentas de prueba. |
| `InboxScreen.kt` | La bandeja: la búsqueda, los filtros, el resumen de arriba y la lista de tickets. |
| `TicketDetailScreen.kt` | El detalle de un ticket: sus datos, el cambio de estado, los comentarios, las evidencias y el historial. |
| `NewTicketScreen.kt` | El formulario para registrar una solicitud nueva. |
| `DashboardScreen.kt` | El panel de indicadores con las cifras y la carga de cada equipo. |
| `AdminScreen.kt` | La administración de los usuarios de prueba. |
| `ProfileScreen.kt` | El perfil del usuario, con el resumen de lo guardado en el teléfono, los datos de contacto que recuerda la app y el botón de cerrar sesión. |
| `SettingsScreen.kt` | La pantalla de Ajustes: el modo de color de la aplicación, la prioridad por defecto de las solicitudes nuevas, los datos de contacto y el botón para borrar lo guardado. |

### `ui/components/` — las piezas que se repiten

| Archivo | Para qué sirve |
|---|---|
| `AppChips.kt` | Las etiquetas de color: prioridad, estado, rol, y la que se usa para filtrar. |
| `DashboardComponents.kt` | Las tarjetas de números y las barras de carga por equipo. |
| `Feedback.kt` | Los avisos de todas las pantallas: la rueda de carga, el mensaje de error, la pantalla vacía y los títulos. |
| `FormFields.kt` | Las listas desplegables de los formularios. |
| `TicketCard.kt` | La tarjeta de un ticket dentro de la bandeja. |
| `Visuals.kt` | Los colores de cada prioridad y estado, y los textos de apoyo como "hace 3 h". |

### `ui/navigation/` — el mapa de la app

| Archivo | Para qué sirve |
|---|---|
| `Routes.kt` | Los nombres de cada pantalla, escritos en un solo lugar para no repetirlos y no equivocarse. |
| `AppNavHost.kt` | El mapa de la app: dice qué pantallas existen, cómo se pasa de una a otra y qué botones aparecen abajo según el rol. |

### `ui/theme/` — el aspecto

| Archivo | Para qué sirve |
|---|---|
| `Color.kt` | Los colores de la app, tanto de día como de noche. |
| `Theme.kt` | Junta esos colores y decide cuál set se usa. El color no se decide aquí: viene del modo guardado en Ajustes, así que si el usuario lo cambia, toda la app cambia al instante. |
| `Type.kt` | Los tamaños y grosores de las letras. |

---

## 5. Los dos archivos sueltos

| Archivo | Para qué sirve |
|---|---|
| `MainActivity.kt` | **La puerta de entrada de la app.** Es el único archivo que arranca todo. Crea las "memorias" de las pantallas (para que no se pierda nada al girar el teléfono), decide si la app abre en el acceso o directo en la bandeja, aplica el tema que el usuario eligió en Ajustes y muestra el mapa de navegación. |
| `PanalSuiteApplication.kt` | Prepara el almacenamiento local del teléfono antes de que se abra cualquier pantalla. Es el que se encarga de que los datos guardados estén disponibles desde el principio. |

---

## 6. Un ejemplo para entender cómo se conectan

**El usuario escribe un comentario en un ticket:**

1. La pantalla (`ui`) ve que escribió y **avisa** hacia arriba.
2. El ViewModel (`viewmodel`) **revisa** que no esté vacío. Si está mal, avisa el error y no sigue. Si está bien, lo envía.
3. El repositorio (`repository`) pide el dato a la API y **guarda** el resultado en el teléfono.
4. La API se **toma su tiempo** (como si fuera internet) y responde.
5. El ViewModel **actualiza** el estado y avisa que todo salió bien.
6. La pantalla **se vuelve a dibujar** sola con el comentario nuevo.

Y si el usuario gira el teléfono a la mitad del proceso, **no se pierde nada**: las memorias
las creó la `MainActivity` y siguen vivas.

**El usuario cambia el modo de color en Ajustes:**

1. La pantalla de Ajustes ve que lo cambió y **avisa** hacia arriba.
2. El ViewModel **guarda** el modo elegido en el archivo del teléfono.
3. Ese archivo **avisa** que cambió, y la `MainActivity` se entera.
4. La `MainActivity` **vuelve a dibujar** toda la app con el tema nuevo.
5. Todas las pantallas **cambian de color** a la vez, sin que nadie se lo ordene.

---

## 7. Lo que la aplicación recuerda

| Dato | Dónde se guarda | Para qué |
|---|---|---|
| Sesión iniciada | `LocalStore` | Entrar directo a la bandeja sin escribir la contraseña otra vez |
| Tickets y usuarios | `LocalStore` | Que los cambios y el historial sobrevivan al cierre de la app |
| Datos de contacto | `UserPreferencesRepository` (DataStore) | Tenerlos a mano sin volver a escribirlos |
| Último nombre de acceso | `UserPreferencesRepository` (DataStore) | Que el formulario de acceso ya venga escrito |
| Prioridad preferida | `UserPreferencesRepository` (DataStore) | Que el formulario de solicitud parta con esa prioridad |
| Modo de color | `UserPreferencesRepository` (DataStore) | Que la app se abra siempre con el tema elegido |

Se usan dos herramientas a propósito: `LocalStore` para las listas de tickets y usuarios,
que tienen datos relacionados entre sí, y DataStore para las preferencias sueltas, que son
valores simples.

---

## 8. Datos de prueba

- **5 usuarios**, uno por rol: `solicitante`, `resolutor`, `supervisor`, `gerencia`, `admin`.
- **Contraseña para todos:** `1234`.
- **10 tickets** de ejemplo que cubren todos los estados y prioridades.
- En la pantalla de acceso se tocan las cuentas de prueba y el formulario se completa solo.
- La lista completa de usuarios está en la sección **Usuarios** de la barra inferior, que
  solo ve el rol `admin`.
- Los ajustes están en el ícono de engranaje de la pantalla **Perfil**.
- Todos los datos son inventados: no hay credenciales ni información real.
