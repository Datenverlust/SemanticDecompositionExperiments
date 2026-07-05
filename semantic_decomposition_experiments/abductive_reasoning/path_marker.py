from __future__ import annotations
from typing import Optional, TYPE_CHECKING

from .inference_path import InferencePath

if TYPE_CHECKING:
    from marker_passing import Link
    from semantic_decomposition import Concept


class PathMarker:
    """
    Marker that carries its full traversal history as an InferencePath.

    New markers are created (immutable extension) when crossing a link rather
    than mutating in place, so each node on the path sees its own history.
    """

    def __init__(
        self,
        origin: Optional["Concept"] = None,
        starts_at_answer: bool = False,
        answer_no: int = -1,
        activation: float = 1.0,
        parent: Optional["PathMarker"] = None,
        link: Optional["Link"] = None,
        node: object = None,
    ) -> None:
        self.origin: Optional["Concept"] = origin
        self.starts_at_answer: bool = starts_at_answer
        self.answer_no: int = answer_no
        self.activation: float = activation
        self.abduktive_node: bool = False

        if parent is None:
            self.inference_path = InferencePath()
        else:
            # Copy-extend the parent's path
            self.inference_path = InferencePath()
            self.inference_path.path_type = list(parent.inference_path.path_type)
            self.inference_path.visited_nodes = list(parent.inference_path.visited_nodes)
            self.inference_path.contains_hypernym_link = parent.inference_path.contains_hypernym_link
            self.inference_path.contains_hyponym_link = parent.inference_path.contains_hyponym_link
            self.inference_path.contains_synonym_link = parent.inference_path.contains_synonym_link
            self.inference_path.contains_meronym_link = parent.inference_path.contains_meronym_link
            self.inference_path.contains_antonym_link = parent.inference_path.contains_antonym_link
            self.inference_path.contains_definition_link = parent.inference_path.contains_definition_link
            self.inference_path.contains_arbitrary_link = parent.inference_path.contains_arbitrary_link
            self.inference_path.n_abductive_link_size = parent.inference_path.n_abductive_link_size
            if link is not None and node is not None:
                self.inference_path.add_link_to_path(link, node)

    def is_node_on_path(self, node: object) -> bool:
        return self.inference_path.is_node_on_path(node)

    def get_path_length(self) -> int:
        return len(self.inference_path)

    def __repr__(self) -> str:
        return (
            f"PathMarker(origin={self.origin}, answer={self.answer_no}, "
            f"path_len={self.get_path_length()})"
        )
