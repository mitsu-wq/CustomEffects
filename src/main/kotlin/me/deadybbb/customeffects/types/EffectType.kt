package me.deadybbb.customeffects.types

import net.kyori.adventure.translation.Translatable
import org.bukkit.NamespacedKey
import org.jetbrains.annotations.NotNull

interface EffectType : Translatable {
    @NotNull
    fun getKey(): NamespacedKey

    @NotNull
    fun getName(): String

    @NotNull
    fun getCategory(): Category

    fun isInstant(): Boolean
}