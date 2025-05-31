package me.deadybbb.customeffects.types

class InstantEffectType(
    private val name: String,
    private val category: EffectCategory,
    private val namespace: String = "customeffect"
) : CustomEffectType(name, category,EffectBehavior.INSTANT, namespace) {
    companion object {
        fun create(
            name: String,
            category: EffectCategory,
            namespace: String = "customeffect"
        ): InstantEffectType {
            return InstantEffectType(name, category, namespace)
        }
    }
}