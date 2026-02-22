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
        layout.addView(restartBtn)

        setContentView(layout)
    }
}
