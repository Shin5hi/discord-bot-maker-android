"""
Validation tests for the Server Tree View refactor, OrionBubble FAB, and Grid branding.
Covers:
  1. ToolLibraryScreen.kt — high-density compact tree view with branch lines
  2. OrionBubble.kt — reusable draggable FAB component
  3. AppNavigation.kt — OrionBubble integration in main Scaffold
  4. Branding — 'Grid' header in tree view
"""
import os
import re
import pytest

PROJECT_DIR = os.path.dirname(os.path.abspath(__file__))


def read_file(relative_path: str) -> str:
    full_path = os.path.join(PROJECT_DIR, relative_path)
    with open(full_path, "r") as f:
        return f.read()


# ─── ToolLibraryScreen.kt — Server Tree View ──────────────────────────────────

class TestToolLibraryTreeView:
    """Verify the Tree view has Discord server tree structure."""

    def setup_method(self):
        self.content = read_file("ui/ToolLibraryScreen.kt")

    def test_file_exists_and_nonempty(self):
        assert len(self.content) > 100, "ToolLibraryScreen.kt should be substantial"

    def test_has_tool_library_screen_composable(self):
        assert "fun ToolLibraryScreen" in self.content

    def test_uses_app_colors(self):
        assert "import com.discordbotmaker.android.ui.theme.AppColors" in self.content

    def test_uses_discord_dark_background(self):
        assert "AppColors.Background" in self.content

    def test_has_tree_category_model(self):
        assert "TreeCategory" in self.content

    def test_has_tree_channel_model(self):
        assert "TreeChannel" in self.content

    def test_has_channel_type_enum(self):
        assert "enum class ChannelType" in self.content
        assert "TEXT" in self.content
        assert "VOICE" in self.content
        assert "ANNOUNCEMENT" in self.content

    def test_has_branch_line_drawing(self):
        """The tree must draw vertical hierarchy lines."""
        assert "Canvas" in self.content, "Canvas for drawing branch lines is missing"
        assert "drawLine" in self.content, "drawLine for branch lines is missing"

    def test_has_vertical_branch_line(self):
        """Must have vertical line logic."""
        assert "Vertical line" in self.content or "isLast" in self.content

    def test_has_horizontal_branch_line(self):
        """Must have horizontal branch line."""
        assert "Horizontal" in self.content or "branch" in self.content.lower()

    def test_uses_hash_icon_for_text_channels(self):
        assert '"#"' in self.content, "Text channels should use # icon"

    def test_uses_voice_icon(self):
        assert '"🔊"' in self.content, "Voice channels should use speaker icon"

    def test_uses_announcement_icon(self):
        assert '"📢"' in self.content, "Announcement channels should use megaphone icon"

    def test_uses_stage_icon(self):
        assert '"📡"' in self.content, "Stage channels should use satellite icon"

    def test_has_collapsible_categories(self):
        assert "AnimatedVisibility" in self.content
        assert "expandVertically" in self.content

    def test_has_chevron_rotation(self):
        assert "animateFloatAsState" in self.content
        assert "chevronRotation" in self.content

    def test_category_names_uppercase(self):
        """Discord category names are uppercase."""
        assert '"LAUNCH & DEPLOY"' in self.content
        assert '"MODERATION"' in self.content
        assert '"AI & INTELLIGENCE"' in self.content
        assert '"COMMANDS & UTILITIES"' in self.content
        assert '"MUSIC & AUDIO"' in self.content
        assert '"ANALYTICS & ENGAGEMENT"' in self.content
        assert '"CONFIGURATION"' in self.content

    def test_channel_names_kebab_case(self):
        """Discord channel names are lowercase kebab-case."""
        assert '"token-connect"' in self.content
        assert '"auto-ban"' in self.content
        assert '"chat-ai"' in self.content
        assert '"music-player"' in self.content

    def test_has_compact_row_height(self):
        """Rows should be compact (30dp or similar, not 44dp+)."""
        assert ".height(30.dp)" in self.content or "30.dp" in self.content

    def test_has_accent_colors_per_category(self):
        assert "AccentRocket" in self.content
        assert "AccentShield" in self.content
        assert "AccentBrain" in self.content
        assert "AccentBolt" in self.content
        assert "AccentMusic" in self.content
        assert "AccentChart" in self.content
        assert "AccentGear" in self.content

    def test_has_pro_badge(self):
        assert '"PRO"' in self.content

    def test_has_featured_orion_banner(self):
        assert "FeaturedOrionBanner" in self.content or "Asistente Orión" in self.content

    def test_onToolSelected_callback(self):
        assert "onToolSelected" in self.content

    def test_asistente_orion_navigation(self):
        assert '"Asistente Orión"' in self.content

    def test_no_monospace_font(self):
        assert "FontFamily.Monospace" not in self.content

    def test_uses_sans_serif(self):
        assert "FontFamily.SansSerif" in self.content

    def test_no_old_card_based_layout(self):
        """Tree view should NOT use Card/ToolCategoryCard pattern."""
        assert "ToolCategoryCard" not in self.content, "Old card layout should be removed"
        assert "ToolItemRow" not in self.content, "Old ToolItemRow should be removed"

    def test_search_bar_present(self):
        assert '"Search"' in self.content or '"Search tools"' in self.content


