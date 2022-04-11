package land.vani.mockpaper.inventory.meta

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper

@Suppress("DEPRECATION")
class SkullMetaTest : ShouldSpec({
    lateinit var meta: SkullMetaMock

    beforeEach {
        MockPaper.mock()
        meta = SkullMetaMock()
    }

    afterEach {
        MockPaper.unmock()
    }

    context("owner") {
        should("hasOwner is default false") {
            meta.hasOwner() shouldBe false
        }

        should("set owner") {
            meta.owner = "somePlayer"

            meta.hasOwner() shouldBe true
            meta.owner shouldBe "somePlayer"
        }
    }

    should("clone") {
        val meta2 = meta.clone()

        meta shouldBe meta2
        meta2 shouldBe meta
    }
})
