"""
Validation tests for the Grid-branded Discord Design Language UI.
Ensures all Kotlin files use the correct Discord color palette,
typography, Grid branding, and component style consistently.
"""
import os
import re
import pytest

PROJECT_DIR = os.path.dirname(os.path.abspath(__file__))

# All .kt files to check
UI_FILES = [
    "ui/AppTheme.kt",
    "ui/SplashScreen.kt",
    "ui/MainDashboardScreen.kt",
    "LiveConsoleScreen.kt",
    "AutoModScreen.kt",
    "BotCreationScreen.kt",
]


def read_file(relative_path: str) -> str:
    full_path = os.path.join(PROJECT_DIR, relative_path)
    with open(full_path, "r") as f:
        return f.read()


# ─── AppTheme.kt Tests ───────────────────────────────────────────────────────

class TestAppThemeColors:
    """Verify Discord color palette in AppTheme.kt."""

    def setup_method(self):
        self.content = read_file("ui/AppTheme.kt")

    def test_primary_blurple(self):
        assert "0xFF5865F2" in self.content, "Primary Blurple #5865F2 missing"

    def test_background_color(self):
        assert "0xFF313338" in self.content, "Background #313338 missing"

    def test_surface_color(self):
        assert "0xFF2B2D31" in self.content, "Surface #2B2D31 missing"

    def test_text_primary_white(self):
        assert "0xFFFFFFFF" in self.content, "TextPrimary #FFFFFF missing"

    def test_text_secondary(self):
        assert "0xFFB5BAC1" in self.content, "TextSecondary #B5BAC1 missing"

    def test_success_green(self):
        assert "0xFF23A559" in self.content, "Success Green #23A559 missing"

    def test_danger_red(self):
        assert "0xFFF23F43" in self.content, "Danger Red #F23F43 missing"

    def test_input_background(self):
        assert "0xFF1E1F22" in self.content, "InputBackground #1E1F22 missing"

    def test_no_old_generic_blue(self):
        assert "0xFF007BFF" not in self.content, "Old generic blue #007BFF should be removed"

    def test_no_old_background(self):
        assert "0xFF121212" not in self.content, "Old background #121212 should be removed"

    def test_no_old_surface(self):
        assert "0xFF1E1E1E" not in self.content, "Old surface #1E1E1E should be removed"


class TestAppThemeTypography:
    """Verify typography uses SansSerif only."""

    def setup_method(self):
        self.content = read_file("ui/AppTheme.kt")

    def test_uses_sans_serif(self):
        assert "FontFamily.SansSerif" in self.content

    def test_no_monospace(self):
        assert "FontFamily.Monospace" not in self.content, "Monospace should not be in AppTheme"


class TestAppThemeGridBranding:
    """Verify Grid branding identity comment block in AppTheme.kt."""

    def setup_method(self):
        self.content = read_file("ui/AppTheme.kt")

    def test_grid_branding_comment_present(self):
        assert "Grid Branding Identity" in self.content, "Grid branding identity comment block missing"

    def test_mentions_minimalist_geometric_g(self):
        assert "Minimalist geometric" in self.content, "Logo description missing"

    def test_mentions_blurple_in_branding(self):
        assert "Blurple" in self.content, "Blurple mention missing in branding block"

    def test_mentions_deep_charcoal(self):
        assert "Deep Charcoal" in self.content, "Deep Charcoal mention missing"

    def test_mentions_inter_roboto(self):
        assert "Inter" in self.content and "Roboto" in self.content, (
            "Inter/Roboto typeface mention missing"
        )

    def test_no_monospace_directive(self):
        assert "No monospace" in self.content.lower() or "no monospace" in self.content.lower(), (
            "No-monospace directive missing from branding block"
        )


# ─── SplashScreen.kt Tests ──────────────────────────────────────────────────

