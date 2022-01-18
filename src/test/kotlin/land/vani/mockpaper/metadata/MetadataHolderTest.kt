package land.vani.mockpaper.metadata

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.ServerMock
import org.bukkit.metadata.FixedMetadataValue

class MetadataHolderTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var meta: MetadataHolder

    beforeTest {
        server = ServerMock()
        meta = MetadataHolder()
    }

    should("setMetadata") {
        val plugin = server.pluginManager.createMockPlugin()
        meta.setMetadata(
            "someMetadata",
            FixedMetadataValue(plugin, "this is some data")
        )

        meta.hasMetadata("someMetadata") shouldBe true
        meta.getMetadata("someMetadata") shouldHaveSize 1
    }

    should("getMetadata with multiple metadata set by multiple plugins") {
        val plugin1 = server.pluginManager.createMockPlugin("plugin1")
        val plugin2 = server.pluginManager.createMockPlugin("plugin2")

        meta.setMetadata(
            "someMetadata",
            FixedMetadataValue(plugin1, "test1")
        )
        meta.setMetadata(
            "someMetadata",
            FixedMetadataValue(plugin1, "test2")
        )
        meta.setMetadata(
            "someMetadata",
            FixedMetadataValue(plugin2, "test3")
        )

        meta.hasMetadata("someMetadata") shouldBe true
        val metadata = meta.getMetadata("someMetadata")

        metadata shouldHaveSize 2
        metadata[0].owningPlugin shouldBe plugin1
        metadata[0].asString() shouldBe "test2"
        metadata[1].owningPlugin shouldBe plugin2
        metadata[1].asString() shouldBe "test3"
    }

    should("removeMetadata") {
        val plugin1 = server.pluginManager.createMockPlugin("plugin1")
        val plugin2 = server.pluginManager.createMockPlugin("plugin2")
        meta.setMetadata(
            "someMetadata",
            FixedMetadataValue(plugin1, "test1")
        )
        meta.setMetadata(
            "someMetadata",
            FixedMetadataValue(plugin2, "test2")
        )
        meta.removeMetadata("someMetadata", plugin1)

        meta.hasMetadata("someMetadata") shouldBe true
        val metadata = meta.getMetadata("someMetadata")
        metadata shouldHaveSize 1
        metadata[0].owningPlugin shouldBe plugin2
    }

    should("removeMetadata with none set") {
        val plugin = server.pluginManager.createMockPlugin()

        shouldNotThrowAny {
            meta.removeMetadata("someMetadata", plugin)
        }
    }
})
