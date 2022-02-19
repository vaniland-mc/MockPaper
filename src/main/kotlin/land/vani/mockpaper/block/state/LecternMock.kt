package land.vani.mockpaper.block.state

import land.vani.mockpaper.inventory.InventoryMock
import land.vani.mockpaper.inventory.LecternInventoryMock
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Lectern
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import kotlin.math.max
import kotlin.math.min

/**
 * This [ContainerMock] represents a [Lectern].
 */
class LecternMock(material: Material, block: Block? = null) : ContainerMock(material, block), Lectern {
    constructor(block: Block) : this(block.type, block)

    constructor(state: LecternMock) : this(
        state.material,
        if (state.isPlaced) {
            state.block
        } else {
            null
        }
    )

    private var currentPage: Int = 0

    override fun createInventory(): InventoryMock = LecternInventoryMock(this)

    override fun getSnapshot(): BlockStateMock = LecternMock(this)

    override fun getPage(): Int = currentPage

    override fun setPage(page: Int) {
        val book = inventory.getItem(0)
        val maxPages = getMaxPages(book)

        currentPage = min(max(0, page), maxPages - 1)
    }

    private fun getMaxPages(book: ItemStack?): Int {
        if (book == null || !book.hasItemMeta()) return 1

        val meta = book.itemMeta
        return if (meta !is BookMeta) 1
        else meta.pageCount
    }
}
