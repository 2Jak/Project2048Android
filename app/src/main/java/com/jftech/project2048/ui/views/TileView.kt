package com.jftech.project2048.ui.views

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.jftech.project2048.R
import com.jftech.project2048.gameengine.Game

class TileView(context: Context) : ConstraintLayout(context)
{
    private lateinit var valueTextView: TextView
    lateinit var PositionReference: Game.Position
    init
    {
        LayoutInflater.from(context).inflate(R.layout.game_tile_view, this)
    }

    fun InitWith(value: Int, position: Game.Position)
    {
        wireViews()
        PositionReference = position
        valueTextView.text = value.toString()
        this.setBackgroundColor(colorForValue(value))
    }

    private fun wireViews()
    {
        valueTextView = findViewById(R.id.game_tile_value_textview)
    }

    private fun colorForValue(value: Int): Int
    {
        return when (value)
        {
            2 -> Color.valueOf(238f/255f,247f/255f,76f/255f).toArgb()
            4 -> Color.valueOf(247f/255f,198f/255f,76f/255f).toArgb()
            8 -> Color.valueOf(247f/255f,137f/255f,76f/255f).toArgb()
            16 -> Color.valueOf(249f/255f,49f/255f,49f/255f).toArgb()
            32 -> Color.valueOf(249f/255f,44f/255f,157f/255f).toArgb()
            64 -> Color.valueOf(233f/255f,2f/255f,1f).toArgb()
            128 -> Color.valueOf(120f/255f,2f/255f,1f).toArgb()
            256 -> Color.valueOf(15f/255f,2f/255f,1f).toArgb()
            512 -> Color.valueOf(2f/255f,250f/255f,1f).toArgb()
            1024 -> Color.valueOf(2f/255f,1f,82f/255f).toArgb()
            2048 -> Color.valueOf(1f,215f/255f,0f).toArgb()
            else -> 0
        }
    }
}