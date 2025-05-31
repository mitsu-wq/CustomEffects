package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.types.Effect
import org.bukkit.entity.LivingEntity

interface CustomEffectHandler {
    fun applyEffect(entity: LivingEntity, effect: Effect)
    fun removeEffect(entity: LivingEntity, effect: Effect) { return }
}

interface InstantEffectHandler : CustomEffectHandler { }

interface NonInstantEffectHandler : CustomEffectHandler {
    override fun removeEffect(entity: LivingEntity, effect: Effect)
}