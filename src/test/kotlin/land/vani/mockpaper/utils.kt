package land.vani.mockpaper

import org.bukkit.Location
import org.bukkit.World
import kotlin.random.Random

fun randomLocation(world: World?): Location = Location(
    world,
    Random.nextDouble(),
    Random.nextDouble(),
    Random.nextDouble()
)
