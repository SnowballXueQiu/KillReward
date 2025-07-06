package cc.vastsea.killreward.config

import cc.vastsea.killreward.KillRewardPlugin
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.EntityType

class ConfigManager(private val plugin: KillRewardPlugin) {
    
    private lateinit var config: FileConfiguration
    
    fun loadConfig() {
        plugin.saveDefaultConfig()
        plugin.reloadConfig()
        config = plugin.config
    }
    
    fun reloadConfig() {
        plugin.reloadConfig()
        config = plugin.config
    }
    
    fun getLanguage(): String {
        return config.getString("language", "zh_CN") ?: "zh_CN"
    }
    
    fun isDebugEnabled(): Boolean {
        return config.getBoolean("debug", false)
    }
    
    fun isMonsterRewardEnabled(): Boolean {
        return config.getBoolean("rewards.MONSTER.enabled", true)
    }
    
    fun getMonsterRewardRange(): Pair<Double, Double> {
        val min = config.getDouble("rewards.MONSTER.range.min", 1.0)
        val max = config.getDouble("rewards.MONSTER.range.max", 10.0)
        return Pair(min, max)
    }
    
    fun isAnimalRewardEnabled(): Boolean {
        return config.getBoolean("rewards.ANIMAL.enabled", true)
    }
    
    fun getAnimalRewardRange(): Pair<Double, Double> {
        val min = config.getDouble("rewards.ANIMAL.range.min", 5.0)
        val max = config.getDouble("rewards.ANIMAL.range.max", 15.0)
        return Pair(min, max)
    }
    
    fun isNeutralRewardEnabled(): Boolean {
        return config.getBoolean("rewards.NEUTRAL.enabled", true)
    }
    
    fun getNeutralRewardRange(): Pair<Double, Double> {
        val min = config.getDouble("rewards.NEUTRAL.range.min", 3.0)
        val max = config.getDouble("rewards.NEUTRAL.range.max", 8.0)
        return Pair(min, max)
    }
    
    fun isPlayerRewardEnabled(): Boolean {
        return config.getBoolean("rewards.PLAYER.enabled", true)
    }
    
    fun getPlayerRewardPercentage(): Double {
        val percentageStr = config.getString("rewards.PLAYER.percentage", "25%") ?: "25%"
        return percentageStr.replace("%", "").toDoubleOrNull() ?: 25.0
    }
    
    fun getPlayerMinimumBalance(): Double {
        return config.getDouble("rewards.PLAYER.minimum_balance", 100.0)
    }
    
    fun getPlayerMaximumSteal(): Double {
        return config.getDouble("rewards.PLAYER.maximum_steal", 1000.0)
    }
    
    fun getSpecificEntityReward(entityType: EntityType): Pair<Double, Double>? {
        val path = "specific_entities.${entityType.name}"
        if (!config.contains("$path.enabled") || !config.getBoolean("$path.enabled", false)) {
            return null
        }
        
        val min = config.getDouble("$path.range.min", 1.0)
        val max = config.getDouble("$path.range.max", 10.0)
        return Pair(min, max)
    }
    
    fun isWorldEnabled(worldName: String): Boolean {
        val enabledWorlds = config.getStringList("worlds.enabled_worlds")
        val disabledWorlds = config.getStringList("worlds.disabled_worlds")
        
        // 如果禁用列表包含此世界，返回false
        if (disabledWorlds.contains(worldName)) {
            return false
        }
        
        // 如果启用列表为空，则所有世界都启用（除了禁用列表中的）
        if (enabledWorlds.isEmpty()) {
            return true
        }
        
        // 如果启用列表不为空，只有在列表中的世界才启用
        return enabledWorlds.contains(worldName)
    }
    

    
    fun getEntityCategory(entityType: EntityType): EntityCategory {
        return when {
            entityType.isAlive && isHostileMob(entityType) -> EntityCategory.MONSTER
            entityType.isAlive && isPassiveMob(entityType) -> EntityCategory.ANIMAL
            entityType.isAlive && isNeutralMob(entityType) -> EntityCategory.NEUTRAL
            entityType == EntityType.PLAYER -> EntityCategory.PLAYER
            else -> EntityCategory.OTHER
        }
    }
    
    private fun isHostileMob(entityType: EntityType): Boolean {
        return when (entityType) {
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER,
            EntityType.WITCH, EntityType.ENDERMAN, EntityType.BLAZE, EntityType.GHAST,
            EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.WITHER_SKELETON,
            EntityType.STRAY, EntityType.HUSK, EntityType.PHANTOM, EntityType.DROWNED,
            EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.EVOKER, EntityType.VEX,
            EntityType.RAVAGER, EntityType.WITHER, EntityType.ENDER_DRAGON,
            EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN, EntityType.SHULKER,
            EntityType.SILVERFISH, EntityType.ENDERMITE, EntityType.CAVE_SPIDER,
            EntityType.ZOMBIE_VILLAGER, EntityType.HOGLIN, EntityType.ZOGLIN,
            EntityType.PIGLIN_BRUTE, EntityType.WARDEN -> true
            else -> false
        }
    }
    
    private fun isPassiveMob(entityType: EntityType): Boolean {
        return when (entityType) {
            EntityType.COW, EntityType.PIG, EntityType.CHICKEN, EntityType.SHEEP,
            EntityType.RABBIT, EntityType.HORSE, EntityType.DONKEY, EntityType.MULE,
            EntityType.LLAMA, EntityType.TRADER_LLAMA, EntityType.CAT, EntityType.OCELOT,
            EntityType.WOLF, EntityType.PARROT, EntityType.BAT, EntityType.MUSHROOM_COW,
            EntityType.VILLAGER, EntityType.WANDERING_TRADER, EntityType.TURTLE,
            EntityType.PANDA, EntityType.FOX, EntityType.BEE, EntityType.STRIDER,
            EntityType.AXOLOTL, EntityType.GLOW_SQUID, EntityType.GOAT,
            EntityType.ALLAY, EntityType.FROG, EntityType.TADPOLE, EntityType.CAMEL,
            EntityType.SNIFFER -> true
            else -> false
        }
    }
    
    private fun isNeutralMob(entityType: EntityType): Boolean {
        return when (entityType) {
            EntityType.IRON_GOLEM, EntityType.SNOWMAN, EntityType.SQUID,
            EntityType.DOLPHIN, EntityType.POLAR_BEAR, EntityType.PIGLIN,
            EntityType.ZOMBIFIED_PIGLIN, EntityType.PIGLIN_BRUTE -> true
            else -> false
        }
    }
    
    enum class EntityCategory {
        MONSTER, ANIMAL, NEUTRAL, PLAYER, OTHER
    }
}