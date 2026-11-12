package nl.hexmaster.finalapproach.ui.components

/** The thousands separator of a shown score: U+2009 THIN SPACE. */
const val SCORE_THOUSANDS_SEPARATOR = '\u2009'

private const val SCORE_GROUP_SIZE = 3

/** [value] as the Score Counter shows it: thousands grouped with a thin space, `12 450`. */
fun formatScore(value: Int): String {
    val digits = if (value < 0) (-value.toLong()).toString() else value.toString()
    val grouped = digits.reversed().chunked(SCORE_GROUP_SIZE).joinToString(SCORE_THOUSANDS_SEPARATOR.toString()).reversed()
    return if (value < 0) "-$grouped" else grouped
}
