package com.jftech.project2048.ui.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.SharedPreferencesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jftech.project2048.gameengine.Game
import com.jftech.project2048.gameengine.IGamePresentor
import com.jftech.project2048.ui.views.GameFragment

class GameViewModel(private val gamePresenter: IGamePresentor): ViewModel()
{
    private val gameEngine = Game(this)
    private var score: MutableLiveData<Int> = MutableLiveData()
    val Score: LiveData<Int> = score

    init
    {
        score.value = 0
    }

    fun GetSharedPreferences(): SharedPreferences
    {
        return (gamePresenter as GameFragment).context!!.getSharedPreferences("com.jftech.project2048", Context.MODE_PRIVATE)
    }

    fun SendSaveRequest()
    {
        GetSharedPreferences().edit().putInt("game_score", score.value!!).apply()
        gameEngine.OnSaveRequest()
    }

    fun SendLoadRequest()
    {
        score.value = GetSharedPreferences().getInt("game_score", 0)
        gameEngine.OnLoadRequest()
    }

    fun AddScore(value: Int)
    {
        score.postValue(score.value!! + value)
    }

    fun NotifyTurnFinished(tiles: Array<Game.Tile>, oldPositions: Array<Game.Position>, newPositions: Array<Game.Position>)
    {
        gamePresenter.OnTurnFinished(tiles, oldPositions, newPositions)
    }

    fun NotifyGameStarted(tiles: Array<Game.Tile>)
    {
        gamePresenter.OnGameStart(tiles)
    }

    fun NotifyPanTouch(moveType: Game.MoveType)
    {
        gameEngine.OnMoved(moveType)
    }

    fun NotifyGameOver()
    {
        gamePresenter.OnGameOver()
    }

    fun NotifyTileAdded(tile: Game.Tile)
    {

    }

    fun NotifyGameReset()
    {
        score.value = 0
        gameEngine.OnReset()
    }

    fun NotifyGameWon()
    {
        gamePresenter.OnGameWon()
    }
}