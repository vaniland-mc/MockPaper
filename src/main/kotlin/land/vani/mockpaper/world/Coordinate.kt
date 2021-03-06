package land.vani.mockpaper.world

/**
 * A data class that contains x, y, z coordinate as integers.
 */
data class Coordinate(
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) {
    fun toChunkCoordinate(): ChunkCoordinate =
        ChunkCoordinate(x shr 4, z shr 4)
}
