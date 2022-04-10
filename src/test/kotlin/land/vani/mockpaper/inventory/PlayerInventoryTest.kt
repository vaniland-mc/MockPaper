package land.vani.mockpaper.inventory

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

@Suppress("DEPRECATION")
class PlayerInventoryTest : ShouldSpec({
    lateinit var server: ServerMock
    lateinit var inventory: PlayerInventoryMock

    beforeTest {
        server = MockPaper.mock()
        inventory = PlayerInventoryMock(null)
    }

    should("inventory size is 41") {
        inventory.size shouldBeExactly 41
    }

    should("holder") {
        val player = server.addPlayer()
        inventory = PlayerInventoryMock(player)

        inventory.holder shouldBe player
    }

    should("itemInMainHand") {
        val item = ItemStack(Material.DIRT, 25)
        inventory.setItemInMainHand(item)

        inventory.itemInMainHand shouldBe item
        inventory.itemInHand shouldBe item
        inventory.contents[PlayerInventoryMock.SLOT_BAR] shouldBe item
    }

    should("itemInHand") {
        val item = ItemStack(Material.DIRT, 25)
        @Suppress("DEPRECATION")
        inventory.setItemInHand(item)

        inventory.itemInHand shouldBe item
        inventory.itemInMainHand shouldBe item
    }

    context("heldItemSlot") {

        should("change") {
            inventory.heldItemSlot = 1

            inventory.heldItemSlot shouldBeExactly 1

            val item = ItemStack(Material.DIRT, 25)
            inventory.setItemInMainHand(item)

            inventory.itemInMainHand shouldBe item
            inventory.getItem(PlayerInventoryMock.SLOT_BAR + 1) shouldBe item
        }

        should("range") {
            for (i in 0..8) {
                inventory.heldItemSlot = i
                inventory.heldItemSlot shouldBeExactly i
            }
        }

        should("too low") {
            shouldThrowUnit<IllegalArgumentException> {
                inventory.heldItemSlot = -1
            }
        }

        should("too high") {
            shouldThrowUnit<IllegalArgumentException> {
                inventory.heldItemSlot = 9
            }
        }
    }

    should("armor contents size is 4") {
        inventory.armorContents shouldHaveSize 4
    }

    should("extra contents size is 1") {
        inventory.extraContents shouldHaveSize 1
    }

    context("setItem") {
        should("item in contents") {
            val item = ItemStack(Material.DIRT)
            inventory.setItem(0, item)

            inventory.contents[0] shouldBe item
        }

        should("item in armor contents") {
            val item = ItemStack(Material.DIRT)
            inventory.setItem(PlayerInventoryMock.BOOTS, item)

            inventory.armorContents[0] shouldBe item
        }

        should("item in extra contents") {
            val item = ItemStack(Material.DIRT)
            inventory.setItem(PlayerInventoryMock.OFF_HAND, item)

            inventory.extraContents[0] shouldBe item
        }
    }

    should("armorContents") {
        val boots = ItemStack(Material.DIAMOND_BOOTS)
        val leggings = ItemStack(Material.DIAMOND_LEGGINGS)
        val chestPlate = ItemStack(Material.DIAMOND_CHESTPLATE)
        val helmet = ItemStack(Material.DIAMOND_HELMET)

        inventory.setItem(PlayerInventoryMock.BOOTS, boots)
        inventory.setItem(PlayerInventoryMock.LEGGINGS, leggings)
        inventory.setItem(PlayerInventoryMock.CHEST_PLATE, chestPlate)
        inventory.setItem(PlayerInventoryMock.HELMET, helmet)

        inventory.armorContents shouldContainExactly arrayOf(boots, leggings, chestPlate, helmet)
        inventory.boots shouldBe boots
        inventory.leggings shouldBe leggings
        inventory.chestplate shouldBe chestPlate
        inventory.helmet shouldBe helmet
    }

    should("boots") {
        val boots = ItemStack(Material.DIAMOND_BOOTS)
        inventory.boots = boots

        inventory.boots shouldBe boots
    }

    should("leggings") {
        val leggings = ItemStack(Material.DIAMOND_LEGGINGS)
        inventory.leggings = leggings

        inventory.leggings shouldBe leggings
    }

    should("chestPlate") {
        val chestPlate = ItemStack(Material.DIAMOND_CHESTPLATE)
        inventory.chestplate = chestPlate

        inventory.chestplate shouldBe chestPlate
    }

    should("helmet") {
        val helmet = ItemStack(Material.DIAMOND_HELMET)
        inventory.helmet = helmet

        inventory.helmet shouldBe helmet
    }

    should("set contents from get") {
        shouldNotThrowAny {
            inventory.setContents(inventory.contents.fallbackNull())
        }
    }

    should("set armorContents with array") {
        val boots = ItemStack(Material.DIAMOND_BOOTS)
        val leggings = ItemStack(Material.DIAMOND_LEGGINGS)
        val chestPlate = ItemStack(Material.DIAMOND_CHESTPLATE)
        val helmet = ItemStack(Material.DIAMOND_HELMET)
        val contents = arrayOf<ItemStack?>(boots, leggings, chestPlate, helmet)

        inventory.setArmorContents(contents)

        inventory.boots shouldBe boots
        inventory.leggings shouldBe leggings
        inventory.chestplate shouldBe chestPlate
        inventory.helmet shouldBe helmet
    }

    should("offhand") {
        val item = ItemStack(Material.DIRT)
        inventory.setItemInOffHand(item)

        inventory.itemInOffHand shouldBe item
        inventory.getItem(PlayerInventoryMock.OFF_HAND) shouldBe item
    }

    should("setExtraContents should match offhand") {
        val item = ItemStack(Material.DIRT)
        val contents = arrayOf<ItemStack?>(item)
        inventory.setExtraContents(contents)

        inventory.itemInOffHand shouldBe item
    }

    should("setArmorContents with null") {
        shouldThrow<IllegalArgumentException> {
            inventory.setArmorContents(null)
        }
    }

    should("setExtraContents with null") {
        shouldThrow<IllegalArgumentException> {
            inventory.setExtraContents(null)
        }
    }

    should("setArmorContents with too large array") {
        shouldThrow<IllegalArgumentException> {
            inventory.setArmorContents(arrayOfNulls(5))
        }
    }

    should("setExtraContents with too large array") {
        shouldThrow<IllegalArgumentException> {
            inventory.setExtraContents(arrayOfNulls(2))
        }
    }
})
