package com.example.snake

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var gameView: SnakeView
    private lateinit var scoreText: TextView
    private lateinit var highScoreText: TextView
    private lateinit var speedText: TextView
    private lateinit var timeText: TextView
    private lateinit var modeText: TextView
    private var highScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#1a1a1a"))
        }

        // 顶部信息栏
        val infoLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#2d2d2d"))
            setPadding(16, 12, 16, 12)
        }

        val infoRow1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        scoreText = TextView(this).apply {
            text = "分数: 0"
            textSize = 18f
            setTextColor(Color.parseColor("#00ff00"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        highScoreText = TextView(this).apply {
            text = "最高: 0"
            textSize = 18f
            setTextColor(Color.parseColor("#ffd700"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        speedText = TextView(this).apply {
            text = "速度: 1x"
            textSize = 18f
            setTextColor(Color.parseColor("#00bfff"))
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        infoRow1.addView(scoreText)
        infoRow1.addView(highScoreText)
        infoRow1.addView(speedText)

        val infoRow2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 6, 0, 0)
        }

        timeText = TextView(this).apply {
            text = "时间: 0s"
            textSize = 16f
            setTextColor(Color.parseColor("#ff69b4"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        modeText = TextView(this).apply {
            text = "模式: 普通"
            textSize = 16f
            setTextColor(Color.parseColor("#9370db"))
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        infoRow2.addView(timeText)
        infoRow2.addView(modeText)

        infoLayout.addView(infoRow1)
        infoLayout.addView(infoRow2)

        // 游戏区域
        val gameContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setBackgroundColor(Color.BLACK)
        }

        gameView = SnakeView(this)

        // 底部控制区
        val controlLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#2d2d2d"))
            setPadding(16, 20, 16, 20)
        }

        // 功能按钮行（顶部）
        val functionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 20 }
        }

        val restartBtn = createSmallButton("🔄", Color.parseColor("#ff6b6b")) {
            gameView.reset()
            updateScore()
        }

        val pauseBtn = createSmallButton("⏸", Color.parseColor("#4ecdc4")) {
            gameView.togglePause()
        }

        val speedBtn = createSmallButton("⚡", Color.parseColor("#95e1d3")) {
            gameView.increaseSpeed()
            updateSpeed()
        }

        val modeBtn = createSmallButton("♾", Color.parseColor("#f38181")) {
            gameView.toggleEndlessMode()
            updateMode()
        }

        functionRow.addView(restartBtn)
        functionRow.addView(pauseBtn)
        functionRow.addView(speedBtn)
        functionRow.addView(modeBtn)

        // 方向键区域（大按钮）
        val arrowContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        val arrowRow1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        val arrowRow2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        val arrowRow3 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        val btnUp = createArrowButton("↑") { gameView.setDirection(0, -1) }
        val btnLeft = createArrowButton("←") { gameView.setDirection(-1, 0) }
        val btnRight = createArrowButton("→") { gameView.setDirection(1, 0) }
        val btnDown = createArrowButton("↓") { gameView.setDirection(0, 1) }

        arrowRow1.addView(btnUp)
        arrowRow2.addView(btnLeft)
        arrowRow2.addView(createSpacerButton())
        arrowRow2.addView(btnRight)
        arrowRow3.addView(btnDown)

        arrowContainer.addView(arrowRow1)
        arrowContainer.addView(arrowRow2)
        arrowContainer.addView(arrowRow3)

        controlLayout.addView(functionRow)
        controlLayout.addView(arrowContainer)

        gameContainer.addView(gameView)
        mainLayout.addView(infoLayout)
        mainLayout.addView(gameContainer)
        mainLayout.addView(controlLayout)

        setContentView(mainLayout)

        startScoreUpdater()
    }

    private fun createArrowButton(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            textSize = 36f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#3d3d3d"))
            layoutParams = LinearLayout.LayoutParams(140, 140).apply {
                setMargins(6, 6, 6, 6)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun createSpacerButton(): Button {
        return Button(this).apply {
            text = ""
            isEnabled = false
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = LinearLayout.LayoutParams(140, 140).apply {
                setMargins(6, 6, 6, 6)
            }
        }
    }

    private fun createSmallButton(text: String, color: Int, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            textSize = 20f
            setTextColor(Color.WHITE)
            setBackgroundColor(color)
            layoutParams = LinearLayout.LayoutParams(80, 80).apply {
                setMargins(6, 0, 6, 0)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun updateScore() {
        val score = gameView.getScore()
        scoreText.text = "分数: $score"
        if (score > highScore) {
            highScore = score
            highScoreText.text = "最高: $highScore"
        }
    }

    private fun updateSpeed() {
        speedText.text = "速度: ${gameView.getSpeedLevel()}x"
    }

    private fun updateMode() {
        modeText.text = if (gameView.isEndlessMode()) "模式: 无尽" else "模式: 普通"
    }

    private fun startScoreUpdater() {
        val thread = object : Thread() {
            override fun run() {
                while (true) {
                    Thread.sleep(100)
                    runOnUiThread {
                        updateScore()
                        updateSpeed()
                        updateMode()
                        timeText.text = "时间: ${gameView.getElapsedTime()}s"
                        if (!gameView.isGameRunning()) {
                            scoreText.setTextColor(Color.parseColor("#ff6b6b"))
                        } else {
                            scoreText.setTextColor(Color.parseColor("#00ff00"))
                        }
                    }
                }
            }
        }
        thread.start()
    }
}
