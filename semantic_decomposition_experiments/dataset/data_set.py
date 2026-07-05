from abc import ABC, abstractmethod
from typing import Collection, List
from .data_example import DataExample
from .similarity_pair import SimilarityPair


class DataSet(ABC):
    @abstractmethod
    def read_example_data_set(self) -> List[DataExample]: ...

    @abstractmethod
    def normalize(self) -> Collection[SimilarityPair]: ...
