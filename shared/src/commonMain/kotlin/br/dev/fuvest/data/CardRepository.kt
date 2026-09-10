package br.dev.fuvest.data

import br.dev.fuvest.domain.Card
import br.dev.fuvest.domain.CardState
import br.dev.fuvest.domain.Scheduler
import br.dev.fuvest.fsrs.FsrsParams
import br.dev.fuvest.fsrs.Rating
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

private const val FILE_NAME = "cards.json"

class CardRepository(
    private val store: FileStore = createFileStore(),
    private val scheduler: Scheduler = Scheduler(FsrsParams(maximumIntervalDays = 120)),
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards.asStateFlow()

    init {
        load()
    }

    private fun load() {
        val raw = store.read(FILE_NAME)
        _cards.value = if (raw.isNullOrBlank()) {
            seed().also { persist(it) }
        } else {
            runCatching { json.decodeFromString<List<Card>>(raw) }.getOrElse { emptyList() }
        }
    }

    private fun persist(cards: List<Card>) {
        store.write(FILE_NAME, json.encodeToString(cards))
    }

    fun dueCards(now: Instant = Clock.System.now()): List<Card> =
        _cards.value.filter { it.isDue(now) }.sortedBy { it.due }

    fun decks(): List<String> = _cards.value.map { it.deck }.distinct().sorted()

    fun answer(card: Card, rating: Rating, now: Instant = Clock.System.now()) {
        val updated = scheduler.review(card, rating, now)
        replace(updated)
    }

    fun add(deck: String, front: String, back: String) {
        val now = Clock.System.now()
        val card = Card(
            id = newId(),
            deck = deck.trim().ifBlank { "Sem baralho" },
            front = front.trim(),
            back = back.trim(),
            state = CardState.New,
            due = now,
            updatedAt = now,
        )
        val next = _cards.value + card
        _cards.value = next
        persist(next)
    }

    fun delete(id: String) {
        val next = _cards.value.filterNot { it.id == id }
        _cards.value = next
        persist(next)
    }

    fun previewFor(card: Card, now: Instant = Clock.System.now()): Map<Rating, String> =
        scheduler.preview(card, now)

    private fun replace(card: Card) {
        val next = _cards.value.map { if (it.id == card.id) card else it }
        _cards.value = next
        persist(next)
    }

    private fun newId(): String =
        (Clock.System.now().toEpochMilliseconds().toString(36) +
            Random.nextInt(0, 1_000_000).toString(36))

    private fun seed(): List<Card> {
        val now = Clock.System.now()
        fun card(deck: String, front: String, back: String) = Card(
            id = newId(),
            deck = deck,
            front = front,
            back = back,
            due = now,
            updatedAt = now,
        )
        return listOf(
            card("Matemática", "Fórmula de Bhaskara", "x = (-b ± √(b² - 4ac)) / 2a"),
            card("Matemática", "sen²x + cos²x", "1"),
            card("Matemática", "Soma dos n primeiros termos de uma PA", "Sn = n(a₁ + aₙ)/2"),
            card("Física", "2ª lei de Newton", "F resultante = m · a"),
            card("Física", "Equação de Torricelli", "v² = v₀² + 2aΔs"),
            card("Literatura", "Autora de A Paixão Segundo G.H.", "Clarice Lispector, 1964"),
        )
    }
}
