package land.vani.mockpaper.entity

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.randomLocation
import land.vani.mockpaper.world.WorldMock
import org.bukkit.entity.EntityType

class ArmorStandTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var world: WorldMock
    lateinit var armorStand: ArmorStandMock

    beforeTest {
        server = MockPaper.mock()
        world = server.addSimpleWorld("world")
        armorStand = world.spawn(randomLocation(world))
    }

    should("entityType") {
        armorStand.type shouldBe EntityType.ARMOR_STAND
    }

    should("armorStand in world#getEntities") {
        world.entities shouldContain armorStand
    }

    should("hasEquipment") {
        armorStand.equipment.shouldNotBeNull()
    }

    should("arms") {
        armorStand.setArms(true)
        armorStand.hasArms() shouldBe true

        armorStand.setArms(false)
        armorStand.hasArms() shouldBe false
    }

    should("small") {
        armorStand.isSmall = true
        armorStand.isSmall shouldBe true

        armorStand.isSmall = false
        armorStand.isSmall shouldBe false
    }

    should("marker") {
        armorStand.isMarker = true
        armorStand.isMarker shouldBe true

        armorStand.isMarker = false
        armorStand.isMarker shouldBe false
    }

    should("basePlate") {
        armorStand.setBasePlate(true)
        armorStand.hasBasePlate() shouldBe true

        armorStand.setBasePlate(false)
        armorStand.hasBasePlate() shouldBe false
    }
})
