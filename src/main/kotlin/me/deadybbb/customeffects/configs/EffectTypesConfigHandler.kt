package me.deadybbb.customeffects.configs

import me.deadybbb.customeffects.CustomEffects
import me.deadybbb.customeffects.handlers.CustomEffectHandler
import me.deadybbb.customeffects.types.CustomEffectType
import me.deadybbb.customeffects.types.EffectBehavior
import me.deadybbb.customeffects.types.EffectCategory
import me.deadybbb.customeffects.types.EffectType
import me.deadybbb.customeffects.types.EffectTypesRegistry
import me.deadybbb.ybbbbasicmodule.BasicConfigHandler

/**
 * Manages the configuration for custom effect types stored in effect_types.yml.
 */
class EffectTypesConfigHandler(
    private val cplugin: CustomEffects
) : BasicConfigHandler(cplugin, "effect_types.yml") {

    /**
     * Loads effect types from the configuration file and registers them.
     *
     * @return List of loaded effect types.
     */
    fun loadEffectTypes(): List<EffectType> {
        reloadConfig()
        val effectTypes = mutableListOf<EffectType>()
        val typesSection = config?.getConfigurationSection("effect_types") ?: run {
            cplugin.logger.warning("No effect types found in effect_types.yml")
            return effectTypes
        }

        typesSection.getKeys(false).forEach { typeKey ->
            val section = typesSection.getConfigurationSection(typeKey) ?: run {
                cplugin.logger.warning("Invalid configuration section for effect type '$typeKey'")
                return@forEach
            }
            try {
                val name = section.getString("name")
                    ?: throw IllegalArgumentException("Name not specified for effect type '$typeKey'")
                val namespace = section.getString("namespace", "customeffect")
                    ?: throw IllegalArgumentException("Namespace not specified for effect type '$typeKey'")
                val category = section.getString("category")?.uppercase()?.let { categoryName ->
                    EffectCategory.valueOf(categoryName)
                } ?: throw IllegalArgumentException("Category not specified for effect type '$typeKey'")
                val behavior = section.getString("behavior")?.uppercase()?.let { behaviorName ->
                    EffectBehavior.valueOf(behaviorName)
                } ?: throw IllegalArgumentException("Behavior not specified for effect type '$typeKey'")
                val handlerPath = section.getString("handler")
                    ?: throw IllegalArgumentException("Handler not specified for effect type '$typeKey'")

                val handlerClass = Class.forName(handlerPath).kotlin
                val handler = handlerClass.objectInstance as? CustomEffectHandler
                    ?: throw IllegalArgumentException("Handler '$handlerPath' is not a CustomEffectHandler")

                val effectType = when (behavior) {
                    EffectBehavior.INSTANT -> CustomEffectType.createInstant(name, category, namespace)
                    EffectBehavior.DURATION -> CustomEffectType.createDuration(name, category, namespace)
                    EffectBehavior.PERIODIC -> CustomEffectType.createPeriodic(name, category, namespace)
                }

                EffectTypesRegistry.registerEffectType(effectType, handler)
                effectTypes.add(effectType)
            } catch (e: IllegalArgumentException) {
                cplugin.logger.warning("Failed to load effect type '$typeKey': ${e.message}")
            } catch (e: Exception) {
                cplugin.logger.warning("Unexpected error loading effect type '$typeKey': ${e.message}")
            }
        }
        cplugin.logger.info("Loaded ${effectTypes.size} custom effect types from effect_types.yml")
        return effectTypes
    }

    /**
     * Saves a new effect type to the configuration file.
     *
     * @param typeKey Unique key for the effect type (e.g., "custom:damage").
     * @param name Name of the effect type (e.g., "custom_damage").
     * @param namespace Namespace for the effect type key (e.g., "custom").
     * @param category The category of the effect (BENEFICIAL, HARMFUL, NEUTRAL).
     * @param behavior The behavior of the effect (INSTANT, DURATION, PERIODIC).
     * @param handlerPath Fully qualified class path to the effect handler.
     * @return True if the effect type was saved successfully, false if it already exists or an error occurred.
     */
    fun saveEffectType(
        typeKey: String,
        name: String,
        namespace: String,
        category: EffectCategory,
        behavior: EffectBehavior,
        handlerPath: String
    ): Boolean {
        reloadConfig()
        val typesSection = config?.getConfigurationSection("effect_types")
            ?: config?.createSection("effect_types") ?: return false

        if (typesSection.contains(typeKey)) {
            cplugin.logger.warning("Effect type '$typeKey' already exists in effect_types.yml")
            return false
        }

        try {
            val section = typesSection.createSection(typeKey)
            section.set("name", name)
            section.set("namespace", namespace)
            section.set("category", category.name)
            section.set("behavior", behavior.name)
            section.set("handler", handlerPath)
            saveConfig()
            cplugin.logger.info("Saved effect type '$typeKey' to effect_types.yml")
            return true
        } catch (e: Exception) {
            cplugin.logger.warning("Failed to save effect type '$typeKey': ${e.message}")
            return false
        }
    }

    /**
     * Updates an existing effect type in the configuration file.
     *
     * @param typeKey Unique key of the effect type to update.
     * @param name New name of the effect type.
     * @param namespace New namespace for the effect type key.
     * @param category New category of the effect.
     * @param behavior New behavior of the effect.
     * @param handlerPath New fully qualified class path to the effect handler.
     * @return True if the effect type was updated successfully, false if it does not exist or an error occurred.
     */
    fun updateEffectType(
        typeKey: String,
        name: String,
        namespace: String,
        category: EffectCategory,
        behavior: EffectBehavior,
        handlerPath: String
    ): Boolean {
        reloadConfig()
        val typesSection = config?.getConfigurationSection("effect_types") ?: return false

        if (!typesSection.contains(typeKey)) {
            cplugin.logger.warning("Effect type '$typeKey' does not exist in effect_types.yml")
            return false
        }

        try {
            val section = typesSection.getConfigurationSection(typeKey) ?: return false
            section.set("name", name)
            section.set("namespace", namespace)
            section.set("category", category.name)
            section.set("behavior", behavior.name)
            section.set("handler", handlerPath)
            saveConfig()
            cplugin.logger.info("Updated effect type '$typeKey' in effect_types.yml")
            return true
        } catch (e: Exception) {
            cplugin.logger.warning("Failed to update effect type '$typeKey': ${e.message}")
            return false
        }
    }

    /**
     * Deletes an effect type from the configuration file.
     *
     * @param typeKey Unique key of the effect type to delete.
     * @return True if the effect type was deleted successfully, false if it does not exist or an error occurred.
     */
    fun deleteEffectType(typeKey: String): Boolean {
        reloadConfig()
        val typesSection = config?.getConfigurationSection("effect_types") ?: return false

        if (!typesSection.contains(typeKey)) {
            cplugin.logger.warning("Effect type '$typeKey' does not exist in effect_types.yml")
            return false
        }

        try {
            typesSection.set(typeKey, null)
            saveConfig()
            cplugin.logger.info("Deleted effect type '$typeKey' from effect_types.yml")
            return true
        } catch (e: Exception) {
            cplugin.logger.warning("Failed to delete effect type '$typeKey': ${e.message}")
            return false
        }
    }
}