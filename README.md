# SemanticDecompositionExperiments

NLP experiments built on top of
[SemanticDecomposition](https://github.com/Datenverlust/SemanticDecomposition)
and [MarkerPassingAlgorithm](https://github.com/Datenverlust/MarkerPassingAlgorithm).

The experiments cover a broad range of natural language understanding tasks using
spreading-activation over semantic concept graphs:

| Module | Task |
|---|---|
| **AbductiveReasoning** | Science QA via abductive path-marker inference (SciQ) |
| **Reasoning** | Pronoun coreference resolution (Winograd Schema Challenge) |
| **AiChallenge** | Multiple-choice question answering (AI2 Science Questions) |
| **WordSenseDisambiguation** | WSD via typed marker passing (Senseval-3) |
| **SentenceSemanticSimilarityMeasure** | Sentence-level similarity averaging |
| **SentenceSenseDisambiguation** | Context-aware word-sense selection |
| **SemanticDistanceMeasures** | BDOS, Word2Vec, ELKB similarity measures |
| **ParameterLearner** | Genetic-algorithm optimisation of marker-passing configs |
| **TopicSimilarityDocumentRetrieval** | Topic-based document ranking (NYT corpus) |
| **SemanticEmoji** | Semantic graph analysis of the top-10 emojis |
| **DataSet** | Benchmark dataset loaders (WordSim353, MEN, Rubenstein, …) |

```
@InProceedings{Faehndrich2018,
  author    = {Fähndrich, Johannes and Weber, Sabine and Kanthak, Hannes},
  title     = {A Marker Passing Approach to Winograd Schemas},
  booktitle = {Semantic Technology},
  year      = {2018},
  publisher = {Springer International Publishing},
  pages     = {165--181},
  doi       = {10.1007/978-3-030-04284-4_11}
}
```

---

## Requirements

| Package | Source | Role |
|---|---|---|
| Python 3.9+ | — | — |
| `marker_passing` | [MarkerPassingAlgorithm](https://github.com/Datenverlust/MarkerPassingAlgorithm) `python` branch | Spreading-activation engine |
| `semantic_decomposition` | [SemanticDecomposition](https://github.com/Datenverlust/SemanticDecomposition) `python` branch | Concept graph builder |
| `spacy` *(optional)* | `pip install spacy` | Lemmatisation / NLP pipeline |
| `gensim` *(optional)* | `pip install gensim` | Word2Vec similarity measure |

## Installation

```bash
# 1. MarkerPassingAlgorithm
git clone https://github.com/Datenverlust/MarkerPassingAlgorithm.git
cd MarkerPassingAlgorithm && git checkout python && pip install -e . && cd ..

# 2. SemanticDecomposition
git clone https://github.com/Datenverlust/SemanticDecomposition.git
cd SemanticDecomposition && git checkout python && pip install -e . && cd ..

# 3. SemanticDecompositionExperiments
git clone https://github.com/Datenverlust/SemanticDecompositionExperiments.git
cd SemanticDecompositionExperiments && git checkout python
pip install -e .
```

---

## Package structure

```
semantic_decomposition_experiments/
├── __init__.py
│
├── dataset/                        # Benchmark dataset loaders
│   ├── data_set.py                 #   DataSet ABC
│   ├── data_example.py             #   DataExample base
│   ├── similarity_pair.py          #   SimilarityPair(string1, string2, true_result)
│   ├── word_sim353_data_set.py     #   WordSim-353
│   ├── men_data_set.py             #   MEN
│   ├── msr_vid.py                  #   MSRvid (paraphrase pairs)
│   ├── rubenstein1965_dataset.py   #   Rubenstein & Goodenough 1965
│   ├── stanford_rare_word_similarity_dataset.py
│   └── mtruk_data_set.py           #   MTurk-771
│
├── semantic_distance_measures/
│   ├── bdos/bdos.py                # BDOS  — bidirectional one-step BFS similarity
│   ├── word2vec/                   # Word2Vec cosine similarity via gensim
│   └── elkb/                       # ELKB / Roget's Thesaurus (stub)
│
├── abductive_reasoning/            # Path-marker abductive inference
│   ├── path_marker_passing_config.py   # Static config: link weights + pulse count
│   ├── inference_path.py           #   Link-type path history + specificity scoring
│   ├── path_marker.py              #   Immutable marker with path extension
│   ├── inference_collision.py      #   Meeting point of two markers + ranking
│   ├── path_node.py                #   Node with per-concept activation + collision detection
│   ├── path_marker_passing.py      #   PathMarkerPassing algorithm
│   ├── sciq_dataset.py             #   SciQ JSON dataset loader
│   └── links/
│       └── abductive_links.py      #   AbductiveWeightedLink + typed subclasses
│
├── reasoning/                      # Winograd Schema Challenge
│   ├── winograd_double_node.py     #   WinogradDoubleNode — multi-graph node with negation
│   ├── winograd_double_marker_passing.py  # 4-graph spreading activation
│   ├── winograd_schema_data.py     #   WinogradSchemaQuestion + WinogradSchemaDataSet
│   ├── edges/                      #   NerEdge, RoleEdge, SyntaxEdge
│   ├── links/                      #   NerLink, RoleLink, SyntaxLink (weight 0.5)
│   └── markers/
│       └── marker_information.py   #   MarkerInformation(origin, activation, path)
│
├── word_sense_disambiguation/
│   ├── senseval_data.py            #   Senseval-3 XML corpus loader
│   └── typed_marker_config_evaluator.py
│
├── sentence_semantic_similarity_measure/
│   └── sentence_semantic_similarity_measure.py  # pairwise concept averaging
│
├── sentence_sense_disambiguation/
│   └── sentence_sense_disambiguation.py  # context-overlap WSD
│
├── parameter_learner/
│   ├── parameter_learning_experiment.py        # Mutation operators
│   └── marker_passing_configuration_evaluator.py  # N-fold CV + GeneticLearner
│
├── topic_similarity_document_retrieval/
│   ├── nyt_corpus_document.py      #   NYT XML parser
│   └── topic_similarity_measure.py #   Jaccard-based topic ranker
│
├── ai_challenge/
│   ├── question.py                 #   Question + QuestionClass enum
│   ├── graph_cache.py              #   Pre-built graph bundle per question
│   ├── evaluator.py                #   Activation-score extraction
│   └── data_loader.py              #   AI2 Science Questions CSV loader
│
└── semantic_emoji/
    ├── emoji_graph_builder.py      #   Top-10 emojis → SemanticNet
    └── emoji_net_crawler.py        #   emojitracker.com API client
```

---

## Module documentation

### Abductive reasoning (`abductive_reasoning/`)

Answers multiple-choice science questions by placing markers at question concepts
and answer concepts, running spreading activation, and finding where markers from
the two sides collide.

#### Key classes

**`PathMarkerPassingConfig`** — static config for abductive marker passing.

| Field | Default | Description |
|---|---|---|
| `start_activation` | `1.0` | Initial marker activation |
| `termination_puls_count` | `80` | Max pulses |
| `decomposition_depth` | `1` | Graph expansion depth |
| `synonym_weight` | `0.62` | Weight on synonym links |
| `hypernym_weight` | `-0.02` | Weight on hypernym links |
| `hyponym_weight` | `0.79` | Weight on hyponym links |
| `meronym_weight` | `0.50` | Weight on meronym links |
| `definition_weight` | `-0.78` | Weight on definition links |
| `antonym_weight` | `-0.90` | Weight on antonym links |

**`InferencePath`** — tracks the sequence of link types a marker has traversed.
Computes:
- `get_path_specificity()` — average specificity of all links in the path
- `get_abductions()` — number of abductive (hypernym/hyponym) hops

Specificity constants:

| Relation | Specificity |
|---|---|
| Antonym | 0.900 |
| Hyponym | 0.824 |
| Synonym | 0.750 |
| Definition | 0.600 |
| Meronym | 0.500 |
| Hypernym | 0.176 |

**`InferenceCollision`** — recorded when a question-side marker meets an
answer-side marker at the same node.

| Method | Description |
|---|---|
| `get_answer_number()` | Which answer option (0-indexed) this collision supports |
| `get_specificity()` | Mean path specificity of both markers |
| `get_abductions()` | Total abductive hops of both paths |
| `get_visited_nodes()` | Total path length of both markers |
| `compare(other)` | v1 ranking: fewer abductions wins; ties broken by longer path |
| `compare_v7(other)` | v7 ranking: higher specificity wins; ties broken by fewer abductions |

**`PathMarkerPassing`** — the main algorithm.

| Method | Description |
|---|---|
| `fill_nodes(concepts)` | Build graph from concept list |
| `do_initial_marking(q_concepts, a_concepts_per_slot, algo)` | Place markers at question and answer nodes |
| `execute()` | Run the pulse loop |
| `get_all_inference_collisions()` | All collisions found across all nodes |
| `get_correct_answer_number()` | Best answer by v1 ranking |
| `get_correct_answer_number_v7()` | Best answer by v7 ranking |

---

### Reasoning / Winograd Schema (`reasoning/`)

Resolves pronoun coreference in Winograd Schemas by integrating four graph layers:
decomposition (semantic), syntax (dependency parse), NER, and semantic roles.

**`WinogradDoubleNode`**

| Attribute | Description |
|---|---|
| `activation: Dict[Concept, float]` | Per-concept activation accumulation |
| `is_negated: bool` | Whether this node's concept is negated |
| `THRESHOLD = 0.3` | Fire threshold |
| `NEGATIVE_THRESHOLD = -0.3` | Negative fire threshold |
| `marker_information` | List of `MarkerInformation` items |

**`WinogradDoubleMarkerPassing`**

| Method | Description |
|---|---|
| `fill_nodes(dec_graph, depth)` | Populate from a `SemanticNet` |
| `fill_syntax(syntax_edges)` | Add dependency-parse edges |
| `fill_ner_edges(ner_edges)` | Add named-entity edges |
| `fill_role_edges(role_edges)` | Add semantic-role edges |
| `set_negatives(concepts)` | Mark negated concepts |
| `do_initial_marking(concepts, start_activation)` | Seed activation |
| `get_double_activation()` | `Dict[Concept, float]` — total activation per concept |

---

### Semantic distance measures (`semantic_distance_measures/`)

**`BDOS`** — Bidirectional One-Step algorithm. Computes similarity as
`1 - (shortest_path_length / max_path_length)` using bidirectional BFS.

```python
from semantic_decomposition_experiments.semantic_distance_measures.bdos import BDOS

bdos = BDOS()
similarity = bdos.compare(concept1, concept2)  # 0.0–1.0
```

**`Word2VecSemanticDistanceMeasure`** — cosine similarity via gensim.

```python
from semantic_decomposition_experiments.semantic_distance_measures.word2vec import (
    Word2VecSemanticDistanceMeasure,
)

w2v = Word2VecSemanticDistanceMeasure()
w2v.load_model("GoogleNews-vectors-negative300.bin", binary=True)
print(w2v.compare("cat", "dog"))           # float 0.0–1.0
print(w2v.compare_concepts(cat, dog))      # accepts Concept objects
```

---

### Parameter learner (`parameter_learner/`)

Optimises `TypedMarkerPassingConfig` parameters using a genetic algorithm with
N-fold cross-validation.

**`GeneticLearner`** constants:

| Constant | Value | Description |
|---|---|---|
| `GENERATION_SIZE` | `16` | Individuals per generation |
| `STAGNATION_LIMIT` | `1000` | Stop if best fitness unchanged for this many generations |
| `GOAL` | `0.6` | Target fitness; stop early if reached |

**`ParameterLearningExperiment`** provides mutation operators:
- `get_random_double_mutation()` — perturbs float parameters
- `get_random_integer_mutation()` — perturbs int parameters
- `mutate_config(config)` — applies one random mutation

---

### Dataset loaders (`dataset/`)

All loaders implement `DataSet` with `read_example_data_set()` and `normalize()`.

| Class | File format | Similarity scale |
|---|---|---|
| `WordSim353DataSet` | CSV | 0–10 |
| `MENDataSet` | TSV | 0–50 |
| `MSRvid` | CSV | 0–5 |
| `Rubenstein1965Dataset` | CSV | 0–4 |
| `StanfordRareWordSimilarityDataset` | CSV | 0–10 |
| `MtrukDataSet` | CSV | 0–5 |

`normalize()` maps scores to `[0, 1]`.

---

## Usage examples

### Example 1 — Abductive science QA (SciQ)

```python
from semantic_decomposition import Decomposition, WordType
from semantic_decomposition_experiments.abductive_reasoning.path_marker_passing import (
    PathMarkerPassing,
)
from semantic_decomposition_experiments.abductive_reasoning.sciq_dataset import (
    SciQChallengeDataSet,
)

# Load a question
dataset = SciQChallengeDataSet("path/to/train.json")
question = dataset.load()[0]
print(question.question)        # "What do plants use for photosynthesis?"
print(question.correct_answer)  # "sunlight"
print(question.all_answers)     # ["water", "sunlight", "oxygen", "carbon"]

# Decompose concepts
Decomposition.init([MyDictionary()])

q_concepts = [
    Decomposition.decompose(Decomposition.create_concept(w, WordType.NN))
    for w in question.question.split()
]
a_concepts = [
    [Decomposition.decompose(Decomposition.create_concept(a, WordType.NN))]
    for a in question.all_answers
]

# Run abductive marker passing
algo = PathMarkerPassing()
all_concepts = q_concepts + [c for lst in a_concepts for c in lst]
algo.fill_nodes(all_concepts)
PathMarkerPassing.do_initial_marking(q_concepts, a_concepts, algo)
algo.execute()

predicted = algo.get_correct_answer_number_v7()
print(f"Predicted answer: {question.all_answers[predicted]}")

# Inspect collisions
collisions = algo.get_all_inference_collisions()
for col in sorted(collisions, key=lambda c: -c.get_specificity())[:5]:
    print(f"  Answer {col.get_answer_number()}: "
          f"specificity={col.get_specificity():.3f}, "
          f"abductions={col.get_abductions()}")
```

---

### Example 2 — Winograd Schema coreference resolution

```python
from semantic_decomposition import Decomposition, WordType
from semantic_decomposition.graph.graph_util import GraphUtil
from semantic_decomposition_experiments.reasoning.winograd_double_marker_passing import (
    WinogradDoubleMarkerPassing,
)

# Build semantic graph for the sentence
words = ["The", "trophy", "would", "not", "fit", "in", "the", "suitcase"]
concepts = [
    Decomposition.decompose(Decomposition.create_concept(w, WordType.NN))
    for w in words
]
dec_graph = GraphUtil.create_graph(concepts[1])  # build around "trophy"

# Set up Winograd marker passing
algo = WinogradDoubleMarkerPassing(
    termination_pulse_count=80,
    double_activation_limit=20,
)
algo.fill_nodes(dec_graph)

# Mark negated concepts
not_concept = next(c for c in concepts if c.litheral == "not")
algo.set_negatives([not_concept])

# Seed from the anaphora candidates
trophy = concepts[1]
suitcase = concepts[7]
algo.do_initial_marking([trophy, suitcase], start_activation=1.0)
algo.execute()

activations = algo.get_double_activation()
best = max(activations, key=activations.get)
print(f"Resolved pronoun antecedent: {best.litheral}")
```

---

### Example 3 — Semantic distance (BDOS)

```python
from semantic_decomposition import Decomposition, WordType
from semantic_decomposition_experiments.semantic_distance_measures.bdos import BDOS

Decomposition.init([MyDictionary()])

cat = Decomposition.decompose(Decomposition.create_concept("cat", WordType.NN))
dog = Decomposition.decompose(Decomposition.create_concept("dog", WordType.NN))
car = Decomposition.decompose(Decomposition.create_concept("car", WordType.NN))

bdos = BDOS()
print(bdos.compare(cat, dog))   # higher — animals share hypernyms
print(bdos.compare(cat, car))   # lower  — few shared paths
```

---

### Example 4 — Word2Vec similarity

```python
from semantic_decomposition_experiments.semantic_distance_measures.word2vec import (
    Word2VecSemanticDistanceMeasure,
)

w2v = Word2VecSemanticDistanceMeasure()

# Load a pre-trained model (Google News, fastText, etc.)
w2v.load_model("GoogleNews-vectors-negative300.bin", binary=True)

# Compare raw strings
print(w2v.compare("king", "queen"))     # ~0.65
print(w2v.compare("cat", "automobile")) # ~0.10

# Compare Concept objects
print(w2v.compare_concepts(cat, dog))
```

---

### Example 5 — Dataset loading and correlation

```python
from semantic_decomposition_experiments.dataset.word_sim353_data_set import (
    WordSim353DataSet,
)
from semantic_decomposition_experiments.semantic_distance_measures.bdos import BDOS
from scipy.stats import spearmanr

pairs = WordSim353DataSet("wordsim353.csv").normalize()
bdos = BDOS()

predicted, ground_truth = [], []
for pair in pairs:
    c1 = Decomposition.decompose(Decomposition.create_concept(pair.string1, WordType.NN))
    c2 = Decomposition.decompose(Decomposition.create_concept(pair.string2, WordType.NN))
    predicted.append(bdos.compare(c1, c2))
    ground_truth.append(pair.true_result)

rho, p = spearmanr(predicted, ground_truth)
print(f"Spearman ρ = {rho:.3f}  (p = {p:.4f})")
```

---

### Example 6 — Genetic parameter optimisation

```python
from semantic_decomposition_experiments.parameter_learner.marker_passing_configuration_evaluator import (
    MarkerPassingConfigurationEvaluator,
    GeneticLearner,
)
from semantic_decomposition_experiments.dataset.word_sim353_data_set import (
    WordSim353DataSet,
)

examples = WordSim353DataSet("wordsim353.csv").normalize()

def my_evaluator(config, pairs):
    """Return accuracy on this fold (0–1)."""
    measure = MarkerPassingSemanticDistanceMeasure(config=config)
    scores = [measure.compare_concepts(
        Decomposition.create_concept(p.string1, WordType.NN),
        Decomposition.create_concept(p.string2, WordType.NN),
    ) for p in pairs]
    # ... compute correlation or accuracy
    return 0.5   # placeholder

evaluator = MarkerPassingConfigurationEvaluator(my_evaluator, n_folds=5)
learner = GeneticLearner(evaluator)
best_config = learner.learn(examples=examples, max_generations=200)

print("Best config:", best_config)
print("Best fitness:", learner.best_fitness)
```

---

### Example 7 — Word sense disambiguation (Senseval-3)

```python
from semantic_decomposition_experiments.word_sense_disambiguation.senseval_data import (
    SensevalData,
)

corpus = SensevalData.get_instance()
corpus.load("senseval3-en-lex.xml")

for sentence in corpus.sentences[:3]:
    print(sentence.id)
    for word in sentence.words:
        print(f"  {word.form}  sense={word.sense}")
```

---

### Example 8 — SciQ dataset inspection

```python
from semantic_decomposition_experiments.abductive_reasoning.sciq_dataset import (
    SciQChallengeDataSet,
)

dataset = SciQChallengeDataSet("train.json")
questions = dataset.load()

print(f"Loaded {len(questions)} questions")

q = questions[0]
print("Question:", q.question)
print("Support:", q.support)
print("Answers:", q.all_answers)
print("Correct:", q.correct_answer)
```

---

## API reference

### `PathMarkerPassing`

```python
algo = PathMarkerPassing(config=None)
algo.fill_nodes(concepts: List[Concept]) -> None
PathMarkerPassing.do_initial_marking(
    question_concepts: List[Concept],
    answer_concepts_per_slot: List[List[Concept]],
    algo: PathMarkerPassing,
    start_activation: float = 1.0,
) -> None
algo.execute() -> None
algo.get_all_inference_collisions() -> List[InferenceCollision]
algo.get_correct_answer_number() -> int    # v1: fewest abductions
algo.get_correct_answer_number_v7() -> int # v7: highest specificity
```

### `WinogradDoubleMarkerPassing`

```python
algo = WinogradDoubleMarkerPassing(
    termination_pulse_count=80,
    double_activation_limit=20,
)
algo.fill_nodes(dec_graph: SemanticNet, depth=1) -> None
algo.fill_syntax(syntax_edges: list) -> None
algo.fill_ner_edges(ner_edges: list) -> None
algo.fill_role_edges(role_edges: list) -> None
algo.set_negatives(concepts: List[Concept]) -> None
algo.do_initial_marking(concepts: List[Concept], start_activation=1.0) -> None
algo.execute() -> None
algo.get_double_activation() -> Dict[Concept, float]
```

### `BDOS`

```python
bdos = BDOS()
similarity: float = bdos.compare(c1: Concept, c2: Concept)  # 0.0–1.0
```

### `Word2VecSemanticDistanceMeasure`

```python
w2v = Word2VecSemanticDistanceMeasure()
w2v.load_model(path: str, binary: bool = True) -> None
similarity: float = w2v.compare(word1: str, word2: str)
similarity: float = w2v.compare_concepts(c1: Concept, c2: Concept)
```

### `InferenceCollision`

```python
col.get_answer_number() -> int
col.get_specificity() -> float
col.get_abductions() -> int
col.get_visited_nodes() -> int
col.get_question_concept() -> Concept
col.get_answer_concept() -> Concept
```

### `DataSet` (base for all loaders)

```python
dataset.read_example_data_set() -> List[SimilarityPair]
dataset.normalize() -> List[SimilarityPair]

pair.string1: str
pair.string2: str
pair.true_result: float   # normalised 0–1
```

---

## Related projects

- **MarkerPassingAlgorithm** — spreading-activation engine
- **SemanticDecomposition** — concept graph builder

---

## License

[GPLv3](https://www.gnu.org/licenses/gpl-3.0.html)

Contact: datenverlust@gmail.com
