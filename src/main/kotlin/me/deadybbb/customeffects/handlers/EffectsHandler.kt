package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.CustomEffects
import me.deadybbb.customeffects.api.CustomEffectsAPI
import me.deadybbb.customeffects.configs.EffectTypesConfigHandler
import me.deadybbb.customeffects.configs.EffectsConfigHandler
import me.deadybbb.customeffects.types.Effect
import me.deadybbb.customeffects.types.EffectCategory
import me.deadybbb.customeffects.types.EffectBehavior
import me.deadybbb.customeffects.types.EffectType
import me.deadybbb.customeffects.types.EffectTypesRegistry
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity

/**
 * Central handler for managing custom effects and their types.
 */
class EffectsHandler(
    private val plugin: CustomEffects
) : CustomEffectsAPI {
    private val config = EffectsConfigHandler(plugin)
    private val typesConfig = EffectTypesConfigHandler(plugin)
    private var effects = config.loadEffectsFromConfig()
    private val manager = EffectsManager(plugin)

    init {
        typesConfig.loadEffectTypes()
    }

    override fun getEffects(): List<Effect> = effects

    override fun getEffectByKey(key: String): Effect? =
        effects.find { it.getKey().key == key }

    override fun applyEffect(entity: LivingEntity, effect: Effect) {
        manager.applyEffect(entity, effect)
    }

    override fun removeEffect(entity: LivingEntity, effect: Effect) {
        manager.removeEffect(entity, effect)
    }

    override fun clearEffects() {
        plugin.server.onlinePlayers.forEach { manager.clearEffects(it) }
        effects = emptyList()
    }

    override fun clearEffects(entity: LivingEntity) {
        manager.clearEffects(entity)
    }

    override fun getActiveEffects(entity: LivingEntity): List<Effect> =
        manager.getActiveEffects(entity)

    override fun isEffectActive(entity: LivingEntity, effect: Effect): Boolean =
        manager.isOnEntity(entity, effect)

    override fun reloadEffects() {
        val newEffects = config.loadEffectsFromConfig()
        plugin.server.onlinePlayers.forEach { player ->
            val activeEffects = manager.getActiveEffects(player)
            activeEffects.forEach { activeEffect ->
                if (newEffects.none { it.getKey() == activeEffect.getKey() }) {
                    manager.removeEffect(player, activeEffect)
                }
            }
            newEffects.forEach { newEffect ->
                if (activeEffects.any { it.getKey() == newEffect.getKey() }) {
                    scheduleEffectApplication(player, newEffect)
                }
            }
        }
        effects = newEffects
        plugin.logger.info("Reloaded ${effects.size} effects from configuration")
    }

    override fun createEffect(
        name: String,
        typeName: String,
        stageTime: Int,
        duration: Int,
        amplifier: Int,
        namespace: String
    ): Effect? {
        try {
            val effect = Effect.create(name, typeName, stageTime, duration, amplifier, namespace)
            return createEffect(effect)
        } catch (e: IllegalArgumentException) {
            plugin.logger.warning("Failed to create effect '$name': ${e.message}")
            return null
        }
    }

    override fun createEffect(name: String, typeKey: NamespacedKey, stageTime: Int, duration: Int, amplifier: Int): Effect? {
        try {
            val effect = Effect.create(name, typeKey, stageTime, duration, amplifier)
            return createEffect(effect)
        } catch (e: IllegalArgumentException) {
            plugin.logger.warning("Failed to create effect '$name': ${e.message}")
            return null
        }
    }

     fun createEffect(effect: Effect): Effect? {
        if (doesEffectExist(effect.getKey().key)) {
            plugin.logger.warning("Effect with key '${effect.getKey()}' already exists")
            return null
        }
        if (config.saveEffect(effect)) {
            effects = config.loadEffectsFromConfig()
            return effect
        }
        plugin.logger.warning("Failed to save effect '${effect.getKey()}' to configuration")
        return null
    }

    override fun updateEffect(
        effectKey: String,
        typeKey: NamespacedKey,
        stageTime: Int,
        duration: Int,
        amplifier: Int
    ): Effect? {
        if (!doesEffectExist(effectKey)) {
            plugin.logger.warning("Effect with key '$effectKey' does not exist")
            return null
        }
        val type = EffectTypesRegistry.getByKey(typeKey)
            ?: throw IllegalArgumentException("Effect type '$typeKey' not found")
        try {
            val effect = Effect.create(effectKey, type, stageTime, duration, amplifier)
            return updateEffect(effectKey, effect)
        } catch (e: IllegalArgumentException) {
            plugin.logger.warning("Invalid parameters for effect '$effectKey': ${e.message}")
            return null
        }
    }

    fun updateEffect(
        effectKey: String,
        effect: Effect
    ): Effect? {
        if (!doesEffectExist(effectKey)) {
            plugin.logger.warning("Effect with key '$effectKey' does not exist")
            return null
        }
        if (config.updateEffect(effectKey, effect)) {
            plugin.server.onlinePlayers.forEach { player ->
                val oldEffect = effects.find { it.getKey().key == effectKey }
                if (oldEffect != null && isEffectActive(player, oldEffect)) {
                    removeEffect(player, oldEffect)
                    applyEffect(player, effect)
                }
            }
            effects = config.loadEffectsFromConfig()
            return effect
        }
        plugin.logger.warning("Failed to update effect '$effectKey' in configuration")
        return null
    }

    override fun deleteEffect(effectKey: String): Boolean {
        val effect = effects.find { it.getKey().key == effectKey }
        if (effect == null) {
            plugin.logger.warning("Effect with key '$effectKey' does not exist")
            return false
        }
        if (config.deleteEffect(effectKey)) {
            plugin.server.onlinePlayers.forEach { player ->
                if (isEffectActive(player, effect)) {
                    removeEffect(player, effect)
                }
            }
            effects = config.loadEffectsFromConfig()
            plugin.logger.info("Deleted effect '$effectKey' from configuration")
            return true
        }
        plugin.logger.warning("Failed to delete effect '$effectKey' from configuration")
        return false
    }

    override fun createEffectType(
        typeKey: String,
        name: String,
        namespace: String,
        category: EffectCategory,
        behavior: EffectBehavior,
        handlerPath: String
    ): Boolean {
        if (!isValidName(name) || !isValidName(namespace)) {
            throw IllegalArgumentException("Name and namespace must contain only lowercase letters, numbers, or underscores")
        }
        val namespacedKey = NamespacedKey.fromString(typeKey)
            ?: throw IllegalArgumentException("Invalid typeKey format: '$typeKey'")
        if (EffectTypesRegistry.getByKey(namespacedKey) != null) {
            plugin.logger.warning("Effect type '$typeKey' already exists")
            return false
        }
        try {
            validateHandler(handlerPath)
            if (typesConfig.saveEffectType(typeKey, name, namespace, category, behavior, handlerPath)) {
                typesConfig.loadEffectTypes()
                plugin.logger.info("Created effect type '$typeKey'")
                return true
            }
            plugin.logger.warning("Failed to save effect type '$typeKey' to configuration")
            return false
        } catch (e: Exception) {
            plugin.logger.warning("Failed to create effect type '$typeKey': ${e.message}")
            return false
        }
    }

    override fun updateEffectType(
        typeKey: String,
        name: String,
        namespace: String,
        category: EffectCategory,
        behavior: EffectBehavior,
        handlerPath: String
    ): Boolean {
        if (!isValidName(name) || !isValidName(namespace)) {
            throw IllegalArgumentException("Name and namespace must contain only lowercase letters, numbers, or underscores")
        }
        val namespacedKey = NamespacedKey.fromString(typeKey)
            ?: throw IllegalArgumentException("Invalid typeKey format: '$typeKey'")
        if (EffectTypesRegistry.getByKey(namespacedKey) == null) {
            plugin.logger.warning("Effect type '$typeKey' does not exist")
            return false
        }
        try {
            validateHandler(handlerPath)
            EffectTypesRegistry.unregisterEffectType(namespacedKey)
            if (typesConfig.updateEffectType(typeKey, name, namespace, category, behavior, handlerPath)) {
                typesConfig.loadEffectTypes()
                reloadEffects()
                plugin.logger.info("Updated effect type '$typeKey'")
                return true
            }
            plugin.logger.warning("Failed to update effect type '$typeKey' in configuration")
            return false
        } catch (e: Exception) {
            plugin.logger.warning("Failed to update effect type '$typeKey': ${e.message}")
            return false
        }
    }

    override fun deleteEffectType(typeKey: String): Boolean {
        val namespacedKey = NamespacedKey.fromString(typeKey)
            ?: throw IllegalArgumentException("Invalid typeKey format: '$typeKey'")
        if (EffectTypesRegistry.getByKey(namespacedKey) == null) {
            plugin.logger.warning("Effect type '$typeKey' does not exist")
            return false
        }
        if (effects.any { it.getTypeKey() == namespacedKey }) {
            plugin.logger.warning("Cannot delete effect type '$typeKey' as it is used by existing effects")
            return false
        }
        if (typesConfig.deleteEffectType(typeKey)) {
            EffectTypesRegistry.unregisterEffectType(namespacedKey)
            reloadEffects()
            plugin.logger.info("Deleted effect type '$typeKey'")
            return true
        }
        plugin.logger.warning("Failed to delete effect type '$typeKey' from configuration")
        return false
    }

    private fun doesEffectExist(effectKey: String): Boolean =
        effects.any { it.getKey().key == effectKey }

    private fun scheduleEffectApplication(entity: LivingEntity, effect: Effect) {
        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                manager.applyEffect(entity, effect)
            })
        } else {
            manager.applyEffect(entity, effect)
        }
    }

    private fun isValidName(name: String): Boolean =
        name.matches(Regex("[a-z0-9_]+"))

    private fun validateHandler(handlerPath: String) {
        try {
            val handlerClass = Class.forName(handlerPath)
            if (!CustomEffectHandler::class.java.isAssignableFrom(handlerClass)) {
                throw IllegalArgumentException("Handler '$handlerPath' is not a CustomEffectHandler")
            }
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("Handler class '$handlerPath' not found", e)
        }
    }
}