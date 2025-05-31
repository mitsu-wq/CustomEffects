package me.deadybbb.customeffects.types

class NonInstantEffectType(
    private val name: String,
    private val category: EffectCategory,
    private val namespace: String = "customeffect"
) : CustomEffectType(name, category,EffectBehavior.DURATION, namespace) {
    companion object {
        fun create(
            name: String,
            category: EffectCategory,
            namespace: String = "customeffect"
        ): NonInstantEffectType {
            return NonInstantEffectType(name, category, namespace)
        }
    }
}