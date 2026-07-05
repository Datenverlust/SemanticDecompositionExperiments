from __future__ import annotations
from typing import List, Optional, TYPE_CHECKING

if TYPE_CHECKING:
    from marker_passing import Link
    from semantic_decomposition import Concept


class MarkerInformation:
    """Metadata attached to a marker at a WinogradDoubleNode."""

    def __init__(
        self,
        origin: Optional["Concept"] = None,
        activation: float = 0.0,
        path: Optional[List["Link"]] = None,
    ) -> None:
        self.origin = origin
        self.activation = activation
        self.path: List["Link"] = path if path is not None else []
