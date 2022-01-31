package land.vani.mockpaper.inventory.meta

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import net.kyori.adventure.text.Component
import org.bukkit.inventory.meta.BookMeta

class BookMetaTest : ShouldSpec({
    lateinit var meta: BookMetaMock

    beforeTest {
        MockPaper.mock()
        meta = BookMetaMock()
    }

    should("constructor") {
        meta.author(Component.text("test author"))
        meta.title(Component.text("test title"))
        meta.generation = BookMeta.Generation.ORIGINAL
        meta.addPages(Component.text("test page"))

        val meta2 = BookMetaMock(meta)
        meta shouldBe meta2
        meta2 shouldBe meta
    }

    context("title") {
        should("hasTitle is default false") {
            meta.hasTitle() shouldBe false
        }

        should("title(Component)") {
            meta.title(Component.text("some title"))

            meta.hasTitle() shouldBe true
            meta.title() shouldBe Component.text("some title")
        }

        should("title(null)") {
            meta.title(Component.text("some title"))
            meta.title(null)

            meta.hasTitle() shouldBe false
        }

        should("setTitle(String)") {
            meta.title = "some title"

            meta.hasTitle() shouldBe true
            meta.title shouldBe "some title"
            meta.title() shouldBe Component.text("some title")
        }

        should("setTitle(null)") {
            meta.title = "some title"
            meta.title = null

            meta.hasTitle() shouldBe false
        }
    }

    context("author") {
        should("hasAuthor is default false") {
            meta.hasAuthor() shouldBe false
        }

        should("author(Component)") {
            meta.author(Component.text("some author"))

            meta.hasAuthor() shouldBe true
            meta.author() shouldBe Component.text("some author")
        }

        should("author(null)") {
            meta.author(Component.text("some author"))
            meta.author(null)

            meta.hasAuthor() shouldBe false
        }

        should("setAuthor(String)") {
            meta.author = "some author"

            meta.hasAuthor() shouldBe true
            meta.author shouldBe "some author"
            meta.author() shouldBe Component.text("some author")
        }

        should("setAuthor(null)") {
            meta.author = "some author"
            meta.author = null

            meta.hasAuthor() shouldBe false
        }
    }

    context("generation") {
        should("hasGeneration is default false") {
            meta.hasGeneration() shouldBe false
        }

        should("setGeneration(Generation)") {
            meta.generation = BookMeta.Generation.COPY_OF_ORIGINAL

            meta.hasGeneration() shouldBe true
            meta.generation shouldBe BookMeta.Generation.COPY_OF_ORIGINAL
        }

        should("setGeneration(null)") {
            meta.generation = BookMeta.Generation.COPY_OF_ORIGINAL
            meta.generation = null

            meta.hasGeneration() shouldBe false
        }
    }

    context("pages") {
        should("hasPages is default false") {
            meta.hasPages() shouldBe false
        }

        should("pages is default empty") {
            meta.pages().shouldBeEmpty()
        }

        should("page(Int)") {
            meta.addPages(Component.text("some page"))

            meta.page(1) shouldBe Component.text("some page")
            shouldThrow<IllegalArgumentException> {
                meta.page(2)
            }
        }

        should("page(Int, Component)") {
            meta.addPages(Component.text("some page"))
            meta.page(1, Component.text("other page"))

            meta.page(1) shouldBe Component.text("other page")
        }

        should("addPages(Component...)") {
            meta.addPages(
                Component.text("some page 1"),
                Component.text("some page 2"),
            )

            meta.hasPages() shouldBe true
            meta.pages() shouldHaveSize 2
            meta.pages() shouldContainExactly listOf(
                Component.text("some page 1"),
                Component.text("some page 2"),
            )
        }

        should("pages(List<Component>)") {
            meta.addPages(Component.text("default pages"))
            meta.pages(
                listOf(
                    Component.text("some page 1"),
                    Component.text("some page 2")
                )
            )

            meta.hasPages() shouldBe true
            meta.pages() shouldHaveSize 2
            meta.pages() shouldContainExactly listOf(
                Component.text("some page 1"),
                Component.text("some page 2"),
            )
        }

        should("getPage(Int)") {
            meta.addPages(Component.text("some content"))

            @Suppress("DEPRECATION")
            meta.getPage(1) shouldBe "some content"
        }

        should("setPage(Int, String)") {
            meta.addPages(Component.text("some content"))
            @Suppress("DEPRECATION")
            meta.setPage(1, "another content")

            @Suppress("DEPRECATION")
            meta.getPage(1) shouldBe "another content"
        }

        should("addPage(String...)") {
            @Suppress("DEPRECATION")
            meta.addPage(
                "some content 1",
                "some content 2",
            )

            meta.hasPages() shouldBe true
            meta.pages shouldHaveSize 2
            meta.pages shouldContainExactly listOf(
                "some content 1",
                "some content 2",
            )
            meta.pages() shouldContainExactly listOf(
                Component.text("some content 1"),
                Component.text("some content 2")
            )
        }
    }

    should("clone") {
        val meta2 = meta.clone()
        meta shouldBe meta2
        meta2 shouldBe meta
    }
})
