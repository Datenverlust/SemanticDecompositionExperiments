from __future__ import annotations
from typing import List, Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from semantic_decomposition import Concept
    from semantic_decomposition.graph.spreading_activation.marker_passing import (
        MarkerPassingSemanticDistanceMeasure,
    )


class SentenceSemanticSimilarityMeasure:
    """
    Sentence-level similarity by averaging pairwise concept similarities.

    Uses a MarkerPassingSemanticDistanceMeasure internally for word-level scoring.
    """

    def __init__(self, word_measure: Optional["MarkerPassingSemanticDistanceMeasure"] = None) -> None:
        self._word_measure = word_measure

    def compare(self, sentence1: List["Concept"], sentence2: List["Concept"]) -> float:
        if not sentence1 or not sentence2 or self._word_measure is None:
            return 0.0
        total = 0.0
        count = 0
        for c1 in sentence1:
            for c2 in sentence2:
                try:
                    total += self._word_measure.compare_concepts(c1, c2)
                    count += 1
                except Exception:
                    pass
        return total / count if count > 0 else 0.0
