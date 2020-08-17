package com.jftech.project2048.ui.views

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.jftech.project2048.R
import com.jftech.project2048.gameengine.Game
import com.jftech.project2048.gameengine.IGamePresentor
import com.jftech.project2048.ui.gesturerecognizers.PanGestureListener
import com.jftech.project2048.ui.viewmodels.GameViewModel
import kotlinx.android.synthetic.main.game_fragment.view.*

class GameFragment: Fragment(), IGamePresentor
{
    private var firstRun = true
    private var tileViewSize: Int = 0
    private lateinit var tileContainer: ConstraintLayout
    private lateinit var gameViewModel: GameViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.game_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        tileContainer = view.findViewById(R.id.game_fragment_container)
        view.post {
            tileViewSize = (view.width / 4) - 2
            gameViewModel = GameViewModel(this)
            setGamePanEvents(view)
            gameViewModel.Score.observe(this, Observer { (context!! as GameActivity).OnScoreUpdated(gameViewModel.Score.value!!) })
            sendLoadRequest()
            firstRun = false
        }
    }

    override fun OnTurnFinished(tiles: Array<Game.Tile>, oldPositions: Array<Game.Position>, newPositions: Array<Game.Position>)
    {
        val animator = AnimatorSet()
        animator.playTogether(buildAnimations(oldPositions, newPositions))
        animator.duration = 500
        setAnimatorEvents(animator, tiles)
        animator.start()
    }

    override fun OnGameStart(tiles: Array<Game.Tile>)
    {
        layoutViews(tiles)
    }

    override fun OnGameWon()
    {
        (context!! as GameActivity).OnGameWon()
    }

    override fun OnGameOver()
    {
        (context!! as GameActivity).OnGameOver()
    }

    override fun onStart()
    {
        super.onStart()
        if (!firstRun)
            sendLoadRequest()
    }

    override fun onStop()
    {
        sendSaveRequest()
        super.onStop()
    }

    override fun onDestroy()
    {
        sendSaveRequest()
        super.onDestroy()
    }

    fun NotifyGameReset()
    {
        gameViewModel.NotifyGameReset()
    }

    private fun sendSaveRequest()
    {
        gameViewModel.SendSaveRequest()
    }

    private fun sendLoadRequest()
    {
        gameViewModel.SendLoadRequest()
    }

    private fun layoutViews(tiles: Array<Game.Tile>)
    {
        tileContainer.removeAllViews()
        for (tile in tiles)
        {
            val tileView = TileView(context!!)
            val params = ConstraintLayout.LayoutParams(tileViewSize, tileViewSize)
            val newX = (tileViewSize + 2).toFloat() * tile.Position.Column
            val newY = (tileViewSize + 2).toFloat() * tile.Position.Row
            tileView.apply {
                layoutParams = params
                x = newX
                y = newY
            }
            tileView.InitWith(tile.Value, tile.Position)
            tileContainer.addView(tileView)
        }
    }

    private fun buildAnimations(oldPositions: Array<Game.Position>, newPositions: Array<Game.Position>): MutableList<Animator>
    {
        val animationsCount = oldPositions.size
        val animationList: MutableList<Animator> = mutableListOf()
        for (i in 0 until animationsCount)
        {
            val changeInRow = oldPositions[i].Column == newPositions[i].Column
            val tileView = findTileViewByPosition(oldPositions[i])
            val propertyName = if (changeInRow) "translationY" else "translationX"
            val animationForView = buildAnimationForView(tileView, propertyName, if (changeInRow) newPositions[i].Row else newPositions[i].Column)
            animationList.add(animationForView)
        }
        return animationList
    }

    private fun buildAnimationForView(tileView: TileView, propertyName: String, newValue: Int): ObjectAnimator
    {
        val newPosition = (tileViewSize + 2).toFloat() * newValue
        return ObjectAnimator.ofFloat(tileView, propertyName, newPosition)
    }

    private fun findTileViewByPosition(position: Game.Position): TileView
    {
        return tileContainer[tileContainer.children.indexOfFirst { tileView -> (tileView as TileView).PositionReference ==  position}] as TileView
    }

    private fun setGamePanEvents(view: View)
    {
        view.setOnTouchListener(object: PanGestureListener(context!!)
        {
            override fun OnPanLeft(): Boolean
            {
                gameViewModel.NotifyPanTouch(Game.MoveType.Left)
                return true
            }

            override fun OnPanRight(): Boolean
            {
                gameViewModel.NotifyPanTouch(Game.MoveType.Right)
                return true
            }

            override fun OnPanDown(): Boolean
            {
                gameViewModel.NotifyPanTouch(Game.MoveType.Down)
                return true
            }

            override fun OnPanUp(): Boolean
            {
                gameViewModel.NotifyPanTouch(Game.MoveType.Up)
                return true
            }
        })
    }

    private fun setAnimatorEvents(animator: AnimatorSet, tiles: Array<Game.Tile>)
    {
        animator.addListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                layoutViews(tiles)
            }
        })
    }
}