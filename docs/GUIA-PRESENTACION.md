# PanalSuite Mobile — Guía de presentación

> Documento de apoyo para la revisión del proyecto.
> Explica qué hace el código, por qué está hecho así y qué decir cuando pregunten.

---

## 1. Qué es PanalSuite Mobile

Es un **MVP académico de una aplicación Android** para la organización ficticia *Panalbit*.
PanalSuite es su plataforma de gestión de tickets (solicitudes de atención a clientes, fallas
de infraestructura, temas de facturación o de usuarios).

El caso que entregó el profesor pide un móvil que complemente la plataforma web y que permita
operar **desde el teléfono**: autenticación, consulta de bandejas y solicitudes, revisión de
estados, ejecución de acciones autorizadas, registro de comentarios, incorporación de evidencias
y recepción de notificaciones. Con conectividad intermitente, persistencia local y **datos
100 % ficticios**, sin credenciales ni acceso a sistemas productivos.

**Lo que hace la app, en concreto:**

| | |
|---|---|
| **Roles** | 5: solicitante, resolutor, supervisor, gerencia y administrador. Cada uno ve y puede hacer cosas distintas. |
| **Pantallas** | 8: acceso, bandeja, detalle del ticket, nueva solicitud, indicadores, administración de usuarios, perfil y ajustes. |
| **Datos que maneja** | Tickets con estado, prioridad, comentarios, evidencias y trazabilidad de cambios. |
| **Dónde viven los datos** | En el teléfono. La app funciona completa sin internet. |
| **Lenguaje** | Kotlin, con Jetpack Compose y Material 3. |
| **Tamaño del código** | 55 archivos Kotlin, repartidos en 4 capas. |

---

## 2. Cómo está construido

El proyecto sigue el patrón **MVVM** (Modelo - Vista - Modelo de Vista), que es el mismo de la
guía de Café Andino, con una capa de datos agregada para el enunciado del caso.

```
   MainActivity.kt          ← punto de entrada: crea las memorias y el mapa de navegación
   PanalSuiteApplication.kt ← prepara el almacenamiento local al arrancar

        ┌──────────────────┐
        │        ui/       │  dibuja y avisa. Nunca decide, nunca calcula.
        └────────┬─────────┘
                 │   UiState (StateFlow)      ↑   onAlgoChange
        ┌────────┴─────────┐
        │    viewmodel/    │  guarda el estado, decide, valida
        └────────┬─────────┘
                 │   suspend fun
        ┌────────┴─────────┐
        │    repository/   │  habla con la API y con el almacenamiento local
        └────────┬─────────┘
                 │
        ┌────────┴─────────┐
        │      model/      │  define la forma de los datos. Sin lógica.
        └──────────────────┘
```

**La regla que lo mantiene ordenado:** las dependencias apuntan en **una sola dirección**.
`ui` conoce a `viewmodel`, `viewmodel` conoce a `repository`, `repository` conoce a `model`.
Nunca al revés. Si un archivo de `model/` importara algo de `ui/`, la arquitectura estaría
dada vuelta.

**Cómo se reparten los 55 archivos:**

| Carpeta | Archivos | Qué hay adentro |
|---|---|---|
| `model/` | 14 | Ticket, estados, prioridades, roles, usuarios, comentarios, evidencias, trazabilidad, filtros, indicadores, catálogo de formularios, preferencias y modo de color. |
| `repository/` | 5 | La capa de acceso a la API, el tradutor a JSON, los dos almacenes del teléfono y el repositorio que los coordina. |
| `viewmodel/` | 15 | 7 pares de estado + ViewModel, y el ViewModel de ajustes. |
| `ui/` | 19 | 8 pantallas, 6 archivos de componentes reutilizables, 2 de navegación y 3 del tema visual. |
| raíz | 2 | `MainActivity` y `PanalSuiteApplication`. |

---

## 3. El recorrido de un dato

Esto es lo que hay que explicar si preguntan "¿cómo funciona la app?". Ejemplo: el usuario
escribe un comentario en un ticket.

