package land.vani.mockpaper.inventory.meta

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import land.vani.mockpaper.MockPaper
import net.kyori.adventure.text.Component
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ItemMetaMockTest : ShouldSpec({
    lateinit var meta: ItemMetaMock

    beforeTest {
        MockPaper.mock()
        meta = ItemMetaMock()
    }

    should("constructor") {
        meta.displayName(Component.text("some name"))
        meta.lore(listOf(Component.text("some lore")))
        meta.isUnbreakable = true
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)

        val meta2 = ItemMetaMock(meta)

        meta2 shouldBe meta
        meta shouldBe meta2
        meta.hashCode() shouldBe meta2.hashCode()
        meta.itemFlags shouldContainExactly meta2.itemFlags
    }

    context("displayName") {
        should("hasDisplayName is default false") {
            meta.hasDisplayName() shouldBe false
        }

        should("displayName(Component)") {
            meta.displayName(Component.text("display name"))

            meta.displayName() shouldBe Component.text("display name")
        }

        should("displayName(null)") {
            meta.displayName(Component.text("display name"))
            meta.displayName(null)

            meta.hasDisplayName() shouldBe false
        }

        @Suppress("DEPRECATION")
        should("setDisplayName(String)") {
            meta.setDisplayName("display name")

            meta.hasDisplayName() shouldBe true
            meta.displayName shouldBe "display name"
        }

        @Suppress("DEPRECATION")
        should("setDisplayName(null)") {
            meta.setDisplayName(null)

            meta.hasDisplayName() shouldBe false
        }
    }

    context("lore") {
        should("hasLore is default false") {
            meta.hasLore() shouldBe false
        }

        should("lore(List<Component>)") {
            meta.lore(listOf(Component.text("lore")))

            meta.lore() shouldContainExactly listOf(Component.text("lore"))
        }

        should("lore(null)") {
            meta.lore(null)

            meta.hasLore() shouldBe false
        }

        should("setLore") {
            meta.lore = listOf("lore")

            meta.lore shouldContainExactly listOf("lore")
        }
    }

    context("customModelData") {
        should("hasCustomModelData is default false") {
            meta.hasCustomModelData() shouldBe false
        }

        should("setCustomModelData") {
            meta.setCustomModelData(100)

            meta.customModelData shouldBeExactly 100
        }
    }

    context("enchants") {
        should("hasEnchants is default false") {
            meta.hasEnchants() shouldBe false
        }

        should("hasEnchant is default all false") {
            Enchantment.values().forEach {
                meta.hasEnchant(it) shouldBe false
            }
        }

        should("addEnchant") {
            meta.addEnchant(Enchantment.DURABILITY, 10, true)

            meta.hasEnchant(Enchantment.DURABILITY) shouldBe true
            meta.getEnchantLevel(Enchantment.DURABILITY) shouldBeExactly 10
            meta.enchants shouldContainExactly mapOf(Enchantment.DURABILITY to 10)
        }

        should("removeEnchant") {
            meta.addEnchant(Enchantment.DURABILITY, 10, true)

            meta.removeEnchant(Enchantment.DURABILITY) shouldBe true
            meta.hasEnchant(Enchantment.DURABILITY) shouldBe false
        }
    }

    context("itemFlags") {
        should("itemFlags is default empty") {
            meta.itemFlags.shouldBeEmpty()
        }

        should("hasItemFlag is default all false") {
            ItemFlag.values().forEach {
                meta.hasItemFlag(it) shouldBe false
            }
        }

        should("addItemFlag") {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DESTROYS)

            meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) shouldBe true
            meta.hasItemFlag(ItemFlag.HIDE_DESTROYS) shouldBe true
            meta.hasItemFlag(ItemFlag.HIDE_DYE) shouldBe false
        }

        should("removeItemFlag") {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DESTROYS)
            meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS)

            meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) shouldBe false
            meta.hasItemFlag(ItemFlag.HIDE_DESTROYS) shouldBe true
        }
    }

    should("unbreakable") {
        should("unbreakable is default false") {
            meta.isUnbreakable shouldBe false
        }

        should("setUnbreakable") {
            meta.isUnbreakable = true

            meta.isUnbreakable shouldBe true
        }
    }

    context("damage") {
        should("hasDamage is default false") {
            meta.hasDamage() shouldBe false
        }

        should("setDamage") {
            meta.damage = 10

            meta.hasDamage() shouldBe true
            meta.damage shouldBeExactly 10
        }
    }

    context("repairCost") {
        should("hasRepairCost is default false") {
            meta.hasRepairCost() shouldBe false
        }

        should("setRepairCost") {
            meta.repairCost = 10

            meta.hasRepairCost() shouldBe true
            meta.repairCost shouldBeExactly 10
        }
    }

    context("serialization") {
        should("serialize") {
            meta.displayName(Component.text("test name"))
            meta.lore(listOf(Component.text("test lore")))
            meta.isUnbreakable = true
            meta.damage = 10
            meta.repairCost = 5

            val actual = meta.serialize()
            val expected = buildMap<String, Any?> {
                put("displayName", "test name")
                put("lore", listOf("test lore"))
                put("unbreakable", true)
                put("damage", 10)
                put("repairCost", 5)
                put("enchants", mapOf<Enchantment, Int>())
                put("itemFlags", setOf<ItemFlag>())
                put("persistentDataContainer", mapOf<String, Any?>())
            }

            actual shouldContainAll expected
        }

        should("deserialize") {
            ItemMetaMock.deserialize(meta.serialize()) shouldBe meta
        }

        should("bukkit serialization") {
            val empty = ItemMetaMock()
            val modified = ItemMetaMock()

            modified.apply {
                displayName(Component.text("test name"))
                lore(listOf(Component.text("test lore")))
                isUnbreakable = true
                damage = 5
                repairCost = 3
                setCustomModelData(2)
            }

            val byteOutput = ByteArrayOutputStream()
            val bukkitOutput = BukkitObjectOutputStream(byteOutput)

            withContext(Dispatchers.IO) {
                bukkitOutput.writeObject(empty)
                bukkitOutput.writeObject(modified)
            }

            val byteInput = ByteArrayInputStream(byteOutput.toByteArray())
            val bukkitInput = BukkitObjectInputStream(byteInput)

            withContext(Dispatchers.IO) {
                bukkitInput.readObject() shouldBe empty
                bukkitInput.readObject() shouldBe modified

                bukkitOutput.close()
                bukkitInput.close()
            }
        }
    }

    context("equals") {
        context("display name") {
            should("same display name is same") {
                val meta2 = ItemMetaMock()
                meta.displayName(Component.text("some name"))
                meta2.displayName(Component.text("some name"))

                meta shouldBe meta2
            }

            should("different display name is not same") {
                val meta2 = ItemMetaMock()
                meta.displayName(Component.text("some name 1"))
                meta2.displayName(Component.text("some name 2"))

                meta shouldNotBe meta2
            }

            should("one with display name, one without display name") {
                val meta2 = ItemMetaMock()
                meta.displayName(Component.text("some name"))

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }
        }

        context("lore") {
            should("same lore is same") {
                val meta2 = ItemMetaMock()
                meta.lore(listOf(Component.text("some lore")))
                meta2.lore(listOf(Component.text("some lore")))

                meta shouldBe meta2
            }

            should("different lore is not same") {
                val meta2 = ItemMetaMock()
                meta.lore(listOf(Component.text("some lore 1")))
                meta.lore(listOf(Component.text("some lore 2")))

                meta shouldNotBe meta2
            }

            should("one with lore, one without lore") {
                val meta2 = ItemMetaMock()
                meta.lore(listOf(Component.text("some lore")))

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }

            should("different size of lore") {
                val meta2 = ItemMetaMock()
                meta.lore(listOf(Component.text("lore")))
                meta2.lore(
                    listOf(
                        Component.text("lore"),
                        Component.text("more lore"),
                    )
                )

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }
        }

        should("null") {
            meta shouldNotBe null
            (null as ItemMetaMock?) shouldNotBe meta
        }

        context("customModelData") {
            should("same customModelData") {
                val meta2 = ItemMetaMock()
                meta.setCustomModelData(10)
                meta2.setCustomModelData(10)

                meta shouldBe meta2
            }

            should("different customModelData") {
                val meta2 = ItemMetaMock()
                meta.setCustomModelData(10)
                meta2.setCustomModelData(20)

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }

            should("one with customModel data, one without customModelData") {
                val meta2 = ItemMetaMock()
                meta.setCustomModelData(10)

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }
        }

        context("enchants") {
            should("same enchants") {
                val meta2 = ItemMetaMock()
                meta.addEnchant(Enchantment.DURABILITY, 5, true)
                meta2.addEnchant(Enchantment.DURABILITY, 5, true)

                meta shouldBe meta2
            }

            should("different enchants") {
                val meta2 = ItemMetaMock()
                meta.addEnchant(Enchantment.DURABILITY, 5, true)
                meta2.addEnchant(Enchantment.DURABILITY, 5, true)
                meta2.addEnchant(Enchantment.DAMAGE_ALL, 5, true)

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }

            should("different level of enchants") {
                val meta2 = ItemMetaMock()
                meta.addEnchant(Enchantment.DURABILITY, 5, true)
                meta2.addEnchant(Enchantment.DURABILITY, 10, true)

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }
        }

        context("itemFlags") {
            should("same itemFlags") {
                val meta2 = ItemMetaMock()
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE)
                meta2.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE)

                meta shouldBe meta2
            }

            should("different itemFlags") {
                val meta2 = ItemMetaMock()
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE)
                meta2.addItemFlags(ItemFlag.HIDE_DESTROYS)

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }

            should("one with itemFlags, one with empty itemFlags") {
                val meta2 = ItemMetaMock()
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }
        }

        context("unbreakable") {
            should("same unbreakable") {
                val meta2 = ItemMetaMock()
                meta.isUnbreakable = true
                meta2.isUnbreakable = true

                meta shouldBe meta2
            }

            should("different unbreakable") {
                val meta2 = ItemMetaMock()
                meta.isUnbreakable = true
                meta2.isUnbreakable = false

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }
        }

        context("damage") {
            should("same damage") {
                val meta2 = ItemMetaMock()
                meta.damage = 10
                meta2.damage = 10

                meta shouldBe meta2
            }

            should("different damage") {
                val meta2 = ItemMetaMock()
                meta.damage = 10
                meta2.damage = 20

                meta shouldNotBe meta2
            }

            should("one with damage, one without damage") {
                val meta2 = ItemMetaMock()
                meta.damage = 10

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }
        }

        context("repairCost") {
            should("same repairCost") {
                val meta2 = ItemMetaMock()
                meta.repairCost = 10
                meta2.repairCost = 10

                meta shouldBe meta2
            }

            should("different repairCost") {
                val meta2 = ItemMetaMock()
                meta.repairCost = 10
                meta2.repairCost = 20

                meta shouldNotBe meta2
                meta2 shouldNotBe meta
            }
        }
    }

    should("clone") {
        val meta2 = meta.clone()

        meta shouldBe meta2
        meta2 shouldBe meta
    }
})
