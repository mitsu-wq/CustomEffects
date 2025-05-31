package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.CustomEffects
import me.deadybbb.customeffects.types.Effect
import me.deadybbb.customeffects.EffectsConfigHandler
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity

class EffectsHandler(
    private val plugin: CustomEffects
) {
    private val config = EffectsConfigHandler(plugin)
    private var effects = config.loadEffectsFromConfig()
    private val manager = EffectsManager(plugin)

    fun getEffects(): List<Effect> = effects

    fun getEffectByKey(key: String): Effect? {
        return effects.find { it.getKey().key == key }
    }

    fun reloadEffects() {
        val newEffects = config.loadEffectsFromConfig()

        plugin.server.onlinePlayers.forEach { player ->
            val activeEffects = manager.getActiveEffects(player)

            activeEffects.forEach { activeEffect ->
                if (newEffects.none { it.getKey() == activeEffect.getKey() }) {
                    manager.removeEffect(player, activeEffect)
                }
            }

            activeEffects.forEach { activeEffects ->
                newEffects.find { it.getKey() == activeEffects.getKey() }?.let { newEffect ->
                    if (!Bukkit.isPrimaryThread()) {
                        Bukkit.getScheduler().runTask(plugin, Runnable {
                            manager.applyEffect(player, newEffect)
                        })
                    } else {
                        manager.applyEffect(player, newEffect)
                    }
                }
            }
        }

        effects = newEffects
    }

    fun applyEffect(entity: LivingEntity, effect: Effect) {
        manager.applyEffect(entity, effect)
    }

    fun clearEffects() {
        plugin.server.onlinePlayers.forEach {
            manager.clearEffects(it)
        }
        effects = listOf()
    }
}