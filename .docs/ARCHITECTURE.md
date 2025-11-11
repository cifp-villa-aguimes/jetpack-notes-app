# Architecture Overview (v0.5.0-room-mvvm)

- **UI**: Jetpack Compose + Material3 (Scaffold, TopBar, BottomBar, FAB, Sheets, Dialogs). Se emplean componentes reutilizables (`SwipeableNoteCard`, Add/Edit sheets).
- **Navigation**: Navigation Compose 2.9.5 con rutas tipadas (`@Serializable` + `toRoute()`), centralizadas en `navigation/NavGraph.kt`.
- **State**: `AppState` mantiene solo `userName` (DataStore) y reseteo en logout; las notas provienen de `NotesViewModel` (`StateFlow`).
- **Persistencia ligera**: `data/prefs/UserPrefsRepository` + Jetpack DataStore guardan nombre, modo oscuro, bienvenida y orden.
- **Persistencia estructurada**: `data/local` con Room (`NoteEntity`, `NotesDao`, `NotesDatabase`).
- **Capas MVVM**: `data/repository/NotesRepositoryImpl` media entre DAO y dominio; `ui/notes/NotesViewModel` expone flujos y acciones suspend.
- **Inyección**: `di/ServiceLocator` crea singletons de Room y repositorio, usados desde `MainActivity` para construir el ViewModel.
- **Screens**: Login, Home (All/Favorites), Detail (view/edit), Settings.
- **Componentes compartidos**: `ui/components/AppBottomBar`, utilidades en `ui/common` (validaciones, helpers UI).
- **Edge-to-edge**: `WindowInsets.safeDrawing` para respetar notches.

## Flujos de navegación

`navigation/` define `AppRoute`/`RootDestination` como objetos `@Serializable`:

- `Login`, `Home`, `Favorites`, `Settings`, `Detail(id: String)`.

## Estado y datos

- `data/model/Note`: modelo de dominio (body no nulo para simplificar UI).
- `data/local/entity/NoteEntity`: representación Room con `body` nullable + índices.
- `data/local/dao/NotesDao`: consultas reactivas (Flow) y operaciones suspend.
- `data/repository/NotesRepository`: interfaz; `NotesRepositoryImpl` usa DAO y mappers `NoteEntity ↔ Note`.
- `data/prefs/UserPrefsRepository`: llaves Preferences + flujos (`userNameFlow`, `darkModeFlow`, `welcomeShownFlow`, `sortByFlow`) y setters suspend.
- `ui/notes/NotesViewModel`: expone `notes: StateFlow<List<Note>>`, `observeNote(id)` y acciones suspend con `runCatching` para manejo de errores.
- `MainActivity`: crea `NotesViewModel` via `NotesViewModelFactory` (ServiceLocator) y sincroniza el nombre desde DataStore hacia `AppState`.

## Persistencia y MVVM

- Room persiste cada nota con timestamps `createdAt/updatedAt` generados en el repositorio.
- `NotesRepositoryImpl` aplica la lógica de negocio mínima (IDs, timestamps, mapping, toggle favorito sin tocar `updatedAt`).
- `NotesViewModel` encapsula operaciones suspend y notifica fallos (bool) para que la UI muestre SnackBars.
- Home/Detail consumen flows (`collectAsState()`) y disparan acciones del ViewModel (add/update/toggle/delete).

## UI destacada

- `ui/home/components/SwipeableNoteCard`: gestiona swipe-to-delete y back swipe.
- `AddNoteSheet` / `EditNoteSheet`: formularios con límite 80 caracteres, soporte favorito, consistencia visual.
- Validaciones compartidas (`ui/common/UserNameValidation`): DRY para nickname en Login/Settings.

## Diagrama simplificado (texto)

```
DataStore (Preferences) ──▶ UserPrefsRepository ──▶ (AppState userName / Screens)
Room (NoteEntity) ──▶ NotesDao ─▶ NotesRepositoryImpl ─▶ NotesViewModel ─▶ HomeScreen / DetailScreen
```
