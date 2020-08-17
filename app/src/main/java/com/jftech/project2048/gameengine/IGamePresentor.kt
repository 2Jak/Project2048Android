package com.jftech.project2048.gameengine

interface IGamePresentor
{
    fun OnGameStart(tiles: Array<Game.Tile>)
    fun OnTurnFinished(tiles: Array<Game.Tile>, oldPositions: Array<Game.Position>, newPositions: Array<Game.Position>)
    fun OnGameWon()
    fun OnGameOver()
}