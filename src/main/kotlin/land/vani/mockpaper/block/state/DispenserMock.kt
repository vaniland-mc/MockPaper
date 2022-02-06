package land.vani.mockpaper.block.state

import land.vani.mockpaper.UnimplementedOperationException
import land.vani.mockpaper.inventory.DispenserInventoryMock
import land.vani.mockpaper.inventory.InventoryMock
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockState
import org.bukkit.block.Dispenser
import org.bukkit.loot.LootTable
import org.bukkit.projectiles.BlockProjectileSource
import java.util.UUID

/**
 * This [ContainerMock] represents a [Dispenser].
 *
 * @author TheBusyBiscuit
 */
class DispenserMock : ContainerMock, Dispenser {
    constructor(material: Material, block: Block? = null) : super(material, block)

    constructor(block: Block) : super(block)

    constructor(state: DispenserMock) : super(state)

    override fun getLootTable(): LootTable? {
        throw UnimplementedOperationException()
    }

    override fun setLootTable(table: LootTable?) {
        throw UnimplementedOperationException()
    }

    override fun getSeed(): Long {
        throw UnimplementedOperationException()
    }

    override fun isRefillEnabled(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasBeenFilled(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasPlayerLooted(player: UUID): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLastLooted(player: UUID): Long? {
        throw UnimplementedOperationException()
    }

    override fun setHasPlayerLooted(player: UUID, looted: Boolean): Boolean {
        throw UnimplementedOperationException()
    }

    override fun hasPendingRefill(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getLastFilled(): Long {
        throw UnimplementedOperationException()
    }

    override fun getNextRefill(): Long {
        throw UnimplementedOperationException()
    }

    override fun setNextRefill(refillAt: Long): Long {
        throw UnimplementedOperationException()
    }

    override fun setSeed(seed: Long) {
        throw UnimplementedOperationException()
    }

    override fun createInventory(): InventoryMock = DispenserInventoryMock(this)

    override fun getSnapshot(): BlockState = DispenserMock(this)

    override fun getBlockProjectileSource(): BlockProjectileSource? =
        if (isPlaced) {
            DispenserProjectileSourceMock(this)
        } else {
            null
        }

    override fun dispense(): Boolean {
        throw UnimplementedOperationException()
    }
}
