package me.deadybbb.customeffects.configs

import me.deadybbb.customeffects.CustomEffects
import me.deadybbb.customeffects.types.Effect
import me.deadybbb.customeffects.types.EffectBehavior
import me.deadybbb.customeffects.types.EffectTypesRegistry
import me.deadybbb.ybbbbasicmodule.BasicConfigHandler
import org.bukkit.NamespacedKey

/**
 * Manages the configuration for custom effects stored in custom_effects.yml.
 */
class EffectsConfigHandler(
    private val cplugin: CustomEffects
) : BasicConfigHandler(cplugin, "effects.yml") {

    /**
     * Loads effects from the configuration file.
     *
     * @return A list of loaded effect instances.
     */
    fun loadEffectsFromConfig(): List<Effect> {
        reloadConfig()
        val effects = mutableListOf<Effect>()
        val effectsSection = config?.getConfigurationSection("effects") ?: run {
            cplugin.logger.info("No effects found in effects.yml")
            return effects
        }

        effectsSection.getKeys(false).forEach { effectKey ->
            val section = effectsSection.getConfigurationSection(effectKey) ?: run {
                cplugin.logger.warning("Invalid configuration section for effect '$effectKey'")
                return@forEach
            }

            try {
                val typeKeyString = section.getString("type")
                    ?: throw IllegalArgumentException("Type not specified for effect '$effectKey'")
                val typeKey = NamespacedKey.fromString(typeKeyString)
                    ?: throw IllegalArgumentException("Invalid type key '$typeKeyString' for effect '$effectKey'")
                val type = EffectTypesRegistry.getByKey(typeKey)
                    ?: throw IllegalArgumentException("Effect type '$typeKey' not found for effect '$effectKey'")

                val namespace = section.getString("namespace", "customeffect")
                    ?: throw IllegalArgumentException("Namespace not specified for effect '$effectKey'")
                val stageTime = section.getInt("stage-time", 0)
                val duration = section.getInt("duration", if (type.getBehavior() == EffectBehavior.INSTANT) 1 else -1)
                val amplifier = section.getInt("amplifier", 1)

                if (type.getBehavior() == EffectBehavior.INSTANT && duration != 0) {
                    cplugin.logger.warning("Duration specified for INSTANT effect '$effectKey' is ignored, using duration = 1")
                }

                val effect = Effect.create(
                    name = effectKey,
                    type = type,
                    stageTime = stageTime,
                    duration = duration,
                    amplifier = amplifier,
                    namespace = namespace
                )
                effects.add(effect)
            } catch (e: IllegalArgumentException) {
                cplugin.logger.warning("Failed to load effect '$effectKey': ${e.message}")
            } catch (e: Exception) {
                cplugin.logger.warning("Unexpected error loading effect '$effectKey': ${e.message}")
            }
        }
        cplugin.logger.info("Loaded ${effects.size} custom effects from effects.yml")
        return effects
    }

    /**
     * Saves a new effect to the configuration file.
     *
     * @param effect The effect to save.
     * @return True if the effect was saved successfully, false if it already exists or an error occurred.
     */
    fun saveEffect(effect: Effect): Boolean {
        reloadConfig()
        val effectKey = effect.getKey().key
        val effectsSection = config?.getConfigurationSection("effects")
            ?: config?.createSection("effects") ?: return false

        if (effectsSection.contains(effectKey)) {
            cplugin.logger.warning("Effect '$effectKey' already exists in effects.yml")
            return false
        }

        try {
            val section = effectsSection.createSection(effectKey)
            section.set("type", effect.getTypeKey().toString())
            section.set("namespace", effect.namespace)
            section.set("stage-time", effect.stageTime)
            section.set("duration", effect.duration)
            section.set("amplifier", effect.amplifier)
            saveConfig()
            cplugin.logger.info("Saved effect '$effectKey' to effects.yml")
            return true
        } catch (e: Exception) {
            cplugin.logger.warning("Failed to save effect '$effectKey': ${e.message}")
            return false
        }
    }

    /**
     * Updates an existing effect in the configuration file.
     *
     * @param effectKey Unique key of the effect to update.
     * @param effect The updated effect data.
     * @return True if the effect was updated successfully, false if it does not exist or an error occurred.
     */
    fun updateEffect(effectKey: String, effect: Effect): Boolean {
        reloadConfig()
        val effectsSection = config?.getConfigurationSection("effects") ?: return false

        if (!effectsSection.contains(effectKey)) {
            cplugin.logger.warning("Effect '$effectKey' does not exist in effects.yml")
            return false
        }

        try {
            val section = effectsSection.getConfigurationSection(effectKey) ?: return false
            section.set("type", effect.getTypeKey().toString())
            section.set("namespace", effect.namespace)
            section.set("stage-time", effect.stageTime)
            section.set("duration", effect.duration)
            section.set("amplifier", effect.amplifier)
            saveConfig()
            cplugin.logger.info("Updated effect '$effectKey' in effects.yml")
            return true
        } catch (e: Exception) {
            cplugin.logger.warning("Failed to update effect '$effectKey': ${e.message}")
            return false
        }
    }

    /**
     * Deletes an effect from the configuration file.
     *
     * @param effectKey Unique key of the effect to delete.
     * @return True if the effect was deleted successfully, false if it does not exist or an error occurred.
     */
    fun deleteEffect(effectKey: String): Boolean {
        reloadConfig()
        val effectsSection = config?.getConfigurationSection("effects") ?: return false

        if (!effectsSection.contains(effectKey)) {
            cplugin.logger.warning("Effect '$effectKey' does not exist in effects.yml")
            return false
        }

        try {
            effectsSection.set(effectKey, null)
            saveConfig()
            cplugin.logger.info("Deleted effect '$effectKey' from effects.yml")
            return true
        } catch (e: Exception) {
            cplugin.logger.warning("Failed to delete effect '$effectKey': ${e.message}")
            return false
        }
    }
}