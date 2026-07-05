from __future__ import annotations
from typing import Dict, List, Optional, Tuple, TYPE_CHECKING

from .nyt_corpus_document import NYTCorpusDocument

if TYPE_CHECKING:
    from semantic_decomposition import Concept


class TopicSimilarityMeasure:
    """
    Ranks documents by conceptual topic overlap with a query.

    Represents each document as a bag of Concept objects and scores by
    summing pairwise semantic similarity between query and document concepts.
    """

    def __init__(self) -> None:
        self._documents: List[NYTCorpusDocument] = []
        self._doc_concepts: Dict[str, List["Concept"]] = {}

    def add_document(self, doc: NYTCorpusDocument, concepts: List["Concept"]) -> None:
        self._documents.append(doc)
        self._doc_concepts[doc.doc_id] = concepts

    def rank(
        self,
        query_concepts: List["Concept"],
        top_n: int = 10,
    ) -> List[Tuple[NYTCorpusDocument, float]]:
        scores: List[Tuple[NYTCorpusDocument, float]] = []
        for doc in self._documents:
            doc_concepts = self._doc_concepts.get(doc.doc_id, [])
            score = self._overlap(query_concepts, doc_concepts)
            scores.append((doc, score))
        scores.sort(key=lambda x: x[1], reverse=True)
        return scores[:top_n]

    @staticmethod
    def _overlap(a: List["Concept"], b: List["Concept"]) -> float:
        a_set = {c.litheral for c in a}
        b_set = {c.litheral for c in b}
        intersection = a_set & b_set
        union = a_set | b_set
        return len(intersection) / len(union) if union else 0.0
