"""Validation tests for the current billing screen state."""
import os


PROJECT_ROOT = os.path.dirname(os.path.abspath(__file__))
BILLING_SCREEN = os.path.join(PROJECT_ROOT, "ui", "BillingScreen.kt")


def read_file(path: str) -> str:
    with open(path, "r", encoding="utf-8") as file:
        return file.read()


class TestBillingScreenPlaceholder:
    def test_billing_screen_file_exists(self):
        assert os.path.isfile(BILLING_SCREEN)

    def test_billing_screen_is_explicitly_marked_as_placeholder(self):
        content = read_file(BILLING_SCREEN).strip()
        assert content == "BILLING_SCREEN_PLACEHOLDER"
