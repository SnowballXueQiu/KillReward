package cc.vastsea.killreward.commands

import cc.vastsea.killreward.KillRewardPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class KillRewardCommand(private val plugin: KillRewardPlugin) : CommandExecutor, TabCompleter {
    
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            showHelp(sender)
            return true
        }
        
        when (args[0].lowercase()) {
            "reload" -> {
                if (!sender.hasPermission("killreward.admin")) {
                    plugin.messageManager.sendMessage(sender as? Player ?: return true, "plugin.no_permission")
                    return true
                }
                
                reloadConfig(sender)
            }
            "help" -> {
                showHelp(sender)
            }
            "info" -> {
                showInfo(sender)
            }
            "version" -> {
                showVersion(sender)
            }
            else -> {
                if (sender is Player) {
                    plugin.messageManager.sendMessage(sender, "plugin.invalid_command")
                } else {
                    sender.sendMessage(plugin.messageManager.getMessage("plugin.invalid_command"))
                }
            }
        }
        
        return true
    }
    
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            val subcommands = mutableListOf("help", "info", "version")
            if (sender.hasPermission("killreward.admin")) {
                subcommands.add("reload")
            }
            return subcommands.filter { it.startsWith(args[0].lowercase()) }
        }
        return emptyList()
    }
    
    private fun reloadConfig(sender: CommandSender) {
        try {
            val success = plugin.reloadConfigurations()
            if (success) {
                if (sender is Player) {
                    plugin.messageManager.sendMessage(sender, "plugin.reloaded")
                } else {
                    sender.sendMessage(plugin.messageManager.getMessage("plugin.reloaded"))
                }
            } else {
                if (sender is Player) {
                    plugin.messageManager.sendMessage(sender, "error.config_load_failed", mapOf("error" to "Unknown error"))
                } else {
                    sender.sendMessage(plugin.messageManager.getMessage("error.config_load_failed", mapOf("error" to "Unknown error")))
                }
            }
        } catch (e: Exception) {
            if (sender is Player) {
                plugin.messageManager.sendMessage(sender, "error.config_load_failed", mapOf("error" to e.message.toString()))
            } else {
                sender.sendMessage(plugin.messageManager.getMessage("error.config_load_failed", mapOf("error" to e.message.toString())))
            }
        }
    }
    
    private fun showHelp(sender: CommandSender) {
        val messages = listOf(
            plugin.messageManager.getMessage("commands.help.header"),
            plugin.messageManager.getMessage("commands.help.help"),
            plugin.messageManager.getMessage("commands.help.reload"),
            plugin.messageManager.getMessage("commands.help.footer")
        )
        
        if (sender is Player) {
            messages.forEach { sender.sendMessage(it) }
        } else {
            messages.forEach { sender.sendMessage(it) }
        }
    }
    
    private fun showInfo(sender: CommandSender) {
        val info = listOf(
            "&6========== KillReward Info ==========",
            "&ePlugin: &fKillReward v${plugin.description.version}",
            "&eAuthor: &f${plugin.description.authors.joinToString(", ")}",
            "&eLanguage: &f${plugin.messageManager.getCurrentLanguage()}",
            "&eEconomy: &f${plugin.economyManager.getEconomyName()}",
            "&eDebug: &f${plugin.configManager.isDebugEnabled()}",
            "&6====================================="
        )
        
        info.forEach { message ->
            val coloredMessage = plugin.messageManager.getMessage("").let {
                // 手动处理颜色代码
                org.bukkit.ChatColor.translateAlternateColorCodes('&', message)
            }
            
            if (sender is Player) {
                sender.sendMessage(coloredMessage)
            } else {
                sender.sendMessage(org.bukkit.ChatColor.stripColor(coloredMessage) ?: message)
            }
        }
    }
    
    private fun showVersion(sender: CommandSender) {
        val version = "&aKillReward &fv${plugin.description.version} &7by &e${plugin.description.authors.joinToString(", ")}"
        val coloredMessage = org.bukkit.ChatColor.translateAlternateColorCodes('&', version)
        
        if (sender is Player) {
            sender.sendMessage(coloredMessage)
        } else {
            sender.sendMessage(org.bukkit.ChatColor.stripColor(coloredMessage) ?: version)
        }
    }
}