package land.vani.mockpaper.boss

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.player.PlayerMock
import land.vani.mockpaper.player.randomPlayerName
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarFlag
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar

class BossBarTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var bar: BossBar

    beforeEach {
        server = ServerMock()
        bar = server.createBossBar(
            "Test bossbar",
            BarColor.BLUE,
            BarStyle.SOLID,
            BarFlag.PLAY_BOSS_MUSIC,
            BarFlag.CREATE_FOG
        )
    }

    should("flags is correct") {
        bar.hasFlag(BarFlag.PLAY_BOSS_MUSIC) shouldBe true
        bar.hasFlag(BarFlag.CREATE_FOG) shouldBe true

        bar.hasFlag(BarFlag.DARKEN_SKY) shouldBe false
    }

    should("remove flag") {
        bar.removeFlag(BarFlag.CREATE_FOG)

        bar.hasFlag(BarFlag.CREATE_FOG) shouldBe false
    }

    should("progress") {
        bar.progress = 0.5
        bar.progress shouldBeExactly 0.5
    }

    should("title") {
        bar.title shouldBe "Test bossbar"

        bar.setTitle("Hello world")
        bar.title shouldBe "Hello world"
    }

    should("color") {
        bar.color shouldBe BarColor.BLUE

        bar.color = BarColor.PURPLE
        bar.color shouldBe BarColor.PURPLE
    }

    should("style") {
        bar.style shouldBe BarStyle.SOLID

        bar.style = BarStyle.SEGMENTED_10
        bar.style shouldBe BarStyle.SEGMENTED_10
    }

    should("players") {
        bar.players.size shouldBeExactly 0

        val player1 = PlayerMock(server, randomPlayerName())
        bar.addPlayer(player1)
        bar.players.size shouldBeExactly 1
        bar.players shouldContainExactly listOf(player1)

        val player2 = PlayerMock(server, randomPlayerName())
        bar.addPlayer(player2)
        bar.players.size shouldBeExactly 2
        bar.players shouldContainExactly listOf(player1, player2)

        bar.removePlayer(player1)
        bar.players.size shouldBeExactly 1
        bar.players shouldContainExactly listOf(player2)
    }
})
