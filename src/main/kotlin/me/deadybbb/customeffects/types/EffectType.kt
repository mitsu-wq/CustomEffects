package me.deadybbb.customeffects.types

import net.kyori.adventure.translation.Translatable
import org.bukkit.NamespacedKey
import org.jetbrains.annotations.NotNull

/**
 * Represents a type of effect, defining its key, name, category, and behavior.
 */
interface EffectType : Translatable {
    /**
     * Returns the unique key of the effect type.
     * @return NamespacedKey of the effect type.
     */
    @NotNull
    fun getKey(): NamespacedKey

    /**
     * Returns the name of the effect type.
     * @return Name of the effect type (lowercase, e.g., "heart_fill").
     */
    @NotNull
    fun getName(): String

    /**
     * Returns the category of the effect type.
     * @return EffectCategory (BENEFICIAL, HARMFUL, or NEUTRAL).
     */
    @NotNull
    fun getCategory(): EffectCategory

    /**
     * Returns the behavior of the effect type.
     * @return EffectBehavior (INSTANT, DURATION, or PERIODIC).
     */
    @NotNull
    fun getBehavior(): EffectBehavior
}