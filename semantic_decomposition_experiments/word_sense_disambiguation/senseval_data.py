from __future__ import annotations
import os
import xml.etree.ElementTree as ET
from typing import Dict, List, Optional


class SVWord:
    def __init__(self, lemma: str = "", pos: str = "", sense_key: str = "") -> None:
        self.lemma = lemma
        self.pos = pos
        self.sense_key = sense_key


class SVSense:
    def __init__(self, sense_key: str = "", frequency: int = 1) -> None:
        self.sense_key = sense_key
        self.frequency = frequency


class SVSentence:
    def __init__(self, sentence_id: str = "") -> None:
        self.sentence_id = sentence_id
        self.words: List[SVWord] = []
        self.target_word: Optional[SVWord] = None

    def __repr__(self) -> str:
        return f"SVSentence({self.sentence_id!r}, words={len(self.words)})"


class SensevalData:
    """Singleton loader for Senseval-3 corpus XML data."""

    _instance: Optional["SensevalData"] = None
    _sentences: List[SVSentence] = []

    def __init__(self, path: str = "") -> None:
        self._path = path

    @classmethod
    def get(cls) -> "SensevalData":
        if cls._instance is None:
            cls._instance = cls()
        return cls._instance

    def load(self, path: str) -> List[SVSentence]:
        self._path = path
        if not os.path.exists(path):
            return []
        tree = ET.parse(path)
        root = tree.getroot()
        self._sentences = []
        for corpus_elem in root.iter("corpus"):
            for sent_elem in corpus_elem.iter("sentence"):
                sent = SVSentence(sentence_id=sent_elem.get("id", ""))
                for word_elem in sent_elem:
                    w = SVWord(
                        lemma=word_elem.get("lemma", word_elem.text or ""),
                        pos=word_elem.get("pos", ""),
                        sense_key=word_elem.get("senseid", ""),
                    )
                    sent.words.append(w)
                    if word_elem.tag == "head":
                        sent.target_word = w
                self._sentences.append(sent)
        SensevalData._sentences = self._sentences
        return self._sentences

    @property
    def sentences(self) -> List[SVSentence]:
        return self._sentences
