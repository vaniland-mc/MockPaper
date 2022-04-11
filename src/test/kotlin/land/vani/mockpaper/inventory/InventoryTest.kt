package land.vani.mockpaper.inventory

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.internal.fallbackNull
import org.bukkit.Material
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class InventoryTest : ShouldSpec({
    lateinit var inventory: InventoryMock

    beforeEach {
        MockPaper.mock()
        inventory = InventoryMock(null, 9, InventoryType.CHEST)
    }

    afterEach {
        MockPaper.unmock()
    }

    context("constructors") {
        should("with size") {
            InventoryMock(null, 9, InventoryType.CHEST).size shouldBeExactly 9
            InventoryMock(null, 18, InventoryType.CHEST).size shouldBeExactly 18
        }

        should("with size too small") {
            shouldThrow<IllegalArgumentException> {
                InventoryMock(null, -1, InventoryType.CHEST)
            }
        }

        should("with size too large") {
            shouldThrow<IllegalArgumentException> {
                InventoryMock(null, 63, InventoryType.CHEST)
            }
        }

        should("with size not a multiple of 9") {
            shouldThrow<IllegalArgumentException> {
                InventoryMock(null, 10, InventoryType.CHEST)
            }
        }

        should("without size params") {
            InventoryMock(null, InventoryType.CHEST).size shouldBeExactly 27
        }

        should("type") {
            InventoryMock(null, 9, InventoryType.CHEST).type shouldBe InventoryType.CHEST
            InventoryMock(null, 9, InventoryType.DROPPER).type shouldBe InventoryType.DROPPER
        }
    }

    should("getItem default is all air") {
        inventory.all { it.type == Material.AIR } shouldBe true
    }

    should("clear inventory") {
        repeat(inventory.size) {
            inventory.addItem(ItemStack(Material.DIRT, 64))
        }
        inventory.all { it.type == Material.AIR } shouldBe false

        inventory.clear()
        inventory.all { it.type == Material.AIR } shouldBe true
    }

    should("clear slot") {
        inventory.setItem(0, ItemStack(Material.DIRT))
        inventory.getItem(0)?.type shouldBe Material.DIRT

        inventory.clear(0)
        inventory.getItem(0) shouldBe null
    }

    should("firstEmpty") {
        repeat(inventory.size) {
            inventory.addItem(ItemStack(Material.DIRT, 64))
        }

        inventory.firstEmpty() shouldBeExactly -1
        inventory.clear()

        inventory.firstEmpty() shouldBeExactly 0
    }

    context("addItem") {
        should("on empty add 1 stack") {
            val item = ItemStack(Material.DIRT, 64)
            val remaining = inventory.addItem(item)
            remaining.shouldBeNull()

            val stored = inventory.getItem(0)
            stored shouldBe item

            val next = inventory.getItem(1)
            next.shouldBeNull()
        }

        should("on full inventory add 1 stack") {
            val filledItem = ItemStack(Material.COBBLESTONE, 1)
            repeat(inventory.size) {
                inventory.setItem(it, filledItem)
            }
            val item = ItemStack(Material.DIRT, 64)
            val remaining = inventory.addItem(item)
            remaining shouldBe item
        }

        should("on inventory partially filled add 1 stack") {
            val filledItem = ItemStack(Material.COBBLESTONE, 1)
            for (i in 2 until inventory.size) {
                inventory.setItem(i, filledItem)
            }
            val preset = ItemStack(Material.DIRT, 48)
            inventory.setItem(0, preset)
            inventory.setItem(2, preset)
            preset.amount = 64
            inventory.setItem(1, preset)

            val store = ItemStack(Material.DIRT, 64)
            val remaining = inventory.addItem(store)
            remaining shouldBe ItemStack(Material.DIRT, ((64 - 48) * 2 + 64) - 64 /* = 32 */)
        }

        should("multiple items") {
            val filledItem = ItemStack(Material.COBBLESTONE, 1)
            repeat(inventory.size) {
                inventory.setItem(it, filledItem)
            }

            val preset = ItemStack(Material.DIRT, 48)
            inventory.setItem(0, preset)

            val item1 = ItemStack(Material.COBBLESTONE, 64)
            val item2 = ItemStack(Material.DIRT, 64)
            val result = inventory.addItem(item1, item2)

            result shouldHaveSize 1
            result shouldContainKey 1
            result[1]!!.amount shouldBeExactly 48
        }
    }

    context("set contents") {
        should("add 1 item") {
            val filledItem = ItemStack(Material.COBBLESTONE, 1)
            repeat(inventory.size) {
                inventory.setItem(it, filledItem)
            }
            val item = ItemStack(Material.DIRT, 32)
            inventory.setContents(arrayOf(item))

            inventory.getItem(0)!!.isSimilar(item) shouldBe true
            inventory.drop(1).all { it.type == Material.AIR } shouldBe true
        }

        should("add air") {
            inventory.setContents(arrayOf(ItemStack(Material.AIR)))

            inventory.getItem(0)?.type shouldBe null
        }
    }

    should("iterator") {
        val item1 = ItemStack(Material.COBBLESTONE, 64)
        val item2 = ItemStack(Material.DIRT, 64)
        inventory.addItem(item1, item2)

        val iterator = inventory.iterator()
        iterator.next() shouldBe item1
        iterator.next() shouldBe item2
        iterator.hasNext() shouldBe false
    }

    should("all empty inventories are filled with air") {
        inventory.all { it.type == Material.AIR } shouldBe true
    }

    should("contents (fallback null to air) and storageContents is equal") {
        inventory.contents.fallbackNull() shouldBe inventory.storageContents
    }

    context("contains") {
        should("itemStack") {
            inventory.addItem(ItemStack(Material.DIRT))
            inventory.contains(ItemStack(Material.DIRT)) shouldBe true
        }

        should("itemStack with amount") {
            inventory.addItem(ItemStack(Material.DIRT, 2))
            inventory.contains(ItemStack(Material.DIRT), 2) shouldBe true
        }

        should("itemStack with incorrect item") {
            inventory.addItem(ItemStack(Material.DIRT))
            inventory.contains(ItemStack(Material.COBBLESTONE)) shouldBe false
        }

        should("material") {
            inventory.addItem(ItemStack(Material.DIRT))
            inventory.contains(Material.DIRT) shouldBe true
        }

        should("material with amount") {
            inventory.addItem(ItemStack(Material.DIRT, 2))
            inventory.contains(Material.DIRT, 2) shouldBe true
        }

        should("material with incorrect type") {
            inventory.addItem(ItemStack(Material.DIRT))
            inventory.contains(Material.COBBLESTONE) shouldBe false
        }
    }

    context("containsAtLeast") {
        should("itemStack") {
            inventory.addItem(ItemStack(Material.DIRT, 3))
            inventory.containsAtLeast(ItemStack(Material.DIRT), 3) shouldBe true
        }

        should("itemStack with extra") {
            inventory.addItem(ItemStack(Material.DIRT, 6))
            inventory.containsAtLeast(ItemStack(Material.DIRT), 3) shouldBe true
        }

        should("itemStack with incorrect amount") {
            inventory.addItem(ItemStack(Material.DIRT))
            inventory.containsAtLeast(ItemStack(Material.DIRT), 3) shouldBe false
        }
    }
})
