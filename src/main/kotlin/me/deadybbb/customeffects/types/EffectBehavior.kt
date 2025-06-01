package me.deadybbb.customeffects.types

/**
 * Defines the behavior of an effect, determining how it is applied to entities.
 */
enum class EffectBehavior {
    /**
     * Instant effects are applied once and have no duration (e.g., instant healing).
     */
    INSTANT,

    /**
     * Duration effects are applied continuously over a set period (e.g., speed boost).
     */
    DURATION,

    /**
     * Periodic effects are applied repeatedly at intervals (e.g., poison ticks).
     */
    PERIODIC
}