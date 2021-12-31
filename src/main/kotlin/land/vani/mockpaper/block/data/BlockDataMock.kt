package land.vani.mockpaper.block.data

import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.Material
import org.bukkit.SoundGroup
import org.bukkit.block.data.BlockData

class BlockDataMock(
    private val material: Material,
) : BlockData {
    override fun getMaterial(): Material = material

    override fun getAsString(): String {
        throw UnimplementedOperationException()
    }

    override fun getAsString(hideUnspecified: Boolean): String {
        throw UnimplementedOperationException()
    }

    override fun merge(data: BlockData): BlockData {
        throw UnimplementedOperationException()
    }

    override fun getSoundGroup(): SoundGroup {
        throw UnimplementedOperationException()
    }

    override fun matches(data: BlockData?): Boolean {
        return if (data == null) {
            false
        } else {
            data.material == material
        }
    }

    override fun clone(): BlockData = BlockDataMock(material)
}
