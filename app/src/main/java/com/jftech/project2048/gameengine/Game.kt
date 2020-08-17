package com.jftech.project2048.gameengine

import android.util.Log
import com.jftech.project2048.ui.viewmodels.GameViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import kotlin.random.Random

class Game(private val viewModelReference: GameViewModel)
{
    enum class MoveType { Right, Left, Up, Down}
    private var oldPositions: MutableList<Position> = mutableListOf()
    private var newPositions: MutableList<Position> = mutableListOf()
    private var isGameOver: Boolean = false
    private var gameBoard: MutableList<Tile> = mutableListOf()

    fun OnSaveRequest()
    {
        viewModelReference.GetSharedPreferences().edit().putString("game_state", boardToJSON().toString()).apply()
    }

    fun OnLoadRequest()
    {
        val lastState = viewModelReference.GetSharedPreferences().getString("game_state", "0")
        if (lastState != null && lastState != "0")
            boardFromJSON(JSONArray(lastState))
        else
            gameInit()
    }

    fun OnMoved(moveType: MoveType)
    {
        moveTiles(moveType)
        try
        {
            if (!isGameOver)
                addRandomTile()
        }
        catch (ex: Exception)
        {
            gameOver()
        }
        resetMergedForTiles()
        viewModelReference.NotifyTurnFinished(gameBoard.toTypedArray(), oldPositions.toTypedArray(), newPositions.toTypedArray())
        resetEventsForAnimations()
    }

    fun OnReset()
    {
        isGameOver = false
        gameBoard = mutableListOf()
        resetEventsForAnimations()
        gameInit()
    }

    private fun gameWon()
    {
        isGameOver = true
        viewModelReference.NotifyGameWon()
    }


private fun addTile(tile: Tile)
    {
        gameBoard.add(tile)
        viewModelReference.NotifyTileAdded(tile)
    }

    private fun gameInit()
    {
        addRandomTile()
        addRandomTile()
        OnSaveRequest()
        viewModelReference.NotifyGameStarted(gameBoard.toTypedArray())
    }

    private fun changeTilePosition(tile: Tile, newPosition: Position)
    {
        gameBoard[gameBoard.indexOf(tile)].Position = newPosition
    }

    private fun resetMergedForTiles()
    {
        for (tile in gameBoard)
            tile.Merged = false
    }

    private fun moveTiles(moveType: MoveType)
    {
        sortBoardBy(moveType)
        for (tile in gameBoard.toTypedArray())
        {
            moveTile(tile, moveType)
        }
        removeZeroes()
    }

    private fun moveTile(tileToMove: Tile, moveType: MoveType)
    {
        val oldPosition = tileToMove.Position
        var newPosition = tileToMove.Position
        while (nextPosition(newPosition, moveType).InBounds())
        {
            newPosition = nextPosition(newPosition, moveType)
            val tileAtPosition = gameBoard.firstOrNull { tile -> tile.Position == newPosition && tile.Value != 0 }
            if (tileAtPosition != null)
            {
                if (tileAtPosition.Value == tileToMove.Value && !tileAtPosition.Merged && !tileToMove.Merged)
                {
                    addEventPositions(oldPosition, newPosition)
                    mergeTiles(tileToMove, tileAtPosition)
                    break
                }
                else
                {
                    newPosition = previousPosition(newPosition, moveType)
                    break
                }
            }
            if (tileToMove.Value != 0)
                changeTilePosition(tileToMove, newPosition)
        }
        if (oldPosition != newPosition && tileToMove.Value != 0)
        {
            addEventPositions(oldPosition, newPosition)
        }
    }

    private fun sortBoardBy(moveType: MoveType)
    {
        when (moveType)
        {
            MoveType.Right -> gameBoard.sortWith(compareByDescending<Tile> { it.Position.Column }.thenBy {it.Position.Row})
            MoveType.Left -> gameBoard.sortWith(compareBy<Tile> { it.Position.Column }.thenBy {it.Position.Row})
            MoveType.Up -> gameBoard.sortWith(compareBy<Tile> { it.Position.Row }.thenBy {it.Position.Column})
            MoveType.Down -> gameBoard.sortWith(compareByDescending<Tile> { it.Position.Row }.thenBy {it.Position.Column})
        }
    }

