from __future__ import annotations
import random
from typing import Any, Dict, List, Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from semantic_decomposition.entities.spreading_activation.marker_passing_config import (
        MarkerPassingConfig,
    )


class ParameterLearningExperiment:
    """
    Genetic-algorithm parameter optimizer for MarkerPassingConfig.

    Mirrors Java ParamerterLearningExperimentGeneticAlgorithm.
    Uses simple mutation + tournament selection; no crossover.
    """

    @staticmethod
    def create_new_marker_passing_config() -> Dict[str, Any]:
        return {
            "threshold": random.uniform(0.0, 1.0),
            "start_activation": random.uniform(0.5, 2.0),
            "termination_pulse_count": random.randint(20, 200),
            "double_activation_limit": random.randint(5, 50),
            "synonym_weight": random.uniform(-1.0, 1.0),
            "antonym_weight": random.uniform(-1.0, 1.0),
            "hypernym_weight": random.uniform(-1.0, 1.0),
            "hyponym_weight": random.uniform(-1.0, 1.0),
            "definition_weight": random.uniform(-1.0, 1.0),
            "meronym_weight": random.uniform(-1.0, 1.0),
        }

    @staticmethod
    def get_random_double_mutation(value: float, sigma: float = 0.1) -> float:
        return value + random.gauss(0.0, sigma)

    @staticmethod
    def get_random_integer_mutation(value: int, step: int = 5) -> int:
        return max(1, value + random.randint(-step, step))

    @staticmethod
    def mutate_config(config: Dict[str, Any]) -> Dict[str, Any]:
        mutated = dict(config)
        key = random.choice(list(mutated.keys()))
        if isinstance(mutated[key], float):
            mutated[key] = ParameterLearningExperiment.get_random_double_mutation(mutated[key])
        else:
            mutated[key] = ParameterLearningExperiment.get_random_integer_mutation(mutated[key])
        return mutated
