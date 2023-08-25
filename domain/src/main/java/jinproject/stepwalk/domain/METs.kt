package jinproject.stepwalk.domain

enum class METs {
    Walk,
    FastWalk,
    StairWalk,
    Run;

    fun getMetsWeight() = when(this) {
        Walk -> 3.0f
        FastWalk -> 3.8f
        StairWalk -> 4.0f
        Run -> 8.0f
    }
}