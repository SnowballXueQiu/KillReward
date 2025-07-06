package cc.vastsea.killreward

import cc.vastsea.killreward.commands.KillRewardCommand
import cc.vastsea.killreward.config.ConfigManager
import cc.vastsea.killreward.economy.EconomyManager
import cc.vastsea.killreward.listeners.EntityDeathListener
import cc.vastsea.killreward.utils.MessageManager
import org.bukkit.plugin.java.JavaPlugin

class KillRewardPlugin : JavaPlugin() {
    
    companion object {
        lateinit var instance: KillRewardPlugin
            private set
    }
    
    lateinit var configManager: ConfigManager
        private set
    
    lateinit var messageManager: MessageManager
        private set
    
    lateinit var economyManager: EconomyManager
        private set
    
    override fun onEnable() {
        instance = this
        
        // 初始化配置管理器
        configManager = ConfigManager(this)
        
        // 初始化消息管理器
        messageManager = MessageManager(this)
        
        // 初始化经济管理器
        economyManager = EconomyManager(this)
        
        // 加载配置
        if (!loadConfigurations()) {
            logger.severe("Failed to load configurations, disabling plugin...")
            server.pluginManager.disablePlugin(this)
            return
        }
        
        // 设置经济系统
        if (!economyManager.setupEconomy()) {
            logger.severe("Economy plugin not found! Please install Vault and an economy plugin.")
            server.pluginManager.disablePlugin(this)
            return
        }
        
        // 注册事件监听器
        registerEvents()
        
        // 注册命令
        registerCommands()
        
        // 插件启用完成
        logger.info("Plugin is ready!")
        logger.info("KillReward v${description.version} enabled!")
    }
    
    override fun onDisable() {
        logger.info(messageManager.getMessage("plugin.disabled"))
    }
    
    private fun loadConfigurations(): Boolean {
        return try {
            logger.info("Loading configuration file...")
            configManager.loadConfig()
            logger.info("Configuration file loaded successfully!")
            
            logger.info("Loading language file: ${configManager.getLanguage()}")
            messageManager.loadLanguage(configManager.getLanguage())
            logger.info("Language file loaded successfully!")
            
            true
        } catch (e: Exception) {
            logger.severe("Error loading configurations: ${e.message}")
            e.printStackTrace()
            false
        }
    }
    
    private fun registerEvents() {
        logger.info("Registering event listeners...")
        server.pluginManager.registerEvents(EntityDeathListener(this), this)
        logger.info("Event listeners registered successfully!")
    }
    
    private fun registerCommands() {
        getCommand("killreward")?.setExecutor(KillRewardCommand(this))
    }
    
    fun reloadConfigurations(): Boolean {
        return try {
            configManager.reloadConfig()
            messageManager.loadLanguage(configManager.getLanguage())
            true
        } catch (e: Exception) {
            logger.severe("Error reloading configurations: ${e.message}")
            false
        }
    }
}