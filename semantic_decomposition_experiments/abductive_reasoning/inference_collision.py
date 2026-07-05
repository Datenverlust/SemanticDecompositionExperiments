from __future__ import annotations
from typing import Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from .path_marker import PathMarker
    from semantic_decomposition import Concept


class InferenceCollision:
    """
    Records the meeting point of a question marker and an answer marker at
    the same node. Provides several comparison/ranking strategies.
    """

    def __init__(
        self,
        m1: "PathMarker",
        m2: "PathMarker",
        answer_no: int = -1,
        contains_white_list_link: bool = False,
    ) -> None:
        self.m1 = m1
        self.m2 = m2
        self.answer_no = answer_no
        self.contains_white_list_link = contains_white_list_link

    def get_answer_number(self) -> int:
        return self.answer_no

    def get_question_concept(self) -> Optional["Concept"]:
        if not self.m1.starts_at_answer:
            return self.m1.origin
        return self.m2.origin

    def get_answer_concept(self) -> Optional["Concept"]:
        if self.m1.starts_at_answer:
            return self.m1.origin
        return self.m2.origin

    def get_specificity(self) -> float:
        s1 = self.m1.inference_path.get_path_specificity()
        s2 = self.m2.inference_path.get_path_specificity()
        return (s1 + s2) / 2.0

    def get_abductions(self) -> int:
        return (
            self.m1.inference_path.get_abductions()
            + self.m2.inference_path.get_abductions()
        )

    def get_visited_nodes(self) -> int:
        return self.m1.get_path_length() + self.m2.get_path_length()

    def compare(self, other: "InferenceCollision") -> int:
        """Favour fewer abductions; break ties by longer path (more specific)."""
        if self.get_abductions() != other.get_abductions():
            return self.get_abductions() - other.get_abductions()
        return other.get_visited_nodes() - self.get_visited_nodes()

    def compare_v7(self, other: "InferenceCollision") -> int:
        """v7: favour higher specificity; break ties by fewer abductions."""
        diff = other.get_specificity() - self.get_specificity()
        if abs(diff) > 1e-9:
            return -1 if diff > 0 else 1
        return self.get_abductions() - other.get_abductions()

    def __repr__(self) -> str:
        return (
            f"InferenceCollision(answer={self.answer_no}, "
            f"abductions={self.get_abductions()}, "
            f"path_len={self.get_visited_nodes()})"
        )
