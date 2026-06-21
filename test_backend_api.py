"""
Regression tests for the duplicated FastAPI backend modules.
These checks stay file-based so they do not require Redis or FastAPI runtime deps.
"""
import os


PROJECT_ROOT = os.path.dirname(os.path.abspath(__file__))
ROOT_BACKEND = os.path.join(PROJECT_ROOT, "backend_api.py")
NESTED_BACKEND = os.path.join(PROJECT_ROOT, "backend", "backend_api.py")


def read_file(path: str) -> str:
    with open(path, "r", encoding="utf-8") as file:
        return file.read()


class TestBackendApiFiles:
    def test_backend_files_exist(self):
        assert os.path.isfile(ROOT_BACKEND)
        assert os.path.isfile(NESTED_BACKEND)

    def test_backend_copies_match(self):
        assert read_file(ROOT_BACKEND) == read_file(NESTED_BACKEND)


class TestBackendApiStructure:
    def setup_method(self):
        self.content = read_file(ROOT_BACKEND)

    def test_fastapi_app_definition_exists(self):
        assert 'app = FastAPI(title="Discord Bot Maker API"' in self.content
        assert 'version="0.1.0"' in self.content

    def test_cors_is_enabled(self):
        assert "CORSMiddleware" in self.content
        assert 'allow_origins=["*"]' in self.content
        assert 'allow_methods=["*"]' in self.content
        assert 'allow_headers=["*"]' in self.content

    def test_automod_model_fields_exist(self):
        for field_name in [
            "toxicity_filter_enabled",
            "toxicity_sensitivity",
            "toxicity_action",
            "link_blocking_enabled",
            "spam_protection_enabled",
            "spam_message_threshold",
        ]:
            assert field_name in self.content

    def test_toxicity_action_enum_is_correct(self):
        for action in ["WARN", "DELETE", "MUTE", "BAN"]:
            assert f'{action} = "{action}"' in self.content
        assert "BAM" not in self.content

    def test_log_and_health_endpoints_exist(self):
        assert '@app.websocket("/ws/logs")' in self.content
        assert '@app.post("/api/logs", status_code=201)' in self.content
        assert '@app.get("/health")' in self.content

    def test_music_endpoints_exist(self):
        for route in [
            '@app.post("/api/music/play", status_code=200)',
            '@app.get("/api/music/queue", response_model=MusicQueueResponse)',
            '@app.post("/api/music/add", status_code=201)',
        ]:
            assert route in self.content

    def test_deploy_endpoint_validates_discord_token_format(self):
        assert '@app.post("/api/bots/deploy", status_code=202, response_model=DeployResponse)' in self.content
        assert "DISCORD_TOKEN_RE" in self.content
        assert "Invalid Discord bot token format" in self.content
        assert "Failed to persist deployment state to Redis" in self.content

    def test_health_endpoint_reports_redis_status(self):
        assert '{"status": "healthy", "redis": "connected"}' in self.content
        assert "Redis unavailable" in self.content
