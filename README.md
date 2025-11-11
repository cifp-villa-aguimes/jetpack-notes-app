[![Made with Kotlin](https://img.shields.io/badge/Kotlin-1.9+-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Room Persistence](https://img.shields.io/badge/Room-Persistence%20Library-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/cifp-villa-aguimes/jetpack-notes-app)

# 🗒️ NotesApp Typed

Aplicación educativa de **notas** desarrollada en **Jetpack Compose**,  
con **navegación tipada** y gestión de estado mediante **Flow**.

> Versión: **v0.5.0-room-mvvm** — Persistencia estructurada con Room y patrón MVVM.

## 🆕 Novedades de la versión v0.5.0

- Capa de datos con **Room** (entidad `NoteEntity`, `NotesDao`, `NotesDatabase`).
- Repositorio `NotesRepositoryImpl` + patrón **MVVM** con `NotesViewModel` y Service Locator manual.
- Persistencia real de notas con operaciones CRUD reactivas (Flow).
- Manejo de errores con mensajes SnackBar al fallar operaciones de guardado/borrado.

---

## Descripción

**Compose Notes (Safe Nav)** es una aplicación Android moderna para la gestión de notas,  
diseñada como recurso didáctico para comprender la arquitectura actual de **Jetpack Compose**:  
**UI declarativa**, **navegación tipada segura**, **estado reactivo con Flows**, **persistencia ligera con DataStore** y ahora **persistencia estructurada con Room**.

La versión **v0.5.0 (Room + MVVM)** incorpora la capa de base de datos con Room y un ViewModel dedicado,  
consolidando el camino hacia la arquitectura completa de la app.

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
- Cómo estructurar la **persistencia con Room** siguiendo el patrón **Repository + MVVM**.

---

## Características principales (v0.5.0)

- 📝 Crear, editar y eliminar notas.
- ⭐ Marcar notas como favoritas.
- 🧭 Navegación **type-safe** con rutas tipadas (`@Serializable`).
- 🎨 Interfaz moderna con **Material 3** y **Scaffold** (AppBar, FAB, BottomBar...).
- ⚙️ Pantallas: Login · Home · Favoritos · Detalle · Ajustes.
- 💬 Estado global con **StateFlow** y sincronización en tiempo real.
- 💾 Persistencia ligera con **DataStore Preferences** (nombre, tema, diálogo de bienvenida y orden de notas).
- 🗄️ Persistencia estructurada con **Room** (notas guardadas en base de datos local).
- 📱 Diseño **responsive** con `WindowInsets.safeDrawing` (Edge-to-Edge).

---

## Conceptos clave aprendidos

- **Declaratividad:** la interfaz se basa en el estado actual.
- **StateFlow:** flujo reactivo que mantiene la UI sincronizada.
- **DataStore Preferences:** almacenamiento ligero y asíncrono para ajustes de usuario.
- **Room + MVVM:** persistencia estructurada con repositorio y ViewModel.
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

## Room + MVVM + Service Locator

La persistencia estructurada se implementa con **Room**, siguiendo el patrón **Repository + MVVM** y un **Service Locator** ligero:

1. **Room**
   - `NoteEntity` (`data/local/entity`): entidad Room con índices para `updatedAt` y `isFavorite`.
   - `NotesDao` (`data/local/dao`): expone consultas `Flow` y operaciones `suspend` (insert/update/delete/toggle).
   - `NotesDatabase` (`data/local/NotesDatabase.kt`): registra la entidad y el DAO.

2. **Repository**
   - `NotesRepository` define la interfaz (observe, add, update, toggle, delete).
   - `NotesRepositoryImpl` genera IDs/timestamps, mapea `Note ↔ NoteEntity` y delega en el DAO.

3. **MVVM**
   - `NotesViewModel` (`ui/notes`) crea un `StateFlow` de notas (`stateIn`) y expone acciones `suspend` que devuelven `Boolean` para que la UI muestre SnackBars ante fallos.
   - `HomeScreen` y `DetailScreen` consumen los flujos y llaman al ViewModel para CRUD.

4. **Service Locator**
   - `di/ServiceLocator` crea instancias únicas de `NotesDatabase`, `NotesDao` y `NotesRepositoryImpl`.
   - `MainActivity` usa `NotesViewModelFactory` para inyectar el repositorio en el ViewModel y compartirlo con el `NavGraph`.

Este flujo asegura una única fuente de verdad para las notas, permite testear capas por separado y mantiene la UI desacoplada de la implementación de datos.

---

## 📁 Estructura del Proyecto

```text
app/src/main/java/edu/dam/notesapptyped/
├── data/                 # Estado global (AppState) y modelo Note
│   ├── local/            # Capa Room (Database, DAO, entidades)
│   ├── mappers/          # Conversiones Note ↔ NoteEntity
│   ├── prefs/            # DataStore Preferences y repositorio de usuario
│   └── repository/       # NotesRepository + implementación
├── navigation/           # Gráfico y rutas tipadas (@Serializable)
├── theme/                # Estilos, tipografía y colores Material 3
└── ui/                   # Interfaz y pantallas
    ├── components/       # Componentes compartidos (BottomBar, etc.)
    ├── common/           # Utilidades UI compartidas
    ├── home/             # Pantalla principal (Home + Favoritos)
    ├── detail/           # Vista de detalle y edición
    ├── login/            # Pantalla de inicio de sesión
    ├── notes/            # ViewModel y lógica MVVM de notas
    └── settings/         # Pantalla de ajustes
├── di/                   # Service Locator para Room/Repository
```

---

## 🧱 Tecnologías

| Tecnología                          | Uso principal                      |
| ----------------------------------- | ---------------------------------- |
| **Kotlin**                          | Lenguaje base                      |
| **Jetpack Compose**                 | UI declarativa y moderna           |
| **Navigation Compose 2.9.5**        | Navegación tipada                  |
| **Material 3**                      | Componentes visuales               |
| **Kotlin Serialization**            | Serialización para rutas seguras   |
| **Flow / MutableStateFlow**         | Gestión del estado reactivo        |
| **Jetpack DataStore (Preferences)** | Persistencia ligera de ajustes     |
| **Room**                            | Persistencia estructurada de notas |

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
