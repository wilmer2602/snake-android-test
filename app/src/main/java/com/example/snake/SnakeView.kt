package com.example.snake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class SnakeView(context: Context) : View(context) {
    private val bodyPaint = Paint().apply {
        color = Color.parseColor("#32CD32")
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val headPaint = Paint().apply {
        color = Color.parseColor("#228B22")
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val eyePaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val foodPaint = Paint().apply {
        color = Color.parseColor("#FF4500")
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint().apply {
        color = Color.WHITE
        strokeWidth = 5f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val gridSize = 20
    private val cols = 20
    private val rows = 30

    private var snake = mutableListOf<Pair<Int, Int>>().apply { add(Pair(10, 10)) }
    private var direction = Pair(1, 0)
    private var food = generateFood()
    private var score = 0
    private var gameRunning = true
    private var isPaused = false
    private var speedLevel = 1
    private var baseSpeed = 300L
    private var isEndlessMode = false
    private var startTime = 0L
    private var elapsedTime = 0L

    init {
        val thread = object : Thread() {
            override fun run() {
                while (true) {
                    Thread.sleep(baseSpeed / speedLevel)
                    if (gameRunning && !isPaused) {
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
            newFood = Pair((0 until cols).random(), (0 until rows).random())
        } while (snake.contains(newFood))
        return newFood
    }

    private fun move() {
        if (startTime == 0L) startTime = System.currentTimeMillis()
        elapsedTime = System.currentTimeMillis() - startTime
        
        val head = snake.first()
        var newHead = Pair(head.first + direction.first, head.second + direction.second)

        if (isEndlessMode) {
            newHead = Pair((newHead.first + cols) % cols, (newHead.second + rows) % rows)
        } else {
            if (newHead.first < 0 || newHead.first >= cols || newHead.second < 0 || newHead.second >= rows) {
                gameOver()
                return
            }
        }

        if (snake.contains(newHead)) {
            gameOver()
            return
        }

        snake.add(0, newHead)

        if (newHead == food) {
            score++
            if (snake.size >= cols * rows) {
                gameOver()
                return
            }
            food = generateFood()
        } else {
            snake.removeAt(snake.size - 1)
        }
    }

    private fun gameOver() {
        gameRunning = false
    }

    fun reset() {
        snake.clear()
        snake.add(Pair(10, 10))
        direction = Pair(1, 0)
        food = generateFood()
        score = 0
        gameRunning = true
        isPaused = false
        speedLevel = 1
        startTime = 0L
        elapsedTime = 0L
    }

    fun getScore(): Int = score
    fun isGameRunning(): Boolean = gameRunning
    fun getSpeedLevel(): Int = speedLevel
    fun getElapsedTime(): Long = elapsedTime / 1000
    fun isEndlessMode(): Boolean = isEndlessMode
    
    fun toggleEndlessMode() {
        isEndlessMode = !isEndlessMode
        reset()
    }
    
    fun togglePause() {
        if (gameRunning) isPaused = !isPaused
    }
    
    fun increaseSpeed() {
        if (speedLevel < 5) speedLevel++
    }
    
    fun setDirection(dx: Int, dy: Int) {
        if (dx != 0 && direction.first != 0) return
        if (dy != 0 && direction.second != 0) return
        direction = Pair(dx, dy)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cellSize = width.coerceAtMost(height) / gridSize
        val offsetX = (width - cols * cellSize) / 2
        val offsetY = (height - rows * cellSize) / 2

        canvas.drawRect(
            offsetX.toFloat(), offsetY.toFloat(),
            (offsetX + cols * cellSize).toFloat(), (offsetY + rows * cellSize).toFloat(),
            borderPaint
        )

        for (i in snake.indices) {
            val (x, y) = snake[i]
            val paint = if (i == 0) headPaint else bodyPaint
            canvas.drawRect(
                (offsetX + x * cellSize + 2).toFloat(),
                (offsetY + y * cellSize + 2).toFloat(),
                (offsetX + (x + 1) * cellSize - 2).toFloat(),
                (offsetY + (y + 1) * cellSize - 2).toFloat(),
                paint
            )
            
            if (i == 0) {
                val eyeSize = cellSize / 6f
                val eyeOffsetX = cellSize / 4f
                val eyeOffsetY = cellSize / 3f
                canvas.drawCircle(
                    offsetX + x * cellSize + eyeOffsetX,
                    offsetY + y * cellSize + eyeOffsetY,
                    eyeSize, eyePaint
                )
                canvas.drawCircle(
                    offsetX + (x + 1) * cellSize - eyeOffsetX,
                    offsetY + y * cellSize + eyeOffsetY,
                    eyeSize, eyePaint
                )
            }
        }

        canvas.drawRect(
            (offsetX + food.first * cellSize + 2).toFloat(),
            (offsetY + food.second * cellSize + 2).toFloat(),
            (offsetX + (food.first + 1) * cellSize - 2).toFloat(),
            (offsetY + (food.second + 1) * cellSize - 2).toFloat(),
            foodPaint
        )
    }
}
