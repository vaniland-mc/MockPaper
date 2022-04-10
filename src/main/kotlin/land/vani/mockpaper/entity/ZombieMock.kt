package land.vani.mockpaper.entity

import com.destroystokyo.paper.entity.Pathfinder
import land.vani.mockpaper.ServerMock
import land.vani.mockpaper.UnimplementedOperationException
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.Villager
import org.bukkit.entity.Zombie
import org.bukkit.inventory.EntityEquipment
import java.util.UUID

class ZombieMock(server: ServerMock, uuid: UUID) : MonsterMock(server, uuid), Zombie {
    private val equipment = EntityEquipmentMock(this)

    private var isAdult: Boolean = false
    private var isVillager: Boolean = false
    private var profession: Villager.Profession? = null
    private var isConverting: Boolean = false
    private var conversionTime: Int? = null

    init {
        getAttribute(Attribute.GENERIC_MAX_HEALTH).baseValue = 20.0
        health = 20.0
    }

    override fun getType(): EntityType = EntityType.ZOMBIE

    override fun getEquipment(): EntityEquipment = equipment

    @Deprecated(
        "use Ageable#isAdult",
        ReplaceWith("isAdult")
    )
    override fun isBaby(): Boolean = !isAdult

    @Deprecated(
        "use Ageable#isAdult",
        ReplaceWith("isAdult")
    )
    override fun setBaby(flag: Boolean) {
        isAdult = !flag
    }

    // Suppress: Bukkit recommends checking the instanceof ZombieVillager to be a villager zombie,
    // but MockPaper does not support this.
    @Suppress("OVERRIDE_DEPRECATION")
    override fun isVillager(): Boolean = isVillager

    // Suppress: Bukkit recommends checking the instanceof ZombieVillager to be a villager zombie,
    // but MockPaper does not support this.
    @Suppress("OVERRIDE_DEPRECATION")
    override fun setVillager(flag: Boolean) {
        isVillager = flag
    }

    // Suppress: Bukkit recommends checking the instanceof ZombieVillager to be a villager zombie,
    // but MockPaper does not support this.
    @Suppress("OVERRIDE_DEPRECATION")
    override fun getVillagerProfession(): Villager.Profession? = profession

    // Suppress: Bukkit recommends checking the instanceof ZombieVillager to be a villager zombie,
    // but MockPaper does not support this.
    @Suppress("OVERRIDE_DEPRECATION")
    override fun setVillagerProfession(profession: Villager.Profession?) {
        this.profession = profession
    }

    override fun isConverting(): Boolean = isConverting

    override fun getConversionTime(): Int =
        conversionTime ?: throw IllegalStateException("This zombie is not converting")

    override fun setConversionTime(time: Int) {
        conversionTime = time
    }

    override fun isDrowning(): Boolean {
        throw UnimplementedOperationException()
    }

    @Deprecated(
        "See #setConversionTime",
        ReplaceWith("setConversionTime(drownedConvertionTime)")
    )
    override fun startDrowning(drownedConversionTime: Int) {
        throw UnimplementedOperationException()
    }

    override fun stopDrowning() {
        throw UnimplementedOperationException()
    }

    override fun isArmsRaised(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setArmsRaised(raised: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun shouldBurnInDay(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setShouldBurnInDay(shouldBurnInDay: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun canBreakDoors(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setCanBreakDoors(canBreakDoors: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun supportsBreakingDoors(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun getAge(): Int {
        throw UnimplementedOperationException()
    }

    override fun setAge(age: Int) {
        throw UnimplementedOperationException()
    }

    override fun setBaby() {
        isAdult = true
    }

    override fun getPathfinder(): Pathfinder {
        throw UnimplementedOperationException()
    }

    override fun isLeftHanded(): Boolean {
        throw UnimplementedOperationException()
    }

    override fun setLeftHanded(leftHanded: Boolean) {
        throw UnimplementedOperationException()
    }

    @Deprecated("Breedable supports age locking and breed, but only Ageable does not.")
    override fun getAgeLock(): Boolean {
        throw UnimplementedOperationException()
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Breedable supports age locking and breed, but only Ageable does not.")
    override fun setAgeLock(lock: Boolean) {
        throw UnimplementedOperationException()
    }

    override fun isAdult(): Boolean = isAdult

    override fun setAdult() {
        isAdult = true
    }

    @Deprecated("Breedable supports age locking and breed, but only Ageable does not.")
    override fun canBreed(): Boolean {
        throw UnimplementedOperationException()
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Breedable supports age locking and breed, but only Ageable does not.")
    override fun setBreed(breed: Boolean) {
        throw UnimplementedOperationException()
    }
}
