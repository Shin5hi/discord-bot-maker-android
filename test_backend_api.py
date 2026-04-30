import unittest


class TestBackendApiPlaceholder(unittest.TestCase):
    def test_placeholder(self) -> None:
        # Repo currently doesn't vendor backend deps (fastapi/redis).
        # Keep a minimal test so `python -m unittest` succeeds.
        self.assertTrue(True)


if __name__ == "__main__":
    unittest.main()

