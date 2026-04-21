package com.discordbotmaker.android.ui.commands
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
enum class ResponseType(val label: String, val icon: String, val accent: Color) { TEXT("Text", "\ud83d\udcac", AppColors.Primary), EMBED("Embed", "\ud83d\udccb", AppColors.Primary), RANDOM_MEME("Meme", "\ud83c\udfb2", AppColors.Warning) }
data class BotCommand(val name: String, val responseType: ResponseType = ResponseType.TEXT, val responseContent: String = "")
@Composable fun CommandBuilderScreen(init: List<BotCommand> = emptyList(), onCloud: (List<BotCommand>) -> Unit = {}, onDel: (BotCommand) -> Unit = {}, onSave: (BotCommand) -> Unit = {}) {
    var cmds by remember { mutableStateOf(init) }; var dlg by remember { mutableStateOf(false) }; var ed by remember { mutableStateOf<BotCommand?>(null) }; var saving by remember { mutableStateOf(false) }
    LaunchedEffect(init) { cmds = init }
    Column(Modifier.fillMaxSize().background(AppColors.Background).verticalScroll(rememberScrollState())) {
        Surface(color = AppColors.Surface, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Command Builder", color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(4.dp)); Text("Create and manage custom bot commands.", color = AppColors.TextSecondary, fontSize = 13.sp) } }
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = AppColors.Surface), elevation = CardDefaults.cardElevation(0.dp)) { Row(Modifier.fillMaxWidth().padding(16.dp, 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) { Row(verticalAlignment = Alignment.CenterVertically) { Text("\u26a1", fontSize = 16.sp); Spacer(Modifier.width(8.dp)); Text("Registered Commands", color = AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium) }; Text("${cmds.size}", color = AppColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Bold) } }
        Spacer(Modifier.height(16.dp))
        Column(Modifier.padding(horizontal = 12.dp)) { Text("Commands", color = AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium); Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = AppColors.Surface), elevation = CardDefaults.cardElevation(0.dp)) {
                if (cmds.isEmpty()) { Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("\ud83e\udd16", fontSize = 32.sp); Spacer(Modifier.height(8.dp)); Text("No commands yet", color = AppColors.TextMuted, fontSize = 14.sp) } } }
                else Column { cmds.forEachIndexed { i, c ->
                    Row(Modifier.fillMaxWidth().clickable { ed = c; dlg = true }.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(c.responseType.accent.copy(0.1f)), contentAlignment = Alignment.Center) { Text("/", color = c.responseType.accent, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                        Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text("/${c.name}", color = AppColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis); Row { Text(c.responseType.icon, fontSize = 10.sp); Spacer(Modifier.width(4.dp)); Text(c.responseType.label, color = c.responseType.accent, fontSize = 11.sp) } }
                        Box(Modifier.size(32.dp).clip(RoundedCornerShape(6.dp)).clickable { ed = c; dlg = true }.background(AppColors.Primary.copy(0.08f)), contentAlignment = Alignment.Center) { Text("\u270e", color = AppColors.Primary, fontSize = 14.sp) }
                        Spacer(Modifier.width(6.dp)); Box(Modifier.size(32.dp).clip(RoundedCornerShape(6.dp)).clickable { cmds = cmds.filter { it.name != c.name }; onDel(c) }.background(AppColors.Error.copy(0.08f)), contentAlignment = Alignment.Center) { Text("\u2715", color = AppColors.Error, fontSize = 14.sp) }
                    }; if (i < cmds.lastIndex) HorizontalDivider(color = AppColors.Divider, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 12.dp))
                } }
            }
        }
        Spacer(Modifier.height(16.dp))
        Button({ ed = null; dlg = true }, Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(AppColors.Primary.copy(0.12f), AppColors.Primary)) { Text("+ Add Command", fontWeight = FontWeight.SemiBold, fontSize = 14.sp) }
        Spacer(Modifier.height(24.dp))
        Button({ saving = true; onCloud(cmds) }, cmds.isNotEmpty() && !saving, Modifier.fillMaxWidth().padding(horizontal = 12.dp).height(56.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(AppColors.Primary, AppColors.TextPrimary, AppColors.SurfaceVariant, AppColors.TextMuted)) { Text("\u2601 Save to Cloud", fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
        Spacer(Modifier.height(32.dp))
    }
    if (dlg) CmdDialog(ed, cmds.map { it.name }.toSet()) { cmd -> if (cmd != null) { if (ed != null) cmds = cmds.map { if (it.name == ed!!.name) cmd else it } else cmds = cmds + cmd; onSave(cmd) }; dlg = false; ed = null }
}
@Composable private fun CmdDialog(ex: BotCommand?, names: Set<String>, onResult: (BotCommand?) -> Unit) {
    val isEd = ex != null; var nm by remember { mutableStateOf(ex?.name ?: "") }; var tp by remember { mutableStateOf(ex?.responseType ?: ResponseType.TEXT) }; var ct by remember { mutableStateOf(ex?.responseContent ?: "") }; var er by remember { mutableStateOf<String?>(null) }
    AlertDialog({ onResult(null) }, { Button({ val t = nm.trim().lowercase(); er = when { t.isEmpty() -> "Required"; !t.matches(Regex("^[a-z0-9_-]+$")) -> "Invalid"; !isEd && names.contains(t) -> "Exists"; else -> null }; if (er == null && ct.isNotBlank()) onResult(BotCommand(t, tp, ct.trim())) }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(AppColors.Primary, AppColors.TextPrimary)) { Text(if (isEd) "Update" else "Save", fontWeight = FontWeight.SemiBold) } }, Modifier,
        { OutlinedButton({ onResult(null) }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = AppColors.TextSecondary)) { Text("Cancel") } },
        title = { Text(if (isEd) "Edit" else "New Command", color = AppColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) },
        text = { Column {
            Text("Name", color = AppColors.TextSecondary, fontSize = 12.sp); Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, if (er != null) AppColors.Error else AppColors.SurfaceBorder, RoundedCornerShape(8.dp)).background(AppColors.InputBackground), verticalAlignment = Alignment.CenterVertically) {
                Text("/", color = AppColors.Primary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)); Spacer(Modifier.width(4.dp))
                BasicTextField(nm, { nm = it.lowercase().take(32); er = null }, Modifier.weight(1f).padding(end = 12.dp), !isEd, textStyle = TextStyle(if (isEd) AppColors.TextMuted else AppColors.TextPrimary, fontSize = 14.sp), singleLine = true, cursorBrush = SolidColor(AppColors.Primary), decorationBox = { ib -> Box { if (nm.isEmpty()) Text("e.g. ping", color = AppColors.TextMuted, fontSize = 14.sp); ib() } })
            }; if (er != null) Text(er!!, color = AppColors.Error, fontSize = 11.sp)
            Spacer(Modifier.height(12.dp)); Text("Type", color = AppColors.TextSecondary, fontSize = 12.sp); Spacer(Modifier.height(8.dp))
            Row(Arrangement.spacedBy(8.dp), Modifier.fillMaxWidth()) { ResponseType.entries.forEach { t2 -> val s = tp == t2; val bc by animateColorAsState(if (s) t2.accent else AppColors.SurfaceBorder, tween(200), "c${t2.ordinal}"); Box(Modifier.weight(1f).clip(RoundedCornerShape(8.dp)).border(1.dp, bc, RoundedCornerShape(8.dp)).background(if (s) t2.accent.copy(0.1f) else Color.Transparent).clickable { tp = t2 }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text(t2.icon, fontSize = 16.sp); Spacer(Modifier.height(4.dp)); Text(t2.label, color = if (s) t2.accent else AppColors.TextSecondary, fontSize = 11.sp) } } } }
            Spacer(Modifier.height(12.dp)); Text("Response", color = AppColors.TextSecondary, fontSize = 12.sp); Spacer(Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth().heightIn(80.dp, 140.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, AppColors.SurfaceBorder, RoundedCornerShape(8.dp)).background(AppColors.InputBackground).padding(12.dp)) { BasicTextField(ct, { ct = it.take(500) }, Modifier.fillMaxWidth(), textStyle = TextStyle(AppColors.TextPrimary, fontSize = 13.sp), cursorBrush = SolidColor(AppColors.Primary), decorationBox = { ib -> Box { if (ct.isEmpty()) Text("Type\u2026", color = AppColors.TextMuted, fontSize = 13.sp); ib() } }) }
            Text("${ct.length}/500", color = AppColors.TextMuted, fontSize = 11.sp, modifier = Modifier.align(Alignment.End))
        } }, shape = RoundedCornerShape(12.dp), containerColor = AppColors.Surface)
}
