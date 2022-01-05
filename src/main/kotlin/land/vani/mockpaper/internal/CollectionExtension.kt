package land.vani.mockpaper.internal

import java.util.Collections

@Suppress("UNCHECKED_CAST")
internal fun <C : Collection<T>, T> C.asUnmodifiable(): C =
    when (this) {
        is List<*> -> Collections.unmodifiableList(this) as C
        is Set<*> -> Collections.unmodifiableSet(this) as C
        else -> Collections.unmodifiableCollection(this) as C
    }

internal fun <K, V> Map<K, V>.asUnmodifiable(): Map<K, V> =
    Collections.unmodifiableMap(this)
