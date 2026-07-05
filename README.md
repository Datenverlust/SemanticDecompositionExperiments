# SemanticDecompositionExperiments

NLP experiments built on top of
[SemanticDecomposition](https://github.com/Datenverlust/SemanticDecomposition)
and [MarkerPassingAlgorithm](https://github.com/Datenverlust/MarkerPassingAlgorithm),
available in both **Java** (original) and **Python** (`python` branch).

The experiments cover a range of natural language understanding tasks:

| Module | Task |
|---|---|
| **AbductiveReasoning** | Science QA via abductive path-marker inference (SciQ) |
| **Reasoning** | Pronoun coreference resolution (Winograd Schema Challenge) |
| **AiChallenge** | Multiple-choice question answering (AI2 Science Questions) |
| **WordSenseDisambiguation** | WSD via typed marker passing (Senseval-3) |
| **SentenceSemanticSimilarityMeasure** | Sentence-level similarity (MSRvid) |
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

## Python package (`python` branch)

### Dependencies

| Package | Source | Role |
|---|---|---|
| `marker_passing` | [MarkerPassingAlgorithm](https://github.com/Datenverlust/MarkerPassingAlgorithm) `python` branch | Spreading-activation engine |
| `semantic_decomposition` | [SemanticDecomposition](https://github.com/Datenverlust/SemanticDecomposition) `python-rebuild` branch | Concept graph builder |
| `spacy` *(optional)* | `pip install spacy` | Lemmatisation / NLP pipeline |
| `gensim` *(optional)* | `pip install gensim` | Word2Vec similarity measure |

### Installation

```bash
# 1. MarkerPassingAlgorithm
git clone https://github.com/Datenverlust/MarkerPassingAlgorithm.git
cd MarkerPassingAlgorithm && git checkout python && pip install -e . && cd ..

# 2. SemanticDecomposition
git clone https://github.com/Datenverlust/SemanticDecomposition.git
cd SemanticDecomposition && git checkout python-rebuild && pip install -e . && cd ..

# 3. SemanticDecompositionExperiments
git clone https://github.com/Datenverlust/SemanticDecompositionExperiments.git
cd SemanticDecompositionExperiments && git checkout python
pip install -e .
```

### Package structure

```
semantic_decomposition_experiments/
├── __init__.py
│
├── dataset/                        # Benchmark dataset loaders
│   ├── data_set.py                 #   DataSet ABC
│   ├── similarity_pair.py          #   SimilarityPair / DataExample
│   ├── word_sim353_data_set.py     #   WordSim-353
│   ├── men_data_set.py             #   MEN
│   ├── msr_vid.py                  #   MSRvid (paraphrase pairs)
│   ├── rubenstein1965_dataset.py   #   Rubenstein & Goodenough 1965
│   ├── stanford_rare_word_similarity_dataset.py
│   └── mtruk_data_set.py           #   MTurk-771
│
├── semantic_distance_measures/
│   ├── bdos/bdos.py                # Bi-Directional One-Step algorithm
│   ├── word2vec/                   # Cosine similarity via gensim
│   └── elkb/                       # ELKB / Roget's Thesaurus (stub)
│
├── abductive_reasoning/            # Path-marker abductive inference
│   ├── path_marker_passing_config.py
│   ├── inference_path.py           # Link-type path history + specificity
│   ├── path_marker.py              # Immutable marker with path extension
│   ├── inference_collision.py      # Collision detection & ranking (7 strategies)
│   ├── path_node.py                # Node with per-concept activation + collision detection
│   ├── path_marker_passing.py      # Main algorithm + do_initial_marking()
│   ├── sciq_dataset.py             # SciQ JSON dataset loader
│   └── links/                      # Typed abductive link classes
│
├── reasoning/                      # Winograd Schema Challenge
│   ├── winograd_double_node.py     # Multi-graph node with negation support
│   ├── winograd_double_marker_passing.py  # 4-graph spreading activation
│   ├── winograd_schema_data.py     # Dataset loader
│   ├── edges/                      # NerEdge, RoleEdge, SyntaxEdge
│   ├── links/                      # NerLink, RoleLink, SyntaxLink
│   └── markers/                    # MarkerInformation
│
├── word_sense_disambiguation/
│   ├── senseval_data.py            # Senseval-3 XML corpus loader
│   └── typed_marker_config_evaluator.py
│
├── sentence_semantic_similarity_measure/
│   └── sentence_semantic_similarity_measure.py
│
├── sentence_sense_disambiguation/
│   └── sentence_sense_disambiguation.py
│
├── parameter_learner/
│   ├── parameter_learning_experiment.py        # Mutation operators
│   └── marker_passing_configuration_evaluator.py # N-fold CV + GeneticLearner
│
├── topic_similarity_document_retrieval/
│   ├── nyt_corpus_document.py      # NYT XML parser
│   └── topic_similarity_measure.py # Jaccard-based topic ranker
│
├── ai_challenge/
│   ├── question.py                 # Question + QuestionClass enum
│   ├── graph_cache.py              # Pre-built graph bundle per question
│   ├── evaluator.py                # Activation-score extraction
│   └── data_loader.py              # AI2 Science Questions CSV loader
│
└── semantic_emoji/
    ├── emoji_graph_builder.py      # Top-10 emojis → SemanticNet
    └── emoji_net_crawler.py        # emojitracker.com API client
```

---

## Usage examples

### Abductive science QA (SciQ)

```python
from semantic_decomposition import Decomposition, WordType
from semantic_decomposition_experiments.abductive_reasoning import (
    PathMarkerPassing, PathMarkerPassingConfig, SciQChallengeDataSet,
)

# Load a question
dataset = SciQChallengeDataSet("path/to/train.json")
question = dataset.load()[0]

# Decompose question and answer concepts
question_concepts = [Decomposition.decompose(
    Decomposition.create_concept(w, WordType.NN))
    for w in question.question.split()]
answer_concepts = [
    [Decomposition.decompose(Decomposition.create_concept(a, WordType.NN))]
    for a in question.all_answers
]

# Run abductive marker passing
algo = PathMarkerPassing()
all_concepts = question_concepts + [c for lst in answer_concepts for c in lst]
algo.fill_nodes(all_concepts)
PathMarkerPassing.do_initial_marking(question_concepts, answer_concepts, algo)
algo.execute()

print(f"Predicted answer index: {algo.get_correct_answer_number_v7()}")
```

### Semantic distance (BDOS)

```python
from semantic_decomposition import Decomposition, WordType
from semantic_decomposition_experiments.semantic_distance_measures.bdos import BDOS

Decomposition.init([...])  # pass your Dictionary backends
bdos = BDOS()

cat = Decomposition.decompose(Decomposition.create_concept("cat", WordType.NN))
dog = Decomposition.decompose(Decomposition.create_concept("dog", WordType.NN))

print(bdos.compare(cat, dog))   # 0.0 – 1.0
```

### Word2Vec distance

```python
from semantic_decomposition_experiments.semantic_distance_measures.word2vec import (
    Word2VecSemanticDistanceMeasure,
)

w2v = Word2VecSemanticDistanceMeasure()
w2v.load_model("GoogleNews-vectors-negative300.bin", binary=True)
print(w2v.compare("cat", "dog"))
```

### Dataset loading

```python
from semantic_decomposition_experiments.dataset import WordSim353DataSet

pairs = WordSim353DataSet("path/to/wordsim353.csv").normalize()
for pair in pairs:
    print(f"{pair.string1} – {pair.string2}: {pair.true_result}")
```

### Genetic parameter optimisation

```python
from semantic_decomposition_experiments.parameter_learner import (
    GeneticLearner, MarkerPassingConfigurationEvaluator,
)

def my_evaluator(config, examples):
    # run your experiment with this config, return accuracy 0–1
    return 0.5

evaluator = MarkerPassingConfigurationEvaluator(my_evaluator, n_folds=5)
learner = GeneticLearner(evaluator)
best_config = learner.learn(examples=my_dataset, max_generations=200)
print(best_config, "fitness:", learner.best_fitness)
```

---

## Key translation decisions (Java → Python)

| Java | Python |
|---|---|
| Anonymous inner classes (hooks) | Named private classes |
| JGraphT `Graph` | `semantic_decomposition.SemanticNet` |
| Guava `BiMap` | Two mirrored `dict`s |
| `StanfordCoreNLP` | Optional `spaCy` (lazy import) |
| `DeepLearning4j` Word2Vec | Optional `gensim` (lazy import) |
| Watchmaker GA framework | Simple mutation + `ThreadPoolExecutor` |
| `ExecutorService` | `concurrent.futures.ThreadPoolExecutor` |

---

## Java package (original)

### Requirements

- Java 8, Maven
- [SemanticDecomposition](https://github.com/Datenverlust/SemanticDecomposition) installed
- [dkpro-core](https://github.com/dkpro/dkpro-core) v2.5.0-SNAPSHOT

### Build

```bash
# Build dependencies first (see SemanticDecomposition README)
mvn clean install
```

---

## License

[GPLv3](https://www.gnu.org/licenses/gpl-3.0.html)

Contact: datenverlust@gmail.com