```
1.  TicketDetailScreen  (ui)
        el usuario escribe → avisa hacia arriba con onCommentChange("...")

2.  TicketDetailViewModel  (viewmodel)
        sendComment()
        revisa: si el comentario está vacío, activa showCommentErrors y no sigue
        viewModelScope.launch { }   → el trabajo asíncrono va en una corrutina

3.  PanalSuiteRepository  (repository)
        addComment(id, autor, texto)
        llama a la API y después guarda el resultado en el teléfono

4.  PanalApiService  (repository)
        delay(450)   → simula la latencia de una llamada real
        crea el comentario, lo agrega al ticket y devuelve el JSON

5.  De vuelta en el ViewModel
        el StateFlow notifica el cambio

6.  TicketDetailScreen  (ui)
        collectAsState() recibe el estado nuevo
        Compose vuelve a dibujar solo la parte que cambió
```

**Y en el sentido contrario**, cuando el usuario gira el teléfono: los ViewModel los crea la
`MainActivity` con `by viewModels()`, así que **no se pierde ni un solo carácter escrito**.

---

## 4. Las 8 pantallas

| Pantalla | Qué hace | Qué hay que mirar |
|---|---|---|
| **Acceso** | Login con validación en vivo. | Las **cuentas de prueba** que se tocan para entrar sin escribir nada. El campo de contraseña con el botón de esconder. |
| **Bandeja** | Lista de tickets con búsqueda, filtros por estado y prioridad, y resumen arriba. | Los **chips** de filtro (tocar el mismo estado lo desactiva) y el **estado vacío**. |
| **Detalle** | Todo el ticket: datos, cambio de estado, comentarios, evidencias y trazabilidad. | El **diálogo de confirmación** del cambio de estado y la **trazabilidad** (qué cambió, quién y cuándo). |
| **Nueva solicitud** | Formulario validado. | Los **errores que aparecen bajo cada campo** solo después del primer intento. |
| **Indicadores** | Dashboard con 4 cifras y la carga por equipo. | Las **barras proporcionales** y la tasa de resolución. |
| **Administración** | Lista de usuarios, crear y activar/desactivar. | El **formulario en diálogo** y el chip de color por rol. |
| **Perfil** | Datos de la sesión, lo guardado en el teléfono y cerrar sesión. | El **resumen de lo guardado**: demuestra que la persistencia funciona. |
| **Ajustes** | Modo de color, prioridad por defecto y datos de contacto. | El **diálogo de borrar datos guardados**. |

---

## 5. Manejo del estado

Es la parte que más se explica, así que conviene tenerla clara.

### El estado vive en el ViewModel, no en la pantalla

Cada pantalla tiene dos archivos:

- **`XxxUiState`** — una foto de la pantalla en un momento dado: qué escribió el usuario, qué
  eligió, si está cargando, si hay error.
- **`XxxViewModel`** — guarda ese estado, decide qué pasa al tocar un botón y avisa hacia arriba.

La pantalla **nunca** guarda datos de la app. Por eso, al girar el teléfono, no se pierde nada:
lo que se guardó está en el ViewModel, que sigue vivo.

### Los errores se calculan, no se guardan

Este es el detalle fino que conviene highlight:

```kotlin
val nameError: String?
    get() = if (name.trim().length < 3) "Ingresa tu nombre completo" else null
```

El error **no es un campo más**: es una propiedad calculada con `get()`. Se recalcula sola
cada vez que cambia el valor. Si el error fuera un dato guardado, habría que acordarse de
recalcularlo a mano, y el día que se te olvide el mensaje rojo queda pegado en un campo que el
usuario ya corrigió. Calculado, eso no puede pasar.

Además hay un interruptor `showErrors`: los errores existen desde el primer momento (un
formulario vacío es inválido), pero **no se muestran hasta que el usuario intenta confirmar**.
Sin eso, al abrir el formulario se vería todo en rojo antes de escribir nada.

### El estado se entrega con StateFlow

`StateFlow` es un dato observable: no es una variable común, es una variable que **avisa**
cuando cambia. La pantalla se queda escuchando y, cuando cambia, Compose redibuja solo lo
afectado. No hay que escribir código para refrescar nada.

### Las pantallas están partidas en dos

Este patrón se repite en las 8 pantallas y vale la pena mencionarlo:

```kotlin
@Composable
fun InboxScreen(viewModel: InboxViewModel, ...) {
    val state by viewModel.uiState.collectAsState()
    InboxContent(state = state, onAdd = { ... }, ...)
}

@Composable
fun InboxContent(state: InboxUiState, ...) {
    // solo dibuja
}
```

- `XxxScreen` es la **única** que conoce al ViewModel.
- `XxxContent` **no conoce el ViewModel**: recibe todo por parámetro y solo dibuja.

Dos beneficios: se puede **previsualizar en el editor** sin correr la app (el `@Preview` llama
a la segunda), y la parte visual queda reutilizable.

