package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.CustomEffects
import me.deadybbb.customeffects.types.CustomEffectType
import me.deadybbb.customeffects.types.Effect
import me.deadybbb.customeffects.types.EffectBehavior
import me.deadybbb.customeffects.types.EffectTypesRegistry
import me.deadybbb.customeffects.types.WrappedPotionEffectType
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitTask

/**
 * Manages the application and removal of custom and potion effects on entities.
 */
class EffectsManager(
    private val plugin: CustomEffects
) {
    private val activeEffects = mutableMapOf<LivingEntity, MutableMap<Effect, BukkitTask>>()
    private val removeTasks = mutableMapOf<LivingEntity, MutableMap<Effect, BukkitTask>>()

    /**
     * Checks if the specified effect is active on the given entity.
     *
     * @param entity The living entity to check.
     * @param effect The effect to check for.
     * @return True if the effect is active, false otherwise.
     */
    fun isOnEntity(entity: LivingEntity, effect: Effect): Boolean {
        return activeEffects[entity]?.containsKey(effect) ?: false
    }

    /**
     * Retrieves all active effects on the given entity.
     *
     * @param entity The living entity to check.
     * @return List of active effects.
     */
    fun getActiveEffects(entity: LivingEntity): List<Effect> {
        return activeEffects[entity]?.keys?.toList() ?: emptyList()
    }

    /**
     * Applies the specified effect to the given entity.
     *
     * @param entity The living entity to apply the effect to.
     * @param effect The effect to apply.
     */
    fun applyEffect(entity: LivingEntity, effect: Effect) {
        if (!entity.isValid) {
            plugin.logger.warning("Cannot apply effect '${effect.getKey()}' to invalid entity")
            return
        }
        removeEffect(entity, effect)

        val task = plugin.server.scheduler.runTaskLater(plugin, Runnable {
            when (effect.type) {
                is WrappedPotionEffectType -> applyPotionEffect(entity, effect)
                is CustomEffectType -> applyCustomEffect(entity, effect)
            }
        }, effect.stageTime.toLong())

        if (effect.type.getBehavior() == EffectBehavior.DURATION) {
            activeEffects.getOrPut(entity) { mutableMapOf() }[effect] = task
        }
    }

    /**
     * Removes the specified effect from the given entity.
     *
     * @param entity The living entity to remove the effect from.
     * @param effect The effect to remove.
     */
    fun removeEffect(entity: LivingEntity, effect: Effect) {
        activeEffects[entity]?.get(effect)?.cancel()
        activeEffects[entity]?.remove(effect)
        removeTasks[entity]?.get(effect)?.cancel()
        removeTasks[entity]?.remove(effect)

        when (effect.type) {
            is WrappedPotionEffectType -> {
                val potionType = PotionEffectType.getByKey(effect.type.getKey())
                if (potionType != null) {
                    entity.removePotionEffect(potionType)
                } else {
                    plugin.logger.warning("Potion effect type '${effect.type.getKey()}' not found")
                }
            }
            is CustomEffectType -> {
                val behavior = effect.type.getBehavior()
                if (behavior == EffectBehavior.DURATION || behavior == EffectBehavior.PERIODIC) {
                    EffectTypesRegistry.getHandler(effect.type)?.removeEffect(entity, effect)
                        ?: plugin.logger.warning("No handler found for custom effect '${effect.type.getKey()}'")
                }
            }
        }
    }

    /**
     * Clears all active effects from the given entity.
     *
     * @param entity The living entity to clear effects from.
     */
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
                    val behavior = effect.type.getBehavior()
                    if (behavior == EffectBehavior.DURATION || behavior == EffectBehavior.PERIODIC) {
                        EffectTypesRegistry.getHandler(effect.type)?.removeEffect(entity, effect)
                    }
                }
            }
        }
        removeTasks[entity]?.forEach { (_, task) -> task.cancel() }
        activeEffects.remove(entity)
        removeTasks.remove(entity)
    }

    private fun applyPotionEffect(entity: LivingEntity, effect: Effect) {
        val potionType = PotionEffectType.getByKey(effect.type.getKey())
        if (potionType == null) {
            plugin.logger.warning("Potion effect type '${effect.type.getKey()}' not found")
            return
        }
        entity.addPotionEffect(PotionEffect(
            potionType,
            when {
                effect.type.getBehavior() == EffectBehavior.INSTANT -> 1
                effect.isInfinite() -> Int.MAX_VALUE
                else -> effect.duration
            },
            effect.amplifier - 1
        ))
        if (!effect.isInfinite()) {
            val removeTask = plugin.server.scheduler.runTaskLater(
                plugin,
                Runnable { removeEffect(entity, effect) },
                effect.duration.toLong()
            )
            removeTasks.getOrPut(entity) { mutableMapOf() }[effect] = removeTask
        }
    }

    private fun applyCustomEffect(entity: LivingEntity, effect: Effect) {
        val handler = EffectTypesRegistry.getHandler(effect.type) ?: run {
            plugin.logger.warning("No handler found for custom effect '${effect.type.getKey()}'")
            return
        }
        when (effect.type.getBehavior()) {
            EffectBehavior.INSTANT -> handler.applyEffect(entity, effect)
            EffectBehavior.DURATION -> {
                handler.applyEffect(entity, effect)
                val removeTask = plugin.server.scheduler.runTaskLater(
                    plugin,
                    Runnable { removeEffect(entity, effect) },
                    effect.duration.toLong()
                )
                removeTasks.getOrPut(entity) { mutableMapOf() }[effect] = removeTask
            }
            EffectBehavior.PERIODIC -> {
                val periodicTask = plugin.server.scheduler.runTaskTimer(
                    plugin,
                    Runnable { handler.applyEffect(entity, effect) },
                    0L,
                    effect.stageTime.toLong()
                )
                activeEffects.getOrPut(entity) { mutableMapOf() }[effect] = periodicTask
                if (!effect.isInfinite()) {
                    val removeTask = plugin.server.scheduler.runTaskLater(
                        plugin,
                        Runnable { removeEffect(entity, effect) },
                        effect.duration.toLong()
                    )
                    removeTasks.getOrPut(entity) { mutableMapOf() }[effect] = removeTask
                }
            }
        }
    }
}