package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.model.AnnihilationEvent
import com.example.model.MoveDirection
import com.example.model.Particle
import com.example.model.Tile
import com.example.ui.theme.AnnihilationGold
import com.example.ui.theme.AnnihilationViolet
import com.example.ui.theme.BoardContainerBg
import com.example.ui.theme.BoardEmptyCell
import com.example.ui.theme.BrandPrimaryPurple
import com.example.ui.theme.SurfaceBorderLight
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun GameBoard(
    grid: List<List<Tile?>>,
    recentAnnihilations: List<AnnihilationEvent>,
    onSwipe: (MoveDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    // Screen Shake Animation
    val shakeOffset = remember { Animatable(0f) }

    // Shockwave radius animation for recent annihilations
    val shockwaveAnim = remember { Animatable(0f) }

    // Active visual particles
    val particles = remember { mutableStateListOf<Particle>() }

    // Trigger visual explosion and shake when annihilation happens
    LaunchedEffect(recentAnnihilations) {
        if (recentAnnihilations.isNotEmpty()) {
            // Screen shake
            for (i in 0..3) {
                val offset = if (i % 2 == 0) 10f else -10f
                shakeOffset.animateTo(offset, animationSpec = tween(35, easing = LinearEasing))
            }
            shakeOffset.animateTo(0f, animationSpec = tween(40, easing = LinearEasing))

            // Expand shockwave
            shockwaveAnim.snapTo(0f)
            shockwaveAnim.animateTo(1f, animationSpec = tween(350, easing = LinearEasing))
        }
    }

    // Particle updater loop
    LaunchedEffect(recentAnnihilations) {
        if (recentAnnihilations.isNotEmpty()) {
            for (event in recentAnnihilations) {
                // Spawn 28 sleek sparks
                for (p in 0..28) {
                    val angle = Random.nextDouble(0.0, Math.PI * 2)
                    val speed = Random.nextDouble(4.0, 16.0).toFloat()
                    val pColor = if (p % 3 == 0) 0xFF3B82F6 else if (p % 3 == 1) 0xFFEF4444 else 0xFF6750A4
                    particles.add(
                        Particle(
                            x = (event.col + 0.5f) * 0.25f,
                            y = (event.row + 0.5f) * 0.25f,
                            vx = (cos(angle) * speed).toFloat(),
                            vy = (sin(angle) * speed).toFloat(),
                            color = pColor,
                            size = Random.nextDouble(4.0, 9.0).toFloat(),
                            alpha = 1f,
                            life = 0f,
                            maxLife = Random.nextDouble(200.0, 450.0).toFloat()
                        )
                    )
                }
            }

            // Animate particles over time
            val startTime = System.currentTimeMillis()
            while (particles.isNotEmpty()) {
                val elapsed = 16f
                for (i in particles.indices.reversed()) {
                    val p = particles[i]
                    val newLife = p.life + elapsed
                    if (newLife >= p.maxLife) {
                        particles.removeAt(i)
                    } else {
                        particles[i] = p.copy(
                            life = newLife,
                            alpha = (1f - (newLife / p.maxLife)).coerceIn(0f, 1f)
                        )
                    }
                }
                delay(16)
                if (System.currentTimeMillis() - startTime > 700) {
                    particles.clear()
                    break
                }
            }
        }
    }

    // Gesture detection variables
    var totalDragX by remember { mutableStateOf(0f) }
    var totalDragY by remember { mutableStateOf(0f) }
    var hasTriggeredMove by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .testTag("game_board")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f
                        totalDragY = 0f
                        hasTriggeredMove = false
                    },
                    onDragEnd = {
                        totalDragX = 0f
                        totalDragY = 0f
                        hasTriggeredMove = false
                    },
                    onDragCancel = {
                        totalDragX = 0f
                        totalDragY = 0f
                        hasTriggeredMove = false
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (!hasTriggeredMove) {
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y
                            val threshold = 35f

                            if (abs(totalDragX) > threshold || abs(totalDragY) > threshold) {
                                if (abs(totalDragX) > abs(totalDragY)) {
                                    if (totalDragX > 0) {
                                        onSwipe(MoveDirection.RIGHT)
                                    } else {
                                        onSwipe(MoveDirection.LEFT)
                                    }
                                } else {
                                    if (totalDragY > 0) {
                                        onSwipe(MoveDirection.DOWN)
                                    } else {
                                        onSwipe(MoveDirection.UP)
                                    }
                                }
                                hasTriggeredMove = true
                            }
                        }
                    }
                )
            }
    ) {
        // Outer Sleek Board Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(8.dp, RoundedCornerShape(32.dp), ambientColor = Color(0x1A000000), spotColor = Color(0x1A000000))
                .clip(RoundedCornerShape(32.dp))
                .background(BoardContainerBg)
                .border(1.dp, SurfaceBorderLight, RoundedCornerShape(32.dp))
                .padding(10.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                // 1. Grid Background Slot Cells
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (r in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (c in 0 until 4) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(BoardEmptyCell),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Subtle dot inside slot
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color(0xFFD8D2DC))
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Active Grid Tiles
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (r in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (c in 0 until 4) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val tile = grid[r][c]
                                    if (tile != null) {
                                        TileView(
                                            tile = tile,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Particle System & Annihilation Shockwaves Canvas Overlay
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Draw shockwaves
                    if (shockwaveAnim.value > 0f && shockwaveAnim.value < 1f) {
                        for (event in recentAnnihilations) {
                            val centerX = (event.col + 0.5f) * (w / 4f)
                            val centerY = (event.row + 0.5f) * (h / 4f)
                            val maxRadius = w * 0.45f
                            val currentRadius = shockwaveAnim.value * maxRadius
                            val alpha = (1f - shockwaveAnim.value).coerceIn(0f, 1f)

                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        AnnihilationGold.copy(alpha = alpha * 0.7f),
                                        AnnihilationViolet.copy(alpha = alpha * 0.5f),
                                        Color.Transparent
                                    ),
                                    center = Offset(centerX, centerY),
                                    radius = currentRadius + 10f
                                ),
                                radius = currentRadius,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = 5f * (1f - shockwaveAnim.value))
                            )
                        }
                    }

                    // Draw particles
                    for (p in particles) {
                        val px = p.x * w + p.vx * (p.life / 20f)
                        val py = p.y * h + p.vy * (p.life / 20f)
                        drawCircle(
                            color = Color(p.color).copy(alpha = p.alpha),
                            radius = p.size * (1f - p.life / p.maxLife),
                            center = Offset(px, py)
                        )
                    }
                }
            }
        }
    }
}

