"""
Tests for PR #4 critical fixes:
1. Backend: ToxicityAction.BAN is correct (not BAM)
2. Theme consolidation: app/ files use AppColors, not NeonColors
3. Build infrastructure: required files exist
"""
import os
import re
import ast

PROJECT_ROOT = "/root/workspace/discord-bot-maker-android"
APP_SRC = os.path.join(PROJECT_ROOT, "app", "src", "main", "kotlin", "com", "discordbotmaker", "android")


class TestBackendBugFix:
    """Verify ToxicityAction.BAN is correctly defined (no BAM typo)."""

    def test_root_backend_has_ban_not_bam(self):
        with open(os.path.join(PROJECT_ROOT, "backend_api.py")) as f:
            content = f.read()
        assert "ToxicityAction.BAN" not in content or "BAN" in content
        assert "BAM" not in content, "Found 'BAM' typo in root backend_api.py"
        assert 'BAN = "BAN"' in content or "BAN" in content

    def test_backend_dir_has_ban_not_bam(self):
        with open(os.path.join(PROJECT_ROOT, "backend", "backend_api.py")) as f:
            content = f.read()
        assert "BAM" not in content, "Found 'BAM' typo in backend/backend_api.py"
        assert 'BAN = "BAN"' in content

    def test_toxicity_action_enum_values(self):
        """Parse both backend files and verify ToxicityAction enum has WARN, DELETE, MUTE, BAN."""
        for path in [
            os.path.join(PROJECT_ROOT, "backend_api.py"),
            os.path.join(PROJECT_ROOT, "backend", "backend_api.py"),
        ]:
            with open(path) as f:
                content = f.read()
            for expected in ["WARN", "DELETE", "MUTE", "BAN"]:
                assert f'{expected} = "{expected}"' in content or f"{expected} = \"{expected}\"" in content, \
                    f"Missing {expected} in {path}"


class TestThemeConsolidation:
    """Verify app/ UI files import AppColors from centralized theme, not NeonColors."""

    UI_FILES = [
        os.path.join(APP_SRC, "ui", "automod", "AutoModScreen.kt"),
        os.path.join(APP_SRC, "ui", "commands", "CommandBuilderScreen.kt"),
        os.path.join(APP_SRC, "ui", "console", "LiveConsoleScreen.kt"),
        os.path.join(APP_SRC, "ui", "launch", "BotCreationScreen.kt"),
    ]

    def test_no_neon_colors_imports(self):
        for path in self.UI_FILES:
            with open(path) as f:
                content = f.read()
            assert "NeonColors" not in content, \
                f"Found NeonColors reference in {os.path.basename(path)}"

    def test_app_colors_import_present(self):
        for path in self.UI_FILES:
            with open(path) as f:
                content = f.read()
            assert "import com.discordbotmaker.android.ui.theme.AppColors" in content, \
                f"Missing AppColors import in {os.path.basename(path)}"

    def test_app_colors_usage(self):
        for path in self.UI_FILES:
            with open(path) as f:
                content = f.read()
            assert "AppColors." in content, \
                f"No AppColors usage found in {os.path.basename(path)}"

    def test_centralized_theme_file_exists(self):
        theme_path = os.path.join(APP_SRC, "ui", "theme", "AppTheme.kt")
        assert os.path.isfile(theme_path), "AppTheme.kt not found in app source tree"

    def test_centralized_theme_has_app_colors(self):
        theme_path = os.path.join(APP_SRC, "ui", "theme", "AppTheme.kt")
        with open(theme_path) as f:
            content = f.read()
        assert "object AppColors" in content
        assert "val Background" in content
        assert "val Primary" in content
        assert "val TextPrimary" in content

    def test_root_level_files_use_app_colors(self):
        """Root-level .kt files should also use AppColors."""
        for name in ["CommandBuilderScreen.kt", "AutoModScreen.kt",
                      "BotCreationScreen.kt", "LiveConsoleScreen.kt"]:
            path = os.path.join(PROJECT_ROOT, name)
            with open(path) as f:
                content = f.read()
            assert "NeonColors" not in content, \
                f"Root-level {name} still uses NeonColors"
            assert "AppColors" in content, \
                f"Root-level {name} should use AppColors"


class TestBuildInfrastructure:
    """Verify all required build files exist for Android/Gradle project."""

    def test_root_build_gradle_exists(self):
        path = os.path.join(PROJECT_ROOT, "build.gradle.kts")
        assert os.path.isfile(path), "Root build.gradle.kts missing"

    def test_settings_gradle_exists(self):
        path = os.path.join(PROJECT_ROOT, "settings.gradle.kts")
        assert os.path.isfile(path), "settings.gradle.kts missing"

    def test_app_build_gradle_exists(self):
        path = os.path.join(PROJECT_ROOT, "app", "build.gradle.kts")
        assert os.path.isfile(path), "app/build.gradle.kts missing"

    def test_android_manifest_exists(self):
        path = os.path.join(PROJECT_ROOT, "app", "src", "main", "AndroidManifest.xml")
        assert os.path.isfile(path), "AndroidManifest.xml missing"

    def test_android_manifest_has_internet_permission(self):
        path = os.path.join(PROJECT_ROOT, "app", "src", "main", "AndroidManifest.xml")
        with open(path) as f:
            content = f.read()
        assert "android.permission.INTERNET" in content, \
            "INTERNET permission missing from AndroidManifest.xml"

    def test_main_activity_exists(self):
        path = os.path.join(APP_SRC, "MainActivity.kt")
        assert os.path.isfile(path), "MainActivity.kt missing"

    def test_main_activity_uses_app_theme(self):
        path = os.path.join(APP_SRC, "MainActivity.kt")
        with open(path) as f:
            content = f.read()
        assert "AppTheme" in content, "MainActivity should use AppTheme"
        assert "ComponentActivity" in content, "MainActivity should extend ComponentActivity"

    def test_settings_includes_app(self):
        path = os.path.join(PROJECT_ROOT, "settings.gradle.kts")
        with open(path) as f:
            content = f.read()
        assert 'include(":app")' in content

    def test_root_build_has_android_plugin(self):
        path = os.path.join(PROJECT_ROOT, "build.gradle.kts")
        with open(path) as f:
            content = f.read()
        assert "com.android.application" in content

    def test_app_build_has_compose(self):
        path = os.path.join(PROJECT_ROOT, "app", "build.gradle.kts")
        with open(path) as f:
            content = f.read()
        assert "compose" in content.lower()
        assert "material3" in content

    def test_app_build_has_namespace(self):
        path = os.path.join(PROJECT_ROOT, "app", "build.gradle.kts")
        with open(path) as f:
            content = f.read()
        assert 'namespace = "com.discordbotmaker.android"' in content
