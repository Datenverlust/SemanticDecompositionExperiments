from __future__ import annotations
import csv
import os
from typing import Dict, List, Optional

from .question import Question, QuestionClass


class DataLoader:
    """Loads the AI2 Science Questions CSV dataset."""

    @staticmethod
    def read_example_data_set(path: str) -> List[Question]:
        if not os.path.exists(path):
            return []
        questions: List[Question] = []
        with open(path, newline="", encoding="utf-8") as f:
            reader = csv.DictReader(f)
            for row in reader:
                q = Question(
                    question_content=row.get("questionStatement", ""),
                    answer_a=row.get("answerA", ""),
                    answer_b=row.get("answerB", ""),
                    answer_c=row.get("answerC", ""),
                    answer_d=row.get("answerD", ""),
                    right_answer=row.get("correctAnswer", ""),
                    question_class=DataLoader._classify(row.get("subject", "")),
                )
                questions.append(q)
        return questions

    @staticmethod
    def get_question_map(questions: List[Question]) -> Dict[QuestionClass, List[Question]]:
        result: Dict[QuestionClass, List[Question]] = {qc: [] for qc in QuestionClass}
        for q in questions:
            result[q.type].append(q)
        return result

    @staticmethod
    def _classify(subject: str) -> QuestionClass:
        s = subject.lower()
        if "caus" in s:
            return QuestionClass.CAUSAL
        if "proc" in s or "how" in s:
            return QuestionClass.PROCEDURAL
        if "def" in s or "what is" in s:
            return QuestionClass.DEFINITIONAL
        if subject:
            return QuestionClass.FACTUAL
        return QuestionClass.UNKNOWN
