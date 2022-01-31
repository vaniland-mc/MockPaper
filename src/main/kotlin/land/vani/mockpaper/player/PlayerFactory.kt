package land.vani.mockpaper.player

import kotlin.random.Random
import kotlin.random.nextInt

private val ALLOWED_CHARS = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '_'

fun randomPlayerName(): String {
    val length = Random.nextInt(3..16)
    return buildString {
        repeat(length) {
            append(ALLOWED_CHARS.random())
        }
    }
}
