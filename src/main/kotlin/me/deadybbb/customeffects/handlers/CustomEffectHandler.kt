package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.Effect
import org.bukkit.entity.LivingEntity

interface CustomEffectHandler {
    fun applyEffect(entity: LivingEntity, effect: Effect)
    fun removeEffect(entity: LivingEntity, effect: Effect)
}