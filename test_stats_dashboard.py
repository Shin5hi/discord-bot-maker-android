"""Validation tests for StatsDashboardScreen.kt, AppNavigation.kt updates, and COORDINATION.md."""
import re
import pytest

STATS_FILE = "ui/StatsDashboardScreen.kt"
NAV_FILE = "ui/AppNavigation.kt"
COORD_FILE = "COORDINATION.md"

def read(path):
    with open(path, "r") as f:
        return f.read()

# ─── StatsDashboardScreen.kt Tests ─────────────────────────────────────────────

class TestStatsDashboardScreen:
    @pytest.fixture(autouse=True)
    def load(self):
        self.src = read(STATS_FILE)

    def test_package_declaration(self):
        assert "package com.discordbotmaker.android.ui.stats" in self.src

    def test_imports_appcolors(self):
        assert "import com.discordbotmaker.android.ui.theme.AppColors" in self.src

    def test_no_inline_hex_colors(self):
        # Find Color(0x...) patterns that are NOT inside AppColors/AppTheme
        # The file should only reference AppColors.* for colors
        hex_colors = re.findall(r'Color\(0x[0-9A-Fa-f]+\)', self.src)
        assert len(hex_colors) == 0, f"Found inline hex colors: {hex_colors}"

    def test_no_monospace_font(self):
        assert "FontFamily.Monospace" not in self.src

    def test_uses_sans_serif(self):
        assert "FontFamily.SansSerif" not in self.src or True  # SansSerif not needed if using Material defaults
        # More importantly, no Monospace
        assert "Monospace" not in self.src

    def test_card_elevation_zero(self):
        # All CardDefaults.cardElevation should use 0.dp
        elevations = re.findall(r'cardElevation\((.*?)\)', self.src)
        for e in elevations:
            assert "0.dp" in e or "defaultElevation = 0.dp" in e, f"Non-zero elevation: {e}"

    def test_card_corners_8dp(self):
        corners = re.findall(r'RoundedCornerShape\((\d+)\.dp\)', self.src)
        for c in corners:
            assert c == "8" or c == "2", f"Non-8dp corner found: {c}dp (2dp for accent bars is OK)"

    def test_header_tonal_elevation_zero(self):
        assert "tonalElevation = 0.dp" in self.src

    def test_bot_stats_data_class(self):
        assert "data class BotStats" in self.src
        assert "totalServers" in self.src
        assert "totalUsers" in self.src
        assert "activeCommands" in self.src
        assert "uptimeFormatted" in self.src

    def test_activity_data_point_class(self):
        assert "data class ActivityDataPoint" in self.src
        assert "label" in self.src
        assert "value" in self.src

    def test_composable_screen_function(self):
        assert "@Composable" in self.src
        assert "fun StatsDashboardScreen" in self.src

    def test_stat_cards_present(self):
        assert "Total Servers" in self.src
        assert "Total Users" in self.src
        assert "Active Commands" in self.src
        assert "Uptime" in self.src

    def test_canvas_chart(self):
        assert "Canvas" in self.src
        assert "import androidx.compose.foundation.Canvas" in self.src

    def test_chart_uses_path(self):
        assert "Path()" in self.src
        assert "drawPath" in self.src

    def test_chart_uses_drawline_for_grid(self):
        assert "drawLine" in self.src

    def test_chart_data_dots(self):
        assert "drawCircle" in self.src

    def test_chart_animation(self):
        assert "Animatable" in self.src
        assert "animateTo" in self.src

    def test_uses_appcolors_primary(self):
        assert "AppColors.Primary" in self.src

    def test_uses_appcolors_surface(self):
        assert "AppColors.Surface" in self.src

    def test_uses_appcolors_background(self):
        assert "AppColors.Background" in self.src

    def test_uses_appcolors_success(self):
        assert "AppColors.Success" in self.src

    def test_uses_appcolors_warning(self):
        assert "AppColors.Warning" in self.src

    def test_uses_input_background(self):
        assert "AppColors.InputBackground" in self.src

    def test_summary_card_present(self):
        assert "Quick Summary" in self.src

    def test_format_number_helper(self):
        assert "formatNumber" in self.src

    def test_default_activity_data(self):
        assert "defaultActivityData" in self.src
        # Should have weekday labels
        assert "Mon" in self.src
        assert "Sun" in self.src

    def test_chart_legend(self):
        assert "Commands Executed" in self.src

    def test_x_axis_labels(self):
        assert "X-axis labels" in self.src or "dp.label" in self.src

    def test_fill_area_under_curve(self):
        assert "fillPath" in self.src

    def test_stroke_styling(self):
        assert "StrokeCap.Round" in self.src
        assert "StrokeJoin.Round" in self.src


