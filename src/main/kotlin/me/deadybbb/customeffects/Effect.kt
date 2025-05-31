package me.deadybbb.customeffects

import me.deadybbb.customeffects.types.EffectType
import org.bukkit.NamespacedKey

data class Effect(
    val type: EffectType,
    val stageTime: Int, // Time before activation (in ticks)
    val duration: Int, // Duration of effect (in ticks)
    val amplifier: Int = 1, // Effect strength
) {
    init {
        require(stageTime >= 0) { "stageTime must be positive" }
        require(type.isInstant() || duration > 0) { "duration must be positive for non-instant effects" }
        require(amplifier >= 0) { "amplifier must be positive" }
    }

    fun getKey(): NamespacedKey = type.getKey()
}