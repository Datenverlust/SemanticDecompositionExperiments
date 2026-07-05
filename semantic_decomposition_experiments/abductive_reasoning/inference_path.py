from __future__ import annotations
from typing import List, TYPE_CHECKING

if TYPE_CHECKING:
    from marker_passing import Link, Node


class InferencePath:
    """
    Records the sequence of links traversed by a PathMarker.
    Tracks which semantic relation types appear in the path and computes
    path specificity and abductiveness metrics used for collision ranking.
    """

    # Specificity values per relation type (higher = more specific)
    HYPERNYM_SPEC: float = 0.176
    MERONYM_SPEC: float = 0.500
    SYNONYM_SPEC: float = 0.750
    DEFINITION_SPEC: float = 0.600
    ANTONYM_SPEC: float = 0.900
    HYPONYM_SPEC: float = 0.824

    def __init__(self) -> None:
        self.path_type: List["Link"] = []
        self.visited_nodes: List["Node"] = []
        self.contains_hypernym_link: bool = False
        self.contains_hyponym_link: bool = False
        self.contains_synonym_link: bool = False
        self.contains_meronym_link: bool = False
        self.contains_antonym_link: bool = False
        self.contains_definition_link: bool = False
        self.contains_arbitrary_link: bool = False
        self.n_abductive_link_size: int = 0

    def add_link_to_path(self, link: "Link", node: "Node") -> None:
        self.path_type.append(link)
        self.visited_nodes.append(node)
        link_class = type(link).__name__
        if "Hypernym" in link_class:
            self.contains_hypernym_link = True
        elif "Hyponym" in link_class:
            self.contains_hyponym_link = True
        elif "Synonym" in link_class:
            self.contains_synonym_link = True
        elif "Meronym" in link_class:
            self.contains_meronym_link = True
        elif "Antonym" in link_class:
            self.contains_antonym_link = True
        elif "Definition" in link_class:
            self.contains_definition_link = True
        else:
            self.contains_arbitrary_link = True

    def is_node_on_path(self, node: "Node") -> bool:
        return node in self.visited_nodes

    def get_path_specificity(self) -> float:
        if not self.path_type:
            return 0.0
        total = 0.0
        for link in self.path_type:
            total += self._link_specificity(link)
        return total / len(self.path_type)

    def _link_specificity(self, link: "Link") -> float:
        name = type(link).__name__
        if "Hypernym" in name:
            return self.HYPERNYM_SPEC
        if "Hyponym" in name:
            return self.HYPONYM_SPEC
        if "Synonym" in name:
            return self.SYNONYM_SPEC
        if "Meronym" in name:
            return self.MERONYM_SPEC
        if "Antonym" in name:
            return self.ANTONYM_SPEC
        if "Definition" in name:
            return self.DEFINITION_SPEC
        return 0.3

    def set_abductive_link_number(self, starts_at_answer: bool) -> None:
        count = 0
        for link in self.path_type:
            name = type(link).__name__
            if starts_at_answer and "Hypernym" in name:
                count += 1
            elif not starts_at_answer and "Hyponym" in name:
                count += 1
        self.n_abductive_link_size = count

    def get_abductive_value(self) -> int:
        return self.n_abductive_link_size

    def get_abductions(self) -> int:
        return self.n_abductive_link_size

    @staticmethod
    def matches_white_list(path: "InferencePath") -> bool:
        return (
            path.contains_synonym_link
            or path.contains_hypernym_link
            or path.contains_hyponym_link
            or path.contains_meronym_link
            or path.contains_definition_link
        )

    @staticmethod
    def matches_white_list_from_question(path: "InferencePath") -> bool:
        return InferencePath.matches_white_list(path)

    def __len__(self) -> int:
        return len(self.path_type)
