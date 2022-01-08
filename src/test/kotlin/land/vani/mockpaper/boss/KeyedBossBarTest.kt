package land.vani.mockpaper.boss

import com.google.common.collect.Iterators
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.ServerMock
import org.bukkit.NamespacedKey
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle

class KeyedBossBarTest : ShouldSpec({
    lateinit var server: ServerMock

    beforeEach {
        server = ServerMock()
    }

    should("keyedBossBar") {
        @Suppress("DEPRECATION")
        val key = NamespacedKey("mockpaper", "bossbar1")

        val bar = server.createBossBar(
            key,
            "Boss bar 1",
            BarColor.BLUE,
            BarStyle.SOLID
        )

        bar.title shouldBe "Boss bar 1"
        bar.color shouldBe BarColor.BLUE
        bar.style shouldBe BarStyle.SOLID
        bar.progress shouldBeExactly 1.0
        Iterators.size(server.bossBars) shouldBeExactly 1

        server.getBossBar(key) shouldBe bar
        server.bossBars.next() shouldBe bar

        server.removeBossBar(key) shouldBe true
        Iterators.size(server.bossBars) shouldBeExactly 0

        server.removeBossBar(key) shouldBe false
    }
})
