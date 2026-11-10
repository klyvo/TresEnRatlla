# Android MVVM Tic-Tac-Toe (Tres en Ratlla)

Aquesta aplicació és una implementació del joc del **Tres en Ratlla** en Android desenvolupada amb **Jetpack Compose** seguint l'arquitectura **MVVM (Model-View-ViewModel)** amb model separat i immutable (`MVVM-separatModel`).

---

## 1. Anàlisi i documentació de l'arquitectura MVVM

### 1.1. Documentació dels components (basant-vos en MVVMSimple)

- **Model**:
  - **Funció principal**: Guarda les dades de l'aplicació i la informació d'estat del domini (en el nostre cas, l'estat de les caselles del tauler, de qui és el torn, si hi ha guanyador o empat).
  - **Funció en l'arquitectura**: Actua com la font única de la veritat (*Single Source of Truth*). La seva funció és completament independent de la interfície d'usuari i no té cap coneixement sobre Android, Jetpack Compose o com es mostraran les dades.

- **ViewModel**:
  - **Quina feina fa**: Actua de pont i orquestrador entre el Model i la Vista (UI). Manté l'estat actual de la interfície d'usuari i gestiona el seu cicle de vida (sobrevisquent als canvis de configuració com la rotació de la pantalla).
  - **Comunicació amb el Model**: Executa les accions sobre el Model quan rep esdeveniments de la UI (per exemple, processar un moviment d'un jugador).
  - **Exposició de dades a la UI**: Exposa l'estat cap a la vista mitjançant fluxos de dades reactius de només lectura (com `StateFlow`), assegurant que la UI no pugui modificar directament l'estat sense passar per les funcions del ViewModel.

- **View (UI / Compose)**:
  - **Papel**: Representació visual declarativa de la interfície d'usuari construïda amb Jetpack Compose.
  - **Recepció de canvis d'estat**: Es subscriu als fluxos d'estat del ViewModel (utilitzant operadors com `collectAsState()`). Quan l'estat canvia, Compose redibuixa (*recomposició*) automàticament els components afectats.
  - **Notificació d'accions**: Rep els esdeveniments de l'usuari (ex: fer clic en una casella o en el botó de reiniciar) i els delega al ViewModel cridant als seus mètodes (ex: `onCellClick(position)`).

---

### 1.2. Anàlisi comparatiu (MVVMSimple vs MVVM-separatModel)

- **Estructura i Separació de Responsabilitats**:
  - **Diferència d'organització**: A `MVVMSimple`, el Model sol estar definit com una estructura molt simple o fins i tot barrejat directament dins de les propietats i lògica del ViewModel. En canvi, a `MVVM-separatModel`, el Model (`BoardModel`) és una classe de dades (*data class*) completament independent que conté la seva pròpia lògica i mètodes.
  - **Execució de la lògica de negoci**: A `MVVMSimple`, la lògica de negoci (comprovar guanyador, canviar torns, validar caselles buides) s'executa directament dins del ViewModel. A `MVVM-separatModel`, aquesta lògica s'executa **exclusivament dins del Model** (`BoardModel`), i el ViewModel només s'encarrega d'emmagatzemar i exposar les instàncies actualitzades del model.

- **Immutabilitat i Gestió de l'Estat**:
  - **Modificació de dades i mètode `.copy()`**: A `MVVM-separatModel`, les dades del tauler són immutables. Quan un jugador fa un moviment, el mètode `makeMove()` no modifica (*mutate*) l'objecte existent, sinó que utilitza el mètode `.copy()` de Kotlin per crear i retornar una **nova instància** de `BoardModel` amb les dades actualitzades.
  - **Importància per a l'arquitectura reactiva**: L'immutabilitat permet detectar fàcilment si l'estat ha canviat mitjançant la comparació de referències d'objectes. Assegura que els sistemes reactius com `StateFlow` i Jetpack Compose detectin immediatament el canvi i recomponguin només la UI necessària, evitant efectes secundaris inesperats i errors d'estat incongruent.

- **Avantatges de la separació**:
  - **Testabilitat**: La lògica de negoci del joc (`BoardModel`) es pot provar mitjançant proves unitàries (*Unit Tests*) pures de Kotlin, sense necessitat de configurar entorns d'Android, ViewModel, Corrutines o Jetpack Compose.
  - **Mantenibilitat i Escalabilitat**: A mesura que l'aplicació creix (afegint diferents modes de joc, IA, multijugador en xarxa, etc.), la lògica del joc es manté aïllada i modular.
  - **Principi de Responsabilitat Única (SRP)**: El Model s'encarrega únicament de les regles del joc, el ViewModel s'encarrega de la gestió de l'estat de la pantalla, i la Vista s'encarrega únicament de pintar la interfície.

---

### 1.3. Anàlisi comparatiu: StateFlow / MutableStateFlow vs mutableStateOf / State<T>

- **Per a què es fan servir**:
  - **`StateFlow` / `MutableStateFlow`**: Són fluxos d'estat observables de la llibreria de corrutines de Kotlin (`kotlinx.coroutines.flow`). Es fan servir principalment en les capes d'arquitectura com el ViewModel per gestionar i emetre estats observables de manera asíncrona i decoupled del framework visual.
  - **`mutableStateOf` / `State<T>`**: Són primitives d'estat pròpies del runtime de Jetpack Compose (`androidx.compose.runtime`). S'utilitzen dins de la UI de Compose per activar la recomposició automàtica dels composables quan el seu valor canvia.

- **Diferències entre ells**:
  | Característica | `StateFlow` / `MutableStateFlow` | `mutableStateOf` / `State<T>` |
  | :--- | :--- | :--- |
  | **Dependència** | Kotlin Coroutines (Pur Kotlin, independent de UI) | Jetpack Compose Runtime (Dependent de Compose) |
  | **Observabilitat** | Flux (*Flow*) que requereix un col·lector (`collectAsState()`) | Lectura directa de la propietat `value` que activa recomposició |
  | **Capa d'ús recomanada** | Capa de ViewModel / Arquitectura | Capa de UI / Composables |
  | **Encapsulament** | Protecció fàcil amb `MutableStateFlow` privat i `asStateFlow()` | S'encapsula amb `mutableStateOf` privat o paràmetres d'estat |

- **Elecció per a l'aplicació i justificació**:
  - Per a aquesta aplicació s'ha escollit **`StateFlow` / `MutableStateFlow` al ViewModel**, que es converteix en un `State<T>` de Compose mitjançant **`collectAsState()` a la Vista**.
  - **Justificació**: Aquesta és la millor pràctica recomanada per Google. Permet mantenir la capa de ViewModel completament independent del framework de UI (Jetpack Compose), facilitant les proves unitàries i seguint una separació neta de responsabilitats. A més, exposar `boardState: StateFlow<BoardModel>` protegit d'un `_boardState: MutableStateFlow` privat garanteix la immutabilitat de l'estat des de fora del ViewModel.

---

## 2. Explicació de l’arquitectura utilitzada al Tres en Ratlla

L'aplicació segueix estrictament el patró **MVVM amb model separat (`MVVM-separatModel`)**. L'estructura del projecte s'organitza en tres paquets principals dins de `org.proven.tresenrattla`:

```
org.proven.tresenrattla/
├── model/
│   ├── CellModel.kt      # Dades immutables d'una casella (id, posició, símbol "X"/"O"/buida)
│   └── BoardModel.kt     # Estat del tauler i lògica pura del joc (makeMove, checkWinner, .copy())
├── viewmodel/
│   └── GameViewModel.kt  # Gestor d'estat UI amb MutableStateFlow privat i asStateFlow() públic
└── ui/
    └── GameScreen.kt     # Pantalla Compose (GameScreen, BoardGrid amb LazyVerticalGrid, CellItemView)
```

### Flux de dades unidericcional (Unidirectional Data Flow - UDF):

```
+-------------------------------------------------------------------------+
|                                                                         |
|  [ User Action: Click Cell ] ----> GameViewModel.onCellClick(position)  |
|                                                     |                   |
|                                                     v                   |
|                                          BoardModel.makeMove(position)  |
|                                                     |                   |
|                                                     v                   |
|  [ UI Recomposition ] <--- collectAsState() <--- StateFlow (New Board)  |
|                                                                         |
+-------------------------------------------------------------------------+
```

1. **Usuari (UI)**: Fa clic sobre una casella del tauler.
2. **Event**: La vista crida a `viewModel.onCellClick(position)`.
3. **Lògica de Negoci (Model)**: El `GameViewModel` executa `_boardState.value.makeMove(position)`, que genera un nou `BoardModel` immutable utilitzant `.copy()`.
4. **Actualització d'Estat**: El `MutableStateFlow` emet el nou `BoardModel`.
5. **Recomposició**: La funció composable `GameScreen`, que observa l'estat amb `collectAsState()`, rep la nova instància de `BoardModel` i redibuixa automàticament les caselles afectades i l'indicador de torn/guanyador.

### Funcionament de l'aplicació

- **Torn inicial**: Comença el jugador "X".
- **Comprovació automàtica**: Quan 3 caselles consecutives tenen el mateix símbol, es mostra el missatge de guanyador i es bloquegen les caselles.
- **Empat**: Si el tauler s'omple sense guanyador, es mostra "Empat! Partida finalitzada".
- **Reinici**: El botó "Reiniciar Partida" restableix el tauler a l'estat inicial mitjançant `BoardModel.createInitialBoard()`.

---

## 3. Conceptes i Mètodes Apresos

En el desenvolupament d'aquesta pràctica s'han aplicat i après els següents mètodes, funcions i conceptes claus d'Android, Kotlin i Jetpack Compose:

| Concepte / Mètode | Descripció i Utilitat |
| :--- | :--- |
| **`collectAsState()`** | Funció d'extensió de Compose que subscriu un Composable a un `StateFlow` o `Flow` i el reconverteix en un `State<T>` de Compose. Permet que la UI es recompongui automàticament cada vegada que s'emet un nou estat des del ViewModel. |
| **`asStateFlow()`** | Mètode de Kotlin Coroutines utilitzat per convertir un `MutableStateFlow` privat en un `StateFlow` de només lectura. Protegeix l'estat per evitar modificacions directes fora del ViewModel. |
| **`LazyVerticalGrid`** | Component composable per organitzar elements en una graella bidimensional. Utilitzat amb `GridCells.Fixed(3)` per construir eficientment el tauler de 3x3 del joc. |
| **`.copy()`** | Mètode generat automàticament per les `data class` de Kotlin. Permet crear una nova instància d'un objecte modificant només certs paràmetres i mantenint la resta intactes, elemental per treballar amb estat immutable. |
| **`viewModel()`** | Funció composable del paquet `androidx.lifecycle.viewmodel.compose` que proporciona la instància adequada de `ViewModel` associada al cicle de vida de la pantalla o activitat. |
| **`MutableStateFlow`** | Flux de dades (*StateFlow*) mutable que emet valors observables i manté l'estat actual a la memòria. Actua com la font d'estat reactiva al ViewModel. |
| **`CardDefaults.cardColors()`** | Funció de Material 3 per personalitzar dinàmicament els colors de fons i contingut dels contenidors `Card` segons l'estat del joc (torn, guanyador o empat). |

---

## 4. Gestió del Repositori i Commits (Git & GitHub)

El projecte es gestiona al repositori públic de GitHub: **`Android-MVVM-TicTacToe`**.

### Historial de Commits recomanat:
- `feat: inicialitza l'estructura del projecte i tema Material3`
- `feat: crea CellModel i BoardModel immutable amb createInitialBoard()`
- `feat: implementa la lògica pura de makeMove() i checkWinner() en BoardModel`
- `feat: crea GameViewModel amb MutableStateFlow i asStateFlow()`
- `feat: dissenya la UI amb GameScreen, BoardGrid i CellItemView en Jetpack Compose`
- `test: afegeix proves unitàries per a la lògica de BoardModel`
- `docs: afegeix anàlisi MVVM i documentació completa al README.md`
