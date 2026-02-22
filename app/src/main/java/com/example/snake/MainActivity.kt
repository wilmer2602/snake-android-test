package com.example.snake

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = FrameLayout(this)

        val scoreText = TextView(this).apply {
            text = "Score: 0"
            textSize = 20f
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xFF000000.toInt())
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL
            ).apply { topMargin = 50 }
        }

        val gameView = SnakeView(this)

        val restartBtn = Button(this).apply {
            text = "Restart"
            setOnClickListener {
                gameView.reset()
                scoreText.text = "Score: 0"
            }
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            ).apply { bottomMargin = 50 }
        }

        // 方向控制按钮
        val upBtn = Button(this).apply {
            text = "↑"
            textSize = 24f
            setOnClickListener { gameView.setDirection(0, -1) }
            layoutParams = FrameLayout.LayoutParams(150, 150, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                .apply { bottomMargin = 350 }
        }

        val downBtn = Button(this).apply {
            text = "↓"
            textSize = 24f
            setOnClickListener { gameView.setDirection(0, 1) }
            layoutParams = FrameLayout.LayoutParams(150, 150, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                .apply { bottomMargin = 50 }
        }

        val leftBtn = Button(this).apply {
            text = "←"
            textSize = 24f
            setOnClickListener { gameView.setDirection(-1, 0) }
            layoutParams = FrameLayout.LayoutParams(150, 150, Gravity.BOTTOM or Gravity.START)
                .apply { 
                    bottomMargin = 200
                    marginStart = 50
                }
        }

        val rightBtn = Button(this).apply {
            text = "→"
            textSize = 24f
            setOnClickListener { gameView.setDirection(1, 0) }
            layoutParams = FrameLayout.LayoutParams(150, 150, Gravity.BOTTOM or Gravity.END)
                .apply { 
                    bottomMargin = 200
                    marginEnd = 50
                }
        }

        // 定期更新分数显示
        val updater = object : Thread() {
            override fun run() {
                while (true) {
                    Thread.sleep(100)
                    runOnUiThread {
                        scoreText.text = "Score: ${gameView.getScore()}"
                        if (!gameView.gameRunning) {
                            scoreText.text = "Game Over! Score: ${gameView.getScore()}"
                        }
                    }
                }
            }
        }
        updater.start()

        layout.addView(gameView)
        layout.addView(scoreText)
        layout.addView(upBtn)
        layout.addView(downBtn)
        layout.addView(leftBtn)
        layout.addView(rightBtn)
        layout.addView(restartBtn)

        setContentView(layout)
    }
}
