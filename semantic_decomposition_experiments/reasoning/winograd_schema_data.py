from __future__ import annotations
import json
import os
from typing import List, Optional


class WinogradSchemaQuestion:
    """One pronoun coreference question from the Winograd Schema Challenge."""

    def __init__(
        self,
        sentence: str = "",
        pronoun: str = "",
        option_a: str = "",
        option_b: str = "",
        correct_answer: int = 0,
    ) -> None:
        self.sentence = sentence
        self.pronoun = pronoun
        self.options: List[str] = [option_a, option_b]
        self.correct_answer = correct_answer

    def __repr__(self) -> str:
        return f"WinogradSchemaQuestion({self.sentence[:50]!r}...)"


class WinogradSchemaDataSet:
    """Loader for Winograd Schema Challenge data."""

    def __init__(self, path: str = "") -> None:
        self._path = path
        self._questions: List[WinogradSchemaQuestion] = []

    def load(self) -> List[WinogradSchemaQuestion]:
        if not os.path.exists(self._path):
            return []
        with open(self._path, encoding="utf-8") as f:
            data = json.load(f)
        self._questions = []
        for item in data:
            q = WinogradSchemaQuestion(
                sentence=item.get("sentence", ""),
                pronoun=item.get("pronoun", ""),
                option_a=item.get("option_a", ""),
                option_b=item.get("option_b", ""),
                correct_answer=int(item.get("answer", 0)),
            )
            self._questions.append(q)
        return self._questions

    @property
    def questions(self) -> List[WinogradSchemaQuestion]:
        return self._questions
