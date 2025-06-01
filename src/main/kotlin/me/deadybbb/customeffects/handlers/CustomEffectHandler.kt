package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.types.Effect
import org.bukkit.entity.LivingEntity

/**
 * Base interface for handling custom effects applied to entities.
 */
interface CustomEffectHandler {
    /**
     * Applies the specified effect to the given entity.
     *
     * @param entity The living entity to apply the effect to.
     * @param effect The effect to apply.
     */
    fun applyEffect(entity: LivingEntity, effect: Effect)

    /**
     * Removes the specified effect from the given entity.
     * By default, does nothing for instant effects.
     *
     * @param entity The living entity to remove the effect from.
     * @param effect The effect to remove.
     */
    fun removeEffect(entity: LivingEntity, effect: Effect) { }
}

/**
 * Interface for handling instant effects, which are applied once and have no duration.
 */
interface InstantEffectHandler : CustomEffectHandler { }

/**
 * Interface for handling duration-based effects, which persist for a set period.
 */
interface DurationEffectHandler : CustomEffectHandler {
    /**
     * Removes the duration-based effect from the given entity.
     *
     * @param entity The living entity to remove the effect from.
     * @param effect The effect to remove.
     */
    override fun removeEffect(entity: LivingEntity, effect: Effect)
}

/**
 * Interface for handling periodic effects, which are applied repeatedly at intervals.
 */
interface PeriodicEffectHandler : CustomEffectHandler {
    /**
     * Removes the periodic effect from the given entity.
     *
     * @param entity The living entity to remove the effect from.
     * @param effect The effect to remove.
     */
    override fun removeEffect(entity: LivingEntity, effect: Effect)
}