package org.calcfx.app.ui.components

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.calcfx.app.engine.AngleUnit
import org.calcfx.app.engine.CalculatorMode
import org.calcfx.app.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DisplayScreen(
    expression: String,
    cursorPosition: Int,
    resultPreview: String,
    formattedResult: String,
    isShiftActive: Boolean,
    isAlphaActive: Boolean,
    isHypActive: Boolean,
    angleUnit: AngleUnit,
    mode: CalculatorMode,
    hasMemory: Boolean,
    onSeekCursor: (Int) -> Unit = {},
    onPasteText: (String) -> Unit = {},
    onSwipeDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val accent = LocalAppAccent.current
    var showContextMenu by remember { mutableStateOf(false) }

    // Pulsing cursor animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursorBlink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    // Auto-scroll input toward cursor position
    LaunchedEffect(expression, cursorPosition) {
        val ratio = if (expression.isNotEmpty()) cursorPosition.toFloat() / expression.length else 1f
        val targetScroll = (scrollState.maxValue * ratio).toInt()
        scrollState.scrollTo(targetScroll)
    }

    var totalDrag by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF10141F), Color(0xFF07090E))
                    )
                )
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(AmoledDisplayBorder.copy(alpha = 0.8f), Color(0xFF161A26))
                    ),
                    RoundedCornerShape(24.dp)
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { totalDrag = 0f },
                        onDragEnd = {
                            if (totalDrag < -60f) {
                                onSwipeDelete()
                            }
                            totalDrag = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            totalDrag += dragAmount
                        }
                    )
                }
                .combinedClickable(
                    onClick = {},
                    onLongClick = { showContextMenu = true }
                )
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- 1. Top Status Bar with Polished Badges ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isShiftActive) MinimalPill("SHIFT", AccentGold)
                    if (isAlphaActive) MinimalPill("ALPHA", AccentCoral)
                    if (isHypActive) MinimalPill("HYP", accent)
                    if (hasMemory) MinimalPill("M", AccentBlue)
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val angleLabel = when (angleUnit) {
                        AngleUnit.DEGREE -> "DEG"
                        AngleUnit.RADIAN -> "RAD"
                        AngleUnit.GRADIAN -> "GRA"
                    }
                    MinimalPill(angleLabel, accent)

                    if (mode != CalculatorMode.COMP) {
                        MinimalPill(mode.name, AccentPurple)
                    }
                }
            }

            // --- 2. Formula Input Line with Smooth Pulsing Cursor ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val annotatedText = buildAnnotatedString {
                    val text = if (expression.isEmpty()) "" else expression
                    val pos = cursorPosition.coerceIn(0, text.length)

                    append(text.substring(0, pos))
                    // Pulsing animated cursor
                    withStyle(
                        style = SpanStyle(
                            background = accent.copy(alpha = cursorAlpha),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(if (pos < text.length) text[pos].toString() else " ")
                    }
                    if (pos < text.length) {
                        append(text.substring(pos + 1))
                    }
                }

                @Suppress("DEPRECATION")
                ClickableText(
                    text = annotatedText,
                    onClick = { offset -> onSeekCursor(offset) },
                    style = TextStyle(
                        color = AmoledInputText,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 30.sp
                    )
                )
            }

            // --- 3. Result Display in Dynamic Accent ---
            val displayOutput = formattedResult.ifEmpty { resultPreview }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = {
                            if (displayOutput.isNotBlank() && displayOutput != "0") {
                                clipboardManager.setText(AnnotatedString(displayOutput))
                                Toast.makeText(context, "Copied $displayOutput", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onLongClick = { showContextMenu = true }
                    ),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = displayOutput.ifEmpty { "0" },
                    color = if (displayOutput.contains("ERROR")) AccentCoral else accent,
                    fontSize = if (displayOutput.length > 12) 28.sp else if (displayOutput.length > 8) 32.sp else 38.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }

        // Long-Press Context Menu
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false },
            modifier = Modifier.background(AmoledSurface)
        ) {
            DropdownMenuItem(
                text = { Text("Copy Result", color = AmoledTextPrimary) },
                onClick = {
                    showContextMenu = false
                    val res = formattedResult.ifEmpty { resultPreview }
                    if (res.isNotBlank()) {
                        clipboardManager.setText(AnnotatedString(res))
                        Toast.makeText(context, "Result copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Copy Expression", color = AmoledTextPrimary) },
                onClick = {
                    showContextMenu = false
                    if (expression.isNotBlank()) {
                        clipboardManager.setText(AnnotatedString(expression))
                        Toast.makeText(context, "Expression copied", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            DropdownMenuItem(
                text = { Text("Paste", color = accent) },
                onClick = {
                    showContextMenu = false
                    val clip = clipboardManager.getText()?.text
                    if (!clip.isNullOrBlank()) {
                        onPasteText(clip)
                    }
                }
            )
        }
    }
}

@Composable
private fun MinimalPill(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .border(0.8.dp, color.copy(alpha = 0.45f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        )
    }
}
