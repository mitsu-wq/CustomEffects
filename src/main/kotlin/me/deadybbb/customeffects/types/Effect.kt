package me.deadybbb.customeffects.types

import net.kyori.adventure.translation.Translatable
import org.bukkit.NamespacedKey
import org.jetbrains.annotations.NotNull

/**
 * Represents a custom effect with a specific type, duration, and strength.
 *
 * @property name Unique identifier for the effect (e.g., "my_effect").
 * @property type The type of effect (e.g., heart_fill, speed).
 * @property stageTime Time before activation in ticks (must be non-negative).
 * @property duration Duration of the effect in ticks (-1 for infinite, 1 for INSTANT effects).
 * @property amplifier Strength of the effect (must be non-negative).
 * @property namespace Namespace for the effect key (default: "customeffect").
 */
@ConsistentCopyVisibility
data class Effect private constructor(
    val name: String,
    val type: EffectType,
    val stageTime: Int,
    val duration: Int,
    val amplifier: Int,
    val namespace: String = "customeffect"
) : Translatable {
    init {
        validateParameters()
    }

    /**
     * Returns the unique key of the effect.
     * @return NamespacedKey representing the effect.
     */
    @NotNull
    fun getKey(): NamespacedKey = NamespacedKey(namespace, name.lowercase())

    /**
     * Returns the key of the effect type.
     * @return NamespacedKey of the effect type.
     */
    @NotNull
    fun getTypeKey(): NamespacedKey = type.getKey()

    /**
     * Checks if the effect has infinite duration.
     * @return True if duration is -1, false otherwise.
     */
    @NotNull
    fun isInfinite(): Boolean = duration == -1

    /**
     * Returns the translation key for the effect.
     * @return Translation key in the format "effect.<namespace>.<id>".
     */
    @NotNull
    override fun translationKey(): String = "effect.$namespace.${name.lowercase()}"

    override fun equals(other: Any?): Boolean = other is Effect && getKey() == other.getKey()

    override fun hashCode(): Int = getKey().hashCode()

    override fun toString(): String = "Effect[key=${getKey()}, name=$name]"

    private fun validateParameters() {
        require(name.matches(Regex("[a-z0-9_]+"))) { "Effect ID must contain only lowercase letters, numbers, or underscores" }
        require(namespace.matches(Regex("[a-z0-9_]+"))) { "Namespace must contain only lowercase letters, numbers, or underscores" }
        require(stageTime >= 0) { "stageTime must be non-negative" }
        require(amplifier >= 0) { "amplifier must be non-negative" }
        when (type.getBehavior()) {
            EffectBehavior.INSTANT -> require(duration == 1) { "INSTANT effects must have duration of 1" }
            EffectBehavior.DURATION, EffectBehavior.PERIODIC -> require(duration >= -1) {
                "DURATION and PERIODIC effects must have duration of -1 (infinite) or positive"
            }
        }
        if (type.getBehavior() == EffectBehavior.PERIODIC) {
            require(stageTime > 0) { "PERIODIC effects must have positive stageTime" }
        }
    }

    companion object {
        /**
         * Creates a new Effect instance.
         *
         * @param name Unique identifier for the effect (e.g., "my_effect").
         * @param type The type of effect.
         * @param stageTime Time before activation in ticks (positive for PERIODIC, non-negative otherwise).
         * @param duration Duration in ticks (-1 for infinite, 1 for INSTANT, positive for DURATION/PERIODIC).
         * @param amplifier Strength of the effect (non-negative).
         * @param namespace Namespace for the effect key (default: "customeffect").
         * @return A new Effect instance.
         * @throws IllegalArgumentException if parameters are invalid or type is not found.
         */
        fun create(
            name: String,
            type: EffectType,
            stageTime: Int,
            duration: Int,
            amplifier: Int = 1,
            namespace: String = "customeffect"
        ): Effect {
            val adjustedDuration = if (type.getBehavior() == EffectBehavior.INSTANT) 1 else duration
            return Effect(name, type, stageTime, adjustedDuration, amplifier, namespace)
        }

        /**
         * Creates a new Effect instance using the effect type's NamespacedKey.
         *
         * @param name Unique identifier for the effect (e.g., "my_effect").
         * @param typeKey NamespacedKey of the effect type (e.g., "customeffect:heart_fill").
         * @param stageTime Time before activation in ticks.
         * @param duration Duration in ticks.
         * @param amplifier Strength of the effect.
         * @param namespace Namespace for the effect key.
         * @return A new Effect instance.
         * @throws IllegalArgumentException if typeKey is not found or parameters are invalid.
         */
        fun create(
            name: String,
            typeKey: NamespacedKey,
            stageTime: Int,
            duration: Int,
            amplifier: Int = 1,
            namespace: String = "customeffect"
        ): Effect {
            val type = EffectTypesRegistry.getByKey(typeKey)
                ?: throw IllegalArgumentException("Effect type with key '$typeKey' not found")
            return create(name, type, stageTime, duration, amplifier, namespace)
        }

        /**
         * Creates a new Effect instance using the effect type's name.
         *
         * @param name Unique identifier for the effect (e.g., "my_effect").
         * @param typeName Name of the effect type (e.g., "heart_fill").
         * @param stageTime Time before activation in ticks.
         * @param duration Duration in ticks.
         * @param amplifier Strength of the effect.
         * @param namespace Namespace for the effect key.
         * @return A new Effect instance.
         * @throws IllegalArgumentException if typeName is not found or parameters are invalid.
         */
        fun create(
            name: String,
            typeName: String,
            stageTime: Int,
            duration: Int,
            amplifier: Int = 1,
            namespace: String = "customeffect"
        ): Effect {
            val type = EffectTypesRegistry.getByName(typeName)
                ?: throw IllegalArgumentException("Effect type with name '$typeName' not found")
            return create(name, type, stageTime, duration, amplifier, namespace)
        }
    }
}