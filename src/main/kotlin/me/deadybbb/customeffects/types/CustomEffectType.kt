package me.deadybbb.customeffects.types

import org.bukkit.NamespacedKey
import org.jetbrains.annotations.NotNull

class CustomEffectType(
    private val name: String,
    private val category: Category,
    private val isInstant: Boolean = false
) : EffectType {
    private val key = NamespacedKey("customeffect", name.lowercase())

    init {
        require(name.matches(Regex("[a-z0-9_]+"))) { "Effect name must contain only lowercase letters, number, or underscores" }
    }

    @NotNull
    override fun getKey(): NamespacedKey = key

    @NotNull
    override fun getName(): String = name

    @NotNull
    override fun getCategory(): Category = category

    override fun isInstant(): Boolean = isInstant

    @NotNull
    override fun translationKey(): String = "effect.customeffect.$name"

    override fun equals(other: Any?): Boolean = other is CustomEffectType && key == other.key

    override fun hashCode(): Int = key.hashCode()

    override fun toString(): String = "CustomEffectType[$key $name]"
}