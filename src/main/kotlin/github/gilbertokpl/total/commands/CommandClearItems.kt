package github.gilbertokpl.total.commands

import github.gilbertokpl.core.external.command.CommandTarget
import github.gilbertokpl.core.external.command.annotations.CommandPattern
import github.gilbertokpl.core.external.task.SynchronizationContext
import github.gilbertokpl.total.TotalEssentials
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import github.gilbertokpl.total.util.PlayerUtil
import github.gilbertokpl.total.util.WorldUtil
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandClearItems : github.gilbertokpl.core.external.command.CommandCreator("clearitems") {

    override fun commandPattern(): CommandPattern {
        return CommandPattern(
            aliases = listOf("limparchao"),
            active = MainConfig.ClearitemsActivated,
            target = CommandTarget.ALL,
            countdown = 0,
            permission = "totalessentials.commands.clearitems",
            minimumSize = 0,
            maximumSize = 0,
            usage = listOf(
                "/limpachao",
            )
        )
    }

    override fun funCommand(s: CommandSender, label: String, args: Array<out String>): Boolean {

        WorldUtil.clearItems()

        return false
    }
}
