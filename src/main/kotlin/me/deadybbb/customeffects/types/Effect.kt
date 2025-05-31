package me.deadybbb.customeffects.types

import org.bukkit.NamespacedKey

data class Effect(
    val type: EffectType,
    val stageTime: Int, // Time before activation (in ticks)
    val duration: Int, // Duration of effect (in ticks)
    val amplifier: Int = 1, // Effect strength
) {
    init {
        require(stageTime >= 0) { "stageTime must be positive" }
        require(type.getBehavior() == EffectBehavior.INSTANT || duration > 0) { "duration must be positive for non-instant effects" }
        require(amplifier >= 0) { "amplifier must be positive" }
    }

    fun getKey(): NamespacedKey = type.getKey()
}