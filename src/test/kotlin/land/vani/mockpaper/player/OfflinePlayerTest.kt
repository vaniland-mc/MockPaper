package land.vani.mockpaper.player

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import java.util.UUID

class OfflinePlayerTest : ShouldSpec({
    lateinit var server: ServerMock

    beforeEach {
        server = MockPaper.mock()
    }

    afterEach {
        MockPaper.unmock()
    }

    should("isOnline") {
        val player = OfflinePlayerMock(server, randomPlayerName())
        player.isOnline shouldBe false
    }

    should("name") {
        val name = randomPlayerName()
        val player = OfflinePlayerMock(server, name)
        player.name shouldBe name
    }

    should("uniqueId") {
        val uuid = UUID.randomUUID()
        val player = OfflinePlayerMock(server, randomPlayerName(), uuid)
        player.uniqueId shouldBe uuid
    }

    should("player") {
        val uuid = UUID.randomUUID()
        val name = randomPlayerName()
        val offlinePlayer = OfflinePlayerMock(server, name, uuid)
        val joinedPlayer = offlinePlayer.join(server)
        val player = offlinePlayer.player

        player.shouldNotBeNull()
        joinedPlayer shouldBe player
        player.name shouldBe offlinePlayer.name
        player.uniqueId shouldBe offlinePlayer.uniqueId
    }
})
