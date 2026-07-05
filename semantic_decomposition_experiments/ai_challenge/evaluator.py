from __future__ import annotations
from typing import Dict, List, Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from ..reasoning.winograd_double_marker_passing import WinogradDoubleMarkerPassing
    from semantic_decomposition import Concept


class Evaluator:
    """
    Extracts per-answer activation scores from a WinogradDoubleMarkerPassing run.

    Mirrors Java Evaluator.summe().
    """

    syn_counter: int = 0
    ant_counter: int = 0
    ner_counter: int = 0
    role_counter: int = 0
    def_counter: int = 0

    @staticmethod
    def summe(
        algo: "WinogradDoubleMarkerPassing",
        answers: List[str],
    ) -> Dict[str, float]:
        from ..reasoning.winograd_double_node import WinogradDoubleNode
        scores: Dict[str, float] = {ans: 0.0 for ans in answers}
        for node in algo.active_nodes:
            if not isinstance(node, WinogradDoubleNode):
                continue
            for concept, activation in node.activation.items():
                origin_literal = concept.litheral if concept else ""
                for ans in answers:
                    if origin_literal in ans:
                        scores[ans] += activation
        return scores

    @staticmethod
    def get_right_answer(scores: Dict[str, float], correct: str) -> float:
        return scores.get(correct, 0.0)

    @staticmethod
    def get_highest_answer(scores: Dict[str, float]) -> float:
        return max(scores.values()) if scores else 0.0

    @staticmethod
    def get_second_highest_answer(scores: Dict[str, float]) -> float:
        vals = sorted(scores.values(), reverse=True)
        return vals[1] if len(vals) >= 2 else 0.0
