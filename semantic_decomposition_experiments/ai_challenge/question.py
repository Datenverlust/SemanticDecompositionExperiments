from __future__ import annotations
from enum import Enum
from typing import Dict, List


class QuestionClass(Enum):
    FACTUAL = "FACTUAL"
    CAUSAL = "CAUSAL"
    PROCEDURAL = "PROCEDURAL"
    DEFINITIONAL = "DEFINITIONAL"
    UNKNOWN = "UNKNOWN"


class Question:
    """Multiple-choice question with four answer options."""

    def __init__(
        self,
        question_content: str = "",
        answer_a: str = "",
        answer_b: str = "",
        answer_c: str = "",
        answer_d: str = "",
        right_answer: str = "",
        question_class: QuestionClass = QuestionClass.UNKNOWN,
    ) -> None:
        self.question_content = question_content
        self.answer_a = answer_a
        self.answer_b = answer_b
        self.answer_c = answer_c
        self.answer_d = answer_d
        self.right_answer = right_answer
        self.type = question_class

    @property
    def all_answers(self) -> Dict[str, str]:
        return {"A": self.answer_a, "B": self.answer_b,
                "C": self.answer_c, "D": self.answer_d}

    def __repr__(self) -> str:
        return f"Question({self.question_content[:50]!r}...)"
