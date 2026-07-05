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

from .winograd_double_node import WinogradDoubleNode
from .links import NerLink, RoleLink, SyntaxLink

if TYPE_CHECKING:
    from semantic_decomposition import Concept
    from semantic_decomposition.graph.semantic_net import SemanticNet


class _CountTermination(ProcessingStep, TerminationCondition):
    def __init__(self, max_pulses: int = 80) -> None:
        self._max = max_pulses
        self._count = 0

    def execute(self) -> None:
        self._count += 1

    def compute(self) -> bool:
        return self._count >= self._max


class _WinogradSelect(SelectFiringNodesFunction):
    def __init__(self, activation_limit: int = 20) -> None:
        self._limit = activation_limit

    def compute(self, active_nodes: Collection[Node]) -> Collection[Node]:
        return [n for n in active_nodes if isinstance(n, WinogradDoubleNode) and n.check_thresholds()]


class _WinogradIn(InFunction):
    def compute(self, input_steps: Collection[SpreadingStep], node: Node) -> None:
        if isinstance(node, WinogradDoubleNode):
            node.in_function(input_steps)


class _WinogradOut(OutFunction):
    def compute(self, node: Node) -> Collection[SpreadingStep]:
        if isinstance(node, WinogradDoubleNode):
            return node.out_function()
        return []


class WinogradDoubleMarkerPassing(SpreadingAlgorithm):
    """
    Multi-graph marker-passing for Winograd Schema resolution.

    Integrates four graph layers:
    - decomposition (semantic definitions)
    - syntax (dependency parses)
    - NER (named-entity links)
    - roles (semantic roles)
    """

    def __init__(
        self,
        termination_pulse_count: int = 80,
        double_activation_limit: int = 20,
    ) -> None:
        super().__init__()
        self._concept_to_node: Dict["Concept", WinogradDoubleNode] = {}
        self._node_to_concept: Dict[WinogradDoubleNode, "Concept"] = {}

        termination = _CountTermination(termination_pulse_count)
        self.termination_condition = termination
        self.preprocessing_steps = [termination]
        self.in_function = _WinogradIn()
        self.out_function = _WinogradOut()
        self.select_firing_nodes = _WinogradSelect(double_activation_limit)

    def fill_nodes(self, dec_graph: "SemanticNet", depth: int = 1) -> None:
        from semantic_decomposition.entities.links.synonym_link import SynonymLink
        from semantic_decomposition.entities.links.antonym_link import AntonymLink
        from semantic_decomposition.entities.links.hypernym_link import HypernymLink
        from semantic_decomposition.entities.links.hyponym_link import HyponymLink
        from semantic_decomposition.entities.links.definition_link import DefinitionLink
        from semantic_decomposition.entities.links.meronym_link import MeronymLink

        for entity in dec_graph.vertex_set():
            concept = entity
            if concept not in self._concept_to_node:
                node = WinogradDoubleNode(concept=concept)
                self._concept_to_node[concept] = node
                self._node_to_concept[node] = concept

        for edge in dec_graph.edge_set():
            src_node = self._concept_to_node.get(edge.source)
            tgt_node = self._concept_to_node.get(edge.target)
            if src_node and tgt_node:
                from semantic_decomposition.entities.links.weighted_link import WeightedLink
                link = WeightedLink(source=src_node, target=tgt_node, weight=0.5)
                if link not in src_node.get_links():
                    src_node.get_links().append(link)

        self._active_nodes = list(self._concept_to_node.values())

    def fill_syntax(self, syntax_edges: list) -> None:
        for edge in syntax_edges:
            src_node = self._get_or_create_node(edge.source)
            tgt_node = self._get_or_create_node(edge.target)
            link = SyntaxLink(source=src_node, target=tgt_node)
            if link not in src_node.get_links():
                src_node.get_links().append(link)

    def fill_ner_edges(self, ner_edges: list) -> None:
        for edge in ner_edges:
            src_node = self._get_or_create_node(edge.source)
            tgt_node = self._get_or_create_node(edge.target)
            link = NerLink(source=src_node, target=tgt_node)
            if link not in src_node.get_links():
                src_node.get_links().append(link)

    def fill_role_edges(self, role_edges: list) -> None:
        for edge in role_edges:
            src_node = self._get_or_create_node(edge.source)
            tgt_node = self._get_or_create_node(edge.target)
            link = RoleLink(source=src_node, target=tgt_node)
            if link not in src_node.get_links():
                src_node.get_links().append(link)

    def set_negatives(self, negated_concepts: List["Concept"]) -> None:
        for concept in negated_concepts:
            node = self._concept_to_node.get(concept)
            if node:
                node.is_negated = True

    def _get_or_create_node(self, concept: "Concept") -> WinogradDoubleNode:
        if concept not in self._concept_to_node:
            node = WinogradDoubleNode(concept=concept)
            self._concept_to_node[concept] = node
            self._node_to_concept[node] = concept
            self._active_nodes.append(node)
        return self._concept_to_node[concept]

    def do_initial_marking(
        self,
        concepts: List["Concept"],
        start_activation: float = 1.0,
    ) -> None:
        from semantic_decomposition.entities.markers.double_marker_with_origin import (
            DoubleMarkerWithOrigin,
        )
        for concept in concepts:
            node = self._concept_to_node.get(concept)
            if node is not None:
                marker = DoubleMarkerWithOrigin(activation=start_activation, origin=concept)
                node.get_markers().append(marker)
                node.activation[concept] += start_activation

    def get_double_activation(self) -> Dict["Concept", float]:
        result: Dict["Concept", float] = {}
        for node in self._active_nodes:
            if isinstance(node, WinogradDoubleNode):
                for concept, act in node.activation.items():
                    result[concept] = result.get(concept, 0.0) + act
        return result
