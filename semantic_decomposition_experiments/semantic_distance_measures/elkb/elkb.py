from __future__ import annotations


class ELKB:
    """
    Stub for the ELKB (Extended Lexical Knowledge Base / Roget's Thesaurus) measure.

    The original Java implementation wraps Roget's Thesaurus XML parsing and
    graph traversal. Full re-implementation requires the thesaurus data files.
    """

    def __init__(self, thesaurus_path: str = "") -> None:
        self._path = thesaurus_path
        self._loaded = False

    def load(self) -> None:
        if not self._path:
            raise ValueError("thesaurus_path must be set before loading")
        self._loaded = True

    def compare(self, word1: str, word2: str) -> float:
        if not self._loaded:
            raise RuntimeError("Call load() first")
        raise NotImplementedError(
            "ELKB full implementation requires Roget's Thesaurus data files"
        )
