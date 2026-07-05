from __future__ import annotations
from typing import Collection, Dict, List, Optional, TYPE_CHECKING

from marker_passing.spreading_algorithm import SpreadingAlgorithm
from marker_passing.spreading_step import SpreadingStep
from marker_passing.in_function import InFunction
from marker_passing.out_function import OutFunction
from marker_passing.select_firing_nodes_function import SelectFiringNodesFunction
from marker_passing.node import Node
from marker_passing.termination_condition import TerminationCondition
from marker_passing.processing_step import ProcessingStep

from .path_marker_passing_config import PathMarkerPassingConfig
from .path_node import PathNode
from .path_marker import PathMarker
from .inference_collision import InferenceCollision
from .links import (
    AbductiveHypernymLink, AbductiveHyponymLink, AbductiveSynonymLink,
    AbductiveMeronymLink, AbductiveDefinitionLink, AbductiveAntonymLink,
    AbductiveArbitraryLink,
)

if TYPE_CHECKING:
    from semantic_decomposition import Concept


class _CountTermination(ProcessingStep, TerminationCondition):
    def __init__(self, max_pulses: int) -> None:
        self._max = max_pulses
        self._count = 0

    def execute(self) -> None:
        self._count += 1

    def compute(self) -> bool:
        return self._count >= self._max


class _ThresholdSelect(SelectFiringNodesFunction):
    def compute(self, active_nodes: Collection[Node]) -> Collection[Node]:
        return [n for n in active_nodes if isinstance(n, PathNode) and n.check_thresholds()]


class _PathIn(InFunction):
    def compute(self, input_steps: Collection[SpreadingStep], node: Node) -> None:
        if isinstance(node, PathNode):
            node.in_function(input_steps)


class _PathOut(OutFunction):
    def compute(self, node: Node) -> Collection[SpreadingStep]:
        if isinstance(node, PathNode):
            return node.out_function()
        return []


class PathMarkerPassing(SpreadingAlgorithm):
    """
    Abductive marker-passing algorithm over a semantic concept graph.

    Builds a PathNode network from concept decompositions, places PathMarkers
    at initial question/answer concepts, runs spreading activation, and
    collects InferenceCollisions at convergence points.
    """

    def __init__(self, config: Optional[PathMarkerPassingConfig] = None) -> None:
        super().__init__()
        self._config = config or PathMarkerPassingConfig()
        self._concept_to_node: Dict["Concept", PathNode] = {}
        self._node_to_concept: Dict[PathNode, "Concept"] = {}
        self._inference_collisions: List[InferenceCollision] = []
        self._round_count: int = 0

        termination = _CountTermination(self._config.termination_puls_count)
        self.termination_condition = termination
        self.preprocessing_steps = [termination]
        self.in_function = _PathIn()
        self.out_function = _PathOut()
        self.select_firing_nodes = _ThresholdSelect()

    def fill_nodes(self, concepts: List["Concept"]) -> None:
        for concept in concepts:
            self._add_concept_recursively(concept, self._config.decomposition_depth)
        self._active_nodes = list(self._concept_to_node.values())

    def _add_concept_recursively(self, concept: "Concept", depth: int) -> PathNode:
        if concept in self._concept_to_node:
            return self._concept_to_node[concept]

        node = PathNode(concept=concept, config=self._config)
        self._concept_to_node[concept] = node
        self._node_to_concept[node] = concept

        if depth <= 0:
            return node

        self._connect(concept, concept.synonyms, AbductiveSynonymLink, depth)
        self._connect(concept, concept.antonyms, AbductiveAntonymLink, depth)
        self._connect(concept, concept.hypernyms, AbductiveHypernymLink, depth)
        self._connect(concept, concept.hyponyms, AbductiveHyponymLink, depth)
        self._connect(concept, concept.meronyms, AbductiveMeronymLink, depth)
        for definition in concept.definitions:
            if hasattr(definition, "concepts"):
                self._connect(concept, definition.concepts, AbductiveDefinitionLink, depth)

        return node

    def _connect(self, src_concept: "Concept", targets: list, LinkClass, depth: int) -> None:
        src_node = self._concept_to_node[src_concept]
        for target_concept in targets:
            tgt_node = self._add_concept_recursively(target_concept, depth - 1)
            link = LinkClass(source=src_node, target=tgt_node)
            if link not in src_node.get_links():
                src_node.get_links().append(link)

    @staticmethod
    def do_initial_marking(
        question_concepts: List["Concept"],
        answer_concepts_per_slot: List[List["Concept"]],
        algo: "PathMarkerPassing",
        start_activation: float = 1.0,
    ) -> None:
        for concept in question_concepts:
            node = algo._concept_to_node.get(concept)
            if node is not None:
                marker = PathMarker(
                    origin=concept,
                    starts_at_answer=False,
                    answer_no=-1,
                    activation=start_activation,
                )
                node.get_markers().append(marker)
                node.activation[concept] += start_activation

        for answer_no, answer_concepts in enumerate(answer_concepts_per_slot):
            for concept in answer_concepts:
                node = algo._concept_to_node.get(concept)
                if node is not None:
                    marker = PathMarker(
                        origin=concept,
                        starts_at_answer=True,
                        answer_no=answer_no,
                        activation=start_activation,
                    )
                    node.get_markers().append(marker)
                    node.activation[concept] += start_activation

    def get_all_inference_collisions(self) -> List[InferenceCollision]:
        collisions: List[InferenceCollision] = []
        for node in self._active_nodes:
            if isinstance(node, PathNode):
                collisions.extend(node.inference_collisions)
        return collisions

    def get_correct_answer_number(self) -> int:
        """Pick the answer whose collisions have the fewest abductions (v1)."""
        collisions = self.get_all_inference_collisions()
        if not collisions:
            return -1
        best = min(collisions, key=lambda c: (c.get_abductions(), -c.get_visited_nodes()))
        return best.get_answer_number()

    def get_correct_answer_number_v7(self) -> int:
        """v7: rank by specificity, break ties with fewest abductions."""
        collisions = self.get_all_inference_collisions()
        if not collisions:
            return -1
        best = max(
            collisions,
            key=lambda c: (c.get_specificity(), -c.get_abductions(), c.get_visited_nodes()),
        )
        return best.get_answer_number()

    def get_question_nodes(self) -> List[PathNode]:
        return [
            n for n in self._active_nodes
            if isinstance(n, PathNode)
            and any(not m.starts_at_answer for m in n.get_markers() if isinstance(m, PathMarker))
        ]
