package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.GameState
import com.example.ui.theme.BrandPrimaryPurple
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfacePureWhite
import com.example.ui.theme.SurfaceSleekBorder
import com.example.ui.theme.SurfaceSleekLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMedium

@Composable
fun DailyShareDialog(
    gameState: GameState,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Build Wordle-like share text
    val shareCardText = buildString {
        append("🌌 نقطه صفر (Zero-Sum Challenge)\n")
        append("📅 تاریخ: ${gameState.dailyDateString}\n")
        append("🎯 وضعیت: ${if (gameState.isZeroBoardVictory) "صفر مطلق (پیروزی! 🎉)" else "در حال تسویه"}\n")
        append("⚡ حرکات: ${gameState.moves} | 💥 نابودی‌ها: ${gameState.annihilations}\n")
        append("🏆 امتیاز کوانتومی: ${gameState.score}\n\n")

        // 4x4 Grid emoji representation
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                val tile = gameState.grid.getOrNull(r)?.getOrNull(c)
                val emoji = when {
                    tile == null -> "⬜"
                    tile.isMatter -> "🟦"
                    tile.isAntimatter -> "🟥"
                    else -> "🔲"
                }
                append(emoji)
            }
            append("\n")
        }
        append("\n#نقطه_صفر #ZeroSum #پازل_کوانتومی")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfacePureWhite),
            border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "کارت چالش روزانه",
                        color = TextPrimaryDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMutedLight
                        )
                    }
                }

                // Emoji Card Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceSleekLight)
                        .border(1.dp, SurfaceSleekBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = shareCardText,
                            color = TextPrimaryDark,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy button
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("ZeroSum Result", shareCardText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "متن در کلیپ‌بورد کپی شد!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("copy_result_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorderLight)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = BrandPrimaryPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "کپی",
                            color = BrandPrimaryPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Share button (System Share Sheet)
                    Button(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareCardText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "اشتراک‌گذاری نتیجه نقطه صفر")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("share_native_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandPrimaryPurple,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "اشتراک‌گذاری",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

