package me.deadybbb.customeffects.types

import net.kyori.adventure.translation.Translatable
import org.bukkit.NamespacedKey
import org.jetbrains.annotations.NotNull

class CustomEffectType private constructor(
    private val name: String,
    private val category: EffectCategory,
    private val behavior: EffectBehavior,
    private val namespace: String = "customeffect"
) : EffectType, Translatable {

    init {
        validateParameters()
    }

    @NotNull
    override fun getKey(): NamespacedKey = NamespacedKey(namespace, name.lowercase())

    @NotNull
    override fun getName(): String = name

    @NotNull
    override fun getCategory(): EffectCategory = category

    @NotNull
    override fun getBehavior(): EffectBehavior = behavior

    @NotNull
    override fun translationKey(): String = "type.$namespace.${name.lowercase()}"

    override fun equals(other: Any?): Boolean = other is CustomEffectType && getKey() == other.getKey()

    override fun hashCode(): Int = getKey().hashCode()

    override fun toString(): String = "CustomEffectType[key=${getKey()}, name=$name]"

    private fun validateParameters() {
        require(name.matches(Regex("[a-z0-9_]+"))) { "Effect type ID must contain only lowercase letters, numbers, or underscores" }
        require(namespace.matches(Regex("[a-z0-9_]+"))) { "Namespace must contain only lowercase letters, numbers, or underscores" }
    }

    companion object {
        /**
         * Creates an Instant effect type.
         *
         * @param name Unique identifier for the effect type.
         * @param category The category of the effect.
         * @param namespace Namespace for the effect type key.
         * @return A new InstantEffectType instance.
         */
        fun createInstant(
            name: String,
            category: EffectCategory,
            namespace: String = "customeffect"
        ): CustomEffectType {
            return CustomEffectType(name, category, EffectBehavior.INSTANT, namespace)
        }

        /**
         * Creates a Duration effect type.
         *
         * @param name Unique identifier for the effect type.
         * @param category The category of the effect.
         * @param namespace Namespace for the effect type key.
         * @return A new DurationEffectType instance.
         */
        fun createDuration(
            name: String,
            category: EffectCategory,
            namespace: String = "customeffect"
        ): CustomEffectType {
            return CustomEffectType(name, category, EffectBehavior.DURATION, namespace)
        }

        /**
         * Creates a Periodic effect type.
         *
         * @param name Unique identifier for the effect type.
         * @param category The category of the effect.
         * @param namespace Namespace for the effect type key.
         * @return A new PeriodicEffectType instance.
         */
        fun createPeriodic(
            name: String,
            category: EffectCategory,
            namespace: String = "customeffect"
        ): CustomEffectType {
            return CustomEffectType(name, category, EffectBehavior.PERIODIC, namespace)
        }
    }
}