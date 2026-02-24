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
    private var speedMultiplier = 1.0
    private var baseSpeed = 300L
    private var isEndlessMode = false
    private var startTime = 0L
    private var elapsedTime = 0L
    private var isAutoWalk = false
    private var wallHitCount = 0
    private var stepsAfterWallHit = 0
    
    @Volatile
    private var isUpdating = false

    init {
        val thread = object : Thread() {
            override fun run() {
                while (true) {
                    Thread.sleep((baseSpeed / speedMultiplier).toLong())
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
        if (isUpdating) return
        isUpdating = true
        
        try {
            moveInternal()
        } finally {
            isUpdating = false
        }
    }
    
    private fun moveInternal() {
        if (startTime == 0L) startTime = System.currentTimeMillis()
        elapsedTime = System.currentTimeMillis() - startTime
        
        val head = snake.first()
        
        // 智能避墙系统：提前预判并远离墙壁
        if (!isAutoWalk && isEndlessMode) {
            val safeZone = 5  // 安全区域：距离墙5格以上
            val dangerZone = 3  // 危险区域：距离墙3格以内
            
            val distToLeft = head.first
            val distToRight = cols - 1 - head.first
            val distToTop = head.second
            val distToBottom = rows - 1 - head.second
            
            // 计算到中心的方向（最安全的方向）
            val centerX = cols / 2
            val centerY = rows / 2
            val towardCenterX = if (head.first < centerX) 1 else if (head.first > centerX) -1 else 0
            val towardCenterY = if (head.second < centerY) 1 else if (head.second > centerY) -1 else 0
            
            // 危险检测：如果在危险区域且朝向墙壁，立即转向
            var needCorrection = false
            val safeDirections = mutableListOf<Pair<Int, Int>>()
            
            if (distToLeft < dangerZone && direction.first <= 0) {
                needCorrection = true
            } else if (distToRight < dangerZone && direction.first >= 0) {
                needCorrection = true
            } else if (distToTop < dangerZone && direction.second <= 0) {
                needCorrection = true
            } else if (distToBottom < dangerZone && direction.second >= 0) {
                needCorrection = true
            }
            
            if (needCorrection) {
                // 收集所有朝向中心的安全方向
                if (towardCenterX > 0 && distToRight > safeZone) {
                    safeDirections.add(Pair(1, 0))
                } else if (towardCenterX < 0 && distToLeft > safeZone) {
                    safeDirections.add(Pair(-1, 0))
                }
                
                if (towardCenterY > 0 && distToBottom > safeZone) {
                    safeDirections.add(Pair(0, 1))
                } else if (towardCenterY < 0 && distToTop > safeZone) {
                    safeDirections.add(Pair(0, -1))
                }
                
                // 如果没有朝向中心的安全方向，选择任何远离墙的方向
                if (safeDirections.isEmpty()) {
                    if (distToLeft > distToRight) safeDirections.add(Pair(-1, 0))
                    else safeDirections.add(Pair(1, 0))
                    
                    if (distToTop > distToBottom) safeDirections.add(Pair(0, -1))
                    else safeDirections.add(Pair(0, 1))
                }
                
                // 选择最安全的方向
                if (safeDirections.isNotEmpty()) {
                    direction = safeDirections.random()
                    stepsAfterWallHit = 8  // 强制直行8步，确保远离墙壁
                }
            }
        }
        
        // 自动游走AI
        if (isAutoWalk) {
            val dx = food.first - head.first
            val dy = food.second - head.second
            
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0 && direction != Pair(-1, 0)) direction = Pair(1, 0)
                else if (dx < 0 && direction != Pair(1, 0)) direction = Pair(-1, 0)
                else if (dy > 0 && direction != Pair(0, -1)) direction = Pair(0, 1)
                else if (dy < 0 && direction != Pair(0, 1)) direction = Pair(0, -1)
            } else {
                if (dy > 0 && direction != Pair(0, -1)) direction = Pair(0, 1)
                else if (dy < 0 && direction != Pair(0, 1)) direction = Pair(0, -1)
                else if (dx > 0 && direction != Pair(-1, 0)) direction = Pair(1, 0)
                else if (dx < 0 && direction != Pair(1, 0)) direction = Pair(-1, 0)
            }
        }
        
        var newHead = Pair(head.first + direction.first, head.second + direction.second)

        // 撞墙处理
        val hitWall = newHead.first < 0 || newHead.first >= cols || 
                      newHead.second < 0 || newHead.second >= rows

        if (hitWall) {
            if (isEndlessMode) {
                wallHitCount++
                
                // 惩罚：减2节
                if (snake.size > 1) {
                    repeat(Math.min(2, snake.size - 1)) {
                        if (snake.size > 1) snake.removeAt(snake.size - 1)
                    }
                }
                
                // 强制朝向场地中心
                val centerX = cols / 2
                val centerY = rows / 2
                val toCenterX = centerX - head.first
                val toCenterY = centerY - head.second
                
                direction = if (Math.abs(toCenterX) > Math.abs(toCenterY)) {
                    if (toCenterX > 0) Pair(1, 0) else Pair(-1, 0)
                } else {
                    if (toCenterY > 0) Pair(0, 1) else Pair(0, -1)
                }
                
                stepsAfterWallHit = 10  // 强制朝中心直行10步
                newHead = Pair(head.first + direction.first, head.second + direction.second)
            } else {
                gameOver()
                return
            }
        }
        
        // 直行计数递减
        if (stepsAfterWallHit > 0) {
            stepsAfterWallHit--
        }

        // 撞到自己：无影响，直接穿过（不检查碰撞）

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
        speedMultiplier = 1.0
        startTime = 0L
        elapsedTime = 0L
        isAutoWalk = false
        wallHitCount = 0
        stepsAfterWallHit = 0
    }

    fun getScore(): Int = score
    fun isGameRunning(): Boolean = gameRunning
    fun getSpeedMultiplier(): Double = speedMultiplier
    fun getElapsedTime(): Long = elapsedTime / 1000
    fun isEndlessMode(): Boolean = isEndlessMode
    fun getWallHitCount(): Int = wallHitCount
    
    fun toggleEndlessMode() {
        isEndlessMode = !isEndlessMode
        reset()
    }
    
    fun togglePause() {
        if (gameRunning) isPaused = !isPaused
    }
    
    fun increaseSpeed() {
        speedMultiplier = when {
            speedMultiplier < 0.2 -> 0.2
            speedMultiplier < 0.5 -> 0.5
            speedMultiplier < 1.0 -> 1.0
            speedMultiplier < 2.0 -> 2.0
            speedMultiplier < 5.0 -> 5.0
            speedMultiplier < 10.0 -> 10.0
            speedMultiplier < 20.0 -> 20.0
            speedMultiplier < 50.0 -> 50.0
            speedMultiplier < 100.0 -> 100.0
            else -> 100.0
        }
    }
    
    fun decreaseSpeed() {
        speedMultiplier = when {
            speedMultiplier > 50.0 -> 50.0
            speedMultiplier > 20.0 -> 20.0
            speedMultiplier > 10.0 -> 10.0
            speedMultiplier > 5.0 -> 5.0
            speedMultiplier > 2.0 -> 2.0
            speedMultiplier > 1.0 -> 1.0
            speedMultiplier > 0.5 -> 0.5
            speedMultiplier > 0.2 -> 0.2
            speedMultiplier > 0.1 -> 0.1
            else -> 0.1
        }
    }
    
    fun toggleAutoWalk() {
        isAutoWalk = !isAutoWalk
    }
    
    fun disableAutoWalk() {
        isAutoWalk = false
    }
    
    fun setDirection(dx: Int, dy: Int) {
        // 撞墙后直行期间不允许改变方向
        if (stepsAfterWallHit > 0) return
        
        val newDir = Pair(dx, dy)
        // 只防止180度反向，允许同方向（无操作）
        if (newDir == Pair(-direction.first, -direction.second)) return
        direction = newDir
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
            
            // 计算渐变色：从绿色(头)到蓝色(尾)
            val ratio = if (snake.size > 1) i.toFloat() / (snake.size - 1) else 0f
            
            // 头部：亮绿色 #00FF00 (0, 255, 0)
            // 尾部：深蓝色 #0000FF (0, 0, 255)
            val red = 0
            val green = (255 * (1 - ratio)).toInt()
            val blue = (255 * ratio).toInt()
            
            val segmentPaint = Paint().apply {
                color = Color.rgb(red, green, blue)
                isAntiAlias = true
                style = Paint.Style.FILL
            }
            
            canvas.drawRect(
                (offsetX + x * cellSize + 2).toFloat(),
                (offsetY + y * cellSize + 2).toFloat(),
                (offsetX + (x + 1) * cellSize - 2).toFloat(),
                (offsetY + (y + 1) * cellSize - 2).toFloat(),
                segmentPaint
            )
            
            if (i == 0) {
                // 眼睛
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
                
                // 方向三角形（舌头）
                val centerX = offsetX + x * cellSize + cellSize / 2f
                val centerY = offsetY + y * cellSize + cellSize / 2f
                val triangleSize = cellSize / 3f
                
                val path = android.graphics.Path()
                when (direction) {
                    Pair(1, 0) -> { // 向右
                        path.moveTo(centerX + triangleSize, centerY)
                        path.lineTo(centerX, centerY - triangleSize / 2)
                        path.lineTo(centerX, centerY + triangleSize / 2)
                    }
                    Pair(-1, 0) -> { // 向左
                        path.moveTo(centerX - triangleSize, centerY)
                        path.lineTo(centerX, centerY - triangleSize / 2)
                        path.lineTo(centerX, centerY + triangleSize / 2)
                    }
                    Pair(0, 1) -> { // 向下
                        path.moveTo(centerX, centerY + triangleSize)
                        path.lineTo(centerX - triangleSize / 2, centerY)
                        path.lineTo(centerX + triangleSize / 2, centerY)
                    }
                    Pair(0, -1) -> { // 向上
                        path.moveTo(centerX, centerY - triangleSize)
                        path.lineTo(centerX - triangleSize / 2, centerY)
                        path.lineTo(centerX + triangleSize / 2, centerY)
                    }
                }
                path.close()
                
                val tonguePaint = Paint().apply {
                    color = Color.RED
                    style = Paint.Style.FILL
                    isAntiAlias = true
                }
                canvas.drawPath(path, tonguePaint)
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
