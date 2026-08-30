package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AnnihilationGold
import com.example.ui.theme.AntimatterBarRed
import com.example.ui.theme.AntimatterTextDeep
import com.example.ui.theme.AntimatterTileBg
import com.example.ui.theme.AntimatterTileBorder
import com.example.ui.theme.BrandPrimaryPurple
import com.example.ui.theme.MatterBarBlue
import com.example.ui.theme.MatterTextDeep
import com.example.ui.theme.MatterTileBg
import com.example.ui.theme.MatterTileBorder
import com.example.ui.theme.SurfaceBorderLight
import com.example.ui.theme.SurfacePureWhite
import com.example.ui.theme.SurfaceSleekBorder
import com.example.ui.theme.SurfaceSleekLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryMedium

@Composable
fun HowToPlayDialog(
    onDismiss: () -> Unit
) {
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
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "راهنمای بازی «نقطه صفر»",
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

                Text(
                    text = "برخلاف ۲۰۴۸، هدف ساختن اعداد بزرگ نیست؛ هدف این است که با نابودی ماده و پادماده، صفحه را خالی نگه دارید و به «صفر» برسید!",
                    color = BrandPrimaryPurple,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                // Law 1
                RuleItem(
                    badgeText = "قانون ۱",
                    badgeColor = MatterBarBlue,
                    title = "ترکیب همجنس‌ها (رشد):",
                    desc = "اگر دو خانه همنام به هم بخورند، عدد دو برابر می‌شود:\n(+۲) + (+۲) = +۴  یا  (-۴) + (-۴) = -۸",
                    exampleLeft = "+2",
                    exampleRight = "+2",
                    exampleResult = "+4",
                    isMatter = true
                )

                // Law 2
                RuleItem(
                    badgeText = "قانون ۲",
                    badgeColor = AnnihilationGold,
                    title = "برخورد غیرهمجنس نامساوی (کاهش):",
                    desc = "اگر ماده و پادماده نامساوی به هم بخورند، از هم کم می‌شوند:\n(+۸) + (-۴) = +۴  یا  (-۱۶) + (+۴) = -۱۲",
                    exampleLeft = "+8",
                    exampleRight = "-4",
                    exampleResult = "+4",
                    isMatter = true
                )

                // Law 3: Absolute Annihilation
                RuleItem(
                    badgeText = "قانون ۳ (اوج هیجان!)",
                    badgeColor = AntimatterBarRed,
                    title = "نابودی کوانتومی (Zero-Sum):",
                    desc = "اگر دو عدد متضاد با اندازه برابر به هم بخورند، هر دو پودر شده و خانه کاملاً خالی می‌شود!\n(+۱۶) + (-۱۶) = 💥 ۰ (نابودی کامل و امتیاز طلایی)",
                    exampleLeft = "+16",
                    exampleRight = "-16",
                    exampleResult = "💥 ۰",
                    isMatter = false,
                    isAnnihilation = true
                )

                // Daily Challenge Info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceSleekLight)
                        .border(1.dp, SurfaceSleekBorder, RoundedCornerShape(18.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🌟 چالش روزانه (مدل Wordle):",
                            color = BrandPrimaryPurple,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "هر روز یک جدول پیچیده مشترک دریافت می‌کنید. سعی کنید در کمترین حرکت صفحه را کاملاً صفر کنید و کارت ایموجی آن را با دوستانتان به اشتراک بگذارید!",
                            color = TextSecondaryMedium,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPrimaryPurple,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "متوجه شدم، بزن بریم!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleItem(
    badgeText: String,
    badgeColor: Color,
    title: String,
    desc: String,
    exampleLeft: String,
    exampleRight: String,
    exampleResult: String,
    isMatter: Boolean,
    isAnnihilation: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceSleekLight)
            .border(1.dp, SurfaceSleekBorder, RoundedCornerShape(18.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = title,
                    color = TextPrimaryDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = desc,
                color = TextSecondaryMedium,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            // Visual Equation Demo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfacePureWhite)
                    .border(1.dp, SurfaceBorderLight, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MiniDemoTile(exampleLeft, isMatter = exampleLeft.startsWith("+"))
                Text(
                    text = " + ",
                    color = TextSecondaryMedium,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                MiniDemoTile(exampleRight, isMatter = exampleRight.startsWith("+"))
                Text(
                    text = " = ",
                    color = TextSecondaryMedium,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                MiniDemoTile(
                    exampleResult,
                    isMatter = isMatter,
                    isGold = isAnnihilation
                )
            }
        }
    }
}

@Composable
private fun MiniDemoTile(
    text: String,
    isMatter: Boolean,
    isGold: Boolean = false
) {
    val bg = if (isGold) Color(0xFFFEF3C7) else if (isMatter) MatterTileBg else AntimatterTileBg
    val border = if (isGold) AnnihilationGold else if (isMatter) MatterTileBorder else AntimatterTileBorder
    val textColor = if (isGold) AnnihilationGold else if (isMatter) MatterTextDeep else AntimatterTextDeep

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
        )
    }
}

