from __future__ import annotations
import os
import xml.etree.ElementTree as ET
from typing import List, Optional


class NYTCorpusDocument:
    """Represents a parsed New York Times corpus article."""

    def __init__(
        self,
        doc_id: str = "",
        headline: str = "",
        body: str = "",
        keywords: Optional[List[str]] = None,
    ) -> None:
        self.doc_id = doc_id
        self.headline = headline
        self.body = body
        self.keywords: List[str] = keywords if keywords is not None else []

    def __repr__(self) -> str:
        return f"NYTCorpusDocument({self.doc_id!r}, {self.headline[:40]!r})"


class NYTCorpusDocumentParser:
    """Parses NYT corpus XML files into NYTCorpusDocument objects."""

    @staticmethod
    def parse(path: str) -> Optional[NYTCorpusDocument]:
        if not os.path.exists(path):
            return None
        try:
            tree = ET.parse(path)
            root = tree.getroot()
        except ET.ParseError:
            return None

        doc_id = root.findtext(".//doc-id", default="")
        headline_elem = root.find(".//hl1") or root.find(".//hl2")
        headline = headline_elem.text.strip() if headline_elem is not None and headline_elem.text else ""
        body_parts = [
            (elem.text or "")
            for elem in root.iter("p")
        ]
        body = " ".join(body_parts).strip()
        keywords = [
            elem.get("content", "")
            for elem in root.findall(".//classifier[@type='descriptor']")
        ]
        return NYTCorpusDocument(doc_id=doc_id, headline=headline, body=body, keywords=keywords)

    @staticmethod
    def parse_directory(directory: str) -> List[NYTCorpusDocument]:
        docs: List[NYTCorpusDocument] = []
        for fname in os.listdir(directory):
            if fname.endswith(".xml"):
                doc = NYTCorpusDocumentParser.parse(os.path.join(directory, fname))
                if doc:
                    docs.append(doc)
        return docs
