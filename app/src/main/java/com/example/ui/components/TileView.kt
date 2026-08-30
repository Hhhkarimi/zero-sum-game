package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Tile
import com.example.ui.theme.AntimatterTextDeep
import com.example.ui.theme.AntimatterTileBg
import com.example.ui.theme.AntimatterTileBgDeep
import com.example.ui.theme.AntimatterTileBorder
import com.example.ui.theme.BrandPrimaryPurple
import com.example.ui.theme.MatterTextDeep
import com.example.ui.theme.MatterTileBg
import com.example.ui.theme.MatterTileBgDeep
import com.example.ui.theme.MatterTileBorder

@Composable
fun TileView(
    tile: Tile,
    modifier: Modifier = Modifier
) {
    val isMatter = tile.isMatter
    val magnitude = tile.magnitude

    // Pop / Spawn scale animation
    val scaleAnim = remember { Animatable(if (tile.isNew || tile.isMerged) 0.4f else 1f) }

    LaunchedEffect(tile.id, tile.value) {
        if (tile.isNew || tile.isMerged) {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.65f,
                    stiffness = 550f
                )
            )
        }
    }

    // Gentle pulse for high level tiles (magnitude >= 32)
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (magnitude >= 32) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Palette calculation based on Sleek Interface Design
    val (tileBg, borderColor, textMainColor) = if (isMatter) {
        when {
            magnitude <= 4 -> Triple(MatterTileBg, MatterTileBorder, MatterTextDeep)
            magnitude <= 16 -> Triple(MatterTileBgDeep, MatterTileBorder, MatterTextDeep)
            magnitude <= 64 -> Triple(Color(0xFF93C5FD), Color(0xFF60A5FA), Color(0xFF1E3A8A))
            else -> Triple(Color(0xFF60A5FA), Color(0xFF3B82F6), Color(0xFF0F172A))
        }
    } else {
        when {
            magnitude <= 4 -> Triple(AntimatterTileBg, AntimatterTileBorder, AntimatterTextDeep)
            magnitude <= 16 -> Triple(AntimatterTileBgDeep, AntimatterTileBorder, AntimatterTextDeep)
            magnitude <= 64 -> Triple(Color(0xFFFCA5A5), Color(0xFFF87171), Color(0xFF7F1D1D))
            else -> Triple(Color(0xFFF87171), Color(0xFFEF4444), Color(0xFF450A0A))
        }
    }

    val displayString = if (tile.value > 0) "+${tile.value}" else "${tile.value}"
    val fontSize = when {
        displayString.length <= 2 -> 24.sp
        displayString.length == 3 -> 20.sp
        displayString.length == 4 -> 17.sp
        else -> 14.sp
    }

    val isHighTier = magnitude >= 32

    Box(
        modifier = modifier
            .fillMaxSize()
            .scale(scaleAnim.value * pulseScale)
            .shadow(
                elevation = if (isHighTier) 6.dp else 2.dp,
                shape = RoundedCornerShape(18.dp),
                ambientColor = if (isMatter) MatterTextDeep.copy(alpha = 0.1f) else AntimatterTextDeep.copy(alpha = 0.1f),
                spotColor = if (isMatter) MatterTextDeep.copy(alpha = 0.15f) else AntimatterTextDeep.copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(tileBg)
            .border(
                width = if (isHighTier) 2.dp else 1.dp,
                color = if (isHighTier) BrandPrimaryPurple.copy(alpha = 0.5f) else borderColor,
                shape = RoundedCornerShape(18.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(
                text = displayString,
                color = textMainColor,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-0.5).sp
            )

            // Small label badge
            Text(
                text = if (isMatter) "ماده" else "پادماده",
                color = textMainColor.copy(alpha = 0.65f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

