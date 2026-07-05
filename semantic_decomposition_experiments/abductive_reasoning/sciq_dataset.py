from __future__ import annotations
import json
import os
from typing import List, Optional


class SciQChallengeQuestion:
    """One multiple-choice question from the SciQ dataset."""

    def __init__(
        self,
        question: str = "",
        correct_answer: str = "",
        distractor1: str = "",
        distractor2: str = "",
        distractor3: str = "",
        support: str = "",
    ) -> None:
        self.question = question
        self.correct_answer = correct_answer
        self.distractors: List[str] = [distractor1, distractor2, distractor3]
        self.support = support

    @property
    def all_answers(self) -> List[str]:
        return [self.correct_answer] + self.distractors

    def __repr__(self) -> str:
        return f"SciQChallengeQuestion({self.question[:40]!r}...)"


class SciQChallengeDataSet:
    """Loader for the SciQ JSON benchmark dataset."""

    def __init__(self, path: str = "") -> None:
        self._path = path
        self._questions: List[SciQChallengeQuestion] = []

    def load(self) -> List[SciQChallengeQuestion]:
        if not os.path.exists(self._path):
            return []
        with open(self._path, encoding="utf-8") as f:
            data = json.load(f)
        self._questions = []
        for item in data:
            q = SciQChallengeQuestion(
                question=item.get("question", ""),
                correct_answer=item.get("correct_answer", ""),
                distractor1=item.get("distractor1", ""),
                distractor2=item.get("distractor2", ""),
                distractor3=item.get("distractor3", ""),
                support=item.get("support", ""),
            )
            self._questions.append(q)
        return self._questions

    @property
    def questions(self) -> List[SciQChallengeQuestion]:
        return self._questions
