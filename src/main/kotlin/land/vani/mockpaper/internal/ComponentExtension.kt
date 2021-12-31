package land.vani.mockpaper.internal

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

internal fun Component.toLegacyString(legacyChar: Char = '§'): String =
    LegacyComponentSerializer.legacy(legacyChar).serialize(this)

internal fun String.toComponent(legacyChar: Char = '§'): Component =
    LegacyComponentSerializer.legacy(legacyChar).deserialize(this)