---

## 6. Almacenamiento local

Es el requisito explícito del parcial, y la guía de la parte 3 lo trabaja con **DataStore**.

### Hay dos almacenes, y cada uno tiene su motivo

| Dato | Dónde | Por qué |
|---|---|---|
| Sesión, tickets y usuarios | `LocalStore` | Son **listas con datos relacionados** entre sí (un ticket tiene comentarios, evidencias y trazabilidad). La propia guía dice que eso, en una app real, iría a una base de datos. |
| Datos de contacto, último acceso, prioridad preferida, modo de color | `UserPreferencesRepository` (DataStore) | Son **valores sueltos y pequeños**, que es exactamente para lo que sirve DataStore. |

### DataStore: lo que trae la guía

```kotlin
val userPreferences: Flow<UserPreferences> = context.userPreferencesDataStore.data
    .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
    .map { preferences -> UserPreferences(...) }
```

- **Cada dato bajo su propia clave**, como la etiqueta de un cajón (`contact_name`,
  `contact_email`, `theme_mode`...).
- **Un `Flow`**: entrega lo que hay guardado ahora y vuelve a entregar cada vez que algo
  cambia. Quien lo recoge no tiene que preguntar.
- **`.catch`** protege el caso en que el archivo no se pueda leer: si falla, parte como si
  estuviera vacío, en vez de cerrar la app.
- **Las escrituras son `suspend`**: guardar toma tiempo, así que va dentro de una corrutina
  (`viewModelScope.launch { }`), nunca en el hilo que dibuja.

### La app "se acuerda de ti"

| Comportamiento | Cómo se nota en la demo |
|---|---|
| El modo de color elegido manda sobre **toda** la app | Cambiarlo en Ajustes cambia el color de todas las pantallas al instante |
| El último nombre de acceso queda escrito | Al volver a abrir la app, el campo de acceso ya viene escrito |
| La prioridad preferida se aplica sola | El formulario de nueva solicitud parte con esa prioridad |
| Los datos de contacto se muestran en el Perfil | Se ve que la app los guardó |
| Los tickets y usuarios sobreviven al cierre | Los cambios siguen ahí al reabrir |

### El tema depende de lo guardado

```kotlin
val preferences by settingsViewModel.preferences.collectAsState()
PanalSuiteTheme(themeMode = preferences.themeMode) { ... }
```

El `MainActivity` observa la preferencia y redibuja la app entera con el tema nuevo. Es el
mismo mecanismo del paso 10 de la guía.

---

## 7. Navegación

### El grafo

`ui/navigation/AppNavHost.kt` es **el único archivo que sabe qué pantallas existen y cómo se
conectan**. Cada pantalla es un nodo:

```
   Login ──► Bandeja ──► Detalle ──┐
              │  │                  │
              │  └──► Nueva ───────┤
              │                     ▼
              ├──► Indicadores   Confirmación implícita
              ├──► Usuarios
              └──► Perfil ──► Ajustes
```

Las rutas están declaradas como constantes en `Routes.kt` (`LOGIN`, `INBOX`, `NEW_TICKET`,
`TICKET_DETAIL`, `DASHBOARD`, `ADMIN`, `PROFILE`, `SETTINGS`) para no escribir textos sueltos
por el código: un error de tipeo en `"bandeja"` compilaría y fallaría recién al navegar.

### Las pantallas no reciben el NavController

Reciben **funciones**. `InboxScreen` recibe `onTicketClick: (String) -> Unit`, no el
`NavController`. Tres razones:

1. La pantalla **avisa** que algo pasó y **quien decide** es el nivel de arriba.
2. Si cada pantalla tuviera el `NavController`, cualquiera podría navegar a cualquier parte y
   el mapa de la app dejaría de estar en un solo archivo.
3. El `@Preview` volvería a fallar, porque en la vista previa no hay `NavController`.

### Volver es `popBackStack()`, no `navigate()`

`navigate` **apila** una pantalla encima; `popBackStack` **saca** la de arriba. Si para
"volver" se usara `navigate(HOME)`, se estaría apilando otra copia del menú: el usuario lo ve
y parece correcto, pero al apretar Atrás vuelve al detalle en vez de salir de la app. Es el
bug clásico de la primera app con navegación.

### La barra inferior depende del rol

`bottomItemsFor(role)` arma los botones según el rol, y `navigateSingleTopTo` evita apilar
copias de la misma pantalla cuando se cambia de sección.

