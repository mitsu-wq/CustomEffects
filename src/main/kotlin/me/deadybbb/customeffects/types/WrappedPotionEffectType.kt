package me.deadybbb.customeffects.types

import org.bukkit.NamespacedKey
import org.bukkit.potion.PotionEffectType
import org.jetbrains.annotations.NotNull

/**
 * Wraps a standard Minecraft PotionEffectType to conform to the EffectType interface.
 *
 * @property potionEffectType The underlying Minecraft potion effect type.
 */
class WrappedPotionEffectType(
    private val potionEffectType: PotionEffectType
) : EffectType {

    @NotNull
    override fun getKey(): NamespacedKey = potionEffectType.key

    @NotNull
    override fun getName(): String = potionEffectType.name.lowercase()

    @NotNull
    override fun getCategory(): EffectCategory = when (potionEffectType.effectCategory) {
        PotionEffectType.Category.BENEFICIAL -> EffectCategory.BENEFICIAL
        PotionEffectType.Category.HARMFUL -> EffectCategory.HARMFUL
        else -> EffectCategory.NEUTRAL
    }

    @NotNull
    override fun getBehavior(): EffectBehavior =
        if (potionEffectType.isInstant) EffectBehavior.INSTANT else EffectBehavior.DURATION

    @NotNull
    override fun translationKey(): String = potionEffectType.translationKey()

    override fun equals(other: Any?): Boolean =
        other is WrappedPotionEffectType && potionEffectType == other.potionEffectType

    override fun hashCode(): Int = potionEffectType.hashCode()

    override fun toString(): String = "WrappedPotionEffectType[key=${getKey()}, name=${getName()}]"
}