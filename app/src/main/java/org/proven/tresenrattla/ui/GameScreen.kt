package org.proven.tresenrattla.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.proven.tresenrattla.model.BoardModel
import org.proven.tresenrattla.model.CellModel
import org.proven.tresenrattla.viewmodel.GameViewModel

/**
 * Pantalla principal de la interfície d'usuari (UI) del Tres en Ratlla construïda amb Jetpack Compose.
 *
 * @Composable: Anotació indispensable de Jetpack Compose que indica que aquesta funció dibuixa un element visual
 * de manera declarativa.
 *
 * @param viewModel Instància del [GameViewModel]. La funció viewModel() de Compose s'encarrega d'obtenir o crear
 * la instància vinculada al cicle de vida de la pantalla.
 */
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    // collectAsState(): Subscriu la funció Composable al StateFlow del ViewModel.
    // 'by' (Property Delegation): Permet accedir directament a les propietats de BoardModel
    // sense haver d'escriure boardState.value. Cada vegada que el StateFlow emet un nou valor,
    // Compose executa la RECOMPOSICIÓ d'aquesta pantalla per reflectir els canvis a la UI.
    val boardState by viewModel.boardState.collectAsState()

    // Surface: Contenidor base de Material Design 3 per aplicar color de fons i elevació.
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Títol de l'aplicació
            Text(
                text = "Tres en Ratlla",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Indicador visual de l'estat del joc (torn actual o resultat final)
            GameStatusIndicator(boardState = boardState)

            // Tauler de 3x3 construït amb la graella eficient BoardGrid
            BoardGrid(
                cells = boardState.cells,
                onCellClick = viewModel::onCellClick
            )

            // Botó per reiniciar la partida
            Button(
                onClick = { viewModel.restartGame() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Reiniciar Partida",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Indicador que mostra visualment quin és el torn actual o el resultat final (guanyador/empat).
 */
@Composable
fun GameStatusIndicator(boardState: BoardModel) {
    // Card: Contenidor visual de Material 3 amb fons acolorit dinàmicament segons l'estat del joc
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                boardState.winner != null -> MaterialTheme.colorScheme.primaryContainer
                boardState.isDraw -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.secondaryContainer
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val statusText = when {
                boardState.winner != null -> "Guanyador: Jugador ${boardState.winner}!"
                boardState.isDraw -> "Empat! Partida finalitzada"
                else -> "Torn del Jugador: ${boardState.currentTurn}"
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = when {
                    boardState.winner != null -> MaterialTheme.colorScheme.onPrimaryContainer
                    boardState.isDraw -> MaterialTheme.colorScheme.onSurfaceVariant
                    else -> MaterialTheme.colorScheme.onSecondaryContainer
                }
            )
        }
    }
}

/**
 * Graella de 3x3 que conté les 9 caselles del tauler.
 */
@Composable
fun BoardGrid(
    cells: List<CellModel>,
    onCellClick: (Int) -> Unit
) {
    // LazyVerticalGrid: Component eficient de Compose per organitzar elements en graelles bidimensionals.
    // GridCells.Fixed(3): Defineix una estructura de graella amb exactament 3 columnes d'amplada igual.
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Força a que el tauler mantingui una relació d'aspecte quadrada (1:1)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false
    ) {
        // items(): Recorre la llista de caselles i assigna la clau cel·la.id per optimitzar la recomposició
        items(
            items = cells,
            key = { cell -> cell.id }
        ) { cell ->
            CellItemView(
                cell = cell,
                onCellClick = { onCellClick(cell.position) }
            )
        }
    }
}

/**
 * Visualització interactiva d'una casella individual del tauler.
 */
@Composable
fun CellItemView(
    cell: CellModel,
    onCellClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            // clickable(): Habilita l'esdeveniment de clic només si la casella està buida (cell.symbol.isEmpty())
            .clickable(
                enabled = cell.symbol.isEmpty(),
                onClick = onCellClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val symbolColor = when (cell.symbol) {
                "X" -> MaterialTheme.colorScheme.primary
                "O" -> MaterialTheme.colorScheme.secondary
                else -> Color.Transparent
            }

            Text(
                text = cell.symbol,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = symbolColor
            )
        }
    }
}
