package me.deadybbb.customeffects

import me.deadybbb.customeffects.types.EffectTypes
import me.deadybbb.ybbbbasicmodule.BasicConfigHandler
import org.bukkit.NamespacedKey

class EffectsConfigHandler(
    val cplugin: CustomEffects
) : BasicConfigHandler(cplugin, "custom_effects.yml") {

    fun loadEffectsFromConfig(): List<Effect> {
        reloadConfig()

        val effects = mutableListOf<Effect>()
        val effectsSection = config?.getConfigurationSection("effects") ?: run {
            cplugin.logger.warning("No effects in config")
            return effects
        }

        effectsSection.getKeys(false).forEach { effectKey ->
            val section = config?.getConfigurationSection("effects.$effectKey") ?: return@forEach

            val typeKey = NamespacedKey.fromString(section.getString("type", "") ?: return@forEach)
            val type = EffectTypes.getByKey(typeKey) ?: run {
                cplugin.logger.warning("Effect type $typeKey not found for $effectKey")
                return@forEach
            }

            try {
                effects.add(Effect(
                    type = type,
                    stageTime = section.getInt("stage-time", 1),
                    duration = section.getInt("duration", if (type.isInstant()) 1 else 20),
                    amplifier = section.getInt("amplifier", 1)
                ))
            } catch (e: IllegalArgumentException) {
                cplugin.logger.warning("Invalid configuration for effect $effectKey: ${e.message}")
            }
        }
        cplugin.logger.info("Loaded ${effects.size} custom effects")
        return effects
    }
}