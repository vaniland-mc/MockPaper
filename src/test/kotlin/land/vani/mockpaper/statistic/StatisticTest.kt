package land.vani.mockpaper.statistic

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.player.PlayerMock
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.EntityType

class StatisticTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var player: PlayerMock

    beforeTest {
        server = MockPaper.mock()
        player = server.addPlayer()
    }

    should("defaults") {
        player.getStatistic(Statistic.DEATHS) shouldBeExactly 0
        player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) shouldBeExactly 0
        player.getStatistic(Statistic.KILL_ENTITY, EntityType.SQUID) shouldBeExactly 0
    }

    should("get") {
        player.setStatistic(Statistic.DEATHS, 9)
        player.setStatistic(Statistic.MINE_BLOCK, Material.STONE, 2)
        player.setStatistic(Statistic.KILL_ENTITY, EntityType.SQUID, 8)

        player.getStatistic(Statistic.DEATHS) shouldBeExactly 9
        player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) shouldBeExactly 2
        player.getStatistic(Statistic.KILL_ENTITY, EntityType.SQUID) shouldBeExactly 8
    }

    should("increment") {
        player.setStatistic(Statistic.DEATHS, 400)
        player.setStatistic(Statistic.MINE_BLOCK, Material.STONE, 500)
        player.setStatistic(Statistic.KILL_ENTITY, EntityType.SQUID, 600)

        player.incrementStatistic(Statistic.DEATHS)
        player.incrementStatistic(Statistic.MINE_BLOCK, Material.STONE)
        player.incrementStatistic(Statistic.KILL_ENTITY, EntityType.SQUID)

        player.getStatistic(Statistic.DEATHS) shouldBeExactly 401
        player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) shouldBeExactly 501
        player.getStatistic(Statistic.KILL_ENTITY, EntityType.SQUID) shouldBeExactly 601

        player.incrementStatistic(Statistic.DEATHS, 10)
        player.incrementStatistic(Statistic.MINE_BLOCK, Material.STONE, 20)
        player.incrementStatistic(Statistic.KILL_ENTITY, EntityType.SQUID, 30)

        player.getStatistic(Statistic.DEATHS) shouldBeExactly 411
        player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) shouldBeExactly 521
        player.getStatistic(Statistic.KILL_ENTITY, EntityType.SQUID) shouldBeExactly 631
    }

    should("decrement") {
        player.setStatistic(Statistic.DEATHS, 411)
        player.setStatistic(Statistic.MINE_BLOCK, Material.STONE, 521)
        player.setStatistic(Statistic.KILL_ENTITY, EntityType.SQUID, 631)

        player.decrementStatistic(Statistic.DEATHS)
        player.decrementStatistic(Statistic.MINE_BLOCK, Material.STONE)
        player.decrementStatistic(Statistic.KILL_ENTITY, EntityType.SQUID)

        player.getStatistic(Statistic.DEATHS) shouldBeExactly 410
        player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) shouldBeExactly 520
        player.getStatistic(Statistic.KILL_ENTITY, EntityType.SQUID) shouldBeExactly 630

        player.decrementStatistic(Statistic.DEATHS, 10)
        player.decrementStatistic(Statistic.MINE_BLOCK, Material.STONE, 20)
        player.decrementStatistic(Statistic.KILL_ENTITY, EntityType.SQUID, 30)

        player.getStatistic(Statistic.DEATHS) shouldBeExactly 400
        player.getStatistic(Statistic.MINE_BLOCK, Material.STONE) shouldBeExactly 500
        player.getStatistic(Statistic.KILL_ENTITY, EntityType.SQUID) shouldBeExactly 600
    }

    should("negative increment") {
        player.setStatistic(Statistic.DEATHS, 7)

        shouldThrow<IllegalArgumentException> {
            player.incrementStatistic(Statistic.DEATHS, -1)
        }
        shouldThrow<IllegalArgumentException> {
            player.incrementStatistic(Statistic.DEATHS, 0)
        }
    }

    should("negative decrement") {
        player.setStatistic(Statistic.DEATHS, 7)

        shouldThrow<IllegalArgumentException> {
            player.decrementStatistic(Statistic.DEATHS, -1)
        }
        shouldThrow<IllegalArgumentException> {
            player.decrementStatistic(Statistic.DEATHS, 0)
        }
    }

    should("negative set") {
        player.setStatistic(Statistic.DEATHS, 5)

        shouldThrow<IllegalArgumentException> {
            player.setStatistic(Statistic.DEATHS, -1)
        }
        shouldNotThrow<IllegalArgumentException> {
            player.setStatistic(Statistic.DEATHS, 0)
        }
    }

    should("illegal type") {
        shouldThrow<IllegalArgumentException> {
            player.setStatistic(Statistic.DEATHS, Material.ACACIA_LOG, 5)
        }
        shouldThrow<IllegalArgumentException> {
            player.setStatistic(Statistic.DEATHS, EntityType.SQUID, 4)
        }
        shouldThrow<IllegalArgumentException> {
            player.setStatistic(Statistic.MINE_BLOCK, 10)
        }
        shouldThrow<IllegalArgumentException> {
            player.setStatistic(Statistic.MINE_BLOCK, EntityType.BAT, 10)
        }
    }
})
