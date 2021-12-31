package land.vani.mockpaper.persistence

import land.vani.mockpaper.internal.asUnmodifiable
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class PersistentDataContainerMock
@JvmOverloads
constructor(
    private val map: MutableMap<NamespacedKey, Any?> = mutableMapOf(),
) : PersistentDataContainer {
    private val context = PersistentDataAdapterContextMock()

    override fun hashCode(): Int {
        val hashCode = 3
        return hashCode + map.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is PersistentDataContainerMock) return false

        return map == other.map
    }

    override fun <T : Any, Z : Any> get(key: NamespacedKey, type: PersistentDataType<T, Z>): Z? {
        val value = map[key]
        if (value == null || !type.primitiveType.isInstance(value)) {
            return null
        }
        return type.fromPrimitive(type.primitiveType.cast(value), context)
    }

    override fun <T : Any, Z : Any> set(key: NamespacedKey, type: PersistentDataType<T, Z>, value: Z) {
        map[key] = type.toPrimitive(value, context)
    }

    override fun <T : Any, Z : Any> has(key: NamespacedKey, type: PersistentDataType<T, Z>): Boolean {
        val value = map[key]
        return value != null && type.primitiveType.isInstance(value)
    }

    override fun getAdapterContext(): PersistentDataAdapterContext = context

    override fun <T : Any, Z : Any> getOrDefault(
        key: NamespacedKey,
        type: PersistentDataType<T, Z>,
        defaultValue: Z,
    ): Z = get(key, type) ?: defaultValue

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun remove(key: NamespacedKey) {
        map.remove(key)
    }

    override fun getKeys(): Set<NamespacedKey> = map.keys.asUnmodifiable()

    fun serialize(): Map<String, Any?> = map.map { (key, value) ->
        key.toString() to value
    }.toMap()

    companion object {
        @JvmStatic
        fun deserialize(args: Map<String, Any?>): PersistentDataContainerMock {
            val map = mutableMapOf<NamespacedKey, Any?>()
            args.forEach { (key, value) ->
                map[NamespacedKey.fromString(key)!!] = value
            }
            return PersistentDataContainerMock(map)
        }
    }
}
