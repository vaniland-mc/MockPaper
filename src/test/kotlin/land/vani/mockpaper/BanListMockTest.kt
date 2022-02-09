package land.vani.mockpaper

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.util.Date

class BanListMockTest : ShouldSpec({
    lateinit var banList: BanListMock

    beforeTest {
        banList = BanListMock()
    }

    should("addBan") {
        val date = Date()
        val entry = banList.addBan("target", "reason", date, "source")

        entry shouldBe BanListMock.BanEntryMock("target", date, "reason", "source")
    }

    should("default not not banned") {
        banList.isBanned("target") shouldBe false
    }

    should("ban, banned is true") {
        banList.addBan("target", "reason", Date(), "source")

        banList.isBanned("target") shouldBe true
    }

    should("ban and pardon, banned is false") {
        banList.addBan("target", "reason", Date(), "source")
        banList.pardon("target")

        banList.isBanned("target") shouldBe false
    }

    context("banEntries") {
        should("default empty") {
            banList.banEntries.shouldBeEmpty()
        }

        should("one banned") {
            val entry = banList.addBan("target", "reason", Date(), "source")

            banList.banEntries shouldContainExactly setOf(entry)
        }
    }

    context("getBanEntry") {
        should("not banned") {
            val entry = banList.getBanEntry("target")

            entry.shouldBeNull()
        }

        should("banned") {
            val date = Date()
            val entry1 = banList.addBan("target", "reason", date, "source")
            val entry2 = banList.getBanEntry("target")

            entry2.shouldNotBeNull()
            entry2 shouldBe entry1
        }
    }
})
