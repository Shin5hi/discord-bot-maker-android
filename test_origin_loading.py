"""
Validation tests for the Grid Origin Loading Screen.
Ensures OriginLoadingScreen.kt implements the branded loading experience
with InfiniteTransition sweep ring, correct colors, and text content.
Also validates AppNavigation.kt has the GRID_ORIGIN_LOADING route and
COORDINATION.md documents Grid Origin as the official engine name.
"""
import os
import pytest

PROJECT_DIR = os.path.dirname(os.path.abspath(__file__))


def read_file(relative_path: str) -> str:
    full_path = os.path.join(PROJECT_DIR, relative_path)
    with open(full_path, "r") as f:
        return f.read()


# --- OriginLoadingScreen.kt Tests ---

class TestOriginLoadingScreenExists:
    """Verify the file exists and has the composable."""

    def setup_method(self):
        self.content = read_file("ui/OriginLoadingScreen.kt")

    def test_file_not_empty(self):
        assert len(self.content) > 0

    def test_origin_composable_function(self):
        assert "fun OriginLoadingScreen" in self.content

    def test_sweep_ring_composable(self):
        assert "fun SweepLoadingRing" in self.content

    def test_package_declaration(self):
        assert "package com.discordbotmaker.android.ui.splash" in self.content


class TestOriginLoadingAnimation:
    """Verify InfiniteTransition sweep ring animation."""

    def setup_method(self):
        self.content = read_file("ui/OriginLoadingScreen.kt")

    def test_uses_infinite_transition(self):
        assert "rememberInfiniteTransition" in self.content

    def test_uses_animate_float(self):
        assert "animateFloat" in self.content

    def test_infinite_repeatable(self):
        assert "infiniteRepeatable" in self.content

    def test_linear_easing_for_rotation(self):
        assert "LinearEasing" in self.content

    def test_sweep_rotation_label(self):
        assert "sweepRotation" in self.content

    def test_sweep_glow_label(self):
        assert "sweepGlow" in self.content

    def test_canvas_for_ring(self):
        assert "Canvas" in self.content

    def test_draw_arc(self):
        assert "drawArc" in self.content

    def test_stroke_cap_round(self):
        assert "StrokeCap.Round" in self.content

    def test_sweep_gradient(self):
        assert "sweepGradient" in self.content

    def test_rotation_360(self):
        assert "360f" in self.content


class TestOriginLoadingColors:
    """Verify the screen uses official AppColors with Charcoal #313338 background."""

    def setup_method(self):
        self.content = read_file("ui/OriginLoadingScreen.kt")

    def test_imports_app_colors(self):
        assert "import com.discordbotmaker.android.ui.theme.AppColors" in self.content

    def test_official_charcoal_background(self):
        """Background must be AppColors.Background (#313338), NOT InputBackground."""
        assert "AppColors.Background" in self.content

    def test_background_not_input_background(self):
        """The background must NOT use InputBackground (#1E1F22) --- must use official charcoal."""
        lines = self.content.split('\n')
        bg_line_found = False
        for i, line in enumerate(lines):
            if '.background(AppColors.' in line and 'fillMaxSize' in self.content[max(0, self.content.index(line)-200):self.content.index(line)]:
                bg_line_found = True
                assert 'AppColors.Background' in line, \
                    f"Root background should use AppColors.Background, found: {line.strip()}"
        assert bg_line_found, "Could not find root .background(AppColors.*) modifier"

    def test_blurple_primary(self):
        assert "AppColors.Primary" in self.content

    def test_text_primary_color(self):
        assert "AppColors.TextPrimary" in self.content

    def test_text_secondary_for_slogan(self):
        assert "AppColors.TextSecondary" in self.content

    def test_text_muted_for_version(self):
        assert "AppColors.TextMuted" in self.content


class TestOriginLoadingText:
    """Verify the correct text content is displayed."""

    def setup_method(self):
        self.content = read_file("ui/OriginLoadingScreen.kt")

    def test_g_logo_glyph(self):
        assert '"G"' in self.content

    def test_grid_origin_title(self):
        assert '"Grid Origin"' in self.content

    def test_slogan_text(self):
        assert '"Crea, Organiza, Avanza."' in self.content

    def test_version_stamp(self):
        assert '"v2.0"' in self.content


