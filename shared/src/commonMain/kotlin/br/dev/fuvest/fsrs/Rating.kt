package br.dev.fuvest.fsrs

enum class Rating(val value: Int) {
    Again(1),
    Hard(2),
    Good(3),
    Easy(4);

    val label: String
        get() = when (this) {
            Again -> "De novo"
            Hard -> "Difícil"
            Good -> "Bom"
            Easy -> "Fácil"
        }
}
