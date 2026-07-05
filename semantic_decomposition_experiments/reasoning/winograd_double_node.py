from __future__ import annotations
from collections import defaultdict
from typing import Collection, Dict, List, Optional, TYPE_CHECKING

from marker_passing.node import Node
from marker_passing.spreading_step import SpreadingStep
from .markers.marker_information import MarkerInformation

if TYPE_CHECKING:
    from marker_passing import Link, Marker
    from semantic_decomposition import Concept
    from semantic_decomposition.entities.markers import DoubleMarkerWithOrigin


THRESHOLD: float = 0.3
NEGATIVE_THRESHOLD: float = -0.3


class WinogradDoubleNode(Node):
    """
    Node for Winograd-schema marker passing.

    Extends the DoubleNodeWithMultipleThresholds concept to a multi-graph
    setting integrating decomposition, syntax, NER, and role graph edges.
    """

    def __init__(
        self,
        concept: Optional["Concept"] = None,
        litheral: str = "",
    ) -> None:
        self.concept = concept
        self.litheral: str = litheral or (concept.litheral if concept else "")
        self._links: List["Link"] = []
        self._markers: List["Marker"] = []
        # per-origin activation accumulator
        self.activation: Dict["Concept", float] = defaultdict(float)
        self.activation_history: Dict["Concept", List[float]] = defaultdict(list)
        self.marker_information: List[MarkerInformation] = []
        self.is_negated: bool = False

    def get_links(self) -> List["Link"]:
        return self._links

    def get_markers(self) -> List["Marker"]:
        return self._markers

    def check_thresholds(self, marker_classes: object = None) -> bool:
        return any(
            act >= THRESHOLD or act <= NEGATIVE_THRESHOLD
            for act in self.activation.values()
        )

    def in_function(self, input_steps: Collection[SpreadingStep]) -> None:
        from semantic_decomposition.entities.markers.double_marker_with_origin import (
            DoubleMarkerWithOrigin,
        )
        for step in input_steps:
            for marker in step.markings:
                if isinstance(marker, DoubleMarkerWithOrigin) and marker.origin is not None:
                    activation = marker.activation
                    if self.is_negated:
                        activation = -activation
                    self.activation[marker.origin] += activation
                    self.activation_history[marker.origin].append(activation)
                    self.marker_information.append(
                        MarkerInformation(
                            origin=marker.origin,
                            activation=activation,
                            path=list(marker.visited_links),
                        )
                    )
                    self._markers.append(marker)

    def out_function(self) -> Collection[SpreadingStep]:
        from semantic_decomposition.entities.markers.double_marker_with_origin import (
            DoubleMarkerWithOrigin,
        )
        steps: List[SpreadingStep] = []
        for link in self._links:
            weight = getattr(link, "weight", 0.5)
            new_markers: List["Marker"] = []
            for marker in self._markers:
                if isinstance(marker, DoubleMarkerWithOrigin) and marker.origin is not None:
                    new_act = marker.activation * weight
                    new_m = DoubleMarkerWithOrigin(
                        activation=new_act,
                        origin=marker.origin,
                    )
                    new_m.visited_links = list(marker.visited_links) + [link]
                    new_m.visited_concepts = list(marker.visited_concepts)
                    new_markers.append(new_m)
            if new_markers:
                step = SpreadingStep()
                step.link = link
                step.in_direction = True
                step.markings = new_markers
                steps.append(step)
        return steps

    def get_double_activation(self) -> float:
        return sum(self.activation.values())

    def __repr__(self) -> str:
        return f"WinogradDoubleNode({self.litheral!r})"

    def __hash__(self) -> int:
        return hash(id(self))

    def __eq__(self, other: object) -> bool:
        return self is other
