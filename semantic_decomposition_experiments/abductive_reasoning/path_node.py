from __future__ import annotations
from collections import defaultdict
from typing import Collection, Dict, List, Optional, TYPE_CHECKING

from marker_passing.node import Node
from marker_passing.spreading_step import SpreadingStep

from .path_marker import PathMarker
from .inference_collision import InferenceCollision
from .inference_path import InferencePath
from .path_marker_passing_config import PathMarkerPassingConfig

if TYPE_CHECKING:
    from marker_passing import Link, Marker
    from semantic_decomposition import Concept


class PathNode(Node):
    """
    Node implementation for abductive path-marker passing.

    Tracks per-concept activation, detects InferenceCollisions when markers
    from different origin concepts arrive at the same node, and propagates
    markers along outgoing links using whitelist-filtered paths.
    """

    def __init__(
        self,
        concept: Optional["Concept"] = None,
        config: PathMarkerPassingConfig = None,
    ) -> None:
        self.concept = concept
        self._config = config or PathMarkerPassingConfig()
        self._links: List["Link"] = []
        self._markers: List["Marker"] = []
        # activation[origin_concept] -> cumulative activation
        self.activation: Dict["Concept", float] = defaultdict(float)
        self.threshold: Dict["Concept", float] = defaultdict(
            lambda: PathMarkerPassingConfig.threshold
        )
        self.inference_collisions: List[InferenceCollision] = []

    def get_links(self) -> List["Link"]:
        return self._links

    def get_markers(self) -> List["Marker"]:
        return self._markers

    def check_thresholds(self, marker_classes: object = None) -> bool:
        return any(
            act >= self.threshold[c] for c, act in self.activation.items()
        )

    def in_function(self, input_steps: Collection[SpreadingStep]) -> None:
        path_markers: List[PathMarker] = []
        for step in input_steps:
            for m in step.markings:
                if isinstance(m, PathMarker):
                    path_markers.append(m)

        for marker in path_markers:
            if marker.origin is not None:
                self.activation[marker.origin] += marker.activation
            self._markers.append(marker)

        self._analyse_inference_collisions(path_markers)

    def _analyse_inference_collisions(self, markers: List[PathMarker]) -> None:
        question_markers = [m for m in markers if not m.starts_at_answer]
        answer_markers = [m for m in markers if m.starts_at_answer]

        for qm in question_markers:
            for am in answer_markers:
                if qm.origin != am.origin:
                    has_wl = (
                        InferencePath.matches_white_list(qm.inference_path)
                        or InferencePath.matches_white_list(am.inference_path)
                    )
                    collision = InferenceCollision(
                        qm, am, am.answer_no, has_wl
                    )
                    self.inference_collisions.append(collision)

    def out_function(self) -> Collection[SpreadingStep]:
        from ..abductive_reasoning.links import (
            AbductiveHypernymLink, AbductiveHyponymLink,
            AbductiveSynonymLink, AbductiveMeronymLink,
            AbductiveDefinitionLink, AbductiveAntonymLink,
        )
        steps: List[SpreadingStep] = []
        for link in self._links:
            weight = getattr(link, "weight", 1.0)
            new_markers: List[PathMarker] = []
            for m in self._markers:
                if isinstance(m, PathMarker) and not m.is_node_on_path(self):
                    new_m = PathMarker(
                        origin=m.origin,
                        starts_at_answer=m.starts_at_answer,
                        answer_no=m.answer_no,
                        activation=m.activation * weight,
                        parent=m,
                        link=link,
                        node=self,
                    )
                    new_markers.append(new_m)
            if new_markers:
                step = SpreadingStep()
                step.link = link
                step.in_direction = True
                step.markings = new_markers
                steps.append(step)
        return steps

    def __repr__(self) -> str:
        litheral = self.concept.litheral if self.concept else "?"
        return f"PathNode({litheral!r})"

    def __hash__(self) -> int:
        return hash(id(self))

    def __eq__(self, other: object) -> bool:
        return self is other
