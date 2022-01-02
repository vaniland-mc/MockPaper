package land.vani.mockpaper.persistence

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer

/**
 * This is about as basic of an implementation for [PersistentDataAdapterContext] as you could imagine.
 *
 * But it is identical with the default CraftBukkit implementation too. So mock successful I would say.
 */
class PersistentDataAdapterContextMock : PersistentDataAdapterContext {
    override fun newPersistentDataContainer(): PersistentDataContainer =
        PersistentDataContainerMock()
}
