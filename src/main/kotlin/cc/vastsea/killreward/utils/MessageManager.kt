package cc.vastsea.killreward.utils

import cc.vastsea.killreward.KillRewardPlugin
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.io.InputStreamReader

class MessageManager(private val plugin: KillRewardPlugin) {
    
    private lateinit var languageConfig: FileConfiguration
    private var currentLanguage: String = "zh_CN"
    
    fun loadLanguage(language: String) {
        currentLanguage = language
        val languageFile = File(plugin.dataFolder, "langs/$language.yml")
        
        // 如果语言文件不存在，从资源中复制
        if (!languageFile.exists()) {
            plugin.saveResource("langs/$language.yml", false)
        }
        
        // 加载语言文件
        if (languageFile.exists()) {
            languageConfig = YamlConfiguration.loadConfiguration(languageFile)
            
            // 从jar中加载默认配置作为备用
            val defaultConfigStream = plugin.getResource("langs/$language.yml")
            if (defaultConfigStream != null) {
                val defaultConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultConfigStream, "UTF-8"))
                languageConfig.setDefaults(defaultConfig)
            }
        } else {
            // 如果文件仍然不存在，使用默认的中文配置
            val defaultStream = plugin.getResource("langs/zh_CN.yml")
            if (defaultStream != null) {
                languageConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream, "UTF-8"))
            } else {
                throw RuntimeException("Cannot load any language file!")
            }
        }
    }
    
    fun getMessage(key: String, placeholders: Map<String, Any> = emptyMap()): String {
        var message = languageConfig.getString(key, key) ?: key
        
        // 替换占位符
        for ((placeholder, value) in placeholders) {
            message = message.replace("{$placeholder}", value.toString())
        }
        
        // 处理颜色代码
        return ChatColor.translateAlternateColorCodes('&', message)
    }
    
    fun getConsoleMessage(key: String, placeholders: Map<String, Any> = emptyMap()): String {
        val fullKey = "console.$key"
        var message = languageConfig.getString(fullKey, key) ?: key
        
        // 替换占位符
        for ((placeholder, value) in placeholders) {
            message = message.replace("{$placeholder}", value.toString())
        }
        
        // 控制台消息不需要颜色代码
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message)) ?: message
    }
    
    fun sendMessage(player: Player, key: String, placeholders: Map<String, Any> = emptyMap()) {
        val message = getMessage(key, placeholders)
        if (message.isNotEmpty() && message != key) {
            player.sendMessage(message)
        }
    }
    
    fun sendConsoleMessage(key: String, placeholders: Map<String, Any> = emptyMap()) {
        val message = getConsoleMessage(key, placeholders)
        if (message.isNotEmpty()) {
            plugin.logger.info(message)
        }
    }
    
    fun sendDebugMessage(key: String, placeholders: Map<String, Any> = emptyMap()) {
        if (plugin.configManager.isDebugEnabled()) {
            val fullKey = "debug.$key"
            val message = getConsoleMessage(fullKey, placeholders)
            if (message.isNotEmpty()) {
                plugin.logger.info("[DEBUG] $message")
            }
        }
    }
    
    fun formatCurrency(amount: Double): String {
        return String.format("%.2f", amount)
    }
    
    fun getCurrentLanguage(): String {
        return currentLanguage
    }
}