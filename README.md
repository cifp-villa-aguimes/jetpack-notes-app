# 🗒️ NotesApp Typed

Aplicación educativa de **notas** desarrollada en **Jetpack Compose**,  
con **navegación tipada** y gestión de estado mediante **Flow**.

> Versión: **v0.4.0-datastore** — Persistencia ligera con DataStore y navegación tipada.

## 🆕 Novedades de la versión v0.4.0

- Sincronización reactiva del nombre de usuario entre Login, Ajustes y Home.
- Persistencia del **tema oscuro**, criterio de ordenación y diálogo de bienvenida.
- Refactorización de UI (swipe para eliminar, hojas modales alineadas) para mantener coherencia.

---

## Descripción

**Compose Notes (Safe Nav)** es una aplicación Android moderna para la gestión de notas,  
diseñada como recurso didáctico para comprender la arquitectura actual de **Jetpack Compose**:  
**UI declarativa**, **navegación tipada segura**, **estado reactivo con Flows** y **persistencia ligera con DataStore**.

La versión (v0.4.0) consolida una capa de **preferencias persistentes** basada en DataStore,  
marcando el paso previo a la fase de persistencia completa con Room.

---

## Objetivo didáctico

El proyecto forma parte del módulo **Programación Multimedia y Dispositivos Móviles (PGL)**,  
dentro de la **Unidad de Trabajo 2 — Jetpack Compose** del ciclo **DAM**.

El propósito de esta versión es que el alumnado comprenda:

- Cómo Jetpack Compose gestiona la interfaz de forma **declarativa y reactiva**.
- La estructura moderna de una app con **Scaffold, AppBars, FAB, BottomBar, Sheets y Dialogs**.
- La implementación de una **navegación segura** usando **rutas tipadas (@Serializable)**.
- El uso de **StateFlow + collectAsState()** para compartir y sincronizar estado global.
- Cómo integrar una capa de **persistencia ligera con Jetpack DataStore** para ajustes de usuario.

---

## Características principales (v0.4.0)

- 📝 Crear, editar y eliminar notas.
- ⭐ Marcar notas como favoritas.
- 🧭 Navegación **type-safe** con rutas tipadas (`@Serializable`).
- 🎨 Interfaz moderna con **Material 3** y **Scaffold** (AppBar, FAB, BottomBar...).
- ⚙️ Pantallas: Login · Home · Favoritos · Detalle · Ajustes.
- 💬 Estado global con **StateFlow** y sincronización en tiempo real.
- 💾 Persistencia ligera con **DataStore Preferences** (nombre, tema, diálogo de bienvenida y orden de notas).
- 📱 Diseño **responsive** con `WindowInsets.safeDrawing` (Edge-to-Edge).

---

## Conceptos clave aprendidos

- **Declaratividad:** la interfaz se basa en el estado actual.
- **StateFlow:** flujo reactivo que mantiene la UI sincronizada.
- **DataStore Preferences:** almacenamiento ligero y asíncrono para ajustes de usuario.
- **remember / rememberSaveable:** persistencia temporal del estado.
- **Scaffold:** patrón de estructura moderna (AppBar + FAB + contenido).
- **WindowInsets.safeDrawing:** evita solapamiento con la Dynamic Island / notch.
- **DisposableEffect:** control del ciclo de vida Compose.
- **Navegación tipada:** rutas seguras con `toRoute()` y `popUpTo()`.
- **Arquitectura limpia:** separación UI / Lógica / Estado.

---

## Persistencia ligera con DataStore

La aplicación utiliza **DataStore Preferences** para conservar la información de usuario incluso tras cerrar la app:

- **Nombre de usuario**: se precarga en Login y Ajustes y se sincroniza con el `AppState` global.
- **Tema oscuro**: conmutador en Ajustes, aplicado al `MaterialTheme` desde `MainActivity`.
- **Diálogo de bienvenida**: se muestra solo la primera vez y puede resetearse desde Ajustes.
- **Criterio de ordenación**: permite ordenar las notas por fecha, título o favoritos de forma persistente.

El repositorio `UserPrefsRepository` expone estos valores como **Flow**, lo que permite consumirlos de manera reactiva en Compose (`collectAsState`) y mantener una única fuente de verdad para la UI.

---

## 📁 Estructura del Proyecto

```text
app/src/main/java/edu/dam/notesapptyped/
├── data/                 # Estado global (AppState) y modelo Note
├── navigation/           # Gráfico y rutas tipadas (@Serializable)
├── theme/                # Estilos, tipografía y colores Material 3
└── ui/                   # Interfaz y pantallas
    ├── components/       # Componentes compartidos (BottomBar, etc.)
    ├── common/           # Utilidades UI compartidas
    ├── home/             # Pantalla principal (Home + Favoritos)
    ├── detail/           # Vista de detalle y edición
    ├── login/            # Pantalla de inicio de sesión
    └── settings/         # Pantalla de ajustes
```

---

## 🧱 Tecnologías

| Tecnología                          | Uso principal                    |
| ----------------------------------- | -------------------------------- |
| **Kotlin**                          | Lenguaje base                    |
| **Jetpack Compose**                 | UI declarativa y moderna         |
| **Navigation Compose 2.9.5**        | Navegación tipada                |
| **Material 3**                      | Componentes visuales             |
| **Kotlin Serialization**            | Serialización para rutas seguras |
| **Flow / MutableStateFlow**         | Gestión del estado reactivo      |
| **Jetpack DataStore (Preferences)** | Persistencia ligera de ajustes   |

---

## 📦 Requisitos

- Android Studio **Ladybug** o superior
- Kotlin **2.0.21+**
- SDK mínimo: **API 24 (Android 7.0)**
- SDK objetivo: **API 36**
- JDK 11 o superior

---

## Instalación

1. Clona el repositorio:
   ```bash
   git clone https://github.com/cifp-villa-aguimes/jetpack-notes-app.git
   ```
2. Abre el proyecto en **Android Studio**.
3. Compila y ejecuta en un **emulador o dispositivo físico (Android 8+)**.

---

## 📘 Créditos

Creado como recurso educativo por **Josué García**  
📍 _CIFP Villa de Agüimes_ — Especialidad **Informática y Comunicaciones**  
🧑‍🏫 _Ciclos DAM · Módulo PGL (Programación Multimedia y Dispositivos Móviles)_

---

## 🧾 Licencia

Este proyecto se distribuye bajo la licencia **MIT**.  
Puedes usarlo, adaptarlo o modificarlo libremente con fines educativos.
