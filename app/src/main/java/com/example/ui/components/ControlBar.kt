package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.model.MoveDirection
import com.example.ui.theme.BrandDarkPurple
import com.example.ui.theme.BrandPrimaryPurple
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfaceSleekBorder
import com.example.ui.theme.SurfaceSleekLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryDark

@Composable
fun ControlBar(
    gameState: GameState,
    onMove: (MoveDirection) -> Unit,
    onUndo: () -> Unit,
    onOpenPuzzleList: () -> Unit,
    onShareDaily: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Mode Specific Action Buttons (Level Select for Puzzles, Share for Daily)
        if (gameState.gameMode == GameMode.PUZZLE) {
            Button(
                onClick = onOpenPuzzleList,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimaryPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = BrandPrimaryPurple.copy(alpha = 0.3f))
                    .testTag("puzzle_selector_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "انتخاب مرحله پازل (مرحله فعلی: ${gameState.currentPuzzleId})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (gameState.gameMode == GameMode.DAILY) {
            Button(
                onClick = onShareDaily,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimaryPurple,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = BrandPrimaryPurple.copy(alpha = 0.3f))
                    .testTag("share_daily_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "اشتراک‌گذاری کارت چالش روزانه (Wordle Style)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Action Row & D-Pad
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sleek Undo Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (gameState.canUndo) SurfaceSleekLight else Color(0xFFF0EBF2))
                    .border(
                        width = 1.dp,
                        color = if (gameState.canUndo) SurfaceSleekBorder else SurfaceBorderLight,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = gameState.canUndo) { onUndo() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("undo_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (gameState.canUndo) BrandPrimaryPurple else TextMutedLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "بازگشت",
                        color = if (gameState.canUndo) TextPrimaryDark else TextMutedLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Compact Sleek D-Pad Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DPadButton(
                    label = "←",
                    onClick = { onMove(MoveDirection.LEFT) },
                    testTag = "dpad_left"
                )
                DPadButton(
                    label = "↑",
                    onClick = { onMove(MoveDirection.UP) },
                    testTag = "dpad_up"
                )
                DPadButton(
                    label = "↓",
                    onClick = { onMove(MoveDirection.DOWN) },
                    testTag = "dpad_down"
                )
                DPadButton(
                    label = "→",
                    onClick = { onMove(MoveDirection.RIGHT) },
                    testTag = "dpad_right"
                )
            }
        }
    }
}

@Composable
private fun DPadButton(
    label: String,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceSleekLight)
            .border(1.dp, SurfaceSleekBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = BrandPrimaryPurple,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
    }
}

