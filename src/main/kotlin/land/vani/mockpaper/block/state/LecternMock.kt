package land.vani.mockpaper.block.state

import land.vani.mockpaper.inventory.InventoryMock
import land.vani.mockpaper.inventory.LecternInventoryMock
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.Lectern
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import kotlin.math.max
import kotlin.math.min

/**
 * This [ContainerMock] represents a [Lectern].
 */
class LecternMock : ContainerMock, Lectern {
    constructor(block: Block? = null, material: Material) : super(block, material)

    constructor(block: Block) : super(block)

    constructor(state: LecternMock) : super(state)

    private var currentPage: Int = 0

    override fun createInventory(): InventoryMock = LecternInventoryMock(this)

    override fun getSnapshot(): BlockState = LecternMock(this)

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
