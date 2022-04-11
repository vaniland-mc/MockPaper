package land.vani.mockpaper.inventory

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import org.bukkit.event.inventory.InventoryType

class InventoryViewTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var view: InventoryViewMock

    beforeEach {
        server = MockPaper.mock()
        view = InventoryViewMock(
            server.addPlayer(),
            InventoryMock.Crafting,
            server.createInventory(null, InventoryType.CHEST),
            InventoryType.CHEST
        )
    }

    afterEach {
        MockPaper.unmock()
    }

    context("constructor") {
        should("topInventory is crafting") {
            view.topInventory shouldBe InventoryMock.Crafting
        }
        should("bottomInventory type is chest") {
            view.bottomInventory.type shouldBe InventoryType.CHEST
        }
    }

    should("setTopInventory") {
        val topInventory = InventoryMock(null, InventoryType.CHEST)
        view.topInventory = topInventory

        view.topInventory shouldBe topInventory
    }

    should("setBottomInventory") {
        val bottomInventory = InventoryMock(null, InventoryType.CHEST)
        view.bottomInventory = bottomInventory

        view.bottomInventory shouldBe bottomInventory
    }

    should("getPlayer") {
        val player = server.addPlayer()
        view = InventoryViewMock(
            player,
            InventoryMock.Crafting,
            server.createInventory(null, InventoryType.CHEST),
            InventoryType.CHEST
        )

        view.player shouldBe player
    }

    should("setPlayer") {
        val player = server.addPlayer()
        view.player = player

        view.player shouldBe player
    }

    should("getType") {
        view.type shouldBe InventoryType.CHEST
    }

    should("setType") {
        view.type = InventoryType.CREATIVE

        view.type shouldBe InventoryType.CREATIVE
    }
})