---

## 8. Control de acceso por roles

Está en `model/UserRole.kt`, y cada rol tiene sus permisos como propiedades calculadas:

| Permiso | Qué permite | Quién lo tiene |
|---|---|---|
| `canCreateTicket` | Registrar solicitudes nuevas | Solicitante, Administrador |
| `canManageTickets` | Cambiar el estado de un ticket y registrar evidencias | Resolutor, Supervisor, Administrador |
| `canSeeDashboard` | Ver los indicadores | Supervisor, Gerencia, Administrador |
| `canManageUsers` | Administrar usuarios de prueba | Solo Administrador |

Ese permiso se aplica en **tres lugares distintos**, y esa es la parte que hay que explicar:

1. **En la barra inferior**: la sección Usuarios solo aparece para el administrador.
2. **En la bandeja**: cada rol ve una lista distinta. El solicitante ve los tickets que él
   pidió; el resolutor, los que tiene asignados y los que están sin asignar; los superiores,
   todos.
3. **En el detalle**: un solicitante que abre un ticket puede leerlo y comentar, pero la
   pantalla le dice que su rol no puede cambiar el estado, y el botón no está.

---

## 9. La capa de API

`repository/PanalApiService.kt` es el punto de acceso a la API de PanalSuite.

**Cómo funciona:** cada operación simula la latencia de una llamada real (`delay(450)`) y
responde en el **mismo formato JSON** que usaría el backend REST:

```json
{ "success": true, "message": null, "data": { ... } }
```

`JsonMappers.kt` traduce ese texto a los modelos de Kotlin. Como la API responde JSON, el
cambio de una cosa a la otra no toca ni la capa de datos ni las pantallas.

**Por qué es una buena decisión y no un atajo:** la API está **aislada en una sola clase**.
Para conectarla de verdad solo se cambia `PanalApiService` por un cliente Retrofit que apunte
a la URL real; `JsonMappers`, el repositorio, los ViewModel y las 8 pantallas **no se tocan**.
Eso es lo que hace que el enunciado pida "consumo de API" sin que la app dependa de un
servidor que en un MVP académico no existe.

**La semilla de datos:** 10 tickets y 5 usuarios, todos ficticios, que cubren los seis
estados, las cuatro prioridades, los distintos equipos resolutores y las sucursales. Sirven
para poder mostrar la app en la revisión sin depender de ningún servidor.

---

## 10. Decisiones de diseño (y por qué)

Esta es la sección que más suma en la revisión: no es lo que hace el código, sino **por qué
está así**.

### ¿Por qué no usaron Hilt, Room ni Retrofit?

La primera versión del proyecto sí usaba esa arquitectura por capas. Se descartó por dos
motivos concretos:

1. **No compilaba.** Esas librerías usan KSP, y KSP con Kotlin 1.9.23 no soporta el JDK 25
   que trae el IDE instalado. El error era `IllegalArgumentException: 25.0.2` en
   `kspDebugKotlin`.
2. **No se podía defender.** La capa de inyección de dependencias y los DAO quedaban por
   encima del nivel de la asignatura.

La versión entregada usa la misma estructura de la guía de la asignatura, que sí se puede
explicar de punta a punta. Y el diseño **no pierde nada de lo importante**: la capa de API
sigue siendo una capa, y el almacenamiento local sigue siendo local.

### ¿Por qué dos almacenes y no una base de datos?

Porque la propia guía distingue los dos casos: las preferencias sueltas van en DataStore, y
las listas con relaciones (los tickets con sus comentarios y su historial) irían en una base
de datos. La regla de negocio de una app real, con todos sus tickets, es más grande. Para el
MVP, un almacén local con JSON cumple y se puede explicar en una frase.

### ¿Por qué el ViewModel no sabe nada de la pantalla?

Porque el `ViewModel` sobrevive al giro del teléfono, pero la pantalla no. Si el estado
estuviera en la pantalla, al rotar se perdería. Además, al no depender de Compose, los
ViewModel se pueden probar sin interfaz.

### ¿Por qué los errores se calculan en vez de guardarse?

Porque un error guardado puede quedar desactualizado: el usuario escribe, corrige, y el
mensaje rojo sigue ahí. Un error calculado con `get()` **siempre** corresponde al valor actual.
Es la diferencia entre "a veces funciona" y "siempre funciona".

### ¿Por qué el ticket se valida en el `UiState` y no en la pantalla?

