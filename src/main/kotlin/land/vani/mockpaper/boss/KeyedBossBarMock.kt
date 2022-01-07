package land.vani.mockpaper.boss

import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.KeyedBossBar

class KeyedBossBarMock(
    private val key: NamespacedKey,
    title: String?,
    color: BarColor,
    style: BarStyle,
    vararg flags: BarFlag,
) : BossBarMock(title, color, style, *flags), KeyedBossBar {
    override fun getKey(): NamespacedKey = key
}
