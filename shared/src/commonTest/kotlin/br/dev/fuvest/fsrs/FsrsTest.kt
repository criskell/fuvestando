package br.dev.fuvest.fsrs

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FsrsTest {

    private val params = FsrsParams()

    private fun close(a: Double, b: Double, tol: Double = 1e-9) =
        assertTrue(abs(a - b) < tol, "esperava $b, veio $a")

    @Test
    fun `retrievability e 1 quando nao passou tempo`() {
        close(Fsrs.retrievability(0.0, 10.0), 1.0)
    }

    @Test
    fun `retrievability e 0_9 quando o tempo decorrido iguala a estabilidade`() {
        close(Fsrs.retrievability(10.0, 10.0), 0.9, tol = 1e-9)
        close(Fsrs.retrievability(1.0, 1.0), 0.9, tol = 1e-9)
    }

    @Test
    fun `retrievability so cai com o tempo`() {
        var previous = 1.1
        for (day in 0..200) {
            val r = Fsrs.retrievability(day.toDouble(), 20.0)
            assertTrue(r < previous, "R subiu no dia $day")
            assertTrue(r in 0.0..1.0)
            previous = r
        }
    }

    @Test
    fun `intervalo cresce junto com a estabilidade`() {
        val intervals = listOf(1.0, 5.0, 20.0, 100.0).map { Fsrs.intervalDays(it, params) }
        assertEquals(intervals.sortedBy { it }, intervals)
        assertTrue(intervals.all { it >= 1L })
    }

    @Test
    fun `intervalo respeita o teto`() {
        val capped = FsrsParams(maximumIntervalDays = 30)
        assertEquals(30L, Fsrs.intervalDays(100_000.0, capped))
    }

    @Test
    fun `estabilidade inicial segue a ordem das respostas`() {
        val s = Rating.entries.map { Fsrs.initialStability(it, params) }
        assertEquals(s.sortedBy { it }, s, "Fácil tem que dar estabilidade maior que De novo")
    }

    @Test
    fun `dificuldade inicial fica dentro de 1 a 10 e cai conforme a resposta melhora`() {
        val d = Rating.entries.map { Fsrs.initialDifficulty(it, params) }
        assertTrue(d.all { it in 1.0..10.0 })
        assertEquals(d.sortedByDescending { it }, d, "Fácil tem que ser menos difícil que De novo")
    }

    @Test
    fun `errar aumenta a dificuldade e acertar facil diminui`() {
        val start = 5.0
        assertTrue(Fsrs.nextDifficulty(start, Rating.Again, params) > start)
        assertTrue(Fsrs.nextDifficulty(start, Rating.Easy, params) < start)
    }

    @Test
    fun `dificuldade nunca escapa de 1 a 10`() {
        var d = 1.0
        repeat(50) { d = Fsrs.nextDifficulty(d, Rating.Easy, params) }
        assertTrue(d in 1.0..10.0, "escapou por baixo: $d")

        var hard = 10.0
        repeat(50) { hard = Fsrs.nextDifficulty(hard, Rating.Again, params) }
        assertTrue(hard in 1.0..10.0, "escapou por cima: $hard")
    }

    @Test
    fun `acertar sempre aumenta a estabilidade`() {
        val s = 10.0
        val r = Fsrs.retrievability(10.0, s)
        for (rating in listOf(Rating.Hard, Rating.Good, Rating.Easy)) {
            val next = Fsrs.nextStabilityOnRecall(s, 5.0, r, rating, params)
            assertTrue(next > s, "$rating não aumentou a estabilidade")
        }
    }

    @Test
    fun `facil ganha mais estabilidade que bom que dificil`() {
        val s = 10.0
        val r = Fsrs.retrievability(10.0, s)
        val hard = Fsrs.nextStabilityOnRecall(s, 5.0, r, Rating.Hard, params)
        val good = Fsrs.nextStabilityOnRecall(s, 5.0, r, Rating.Good, params)
        val easy = Fsrs.nextStabilityOnRecall(s, 5.0, r, Rating.Easy, params)
        assertTrue(hard < good, "Difícil deveria render menos que Bom")
        assertTrue(good < easy, "Bom deveria render menos que Fácil")
    }

    @Test
    fun `revisar mais tarde rende mais estabilidade`() {
        val s = 10.0
        val cedo = Fsrs.nextStabilityOnRecall(s, 5.0, Fsrs.retrievability(2.0, s), Rating.Good, params)
        val tarde = Fsrs.nextStabilityOnRecall(s, 5.0, Fsrs.retrievability(15.0, s), Rating.Good, params)
        assertTrue(tarde > cedo)
    }

    @Test
    fun `errar nunca aumenta a estabilidade`() {
        for (s in listOf(1.0, 10.0, 100.0, 1000.0)) {
            val r = Fsrs.retrievability(s, s)
            val next = Fsrs.nextStabilityOnLapse(s, 5.0, r, params)
            assertTrue(next <= s, "lapso subiu a estabilidade: $s -> $next")
            assertTrue(next > 0.0)
        }
    }

    @Test
    fun `primeira resposta cria estado do zero`() {
        val state = Fsrs.next(null, Rating.Good, elapsedDays = 0.0, params = params)
        close(state.stability, Fsrs.initialStability(Rating.Good, params))
        close(state.difficulty, Fsrs.initialDifficulty(Rating.Good, params))
    }

    @Test
    fun `sequencia de acertos leva o intervalo para meses`() {
        var state = Fsrs.next(null, Rating.Good, 0.0, params)
        repeat(8) {
            val dias = Fsrs.intervalDays(state.stability, params)
            state = Fsrs.next(state, Rating.Good, dias.toDouble(), params)
        }
        assertTrue(
            Fsrs.intervalDays(state.stability, params) > 60,
            "depois de 9 acertos o intervalo deveria passar de 2 meses",
        )
    }

    @Test
    fun `parametros invalidos sao rejeitados`() {
        val erro = runCatching { FsrsParams(w = listOf(1.0, 2.0)) }.exceptionOrNull()
        assertTrue(erro is IllegalArgumentException)
    }
}
