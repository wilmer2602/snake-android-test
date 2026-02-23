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
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#2d2d2d"))
            setPadding(24, 32, 24, 32)
            gravity = Gravity.CENTER
        }

        // 左侧功能按钮组
        val leftButtons = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { rightMargin = 32 }
        }

        val restartBtn = createSideButton(R.drawable.btn_restart, Color.parseColor("#ff6b6b")) {
            gameView.reset()
            updateScore()
        }
        val pauseBtn = createSideButton(R.drawable.btn_pause, Color.parseColor("#4ecdc4")) {
            gameView.togglePause()
        }
        val slowBtn = createSideButton(R.drawable.btn_slow, Color.parseColor("#ffa726")) {
            gameView.decreaseSpeed()
            updateSpeed()
        }

        leftButtons.addView(restartBtn)
        leftButtons.addView(pauseBtn)
        leftButtons.addView(slowBtn)

        // 中间方向键区域
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

        // 右侧功能按钮组
        val rightButtons = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { leftMargin = 32 }
        }

        val speedBtn = createSideButton(R.drawable.btn_fast, Color.parseColor("#95e1d3")) {
            gameView.increaseSpeed()
            updateSpeed()
        }
        val modeBtn = createSideButton(R.drawable.btn_endless, Color.parseColor("#f38181")) {
            gameView.toggleEndlessMode()
            updateMode()
        }
        val autoBtn = createSideButton(R.drawable.btn_auto, Color.parseColor("#9c27b0")) {
            gameView.toggleAutoWalk()
        }

        rightButtons.addView(speedBtn)
        rightButtons.addView(modeBtn)
        rightButtons.addView(autoBtn)

        controlLayout.addView(leftButtons)
        controlLayout.addView(arrowContainer)
        controlLayout.addView(rightButtons)

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
            textSize = 64f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#4a4a4a"))
            setPadding(0, 0, 0, 0)
            elevation = 8f
            stateListAnimator = null
            layoutParams = LinearLayout.LayoutParams(150, 150).apply {
                setMargins(10, 10, 10, 10)
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
            textSize = 48f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#ff9800"))
            setPadding(0, 0, 0, 0)
            elevation = 8f
            stateListAnimator = null
            layoutParams = LinearLayout.LayoutParams(150, 150).apply {
                setMargins(10, 10, 10, 10)
            }
            setOnClickListener { 
                alpha = 0.7f
                postDelayed({ alpha = 1f }, 100)
                onClick() 
            }
        }
    }

    private fun createSideButton(drawableRes: Int, color: Int, onClick: () -> Unit): Button {
        return Button(this).apply {
            setBackgroundColor(color)
            setPadding(16, 16, 16, 16)
            elevation = 8f
            stateListAnimator = null
            layoutParams = LinearLayout.LayoutParams(130, 130).apply {
                setMargins(10, 18, 10, 18)
            }
            
            // 设置图标
            val drawable = resources.getDrawable(drawableRes, null)
            drawable.setBounds(0, 0, 96, 96)
            setCompoundDrawables(null, drawable, null, null)
            
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
