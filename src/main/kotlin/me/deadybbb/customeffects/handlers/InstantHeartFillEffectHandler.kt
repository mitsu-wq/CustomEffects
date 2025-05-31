package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.types.Effect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import java.util.UUID

class InstantHeartFillEffectHandler : InstantEffectHandler {
    override fun applyEffect(
        entity: LivingEntity,
        effect: Effect
    ) {
        val modifier = AttributeModifier(
            UUID.randomUUID(),
            "heart_fill",
            2.0 * effect.amplifier, // 1 heart per amplifier level
            AttributeModifier.Operation.ADD_NUMBER
        )
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.addModifier(modifier)
    }
}