package github.gilbertokpl.total.util

import github.gilbertokpl.core.external.task.SynchronizationContext
import github.gilbertokpl.total.TotalEssentialsJava
import github.gilbertokpl.total.cache.local.PlayerData
import github.gilbertokpl.total.cache.local.ShopData
import github.gilbertokpl.total.config.files.LangConfig
import github.gilbertokpl.total.config.files.MainConfig
import org.apache.commons.io.IOUtils
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.json.JSONObject
import java.net.URL


internal object PlayerUtil {

    fun sendAllMessage(message: String) {
        for (p in TotalEssentialsJava.basePlugin.getReflection().getPlayers()) {
            p.sendMessage(message)
        }
    }

    fun sendMessage(player: String, message: String) {
        val p = TotalEssentialsJava.instance.server.getPlayerExact(player.lowercase()) ?: return
        p.sendMessage(message)
    }

    fun getIntOnlinePlayers(vanish: Boolean): Int {
        var amount = TotalEssentialsJava.basePlugin.getReflection().getPlayers()
        if (!vanish) {
            amount = amount.filter {
                PlayerData.vanishCache[it] != null && !PlayerData.vanishCache[it]!!
            }
        }
        return amount.size
    }

    fun getNumberGameMode(gameMode: GameMode): Int {
        return try {
            when (gameMode) {
                GameMode.SURVIVAL -> 0
                GameMode.CREATIVE -> 1
                GameMode.ADVENTURE -> 2
                GameMode.SPECTATOR -> 3
            }
        } catch (e: Throwable) {
            when (gameMode) {
                GameMode.SURVIVAL -> 0
                GameMode.CREATIVE -> 1
                else -> {
                    0
                }
            }
        }
    }

    fun getGameModeNumber(number: String): GameMode {
        return when (number.lowercase()) {
            "0" -> GameMode.SURVIVAL
            "1" -> GameMode.CREATIVE
            "survival" -> GameMode.SURVIVAL
            "creative" -> GameMode.CREATIVE
            "adventure" -> try {
                GameMode.ADVENTURE
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }

            "spectactor" -> try {
                GameMode.SPECTATOR
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }

            "2" -> try {
                GameMode.ADVENTURE
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }

            "3" -> try {
                GameMode.SPECTATOR
            } catch (e: NoSuchMethodError) {
                GameMode.SURVIVAL
            }

            else -> GameMode.SURVIVAL
        }
    }

    fun checkPlayer(address: String): List<String> {
        try {
            if (address.contains("127.0.0.1")) return listOf(
                "País: Local",
                "Estado: Local",
                "Cidade: Local",
                "false"
            )
            val result = JSONObject(
                IOUtils.toString(
                    URL("http://ip-api.com/json$address?fields=status,country,regionName,city,hosting,query"),
                    "UTF-8"
                )
            )
            if (!(result["status"]?.equals("fail"))!!) {
                return listOf(
                    "País: ${result.get("country")}",
                    "Estado: ${result.get("regionName")}",
                    "Cidade: ${result.get("city")}",
                    "${result.get("hosting")}"
                )
            }
        } catch (e : Exception) {
            return listOf("Erro API").toList()
        }

        return listOf("Erro API").toList()
    }

    fun shopTeleport(p: Player, shop: String) {
        val loc = ShopData.shopLocation[shop] ?: return
        teleportWithTime(
            p,
            loc,
            MainConfig.homesTimeToTeleport,
            LangConfig.shopTeleport.replace("%player%", shop),
            "shop"
        )
    }

    fun teleportWithTime(p: Player, location: Location, time: Int, message: String?, locationName: String) {
        if (p.hasPermission("totalessentials.bypass.teleport") || time == 0) {
            p.teleport(location)
            if (message != null) {
                p.sendMessage(message)
            }
            return
        }
        val inTeleport = PlayerData.inTeleport[p]

        if (inTeleport != null && inTeleport) {
            p.sendMessage(LangConfig.generalInTeleport)
            return
        }

        TotalEssentialsJava.basePlugin.getTask().async {
            waitFor(time.toLong() * 20)
            try {
                switchContext(SynchronizationContext.SYNC)
                PlayerData.inTeleport[p] = false
                p.teleport(location)
                p.sendMessage(message)
            } catch (ex: Throwable) {
                ex.printStackTrace()
            }
        }

        p.sendMessage(
            LangConfig.generalTimeToTeleport.replace("%local%", locationName).replace("%time%", time.toString())
        )

    }

}
