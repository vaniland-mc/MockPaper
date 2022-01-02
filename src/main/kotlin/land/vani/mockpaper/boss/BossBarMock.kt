package land.vani.mockpaper.boss

import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player

class BossBarMock(
    private var title: String,
    private var color: BarColor,
    private var style: BarStyle,
    vararg flags: BarFlag,
) : BossBar {
    private val flags: MutableList<BarFlag> = flags.toMutableList()
    private var progress: Double = 1.0
    private val players = mutableSetOf<Player>()
    private var isVisible: Boolean = true

    override fun getTitle(): String = title

    override fun setTitle(title: String?) {
        this.title = title ?: ""
    }

    override fun getColor(): BarColor = color

    override fun setColor(color: BarColor) {
        this.color = color
    }

    override fun getStyle(): BarStyle = style

    override fun setStyle(style: BarStyle) {
        this.style = style
    }

    override fun addFlag(flag: BarFlag) {
        flags += flag
    }

    override fun removeFlag(flag: BarFlag) {
        flags -= flag
    }

    override fun hasFlag(flag: BarFlag): Boolean = flag in flags

    override fun getProgress(): Double = progress

    override fun setProgress(progress: Double) {
        require(progress in 0.0..1.0) { "Progress must be in 0.0..1.0" }
        this.progress = progress
    }

    override fun addPlayer(player: Player) {
        players += player
    }

    override fun removePlayer(player: Player) {
        players -= player
    }

    override fun removeAll() {
        players.clear()
    }

    override fun getPlayers(): List<Player> = players.toList()

    override fun isVisible(): Boolean = isVisible

    override fun setVisible(visible: Boolean) {
        isVisible = visible
    }

    override fun show() {
        isVisible = true
    }

    override fun hide() {
        isVisible = false
    }
}
