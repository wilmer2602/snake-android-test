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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 主布局：垂直
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#2E8B57")) // 海洋绿背景
        }

        // 顶部分数栏
        scoreText = TextView(this).apply {
            text = "🐍 分数: 0"
            textSize = 24f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#006400")) // 深绿
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }

        // 游戏区域容器
        val gameContainer = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
            setBackgroundColor(Color.BLACK)
        }

        gameView = SnakeView(this)

        // 底部控制区：箭头 + 重置
        val controlLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(8, 12, 8, 12)
            setBackgroundColor(Color.parseColor("#556B2F")) // 暗橄榄绿
        }

        // 箭头按钮组
        val arrowLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 20 }
        }

        // 上行
        val row1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        // 中行
        val row2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
        }
        // 下行
        val row3 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_HORIZONTAL
        }

        val btnUp = createArrowButton("↑") { gameView.setDirection(0, -1) }
        val btnLeft = createArrowButton("←") { gameView.setDirection(-1, 0) }
        val btnCenter = Button(this).apply {
            text = "●"
            setTextColor(Color.GRAY)
            isEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { leftMargin = 20; rightMargin = 20 }
        }
        val btnRight = createArrowButton("→") { gameView.setDirection(1, 0) }
        val btnDown = createArrowButton("↓") { gameView.setDirection(0, 1) }

        row1.addView(btnUp)
        row2.addView(btnLeft)
        row2.addView(btnCenter)
        row2.addView(btnRight)
        row3.addView(btnDown)

        arrowLayout.addView(row1)
        arrowLayout.addView(row2)
        arrowLayout.addView(row3)

        // 重置按钮
        val restartBtn = Button(this).apply {
            text = "重置"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#8B4513")) // 鞍褐色
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { leftMargin = 30 }
            setOnClickListener {
                gameView.reset()
                scoreText.text = "🐍 分数: 0"
            }
        }

        controlLayout.addView(arrowLayout)
        controlLayout.addView(restartBtn)

        // 组装
        gameContainer.addView(gameView)
        mainLayout.addView(scoreText)
        mainLayout.addView(gameContainer)
        mainLayout.addView(controlLayout)

        setContentView(mainLayout)

        startScoreUpdater()
    }

    private fun createArrowButton(text: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            this.text = text
            setTextSize(24f)
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#4682B4")) // 钢蓝色
            layoutParams = LinearLayout.LayoutParams(
                120,
                120
            ).apply { margin = 8 }
            setOnClickListener { onClick() }
        }
    }

    private fun startScoreUpdater() {
        val thread = object : Thread() {
            override fun run() {
                while (true) {
                    Thread.sleep(100)
                    runOnUiThread {
                        scoreText.text = "🐍 分数: ${gameView.getScore()}"
                        if (!gameView.isGameRunning()) {
                            scoreText.text = "💀 游戏结束! 分数: ${gameView.getScore()}"
                        }
                    }
                }
            }
        }
        thread.start()
    }
}
