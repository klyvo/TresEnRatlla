package org.proven.tresenrattla.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.proven.tresenrattla.model.BoardModel

/**
 * ViewModel encarregat de gestionar l'estat de la interfície d'usuari (UI) del Tres en Ratlla.
 *
 * Hereta de [ViewModel] d'Android Lifecycle per persistir i sobreviure als canvis de configuració
 * de la pantalla (com la rotació del dispositiu).
 */
class GameViewModel : ViewModel() {

    // MutableStateFlow: Flux d'estat reactiu i mutable de Kotlin Coroutines.
    // Es manté privat per evitar que la vista puga modificar l'estat directament sense passar pel ViewModel.
    private val _boardState = MutableStateFlow(BoardModel.createInitialBoard())

    // StateFlow & asStateFlow(): Exposa una versió de només lectura del MutableStateFlow a la UI.
    // D'aquesta manera s'assegura l'encapsulament i el flux de dades unidireccional (UDF).
    val boardState: StateFlow<BoardModel> = _boardState.asStateFlow()

    /**
     * Processa el clic en una casella cridant al mètode d'execució pura del Model (makeMove)
     * i emetent la nova instància immutable del tauler al MutableStateFlow.
     */
    fun onCellClick(position: Int) {
        _boardState.value = _boardState.value.makeMove(position)
    }

    /**
     * Reinicia la partida restablint l'estat al tauler net inicial de BoardModel.
     */
    fun restartGame() {
        _boardState.value = BoardModel.createInitialBoard()
    }
}
