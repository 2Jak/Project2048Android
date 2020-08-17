package com.jftech.project2048.ui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.jftech.project2048.R

class GameActivity : AppCompatActivity()
{
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction
    private lateinit var gameFragment: GameFragment
    private lateinit var resetButton: AppCompatButton
    private lateinit var scoreTextView: AppCompatTextView
    private lateinit var masterContainer: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        wireViews()
        fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.game_fragment, gameFragment)
        fragmentTransaction.commit()
        resetButton.setOnClickListener {
            gameFragment.NotifyGameReset()
        }
    }

    fun OnScoreUpdated(score: Int)
    {
        scoreTextView.text = score.toString()
    }

    fun OnGameWon()
    {
        addEndGameScreen(true)
    }

    fun OnGameOver()
    {
        addEndGameScreen(false)
    }

    private fun addEndGameScreen(gameWon: Boolean)
    {
        val params: ConstraintLayout.LayoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        params.apply {
            topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        }
        val endGameScreen: View = View.inflate(this, if (gameWon) R.layout.game_won_overlay else R.layout.game_over_overlay, null)
        endGameScreen.apply {
            layoutParams = params
        }
        masterContainer.addView(endGameScreen)
        endGameScreen.setOnClickListener {
            gameFragment.NotifyGameReset()
            masterContainer.removeView(endGameScreen) }
    }

    private fun wireViews()
    {
        resetButton = findViewById(R.id.game_activity_reset_button)
        scoreTextView = findViewById(R.id.game_activity_score_textview)
        masterContainer = findViewById(R.id.app_container)
        gameFragment = GameFragment()
        fragmentManager = supportFragmentManager
    }
}