from __future__ import annotations
import copy


class DataExample:
    def __init__(self, result: float = 0.0, true_result: float = 0.0) -> None:
        self.result: float = result
        self.true_result: float = true_result

    def clone(self) -> "DataExample":
        return copy.copy(self)
