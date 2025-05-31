package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.types.Effect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import java.util.UUID

class HeartDrainEffectHandler : NonInstantEffectHandler {
    private val modifierMap = mutableMapOf<LivingEntity, AttributeModifier>()

    override fun applyEffect(
        entity: LivingEntity,
        effect: Effect
    ) {
        val modifier = AttributeModifier(
            UUID.randomUUID(),
            "heart_drain",
            -2.0 * effect.amplifier, // 1 heart per amplifier level
            AttributeModifier.Operation.ADD_NUMBER
        )
        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.addModifier(modifier)
        modifierMap[entity] = modifier
    }

    override fun removeEffect(
        entity: LivingEntity,
        effect: Effect
    ) {
        modifierMap[entity]?.let { modifier ->
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.removeModifier(modifier)
            modifierMap.remove(entity)
        }
    }

}