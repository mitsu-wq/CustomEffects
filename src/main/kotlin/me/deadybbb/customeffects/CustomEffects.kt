package me.deadybbb.customeffects

import me.deadybbb.customeffects.api.CustomEffectsAPI
import me.deadybbb.customeffects.handlers.EffectsHandler
import me.deadybbb.ybbbbasicmodule.BasicLoggerHandler
import org.bukkit.plugin.java.JavaPlugin

class CustomEffects : JavaPlugin() {
    val logger by lazy { BasicLoggerHandler(this) }
    val handler by lazy { EffectsHandler(this) }

    override fun onEnable() {
        handler.reloadEffects()
        CustomEffectsCommand(this).registerCommand()
    }

    override fun onDisable() {
        handler.clearEffects()
    }

    companion object {
        /**
         * Retrieves the instance of the CustomEffectsAPI.
         * @return The CustomEffectsAPI instance, or null if the plugin is not enabled.
         */
        fun getAPI(): CustomEffectsAPI? {
            val plugin = getPlugin(CustomEffects::class.java)
            return if (plugin.isEnabled) plugin.handler else null
        }
    }
}