class TestToolLibraryGridBranding:
    """Verify Grid branding in the tree header."""

    def setup_method(self):
        self.content = read_file("ui/ToolLibraryScreen.kt")

    def test_grid_brand_mark(self):
        assert '"G"' in self.content, 'Grid "G" brand mark missing from tree header'

    def test_grid_name_in_header(self):
        assert '"Grid"' in self.content, '"Grid" name missing from header'

    def test_server_tree_view_subtitle(self):
        assert '"Server Tree View"' in self.content, '"Server Tree View" subtitle missing'

    def test_blurple_brand_mark(self):
        assert "AppColors.Primary" in self.content, "Blurple primary not used"


# ─── OrionBubble.kt — Floating Action Button ──────────────────────────────────

class TestOrionBubble:
    """Verify the OrionBubble FAB component."""

    def setup_method(self):
        self.content = read_file("ui/OrionBubble.kt")

    def test_file_exists(self):
        assert len(self.content) > 50

    def test_composable_function(self):
        assert "fun OrionBubble" in self.content

    def test_uses_app_colors(self):
        assert "import com.discordbotmaker.android.ui.theme.AppColors" in self.content

    def test_has_onClick_callback(self):
        assert "onClick" in self.content

    def test_is_draggable(self):
        """FAB should be movable by the user via drag gestures."""
        assert "detectDragGestures" in self.content or "pointerInput" in self.content

    def test_has_offset_state(self):
        assert "offsetX" in self.content and "offsetY" in self.content

    def test_uses_blurple_color(self):
        assert "AppColors.Primary" in self.content

    def test_has_circle_shape(self):
        assert "CircleShape" in self.content

    def test_has_antenna_visuals(self):
        """Should have antenna dots or antenna-like visuals."""
        assert "antenna" in self.content.lower() or "Antenna" in self.content

    def test_has_bot_icon_eyes(self):
        """Robot face should have eyes."""
        assert "eye" in self.content.lower() or "Eye" in self.content

    def test_has_pulse_animation(self):
        assert "pulse" in self.content.lower()
        assert "infiniteRepeatable" in self.content or "rememberInfiniteTransition" in self.content

    def test_has_orion_label(self):
        assert '"Orión"' in self.content

    def test_correct_package(self):
        assert "package com.discordbotmaker.android.ui.components" in self.content

    def test_no_monospace(self):
        assert "FontFamily.Monospace" not in self.content

    def test_has_shadow(self):
        assert "shadow" in self.content.lower()


# ─── AppNavigation.kt — OrionBubble Integration ───────────────────────────────