Porque la regla de negocio no puede estar en la parte visual. Si mañana la validación cambia,
tiene que cambiar en un solo lugar, y no en cinco pantallas.

### ¿Por qué el `ViewModel` se crea en la Activity?

Porque el estado es compartido entre pantallas: la bandeja, el detalle y el formulario usan
el mismo carrito de tickets. Lo que usan varias pantallas necesita su propio ViewModel, con
un nombre que diga lo que es.

### ¿Por qué `AndroidViewModel` en algunos ViewModel?

Porque el repositorio de preferencias necesita un `Context` para encontrar el archivo en el
teléfono, y un `ViewModel` común no lo tiene. `AndroidViewModel` recibe la `Application`,
que vive mientras la app esté abierta.

---

## 11. Errores encontrados y corregidos

Cada corrección tiene su propio commit `fix:`, y la causa está anotada en el mensaje.

| Qué pasaba | Por qué fallaba (la causa) | Commit |
|---|---|---|
| Al registrar una solicitud y volver a la bandeja, **la app se cerraba**. | El contador de identificadores arrancaba en 1000, pero los tickets de prueba ya ocupaban del `C-1001` al `C-1010`. La primera solicitud creada repetía un ID existente, y Compose rechaza la lista cuando hay claves duplicadas. | `fix: generar identificadores de ticket que no repitan los existentes` |
| El proyecto **no compilaba** con el JDK del IDE. | KSP con Kotlin 1.9.23 no soporta Java 25. Además, `gradle-daemon-jvm.properties` fijaba el daemon en Java 25. | `fix: fijar Gradle en JDK 21 para que el proyecto compile` |

**Errores que están documentados y pendientes de corregir** (también con su causa y su
arrreglo listo):

1. **Al refrescar después de reiniciar la app se pierden los cambios.** La API vive en
   memoria y al arrancar vuelve a su lista original, que después pisa lo guardado. Se
   arregla sincronizando la API con el almacenamiento local al iniciar.
2. **Un ticket creado repetía identificador** (ya corregido, arriba).
3. **La regla de visibilidad del resolutor** muestra tickets que ya tienen otro responsable.
4. **Dos tickets con el mismo ID** cerrarían la app: es el mismo error del punto 1.

> La fila que más importa en la bitácora de errores es **"por qué fallaba"**, porque ahí va la
> causa y no el síntoma. "El total sale mal" es un síntoma; la causa es qué operación en el
> código produce ese total.

---

## 12. Cómo abrir, compilar y correr el proyecto

**Requisitos:** Android Studio y un emulador con API 24 o superior.

```
1. File → Open → carpeta Proyecto_Fuentes_Fernandez (la que tiene settings.gradle.kts)
2. Esperar el Gradle Sync
3. Run ▶
```

**El JDK del Gradle debe ser el 21** (Settings → Build Tools → Gradle → Gradle JDK). Con el
JDK 25 que trae el IDE, la compilación falla.

