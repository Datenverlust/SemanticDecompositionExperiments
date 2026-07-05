from __future__ import annotations
from typing import Dict, List

try:
    import urllib.request as _urllib
    _HTTP_AVAILABLE = True
except ImportError:
    _HTTP_AVAILABLE = False


class EmojiNetCrawler:
    """Downloads emoji usage statistics from emojitracker.com."""

    API_URL = "https://www.emojitracker.com/api/rankings"

    def fetch_top_n(self, n: int = 10) -> List[Dict[str, object]]:
        """Return the top-N emojis as [{id, score, char}] dicts."""
        if not _HTTP_AVAILABLE:
            return []
        try:
            import json
            with _urllib.urlopen(self.API_URL, timeout=10) as resp:
                data = json.loads(resp.read().decode())
            return data[:n] if isinstance(data, list) else []
        except Exception:
            return []
