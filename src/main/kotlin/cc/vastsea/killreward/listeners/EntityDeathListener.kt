package cc.vastsea.killreward.listeners

import cc.vastsea.killreward.KillRewardPlugin
import cc.vastsea.killreward.config.ConfigManager
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class EntityDeathListener(private val plugin: KillRewardPlugin) : Listener {
    
    private val rewardQueue = ConcurrentHashMap<Player, Double>()
    private val killCounts = ConcurrentHashMap<Player, Int>()
    private val lastKillTime = ConcurrentHashMap<Player, Long>()
    
    init {
        // 定期处理奖励队列，延长处理间隔到30秒
        object : BukkitRunnable() {
            override fun run() {
                processRewardQueue()
            }
        }.runTaskTimerAsynchronously(plugin, 600L, 600L) // 30秒处理一次
    }
    
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
        
        // 检查击杀频率
        if (isKillRateExcessive(killer)) {
            plugin.messageManager.sendDebugMessage("kill_rate_excessive", mapOf(
                "player" to killer.name
            ))
            return
        }
        
        // 更新击杀统计
        updateKillStats(killer)
        
        // 处理击杀奖励
        if (victim is Player) {
            handlePlayerKill(killer, victim)
        } else {
            handleEntityKill(killer, victim.type)
        }
    }
    
    private fun isKillRateExcessive(player: Player): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastTime = lastKillTime.getOrDefault(player, 0L)
        val timeDiff = currentTime - lastTime
        
        // 如果在1秒内击杀超过5次，认为是刷怪塔
        return killCounts.getOrDefault(player, 0) >= 5 && timeDiff <= 1000
    }
    
    private fun updateKillStats(player: Player) {
        val currentTime = System.currentTimeMillis()
        val lastTime = lastKillTime.getOrDefault(player, 0L)
        
        if (currentTime - lastTime > 1000) {
            // 重置计数
            killCounts.put(player, 1)
        } else {
            // 增加计数
            killCounts.merge(player, 1) { old, _ -> old + 1 }
        }
        
        lastKillTime.put(player, currentTime)
    }
    
    private fun processRewardQueue() {
        if (rewardQueue.isEmpty()) return
        
        // 完全异步处理奖励
        object : BukkitRunnable() {
            override fun run() {
                val iterator = rewardQueue.entries.iterator()
                while (iterator.hasNext()) {
                    val (player, amount) = iterator.next()
                    if (player.isOnline) {
                        val success = plugin.economyManager.deposit(player, amount)
                        if (success) {
                            // 在主线程发送消息
                            object : BukkitRunnable() {
                                override fun run() {
                                    plugin.messageManager.sendMessage(player, "reward.batch_received", mapOf(
                                        "amount" to plugin.economyManager.formatCurrency(amount)
                                    ))
                                }
                            }.runTask(plugin)
                        }
                    }
                    iterator.remove()
                }
            }
        }.runTaskAsynchronously(plugin)
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
        
        queueReward(killer, rewardAmount)
    }
    
    private fun handleEntityKill(killer: Player, entityType: org.bukkit.entity.EntityType) {
        // 首先检查是否有特定实体配置
        val specificReward = plugin.configManager.getSpecificEntityReward(entityType)
        if (specificReward != null) {
            val rewardAmount = calculateReward(specificReward.first, specificReward.second)
            queueReward(killer, rewardAmount)
            return
        }
        
        // 根据实体类别给予奖励
        val category = plugin.configManager.getEntityCategory(entityType)
        when (category) {
            ConfigManager.EntityCategory.MONSTER -> {
                if (plugin.configManager.isMonsterRewardEnabled()) {
                    val range = plugin.configManager.getMonsterRewardRange()
                    val rewardAmount = calculateReward(range.first, range.second)
                    queueReward(killer, rewardAmount)
                }
            }
            ConfigManager.EntityCategory.ANIMAL -> {
                if (plugin.configManager.isAnimalRewardEnabled()) {
                    val range = plugin.configManager.getAnimalRewardRange()
                    val rewardAmount = calculateReward(range.first, range.second)
                    queueReward(killer, rewardAmount)
                }
            }
            else -> return
        }
    }
    
    private fun calculateReward(min: Double, max: Double): Double {
        return if (min == max) {
            min
        } else {
            Random.nextDouble(min, max)
        }
    }
    
    private fun queueReward(player: Player, amount: Double) {
        rewardQueue.merge(player, amount) { old, new -> old + new }
    }
}