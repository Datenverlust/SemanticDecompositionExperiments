from __future__ import annotations
import csv
import os
from typing import Collection, List

from .data_set import DataSet
from .data_example import DataExample
from .similarity_pair import SimilarityPair

_DEFAULT_PATH = os.path.join(os.path.dirname(__file__), "resources", "mturk-771.csv")


class MtrukDataSet(DataSet):
    def __init__(self, path: str = _DEFAULT_PATH) -> None:
        self._path = path

    def read_example_data_set(self) -> List[DataExample]:
        return list(self.normalize())

    def normalize(self) -> Collection[SimilarityPair]:
        pairs: List[SimilarityPair] = []
        if not os.path.exists(self._path):
            return pairs
        with open(self._path, newline="", encoding="utf-8") as f:
            reader = csv.reader(f)
            next(reader, None)
            for row in reader:
                if len(row) >= 3:
                    pairs.append(SimilarityPair(row[0], row[1], 0.0, float(row[2])))
        return pairs
