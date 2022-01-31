package land.vani.mockpaper.inventory.meta

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.internal.toComponent
import land.vani.mockpaper.internal.toLegacyString
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import org.bukkit.inventory.meta.BookMeta
import java.util.Objects

class BookMetaMock : ItemMetaMock, BookMeta {
    private var title: Component? = null
    private var author: Component? = null
    private var generation: BookMeta.Generation? = null
    private var pages: MutableList<Component> = mutableListOf()

    constructor() : super()

    constructor(meta: BookMeta) : super(meta) {
        title = meta.title()
        author = meta.author()
        generation = meta.generation
        pages = meta.pages().toMutableList()
    }

    override fun hasTitle(): Boolean = title != null

    override fun getTitle(): String? = title?.toLegacyString()

    override fun setTitle(title: String?): Boolean {
        this.title = title?.toComponent()
        return true
    }

    override fun hasAuthor(): Boolean = author != null

    override fun getAuthor(): String? = author?.toLegacyString()

    override fun setAuthor(author: String?) {
        this.author = author?.toComponent()
    }

    override fun hasGeneration(): Boolean = generation != null

    override fun getGeneration(): BookMeta.Generation? = generation

    override fun setGeneration(generation: BookMeta.Generation?) {
        this.generation = generation
    }

    override fun hasPages(): Boolean = pages.isNotEmpty()

    override fun title(): Component = title ?: Component.empty()

    override fun title(title: Component?): BookMeta = apply {
        this.title = title
    }

    override fun author(): Component = author ?: Component.empty()

    override fun author(author: Component?): BookMeta = apply {
        this.author = author
    }

    private fun validatePageNumber(page: Int) {
        require(page in 1..this.pages.size) {
            "Invalid page number $page / ${this.pages.size}"
        }
    }

    override fun page(page: Int): Component {
        validatePageNumber(page)
        return pages[page - 1]
    }

    override fun page(page: Int, data: Component) {
        validatePageNumber(page)
        pages[page - 1] = data
    }

    override fun addPages(vararg pages: Component) {
        this.pages += pages
    }

    override fun pages(): List<Component> = pages.toList()

    override fun pages(pages: List<Component>): Book = apply {
        this.pages = pages.toMutableList()
    }

    override fun toBuilder(): BookMeta.BookMetaBuilder {
        throw UnimplementedOperationException()
    }

    override fun getPage(page: Int): String = page(page).toLegacyString()

    override fun setPage(page: Int, data: String) {
        page(page, data.toComponent())
    }

    override fun getPages(): List<String> = pages.map { it.toLegacyString() }

    override fun setPages(pages: List<String>) {
        pages(pages.map { it.toComponent() })
    }

    override fun setPages(vararg pages: String) {
        pages(pages.map { it.toComponent() })
    }

    override fun addPage(vararg pages: String) {
        addPages(*pages.map { it.toComponent() }.toTypedArray())
    }

    override fun getPageCount(): Int = pages.size

    override fun clone(): BookMetaMock = (super.clone() as BookMetaMock).apply {
        pages(this@BookMetaMock.pages.toList())
    }

    override fun spigot(): BookMeta.Spigot {
        throw UnimplementedOperationException()
    }

    override fun hashCode(): Int = super.hashCode() + Objects.hash(
        author,
        pages,
        title
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (other !is BookMetaMock) return false

        if (title != other.title) return false
        if (author != other.author) return false
        if (generation != other.generation) return false
        if (pages != other.pages) return false

        return true
    }
}