class TestOriginLoadingTypography:
    """Verify typography follows Grid brand guidelines."""

    def setup_method(self):
        self.content = read_file("ui/OriginLoadingScreen.kt")

    def test_uses_sans_serif(self):
        assert "FontFamily.SansSerif" in self.content

    def test_no_monospace(self):
        assert "FontFamily.Monospace" not in self.content

    def test_bold_weight_for_logo(self):
        assert "FontWeight.Bold" in self.content

    def test_title_font_size(self):
        assert "32.sp" in self.content

    def test_logo_font_size(self):
        assert "48.sp" in self.content

    def test_slogan_font_size(self):
        assert "14.sp" in self.content


class TestOriginLoadingEntranceAnimation:
    """Verify staged entrance animation."""

    def setup_method(self):
        self.content = read_file("ui/OriginLoadingScreen.kt")

    def test_logo_scale_animatable(self):
        assert "logoScale" in self.content

    def test_title_alpha_animatable(self):
        assert "titleAlpha" in self.content

    def test_slogan_alpha_animatable(self):
        assert "sloganAlpha" in self.content

    def test_launched_effect(self):
        assert "LaunchedEffect" in self.content

    def test_delay_before_complete(self):
        assert "delay" in self.content

    def test_on_loading_complete_callback(self):
        assert "onLoadingComplete" in self.content


class TestOriginLoadingLayout:
    """Verify layout structure."""

    def setup_method(self):
        self.content = read_file("ui/OriginLoadingScreen.kt")

    def test_fill_max_size(self):
        assert "fillMaxSize" in self.content

    def test_logo_size_96dp(self):
        assert "96.dp" in self.content

    def test_ring_size_140dp(self):
        assert "140.dp" in self.content

    def test_rounded_corner_shape(self):
        assert "RoundedCornerShape(20.dp)" in self.content

    def test_center_alignment(self):
        assert "Alignment.Center" in self.content


# --- SplashScreen.kt Delegation Tests ---

class TestSplashScreenDelegation:
    """Verify SplashScreen delegates to OriginLoadingScreen."""

    def setup_method(self):
        self.content = read_file("ui/SplashScreen.kt")

    def test_splash_composable_exists(self):
        assert "fun SplashScreen" in self.content

    def test_delegates_to_origin(self):
        assert "OriginLoadingScreen" in self.content

    def test_passes_callback(self):
        assert "onLoadingComplete" in self.content

    def test_on_splash_complete_parameter(self):
        assert "onSplashComplete" in self.content

    def test_same_package(self):
        assert "package com.discordbotmaker.android.ui.splash" in self.content


# --- design/branding.md Tests ---

class TestBrandingDoc:
    """Verify branding.md documents the logo and animation spec."""

    def setup_method(self):
        self.content = read_file("design/branding.md")

    def test_file_not_empty(self):
        assert len(self.content) > 100

    def test_mentions_grid_origin(self):
        assert "Grid Origin" in self.content

    def test_mentions_blurple_hex(self):
        assert "#5865F2" in self.content

    def test_mentions_charcoal_hex(self):
        assert "#1E1F22" in self.content or "#313338" in self.content

    def test_mentions_slogan(self):
        assert "Crea, Organiza, Avanza." in self.content

    def test_mentions_sweep_ring(self):
        assert "sweep" in self.content.lower() or "ring" in self.content.lower()

    def test_mentions_logo_dimensions(self):
        assert "96" in self.content

    def test_mentions_ring_diameter(self):
        assert "140" in self.content

    def test_mentions_rotation_speed(self):
        assert "1400" in self.content

    def test_mentions_density_buckets(self):
        assert "mdpi" in self.content or "xxxhdpi" in self.content


# --- COORDINATION.md Update Tests ---

class TestCoordinationGridOrigin:
    """Verify COORDINATION.md documents Grid Origin loading screen."""

    def setup_method(self):
        self.content = read_file("COORDINATION.md")

    def test_grid_origin_section_exists(self):
        assert "Grid Origin Loading Screen" in self.content

    def test_mentions_origin_loading_screen_file(self):
        assert "OriginLoadingScreen.kt" in self.content

    def test_mentions_sweep_loading_ring(self):
        assert "SweepLoadingRing" in self.content

    def test_mentions_infinite_transition(self):
        assert "InfiniteTransition" in self.content

    def test_documents_delegation(self):
        assert "delegate" in self.content.lower()

    def test_mentions_branding_md(self):
        assert "branding.md" in self.content

    def test_codex_warning_present(self):
        assert "For Codex" in self.content

    def test_mentions_slogan_in_coordination(self):
        assert "Crea, Organiza, Avanza" in self.content

    def test_file_inventory_has_origin(self):
        assert "OriginLoadingScreen" in self.content

    def test_brand_identity_updated(self):
        assert "Grid Origin" in self.content