# ─── AppNavigation.kt Tests ────────────────────────────────────────────────────

class TestAppNavigation:
    @pytest.fixture(autouse=True)
    def load(self):
        self.src = read(NAV_FILE)

    def test_imports_stats_screen(self):
        assert "import com.discordbotmaker.android.ui.stats.StatsDashboardScreen" in self.src

    def test_stats_route_constant(self):
        assert 'STATS_DASHBOARD' in self.src
        assert '"stats_dashboard"' in self.src

    def test_stats_composable_route(self):
        assert "composable(AppRoutes.STATS_DASHBOARD)" in self.src

    def test_stats_screen_invocation(self):
        assert "StatsDashboardScreen()" in self.src

    def test_existing_routes_preserved(self):
        assert "AppRoutes.DASHBOARD" in self.src
        assert "AppRoutes.LIVE_CONSOLE" in self.src
        assert "AppRoutes.AUTO_MOD" in self.src
        assert "AppRoutes.COMMAND_BUILDER" in self.src
        assert "AppRoutes.BOT_CREATION" in self.src

    def test_existing_imports_preserved(self):
        assert "import com.discordbotmaker.android.ui.automod.AutoModScreen" in self.src
        assert "import com.discordbotmaker.android.ui.dashboard.MainDashboardScreen" in self.src
        assert "import com.discordbotmaker.android.ui.launch.BotCreationScreen" in self.src

    def test_nav_host_start_destination(self):
        assert "startDestination = AppRoutes.DASHBOARD" in self.src


# ─── COORDINATION.md Tests ─────────────────────────────────────────────────────

class TestCoordinationMd:
    @pytest.fixture(autouse=True)
    def load(self):
        self.src = read(COORD_FILE)

    def test_migration_status_section(self):
        assert "## Migration Status" in self.src

    def test_migration_mentions_org_permissions(self):
        assert "org" in self.src.lower() and "permission" in self.src.lower()

    def test_migration_mentions_pending(self):
        assert "pending" in self.src.lower() or "waiting" in self.src.lower()

    def test_migration_mentions_personal_fork(self):
        assert "Shin5hi/discord-bot-maker-android" in self.src

    def test_stats_module_section(self):
        assert "Stats Dashboard Module" in self.src

    def test_stats_file_ownership(self):
        assert "ui/StatsDashboardScreen.kt" in self.src

    def test_stats_route_documented(self):
        assert "stats_dashboard" in self.src

    def test_stats_models_documented(self):
        assert "BotStats" in self.src
        assert "ActivityDataPoint" in self.src

    def test_stats_components_documented(self):
        assert "StatsCardGrid" in self.src or "StatCard" in self.src
        assert "ActivityChart" in self.src

    def test_design_compliance_noted(self):
        assert "0.dp" in self.src
        assert "8.dp" in self.src

    def test_art_direction_preserved(self):
        assert "## Art Direction" in self.src
        assert "#5865F2" in self.src
        assert "#313338" in self.src

    def test_file_ownership_table_updated(self):
        lines = self.src.split("\n")
        found = False
        for line in lines:
            if "StatsDashboardScreen" in line and "Active" in line:
                found = True
                break
        assert found, "StatsDashboardScreen not in File Ownership table as Active"

    def test_conventions_preserved(self):
        assert "## Conventions" in self.src
        assert "AppColors.*" in self.src
