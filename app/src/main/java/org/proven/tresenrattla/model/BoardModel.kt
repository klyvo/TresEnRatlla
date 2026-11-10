package org.proven.tresenrattla.model

/**
 * Model de dades immutable que gestiona el tauler i la lògica de negoci del Tres en Ratlla.
 *
 * 'data class' immutable: En lloc de modificar (mutar) les propietats d'aquesta classe,
 * qualsevol canvi genera una nova instància utilitzant el mètode .copy().
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
    // companion object: Conté elements estàtics associats a la classe, similar a 'static' en Java.
    companion object {
        /**
         * Mètode de fàbrica (Factory Method) per crear l'estat inicial del tauler net.
         */
        fun createInitialBoard(): BoardModel {
            // (0..8).map { ... }: Crea la llista de les 9 caselles inicials amb símbols buits ""
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
     * Lògica pura: Aplica un moviment en la posició indicada i retorna una NOVA instància immutable
     * actualitzada mitjançant el mètode .copy().
     */
    fun makeMove(position: Int): BoardModel {
        // Validació d'estat: Si la partida ha acabat, la posició està fora de rang o la casella ja té símbol,
        // retornem la instància actual sense fer cap canvi.
        if (isGameOver || position !in cells.indices || cells[position].symbol.isNotEmpty()) {
            return this
        }

        // map { ... }: Genera una llista nova substituint únicament la casella seleccionada per una còpia (.copy)
        val updatedCells = cells.map { cell ->
            if (cell.position == position) cell.copy(symbol = currentTurn) else cell
        }

        // Comprovem les condicions de guanyador o empat amb la nova llista de caselles
        val (newWinner, newIsDraw) = checkWinner(updatedCells)
        val newIsGameOver = newWinner != null || newIsDraw
        val nextTurn = if (currentTurn == "X") "O" else "X"

        // .copy(): Mètode clau en l'arquitectura immutable. Crea un nou BoardModel clonant l'anterior
        // però modificant únicament els paràmetres especificats.
        return copy(
            cells = updatedCells,
            currentTurn = if (newIsGameOver) currentTurn else nextTurn,
            winner = newWinner,
            isDraw = newIsDraw,
            isGameOver = newIsGameOver
        )
    }

    /**
     * Lògica pura de comprovació de les 8 línies guanyadores (3 horitzontals, 3 verticals, 2 diagonals)
     * o condició d'empat (tauler ple sense guanyador).
     *
     * Retorna un Pair<String?, Boolean> amb el símbol del guanyador i si hi ha empat.
     */
    private fun checkWinner(boardCells: List<CellModel>): Pair<String?, Boolean> {
        val winningLines = listOf(
            // Combinacions horitzontals (files)
            listOf(0, 1, 2),
            listOf(3, 4, 5),
            listOf(6, 7, 8),
            // Combinacions verticals (columnes)
            listOf(0, 3, 6),
            listOf(1, 4, 7),
            listOf(2, 5, 8),
            // Combinacions diagonals
            listOf(0, 4, 8),
            listOf(2, 4, 6)
        )

        for (line in winningLines) {
            val (a, b, c) = line
            val symbolA = boardCells[a].symbol
            val symbolB = boardCells[b].symbol
            val symbolC = boardCells[c].symbol

            // Si les tres posicions contenen el mateix símbol no buit, hi ha guanyador
            if (symbolA.isNotEmpty() && symbolA == symbolB && symbolB == symbolC) {
                return Pair(symbolA, false)
            }
        }

        // all { ... }: Comprova si totes les caselles tenen un símbol (tauler ple)
        val isFull = boardCells.all { it.symbol.isNotEmpty() }
        if (isFull) {
            return Pair(null, true) // Empat
        }

        return Pair(null, false) // La partida continua
    }
}
