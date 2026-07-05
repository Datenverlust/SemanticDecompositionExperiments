from __future__ import annotations
from typing import Dict, List, Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from semantic_decomposition import Concept


class SentenceSenseDisambiguation:
    """
    Disambiguates word senses at the sentence level using marker-passing scores.

    For each ambiguous word, selects the sense whose concept activates most
    strongly given the surrounding sentence context.
    """

    def __init__(self) -> None:
        self._sense_map: Dict[str, List["Concept"]] = {}

    def register_senses(self, word: str, senses: List["Concept"]) -> None:
        self._sense_map[word] = senses

    def disambiguate(
        self, target_word: str, context_concepts: List["Concept"]
    ) -> Optional["Concept"]:
        senses = self._sense_map.get(target_word)
        if not senses:
            return None
        best_sense: Optional["Concept"] = None
        best_score = -float("inf")
        for sense in senses:
            score = self._score_sense(sense, context_concepts)
            if score > best_score:
                best_score = score
                best_sense = sense
        return best_sense

    def _score_sense(self, sense: "Concept", context: List["Concept"]) -> float:
        score = 0.0
        sense_tokens = {sense.litheral}
        for d in sense.definitions:
            if hasattr(d, "concepts"):
                for c in d.concepts:
                    sense_tokens.add(c.litheral)
        for ctx in context:
            if ctx.litheral in sense_tokens:
                score += 1.0
        return score
