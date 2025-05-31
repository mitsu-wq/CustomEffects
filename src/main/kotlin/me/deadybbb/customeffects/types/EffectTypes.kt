package me.deadybbb.customeffects.types

import me.deadybbb.customeffects.handlers.CustomEffectHandler
import me.deadybbb.customeffects.handlers.HeartDrainEffectHandler
import me.deadybbb.customeffects.handlers.HeartFillEffectHandler
import me.deadybbb.customeffects.handlers.InstantHeartDrainEffectHandler
import me.deadybbb.customeffects.handlers.InstantHeartFillEffectHandler
import org.bukkit.NamespacedKey
import org.bukkit.potion.PotionEffectType
import org.jetbrains.annotations.NotNull
import java.util.concurrent.ConcurrentHashMap

object EffectTypes {
    private val byKey = ConcurrentHashMap<NamespacedKey, EffectType>()
    private val byName = ConcurrentHashMap<String, EffectType>()
    private val handlers = ConcurrentHashMap<NamespacedKey, CustomEffectHandler>()
    private var acceptingNew = true

    val HEART_DRAIN = CustomEffectType(name = "heart_drain", category = Category.HARMFUL).also { registerEffectType(it, HeartDrainEffectHandler()) }
    val HEART_FILL = CustomEffectType(name = "heart_fill", category = Category.BENEFICIAL).also { registerEffectType(it, HeartFillEffectHandler()) }
    val INSTANT_HEART_DRAIN = CustomEffectType(name = "instant_heart_drain", category = Category.HARMFUL, isInstant = true).also { registerEffectType(it, InstantHeartDrainEffectHandler()) }
    val INSTANT_HEART_FILL = CustomEffectType(name = "instant_heart_fill", category = Category.BENEFICIAL, isInstant = true).also { registerEffectType(it, InstantHeartFillEffectHandler()) }

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

    fun registerEffectType(type: EffectType, handler: CustomEffectHandler? = null) {
        if (byKey.containsKey(type.getKey()) || byName.containsKey(type.getName().lowercase())) {
            throw IllegalArgumentException("Effect with key ${type.getKey()} or name ${type.getName()} already registered")
        }
        if (!acceptingNew) {
            throw IllegalStateException("No longer accepting new effect types")
        }
        byKey[type.getKey()] = type
        byName[type.getName().lowercase()] = type
        if (handler != null && type is CustomEffectType) {
            handlers[type.getKey()] = handler
        }
    }

    @NotNull
    fun getByKey(key: NamespacedKey?): EffectType? = byKey[key]

    @NotNull
    fun getByName(name: String): EffectType? = byName[name.lowercase()]

    @NotNull
    fun values(): Array<EffectType> = byKey.values.toTypedArray()

    @NotNull
    fun getHandler(type: EffectType): CustomEffectHandler? = handlers[type.getKey()]

    fun stopAcceptingRegistrations() {
        acceptingNew = false
    }
}