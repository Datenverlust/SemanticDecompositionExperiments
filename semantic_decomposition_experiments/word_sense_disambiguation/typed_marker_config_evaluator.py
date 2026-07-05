from __future__ import annotations
import random
from typing import List, TYPE_CHECKING

from .senseval_data import SensevalData, SVSentence

if TYPE_CHECKING:
    from semantic_decomposition.entities.spreading_activation.typed_marker_passing_config import (
        TypedMarkerPassingConfig,
    )


class TypedMarkerConfigEvaluator:
    """
    Fitness evaluator for TypedMarkerPassingConfig via N-fold CV on Senseval data.

    Mirrors Java TypedMarkerConfigEvaluator.
    """

    def __init__(self, senseval_data: SensevalData, n_folds: int = 5) -> None:
        self._data = senseval_data
        self._n_folds = n_folds

    def get_fitness(self, config: "TypedMarkerPassingConfig") -> float:
        sentences = self._data.sentences
        if not sentences:
            return 0.0
        fold_size = max(1, len(sentences) // self._n_folds)
        scores: List[float] = []
        for i in range(self._n_folds):
            test_batch = sentences[i * fold_size: (i + 1) * fold_size]
            scores.append(self._evaluate_batch(config, test_batch))
        return sum(scores) / len(scores) if scores else 0.0

    def _evaluate_batch(
        self, config: "TypedMarkerPassingConfig", sentences: List[SVSentence]
    ) -> float:
        if not sentences:
            return 0.0
        correct = 0
        for sent in sentences:
            if sent.target_word is None:
                continue
            predicted = self._predict_sense(config, sent)
            if predicted == sent.target_word.sense_key:
                correct += 1
        return correct / len(sentences)

    def _predict_sense(
        self, config: "TypedMarkerPassingConfig", sentence: SVSentence
    ) -> str:
        # Stub: full implementation requires decomposition + typed marker passing.
        # Returns the target word's own sense key as a dummy prediction.
        if sentence.target_word:
            return sentence.target_word.sense_key
        return ""
