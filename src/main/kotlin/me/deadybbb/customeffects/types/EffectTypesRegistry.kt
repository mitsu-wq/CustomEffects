package me.deadybbb.customeffects.types

import me.deadybbb.customeffects.handlers.CustomEffectHandler
import me.deadybbb.customeffects.handlers.DurationEffectHandler
import me.deadybbb.customeffects.handlers.HeartDrainEffectHandler
import me.deadybbb.customeffects.handlers.HeartFillEffectHandler
import me.deadybbb.customeffects.handlers.InstantEffectHandler
import me.deadybbb.customeffects.handlers.InstantHeartDrainEffectHandler
import me.deadybbb.customeffects.handlers.InstantHeartFillEffectHandler
import me.deadybbb.customeffects.handlers.PeriodicEffectHandler
import org.bukkit.NamespacedKey
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.ConcurrentHashMap

/**
 * Registry for all effect types and their associated handlers.
 */
object EffectTypesRegistry {
    private val byKey = ConcurrentHashMap<NamespacedKey, EffectType>()
    private val byName = ConcurrentHashMap<String, EffectType>()
    private val handlers = ConcurrentHashMap<NamespacedKey, CustomEffectHandler>()

    // Custom effect types
    val HEART_DRAIN = CustomEffectType.createDuration(
        name = "heart_drain",
        category = EffectCategory.HARMFUL
    ).also { registerEffectType(it, HeartDrainEffectHandler()) }

    val HEART_FILL = CustomEffectType.createDuration(
        name = "heart_fill",
        category = EffectCategory.BENEFICIAL
    ).also { registerEffectType(it, HeartFillEffectHandler()) }

    val INSTANT_HEART_DRAIN = CustomEffectType.createInstant(
        name = "instant_heart_drain",
        category = EffectCategory.HARMFUL
    ).also { registerEffectType(it, InstantHeartDrainEffectHandler()) }

    val INSTANT_HEART_FILL = CustomEffectType.createInstant(
        name = "instant_heart_fill",
        category = EffectCategory.BENEFICIAL
    ).also { registerEffectType(it, InstantHeartFillEffectHandler()) }

    // Minecraft potion effect types
    val SPEED = WrappedPotionEffectType(PotionEffectType.SPEED).also { registerEffectType(it) }
    val SLOW = WrappedPotionEffectType(PotionEffectType.SLOW).also { registerEffectType(it) }
    val FAST_DIGGING = WrappedPotionEffectType(PotionEffectType.FAST_DIGGING).also { registerEffectType(it) }
    val SLOW_DIGGING = WrappedPotionEffectType(PotionEffectType.SLOW_DIGGING).also { registerEffectType(it) }
    val INCREASE_DAMAGE = WrappedPotionEffectType(PotionEffectType.INCREASE_DAMAGE).also { registerEffectType(it) }
    val HEAL = WrappedPotionEffectType(PotionEffectType.HEAL).also { registerEffectType(it) }
    val HARM = WrappedPotionEffectType(PotionEffectType.HARM).also { registerEffectType(it) }
    val JUMP = WrappedPotionEffectType(PotionEffectType.JUMP).also { registerEffectType(it) }
    val CONFUSION = WrappedPotionEffectType(PotionEffectType.CONFUSION).also { registerEffectType(it) }
    val REGENERATION = WrappedPotionEffectType(PotionEffectType.REGENERATION).also { registerEffectType(it) }
    val DAMAGE_RESISTANCE = WrappedPotionEffectType(PotionEffectType.DAMAGE_RESISTANCE).also { registerEffectType(it) }
    val FIRE_RESISTANCE = WrappedPotionEffectType(PotionEffectType.FIRE_RESISTANCE).also { registerEffectType(it) }
    val WATER_BREATHING = WrappedPotionEffectType(PotionEffectType.WATER_BREATHING).also { registerEffectType(it) }
    val INVISIBILITY = WrappedPotionEffectType(PotionEffectType.INVISIBILITY).also { registerEffectType(it) }
    val BLINDNESS = WrappedPotionEffectType(PotionEffectType.BLINDNESS).also { registerEffectType(it) }
    val NIGHT_VISION = WrappedPotionEffectType(PotionEffectType.NIGHT_VISION).also { registerEffectType(it) }
    val HUNGER = WrappedPotionEffectType(PotionEffectType.HUNGER).also { registerEffectType(it) }
    val WEAKNESS = WrappedPotionEffectType(PotionEffectType.WEAKNESS).also { registerEffectType(it) }
    val POISON = WrappedPotionEffectType(PotionEffectType.POISON).also { registerEffectType(it) }
    val WITHER = WrappedPotionEffectType(PotionEffectType.WITHER).also { registerEffectType(it) }
    val HEALTH_BOOST = WrappedPotionEffectType(PotionEffectType.HEALTH_BOOST).also { registerEffectType(it) }
    val ABSORPTION = WrappedPotionEffectType(PotionEffectType.ABSORPTION).also { registerEffectType(it) }
    val SATURATION = WrappedPotionEffectType(PotionEffectType.SATURATION).also { registerEffectType(it) }
    val GLOWING = WrappedPotionEffectType(PotionEffectType.GLOWING).also { registerEffectType(it) }
    val LEVITATION = WrappedPotionEffectType(PotionEffectType.LEVITATION).also { registerEffectType(it) }
    val LUCK = WrappedPotionEffectType(PotionEffectType.LUCK).also { registerEffectType(it) }
    val UNLUCK = WrappedPotionEffectType(PotionEffectType.UNLUCK).also { registerEffectType(it) }
    val SLOW_FALLING = WrappedPotionEffectType(PotionEffectType.SLOW_FALLING).also { registerEffectType(it) }
    val CONDUIT_POWER = WrappedPotionEffectType(PotionEffectType.CONDUIT_POWER).also { registerEffectType(it) }
    val DOLPHINS_GRACE = WrappedPotionEffectType(PotionEffectType.DOLPHINS_GRACE).also { registerEffectType(it) }
    val BAD_OMEN = WrappedPotionEffectType(PotionEffectType.BAD_OMEN).also { registerEffectType(it) }
    val HERO_OF_THE_VILLAGE = WrappedPotionEffectType(PotionEffectType.HERO_OF_THE_VILLAGE).also { registerEffectType(it) }
    val DARKNESS = WrappedPotionEffectType(PotionEffectType.DARKNESS).also { registerEffectType(it) }

