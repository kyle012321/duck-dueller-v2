package best.spaghetcodes.duckdueller.bot.bots

import best.spaghetcodes.duckdueller.DuckDueller
import best.spaghetcodes.duckdueller.bot.BotBase
import best.spaghetcodes.duckdueller.bot.features.Bow
import best.spaghetcodes.duckdueller.bot.features.MovePriority
import best.spaghetcodes.duckdueller.bot.player.Inventory
import best.spaghetcodes.duckdueller.bot.player.Mouse
import best.spaghetcodes.duckdueller.bot.player.Movement
import best.spaghetcodes.duckdueller.utils.*
import net.minecraft.init.Blocks
import net.minecraft.util.Vec3

class Classic : BotBase("/play duels_bow_duel"), Bow, MovePriority {

    override fun getName(): String {
        return "Classic"
    }

    init {
        setStatKeys(
            mapOf(
                "wins" to "player.stats.Duels.classic_duel_wins",
                "losses" to "player.stats.Duels.classic_duel_losses",
                "ws" to "player.stats.Duels.current_classic_winstreak",
            )
        )
    }

    var shotsFired = 0
    var maxArrows = 100

    override fun onGameStart() {
        Movement.startSprinting()
        Movement.startForward()
        TimeUtils.setTimeout(Movement::startJumping, RandomUtils.randomIntInRange(400, 1200))
    }

    override fun onGameEnd() {
        shotsFired = 0
        Mouse.stopLeftAC()
        Mouse.stopTracking()
        Movement.clearAll()
    }

    override fun onTick() {
        if (opponent() != null && mc.theWorld != null && mc.thePlayer != null) {
            val distance = EntityUtils.getDistanceNoY(mc.thePlayer, opponent())

            // Always hold a bow
            if (mc.thePlayer.heldItem == null || !mc.thePlayer.heldItem.unlocalizedName.lowercase().contains("bow")) {
                Inventory.setInvItem("bow")
            }

            // Always track the opponent
            if (distance < (DuckDueller.config?.maxDistanceLook ?: 150)) {
                Mouse.startTracking()
            } else {
                Mouse.stopTracking()
            }

            // Only shoot arrows, no melee attacks
            if (distance in 5.0..30.0 && shotsFired < maxArrows) {
                useBow(distance, fun() {
                    shotsFired++
                })
            }

            // Avoid close combat
            if (distance < 5.0) {
                Movement.startBackward()
            } else {
                Movement.stopBackward()
            }

            // Strafe and jump randomly to avoid enemy shots
            if (distance > 5.0) {
                Movement.startJumping()
            } else {
                Movement.stopJumping()
            }
        }
    }
}
