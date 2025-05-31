package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.CustomEffects
import me.deadybbb.customeffects.Effect
import me.deadybbb.customeffects.types.CustomEffectType
import me.deadybbb.customeffects.types.EffectTypes
import me.deadybbb.customeffects.types.WrappedPotionEffectType
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask

class EffectsManager(
    val plugin: CustomEffects
) {
    private val activeEffects = mutableMapOf<LivingEntity, MutableMap<Effect, BukkitTask>>()

    fun isOnEntity(entity: LivingEntity, effect: Effect): Boolean {
        return activeEffects[entity]?.containsKey(effect) ?: false
    }

    fun getActiveEffects(entity: LivingEntity): List<Effect> {
        return activeEffects[entity]?.keys?.toList() ?: emptyList()
    }

    fun applyEffect(entity: LivingEntity, effect: Effect) {
        removeEffect(entity, effect)

        val task = plugin.server.scheduler.runTaskLater(plugin, Runnable {
            when (effect.type) {
                is WrappedPotionEffectType -> {
                    val potionType = PotionEffectType.getByKey(effect.type.getKey())
                    if (potionType != null) {
                        entity.addPotionEffect(PotionEffect(potionType, if (effect.type.isInstant()) 1 else effect.duration, effect.amplifier - 1))
                    } else {
                        plugin.logger.warning("Potion effect type ${effect.type.getKey()} not found")
                    }
                }
                is CustomEffectType -> {
                    EffectTypes.getHandler(effect.type)?.applyEffect(entity, effect)
                        ?: plugin.logger.warning("No handlers found for custom effect ${effect.type.getKey()}")
                }
            }

            if (!effect.type.isInstant()) {
                val removalTask = plugin.server.scheduler.runTaskLater(plugin, Runnable {
                    removeEffect(entity, effect)
                }, effect.duration.toLong())
                activeEffects.getOrPut(entity) { mutableMapOf() }[effect] = removalTask
            }
        }, effect.stageTime.toLong())

        activeEffects.getOrPut(entity) { mutableMapOf() }[effect] = task
    }

    fun removeEffect(entity: LivingEntity, effect: Effect) {
        activeEffects[entity]?.get(effect)?.cancel()
        activeEffects[entity]?.remove(effect)

        when (effect.type) {
            is WrappedPotionEffectType -> {
                val potionType = PotionEffectType.getByKey(effect.type.getKey())
                if (potionType != null) {
                    entity.removePotionEffect(potionType)
                }
            }
            is CustomEffectType -> {
                EffectTypes.getHandler(effect.type)?.removeEffect(entity, effect)
            }
        }
    }

    fun clearEffects(entity: LivingEntity) {
        activeEffects[entity]?.forEach { (effect, task) ->
            task.cancel()
            when (effect.type) {
                is WrappedPotionEffectType -> {
                    val potionType = PotionEffectType.getByKey(effect.type.getKey())
                    if (potionType != null) {
                        entity.removePotionEffect(potionType)
                    }
                }
                is CustomEffectType -> {
                    EffectTypes.getHandler(effect.type)?.removeEffect(entity, effect)
                }
            }
        }
        activeEffects.remove(entity)
    }
}