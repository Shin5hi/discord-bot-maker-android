"""Validation tests for the Discord Design Language UI refactoring."""
import os
import re
import pytest

PROJECT_DIR = os.path.dirname(os.path.abspath(__file__))

UI_FILES = [
    "ui/AppTheme.kt",
    "ui/MainDashboardScreen.kt",
    "LiveConsoleScreen.kt",
    "AutoModScreen.kt",
    "BotCreationScreen.kt",
    "CommandBuilderScreen.kt",
    "MusicPlayerScreen.kt",
]

def read_file(relative_path: str) -> str:
    full_path = os.path.join(PROJECT_DIR, relative_path)
    with open(full_path, "r") as f:
        return f.read()

class TestAppThemeColors:
    def setup_method(self):
        self.content = read_file("ui/AppTheme.kt")
    def test_primary_blurple(self):
        assert "0xFF5865F2" in self.content
    def test_background_color(self):
        assert "0xFF313338" in self.content
    def test_surface_color(self):
        assert "0xFF2B2D31" in self.content
    def test_text_primary_white(self):
        assert "0xFFFFFFFF" in self.content
    def test_text_secondary(self):
        assert "0xFFB5BAC1" in self.content
    def test_success_green(self):
        assert "0xFF23A559" in self.content
    def test_danger_red(self):
        assert "0xFFF23F43" in self.content
    def test_no_old_blue(self):
        assert "0xFF007BFF" not in self.content
    def test_no_old_bg(self):
        assert "0xFF121212" not in self.content

class TestNoOldColors:
    OLD = ["0xFF121212","0xFF1E1E1E","0xFF007BFF","0xFF17A2B8","0xFF28A745","0xFFDC3545"]
    @pytest.mark.parametrize("fp", UI_FILES)
    def test_no_old(self, fp):
        c = read_file(fp)
        for h in self.OLD:
            assert h not in c, f"{fp} has {h}"

class TestNoMonospace:
    @pytest.mark.parametrize("fp", UI_FILES)
    def test_no_mono(self, fp):
        assert "FontFamily.Monospace" not in read_file(fp)

class TestCoordination:
    def setup_method(self):
        self.c = read_file("COORDINATION.md")
    def test_discord(self):
        assert "Discord Design Language" in self.c
    def test_blurple(self):
        assert "#5865F2" in self.c
    def test_pivot(self):
        assert "pivot" in self.c.lower()