Desde la terminal:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
.\gradlew.bat :app:assembleDebug
```

**Cuentas de prueba** (contraseña `1234` para todas). Se pueden tocar en la pantalla de
acceso, que completa el formulario solo:

| Usuario | Rol | Qué ve |
|---|---|---|
| `solicitante` | Solicitante | Bandeja con sus solicitudes, y puede registrar una nueva |
| `resolutor` | Resolutor | Bandeja con lo asignado, puede comentar y cambiar estados |
| `supervisor` | Supervisor | Todo lo anterior más los indicadores |
| `gerencia` | Gerencia | Bandeja completa e indicadores, sin tocar tickets |
| `admin` | Administrador | Todo, más la administración de usuarios de prueba |

---

## 13. Prueba de 3 minutos

Si hay que mostrar la app en un tiempo corto, este es el orden que muestra más cosas:

| Tiempo | Qué hacer | Qué demuestra |
|---|---|---|
| 0:00 | Entra como `solicitante` tocando la cuenta de prueba | El acceso validado y las cuentas de prueba |
| 0:20 | Abre el ícono de **más** y registra una solicitud con título y detalle, sin llenarlos todos | **El formulario validado**: aparecen los errores en rojo bajo los campos |
| 0:50 | Entra al ticket que acabas de crear | La navegación con argumento, la pantalla de detalle y la trazabilidad |
| 1:10 | Escribe un comentario y envíalo | El estado, el Snackbar y la persistencia |
| 1:30 | Gira el emulador | **No se pierde nada**: el estado está en el ViewModel |
| 1:45 | Cierra la app y vuelve a abrirla | **Los datos siguen ahí**: la persistencia local |
| 2:00 | Perfil → engranaje → cambia a "Oscuro" | **Toda la app** cambia de color al instante |
| 2:30 | Cierra sesión, entra como `admin` → Usuarios | **El control de acceso por rol** y la administración |
| 2:45 | Indicadores | Los KPI y la carga por equipo |

---

## 14. Preguntas que te puede hacer el profesor

**¿Por qué separaste la pantalla en dos funciones?**
Porque la que conoce al ViewModel y la que solo dibuja son responsabilidades distintas. La
primera pide el estado y avisa los cambios; la segunda dibuja. Así la parte visual se puede
previsualizar en el editor sin correr la app, y se puede reutilizar.

**¿Qué pasa si el usuario gira el teléfono a mitad de un formulario?**
No se pierde nada. Los ViewModel los crea la Activity con `by viewModels()`, y el ViewModel
sobrevive al giro porque no guarda ninguna referencia a la Activity. La pantalla se vuelve a
crear y pide el estado otra vez.

**¿Cómo validan los formularios?**
En el `UiState`, con una propiedad calculada por campo. El error no se guarda: se calcula cada
vez que cambia el valor, así que nunca queda desactualizado. Y hay un interruptor `showErrors`
para que los errores no aparezcan hasta el primer intento de enviar.

**¿Qué pasa si no hay internet?**
La app sigue funcionando completa. La capa de API corre dentro del teléfono y el
almacenamiento local es la fuente principal de los datos. No se necesita conexión para
navegar, comentar, cambiar un estado ni registrar una solicitud.

**¿Cómo se controla que un usuario no vea lo que no le corresponde?**
En tres lugares, todos a partir de los permisos de `UserRole`: la barra inferior arma sus
botones según el rol, la bandeja calcula qué tickets le corresponden, y el detalle deshabilita
las acciones que su rol no permite.

**¿Cómo se cambiaría esto para usar la API real?**
Solo se reemplaza `PanalApiService` por un cliente Retrofit que apunte a la URL real. Como la
API ya responde JSON y hay un traductor dedicado, ni el repositorio, ni los ViewModel, ni las
pantallas cambian.

**¿Qué herramienta usaron para guardar los datos y por qué?**
Dos. DataStore para las preferencias, que son valores sueltos y pequeños, y es lo que
trabaja la guía de la parte 3. Y un almacén local propio para los tickets y los usuarios,
que son listas con datos relacionados entre sí.

**¿Cómo saben que no se repiten los identificadores?**
El generador toma el ID más alto que ya existe en la lista y le suma uno, en vez de usar un
contador fijo. Así funciona aunque se agreguen tickets de prueba más adelante.

---

## 15. Glosario

| Término | Qué significa |
|---|---|
| **Composable** | Una función de Kotlin que dibuja parte de la pantalla. Describe cómo se ve, no devuelve nada. |
| **StateFlow** | Un dato que avisa cuando cambia. La pantalla lo escucha y se redibuja sola. |
| **ViewModel** | La memoria de una pantalla. Sobrevive al giro del teléfono y decide qué pasa al tocar un botón. |
| **UiState** | Una foto de la pantalla en un momento dado. Ahí viven los errores calculados. |
| **Flow** | Como el StateFlow, pero entrega valores a lo largo del tiempo. Se usa para leer lo guardado. |
| **suspend** | Una función que puede esperar sin bloquear la pantalla. Se llama dentro de una corrutina. |
| **corrutina** | Un trabajo que se hace en segundo plano y se cancela solo cuando ya no se necesita. |
| **DataStore** | La herramienta de Android para guardar datos pequeños en el teléfono de forma moderna. |
| **SharedPreferences** | La herramienta más antigua de Android para guardar preferencias. Se usa aquí para las listas de tickets. |
| **JSON** | El formato de texto en el que viajan los datos de una API. |
| **NavHost** | El mapa de navegación: qué pantallas existen y cómo se pasa de una a otra. |
| **popBackStack** | Sacar la pantalla de arriba de la pila, es decir, "volver". |
| **MVVM** | Modelo-Vista-Modelo de Vista. La forma de separar lo que se dibuja de lo que decide. |
| **KSP / Hilt / Room / Retrofit** | Librerías de la primera versión del proyecto. Se quitaron porque no compilaban con el JDK del IDE y porque quedaban por encima del nivel de la asignatura. |