class TestSplashScreen:
    """Verify Grid splash screen structure."""

    def setup_method(self):
        self.content = read_file("ui/SplashScreen.kt")

    def test_file_exists(self):
        assert len(self.content) > 0, "SplashScreen.kt should not be empty"

    def test_splash_composable_function(self):
        assert "fun SplashScreen" in self.content, "SplashScreen composable missing"

    def test_uses_app_colors(self):
        assert "import com.discordbotmaker.android.ui.theme.AppColors" in self.content

    def test_uses_blurple_background(self):
        assert "AppColors.Primary" in self.content, "Blurple primary color not used in splash"

    def test_has_pulsing_animation(self):
        assert "pulseAlpha" in self.content or "pulse" in self.content.lower(), (
            "Pulsing animation not found in SplashScreen"
        )

    def test_shows_grid_logo_text(self):
        assert '"G"' in self.content, 'Grid "G" logo placeholder missing'

    def test_shows_grid_name(self):
        assert '"Grid"' in self.content, 'Grid name text missing from splash'

    def test_uses_sans_serif(self):
        assert "FontFamily.SansSerif" in self.content, "Splash must use SansSerif font"

    def test_no_monospace(self):
        assert "FontFamily.Monospace" not in self.content, "Splash must not use Monospace"

    def test_on_splash_complete_callback(self):
        assert "onSplashComplete" in self.content, "Splash completion callback missing"

    def test_has_delay(self):
        assert "delay" in self.content, "Splash should auto-navigate after a delay"


# ─── MainDashboardScreen.kt Tests ────────────────────────────────────────────

class TestMainDashboardGridBranding:
    """Verify Grid branding in dashboard header."""

    def setup_method(self):
        self.content = read_file("ui/MainDashboardScreen.kt")

    def test_grid_bot_hub_title(self):
        assert "Grid Bot Hub" in self.content, "Header should show 'Grid Bot Hub'"

    def test_no_generic_dashboard_only_title(self):
        # Should not have a standalone "Dashboard" as the main header text
        # (it's fine as part of other strings)
        lines = self.content.split("\n")
        for line in lines:
            if 'text = "Dashboard"' in line:
                pytest.fail("Generic 'Dashboard' title should be replaced with 'Grid Bot Hub'")

    def test_grid_brand_mark_in_header(self):
        assert '"G"' in self.content, 'Grid "G" brand mark missing from header'

    def test_version_string_branded(self):
        assert "Grid Bot Hub v" in self.content, "Version string should include Grid Bot Hub"

    def test_no_old_version_string(self):
        assert "discord-bot-maker v" not in self.content, "Old version string still present"


# ─── AppNavigation.kt Tests ──────────────────────────────────────────────────

class TestAppNavigationSplash:
    """Verify splash screen is integrated in navigation."""

    def setup_method(self):
        self.content = read_file("ui/AppNavigation.kt")

    def test_splash_route_defined(self):
        assert 'SPLASH' in self.content, "SPLASH route constant missing"

    def test_splash_is_start_destination(self):
        assert "AppRoutes.SPLASH" in self.content, "Splash should be referenced in nav"

    def test_splash_import(self):
        assert "import com.discordbotmaker.android.ui.splash.SplashScreen" in self.content

    def test_splash_composable_route(self):
        assert "SplashScreen(" in self.content, "SplashScreen composable not wired in NavGraph"


# ─── Cross-file consistency tests ─────────────────────────────────────────────

class TestNoOldColors:
    """Ensure no UI file references the old color palette inline."""

    OLD_COLORS = [
        "0xFF121212",  # old Background
        "0xFF1E1E1E",  # old Surface
        "0xFF007BFF",  # old Primary blue
        "0xFF17A2B8",  # old Info teal
        "0xFF28A745",  # old Success green
        "0xFFDC3545",  # old Error red
    ]

    @pytest.mark.parametrize("filepath", UI_FILES)
    def test_no_old_colors(self, filepath):
        content = read_file(filepath)
        for old_hex in self.OLD_COLORS:
            assert old_hex not in content, (
                f"{filepath} still contains old color {old_hex}"
            )


