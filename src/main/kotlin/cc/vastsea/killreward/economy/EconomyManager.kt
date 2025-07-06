package cc.vastsea.killreward.economy

import cc.vastsea.killreward.KillRewardPlugin
import net.milkbowl.vault.economy.Economy
import org.bukkit.entity.Player
import org.bukkit.plugin.RegisteredServiceProvider

class EconomyManager(private val plugin: KillRewardPlugin) {
    
    private var economy: Economy? = null
    
    fun setupEconomy(): Boolean {
        if (plugin.server.pluginManager.getPlugin("Vault") == null) {
            plugin.logger.severe("Vault plugin not found!")
            return false
        }
        
        plugin.logger.info("Hooking into Vault...")
        
        val rsp: RegisteredServiceProvider<Economy>? = plugin.server.servicesManager.getRegistration(Economy::class.java)
        if (rsp == null) {
            plugin.logger.severe("No economy plugin found!")
            return false
        }
        
        economy = rsp.provider
        plugin.logger.info("Successfully hooked into Vault!")
        plugin.logger.info("Economy plugin found: ${economy?.name ?: "Unknown"}")
        
        return economy != null
    }
    
    fun getBalance(player: Player): Double {
        return economy?.getBalance(player) ?: 0.0
    }
    
    fun hasEnough(player: Player, amount: Double): Boolean {
        return economy?.has(player, amount) ?: false
    }
    
    fun deposit(player: Player, amount: Double): Boolean {
        val response = economy?.depositPlayer(player, amount)
        return response?.transactionSuccess() ?: false
    }
    
    fun withdraw(player: Player, amount: Double): Boolean {
        val response = economy?.withdrawPlayer(player, amount)
        return response?.transactionSuccess() ?: false
    }
    
    fun transfer(from: Player, to: Player, amount: Double): Boolean {
        if (!hasEnough(from, amount)) {
            return false
        }
        
        val withdrawSuccess = withdraw(from, amount)
        if (!withdrawSuccess) {
            return false
        }
        
        val depositSuccess = deposit(to, amount)
        if (!depositSuccess) {
            // 如果存款失败，退还金额给原玩家
            deposit(from, amount)
            return false
        }
        
        return true
    }
    
    fun calculatePlayerReward(victim: Player, percentage: Double, minimumBalance: Double, maximumSteal: Double): Double {
        val victimBalance = getBalance(victim)
        
        // 检查最小余额要求
        if (victimBalance < minimumBalance) {
            return 0.0
        }
        
        // 计算百分比奖励
        val calculatedAmount = victimBalance * (percentage / 100.0)
        
        // 应用最大偷取限制
        return minOf(calculatedAmount, maximumSteal)
    }
    
    fun isEconomyAvailable(): Boolean {
        return economy != null
    }
    
    fun getEconomyName(): String {
        return economy?.name ?: "None"
    }
    
    fun formatCurrency(amount: Double): String {
        return economy?.format(amount) ?: plugin.messageManager.formatCurrency(amount)
    }
}