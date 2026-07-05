from __future__ import annotations
import concurrent.futures
import random
from typing import Any, Callable, Dict, List, Tuple

from .parameter_learning_experiment import ParameterLearningExperiment


class MarkerPassingConfigurationEvaluator:
    """
    N-fold cross-validation fitness evaluator for marker-passing configurations.

    Mirrors Java MarkerPassingConfigurationEvaluator.
    Pass a callable `run_fn(config, examples) -> float` that evaluates one fold.
    """

    def __init__(
        self,
        run_fn: Callable[[Dict[str, Any], List], float],
        n_folds: int = 5,
    ) -> None:
        self._run_fn = run_fn
        self._n_folds = n_folds

    def get_fitness(self, config: Dict[str, Any], examples: List) -> float:
        if not examples:
            return 0.0
        fold_size = max(1, len(examples) // self._n_folds)
        scores: List[float] = []
        for i in range(self._n_folds):
            test = examples[i * fold_size: (i + 1) * fold_size]
            if test:
                scores.append(self._run_fn(config, test))
        return sum(scores) / len(scores) if scores else 0.0


class GeneticLearner:
    """
    Simple genetic-algorithm wrapper for configuration search.

    Mirrors Java GeneticLearner from AiChallenge.
    """

    GENERATION_SIZE: int = 16
    STAGNATION_LIMIT: int = 1000
    GOAL: float = 0.6

    def __init__(self, evaluator: MarkerPassingConfigurationEvaluator) -> None:
        self._evaluator = evaluator
        self._best_config: Dict[str, Any] = {}
        self._best_fitness: float = 0.0

    def learn(self, examples: List, max_generations: int = 500) -> Dict[str, Any]:
        self._best_config = ParameterLearningExperiment.create_new_marker_passing_config()
        self._best_fitness = self._evaluator.get_fitness(self._best_config, examples)

        stagnation = 0
        for _ in range(max_generations):
            if self._best_fitness >= self.GOAL or stagnation >= self.STAGNATION_LIMIT:
                break
            candidates = [
                ParameterLearningExperiment.mutate_config(self._best_config)
                for _ in range(self.GENERATION_SIZE)
            ]
            with concurrent.futures.ThreadPoolExecutor() as executor:
                futures = {
                    executor.submit(self._evaluator.get_fitness, c, examples): c
                    for c in candidates
                }
                for future in concurrent.futures.as_completed(futures):
                    fitness = future.result()
                    if fitness > self._best_fitness:
                        self._best_fitness = fitness
                        self._best_config = futures[future]
                        stagnation = 0

            stagnation += 1

        return self._best_config

    @property
    def best_fitness(self) -> float:
        return self._best_fitness
