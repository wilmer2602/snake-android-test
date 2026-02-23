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
            setPadding(16, 16, 16, 16)
        }

        // 顶部功能按钮行
        val functionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }

        val restartBtn = createFunctionButton("🔄", Color.parseColor("#ff6b6b")) {
            gameView.reset()
            updateScore()
        }
        val pauseBtn = createFunctionButton("⏸", Color.parseColor("#4ecdc4")) {
            gameView.togglePause()
        }
        val slowBtn = createFunctionButton("🐌", Color.parseColor("#ffa726")) {
            gameView.decreaseSpeed()
            updateSpeed()
        }
        val speedBtn = createFunctionButton("⚡", Color.parseColor("#95e1d3")) {
            gameView.increaseSpeed()
            updateSpeed()
        }
        val modeBtn = createFunctionButton("♾", Color.parseColor("#f38181")) {
            gameView.toggleEndlessMode()
            updateMode()
        }

        functionRow.addView(restartBtn)
        functionRow.addView(pauseBtn)
        functionRow.addView(slowBtn)
        functionRow.addView(speedBtn)
        functionRow.addView(modeBtn)

        // 方向键区域
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

        val btnUp = createArrowButton("↑") { 
            gameView.setDirection(0, -1)
            gameView.disableAutoWalk()
        }
        val btnLeft = createArrowButton("←") { 
            gameView.setDirection(-1, 0)
            gameView.disableAutoWalk()
        }
        val btnRight = createArrowButton("→") { 
            gameView.setDirection(1, 0)
            gameView.disableAutoWalk()
        }
        val btnDown = createArrowButton("↓") { 
            gameView.setDirection(0, 1)
            gameView.disableAutoWalk()
        }
        val btnCenter = createCenterButton("🤖") {
            gameView.toggleAutoWalk()
        }

        arrowRow1.addView(btnUp)
        arrowRow2.addView(btnLeft)
        arrowRow2.addView(btnCenter)
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
            textSize = 56f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#4a4a4a"))
            setPadding(0, 0, 0, 0)
            elevation = 8f
            stateListAnimator = null
            layoutParams = LinearLayout.LayoutParams(130, 130).apply {
                setMargins(6, 6, 6, 6)
            }
            setOnClickListener { 
                alpha = 0.7f
                postDelayed({ alpha = 1f }, 100)
                onClick() 
            }
        }
    }

    private fun createCenterButton(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            textSize = 40f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#ff9800"))
            setPadding(0, 0, 0, 0)
            elevation = 8f
            stateListAnimator = null
            layoutParams = LinearLayout.LayoutParams(130, 130).apply {
                setMargins(6, 6, 6, 6)
            }
            setOnClickListener { 
                alpha = 0.7f
                postDelayed({ alpha = 1f }, 100)
                onClick() 
            }
        }
    }

    private fun createFunctionButton(text: String, color: Int, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundColor(color)
            setPadding(0, 0, 0, 0)
            elevation = 8f
            stateListAnimator = null
            layoutParams = LinearLayout.LayoutParams(90, 90).apply {
                setMargins(8, 0, 8, 0)
            }
            setOnClickListener { 
                alpha = 0.7f
                postDelayed({ alpha = 1f }, 100)
                onClick() 
            }
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
        val speed = gameView.getSpeedMultiplier()
        speedText.text = when {
            speed >= 1.0 -> "速度: ${speed.toInt()}x"
            else -> "速度: ${speed}x"
        }
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
