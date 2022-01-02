package land.vani.mockpaper

import land.vani.mockpaper.internal.toComponent
import land.vani.mockpaper.internal.toLegacyString
import net.kyori.adventure.text.Component
import org.bukkit.Nameable

/**
 * A [Nameable] holder for delegating.
 */
class NameableHolder : Nameable {
    private var customName: Component? = null

    override fun customName(): Component? = customName

    override fun customName(customName: Component?) {
        this.customName = customName
    }

    override fun getCustomName(): String? = customName?.toLegacyString()

    override fun setCustomName(name: String?) {
        customName = name?.toComponent()
    }
}
