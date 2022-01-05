package land.vani.mockpaper.internal

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.chat.BaseComponent

internal fun Component.toLegacyString(legacyChar: Char = '§'): String =
    LegacyComponentSerializer.legacy(legacyChar).serialize(this)

internal fun String.toComponent(legacyChar: Char = '§'): Component =
    LegacyComponentSerializer.legacy(legacyChar).deserialize(this)

internal fun Component.toBungeeComponents(): Array<out BaseComponent> =
    BungeeComponentSerializer.get().serialize(this)

internal fun Array<out BaseComponent>.toComponent(): Component =
    BungeeComponentSerializer.get().deserialize(this)