    /**
     * Registers an effect type with an optional handler.
     *
     * @param type The effect type to register.
     * @param handler The handler for custom effect types, if applicable.
     * @throws IllegalArgumentException if the effect type is already registered or the handler is invalid.
     */
    fun registerEffectType(type: EffectType, handler: CustomEffectHandler? = null) {
        val key = type.getKey()
        val name = type.getName().lowercase()
        if (byKey.containsKey(key) || byName.containsKey(name)) {
            throw IllegalArgumentException("Effect type with key '$key' or name '$name' is already registered")
        }
        if (handler != null && type is CustomEffectType) {
            when (type.getBehavior()) {
                EffectBehavior.INSTANT -> require(handler is InstantEffectHandler) {
                    "Handler for INSTANT effect must implement InstantEffectHandler"
                }
                EffectBehavior.DURATION -> require(handler is DurationEffectHandler) {
                    "Handler for DURATION effect must implement DurationEffectHandler"
                }
                EffectBehavior.PERIODIC -> require(handler is PeriodicEffectHandler) {
                    "Handler for PERIODIC effect must implement PeriodicEffectHandler"
                }
            }
            handlers[key] = handler
        }
        byKey[key] = type
        byName[name] = type
    }

    /**
     * Registers an instant effect type.
     *
     * @param name Unique identifier for the effect type.
     * @param category The category of the effect.
     * @param namespace Namespace for the effect type key.
     * @param handler Optional handler for the effect.
     * @return The registered Instant effect type.
     */
    fun registerInstantEffect(
        name: String,
        category: EffectCategory,
        namespace: String = "customeffect",
        handler: InstantEffectHandler? = null
    ): CustomEffectType {
        val type = CustomEffectType.createInstant(name, category, namespace)
        registerEffectType(type, handler)
        return type
    }

    /**
     * Registers a duration effect type.
     *
     * @param name Unique identifier for the effect type.
     * @param category The category of the effect.
     * @param namespace Namespace for the effect type key.
     * @param handler Optional handler for the effect.
     * @return The registered Duration effect type.
     */
    fun registerDurationEffect(
        name: String,
        category: EffectCategory,
        namespace: String = "customeffect",
        handler: DurationEffectHandler? = null
    ): CustomEffectType {
        val type = CustomEffectType.createDuration(name, category, namespace)
        registerEffectType(type, handler)
        return type
    }

    /**
     * Registers a periodic effect type.
     *
     * @param name Unique identifier for the effect type.
     * @param category The category of the effect.
     * @param namespace Namespace for the effect type key.
     * @param handler Optional handler for the effect.
     * @return The registered Periodic effect type.
     */
    fun registerPeriodicEffect(
        name: String,
        category: EffectCategory,
        namespace: String = "customeffect",
        handler: PeriodicEffectHandler? = null
    ): CustomEffectType {
        val type = CustomEffectType.createPeriodic(name, category, namespace)
        registerEffectType(type, handler)
        return type
    }

    /**
     * Unregisters an effect type.
     *
     * @param key The NamespacedKey of the effect type to unregister.
     * @return True if the effect type was unregistered, false if it was not found.
     */
    fun unregisterEffectType(key: NamespacedKey): Boolean {
        val type = byKey.remove(key) ?: return false
        byName.remove(type.getName().lowercase())
        handlers.remove(key)
        return true
    }

    /**
     * Retrieves an effect type by its NamespacedKey.
     *
     * @param key The NamespacedKey of the effect type.
     * @return The EffectType if found, null otherwise.
     */
    fun getByKey(key: NamespacedKey?): EffectType? = key?.let { byKey[it] }

    /**
     * Retrieves an effect type by its name.
     *
     * @param name The name of the effect type (case-insensitive).
     * @return The EffectType if found, null otherwise.
     */
    fun getByName(name: String): EffectType? = byName[name.lowercase()]

    /**
     * Returns all registered effect types.
     *
     * @return Array of all registered EffectType instances.
     */
    fun values(): Array<EffectType> = byKey.values.toTypedArray()

    /**
     * Retrieves the handler for an effect type.
     *
     * @param type The effect type.
     * @return The associated CustomEffectHandler, or null if none exists.
     */
    fun getHandler(type: EffectType): CustomEffectHandler? = handlers[type.getKey()]
}