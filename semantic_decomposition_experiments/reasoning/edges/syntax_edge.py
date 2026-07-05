from __future__ import annotations
from typing import Any


class SyntaxEdge:
    """Edge carrying a syntactic dependency relation between two word nodes."""

    def __init__(
        self, source: Any = None, target: Any = None, relation: str = ""
    ) -> None:
        self.source = source
        self.target = target
        self.relation = relation

    def __repr__(self) -> str:
        return f"SyntaxEdge({self.source!r} -[SYN:{self.relation}]-> {self.target!r})"
