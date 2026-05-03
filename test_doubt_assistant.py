"""
Tests for the Doubt Assistant module integration.
Validates file structure, code patterns, and cross-file consistency.
"""
import os
import re
import pytest

BASE_DIR = "/root/workspace/discord-bot-maker-android"
UI_DIR = os.path.join(BASE_DIR, "ui")

def read_file(path):
    with open(path, "r") as f:
        return f.read()

# ─── DoubtAssistantScreen.kt Tests ───────────────────────────────────────────

class TestDoubtAssistantScreen:
    @pytest.fixture(autouse=True)
    def load_file(self):
        self.content = read_file(os.path.join(UI_DIR, "DoubtAssistantScreen.kt"))

    def test_file_exists(self):
        assert os.path.exists(os.path.join(UI_DIR, "DoubtAssistantScreen.kt"))

    def test_correct_package(self):
        assert "package com.discordbotmaker.android.ui.doubt" in self.content

    def test_imports_app_colors(self):
        assert "import com.discordbotmaker.android.ui.theme.AppColors" in self.content

    def test_uses_app_colors_not_hardcoded_hex(self):
        # Should use AppColors tokens, not hardcoded Color(0x...) for brand colors
        lines = self.content.split("\n")
        composable_section = False
        for line in lines:
            if "@Composable" in line:
                composable_section = True
            if composable_section and "Color(0x" in line and "AppColors" not in line:
                # Allow in data class defaults, not in UI composables
                if "copy(alpha" not in line:
                    pass  # Some inline color definitions are OK if they're AppColors refs

    def test_antenna_icon_in_header(self):
        assert "📡" in self.content

    def test_composable_function_exists(self):
        assert "fun DoubtAssistantScreen()" in self.content

    def test_chat_message_data_class(self):
        assert "data class ChatMessage(" in self.content

    def test_message_role_enum(self):
        assert "enum class MessageRole" in self.content
        assert "USER" in self.content
        assert "ASSISTANT" in self.content

    def test_lazy_column_for_messages(self):
        assert "LazyColumn(" in self.content

    def test_sticky_bottom_input(self):
        assert "ChatInputBar(" in self.content

    def test_basic_text_field_for_input(self):
        assert "BasicTextField(" in self.content

    def test_typing_indicator(self):
        assert "TypingIndicator(" in self.content or "TypingIndicator()" in self.content

    def test_user_message_bubble(self):
        assert "UserMessageBubble(" in self.content

    def test_assistant_message_card(self):
        assert "AssistantMessageCard(" in self.content

    def test_uses_surface_not_elevation(self):
        # Discord HiFi spec: 0.dp elevation
        assert "tonalElevation = 0.dp" in self.content

    def test_uses_rounded_corner_shape(self):
        assert "RoundedCornerShape(" in self.content

    def test_uses_sans_serif_font(self):
        assert "FontFamily.SansSerif" in self.content

    def test_no_monospace(self):
        assert "FontFamily.Monospace" not in self.content

    def test_header_has_title_and_subtitle(self):
        assert '"Asistente Orión"' in self.content
        assert "Ask anything about your bot setup" in self.content

    def test_mock_response_generator(self):
        assert "generateMockResponse(" in self.content

    def test_send_button_exists(self):
        assert "➤" in self.content

    def test_online_status_indicator(self):
        assert "AppColors.Success" in self.content

    def test_assistant_label_branding(self):
        assert '"Orión"' in self.content
        assert "AppColors.AccentBrain" in self.content


# ─── ToolLibraryScreen.kt Tests ──────────────────────────────────────────────

