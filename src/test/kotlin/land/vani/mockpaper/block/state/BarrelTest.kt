package land.vani.mockpaper.block.state

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import land.vani.mockpaper.MockPaper
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.block.BlockMock
import land.vani.mockpaper.inventory.BarrelInventoryMock
import land.vani.mockpaper.randomLocation
import land.vani.mockpaper.world.WorldMock
import org.bukkit.Material
import org.bukkit.loot.LootTables

class BarrelTest : ShouldSpec({
    lateinit var server: ServerMock

    beforeTest {
        server = MockPaper.mock()
    }

    context("placed") {
        lateinit var world: WorldMock
        lateinit var block: BlockMock
        lateinit var barrel: BarrelMock

        beforeTest {
            world = WorldMock(server)
            block = BlockMock(Material.BARREL, randomLocation(world))
            barrel = BarrelMock(Material.BARREL, block)
        }

        should("isPlaced is true") {
            barrel.isPlaced shouldBe true
        }

        should("createInventory") {
            barrel.inventory.shouldBeInstanceOf<BarrelInventoryMock>()
        }

        should("getSnapshot") {
            val snapshot = barrel.getSnapshot()

            snapshot.material shouldBe barrel.material
            snapshot.block shouldBe barrel.block
        }
    }

    context("not placed") {
        lateinit var barrel: BarrelMock

        beforeTest {
            barrel = BarrelMock(Material.BARREL)
        }

        should("isPlaced is false") {
            barrel.isPlaced shouldBe false
        }

        should("createInventory") {
            barrel.inventory.shouldBeInstanceOf<BarrelInventoryMock>()
        }

        should("getSnapshot") {
            val snapshot = barrel.getSnapshot()

            snapshot.material shouldBe barrel.material
        }

        should("getBlock throws IllegalStateException") {
            shouldThrow<IllegalStateException> {
                barrel.block
            }
        }
    }

    context("common") {
        lateinit var barrel: BarrelMock

        beforeTest {
            barrel = BarrelMock(Material.BARREL)
        }

        should("getLootTable is not implemented yet") {
            shouldThrow<UnimplementedOperationException> {
                barrel.lootTable
            }
        }

        should("setLootTable is not implemented yet") {
            shouldThrowUnit<UnimplementedOperationException> {
                barrel.lootTable = LootTables.SPAWN_BONUS_CHEST.lootTable
            }
        }

        should("getSeed is not implemented yet") {
            shouldThrow<UnimplementedOperationException> {
                barrel.seed
            }
        }

        should("setSeed is not implemented yet") {
            shouldThrowUnit<UnimplementedOperationException> {
                barrel.seed = 10
            }
        }

        should("open is not implemented yet") {
            shouldThrow<UnimplementedOperationException> {
                barrel.open()
            }
        }

        should("isOpen is not implemented yet") {
            shouldThrow<UnimplementedOperationException> {
                barrel.isOpen
            }
        }

        should("close is not implemented yet") {
            shouldThrow<UnimplementedOperationException> {
                barrel.close()
            }
        }
    }
})
