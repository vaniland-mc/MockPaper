package land.vani.mockpaper.statistic

import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.EntityType
import java.util.EnumMap

/**
 * An implementation of player statistic, similar to CraftStatistic.
 */
class StatisticsMock {
    private val untypedStatistics: MutableMap<Statistic, Int> = EnumMap(Statistic::class.java)
    private val materialStatistics: MutableMap<Statistic, MutableMap<Material, Int>> = EnumMap(Statistic::class.java)
    private val entityStatistics: MutableMap<Statistic, MutableMap<EntityType, Int>> = EnumMap(Statistic::class.java)

    fun setStatistic(statistic: Statistic, amount: Int) {
        require(amount >= 0) { "amount must be greater than or equals to 0" }
        require(statistic.type == Statistic.Type.UNTYPED) { "statistic must be provided with parameter" }
        untypedStatistics[statistic] = amount
    }

    fun setStatistic(statistic: Statistic, material: Material, amount: Int) {
        require(amount >= 0) { "amount must be greater than or equals to 0" }
        require(
            statistic.type == Statistic.Type.ITEM || statistic.type == Statistic.Type.BLOCK
        ) { "statistic must take a material parameter" }
        materialStatistics.getOrPut(statistic) {
            EnumMap(Material::class.java)
        }[material] = amount
    }

    fun setStatistic(statistic: Statistic, entityType: EntityType, amount: Int) {
        require(amount >= 0) { "amount must be greater than or equals to 0" }
        require(statistic.type == Statistic.Type.ENTITY) { "statistic must take an entity parameter" }
        entityStatistics.getOrPut(statistic) {
            EnumMap(EntityType::class.java)
        }[entityType] = amount
    }

    fun incrementStatistic(statistic: Statistic, amount: Int) {
        require(amount > 0) { "amount must be greater than 0" }
        setStatistic(statistic, getStatistic(statistic) + amount)
    }

    fun incrementStatistic(statistic: Statistic, material: Material, amount: Int) {
        require(amount > 0) { "amount must be greater than 0" }
        require(
            statistic.type == Statistic.Type.ITEM || statistic.type == Statistic.Type.BLOCK
        ) { "statistic must take a material parameter" }
        setStatistic(statistic, getStatistic(statistic, material) + amount)
    }

    fun incrementStatistic(statistic: Statistic, entityType: EntityType, amount: Int) {
        require(amount > 0) { "amount must be greater than 0" }
        require(statistic.type == Statistic.Type.ENTITY) { "statistic must take an entity parameter" }
        setStatistic(statistic, getStatistic(statistic, entityType) + amount)
    }

    fun decrementStatistic(statistic: Statistic, amount: Int) {
        require(amount > 0) { "amount must be greater than 0" }
        setStatistic(statistic, getStatistic(statistic) - 1)
    }

    fun decrementStatistic(statistic: Statistic, material: Material, amount: Int) {
        require(amount > 0) { "amount must be greater than 0" }
        require(
            statistic.type == Statistic.Type.ITEM || statistic.type == Statistic.Type.BLOCK
        ) { "statistic must take a material parameter" }
        setStatistic(statistic, getStatistic(statistic, material) - amount)
    }

    fun decrementStatistic(statistic: Statistic, entityType: EntityType, amount: Int) {
        require(amount > 0) { "amount must be greater than 0" }
        require(statistic.type == Statistic.Type.ENTITY) { "statistic must take an entity parameter" }
        setStatistic(statistic, getStatistic(statistic, entityType) - amount)
    }

    fun getStatistic(statistic: Statistic): Int {
        require(statistic.type == Statistic.Type.UNTYPED) { "statistic must be provided with parameter" }
        return untypedStatistics[statistic] ?: 0
    }

    fun getStatistic(statistic: Statistic, material: Material): Int {
        require(
            statistic.type == Statistic.Type.ITEM || statistic.type == Statistic.Type.BLOCK
        ) { "statistic must take a material parameter" }
        return materialStatistics[statistic]?.get(material) ?: 0
    }

    fun getStatistic(statistic: Statistic, entityType: EntityType): Int {
        require(statistic.type == Statistic.Type.ENTITY) { "statistic must take an entity parameter" }
        return entityStatistics[statistic]?.get(entityType) ?: 0
    }
}
