package br.dev.fuvest.fsrs

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToLong

data class MemoryState(
    val stability: Double,
    val difficulty: Double,
)

object Fsrs {

    const val DECAY: Double = -0.5

    val FACTOR: Double = 0.9.pow(1.0 / DECAY) - 1.0

    private const val MIN_DIFFICULTY = 1.0
    private const val MAX_DIFFICULTY = 10.0
    private const val MIN_STABILITY = 0.01

    fun retrievability(elapsedDays: Double, stability: Double): Double {
        if (stability <= 0.0) return 0.0
        val t = max(elapsedDays, 0.0)
        return (1.0 + FACTOR * t / stability).pow(DECAY)
    }

    fun intervalDays(stability: Double, params: FsrsParams): Long {
        val raw = (stability / FACTOR) * (params.requestRetention.pow(1.0 / DECAY) - 1.0)
        return raw.roundToLong().coerceIn(1L, params.maximumIntervalDays)
    }

    fun initialStability(rating: Rating, params: FsrsParams): Double =
        max(params.w[rating.value - 1], MIN_STABILITY)

    fun initialDifficulty(rating: Rating, params: FsrsParams): Double {
        val d = params.w[4] - exp(params.w[5] * (rating.value - 1)) + 1.0
        return d.coerceIn(MIN_DIFFICULTY, MAX_DIFFICULTY)
    }

    fun nextDifficulty(difficulty: Double, rating: Rating, params: FsrsParams): Double {
        val delta = -params.w[6] * (rating.value - 3.0)
        val damped = difficulty + delta * ((10.0 - difficulty) / 9.0)
        val target = initialDifficulty(Rating.Easy, params)
        val reverted = params.w[7] * target + (1.0 - params.w[7]) * damped
        return reverted.coerceIn(MIN_DIFFICULTY, MAX_DIFFICULTY)
    }

    fun nextStabilityOnRecall(
        stability: Double,
        difficulty: Double,
        retrievability: Double,
        rating: Rating,
        params: FsrsParams,
    ): Double {
        val hardPenalty = if (rating == Rating.Hard) params.w[15] else 1.0
        val easyBonus = if (rating == Rating.Easy) params.w[16] else 1.0
        val growth = exp(params.w[8]) *
            (11.0 - difficulty) *
            stability.pow(-params.w[9]) *
            (exp(params.w[10] * (1.0 - retrievability)) - 1.0) *
            hardPenalty *
            easyBonus
        return max(stability * (1.0 + growth), MIN_STABILITY)
    }

    fun nextStabilityOnLapse(
        stability: Double,
        difficulty: Double,
        retrievability: Double,
        params: FsrsParams,
    ): Double {
        val sf = params.w[11] *
            difficulty.pow(-params.w[12]) *
            ((stability + 1.0).pow(params.w[13]) - 1.0) *
            exp(params.w[14] * (1.0 - retrievability))
        return min(max(sf, MIN_STABILITY), stability)
    }

    fun shortTermStability(stability: Double, rating: Rating, params: FsrsParams): Double {
        val s = stability * exp(params.w[17] * (rating.value - 3.0 + params.w[18]))
        return max(s, MIN_STABILITY)
    }

    fun next(
        current: MemoryState?,
        rating: Rating,
        elapsedDays: Double,
        params: FsrsParams = FsrsParams(),
    ): MemoryState {
        if (current == null) {
            return MemoryState(
                stability = initialStability(rating, params),
                difficulty = initialDifficulty(rating, params),
            )
        }

        val r = retrievability(elapsedDays, current.stability)
        val difficulty = nextDifficulty(current.difficulty, rating, params)

        val stability = when {
            elapsedDays < 1.0 -> shortTermStability(current.stability, rating, params)
            rating == Rating.Again -> nextStabilityOnLapse(current.stability, current.difficulty, r, params)
            else -> nextStabilityOnRecall(current.stability, current.difficulty, r, rating, params)
        }

        return MemoryState(stability = stability, difficulty = difficulty)
    }

    fun daysUntilRetention(stability: Double, targetRetention: Double): Double {
        require(targetRetention > 0.0 && targetRetention < 1.0)
        return (stability / FACTOR) * (exp(ln(targetRetention) / DECAY) - 1.0)
    }
}
