package land.vani.mockpaper.inventory.meta

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import org.bukkit.Color

class LeatherArmorMetaTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var meta: LeatherArmorMetaMock

    beforeEach {
        server = MockPaper.mock()
        meta = LeatherArmorMetaMock()
    }

    afterEach {
        MockPaper.unmock()
    }

    should("getColor is default") {
        meta.color shouldBe server.itemFactory.defaultLeatherColor
    }

    should("setColor") {
        meta.setColor(Color.MAROON)

        meta.color shouldBe Color.MAROON
    }

    should("reset color") {
        meta.setColor(Color.MAROON)
        meta.setColor(null)

        meta.color shouldBe server.itemFactory.defaultLeatherColor
    }

    should("clone") {
        val meta2 = meta.clone()

        meta shouldBe meta2
        meta2 shouldBe meta
    }
})
