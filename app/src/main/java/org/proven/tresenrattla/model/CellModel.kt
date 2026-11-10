package org.proven.tresenrattla.model

/**
 * Model de dades que representa una casella individual del tauler de Tres en Ratlla.
 *
 * @param id Identificador únic de la casella.
 * @param position Posició dins de la graella de 3x3 (índex del 0 al 8).
 * @param symbol Símbol col·locat a la casella: "X", "O" o cadena buida "" si està lliure.
 */
data class CellModel(
    val id: Int,
    val position: Int,
    val symbol: String = ""
)
