package org.proven.tresenrattla.model

/**
 * Model de dades que representa una casella individual del tauler de Tres en Ratlla.
 *
 * Utilitzem una 'data class' en Kotlin perquè ens proporciona automàticament
 * mètodes com equals(), hashCode(), toString() i sobretot el mètode .copy(),
 * indispensable per mantenir la immutabilitat de les dades.
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
