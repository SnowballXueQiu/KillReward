package cc.vastsea.killreward.listeners

import cc.vastsea.killreward.KillRewardPlugin
import cc.vastsea.killreward.config.ConfigManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import kotlin.random.Random

class EntityDeathListener(private val plugin: KillRewardPlugin) : Listener {
    
    @EventHandler(priority = EventPriority.NORMAL)
    fun onEntityDeath(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val victim = event.entity
        
        // 检查击杀者是否有权限
        if (!killer.hasPermission("killreward.receive")) {
            return
        }
        
        // 检查世界是否启用
        if (!plugin.configManager.isWorldEnabled(killer.world.name)) {
            plugin.messageManager.sendDebugMessage("world_check", mapOf(
                "world" to killer.world.name,
                "enabled" to false
            ))
            return
        }
        
        // 调试信息
        plugin.messageManager.sendDebugMessage("entity_killed", mapOf(
            "entity" to victim.type.name,
            "killer" to killer.name
        ))
        
        // 检查经济系统是否可用
        if (!plugin.economyManager.isEconomyAvailable()) {
            plugin.messageManager.sendMessage(killer, "economy.not_found")
            return
        }
        
        // 处理不同类型的实体
        when (victim.type.name) {
            "PLAYER" -> handlePlayerKill(killer, victim as Player)
            else -> handleEntityKill(killer, victim.type)
        }
    }
    
    private fun handlePlayerKill(killer: Player, victim: Player) {
        if (!plugin.configManager.isPlayerRewardEnabled()) {
            return
        }
        
        val percentage = plugin.configManager.getPlayerRewardPercentage()
        val minimumBalance = plugin.configManager.getPlayerMinimumBalance()
        val maximumSteal = plugin.configManager.getPlayerMaximumSteal()
        
        val rewardAmount = plugin.economyManager.calculatePlayerReward(
            victim, percentage, minimumBalance, maximumSteal
        )
        
        if (rewardAmount <= 0) {
            plugin.messageManager.sendMessage(killer, "economy.insufficient_funds")
            return
        }
        
        // 执行转账
        val transferSuccess = plugin.economyManager.transfer(victim, killer, rewardAmount)
        
        if (transferSuccess) {
            // 发送成功消息给击杀者
            plugin.messageManager.sendActionBar(killer, "reward.kill_player", mapOf(
                "player" to victim.name,
                "amount" to plugin.economyManager.formatCurrency(rewardAmount),
                "symbol" to "",
                "percentage" to "${percentage}%"
            ))
            
            // 发送失去金钱消息给被击杀者
            plugin.messageManager.sendActionBar(victim, "reward.player_killed_by", mapOf(
                "killer" to killer.name,
                "amount" to plugin.economyManager.formatCurrency(rewardAmount),
                "symbol" to "",
                "percentage" to "${percentage}%"
            ))
            
            // 调试信息
            plugin.messageManager.sendDebugMessage("reward_calculated", mapOf(
                "amount" to rewardAmount,
                "player" to killer.name
            ))
        } else {
            plugin.messageManager.sendMessage(killer, "economy.transaction_failed")
        }
    }
    
    private fun handleEntityKill(killer: Player, entityType: org.bukkit.entity.EntityType) {
        // 首先检查是否有特定实体配置
        val specificReward = plugin.configManager.getSpecificEntityReward(entityType)
        if (specificReward != null) {
            val rewardAmount = calculateReward(specificReward.first, specificReward.second)
            giveReward(killer, rewardAmount, "reward.kill_specific", mapOf("entity" to entityType.name))
            return
        }
        
        // 根据实体类别给予奖励
        val category = plugin.configManager.getEntityCategory(entityType)
        when (category) {
            ConfigManager.EntityCategory.MONSTER -> {
                if (plugin.configManager.isMonsterRewardEnabled()) {
                    val range = plugin.configManager.getMonsterRewardRange()
                    val rewardAmount = calculateReward(range.first, range.second)
                    giveReward(killer, rewardAmount, "reward.kill_monster")
                }
            }
            ConfigManager.EntityCategory.ANIMAL -> {
                if (plugin.configManager.isAnimalRewardEnabled()) {
                    val range = plugin.configManager.getAnimalRewardRange()
                    val rewardAmount = calculateReward(range.first, range.second)
                    giveReward(killer, rewardAmount, "reward.kill_animal")
                }
            }
            ConfigManager.EntityCategory.NEUTRAL -> {
                if (plugin.configManager.isNeutralRewardEnabled()) {
                    val range = plugin.configManager.getNeutralRewardRange()
                    val rewardAmount = calculateReward(range.first, range.second)
                    giveReward(killer, rewardAmount, "reward.kill_neutral")
                }
            }
            else -> {
                // 其他类型的实体不给予奖励
                return
            }
        }
    }
    
    private fun calculateReward(min: Double, max: Double): Double {
        return if (min == max) {
            min
        } else {
            Random.nextDouble(min, max)
        }
    }
    
    private fun giveReward(player: Player, amount: Double, messageKey: String, extraPlaceholders: Map<String, Any> = emptyMap()) {
        val success = plugin.economyManager.deposit(player, amount)
        
        if (success) {
            val placeholders = mutableMapOf<String, Any>(
                "amount" to plugin.economyManager.formatCurrency(amount),
                "symbol" to ""
            )
            placeholders.putAll(extraPlaceholders)
            
            plugin.messageManager.sendActionBar(player, messageKey, placeholders)
            
            // 调试信息
            plugin.messageManager.sendDebugMessage("reward_calculated", mapOf(
                "amount" to amount,
                "player" to player.name
            ))
        } else {
            plugin.messageManager.sendMessage(player, "economy.transaction_failed")
        }
    }
}