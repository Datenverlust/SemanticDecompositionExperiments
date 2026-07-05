from __future__ import annotations
from typing import Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from semantic_decomposition import IConcept


class Word2VecSemanticDistanceMeasure:
    """
    Cosine-similarity-based semantic distance using Word2Vec embeddings.

    Replaces the Java DeepLearning4j implementation with gensim.
    Load a pre-trained model with load_model() before calling compare().
    """

    def __init__(self) -> None:
        self._model: Optional[object] = None

    def load_model(self, path: str, binary: bool = True) -> None:
        try:
            from gensim.models import KeyedVectors
        except ImportError as exc:
            raise ImportError("gensim is required: pip install gensim") from exc
        self._model = KeyedVectors.load_word2vec_format(path, binary=binary)

    def compare(self, word1: str, word2: str) -> float:
        if self._model is None:
            return 0.0
        try:
            return float(self._model.similarity(word1, word2))
        except KeyError:
            return 0.0

    def compare_concepts(self, c1: "IConcept", c2: "IConcept") -> float:
        w1 = getattr(c1, "litheral", str(c1))
        w2 = getattr(c2, "litheral", str(c2))
        return self.compare(w1, w2)