    private fun nextPosition(position: Position, moveType: MoveType): Position
    {
        return when (moveType)
        {
            MoveType.Right -> Position(position.Row, position.Column + 1)
            MoveType.Left -> Position(position.Row, position.Column - 1)
            MoveType.Up -> Position(position.Row - 1, position.Column)
            MoveType.Down -> Position(position.Row + 1, position.Column)
        }
    }

    private fun previousPosition(position: Position, moveType: MoveType): Position
    {
        return when (moveType)
        {
            MoveType.Right -> Position(position.Row, position.Column - 1)
            MoveType.Left -> Position(position.Row, position.Column + 1)
            MoveType.Up -> Position(position.Row + 1, position.Column)
            MoveType.Down -> Position(position.Row - 1, position.Column)
        }
    }

    private fun mergeTiles(a: Tile, b: Tile)
    {
        if (a.Value == b.Value)
        {
            b.Value = a.Value + b.Value
            viewModelReference.AddScore(b.Value)
            if (b.Value == 2048)
                gameWon()
            b.Merged = true
            a.Value = 0
        }
    }

    private fun addRandomTile()
    {
        val position = Position.RandomPosition(gameBoard.toTypedArray())
        val chance = Random.nextFloat()
        val value = if (chance > 0.1) 2 else 4
        val newTile = Tile(position, value)
        addTile(newTile)
    }

    private fun boardToJSON(): JSONArray
    {
        return JSONArray().apply {
            for (tile in gameBoard)
                put(tile.toJSON())
        }
    }

    private fun boardFromJSON(jsonArray: JSONArray)
    {
        val tileList: MutableList<Tile> = mutableListOf()
        for (i in 0 until jsonArray.length())
            tileList.add(Tile.fromJSON(jsonArray[i] as JSONObject))
        gameBoard = tileList
        viewModelReference.NotifyGameStarted(gameBoard.toTypedArray())
    }

    private fun removeZeroes()
    {
        gameBoard.removeAll {tile -> tile.Value == 0 }
    }

    private fun gameOver()
    {
        isGameOver = true
        viewModelReference.NotifyGameOver()
    }

    private fun addEventPositions(oldPosition: Position, newPosition: Position)
    {
        oldPositions.add(oldPosition)
        newPositions.add(newPosition)
    }

    private fun resetEventsForAnimations()
    {
        oldPositions = mutableListOf()
        newPositions = mutableListOf()
    }

    data class Tile(var Position: Position, var Value: Int, var Merged: Boolean = false)
    {
        fun toJSON(): JSONObject
        {
            return JSONObject().apply {
                put("position", Position.toJSON())
                put("value", Value)
                put("merged", Merged)
            }
        }

        companion object
        {
            fun fromJSON(json: JSONObject): Tile
            {
                return Tile(Position.fromJSON(json.getJSONObject("position")), json.getInt("value"), json.getBoolean("merged"))
            }
        }
    }
    data class Position(val Row: Int, val  Column: Int)
    {
        fun toJSON(): JSONObject
        {
            return JSONObject().apply {
                put("row", Row)
                put("col", Column)
            }
        }

        fun InBounds(): Boolean
        {
            if (this.Row in 0..3 && this.Column in 0..3)
                return true
            return false
        }

        companion object
        {
            fun fromJSON(json: JSONObject): Position
            {
                return Position(json.getInt("row"), json.getInt("col"))
            }

            fun RandomPosition(tiles: Array<Tile>): Position
            {
                var possiblePositions: MutableList<Position> = mutableListOf()
                var currentPositions: MutableList<Position> = mutableListOf()
                for (i in 0 until 4)
                    for (j in 0 until 4)
                        possiblePositions.add(Position(i,j))
                for (tile in tiles)
                    currentPositions.add(tile.Position)
                possiblePositions.removeAll(currentPositions)
                return possiblePositions.random()
            }
        }
    }

    fun UnitTest()
    {
        gameBoard = mutableListOf(Tile(Position(0,0), 1024, true),Tile(Position(0,1), 1024),Tile(Position(0,2), 8))
        viewModelReference.NotifyGameStarted(gameBoard.toTypedArray())
        var printValue: MutableList<String> = mutableListOf()
        for (tile in gameBoard) { printValue.add(tile.Value.toString())}
        Log.d("Test", printValue.toString())
        OnMoved(MoveType.Right)
        printValue = mutableListOf()
        for (tile in gameBoard) { printValue.add(tile.Value.toString())}
        Log.d("Test", printValue.toString())
    }
}