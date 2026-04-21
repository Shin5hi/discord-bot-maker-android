package com.discordbotmaker.android.ui.music
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.discordbotmaker.android.ui.theme.AppColors
enum class PlaybackState { PLAYING, PAUSED, STOPPED }
data class Track(val id: String, val title: String, val artist: String, val durationSeconds: Int, val url: String = "", val thumbnailEmoji: String = "\ud83c\udfb5")
data class MusicPlayerState(val playbackState: PlaybackState = PlaybackState.STOPPED, val currentTrack: Track? = null, val progressSeconds: Int = 0, val queue: List<Track> = emptyList())
private fun fmtTime(s: Int): String { return "%d:%02d".format(s / 60, s % 60) }
@Composable fun MusicPlayerScreen(state: MusicPlayerState = MusicPlayerState(), onPlay: () -> Unit = {}, onPause: () -> Unit = {}, onSkip: () -> Unit = {}, onStop: () -> Unit = {}, onAddTrack: (String) -> Unit = {}) {
    var ps by remember { mutableStateOf(state) }; var q by remember { mutableStateOf("") }
    LaunchedEffect(state) { ps = state }
    Column(Modifier.fillMaxSize().background(AppColors.Background).verticalScroll(rememberScrollState())) {
        Surface(color = AppColors.Surface, tonalElevation = 0.dp, modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Music Player", color = AppColors.TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.SemiBold); Spacer(Modifier.height(4.dp)); Text("Stream music to your Discord voice channel.", color = AppColors.TextSecondary, fontSize = 13.sp) } }
        Spacer(Modifier.height(16.dp))
        Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = AppColors.Surface), elevation = CardDefaults.cardElevation(0.dp)) {
            Column(Modifier.padding(16.dp)) {
                val ct = ps.currentTrack; val active = ct != null && ps.playbackState != PlaybackState.STOPPED
                Text("Now Playing", color = if (active) AppColors.Primary else AppColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(12.dp))
                if (ct != null) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).background(AppColors.Primary.copy(0.08f)), contentAlignment = Alignment.Center) { Text(ct.thumbnailEmoji, fontSize = 24.sp) }
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) { Text(ct.title, color = AppColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis); Spacer(Modifier.height(2.dp)); Text(ct.artist, color = AppColors.TextSecondary, fontSize = 12.sp, maxLines = 1) }
                        val (sl, sc) = when (ps.playbackState) { PlaybackState.PLAYING -> "\u25b6 Playing" to AppColors.Success; PlaybackState.PAUSED -> "\u275a\u275a Paused" to AppColors.Warning; PlaybackState.STOPPED -> "\u25a0 Stopped" to AppColors.Error }
                        Text(sl, color = sc, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(Modifier.height(14.dp))
                    val prog = if (ct.durationSeconds > 0) (ps.progressSeconds.toFloat() / ct.durationSeconds).coerceIn(0f, 1f) else 0f
                    val ap by animateFloatAsState(prog, tween(300, easing = LinearEasing), "prog")
                    Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(AppColors.ProgressTrack)) { Box(Modifier.fillMaxHeight().fillMaxWidth(ap).clip(RoundedCornerShape(3.dp)).background(AppColors.Primary)) }
                    Spacer(Modifier.height(6.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text(fmtTime(ps.progressSeconds), color = AppColors.Primary, fontSize = 11.sp); Text(fmtTime(ct.durationSeconds), color = AppColors.TextMuted, fontSize = 11.sp) }
                } else { Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("\ud83c\udfb6", fontSize = 28.sp); Spacer(Modifier.height(6.dp)); Text("No track loaded", color = AppColors.TextMuted, fontSize = 12.sp) } } }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), Arrangement.SpaceEvenly, Alignment.CenterVertically) {
            val hasTk = ps.currentTrack != null
            CtlBtn("\u25a0", "Stop", AppColors.Error, hasTk && ps.playbackState != PlaybackState.STOPPED, onStop, 44)
            val playing = ps.playbackState == PlaybackState.PLAYING
            CtlBtn(if (playing) "\u275a\u275a" else "\u25b6", if (playing) "Pause" else "Play", AppColors.Primary, hasTk, { if (playing) onPause() else onPlay() }, 64)
            CtlBtn("\u23ed", "Skip", AppColors.Primary, hasTk, onSkip, 44)
        }
        Spacer(Modifier.height(20.dp))
        Column(Modifier.padding(horizontal = 12.dp)) { Text("Add Track", color = AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium); Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, AppColors.SurfaceBorder, RoundedCornerShape(8.dp)).background(AppColors.InputBackground), verticalAlignment = Alignment.CenterVertically) {
                Text("\ud83d\udd0d", fontSize = 16.sp, modifier = Modifier.padding(start = 12.dp)); Spacer(Modifier.width(8.dp))
                BasicTextField(q, { q = it }, Modifier.weight(1f).padding(vertical = 4.dp), textStyle = TextStyle(AppColors.TextPrimary, fontSize = 13.sp), singleLine = true, cursorBrush = SolidColor(AppColors.Primary), decorationBox = { ib -> Box { if (q.isEmpty()) Text("URL or name\u2026", color = AppColors.TextMuted, fontSize = 13.sp); ib() } })
                Spacer(Modifier.width(4.dp)); Button({ if (q.isNotBlank()) { onAddTrack(q); q = "" } }, Modifier.height(36.dp).padding(end = 6.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(AppColors.Primary, AppColors.TextPrimary), contentPadding = PaddingValues(horizontal = 14.dp)) { Text("+ Add", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            }
        }
        Spacer(Modifier.height(20.dp))
        Column(Modifier.padding(horizontal = 12.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Queue", color = AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium); Text("${ps.queue.size} track${if (ps.queue.size != 1) "s" else ""}", color = AppColors.Primary, fontSize = 11.sp) }
            Spacer(Modifier.height(8.dp))
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = AppColors.Surface), elevation = CardDefaults.cardElevation(0.dp)) {
                if (ps.queue.isEmpty()) { Box(Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("\ud83d\udced", fontSize = 24.sp); Spacer(Modifier.height(8.dp)); Text("Queue is empty", color = AppColors.TextMuted, fontSize = 13.sp) } } }
                else Column { ps.queue.forEachIndexed { i, t -> Row(Modifier.fillMaxWidth().padding(14.dp, 12.dp), verticalAlignment = Alignment.CenterVertically) { Text("#${i+1}", color = if (i == 0) AppColors.Primary else AppColors.TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.width(32.dp)); Text(t.thumbnailEmoji, fontSize = 20.sp); Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(t.title, color = AppColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(t.artist, color = AppColors.TextMuted, fontSize = 11.sp) }; Text(fmtTime(t.durationSeconds), color = AppColors.TextSecondary, fontSize = 11.sp) }; if (i < ps.queue.lastIndex) HorizontalDivider(color = AppColors.Divider, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 12.dp)) } }
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
@Composable private fun CtlBtn(icon: String, label: String, color: Color, enabled: Boolean, onClick: () -> Unit, sz: Int) {
    val c = if (enabled) color else AppColors.TextMuted
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(12.dp)).clickable(enabled, onClick = onClick).padding(8.dp)) {
        Box(Modifier.size(sz.dp).clip(CircleShape).border(if (sz > 50) 2.dp else 1.dp, c.copy(0.4f), CircleShape).background(c.copy(0.08f)), contentAlignment = Alignment.Center) { Text(icon, fontSize = if (sz > 50) 24.sp else 18.sp, color = c) }
        Spacer(Modifier.height(4.dp)); Text(label, color = c, fontSize = if (sz > 50) 11.sp else 10.sp, fontWeight = FontWeight.Medium)
    }
}
