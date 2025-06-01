package me.deadybbb.customeffects

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.IntegerArgument
import dev.jorel.commandapi.arguments.PlayerArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import me.deadybbb.customeffects.types.EffectBehavior
import me.deadybbb.customeffects.types.EffectCategory
import me.deadybbb.ybbbbasicmodule.LegacyTextHandler
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player

/**
 * Registers and handles the /customeffects command and its subcommands.
 */
class CustomEffectsCommand(
    private val plugin: CustomEffects
) {
    /**
     * Registers the /customeffects command with all subcommands.
     */
    fun registerCommand() {
        // Subcommand: /customeffects apply <player> <effect>
        val apply = CommandAPICommand("apply")
            .withArguments(PlayerArgument("player"))
            .withArguments(StringArgument("effect").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                plugin.handler.getEffects()
                    .map { it.getKey().key }
                    .toTypedArray()
            }))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val player = args.get("player") as Player
                    val effectKey = args.get("effect") as String
                    val effect = plugin.handler.getEffectByKey(effectKey)
                    if (effect == null) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<red>Эффект '$effectKey' не найден.")
                        return@PlayerCommandExecutor
                    }
                    plugin.handler.applyEffect(player, effect)
                    LegacyTextHandler.sendFormattedMessage(
                        sender,
                        "<green>Эффект '$effectKey' успешно применен к игроку ${player.name}."
                    )
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при применении эффекта: ${e.message}")
                    plugin.logger.severe("Error applying effect: ${e.stackTraceToString()}")
                }
            })

        // Subcommand: /customeffects reload
        val reload = CommandAPICommand("reload")
            .executesPlayer(PlayerCommandExecutor { sender, _ ->
                try {
                    plugin.handler.reloadEffects()
                    LegacyTextHandler.sendFormattedMessage(sender, "<green>Конфигурация эффектов успешно перезагружена.")
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при перезагрузке конфигурации: ${e.message}")
                    plugin.logger.severe("Error reloading config: ${e.stackTraceToString()}")
                }
            })

        // Subcommand: /customeffects create <name> <type> <stageTime> <duration> <amplifier> [namespace]
        val create = CommandAPICommand("create")
            .withArguments(StringArgument("name"))
            .withArguments(StringArgument("type").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                plugin.handler.getEffects().map { it.getKey().namespace }.distinct().toTypedArray()
            }))
            .withArguments(IntegerArgument("stageTime", 0))
            .withArguments(IntegerArgument("duration", -1))
            .withArguments(IntegerArgument("amplifier", 0))
            .withArguments(StringArgument("namespace").setOptional(true))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val name = args.get("name") as String
                    val typeName = args.get("type") as String
                    val stageTime = args.get("stageTime") as Int
                    val duration = args.get("duration") as Int
                    val amplifier = args.get("amplifier") as Int
                    val namespace = args.get("namespace") as String? ?: "customeffect"
                    val effect = plugin.handler.createEffect(name, typeName, stageTime, duration, amplifier, namespace)
                    if (effect == null) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<red>Не удалось создать эффект '$name'.")
                        return@PlayerCommandExecutor
                    }
                    LegacyTextHandler.sendFormattedMessage(sender, "<green>Эффект '$name' успешно создан.")
                } catch (e: IllegalArgumentException) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Неверные параметры: ${e.message}")
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при создании эффекта: ${e.message}")
                    plugin.logger.severe("Error creating effect: ${e.stackTraceToString()}")
                }
            })

        // Subcommand: /customeffects update <effect> <type> <stageTime> <duration> <amplifier>
        val update = CommandAPICommand("update")
            .withArguments(StringArgument("effect").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                plugin.handler.getEffects().map { it.getKey().key }.toTypedArray()
            }))
            .withArguments(StringArgument("type").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                plugin.handler.getEffects().map { it.getKey().namespace }.distinct().toTypedArray()
            }))
            .withArguments(IntegerArgument("stageTime", 0))
            .withArguments(IntegerArgument("duration", -1))
            .withArguments(IntegerArgument("amplifier", 0))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val effectKey = args.get("effect") as String
                    val typeName = args.get("type") as String
                    val stageTime = args.get("stageTime") as Int
                    val duration = args.get("duration") as Int
                    val amplifier = args.get("amplifier") as Int
                    val typeKey = NamespacedKey.fromString(typeName)
                        ?: throw IllegalArgumentException("Неверный формат ключа типа '$typeName'.")
                    val effect = plugin.handler.updateEffect(effectKey, typeKey, stageTime, duration, amplifier)
                    if (effect == null) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<red>Не удалось обновить эффект '$effectKey'.")
                        return@PlayerCommandExecutor
                    }
                    LegacyTextHandler.sendFormattedMessage(sender, "<green>Эффект '$effectKey' успешно обновлен.")
                } catch (e: IllegalArgumentException) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Неверные параметры: ${e.message}")
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при обновлении эффекта: ${e.message}")
                    plugin.logger.severe("Error updating effect: ${e.stackTraceToString()}")
                }
            })

        // Subcommand: /customeffects delete <effect>
        val delete = CommandAPICommand("delete")
            .withArguments(StringArgument("effect").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                plugin.handler.getEffects().map { it.getKey().key }.toTypedArray()
            }))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val effectKey = args.get("effect") as String
                    if (plugin.handler.deleteEffect(effectKey)) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<green>Эффект '$effectKey' успешно удален.")
                    } else {
                        LegacyTextHandler.sendFormattedMessage(sender, "<red>Не удалось удалить эффект '$effectKey'.")
                    }
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при удалении эффекта: ${e.message}")
                    plugin.logger.severe("Error deleting effect: ${e.stackTraceToString()}")
                }
            })

        // Subcommand: /customeffects clear <player>
        val clear = CommandAPICommand("clear")
            .withArguments(PlayerArgument("player"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val player = args.get("player") as Player
                    plugin.handler.clearEffects(player)
                    LegacyTextHandler.sendFormattedMessage(
                        sender,
                        "<green>Все эффекты удалены у игрока ${player.name}."
                    )
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при очистке эффектов: ${e.message}")
                    plugin.logger.severe("Error clearing effects: ${e.stackTraceToString()}")
                }
            })

        // Subcommand: /customeffects list <player>
        val list = CommandAPICommand("list")
            .withArguments(PlayerArgument("player"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val player = args.get("player") as Player
                    val activeEffects = plugin.handler.getActiveEffects(player)
                    if (activeEffects.isEmpty()) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<yellow>У игрока ${player.name} нет активных эффектов.")
                        return@PlayerCommandExecutor
                    }
                    LegacyTextHandler.sendFormattedMessage(
                        sender,
                        "<green>Активные эффекты игрока ${player.name}:"
                    )
                    activeEffects.forEach { effect ->
                        LegacyTextHandler.sendFormattedMessage(
                            sender,
                            "<yellow>- ${effect.getKey().key} (Amplifier: ${effect.amplifier}, Duration: ${effect.duration})"
                        )
                    }
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при получении списка эффектов: ${e.message}")
                    plugin.logger.severe("Error listing effects: ${e.stackTraceToString()}")
                }
            })

        // Subcommand: /customeffects create-type <typeKey> <name> <namespace> <category> <behavior> <handlerPath>
        val createType = CommandAPICommand("create-type")
            .withArguments(StringArgument("typeKey"))
            .withArguments(StringArgument("name"))
            .withArguments(StringArgument("namespace"))
            .withArguments(StringArgument("category").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                EffectCategory.values().map { it.name }.toTypedArray()
            }))
            .withArguments(StringArgument("behavior").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                EffectBehavior.values().map { it.name }.toTypedArray()
            }))
            .withArguments(StringArgument("handlerPath"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val typeKey = args.get("typeKey") as String
                    val name = args.get("name") as String
                    val namespace = args.get("namespace") as String
                    val category = EffectCategory.valueOf(args.get("category") as String)
                    val behavior = EffectBehavior.valueOf(args.get("behavior") as String)
                    val handlerPath = args.get("handlerPath") as String
                    if (plugin.handler.createEffectType(typeKey, name, namespace, category, behavior, handlerPath)) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<green>Тип эффекта '$typeKey' успешно создан.")
                    } else {
                        LegacyTextHandler.sendFormattedMessage(sender, "<red>Не удалось создать тип эффекта '$typeKey'.")
                    }
                } catch (e: IllegalArgumentException) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Неверные параметры: ${e.message}")
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при создании типа эффекта: ${e.message}")
                    plugin.logger.severe("Error creating effect type: ${e.stackTraceToString()}")
                }
            })

        // Subcommand: /customeffects update-type <typeKey> <name> <namespace> <category> <behavior> <handlerPath>
        val updateType = CommandAPICommand("update-type")
            .withArguments(StringArgument("typeKey"))
            .withArguments(StringArgument("name"))
            .withArguments(StringArgument("namespace"))
            .withArguments(StringArgument("category").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                EffectCategory.values().map { it.name }.toTypedArray()
            }))
            .withArguments(StringArgument("behavior").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                EffectBehavior.values().map { it.name }.toTypedArray()
            }))
            .withArguments(StringArgument("handlerPath"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val typeKey = args.get("typeKey") as String
                    val name = args.get("name") as String
                    val namespace = args.get("namespace") as String
                    val category = EffectCategory.valueOf(args.get("category") as String)
                    val behavior = EffectBehavior.valueOf(args.get("behavior") as String)
                    val handlerPath = args.get("handlerPath") as String
                    if (plugin.handler.updateEffectType(typeKey, name, namespace, category, behavior, handlerPath)) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<green>Тип эффекта '$typeKey' успешно обновлен.")
                    } else {
                        LegacyTextHandler.sendFormattedMessage(sender, "<red>Не удалось обновить тип эффекта '$typeKey'.")
                    }
                } catch (e: IllegalArgumentException) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Неверные параметры: ${e.message}")
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при обновлении типа эффекта: ${e.message}")
                    plugin.logger.severe("Error updating effect type: ${e.stackTraceToString()}")
                }
            })

        // Subcommand: /customeffects delete-type <typeKey>
        val deleteType = CommandAPICommand("delete-type")
            .withArguments(StringArgument("typeKey"))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val typeKey = args.get("typeKey") as String
                    if (plugin.handler.deleteEffectType(typeKey)) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<green>Тип эффекта '$typeKey' успешно удален.")
                    } else {
                        LegacyTextHandler.sendFormattedMessage(sender, "<red>Не удалось удалить тип эффекта '$typeKey'.")
                    }
                } catch (e: IllegalArgumentException) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Неверные параметры: ${e.message}")
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Ошибка при удалении типа эффекта: ${e.message}")
                    plugin.logger.severe("Error deleting effect type: ${e.stackTraceToString()}")
                }
            })

        // Register main command
        CommandAPICommand("customeffects")
            .withSubcommands(apply, reload, create, update, delete, clear, list, createType, updateType, deleteType)
            .withPermission("customeffects.admin")
            .register()
    }
}