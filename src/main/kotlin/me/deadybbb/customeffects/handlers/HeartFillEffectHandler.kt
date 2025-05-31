package me.deadybbb.customeffects.handlers

import me.deadybbb.customeffects.Effect
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.LivingEntity
import java.util.UUID

class HeartFillEffectHandler : CustomEffectHandler {
    private val modiferMap = mutableMapOf<LivingEntity, AttributeModifier>()

    override fun applyEffect(
        entity: LivingEntity,
        effect: Effect
    ) {
        val modifier = AttributeModifier(
            UUID.randomUUID(),
            "heart_fill",
            2.0 * effect.amplifier, // // 1 heart per amplifier level
            AttributeModifier.Operation.ADD_NUMBER
        )

        entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.addModifier(modifier)
        modiferMap[entity] = modifier
    }

    override fun removeEffect(entity: LivingEntity, effect: Effect) {
        modiferMap[entity]?.let { modifier ->
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.removeModifier(modifier)
            modiferMap.remove(entity)
        }
    }

}