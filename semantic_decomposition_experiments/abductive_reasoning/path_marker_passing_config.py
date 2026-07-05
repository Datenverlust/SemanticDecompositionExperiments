class PathMarkerPassingConfig:
    """Static configuration for PathMarkerPassing."""

    decomposition_depth: int = 2
    n_abduction_value: int = 2
    n_max_amount_of_questions: int = 4
    b_abductive_inference: bool = True
    termination_puls_count: int = 80

    # Link weights
    d_hypernym: float = -0.02
    d_synonym: float = 0.62
    d_meronym: float = 0.5
    d_definition: float = -0.78
    d_antonym: float = -0.9
    d_hyponym: float = 0.79
    d_arbitrary: float = 0.0

    threshold: float = 0.064
    start_activation: float = 1.0
