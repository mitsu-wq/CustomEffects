package me.deadybbb.customeffects.types

import net.kyori.adventure.translation.Translatable
import org.bukkit.NamespacedKey
import org.jetbrains.annotations.NotNull
import kotlin.String

abstract class CustomEffectType(
    private val name: String,
    private val category: EffectCategory,
    private val behavior: EffectBehavior,
    private val namespace: String = "customeffect"
) : EffectType, Translatable {
    private val key = NamespacedKey(namespace, name.lowercase())

    init {
        require(name.matches(Regex("[a-z0-9_]+"))) { "Effect name must contain only lowercase letters, number, or underscores" }
        require(namespace.matches(Regex("[a-z0-9_]+"))) { "Namespace name must contain only lowercase letters, number, or underscores" }
    }

    @NotNull
    override fun getKey(): NamespacedKey = key

    @NotNull
    override fun getName(): String = name

    @NotNull
    override fun getCategory(): EffectCategory = category

    @NotNull
    override fun getBehavior(): EffectBehavior = behavior

    @NotNull
    override fun translationKey(): String = "effect.$namespace.$name"

    override fun equals(other: Any?): Boolean = other is CustomEffectType && key == other.key

    override fun hashCode(): Int = key.hashCode()

    override fun toString(): String = "CustomEffectType[$key $name]"
}