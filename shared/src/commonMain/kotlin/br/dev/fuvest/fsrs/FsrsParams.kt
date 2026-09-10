package br.dev.fuvest.fsrs

data class FsrsParams(
    val w: List<Double> = DEFAULT_W,
    val requestRetention: Double = 0.90,
    val maximumIntervalDays: Long = 365L * 5,
) {
    init {
        require(w.size == 19) { "FSRS-5 espera 19 pesos, recebeu ${w.size}" }
        require(requestRetention > 0.0 && requestRetention < 1.0) {
            "requestRetention precisa estar entre 0 e 1"
        }
    }

    companion object {
        val DEFAULT_W: List<Double> = listOf(
            0.40255, 1.18385, 3.173, 15.69105, 7.1949, 0.5345, 1.4604, 0.0046,
            1.54575, 0.1192, 1.01925, 1.9395, 0.11, 0.29605, 2.2698, 0.2315,
            2.9898, 0.51655, 0.6621,
        )
    }
}
