package land.vani.mockpaper.attribute

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.doubles.shouldBeExactly
import io.kotest.matchers.shouldBe
import org.bukkit.attribute.Attribute

class AttributeInstanceMockTest : ShouldSpec({
    lateinit var attribute: AttributeInstanceMock

    context("default") {
        attribute = AttributeInstanceMock(Attribute.GENERIC_FLYING_SPEED, 5.0)

        should("getAttribute is Attribute.GENERIC_FLYING_SPEED") {
            attribute.attribute shouldBe Attribute.GENERIC_FLYING_SPEED
        }

        should("getBaseValue is 5.0") {
            attribute.baseValue shouldBeExactly 5.0
        }
        should("getValue is 5.0") {
            attribute.value shouldBeExactly 5.0
        }
        should("getDefaultValue is 5.0") {
            attribute.defaultValue shouldBeExactly 5.0
        }
    }

    context("set baseValue to 8.0") {
        AttributeInstanceMock(Attribute.GENERIC_FLYING_SPEED, 5.0)
        attribute.baseValue = 8.0

        should("getBaseValue is 8.0") {
            attribute.baseValue shouldBeExactly 8.0
        }
        should("getValue is 8.0") {
            attribute.value shouldBeExactly 8.0
        }
        should("getDefaultValue is 5.0") {
            attribute.defaultValue shouldBeExactly 5.0
        }
    }
})
