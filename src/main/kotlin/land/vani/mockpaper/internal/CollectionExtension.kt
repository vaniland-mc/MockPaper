package land.vani.mockpaper.internal

import java.util.Collections

internal fun <T> List<T>.asUnmodifiable(): List<T> = Collections.unmodifiableList(this)

internal fun <T> Set<T>.asUnmodifiable(): Set<T> = Collections.unmodifiableSet(this)

internal fun <T> Collection<T>.asUnmodifiable(): Collection<T> = Collections.unmodifiableCollection(this)

internal fun <K, V> Map<K, V>.asUnmodifiable(): Map<K, V> =
    Collections.unmodifiableMap(this)

internal fun <T> MutableSet<T>.asSynchronized(): MutableSet<T> =
    Collections.synchronizedSet(this)
