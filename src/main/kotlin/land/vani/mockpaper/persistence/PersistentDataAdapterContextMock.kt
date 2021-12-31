package land.vani.mockpaper.persistence

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer

class PersistentDataAdapterContextMock : PersistentDataAdapterContext {
    override fun newPersistentDataContainer(): PersistentDataContainer =
        PersistentDataContainerMock()
}
