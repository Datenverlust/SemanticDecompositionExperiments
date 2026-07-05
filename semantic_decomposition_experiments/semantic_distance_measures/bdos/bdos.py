from __future__ import annotations
from typing import List, Optional, Set, TYPE_CHECKING

if TYPE_CHECKING:
    from semantic_decomposition import Concept


class BDOS:
    """
    Bi-Directional One-Step algorithm.

    Computes semantic similarity between two concepts by performing a
    bidirectional search through semantic relation sets until both concept
    neighbourhoods intersect, then normalising by the maximum path length.

    Formula: similarity = 1.0 - (shortest_path / max_path)
    """

    def __init__(self, max_path: int = 16) -> None:
        self._max_path = max_path

    def compare(self, c1: "Concept", c2: "Concept") -> float:
        if c1 is None or c2 is None:
            return 0.0
        if c1.litheral == c2.litheral:
            return 1.0
        path_len = self._bidirectional_search(c1, c2)
        if path_len < 0:
            return 0.0
        return 1.0 - (path_len / self._max_path)

    def _bidirectional_search(self, c1: "Concept", c2: "Concept") -> int:
        frontier_a: Set[str] = {c1.litheral}
        frontier_b: Set[str] = {c2.litheral}
        visited_a: Set[str] = {c1.litheral}
        visited_b: Set[str] = {c2.litheral}

        for step in range(1, self._max_path + 1):
            frontier_a = self._expand(frontier_a, visited_a, c1)
            if frontier_a & frontier_b:
                return step
            frontier_b = self._expand(frontier_b, visited_b, c2)
            if frontier_a & frontier_b:
                return step

        return -1

    def _expand(
        self, frontier: Set[str], visited: Set[str], root: "Concept"
    ) -> Set[str]:
        next_frontier: Set[str] = set()
        for literal in frontier:
            related = self._get_related_literals(literal, root)
            for r in related:
                if r not in visited:
                    visited.add(r)
                    next_frontier.add(r)
        return next_frontier

    @staticmethod
    def _get_related_literals(literal: str, concept: "Concept") -> List[str]:
        related: List[str] = []
        if concept.litheral == literal:
            for c in (
                concept.synonyms
                + concept.hypernyms
                + concept.hyponyms
                + concept.meronyms
            ):
                related.append(c.litheral)
            for d in concept.definitions:
                if hasattr(d, "concepts"):
                    for dc in d.concepts:
                        related.append(dc.litheral)
        return related
