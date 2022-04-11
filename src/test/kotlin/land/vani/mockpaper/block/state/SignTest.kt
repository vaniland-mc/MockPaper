package land.vani.mockpaper.block.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import land.vani.mockpaper.UnimplementedOperationException
import net.kyori.adventure.text.Component
import org.bukkit.DyeColor
import org.bukkit.Material

class SignTest : ShouldSpec({
    lateinit var sign: SignMock

    beforeEach {
        sign = SignMock(Material.OAK_SIGN)
    }

    should("getSnapshot is SignMock") {
        sign.getSnapshot().shouldBeInstanceOf<SignMock>()
    }

    should("getColor is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            sign.color
        }
    }

    should("setColor is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            sign.color = DyeColor.WHITE
        }
    }

    context("lines") {
        should("lines is default empty strings") {
            sign.lines() shouldHaveSize 4
            sign.lines() shouldContainExactly listOf(
                Component.empty(),
                Component.empty(),
                Component.empty(),
                Component.empty(),
            )
        }

        should("line added") {
            sign.line(0, Component.text("some text"))

            sign.lines() shouldHaveSize 4
            sign.lines() shouldContainExactly listOf(
                Component.text("some text"),
                Component.empty(),
                Component.empty(),
                Component.empty(),
            )
        }
    }

    context("line(Int)") {
        should("success") {
            sign.line(0, Component.text("some text"))

            sign.line(0) shouldBe Component.text("some text")
        }

        should("index outbounds") {
            shouldThrow<IndexOutOfBoundsException> {
                sign.line(10)
            }
        }
    }

    context("line(Int, Component)") {
        should("success") {
            sign.line(0, Component.text("some text"))

            sign.line(0) shouldBe Component.text("some text")
        }

        should("index outbounds") {
            shouldThrow<IndexOutOfBoundsException> {
                sign.line(10, Component.text("some text"))
            }
        }
    }

    context("getLines") {
        should("default empty strings") {
            sign.lines shouldHaveSize 4
            sign.lines shouldContainExactly arrayOf("", "", "", "")
        }

        @Suppress("DEPRECATION")
        should("line added") {
            sign.setLine(0, "some text")

            sign.lines shouldHaveSize 4
            sign.lines shouldContainExactly arrayOf("some text", "", "", "")
        }
    }

    context("getLine(Int)") {
        @Suppress("DEPRECATION")
        should("success") {
            sign.setLine(0, "some text")

            sign.lines shouldHaveSize 4
            sign.lines shouldContainExactly arrayOf("some text", "", "", "")
        }

        should("index outbounds") {
            @Suppress("DEPRECATION")
            shouldThrow<IndexOutOfBoundsException> {
                sign.getLine(10)
            }
        }
    }

    @Suppress("DEPRECATION")
    context("setLine(Int, String)") {
        should("success") {
            sign.setLine(0, "some text")

            sign.lines shouldHaveSize 4
            sign.lines shouldContainExactly arrayOf("some text", "", "", "")
        }

        should("index outbounds") {
            shouldThrow<IndexOutOfBoundsException> {
                sign.setLine(10, "some text")
            }
        }
    }

    should("isEditable is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            sign.isEditable
        }
    }

    should("setEditable is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            sign.isEditable = true
        }
    }

    should("isGlowingText is not implemented yet") {
        shouldThrow<UnimplementedOperationException> {
            sign.isGlowingText
        }
    }

    should("setGlowingText is not implemented yet") {
        shouldThrowUnit<UnimplementedOperationException> {
            sign.isGlowingText = true
        }
    }
})
