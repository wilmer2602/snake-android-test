package com.example.snake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class SnakeView(context: Context) : View(context) {
    private val paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 20f
        isAntiAlias = true
    }
    private val foodPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 20f
        isAntiAlias = true
    }

    private val gridSize = 20
    private val cols = 20
    private val rows = 30

    private var snake = mutableListOf<Pair<Int, Int>>().apply {
        add(Pair(10, 10))
    }
    private var direction = Pair(1, 0) // (dx, dy)
    private var food = generateFood()
    private var score = 0

    init {
        // 简单的游戏循环
        val thread = object : Thread() {
            override fun run() {
                while (true) {
                    Thread.sleep(300)
                    move()
                    postInvalidate()
                }
            }
        }
        thread.start()
    }

    private fun generateFood(): Pair<Int, Int> {
        return Pair(
            (0 until cols).random(),
            (0 until rows).random()
        )
    }

    private fun move() {
        val head = snake.first()
        val newHead = Pair(head.first + direction.first, head.second + direction.second)

        // 撞墙检测
        if (newHead.first < 0 || newHead.first >= cols ||
            newHead.second < 0 || newHead.second >= rows) {
            reset()
            return
        }

        // 撞自己检测
        if (snake.contains(newHead)) {
            reset()
            return
        }

        snake.add(0, newHead)

        // 吃食物
        if (newHead == food) {
            score++
            food = generateFood()
        } else {
            snake.removeAt(snake.size - 1)
        }
    }

    fun reset() {
        snake.clear()
        snake.add(Pair(10, 10))
        direction = Pair(1, 0)
        food = generateFood()
        score = 0
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellSize = width.coerceAtMost(height) / gridSize

        // 绘制蛇
        for ((x, y) in snake) {
            canvas.drawRect(
                (x * cellSize).toFloat(),
                (y * cellSize).toFloat(),
                ((x + 1) * cellSize).toFloat(),
                ((y + 1) * cellSize).toFloat(),
                paint
            )
        }

        // 绘制食物
        canvas.drawRect(
            (food.first * cellSize).toFloat(),
            (food.second * cellSize).toFloat(),
            ((food.first + 1) * cellSize).toFloat(),
            ((food.second + 1) * cellSize).toFloat(),
            foodPaint
        )
    }
}
