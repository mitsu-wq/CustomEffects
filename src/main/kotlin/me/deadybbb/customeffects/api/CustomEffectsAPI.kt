package me.deadybbb.customeffects.api

import me.deadybbb.customeffects.types.Effect
import me.deadybbb.customeffects.types.EffectBehavior
import me.deadybbb.customeffects.types.EffectCategory
import org.bukkit.entity.LivingEntity
import org.bukkit.NamespacedKey

/**
 * Public API for interacting with the CustomEffects plugin.
 */
interface CustomEffectsAPI {
    /**
     * Retrieves a list of all registered effects.
     * @return List of registered effects.
     */
    fun getEffects(): List<Effect>

    /**
     * Retrieves an effect by its key.
     * @param key The key of the effect (e.g., "my_effect").
     * @return The effect if found, null otherwise.
     */
    fun getEffectByKey(key: String): Effect?

    /**
     * Applies an effect to a living entity.
     * @param entity The entity to apply the effect to.
     * @param effect The effect to apply.
     */
    fun applyEffect(entity: LivingEntity, effect: Effect)

    /**
     * Removes a specific effect from a living entity.
     * @param entity The entity to remove the effect from.
     * @param effect The effect to remove.
     */
    fun removeEffect(entity: LivingEntity, effect: Effect)

    /**
     * Clears all effects from a living entity.
     * @param entity The entity to clear effects from.
     */
    fun clearEffects(entity: LivingEntity)

    /**
     * Clears all effects from every online player.
     */
    fun clearEffects()

    /**
     * Retrieves the list of active effects on a living entity.
     * @param entity The entity to check.
     * @return List of active effects on the entity.
     */
    fun getActiveEffects(entity: LivingEntity): List<Effect>

    /**
     * Checks if a specific effect is active on a living entity.
     * @param entity The entity to check.
     * @param effect The effect to check for.
     * @return True if the effect is active, false otherwise.
     */
    fun isEffectActive(entity: LivingEntity, effect: Effect): Boolean

    /**
     * Reloads every effect from config for every online player.
     */
    fun reloadEffects()

    /**
     * Creates a new effect and saves it to the configuration using the effect type name.
     * @param name The unique name for the effect (e.g., "my_effect").
     * @param typeName The name of the effect type (e.g., "heart_fill").
     * @param stageTime The time before activation in ticks (must be positive for PERIODIC effects, otherwise >= 0).
     * @param duration The duration of the effect in ticks (-1 for infinite, 1 for INSTANT effects, positive for DURATION and PERIODIC).
     * @param amplifier The strength of the effect (must be positive or 0).
     * @param namespace The namespace for the effect key (default: "customeffect").
     * @return The created Effect if successful, null if the name already exists or parameters are invalid.
     * @throws IllegalArgumentException if the typeName is not found or parameters do not meet requirements.
     */
    fun createEffect(
        name: String,
        typeName: String,
        stageTime: Int,
        duration: Int,
        amplifier: Int,
        namespace: String = "customeffect"
    ): Effect?

    /**
     * Creates a new effect and saves it to the configuration using the effect type's NamespacedKey.
     * @param name The unique name for the effect (e.g., "my_effect").
     * @param typeKey The NamespacedKey of the effect type (e.g., "customeffect:heart_fill").
     * @param stageTime The time before activation in ticks (must be positive for PERIODIC effects, otherwise >= 0).
     * @param duration The duration of the effect in ticks (-1 for infinite, 1 for INSTANT effects, positive for DURATION and PERIODIC).
     * @param amplifier The strength of the effect (must be positive or 0).
     * @return The created Effect if successful, null if the name already exists or parameters are invalid.
     * @throws IllegalArgumentException if the typeKey is not found or parameters do not meet requirements.
     */
    fun createEffect(
        name: String,
        typeKey: NamespacedKey,
        stageTime: Int,
        duration: Int,
        amplifier: Int
    ): Effect?

    /**
     * Updates an existing effect in the configuration.
     * @param effectKey The key of the effect to update (e.g., "my_effect").
     * @param typeKey The NamespacedKey of the effect type (e.g., "customeffect:heart_fill").
     * @param stageTime The time before activation in ticks.
     * @param duration The duration of the effect in ticks.
     * @param amplifier The strength of the effect.
     * @return The updated Effect if successful, null if the effectKey does not exist or parameters are invalid.
     * @throws IllegalArgumentException if the typeKey is not found or parameters do not meet requirements.
     */
    fun updateEffect(
        effectKey: String,
        typeKey: NamespacedKey,
        stageTime: Int,
        duration: Int,
        amplifier: Int
    ): Effect?

    /**
     * Deletes an effect from the configuration.
     * @param effectKey The key of the effect to delete (e.g., "my_effect").
     * @return True if the effect was deleted, false if it did not exist.
     */
    fun deleteEffect(effectKey: String): Boolean

    /**
     * Creates a new custom effect type and saves it to the configuration.
     * @param typeKey The unique key for the effect type (e.g., "customeffect:my_type").
     * @param name The name of the effect type (e.g., "my_type").
     * @param namespace The namespace for the effect type (e.g., "customeffect").
     * @param category The category of the effect (BENEFICIAL, HARMFUL, NEUTRAL).
     * @param behavior The behavior of the effect (INSTANT, DURATION, PERIODIC).
     * @param handlerPath The fully qualified class path to the handler (e.g., "me.deadybbb.customeffects.handlers.HeartEffectHandler").
     * @return True if the effect type was created, false if the typeKey already exists.
     * @throws IllegalArgumentException if parameters are invalid or handler cannot be loaded.
     */
    fun createEffectType(
        typeKey: String,
        name: String,
        namespace: String,
        category: EffectCategory,
        behavior: EffectBehavior,
        handlerPath: String
    ): Boolean

    /**
     * Updates an existing custom effect type in the configuration.
     * @param typeKey The key of the effect type to update (e.g., "customeffect:my_type").
     * @param name The name of the effect type (e.g., "my_type").
     * @param namespace The namespace for the effect type (e.g., "customeffect").
     * @param category The category of the effect (BENEFICIAL, HARMFUL, NEUTRAL).
     * @param behavior The behavior of the effect (INSTANT, DURATION, PERIODIC).
     * @param handlerPath The fully qualified class path to the handler (e.g., "me.deadybbb.customeffects.handlers.HeartEffectHandler").
     * @return True if the effect type was updated, false if the typeKey does not exist.
     * @throws IllegalArgumentException if parameters are invalid or handler cannot be loaded.
     */
    fun updateEffectType(
        typeKey: String,
        name: String,
        namespace: String,
        category: EffectCategory,
        behavior: EffectBehavior,
        handlerPath: String
    ): Boolean

    /**
     * Deletes a custom effect type from the configuration.
     * @param typeKey The key of the effect type to delete (e.g., "customeffect:my_type").
     * @return True if the effect type was deleted, false if it did not exist.
     */
    fun deleteEffectType(typeKey: String): Boolean
}