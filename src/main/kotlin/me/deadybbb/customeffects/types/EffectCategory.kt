package me.deadybbb.customeffects.types

import net.kyori.adventure.text.format.NamedTextColor

/**
 * Defines the category of an effect, indicating its impact on entities.
 *
 * @property color The color associated with the category for display purposes.
 */
enum class EffectCategory(
    val color: NamedTextColor
) {
    /**
     * Beneficial effects that provide positive outcomes (e.g., regeneration).
     */
    BENEFICIAL(NamedTextColor.BLUE),

    /**
     * Harmful effects that cause negative outcomes (e.g., poison).
     */
    HARMFUL(NamedTextColor.RED),

    /**
     * Neutral effects with no significant positive or negative impact (e.g., invisibility).
     */
    NEUTRAL(NamedTextColor.BLUE)
}