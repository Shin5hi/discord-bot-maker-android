package com.discordbotmaker.android.feature.templates

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.R

private data class TreeNode(
    val name: String,
    val icon: String,
    val premium: Boolean = false,
)

private data class TemplateCategory(
    val title: String,
    val accent: Color,
    val nodes: List<TreeNode>,
)

private val gridTree = listOf(
    TemplateCategory(
        title = "LAUNCH & DEPLOY",
        accent = Color(0xFF7B86FF),
        nodes = listOf(
            TreeNode("token-connect", "#"),
            TreeNode("quick-deploy", "⌁"),
            TreeNode("bot-packager", "▣"),
            TreeNode("hot-reload", "⟳"),
        ),
    ),
    TemplateCategory(
        title = "MODERATION",
        accent = Color(0xFF41E8C8),
        nodes = listOf(
            TreeNode("auto-ban", "#"),
            TreeNode("message-purge", "#"),
            TreeNode("warning-system", "⚑"),
            TreeNode("audit-logger", "▣"),
        ),
    ),
    TemplateCategory(
        title = "AI & INTELLIGENCE",
        accent = Color(0xFFC07BFF),
        nodes = listOf(
            TreeNode("chat-ai", "#"),
            TreeNode("toxicity-filter", "▣"),
            TreeNode("auto-summary", "#"),
            TreeNode("image-gen", "#", premium = true),
        ),
    ),
    TemplateCategory(
        title = "COMMANDS & UTILITIES",
        accent = Color(0xFF22D3EE),
        nodes = listOf(
            TreeNode("slash-commands", "#"),
            TreeNode("role-manager", "#"),
            TreeNode("reminders", "⚑"),
            TreeNode("asistente-orion", "✦"),
        ),
    ),
)

@Composable
fun TemplatesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0D15), Color(0xFF111521), Color(0xFF151925)),
                ),
            )
            .verticalScroll(rememberScrollState())
            .padding(bottom = 94.dp),
    ) {
        TemplatesHeader()
        SearchStrip()
        OrionBanner()
        WorkspaceSummaryCard()
        TreeWorkspaceCard()
    }
}

@Composable
private fun TemplatesHeader() {
    Surface(
        color = Color(0xFF111521),
        tonalElevation = 0.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                color = Color(0xFF1A1F31),
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 8.dp,
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .padding(7.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Grid logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "Grid",
                    color = Color(0xFFF5F7FF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Server Tree View",
                    color = Color(0xFF95A0BB),
                    fontSize = 11.sp,
                )
            }

            Surface(
                color = Color(0xFF1B2030),
                shape = RoundedCornerShape(999.dp),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF33D17A)),
                    )
                    Text(
                        text = "16 tools",
                        color = Color(0xFFDDE3F2),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }

    HorizontalDivider(color = Color(0xFF20263A), thickness = 1.dp)
}

@Composable
private fun SearchStrip() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Surface(
            color = Color(0xFF141A29),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "⌕",
                    color = Color(0xFF7D879E),
                    fontSize = 13.sp,
                )
                Text(
                    text = "Search templates",
                    color = Color(0xFF7D879E),
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                )
                Surface(
                    color = Color(0x1A6C78FF),
                    shape = RoundedCornerShape(999.dp),
                ) {
                    Text(
                        text = "Live tree",
                        color = Color(0xFF8D9AFF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun OrionBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x0F6C78FF))
            .border(1.dp, Color(0x1E6C78FF), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "✦",
            color = Color(0xFF7B86FF),
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Asistente Orión",
            color = Color(0xFF7B86FF),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Surface(
            color = Color(0x1933D17A),
            shape = RoundedCornerShape(4.dp),
        ) {
            Text(
                text = "NEW",
                color = Color(0xFF33D17A),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "›",
            color = Color(0xFF7B86FF),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun WorkspaceSummaryCard() {
    Surface(
        color = Color(0xFF111521),
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "Map your bot like a real server system.",
                color = Color(0xFFF5F7FF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 23.sp,
            )
            Text(
                text = "Each branch reflects capabilities, modules and premium tools. Orión can explain what every node needs before you deploy it.",
                color = Color(0xFF9AA5BE),
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SummaryPill(label = "4 categories", accent = Color(0xFF7B86FF))
                SummaryPill(label = "16 nodes", accent = Color(0xFF22D3EE))
                SummaryPill(label = "1 PRO", accent = Color(0xFFFFC857))
            }
        }
    }
}

@Composable
private fun SummaryPill(
    label: String,
    accent: Color,
) {
    Surface(
        color = accent.copy(alpha = 0.12f),
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = label,
            color = accent,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun TreeWorkspaceCard() {
    Surface(
        color = Color(0xFF101521),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            gridTree.forEach { category ->
                TreeCategorySection(category)
            }
        }
    }
}

@Composable
private fun TreeCategorySection(category: TemplateCategory) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(start = 8.dp, end = 10.dp, top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (expanded) "▾" else "▸",
                color = Color(0xFF7D879E),
                fontSize = 11.sp,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(category.accent),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.title,
                color = Color(0xFF8E99B3),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${category.nodes.size}",
                color = Color(0xFF5E6882),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (expanded) {
            category.nodes.forEachIndexed { index, node ->
                TreeNodeRow(
                    node = node,
                    accent = category.accent,
                    isLast = index == category.nodes.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun TreeNodeRow(
    node: TreeNode,
    accent: Color,
    isLast: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.width(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(10.dp)
                    .background(Color(0xFF2A3042)),
            )
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(1.dp)
                    .background(Color(0xFF2A3042)),
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(if (isLast) 10.dp else 36.dp)
                    .background(Color(0xFF2A3042)),
            )
        }

        Surface(
            color = Color(0xFF141A27),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.13f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = node.icon,
                        color = accent,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = node.name,
                        color = Color(0xFFDDE3F2),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.SansSerif,
                    )
                    Text(
                        text = if (node.premium) "Premium module ready for advanced flows" else "Drag this node into your server tree",
                        color = Color(0xFF74809A),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                    )
                }

                if (node.premium) {
                    Surface(
                        color = Color(0x19FFC857),
                        shape = RoundedCornerShape(6.dp),
                    ) {
                        Text(
                            text = "PRO",
                            color = Color(0xFFFFC857),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp),
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "+",
                        color = accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
