package org.proven.tresenrattla.model

/**
 * Model de dades immutable que gestiona el tauler i la lògica de negoci del Tres en Ratlla.
 *
 * @param cells Llista de les 9 caselles del tauler.
 * @param currentTurn Jugador que té el torn actual ("X" o "O").
 * @param winner Jugador guanyador ("X", "O") o null si no hi ha guanyador encara.
 * @param isDraw True si la partida ha acabat en empat.
 * @param isGameOver True si la partida ha finalitzat (per victòria o empat).
 */
data class BoardModel(
    val cells: List<CellModel>,
    val currentTurn: String = "X",
    val winner: String? = null,
    val isDraw: Boolean = false,
    val isGameOver: Boolean = false
) {
    companion object {
        /**
         * Mètode de fàbrica per crear l'estat inicial del tauler net.
         */
        fun createInitialBoard(): BoardModel {
            val initialCells = (0..8).map { pos ->
                CellModel(id = pos, position = pos, symbol = "")
            }
            return BoardModel(
                cells = initialCells,
                currentTurn = "X",
                winner = null,
                isDraw = false,
                isGameOver = false
            )
        }
    }

    /**
     * Aplica un moviment en la posició indicada i retorna una nova instància immutable
     * actualitzada mitjançant el mètode .copy().
     */
    fun makeMove(position: Int): BoardModel {
        // Validem que la partida no hagi acabat, la posició sigui vàlida i la casella estigui buida
        if (isGameOver || position !in cells.indices || cells[position].symbol.isNotEmpty()) {
            return this
        }

        // Actualitzem la casella seleccionada creant una llista nova amb la casella modificada
        val updatedCells = cells.map { cell ->
            if (cell.position == position) cell.copy(symbol = currentTurn) else cell
        }

        // Comprovem si hi ha guanyador o empat
        val (newWinner, newIsDraw) = checkWinner(updatedCells)
        val newIsGameOver = newWinner != null || newIsDraw
        val nextTurn = if (currentTurn == "X") "O" else "X"

        // Retornem la nova instància immutable actualitzada amb .copy()
        return copy(
            cells = updatedCells,
            currentTurn = if (newIsGameOver) currentTurn else nextTurn,
            winner = newWinner,
            isDraw = newIsDraw,
            isGameOver = newIsGameOver
        )
    }

    /**
     * Comprova les combinacions guanyadores (3 en línia horitzontal, vertical o diagonal)
     * o si s'ha arribat a un empat.
     */
    private fun checkWinner(boardCells: List<CellModel>): Pair<String?, Boolean> {
        val winningLines = listOf(
            // Horitzontals
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            // Verticals
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            // Diagonals
            listOf(0, 4, 8),
            listOf(2, 4, 6)
        )

        for (line in winningLines) {
            val (a, b, c) = line
            val symbolA = boardCells[a].symbol
            val symbolB = boardCells[b].symbol
            val symbolC = boardCells[c].symbol

            if (symbolA.isNotEmpty() && symbolA == symbolB && symbolB == symbolC) {
                return Pair(symbolA, false) // Hi ha guanyador
            }
        }

        // Si totes les caselles tenen símbol i no hi ha guanyador, és un empat
        val isFull = boardCells.all { it.symbol.isNotEmpty() }
        if (isFull) {
            return Pair(null, true) // Empat
        }

        return Pair(null, false) // La partida continua
    }
}
