package land.vani.mockpaper.entity

import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
import java.util.UUID

/**
 * This is the mock of a dropped [Item].
 *
 * It can hold an [ItemStack], that pretty much covers it all.
 *
 * @author TheBusyBiscuit
 */
class ItemEntityMock(
    server: ServerMock,
    uuid: UUID,
    private var item: ItemStack,
) : EntityMock(server, uuid), Item {
    private var pickupDelay: Int = 10

    override fun getType(): EntityType = EntityType.DROPPED_ITEM

    override fun getItemStack(): ItemStack = item

    override fun setItemStack(stack: ItemStack) {
        item = stack.clone()
    }

    override fun getPickupDelay(): Int = pickupDelay

    override fun setPickupDelay(delay: Int) {
        pickupDelay = delay
    }

    override fun getOwner(): UUID? {
        throw UnimplementedOperationException()
    }

    override fun setOwner(owner: UUID?) {
        throw UnimplementedOperationException()
    }

    override fun getThrower(): UUID? {
        throw UnimplementedOperationException()
    }

    override fun setThrower(uuid: UUID?) {
        throw UnimplementedOperationException()
    }

    override fun canMobPickup(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setCanMobPickup(canMobPickup: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun canPlayerPickup(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setCanPlayerPickup(canPlayerPickup: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun willAge(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setWillAge(willAge: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun getHealth(): Int {
        throw UnimplementedOperationException()
    }

    override fun setHealth(health: Int) {
        throw UnimplementedOperationException()
    }
}
