package com.example.snake

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var gameView: SnakeView
    private lateinit var scoreText: TextView
    private lateinit var restartBtn: Button
    private lateinit var showArrowsCheck: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.BLACK)
        }

        scoreText = TextView(this).apply {
            text = "Score: 0"
            textSize = 20f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.DKGRAY)
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 10 }
        }

        val gameContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setBackgroundColor(Color.BLACK)
        }

        gameView = SnakeView(this)

        val controlLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, 10, 0, 10)
        }

        val arrowLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            visibility = android.view.View.GONE
        }

        val btnUp = Button(this).apply {
            text = "↑"
            setOnClickListener { gameView.setDirection(0, -1) }
        }
        val midRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val btnLeft = Button(this).apply {
            text = "←"
            setOnClickListener { gameView.setDirection(-1, 0) }
        }
        val btnCenter = Button(this).apply {
            text = "●"
            isEnabled = false
        }
        val btnRight = Button(this).apply {
            text = "→"
            setOnClickListener { gameView.setDirection(1, 0) }
        }
        midRow.addView(btnLeft)
        midRow.addView(btnCenter)
        midRow.addView(btnRight)
        val btnDown = Button(this).apply {
            text = "↓"
            setOnClickListener { gameView.setDirection(0, 1) }
        }

        arrowLayout.addView(btnUp)
        arrowLayout.addView(midRow)
        arrowLayout.addView(btnDown)

        restartBtn = Button(this).apply {
            text = "Reset"
            setOnClickListener {
                gameView.reset()
                scoreText.text = "Score: 0"
            }
        }

        showArrowsCheck = CheckBox(this).apply {
            text = "Show Arrows"
            isChecked = false
            setOnCheckedChangeListener { _, isChecked ->
                arrowLayout.visibility = if (isChecked) android.view.View.VISIBLE else android.view.View.GONE
            }
        }

        controlLayout.addView(arrowLayout)
        controlLayout.addView(restartBtn)
        controlLayout.addView(showArrowsCheck)

        gameContainer.addView(gameView)
        mainLayout.addView(scoreText)
        mainLayout.addView(gameContainer)
        mainLayout.addView(controlLayout)

        setContentView(mainLayout)

        startScoreUpdater()
    }

    private fun startScoreUpdater() {
        val thread = object : Thread() {
            override fun run() {
                while (true) {
                    Thread.sleep(100)
                    runOnUiThread {
                        scoreText.text = "Score: ${gameView.getScore()}"
                        if (!gameView.isGameRunning()) {
                            scoreText.text = "Game Over! Score: ${gameView.getScore()}"
                        }
                    }
                }
            }
        }
        thread.start()
    }
}
