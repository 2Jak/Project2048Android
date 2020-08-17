package com.jftech.project2048.ui.gesturerecognizers

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

open class PanGestureListener(context: Context): View.OnTouchListener
{
    private val panDetector: GestureDetectorCompat

    init
    {
        panDetector = GestureDetectorCompat(context, PanGestureDetector())
    }

    override fun onTouch(view: View, motionEvent: MotionEvent?): Boolean
    {
        return panDetector.onTouchEvent(motionEvent)
    }

    open fun OnPanRight(): Boolean {return false}

    open fun OnPanLeft():Boolean {return false}

    open fun OnPanUp(): Boolean {return false}

    open fun OnPanDown(): Boolean {return false}

    inner class PanGestureDetector : GestureDetector.OnGestureListener
    {
        private var startX1: Float = 0f
        private var startY1: Float = 0f

        override fun onDown(e: MotionEvent): Boolean
        {
            return true
        }

        override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean
        {
            return true
        }
//        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean
//        {
//            //Spy check
//            if (e1 == null || e2 == null) { return false; }
//            var result = false
//            if (abs(distanceX) > abs(distanceY))
//            {
//                if (distanceX > 0)
//                    result = OnPanRight()
//                else if (distanceX < 0)
//                    result = OnPanLeft()
//            }
//            else
//            {
//                if (distanceY > 0)
//                    result = OnPanUp()
//                else if (distanceY < 0)
//                    result = OnPanDown()
//            }
//            return result
//        }



        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean
        {
            //Spy check
            if (e1 == null || e2 == null) { return false; }
            if (startX1 != e1.x || startY1 != e1.y)
            {
                startX1 = e1.x
                startY1 = e1.y
                var result = false
                val distanceX = e2.x - e1.x
                val distanceY = e2.y - e1.y
                if (abs(distanceX) > abs(distanceY)) {
                    if (distanceX > 0)
                        result = OnPanRight()
                    else if (distanceX < 0)
                        result = OnPanLeft()
                } else {
                    if (distanceY > 0)
                        result = OnPanDown()
                    else if (distanceY < 0)
                        result = OnPanUp()
                }
                return result
            }
            return false
        }

        override fun onShowPress(p0: MotionEvent?) {}

        override fun onSingleTapUp(p0: MotionEvent?): Boolean { return false}

        override fun onLongPress(p0: MotionEvent?) {}
    }
}