# Architecture Overview (v0.4.0-datastore)

- **UI**: Jetpack Compose + Material3 (Scaffold, TopBar, BottomBar, FAB, Sheets, Dialogs). Se emplean componentes reutilizables (`SwipeableNoteCard`, hojas Add/Edit) para mantener coherencia.
- **Navigation**: Navigation Compose 2.9.5 con rutas tipadas (`@Serializable` + `toRoute()`), centralizadas en `navigation/NavGraph.kt`.
- **State**: `AppState` expone `MutableStateFlow` para notas y nombre de usuario; las pantallas usan `collectAsState()`.
- **Persistencia ligera**: `data/prefs/UserPrefsRepository` + Jetpack DataStore (Preferences) guardan nombre, modo oscuro, bienvenida vista y orden de notas.
- **Screens**: Login, Home (All/Favorites), Detail (view/edit), Settings.
- **Componentes compartidos**: `ui/components/AppBottomBar`, utilidades en `ui/common` (validaciones, helpers UI).
- **Edge-to-edge**: `WindowInsets.safeDrawing` para respetar notches.
- **Pendiente**: integración de Room para CRUD persistente de notas.

## Flujos de navegación

`navigation/` define `AppRoute`/`RootDestination` como objetos `@Serializable`:

- `Login`, `Home`, `Favorites`, `Settings`, `Detail(id: String)`.

## Estado y datos

- `data/model/Note`: id, title, body, author, createdAt, updatedAt, isFavorite.
- `data/AppState`: operaciones sobre notas (`add`, `update`, `delete`, `toggleFavorite`, `resetForLogout`) + sincronización con DataStore (nombre).
- `data/prefs/UserPrefsRepository`: llaves Preferences + flujos (`userNameFlow`, `darkModeFlow`, `welcomeShownFlow`, `sortByFlow`) y setters suspend.

## Persistencia ligera (DataStore)

- DataStore Preferences se instancia en `MainActivity` y se inyecta a `NavGraph` para compartirlo entre pantallas.
- `MainActivity` aplica el modo oscuro leyendo `darkModeFlow` y sincroniza `userNameFlow` con `AppState`.
- Login y Settings escriben el nombre; Settings controla bienvenida y orden (`SortBy` enum: `DATE`, `TITLE`, `FAVORITE`).
- Home consume las preferencias para mostrar el diálogo de bienvenida una sola vez y ordenar la lista de notas.

## UI destacada

- `ui/home/components/SwipeableNoteCard`: encapsula el gesto de swipe-to-delete con confirmación y estado hoisted.
- `AddNoteSheet` / `EditNoteSheet`: hojas modales con límites de caracteres y diseño alineado.
- Validaciones compartidas (`ui/common/UserNameValidation`): DRY para nickname en Login/Settings.
