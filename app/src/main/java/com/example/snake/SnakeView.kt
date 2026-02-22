package com.example.snake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
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
    private var direction = Pair(1, 0) // (dx, dy): 1=right, -1=left, 1=down, -1=up
    private var food = generateFood()
    private var score = 0
    private var gameRunning = true

    // 触摸起始坐标
    private var startX = 0f
    private var startY = 0f

    init {
        // 游戏循环
        val thread = object : Thread() {
            override fun run() {
                while (true) {
                    Thread.sleep(300)
                    if (gameRunning) {
                        move()
                        postInvalidate()
                    }
                }
            }
        }
        thread.start()
    }

    private fun generateFood(): Pair<Int, Int> {
        var newFood: Pair<Int, Int>
        do {
            newFood = Pair(
                (0 until cols).random(),
                (0 until rows).random()
            )
        } while (snake.contains(newFood))
        return newFood
    }

    private fun move() {
        val head = snake.first()
        val newHead = Pair(head.first + direction.first, head.second + direction.second)

        // 撞墙
        if (newHead.first < 0 || newHead.first >= cols ||
            newHead.second < 0 || newHead.second >= rows) {
            gameOver()
            return
        }

        // 撞自己
        if (snake.contains(newHead)) {
            gameOver()
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

    private fun gameOver() {
        gameRunning = false
        // 可以触发回调到 Activity
    }

    fun reset() {
        snake.clear()
        snake.add(Pair(10, 10))
        direction = Pair(1, 0)
        food = generateFood()
        score = 0
        gameRunning = true
    }

    fun getScore(): Int = score

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                val diffX = endX - startX
                val diffY = endY - startY

                // 判断滑动方向（最小滑动距离 50px）
                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 50) {
                    // 水平滑动
                    if (diffX > 0 && direction.first != -1) {
                        direction = Pair(1, 0) // 右
                    } else if (diffX < 0 && direction.first != 1) {
                        direction = Pair(-1, 0) // 左
                    }
                } else if (Math.abs(diffY) > 50) {
                    // 垂直滑动
                    if (diffY > 0 && direction.second != -1) {
                        direction = Pair(0, 1) // 下
                    } else if (diffY < 0 && direction.second != 1) {
                        direction = Pair(0, -1) // 上
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
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
