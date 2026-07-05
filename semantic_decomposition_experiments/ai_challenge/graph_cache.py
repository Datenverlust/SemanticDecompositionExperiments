from __future__ import annotations
from typing import Any, Dict, List, Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from .question import Question
    from semantic_decomposition.graph.semantic_net import SemanticNet


class GraphCache:
    """
    Bundles all pre-built graph structures for one question.

    Replaces the Java GraphCache inner class; avoids repeated decomposition.
    """

    def __init__(self, question: Optional["Question"] = None) -> None:
        self.question = question
        self.dec_graph: Optional["SemanticNet"] = None
        self.syntax_graph: Optional["SemanticNet"] = None
        self.ner_graph: Optional["SemanticNet"] = None
        self.role_graph: Optional["SemanticNet"] = None
        self.ner_map: Dict[str, str] = {}
        self.start_activation: float = 1.0
