package com.discordbotmaker.android.feature.orion

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OrionAssistantOverlay(
    currentRoute: String?,
    modifier: Modifier = Modifier,
) {
    var sheetVisible by remember { mutableStateOf(false) }
    val content = remember(currentRoute) { orionContentForRoute(currentRoute) }
    val compactMode = currentRoute == "home"

    if (sheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { sheetVisible = false },
            containerColor = Color(0xFF171A24),
            contentColor = Color(0xFFF5F7FF),
            dragHandle = null,
        ) {
            OrionAssistantSheet(
                content = content,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            )
        }
    }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .clickable { sheetVisible = true },
            color = Color.Transparent,
            tonalElevation = 0.dp,
            shadowElevation = 18.dp,
        ) {
            Row(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF23263A), Color(0xFF171A24)),
                        ),
                    )
                    .padding(
                        horizontal = if (compactMode) 8.dp else 10.dp,
                        vertical = if (compactMode) 8.dp else 10.dp,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(if (compactMode) 0.dp else 10.dp),
            ) {
                OrionBubbleFace(compact = compactMode)
                if (!compactMode) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Orión",
                            color = Color(0xFFF5F7FF),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = content.hint,
                            color = Color(0xFF9DA6BE),
                            fontSize = 11.sp,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrionBubbleFace(compact: Boolean = false) {
    Box(
        modifier = Modifier
            .size(if (compact) 40.dp else 44.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6472FF), Color(0xFF4A57E8)),
                ),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 26.dp else 28.dp)
                .background(Color(0xFF0F1220), RoundedCornerShape(if (compact) 10.dp else 11.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(if (compact) 3.dp else 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = ">",
                    color = Color(0xFF28E3F0),
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 12.sp else 13.sp,
                )
                Text(
                    text = "_",
                    color = Color(0xFF28E3F0),
                    fontWeight = FontWeight.Bold,
                    fontSize = if (compact) 12.sp else 13.sp,
                )
            }
        }
    }
}

@Composable
private fun OrionAssistantSheet(
    content: OrionSheetContent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OrionBubbleFace()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Orión",
                    color = Color(0xFFF5F7FF),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = content.title,
                    color = Color(0xFF9DA6BE),
                    fontSize = 14.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            color = Color(0xFF111521),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                OrionSection(label = "What this area does", body = content.summary)
                HorizontalDivider(color = Color(0xFF2C3347))
                OrionSection(label = "What to check", body = content.checklist)
                HorizontalDivider(color = Color(0xFF2C3347))
                OrionSection(label = "Next step", body = content.nextStep)
            }
        }
    }
}

@Composable
private fun OrionSection(
    label: String,
    body: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = Color(0xFF6F83FF),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = body,
            color = Color(0xFFE6EAF7),
            fontSize = 14.sp,
            lineHeight = 21.sp,
        )
    }
}

private data class OrionSheetContent(
    val title: String,
    val hint: String,
    val summary: String,
    val checklist: String,
    val nextStep: String,
)

private fun orionContentForRoute(route: String?): OrionSheetContent {
    return when (route) {
        "create-bot" -> OrionSheetContent(
            title = "Bot setup guidance",
            hint = "URL, token and connection help",
            summary = "Here you connect your backend, register the bot token and define the basics that let Grid talk to your running bot.",
            checklist = "Make sure the backend URL is reachable, the bot token is valid and the connection details match the bot you really want to control.",
            nextStep = "If something fails, start by checking the backend URL first and only then the token. That usually saves time.",
        )
        "console" -> OrionSheetContent(
            title = "Live runtime help",
            hint = "Logs and connection clues",
            summary = "This view is your live feed. It tells you whether the bot connected, failed, or is missing something in the backend runtime.",
            checklist = "Look for startup errors, disconnect messages or repeated warnings. If logs stop moving, the backend may be down or the socket may have dropped.",
            nextStep = "Use the latest visible error as your clue. Fix one concrete issue first instead of chasing the whole log at once.",
        )
        "automod" -> OrionSheetContent(
            title = "Moderation rules help",
            hint = "Links, spam and thresholds",
            summary = "This area controls the defaults your bot will use for links, spam and moderation actions. Small changes here can have strong effects.",
            checklist = "Check whether link blocking, spam windows and punishment actions match the community you actually want to manage.",
            nextStep = "Keep your first rules conservative. It is safer to tighten them later than to over-block people on day one.",
        )
        else -> OrionSheetContent(
            title = "Quick builder guidance",
            hint = "Tap me if something feels unclear",
            summary = "I am your in-app guide. Use me when you want the short version of what a section does, what is missing and what to do next.",
            checklist = "On the home screen, check the bot status first. If the bot is not configured, go to setup. If it is configured but not running, use the start controls and then the console.",
            nextStep = "The safest path is always the same: connect the bot, confirm status, then watch the console before changing anything more advanced.",
        )
    }
}
