package land.vani.mockpaper.inventory

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import org.bukkit.event.inventory.InventoryType

class PlayerInventoryViewTest : ShouldSpec({
    lateinit var server: ServerMock

    beforeEach {
        server = MockPaper.mock()
    }

    afterEach {
        MockPaper.unmock()
    }

    should("constructor") {
        val player = server.addPlayer()
        val inventory = InventoryMock(null, 9, InventoryType.CHEST)

        val view = PlayerInventoryViewMock(player, inventory)

        view.player shouldBe player
        view.topInventory shouldBe inventory
        view.bottomInventory shouldBe player.inventory
    }
})
