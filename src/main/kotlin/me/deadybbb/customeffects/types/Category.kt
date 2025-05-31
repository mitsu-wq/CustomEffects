package me.deadybbb.customeffects.types

import net.kyori.adventure.text.format.NamedTextColor

enum class Category(
    val color: NamedTextColor
) {
    BENEFICIAL(NamedTextColor.BLUE),
    HARMFUL(NamedTextColor.RED),
    NEUTRAL(NamedTextColor.BLUE)
}