package br.dev.fuvest.domain

import br.dev.fuvest.fsrs.MemoryState
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
enum class CardState {
    New,
    Relearning,
    Review,
}

@Serializable
data class Card(
    val id: String,
    val deck: String,
    val front: String,
    val back: String,
    val state: CardState = CardState.New,
    val stability: Double? = null,
    val difficulty: Double? = null,
    val due: Instant,
    val lastReview: Instant? = null,
    val reps: Int = 0,
    val lapses: Int = 0,
    val updatedAt: Instant,
) {
    val memory: MemoryState?
        get() = if (stability != null && difficulty != null) {
            MemoryState(stability, difficulty)
        } else {
            null
        }

    fun isDue(now: Instant): Boolean = due <= now
}
