# 🗒️ NotesApp Typed

Aplicación educativa de **notas** desarrollada en **Jetpack Compose**,  
con **navegación tipada** y gestión de estado mediante **Flow**.

> Versión: **v0.3.0-nav-typed** — Navegación con rutas tipadas y estructura modular.

---

## Descripción

**Compose Notes (Safe Nav)** es una aplicación Android moderna para la gestión de notas,  
diseñada como recurso didáctico para comprender la arquitectura actual de **Jetpack Compose**:  
**UI declarativa**, **navegación tipada segura** y **estado reactivo con Flows**.

Esta versión (v0.3.0) representa un **punto intermedio del proyecto**,  
que sienta las bases para la siguiente fase: **persistencia con DataStore y Room**.

---

## Objetivo didáctico

El proyecto forma parte del módulo **Programación Multimedia y Dispositivos Móviles (PGL)**,  
dentro de la **Unidad de Trabajo 2 — Jetpack Compose** del ciclo **DAM/DAW**.

El propósito de esta versión es que el alumnado comprenda:

- Cómo Jetpack Compose gestiona la interfaz de forma **declarativa y reactiva**.  
- La estructura moderna de una app con **Scaffold, AppBars, FAB, BottomBar, Sheets y Dialogs**.  
- La implementación de una **navegación segura** usando **rutas tipadas (@Serializable)**.  
- El uso de **StateFlow + collectAsState()** para compartir y sincronizar estado global.

---

## Características principales (v0.3.0)

- 📝 Crear, editar y eliminar notas.
- ⭐ Marcar notas como favoritas.
- 🧭 Navegación **type-safe** con rutas tipadas (`@Serializable`).
- 🎨 Interfaz moderna con **Material 3** y **Scaffold** (AppBar, FAB, BottomBar...).
- ⚙️ Pantallas: Login · Home · Favoritos · Detalle · Ajustes.
- 💬 Estado global con **StateFlow** y sincronización en tiempo real.
- 📱 Diseño **responsive** con `WindowInsets.safeDrawing` (Edge-to-Edge).

---

## Conceptos clave aprendidos

- **Declaratividad:** la interfaz se basa en el estado actual.  
- **StateFlow:** flujo reactivo que mantiene la UI sincronizada.  
- **remember / rememberSaveable:** persistencia temporal del estado.  
- **Scaffold:** patrón de estructura moderna (AppBar + FAB + contenido).  
- **WindowInsets.safeDrawing:** evita solapamiento con la Dynamic Island / notch.  
- **DisposableEffect:** control del ciclo de vida Compose.  
- **Navegación tipada:** rutas seguras con `toRoute()` y `popUpTo()`.  
- **Arquitectura limpia:** separación UI / Lógica / Estado.

---

## 📁 Estructura del Proyecto

```text
app/src/main/java/edu/dam/notesapptyped/
├── data/                 # Estado global (AppState) y modelo Note
├── navigation/           # Gráfico y rutas tipadas (@Serializable)
├── theme/                # Estilos, tipografía y colores Material 3
└── ui/                   # Interfaz y pantallas
    ├── components/       # Componentes compartidos (BottomBar, etc.)
    ├── home/             # Pantalla principal (Home + Favoritos)
    ├── detail/           # Vista de detalle y edición
    ├── login/            # Pantalla de inicio de sesión
    └── settings/         # Pantalla de ajustes
```
    
---

## 🧱 Tecnologías

| Tecnología | Uso principal |
|-------------|----------------|
| **Kotlin** | Lenguaje base |
| **Jetpack Compose** | UI declarativa y moderna |
| **Navigation Compose 2.9.5** | Navegación tipada |
| **Material 3** | Componentes visuales |
| **Kotlin Serialization** | Serialización para rutas seguras |
| **Flow / MutableStateFlow** | Gestión del estado reactivo |

---

## 📦 Requisitos

- Android Studio **Ladybug** o superior  
- Kotlin **2.0.21+**  
- SDK mínimo: **API 24 (Android 7.0)**  
- SDK objetivo: **API 36**  
- JDK 17 o superior  

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
📍 *CIFP Villa de Agüimes* — Especialidad **Informática y Comunicaciones**  
🧑‍🏫 *Ciclos DAM · Módulo PGL (Programación Multimedia y Dispositivos Móviles)*

---

## 🧾 Licencia

Este proyecto se distribuye bajo la licencia **MIT**.  
Puedes usarlo, adaptarlo o modificarlo libremente con fines educativos.