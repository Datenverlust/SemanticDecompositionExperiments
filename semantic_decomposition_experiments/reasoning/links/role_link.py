from __future__ import annotations
from marker_passing.link import Link


class RoleLink(Link):
    """Link representing a semantic role relation."""

    def __init__(self, source=None, target=None, weight: float = 0.5) -> None:
        self._source = source
        self._target = target
        self._weight = weight

    @property
    def source(self): return self._source

    @source.setter
    def source(self, v): self._source = v

    @property
    def target(self): return self._target

    @target.setter
    def target(self, v): self._target = v

    @property
    def weight(self) -> float: return self._weight