class TestAppNavigationOrionBubble:
    """Verify OrionBubble is integrated in the main Scaffold."""

    def setup_method(self):
        self.content = read_file("ui/AppNavigation.kt")

    def test_imports_orion_bubble(self):
        assert "import com.discordbotmaker.android.ui.components.OrionBubble" in self.content

    def test_orion_bubble_composable_used(self):
        assert "OrionBubble(" in self.content

    def test_orion_bubble_navigates_to_doubt_assistant(self):
        assert "DOUBT_ASSISTANT" in self.content

    def test_orion_bubble_at_bottom_end(self):
        assert "BottomEnd" in self.content

    def test_orion_bubble_hidden_on_splash(self):
        assert "hideOrionRoutes" in self.content or "SPLASH" in self.content

    def test_orion_bubble_hidden_on_doubt_assistant(self):
        """FAB should not show when already on Orión screen."""
        assert "DOUBT_ASSISTANT" in self.content
        # Check the hideOrionRoutes set includes DOUBT_ASSISTANT
        hide_section = self.content[self.content.find("hideOrionRoutes"):]
        assert "DOUBT_ASSISTANT" in hide_section[:200]

    def test_show_orion_bubble_flag(self):
        assert "showOrionBubble" in self.content

    def test_box_wrapper_for_overlay(self):
        """NavHost and OrionBubble should be in a Box for overlay positioning."""
        box_idx = self.content.find("Box(modifier = Modifier.padding(innerPadding)")
        assert box_idx != -1, "Box wrapper for overlay not found"

    def test_splash_route_still_works(self):
        assert "AppRoutes.SPLASH" in self.content

    def test_all_routes_present(self):
        for route in ["SPLASH", "DASHBOARD", "TOOL_LIBRARY", "SETTINGS",
                       "LIVE_CONSOLE", "AUTO_MOD", "COMMAND_BUILDER",
                       "BOT_CREATION", "DOUBT_ASSISTANT"]:
            assert route in self.content, f"Route {route} missing"


# ─── Cross-file: No old ToolLibrary patterns ──────────────────────────────────

class TestNoOldPatterns:
    """Ensure old card-based ToolLibrary patterns are fully replaced."""

    def setup_method(self):
        self.content = read_file("ui/ToolLibraryScreen.kt")

    def test_no_tool_item_data_class(self):
        """Old ToolItem data class should be replaced by TreeChannel."""
        # ToolItem as a standalone data class (not in comments)
        lines = [l.strip() for l in self.content.split("\n") if not l.strip().startswith("//")]
        code = "\n".join(lines)
        assert "data class ToolItem" not in code, "Old ToolItem data class should be removed"

    def test_no_tool_category_data_class(self):
        lines = [l.strip() for l in self.content.split("\n") if not l.strip().startswith("//")]
        code = "\n".join(lines)
        assert "data class ToolCategory(" not in code, "Old ToolCategory should be TreeCategory"

    def test_no_featured_module_card(self):
        assert "FeaturedModuleCard" not in self.content

    def test_has_tree_channel_type(self):
        assert "TreeChannel" in self.content
        assert "TreeCategory" in self.content


# ─── COORDINATION.md — Tree View Documentation ────────────────────────────────

class TestCoordinationTreeView:
    """Verify COORDINATION.md documents the tree view change."""

    def setup_method(self):
        self.content = read_file("COORDINATION.md")

    def test_mentions_server_tree_view(self):
        assert "Server Tree View" in self.content or "Tree View" in self.content

    def test_mentions_orion_bubble(self):
        assert "OrionBubble" in self.content or "Orión Bubble" in self.content or "OrionBubble" in self.content

    def test_mentions_branch_lines(self):
        assert "branch" in self.content.lower() or "hierarchy" in self.content.lower()

    def test_mentions_compact(self):
        assert "compact" in self.content.lower() or "high-density" in self.content.lower()

    def test_documents_orion_bubble_file(self):
        assert "OrionBubble.kt" in self.content
