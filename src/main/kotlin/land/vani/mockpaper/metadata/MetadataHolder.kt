package land.vani.mockpaper.metadata

import org.bukkit.metadata.MetadataValue
import org.bukkit.metadata.Metadatable
import org.bukkit.plugin.Plugin

/**
 * A [Metadata] holder for delegating.
 */
class MetadataHolder : Metadatable {
    private val metadata = mutableMapOf<String, MutableMap<Plugin, MetadataValue>>()

    override fun setMetadata(metadataKey: String, newMetadataValue: MetadataValue) {
        val values: MutableMap<Plugin, MetadataValue> = metadata.getOrPut(metadataKey) { mutableMapOf() }
        values[newMetadataValue.owningPlugin!!] = newMetadataValue
    }

    override fun getMetadata(metadataKey: String): List<MetadataValue> =
        metadata[metadataKey]?.values
            .orEmpty()
            .toList()

    override fun hasMetadata(metadataKey: String): Boolean =
        metadataKey in metadata && !metadata[metadataKey].isNullOrEmpty()

    override fun removeMetadata(metadataKey: String, owningPlugin: Plugin) {
        if (metadataKey in metadata) {
            metadata[metadataKey]?.remove(owningPlugin)
        }
    }
}
