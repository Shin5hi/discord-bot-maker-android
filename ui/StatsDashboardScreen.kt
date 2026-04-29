package com.discordbotmaker.android.ui.stats

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors

// ─── Data Models ─────────────────────────────────────────────────────────────────

data class BotStats(
    val totalServers: Int = 0,
    val totalUsers: Int = 0,
    val activeCommands: Int = 0,
    val uptimeFormatted: String = "—"
)

data class ActivityDataPoint(
    val label: String,
    val value: Float
)

// ─── Stats Dashboard Screen ─────────────────────────────────────────────────────

@Composable
fun StatsDashboardScreen(
    stats: BotStats = BotStats(),
    activityData: List<ActivityDataPoint> = defaultActivityData()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        StatsHeader()

        Spacer(Modifier.height(16.dp))

        // Stats cards grid — 2x2
        StatsCardGrid(stats)

        Spacer(Modifier.height(20.dp))

        // Activity chart section
        Text(
            text = "Activity Over Time",
            color = AppColors.TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        ActivityChart(activityData)

        Spacer(Modifier.height(20.dp))

        // Summary section
        SummaryCard(stats)

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Stats Dashboard \u00b7 discord-bot-maker",
            color = AppColors.TextMuted,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}

// ─── Header ──────────────────────────────────────────────────────────────────────

@Composable
private fun StatsHeader() {
    Surface(
        color = AppColors.Surface,
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bot Statistics",
                color = AppColors.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Overview of your bot's performance and reach.",
                color = AppColors.TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

// ─── Stat Cards Grid (2x2) ──────────────────────────────────────────────────────

@Composable
private fun StatsCardGrid(stats: BotStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = "\ud83c\udf10",
                label = "Total Servers",
                value = formatNumber(stats.totalServers),
                accentColor = AppColors.Primary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = "\ud83d\udc65",
                label = "Total Users",
                value = formatNumber(stats.totalUsers),
                accentColor = AppColors.Success,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                icon = "\u26a1",
                label = "Active Commands",
                value = "${stats.activeCommands}",
                accentColor = AppColors.Warning,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = "\u23f1",
                label = "Uptime",
                value = stats.uptimeFormatted,
                accentColor = AppColors.Info,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: String,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 16.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = label,
                    color = AppColors.TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = value,
                color = AppColors.TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(3.dp)
                    .background(accentColor, RoundedCornerShape(2.dp))
            )
        }
    }
}

// ─── Activity Line Chart (Compose Canvas) ───────────────────────────────────────

@Composable
private fun ActivityChart(data: List<ActivityDataPoint>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Chart area
            val animProgress = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                animProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.InputBackground)
            ) {
                if (data.isNotEmpty()) {
                    val maxVal = data.maxOf { it.value }.coerceAtLeast(1f)
                    val progress = animProgress.value

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        val w = size.width
                        val h = size.height
                        val stepX = if (data.size > 1) w / (data.size - 1) else w
                        val gridLineCount = 4

                        // Grid lines
                        for (i in 0..gridLineCount) {
                            val y = h * i / gridLineCount
                            drawLine(
                                color = AppColors.Divider.copy(alpha = 0.3f),
                                start = Offset(0f, y),
                                end = Offset(w, y),
                                strokeWidth = 1f
                            )
                        }

                        // Build points
                        val points = data.mapIndexed { index, dp ->
                            val x = stepX * index
                            val y = h - (dp.value / maxVal * h)
                            Offset(x, y)
                        }

                        // Animated number of visible segments
                        val visibleCount = (points.size * progress).toInt().coerceAtLeast(1)

                        // Fill area under the line
                        if (visibleCount >= 2) {
                            val fillPath = Path().apply {
                                moveTo(points[0].x, h)
                                lineTo(points[0].x, points[0].y)
                                for (i in 1 until visibleCount) {
                                    lineTo(points[i].x, points[i].y)
                                }
                                lineTo(points[visibleCount - 1].x, h)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                color = AppColors.Primary.copy(alpha = 0.08f)
                            )
                        }

                        // Line
                        if (visibleCount >= 2) {
                            val linePath = Path().apply {
                                moveTo(points[0].x, points[0].y)
                                for (i in 1 until visibleCount) {
                                    lineTo(points[i].x, points[i].y)
                                }
                            }
                            drawPath(
                                path = linePath,
                                color = AppColors.Primary,
                                style = Stroke(
                                    width = 2.5f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        // Data points (dots)
                        for (i in 0 until visibleCount) {
                            drawCircle(
                                color = AppColors.Primary,
                                radius = 4f,
                                center = points[i]
                            )
                            drawCircle(
                                color = AppColors.Surface,
                                radius = 2f,
                                center = points[i]
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No activity data available",
                            color = AppColors.TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // X-axis labels
            if (data.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    data.forEach { dp ->
                        Text(
                            text = dp.label,
                            color = AppColors.TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Legend
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AppColors.Primary)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Commands Executed",
                    color = AppColors.TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ─── Summary Card ────────────────────────────────────────────────────────────────

@Composable
private fun SummaryCard(stats: BotStats) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick Summary",
                color = AppColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = AppColors.Divider, thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            SummaryRow(label = "Avg Users/Server", value = avgUsersPerServer(stats))
            Spacer(Modifier.height(8.dp))
            SummaryRow(label = "Commands Available", value = "${stats.activeCommands}")
            Spacer(Modifier.height(8.dp))
            SummaryRow(label = "Status", value = "Operational", valueColor = AppColors.Success)
            Spacer(Modifier.height(8.dp))
            SummaryRow(label = "Uptime", value = stats.uptimeFormatted)
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = AppColors.TextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = AppColors.TextSecondary,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────────

private fun formatNumber(n: Int): String {
    return when {
        n >= 1_000_000 -> "%.1fM".format(n / 1_000_000f)
        n >= 1_000 -> "%.1fK".format(n / 1_000f)
        else -> "$n"
    }
}

private fun avgUsersPerServer(stats: BotStats): String {
    if (stats.totalServers == 0) return "—"
    val avg = stats.totalUsers.toFloat() / stats.totalServers
    return "%.1f".format(avg)
}

private fun defaultActivityData(): List<ActivityDataPoint> = listOf(
    ActivityDataPoint("Mon", 120f),
    ActivityDataPoint("Tue", 185f),
    ActivityDataPoint("Wed", 142f),
    ActivityDataPoint("Thu", 230f),
    ActivityDataPoint("Fri", 198f),
    ActivityDataPoint("Sat", 310f),
    ActivityDataPoint("Sun", 265f)
)
