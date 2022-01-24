package land.vani.mockpaper

import land.vani.mockpaper.world.Coordinate
import org.bukkit.Location
import org.bukkit.World
import kotlin.random.Random

fun randomLocation(world: World?): Location = Location(
    world,
    Random.nextDouble(),
    Random.nextDouble(),
    Random.nextDouble(),
)

fun randomCoordinate(): Coordinate = Coordinate(
    Random.nextInt(),
    Random.nextInt(),
    Random.nextInt(),
)
