from __future__ import annotations
from typing import Dict, List, Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from semantic_decomposition import Concept
    from semantic_decomposition.graph.semantic_net import SemanticNet

# Top 10 most frequent emoji codes (Unicode code points)
TOP_EMOJIS: List[str] = [
    "U+1F602",  # 😂 Face with Tears of Joy
    "U+2764",   # ❤️ Red Heart
    "U+1F60D",  # 😍 Smiling Face with Heart-Eyes
    "U+1F629",  # 😩 Weary Face
    "U+1F62D",  # 😭 Loudly Crying Face
    "U+1F618",  # 😘 Face Blowing a Kiss
    "U+1F4AF",  # 💯 Hundred Points
    "U+1F60A",  # 😊 Smiling Face with Smiling Eyes
    "U+1F495",  # 💕 Two Hearts
    "U+1F612",  # 😒 Unamused Face
]

EMOJI_NAMES: Dict[str, str] = {
    "U+1F602": "face_with_tears_of_joy",
    "U+2764":  "red_heart",
    "U+1F60D": "smiling_face_heart_eyes",
    "U+1F629": "weary_face",
    "U+1F62D": "loudly_crying_face",
    "U+1F618": "face_blowing_kiss",
    "U+1F4AF": "hundred_points",
    "U+1F60A": "smiling_face_smiling_eyes",
    "U+1F495": "two_hearts",
    "U+1F612": "unamused_face",
}


class EmojiGraphBuilder:
    """Builds a semantic graph from the top-N most frequent emojis."""

    def get_emojis_as_concepts(self) -> List["Concept"]:
        from semantic_decomposition.concept import Concept
        concepts: List[Concept] = []
        for code in TOP_EMOJIS:
            c = Concept()
            c.litheral = EMOJI_NAMES.get(code, code)
            concepts.append(c)
        return concepts

    def get_emoji_graph(self) -> "SemanticNet":
        from semantic_decomposition.graph.semantic_net import SemanticNet
        from semantic_decomposition.graph.graph_util import GraphUtil
        concepts = self.get_emojis_as_concepts()
        if not concepts:
            return SemanticNet()
        graph = GraphUtil.create_graph(concepts[0])
        for concept in concepts[1:]:
            g = GraphUtil.create_graph(concept)
            graph = GraphUtil.merge_graph(graph, g)
        return graph