class TestNoNeonOrGlowEffects:
    """Ensure all neon/glow/terminal effects are removed."""

    BANNED_PATTERNS = [
        "neonGlow",
        "neonGlowBrush",
        "scanline",
        "CRT",
    ]

    @pytest.mark.parametrize("filepath", UI_FILES)
    def test_no_neon_glow(self, filepath):
        content = read_file(filepath)
        for pattern in self.BANNED_PATTERNS:
            assert pattern not in content, (
                f"{filepath} contains banned pattern '{pattern}'"
            )


class TestNoMonospaceInUI:
    """Ensure Monospace is not used anywhere."""

    @pytest.mark.parametrize("filepath", UI_FILES)
    def test_no_monospace(self, filepath):
        content = read_file(filepath)
        assert "FontFamily.Monospace" not in content, (
            f"{filepath} still uses FontFamily.Monospace"
        )


class TestAllScreensUseAppColors:
    """Ensure screens import AppColors, not inline Color() hex."""

    SCREEN_FILES = [f for f in UI_FILES if f != "ui/AppTheme.kt"]

    @pytest.mark.parametrize("filepath", SCREEN_FILES)
    def test_imports_app_colors(self, filepath):
        content = read_file(filepath)
        assert "import com.discordbotmaker.android.ui.theme.AppColors" in content, (
            f"{filepath} does not import AppColors"
        )


class TestRoundedCorners:
    """Verify cards and buttons use 8.dp rounded corners (Discord style)."""

    SCREEN_FILES = [f for f in UI_FILES if f != "ui/AppTheme.kt"]

    @pytest.mark.parametrize("filepath", SCREEN_FILES)
    def test_uses_8dp_corners(self, filepath):
        content = read_file(filepath)
        assert "RoundedCornerShape(8.dp)" in content or "RoundedCornerShape(20.dp)" in content, (
            f"{filepath} does not use appropriate rounded corners"
        )


class TestHeadersNoElevation:
    """Verify headers use tonalElevation = 0.dp (flat Discord style)."""

    SCREEN_FILES = [
        "ui/MainDashboardScreen.kt",
        "LiveConsoleScreen.kt",
        "AutoModScreen.kt",
        "BotCreationScreen.kt",
    ]

    @pytest.mark.parametrize("filepath", SCREEN_FILES)
    def test_headers_flat(self, filepath):
        content = read_file(filepath)
        assert "tonalElevation = 0.dp" in content, (
            f"{filepath} header should use tonalElevation = 0.dp"
        )


class TestCoordinationMd:
    """Verify COORDINATION.md documents the design pivot and Grid branding."""

    def setup_method(self):
        self.content = read_file("COORDINATION.md")

    def test_mentions_discord_design_language(self):
        assert "Discord Design Language" in self.content

    def test_mentions_blurple(self):
        assert "#5865F2" in self.content or "Blurple" in self.content

    def test_mentions_pivot(self):
        assert "Pivot" in self.content or "pivot" in self.content

    def test_mentions_previous_theme(self):
        assert "Previous" in self.content

    def test_documents_background_color(self):
        assert "#313338" in self.content

    def test_documents_surface_color(self):
        assert "#2B2D31" in self.content

    def test_documents_danger_red(self):
        assert "#F23F43" in self.content

    def test_documents_success_green(self):
        assert "#23A559" in self.content

    def test_documents_grid_branding(self):
        assert "Grid Bot Hub" in self.content, "COORDINATION.md should document Grid branding"

    def test_documents_splash_screen(self):
        assert "SplashScreen" in self.content, "COORDINATION.md should document splash screen"

    def test_documents_grid_branding_integration_section(self):
        assert "Grid Branding Integration" in self.content

    def test_documents_inter_roboto(self):
        assert "Inter" in self.content and "Roboto" in self.content