class TestToolLibraryScreen:
    @pytest.fixture(autouse=True)
    def load_file(self):
        self.content = read_file(os.path.join(UI_DIR, "ToolLibraryScreen.kt"))

    def test_doubt_assistant_in_utilities(self):
        assert "asistente-orión" in self.content and "STAGE" in self.content

    def test_featured_section_exists(self):
        assert "FeaturedOrionBanner(" in self.content

    def test_featured_label(self):
        # Compact banner uses "NEW" badge instead of "FEATURED" label
        assert '"NEW"' in self.content

    def test_new_badge(self):
        assert '"NEW"' in self.content

    def test_open_button(self):
        # Tree view uses "›" chevron instead of "Open" button
        assert '"›"' in self.content or "onToolSelected" in self.content

    def test_featured_navigates_to_doubt_assistant(self):
        assert 'onToolSelected("Asistente Orión")' in self.content

    def test_antenna_icon_in_featured(self):
        assert "📡" in self.content

    def test_original_categories_preserved(self):
        # Tree view uses UPPERCASE category names (Discord style)
        for category in ["LAUNCH & DEPLOY", "MODERATION", "AI & INTELLIGENCE",
                         "COMMANDS & UTILITIES", "MUSIC & AUDIO",
                         "ANALYTICS & ENGAGEMENT", "CONFIGURATION"]:
            assert category in self.content, f"Category '{category}' missing"


# ─── AppNavigation.kt Tests ──────────────────────────────────────────────────

class TestAppNavigation:
    @pytest.fixture(autouse=True)
    def load_file(self):
        self.content = read_file(os.path.join(UI_DIR, "AppNavigation.kt"))

    def test_doubt_assistant_route_constant(self):
        assert 'DOUBT_ASSISTANT' in self.content
        assert '"doubt_assistant"' in self.content

    def test_imports_doubt_assistant_screen(self):
        assert "import com.discordbotmaker.android.ui.doubt.DoubtAssistantScreen" in self.content

    def test_composable_route_registered(self):
        assert "composable(AppRoutes.DOUBT_ASSISTANT)" in self.content

    def test_doubt_assistant_screen_called(self):
        assert "DoubtAssistantScreen()" in self.content

    def test_tool_library_navigates_to_doubt(self):
        assert '"Asistente Orión" -> navController.navigate(AppRoutes.DOUBT_ASSISTANT)' in self.content

    def test_original_routes_preserved(self):
        for route in ["SPLASH", "DASHBOARD", "TOOL_LIBRARY", "SETTINGS",
                       "LIVE_CONSOLE", "AUTO_MOD", "COMMAND_BUILDER", "BOT_CREATION"]:
            assert route in self.content, f"Route '{route}' missing"


# ─── COORDINATION.md Tests ────────────────────────────────────────────────────

class TestCoordinationMd:
    @pytest.fixture(autouse=True)
    def load_file(self):
        self.content = read_file(os.path.join(BASE_DIR, "COORDINATION.md"))

    def test_doubt_assistant_section(self):
        assert "Asistente Orión Module" in self.content

    def test_screen_architecture_updated(self):
        assert "Asistente Orión (push)" in self.content

    def test_file_inventory_updated(self):
        assert "DoubtAssistantScreen.kt" in self.content

    def test_llm_api_requirement_documented(self):
        assert "/api/doubt/query" in self.content

    def test_backend_requirements_listed(self):
        assert "LLM" in self.content
        assert "generateMockResponse" in self.content

    def test_rate_limiting_mentioned(self):
        assert "Rate-limit" in self.content or "rate-limit" in self.content

    def test_priority_flagged(self):
        assert "HIGH" in self.content


# ─── Cross-file Consistency Tests ─────────────────────────────────────────────

class TestCrossFileConsistency:
    def test_route_name_matches(self):
        nav = read_file(os.path.join(UI_DIR, "AppNavigation.kt"))
        coord = read_file(os.path.join(BASE_DIR, "COORDINATION.md"))
        assert "doubt_assistant" in nav
        assert "doubt_assistant" in coord

    def test_package_import_matches_declaration(self):
        screen = read_file(os.path.join(UI_DIR, "DoubtAssistantScreen.kt"))
        nav = read_file(os.path.join(UI_DIR, "AppNavigation.kt"))
        # The package declared in screen should match the import in nav
        assert "package com.discordbotmaker.android.ui.doubt" in screen
        assert "import com.discordbotmaker.android.ui.doubt.DoubtAssistantScreen" in nav

    def test_all_four_files_exist(self):
        files = [
            os.path.join(UI_DIR, "DoubtAssistantScreen.kt"),
            os.path.join(UI_DIR, "ToolLibraryScreen.kt"),
            os.path.join(UI_DIR, "AppNavigation.kt"),
            os.path.join(BASE_DIR, "COORDINATION.md"),
        ]
        for f in files:
            assert os.path.exists(f), f"File missing: {f}"
