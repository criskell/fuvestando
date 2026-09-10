package br.dev.fuvest.domain

import br.dev.fuvest.fsrs.Fsrs
import br.dev.fuvest.fsrs.FsrsParams
import br.dev.fuvest.fsrs.Rating
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.Instant

class Scheduler(private val params: FsrsParams = FsrsParams()) {

    fun review(card: Card, rating: Rating, now: Instant): Card {
        val elapsedDays = card.lastReview
            ?.let { (now - it).inWholeSeconds / 86_400.0 }
            ?.coerceAtLeast(0.0)
            ?: 0.0

        val memory = Fsrs.next(card.memory, rating, elapsedDays, params)

        val relearning = rating == Rating.Again
        val due = if (relearning) {
            now + 10.minutes
        } else {
            now + Fsrs.intervalDays(memory.stability, params).days
        }

        return card.copy(
            state = if (relearning) CardState.Relearning else CardState.Review,
            stability = memory.stability,
            difficulty = memory.difficulty,
            due = due,
            lastReview = now,
            reps = card.reps + 1,
            lapses = card.lapses + if (relearning && card.state == CardState.Review) 1 else 0,
            updatedAt = now,
        )
    }

    fun preview(card: Card, now: Instant): Map<Rating, String> {
        return Rating.entries.associateWith { rating ->
            val next = review(card, rating, now)
            val minutes = (next.due - now).inWholeMinutes
            when {
                minutes < 60 -> "${minutes}min"
                minutes < 60 * 36 -> "${minutes / 60}h"
                minutes < 60 * 24 * 60 -> "${minutes / (60 * 24)}d"
                else -> "${minutes / (60 * 24 * 30)}m"
            }
        }
    }
}
