package me.deadybbb.customeffects

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
}
