package land.vani.mockpaper.entity

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import org.bukkit.Material
import org.bukkit.inventory.EntityEquipment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.UUID

class EntityEquipmentMockTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var armorStand: ArmorStandMock
    lateinit var equipment: EntityEquipment

    beforeTest {
        server = MockPaper.mock()
        armorStand = ArmorStandMock(server, UUID.randomUUID())
        equipment = armorStand.equipment
    }

    should("mainHand") {
        val item = ItemStack(Material.DIAMOND)

        equipment.itemInMainHand shouldBe ItemStack(Material.AIR)
        equipment.setItemInMainHand(item)

        equipment.itemInMainHand shouldBe item
        equipment.getItem(EquipmentSlot.HAND) shouldBe item
    }

    should("offHand") {
        val item = ItemStack(Material.DIAMOND)

        equipment.itemInOffHand shouldBe ItemStack(Material.AIR)
        equipment.setItemInOffHand(item)

        equipment.itemInOffHand shouldBe item
        equipment.getItem(EquipmentSlot.OFF_HAND) shouldBe item
    }

    should("helmet") {
        val item = ItemStack(Material.DIAMOND)

        equipment.helmet.shouldBeNull()
        equipment.helmet = item

        equipment.helmet shouldBe item
        equipment.getItem(EquipmentSlot.HEAD) shouldBe item
    }

    should("chestPlate") {
        val item = ItemStack(Material.DIAMOND)

        equipment.chestplate.shouldBeNull()
        equipment.chestplate = item

        equipment.chestplate shouldBe item
        equipment.getItem(EquipmentSlot.CHEST) shouldBe item
    }

    should("leggings") {
        val item = ItemStack(Material.DIAMOND)

        equipment.leggings.shouldBeNull()
        equipment.leggings = item

        equipment.leggings shouldBe item
        equipment.getItem(EquipmentSlot.LEGS) shouldBe item
    }

    should("boots") {
        val item = ItemStack(Material.DIAMOND)

        equipment.boots.shouldBeNull()
        equipment.boots = item

        equipment.boots shouldBe item
        equipment.getItem(EquipmentSlot.FEET) shouldBe item
    }
})
