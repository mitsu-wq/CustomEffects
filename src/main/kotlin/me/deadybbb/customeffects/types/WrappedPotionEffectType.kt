package me.deadybbb.customeffects.types

import org.bukkit.NamespacedKey
import org.bukkit.potion.PotionEffectType
import org.jetbrains.annotations.NotNull

class WrappedPotionEffectType(
    private val potionEffectType: PotionEffectType
) : EffectType {

    @NotNull
    override fun getKey(): NamespacedKey = potionEffectType.key

    @NotNull
    override fun getName(): String = potionEffectType.name

    @NotNull
    override fun getCategory(): Category = when (potionEffectType.effectCategory) {
        PotionEffectType.Category.BENEFICIAL -> Category.BENEFICIAL
        PotionEffectType.Category.HARMFUL -> Category.HARMFUL
        else -> Category.NEUTRAL
    }

    override fun isInstant(): Boolean = potionEffectType.isInstant

    @NotNull
    override fun translationKey(): String = potionEffectType.translationKey()

    override fun equals(other: Any?): Boolean = other is WrappedPotionEffectType && potionEffectType == other.potionEffectType

    override fun hashCode(): Int = potionEffectType.hashCode()

    override fun toString(): String = "WrappedPotionEffectType[${getKey()}, ${getName()}]"
}