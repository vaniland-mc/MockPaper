package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.internal.toComponent
import land.vani.mockpaper.internal.toLegacyString
import net.kyori.adventure.text.Component
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Sign

/**
 * This [TileStateMock] represents a [Sign].
 *
 * @author TheBusyBiscuit
 */
class SignMock(material: Material, block: Block? = null) : TileStateMock(material, block), Sign {
    private val lines: Array<String> = arrayOf("", "", "", "")

    constructor(block: Block) : this(block.type, block)

    constructor(state: SignMock) : this(
        state.material,
        if (state.isPlaced) state.block else null
    ) {
        repeat(4) {
            lines[it] = state.lines[it]
        }
    }

    override fun getSnapshot(): BlockStateMock = SignMock(this)

    override fun getColor(): DyeColor? {
        throw UnimplementedOperationException()
    }

    override fun setColor(color: DyeColor?) {
        throw UnimplementedOperationException()
    }

    override fun lines(): List<Component> = lines.map { it.toComponent() }

    override fun line(index: Int): Component = lines[index].toComponent()

    override fun line(index: Int, line: Component) {
        lines[index] = line.toLegacyString()
    }

    override fun getLines(): Array<String> = lines.clone()

    override fun getLine(index: Int): String = lines[index]

    override fun setLine(index: Int, line: String) {
        lines[index] = line
    }

    override fun isEditable(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setEditable(editable: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun isGlowingText(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setGlowingText(glowing: Boolean) {
        throw UnimplementedOperationException()
    }
}
