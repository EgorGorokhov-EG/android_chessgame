package com.eg.chessgame

import kotlin.math.sign

class Presenter(private val view: ChessboardInterface) {
    
    private var game = Game()

    // Variable of check state
    // 0: no check
    // 1: white has to move his king
    // -1: black has to move his king
    private var isCheck = 0

    private var lastAvailableMoves: List<Pair<Int, Int>> = listOf()

    fun cancelMove() {
        game.cancelMove()
        view.redrawPieces(game.playerWhite.pieces, game.playerBlack.pieces)
    }

    fun restartGame() {
        // Init new Game object with initial state of the game
        game = Game()
        // ANd redraw pieces on the board
        view.redrawPieces(game.playerWhite.pieces, game.playerBlack.pieces)
    }

    fun handleInput(currentPosition: Pair<Int, Int>?, previousPosition: Pair<Int, Int>?) {

        val pieceNum = game.board[currentPosition!!.first][currentPosition.second]
        val currentPlayerNum = game.currentPlayerColor

         /* Handle the logic:
            -if chosen piece of current player's side -> tell view to select it and
             display available moves

            -if chosen pos is one of the available moves for previous pos -> make move for
             piece on previous pos

            -else -> clear all selections and list of available positions */
        when {
            (pieceNum.sign == currentPlayerNum) -> selectPieceToMove(pieceNum, currentPlayerNum)
            (lastAvailableMoves.contains(currentPosition)) -> movePiece(previousPosition!!, currentPosition)
            else -> view.clearSelection()
        }
    }

    private fun selectPieceToMove(pieceNum: Int, currentPlayerNum: Int) {
        lastAvailableMoves = game.gameUtils.getAvailableMovesForPiece(pieceNum, game.players[currentPlayerNum])
        view.displayAvailableMoves(lastAvailableMoves)
    }

    private fun movePiece(piecePos: Pair<Int, Int>, movePos: Pair<Int, Int>) {
        // If game is already finished, just display winner (done to prevent moving pieces after game finished, but before restart)
        // else make move and display winner if there is
        if (game.isEnd != 0) {
            view.displayWinner(game.isEnd)
        } else {
            // Tell game to make move for current player
            game.makeMove(piecePos, movePos)
            // Clear available moves
            lastAvailableMoves = listOf()
            // Tell View to clear selection and available moves on board
            view.clearSelection()
            // Tell View to redraw pieces on board
            view.redrawPieces(game.playerWhite.pieces, game.playerBlack.pieces)

            if (game.isEnd != 0) {
                view.displayWinner(game.isEnd)
            }
        }
    }



    // Interface for interaction with View(Activity)
    interface ChessboardInterface {
        fun displayAvailableMoves(movesCoordinates: List<Pair<Int, Int>>)
        fun sendInputToPresenter(currentPosition: Pair<Int, Int>?, previousPosition: Pair<Int, Int>?)
        fun clearSelection()
        fun redrawPieces(whitePieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>,
                         blackPieces: MutableMap<Int, Pair<String, Pair<Int, Int>>>)
        fun displayWinner(player: Int)
    }
}