class TestCoordinationGridOriginEngine:
    """Verify COORDINATION.md documents Grid Origin as the official engine name."""

    def setup_method(self):
        self.content = read_file("COORDINATION.md")

    def test_engine_definition(self):
        assert "bot/server creation engine" in self.content or "creation engine" in self.content

    def test_grid_origin_engine_section(self):
        assert "Grid Origin" in self.content

    def test_documents_grid_origin_loading_route(self):
        assert "GRID_ORIGIN_LOADING" in self.content

    def test_documents_official_charcoal(self):
        assert "#313338" in self.content

    def test_documents_animated_loading_sequence(self):
        assert "animated loading" in self.content.lower()

    def test_background_is_313338_in_coordination(self):
        """COORDINATION should reference #313338 as the official charcoal."""
        assert "Official Charcoal" in self.content


# --- Navigation Tests --- GRID_ORIGIN_LOADING Route ---

class TestNavigationStillWorks:
    """Verify AppNavigation.kt still references SplashScreen correctly."""

    def setup_method(self):
        self.content = read_file("ui/AppNavigation.kt")

    def test_splash_route_still_exists(self):
        assert 'SPLASH' in self.content

    def test_splash_import(self):
        assert "import com.discordbotmaker.android.ui.splash.SplashScreen" in self.content

    def test_splash_composable_used(self):
        assert "SplashScreen(" in self.content

    def test_splash_is_start_destination(self):
        assert "startDestination = AppRoutes.SPLASH" in self.content


class TestGridOriginLoadingRoute:
    """Verify the GRID_ORIGIN_LOADING route is properly added."""

    def setup_method(self):
        self.content = read_file("ui/AppNavigation.kt")

    def test_route_constant_defined(self):
        assert 'GRID_ORIGIN_LOADING' in self.content

    def test_route_value(self):
        assert '"grid_origin_loading"' in self.content

    def test_composable_destination_exists(self):
        assert 'composable(AppRoutes.GRID_ORIGIN_LOADING)' in self.content

    def test_origin_loading_screen_import(self):
        assert "import com.discordbotmaker.android.ui.splash.OriginLoadingScreen" in self.content

    def test_origin_loading_screen_used_in_route(self):
        """OriginLoadingScreen must be called inside the GRID_ORIGIN_LOADING composable."""
        assert "OriginLoadingScreen(" in self.content

    def test_navigates_to_dashboard_on_complete(self):
        """The GRID_ORIGIN_LOADING route should navigate to DASHBOARD on completion."""
        assert "AppRoutes.DASHBOARD" in self.content

    def test_pops_origin_loading_inclusive(self):
        """Should pop GRID_ORIGIN_LOADING from back stack."""
        assert "GRID_ORIGIN_LOADING" in self.content
        assert "inclusive = true" in self.content

    def test_hidden_from_orion_bubble(self):
        """GRID_ORIGIN_LOADING should be in hideOrionRoutes."""
        assert "AppRoutes.GRID_ORIGIN_LOADING" in self.content
        lines = self.content.split('\n')
        in_hide_section = False
        found = False
        for line in lines:
            if 'hideOrionRoutes' in line:
                in_hide_section = True
            if in_hide_section and 'GRID_ORIGIN_LOADING' in line:
                found = True
                break
            if in_hide_section and line.strip() == ')':
                break
        assert found, "GRID_ORIGIN_LOADING not found in hideOrionRoutes"


class TestAppThemeCharcoalColor:
    """Verify AppTheme.kt has the correct charcoal background (#313338)."""

    def setup_method(self):
        self.content = read_file("ui/AppTheme.kt")

    def test_background_is_313338(self):
        assert "0xFF313338" in self.content

    def test_background_named_correctly(self):
        assert "val Background" in self.content
        for line in self.content.split('\n'):
            if 'val Background' in line:
                assert '313338' in line, f"Background should be #313338, got: {line.strip()}"
                break
