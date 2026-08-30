package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.GameState
import com.example.ui.theme.AnnihilationGold
import com.example.ui.theme.AntimatterBarRed
import com.example.ui.theme.AntimatterTextDeep
import com.example.ui.theme.BrandDarkPurple
import com.example.ui.theme.BrandPrimaryPurple
import com.example.ui.theme.MatterBarBlue
import com.example.ui.theme.MatterTextDeep
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfacePureWhite
import com.example.ui.theme.SurfaceSleekActive
import com.example.ui.theme.SurfaceSleekBorder
import com.example.ui.theme.SurfaceSleekLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMedium

@Composable
fun HeaderStats(
    gameState: GameState,
    onModeSelected: (GameMode) -> Unit,
    onHowToPlayClick: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptic: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Bar: Sleek Brand Logo, Title & Circular Action Icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sleek circle icon with 0
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BrandPrimaryPurple)
                        .shadow(4.dp, CircleShape, ambientColor = BrandPrimaryPurple.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "0",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }

                Column {
                    Text(
                        text = "نقطه صفر",
                        color = TextPrimaryDark,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Zero-Sum • موازنه ذرات",
                        color = BrandPrimaryPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Sleek Rounded Action Icons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SleekCircleAction(
                    onClick = onHowToPlayClick,
                    icon = Icons.Default.Info,
                    tint = TextSecondaryMedium,
                    contentDesc = "How to play",
                    testTag = "how_to_play_button"
                )

                SleekCircleAction(
                    onClick = onToggleSound,
                    icon = if (gameState.soundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                    tint = if (gameState.soundEnabled) BrandPrimaryPurple else TextMutedLight,
                    contentDesc = "Sound toggle",
                    testTag = "sound_button"
                )

                SleekCircleAction(
                    onClick = onToggleHaptic,
                    icon = Icons.Default.Vibration,
                    tint = if (gameState.hapticEnabled) AntimatterBarRed else TextMutedLight,
                    contentDesc = "Haptic toggle",
                    testTag = "haptic_button"
                )

                SleekCircleAction(
                    onClick = onRestart,
                    icon = Icons.Default.Refresh,
                    tint = BrandDarkPurple,
                    contentDesc = "Restart",
                    testTag = "restart_button"
                )
            }
        }

        // Sleek Mode Switcher Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceSleekLight)
                .border(1.dp, SurfaceSleekBorder, RoundedCornerShape(20.dp))
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GameMode.values().forEach { mode ->
                val isSelected = gameState.gameMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) BrandPrimaryPurple else Color.Transparent
                        )
                        .clickable { onModeSelected(mode) }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.titleFa,
                        color = if (isSelected) Color.White else TextSecondaryMedium,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Sleek Two-Column Stats Cards (Matching the design layout)
        val nonNullTiles = gameState.grid.flatten().filterNotNull()
        val matterTiles = nonNullTiles.filter { it.isMatter }
        val antimatterTiles = nonNullTiles.filter { !it.isMatter }
        val matterSum = matterTiles.sumOf { it.magnitude }
        val antimatterSum = antimatterTiles.sumOf { it.magnitude }
        val totalSum = (matterSum + antimatterSum).coerceAtLeast(1)
        val matterProgress = (matterSum.toFloat() / totalSum).coerceIn(0f, 1f)
        val antimatterProgress = (antimatterSum.toFloat() / totalSum).coerceIn(0f, 1f)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Left Card: Current Balance / Score & Best
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(SurfaceSleekLight)
                    .border(1.dp, SurfaceSleekBorder, RoundedCornerShape(22.dp))
                    .padding(12.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BrandPrimaryPurple)
                        )
                        Text(
                            text = "امتیاز فعلی",
                            color = BrandPrimaryPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${gameState.score}",
                            color = TextPrimaryDark,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "امتیاز",
                            color = TextSecondaryMedium,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }

                    Text(
                        text = "رکورد: ${gameState.bestScore} • حرکت: ${gameState.moves}",
                        color = TextSecondaryMedium,
                        fontSize = 10.sp
                    )
                }
            }

            // Right Card: Live Quantum Board Telemetry (Matter vs Antimatter)
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(SurfacePureWhite)
                    .border(1.dp, SurfaceBorderLight, RoundedCornerShape(22.dp))
                    .padding(10.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Matter Bar
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ماده (+)",
                                color = MatterTextDeep,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "+$matterSum",
                                color = MatterBarBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        LinearProgressIndicator(
                            progress = { matterProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MatterBarBlue,
                            trackColor = SurfaceSleekLight
                        )
                    }

                    // Antimatter Bar
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "پادماده (-)",
                                color = AntimatterTextDeep,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "-$antimatterSum",
                                color = AntimatterBarRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        LinearProgressIndicator(
                            progress = { antimatterProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = AntimatterBarRed,
                            trackColor = SurfaceSleekLight
                        )
                    }

                    // Annihilations count badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "نابودی‌های انجام شده:",
                            color = TextSecondaryMedium,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "💥 ${gameState.annihilations}",
                            color = BrandPrimaryPurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Combo Multiplier Banner
        AnimatedVisibility(
            visible = gameState.comboCount > 1,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BrandPrimaryPurple)
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = AnnihilationGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "واکنش زنجیره‌ای کوانتومی! کمبو ${gameState.comboCount}x",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SleekCircleAction(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    contentDesc: String,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SurfaceSleekLight)
            .border(1.dp, SurfaceSleekBorder, CircleShape)
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}

