@file:Suppress("DEPRECATION")

package land.vani.mockpaper

import com.google.common.collect.Multimap
import io.papermc.paper.inventory.ItemRarity
import net.kyori.adventure.text.flattener.ComponentFlattener
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.UnsafeValues
import org.bukkit.World
import org.bukkit.advancement.Advancement
import org.bukkit.attribute.Attributable
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.plugin.InvalidPluginException
import org.bukkit.plugin.PluginDescriptionFile

class UnsafeValuesMock : UnsafeValues {
    companion object {
        private val COMPATIBLE_API_VERSIONS = setOf("1.13", "1.14", "1.15", "1.16", "1.17", "1.18")
    }

    override fun componentFlattener(): ComponentFlattener =
        ComponentFlattener.basic()

    @Suppress("UnstableApiUsage")
    override fun plainComponentSerializer(): PlainComponentSerializer =
        PlainComponentSerializer.plain()

    override fun plainTextSerializer(): PlainTextComponentSerializer =
        PlainTextComponentSerializer.plainText()

    override fun gsonComponentSerializer(): GsonComponentSerializer =
        GsonComponentSerializer.gson()

    override fun colorDownsamplingGsonComponentSerializer(): GsonComponentSerializer =
        GsonComponentSerializer.colorDownsamplingGson()

    override fun legacyComponentSerializer(): LegacyComponentSerializer =
        LegacyComponentSerializer.legacySection()

    override fun reportTimings() {
        throw UnimplementedOperationException()
    }

    override fun toLegacy(material: Material?): Material {
        if (material?.isLegacy == true) {
            return material
        } else {
            throw UnimplementedOperationException()
        }
    }

    override fun fromLegacy(material: Material?): Material = material!!

    override fun fromLegacy(material: MaterialData?): Material {
        throw UnimplementedOperationException()
    }

    override fun fromLegacy(material: MaterialData?, itemPriority: Boolean): Material {
        throw UnimplementedOperationException()
    }

    override fun fromLegacy(material: Material?, data: Byte): BlockData {
        throw UnimplementedOperationException()
    }

    override fun getMaterial(material: String?, version: Int): Material {
        throw UnimplementedOperationException()
    }

    override fun getDataVersion(): Int = 1

    override fun modifyItemStack(stack: ItemStack?, arguments: String?): ItemStack {
        throw UnimplementedOperationException()
    }

    override fun checkSupported(pdf: PluginDescriptionFile) {
        if (pdf.apiVersion == null) {
            throw InvalidPluginException("Plugin does not specify 'api-version' in plugin.yml")
        }
        if (pdf.apiVersion !in COMPATIBLE_API_VERSIONS) {
            throw InvalidPluginException(
                "Plugin api version ${pdf.apiVersion} is incompatible with the current MockPaper version"
            )
        }
    }

    override fun processClass(pdf: PluginDescriptionFile?, path: String?, clazz: ByteArray): ByteArray =
        clazz

    override fun loadAdvancement(key: NamespacedKey?, advancement: String?): Advancement {
        throw UnimplementedOperationException()
    }

    override fun removeAdvancement(key: NamespacedKey?): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getDefaultAttributeModifiers(
        material: Material?,
        slot: EquipmentSlot?,
    ): Multimap<Attribute, AttributeModifier> {
        throw UnimplementedOperationException()
    }

    override fun getTimingsServerName(): String = "MockPaper"

    override fun isSupportedApiVersion(apiVersion: String?): Boolean = apiVersion in COMPATIBLE_API_VERSIONS

    override fun serializeItem(item: ItemStack?): ByteArray {
        throw UnimplementedOperationException()
    }

    override fun deserializeItem(data: ByteArray?): ItemStack {
        throw UnimplementedOperationException()
    }

    override fun serializeEntity(entity: Entity?): ByteArray {
        throw UnimplementedOperationException()
    }

    override fun deserializeEntity(data: ByteArray?, world: World?, preserveUUID: Boolean): Entity {
        throw UnimplementedOperationException()
    }

    override fun getTranslationKey(mat: Material?): String {
        throw UnimplementedOperationException()
    }

    override fun getTranslationKey(block: Block?): String {
        throw UnimplementedOperationException()
    }

    override fun getTranslationKey(type: EntityType?): String {
        throw UnimplementedOperationException()
    }

    override fun getTranslationKey(itemStack: ItemStack?): String {
        throw UnimplementedOperationException()
    }

    override fun nextEntityId(): Int {
        throw UnimplementedOperationException()
    }

    override fun getItemRarity(material: Material?): ItemRarity {
        throw UnimplementedOperationException()
    }

    override fun getItemStackRarity(itemStack: ItemStack?): ItemRarity {
        throw UnimplementedOperationException()
    }

    override fun isValidRepairItemStack(itemToBeRepaired: ItemStack, repairMaterial: ItemStack): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getItemAttributes(
        material: Material,
        equipmentSlot: EquipmentSlot,
    ): Multimap<Attribute, AttributeModifier> {
        throw UnimplementedOperationException()
    }

    override fun getProtocolVersion(): Int {
        throw UnimplementedOperationException()
    }

    override fun hasDefaultEntityAttributes(entityKey: NamespacedKey): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getDefaultEntityAttributes(entityKey: NamespacedKey): Attributable {
        throw UnimplementedOperationException()
    }

    override fun isCollidable(material: Material): Boolean {
        throw UnimplementedOperationException()
    }
}
