from __future__ import annotations
from typing import Any


class NerEdge:
    """Edge connecting a word node to its Named Entity type."""

    def __init__(self, source: Any = None, target: Any = None, ner_type: str = "") -> None:
        self.source = source
        self.target = target
        self.ner_type = ner_type

    def __repr__(self) -> str:
        return f"NerEdge({self.source!r} -[NER:{self.ner_type}]-> {self.target!r})"
