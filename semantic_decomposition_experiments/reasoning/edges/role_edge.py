from __future__ import annotations
from typing import Any


class RoleEdge:
    """Edge connecting a word to its semantic role (e.g. ARG0, ARG1)."""

    def __init__(self, source: Any = None, target: Any = None, role: str = "") -> None:
        self.source = source
        self.target = target
        self.role = role

    def __repr__(self) -> str:
        return f"RoleEdge({self.source!r} -[ROLE:{self.role}]-> {self.target!r})"
