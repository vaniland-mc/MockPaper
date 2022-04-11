package land.vani.mockpaper.block.state

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.block.BlockMock
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

class LecternTest : ShouldSpec({
    lateinit var lectern: LecternMock

    beforeEach {
        MockPaper.mock()
        lectern = LecternMock(Material.LECTERN)
    }

    afterEach {
        MockPaper.unmock()
    }

    should("setPage is valid") {
        val book = ItemStack(Material.WRITABLE_BOOK)
        val meta = book.itemMeta as BookMeta

        meta.addPages(
            Component.text("ABC"),
            Component.text("DEF"),
            Component.text("GHI"),
            Component.text("JKL"),
        )
        book.itemMeta = meta
        lectern.inventory.addItem(book)

        lectern.page = 2

        lectern.page shouldBeExactly 2
    }

    should("setPage invalid") {
        val book = ItemStack(Material.WRITABLE_BOOK)
        val meta = book.itemMeta as BookMeta

        meta.addPages(
            Component.text("ABC"),
            Component.text("DEF"),
            Component.text("GHI"),
            Component.text("JKL"),
        )

        book.itemMeta = meta
        lectern.inventory.addItem(book)

        lectern.page = -1
        lectern.page shouldBeExactly 0

        lectern.page = 5
        lectern.page shouldBeExactly 3
    }

    should("inventory type is LECTERN") {
        lectern.inventory.type shouldBe InventoryType.LECTERN
        lectern.inventory.holder shouldBe lectern
    }

    should("material block state") {
        val block = BlockMock(Material.LECTERN)

        block.state.shouldBeInstanceOf<LecternMock>()
    }
})
