package land.vani.mockpaper.attribute

import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier

class AttributeInstanceMock(
    private val attribute: Attribute,
    private var value: Double,
) : AttributeInstance {
    private val _defaultValue = value

    override fun getAttribute(): Attribute = attribute

    override fun getBaseValue(): Double = value

    override fun setBaseValue(value: Double) {
        this.value = value
    }

    override fun getModifiers(): MutableCollection<AttributeModifier> {
        throw UnimplementedOperationException()
    }

    override fun addModifier(modifier: AttributeModifier) {
        throw UnimplementedOperationException()
    }

    override fun removeModifier(modifier: AttributeModifier) {
        throw UnimplementedOperationException()
    }

    override fun getValue(): Double = baseValue

    override fun getDefaultValue(): Double = _defaultValue
}
