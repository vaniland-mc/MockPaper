package land.vani.mockpaper.inventory

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.inventory.meta.BookMetaMock
import land.vani.mockpaper.inventory.meta.EnchantedBookMetaMock
import land.vani.mockpaper.inventory.meta.FireworkEffectMetaMock
import land.vani.mockpaper.inventory.meta.FireworkMetaMock
import land.vani.mockpaper.inventory.meta.KnowledgeBookMetaMock
import land.vani.mockpaper.inventory.meta.LeatherArmorMetaMock
import land.vani.mockpaper.inventory.meta.PotionMetaMock
import land.vani.mockpaper.inventory.meta.SkullMetaMock
import land.vani.mockpaper.inventory.meta.SuspiciousStewMetaMock
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.hover.content.Content
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemFactory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.function.UnaryOperator
import kotlin.reflect.KClass

class ItemFactoryMock : ItemFactory {
    companion object {
        private val DEFAULT_LEATHER_COLOR: Color = Color.fromRGB(10511680)
    }

    private fun getItemMetaClass(material: Material): KClass<out ItemMeta> =
        when (material) {
            Material.WRITABLE_BOOK,
            Material.WRITTEN_BOOK,
            -> BookMetaMock::class
            Material.ENCHANTED_BOOK -> EnchantedBookMetaMock::class
            Material.KNOWLEDGE_BOOK -> KnowledgeBookMetaMock::class
            Material.LEATHER_BOOTS,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_HELMET,
            Material.LEATHER_LEGGINGS,
            -> LeatherArmorMetaMock::class
            Material.MAP -> throw UnimplementedOperationException()
            Material.FIREWORK_STAR -> FireworkEffectMetaMock::class
            Material.FIREWORK_ROCKET -> FireworkMetaMock::class
            Material.POTION,
            Material.LINGERING_POTION,
            Material.SPLASH_POTION,
            -> PotionMetaMock::class
            Material.PLAYER_HEAD -> SkullMetaMock::class
            Material.SUSPICIOUS_STEW -> SuspiciousStewMetaMock::class
            Material.TROPICAL_FISH_BUCKET -> throw UnimplementedOperationException()
            else -> ItemMeta::class
        }

    override fun getItemMeta(material: Material): ItemMeta =
        getItemMetaClass(material).java
            .getDeclaredConstructor()
            .newInstance()

    override fun isApplicable(meta: ItemMeta?, stack: ItemStack?): Boolean =
        isApplicable(meta, stack?.type)

    override fun isApplicable(meta: ItemMeta?, material: Material?): Boolean =
        material?.let { getItemMetaClass(it) }
            ?.isInstance(meta) == true

    override fun equals(meta1: ItemMeta?, meta2: ItemMeta?): Boolean = meta1 == meta2

    override fun asMetaFor(meta: ItemMeta, stack: ItemStack): ItemMeta? =
        asMetaFor(meta, stack.type)

    override fun asMetaFor(meta: ItemMeta, material: Material): ItemMeta {
        val target = getItemMetaClass(material).java
        val constructor = target.declaredConstructors
            .find {
                it.parameterCount == 1 &&
                    it.parameterTypes[0].isAssignableFrom(meta.javaClass)
            }
            ?: throw NoSuchMethodException(
                "Cannot find an ItemMeta constructor for the class \"${meta.javaClass.name}\""
            )

        return constructor.newInstance(meta) as ItemMeta
    }

    override fun getDefaultLeatherColor(): Color = DEFAULT_LEATHER_COLOR

    override fun updateMaterial(meta: ItemMeta, material: Material): Material = material

    override fun asHoverEvent(
        item: ItemStack,
        op: UnaryOperator<HoverEvent.ShowItem>,
    ): HoverEvent<HoverEvent.ShowItem> {
        throw UnimplementedOperationException()
    }

    override fun displayName(itemStack: ItemStack): Component {
        throw UnimplementedOperationException()
    }

    override fun getI18NDisplayName(item: ItemStack?): String? {
        throw UnimplementedOperationException()
    }

    override fun ensureServerConversions(item: ItemStack): ItemStack {
        throw UnimplementedOperationException()
    }

    override fun hoverContentOf(itemStack: ItemStack): Content {
        throw UnimplementedOperationException()
    }

    override fun hoverContentOf(entity: Entity): Content {
        throw UnimplementedOperationException()
    }

    override fun hoverContentOf(entity: Entity, customName: Array<out BaseComponent>): Content {
        throw UnimplementedOperationException()
    }

    override fun hoverContentOf(entity: Entity, customName: BaseComponent?): Content {
        throw UnimplementedOperationException()
    }

    override fun hoverContentOf(entity: Entity, customName: String?): Content {
        throw UnimplementedOperationException()
    }

    override fun getSpawnEgg(type: EntityType?): ItemStack? {
        throw UnimplementedOperationException()
    }
}
