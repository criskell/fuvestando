package br.dev.fuvest.roadmap

import kotlinx.datetime.LocalDate

data class Milestone(
    val date: LocalDate,
    val title: String,
    val detail: String,
    val critical: Boolean = false,
)

object Roadmap {

    val milestones: List<Milestone> = listOf(
        Milestone(
            date = LocalDate(2026, 10, 9),
            title = "Fecha a inscrição da Fuvest 2027",
            detail = "Declarar PPI escola pública, anexar o laudo para tempo adicional " +
                "e verificar isenção da taxa. Perder esta data apaga o resto.",
            critical = true,
        ),
        Milestone(
            date = LocalDate(2026, 11, 1),
            title = "1ª fase da Fuvest",
            detail = "80 questões, 5 horas. Todas as matérias com o mesmo peso.",
            critical = true,
        ),
        Milestone(
            date = LocalDate(2026, 12, 6),
            title = "2ª fase, dia 1",
            detail = "10 questões de Português e a redação. A redação vale metade da " +
                "nota do dia e um sexto da nota final.",
            critical = true,
        ),
        Milestone(
            date = LocalDate(2026, 12, 7),
            title = "2ª fase, dia 2",
            detail = "12 questões específicas da carreira, com resolução escrita.",
            critical = true,
        ),
    )

    val obras: List<String> = listOf(
        "Opúsculo Humanitário — Nísia Floresta",
        "Nebulosas — Narcisa Amália",
        "Memórias de Martha — Júlia Lopes de Almeida",
        "Caminho de Pedras — Rachel de Queiroz",
        "A Paixão Segundo G.H. — Clarice Lispector",
        "Geografia — Sophia de Mello Breyner Andresen",
        "Balada de Amor ao Vento — Paulina Chiziane",
        "Canção para Ninar Menino Grande — Conceição Evaristo",
        "A Visão das Plantas — Djaimilia Pereira de Almeida",
    )

    fun nextMilestone(today: LocalDate): Milestone? =
        milestones.firstOrNull { it.date >= today }
}
