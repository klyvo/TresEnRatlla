package org.proven.tresenrattla.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.proven.tresenrattla.model.BoardModel

/**
 * ViewModel encarregat de gestionar l'estat de la interfície d'usuari (UI) del Tres en Ratlla.
 *
 * Hereta de [ViewModel] d'Android Lifecycle per sobreviure als canvis de configuració (ex: rotació de pantalla).
 */
class GameViewModel : ViewModel() {

    // MutableStateFlow privat per modificar l'estat internament des del ViewModel
    private val _boardState = MutableStateFlow(BoardModel.createInitialBoard())

    // StateFlow públic de només lectura exposat a la UI mitjançant asStateFlow()
    val boardState: StateFlow<BoardModel> = _boardState.asStateFlow()

    /**
     * Processa el clic d'una casella cridant a la lògica pura del Model
     * i actualitzant l'estat reactiu de la UI.
     */
    fun onCellClick(position: Int) {
        _boardState.value = _boardState.value.makeMove(position)
    }

    /**
     * Reinicia la partida tornant a l'estat inicial del tauler.
     */
    fun restartGame() {
        _boardState.value = BoardModel.createInitialBoard()
    }
}
