package me.deadybbb.customeffects

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import me.deadybbb.ybbbbasicmodule.LegacyTextHandler

class CustomEffectsCommand(
    private val plugin: CustomEffects
) {
    fun registerCommand() {
        val apply = CommandAPICommand("apply")
            .withArguments(StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                plugin.server.onlinePlayers
                    .map { it.name }
                    .toList()
                    .toTypedArray()
            }))
            .withArguments(StringArgument("effect").replaceSuggestions(ArgumentSuggestions.strings { _ ->
                plugin.handler.getEffects()
                    .map { it.getKey().key }
                    .toList()
                    .toTypedArray()
            }))
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    val playerName = args.get("player") as String
                    val player = plugin.server.onlinePlayers.find { it.name == playerName }
                    val effectKey = args.get("effect") as String
                    val effect = plugin.handler.getEffectByKey(effectKey)
                    if (player == null || effect == null) {
                        LegacyTextHandler.sendFormattedMessage(sender, "<red>Не удалось выдать эффект $effectKey игроку $playerName")
                    } else {
                        plugin.handler.applyEffect(player, effect)
                        LegacyTextHandler.sendFormattedMessage(sender, "<green>Эффект $effectKey успешно выдан игроку $playerName")
                    }
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<red>Произошла ошибка!")
                    plugin.logger.severe("Error while trying executing effect: ${e.message}!")
                }
            })

        val reload = CommandAPICommand("reload")
            .executesPlayer(PlayerCommandExecutor { sender, args ->
                try {
                    plugin.handler.reloadEffects()
                    LegacyTextHandler.sendFormattedMessage(sender, "<green>Конфиг успешно перезагружен!")
                } catch (e: Exception) {
                    LegacyTextHandler.sendFormattedMessage(sender, "<redНе удалось перезагрузить конфиг!")
                    plugin.logger.severe("Error while trying reload config: ${e.message}!")
                }
            })

        CommandAPICommand("customeffects")
            .withSubcommands(apply, reload)
            .withPermission("customeffects.admin")
            .register()
    }
}