# PanalSuite Mobile

Aplicación Android para la gestión móvil de flujos de trabajo de PanalSuite.

---

## El caso

Panalbit está migrando desde su plataforma de mesa de ayuda hacia **PanalSuite**, una plataforma
de gestión de tickets. Hoy esas operaciones se hacen desde la web, lo que limita la gestión
cuando el usuario está en terreno o no tiene un computador a mano.

El entregable es un **MVP académico** que complemente la plataforma web y permita operar desde
el teléfono: autenticación, consulta de bandejas y solicitudes, revisión de estados,
ejecución de acciones autorizadas, registro de comentarios, incorporación de evidencias de
prueba y recepción de notificaciones.

---

## En qué consiste la app

- **5 roles** con permisos distintos: solicitante, resolutor, supervisor, gerencia y
  administrador. Cada uno ve y puede hacer cosas diferentes.
- **8 pantallas**: acceso, bandeja de trabajo, detalle del ticket, nueva solicitud,
  indicadores, administración de usuarios, perfil y ajustes.
- **Tickets completos**: estado, prioridad, comentarios, evidencias y trazabilidad de quién
  cambió qué y cuándo.
- **Funciona sin internet**: todo se guarda en el teléfono.
- **8 pantallas, 4 capas** (modelo, repositorio, ViewModel e interfaz) sobre Kotlin con
  Jetpack Compose y Material 3.

**Cuentas de prueba** — contraseña `1234` para todas: `solicitante`, `resolutor`,
`supervisor`, `gerencia` y `admin`.

---

## Integrantes

| | |
|---|---|
| **Martín Fuentes** | [MFuentesDuoc](https://github.com/MFuentesDuoc) |
| **Martín Fernández** | [MFernandezDuoc](https://github.com/MFernandezDuoc) |
