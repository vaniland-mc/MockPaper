package land.vani.mockpaper.persistence

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.inventory.meta.ItemMetaMock
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType

@Suppress("DEPRECATION")
private fun randomKey(): NamespacedKey = NamespacedKey.randomKey()

class PersistentDataTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var container: PersistentDataContainerMock

    beforeTest {
        server = ServerMock()
        container = PersistentDataContainerMock()
    }

    should("adapterContext") {
        val context = PersistentDataAdapterContextMock()
        context.newPersistentDataContainer()
            .shouldBeInstanceOf<PersistentDataContainerMock>()
    }

    context("mock implementations") {
        should("ItemMeta") {
            val meta = ItemMetaMock()
            meta.persistentDataContainer
                .shouldBeInstanceOf<PersistentDataContainerMock>()
        }

        should("Player") {
            val player = server.addPlayer()
            player.persistentDataContainer
                .shouldBeInstanceOf<PersistentDataContainerMock>()
        }
    }

    should("isEmpty is default true") {
        container.isEmpty shouldBe true
    }

    should("getAdapterContext is PersistentDataAdapterContextMock") {
        container.adapterContext
            .shouldBeInstanceOf<PersistentDataAdapterContextMock>()
    }

    context("add") {
        should("integer") {
            val key = randomKey()
            container[key, PersistentDataType.INTEGER] = 100

            container.isEmpty shouldBe false
            container.has(key, PersistentDataType.INTEGER) shouldBe true
            container[key, PersistentDataType.INTEGER] shouldBe 100
        }

        should("string") {
            val key = randomKey()
            container[key, PersistentDataType.STRING] = "some text"

            container.isEmpty shouldBe false
            container.has(key, PersistentDataType.STRING) shouldBe true
            container[key, PersistentDataType.STRING] shouldBe "some text"
        }
    }

    context("remove") {
        should("integer") {
            val key = randomKey()
            container[key, PersistentDataType.INTEGER] = 100
            container.remove(key)

            container.has(key, PersistentDataType.INTEGER) shouldBe false
            container.isEmpty shouldBe true
        }

        should("string") {
            val key = randomKey()
            container[key, PersistentDataType.STRING] = "some text"
            container.remove(key)

            container.isEmpty shouldBe true
            container.has(key, PersistentDataType.STRING) shouldBe false
        }
    }

    should("getOrDefault") {
        val key = randomKey()

        container.getOrDefault(key, PersistentDataType.INTEGER, 10) shouldBe 10

        container[key, PersistentDataType.INTEGER] = 42
        container.getOrDefault(key, PersistentDataType.INTEGER, 10) shouldBe 42
    }

    should("keys") {
        val key1 = randomKey()
        val key2 = randomKey()

        container.keys shouldHaveSize 0

        container[key1, PersistentDataType.STRING] = "test1"
        container[key2, PersistentDataType.STRING] = "test2"

        container.keys shouldHaveSize 2
        container.keys shouldContainExactly setOf(key1, key2)
    }
})
