package org.rsmod.plugins.api.update.player.mask

import kotlin.math.atan2
import org.rsmod.game.model.domain.Direction
import org.rsmod.game.model.domain.translate
import org.rsmod.game.model.mob.Player
import org.rsmod.plugins.api.model.appearance.Body
import org.rsmod.plugins.api.model.appearance.Equipment
import org.rsmod.plugins.api.protocol.packet.update.AppearanceMask
import org.rsmod.plugins.api.protocol.packet.update.DirectionMask

private val EQUIPMENT_BODY_PART = mapOf(
    Equipment.CHEST to Body.CHEST,
    Equipment.ARMS to Body.SLEEVES,
    Equipment.LEGS to Body.LEGS,
    Equipment.HAIR to Body.HAIR,
    Equipment.GLOVES to Body.HANDS,
    Equipment.BOOTS to Body.FEET,
    Equipment.BEARD to Body.BEARD
)

fun AppearanceMask.Companion.of(player: Player): AppearanceMask {
    val appearance = player.appearance
    return AppearanceMask(
        gender = appearance.gender,
        skull = appearance.skullIcon,
        overheadPrayer = appearance.overheadPrayer,
        npc = appearance.npcTransform,
        looks = player.looks(),
        colors = appearance.colors.toIntArray(),
        bas = appearance.bas.toIntArray(),
        username = player.username,
        combatLevel = 126,
        invisible = false
    )
}

fun DirectionMask.Companion.of(player: Player, face: Direction): DirectionMask {
    val coordinates = player.coords
    val translation = coordinates.translate(face)
    val atan = atan2(coordinates.x.toFloat() - translation.x, coordinates.y.toFloat() - translation.y)
    val degrees = (atan * 325.949).toInt() and 0x7FF
    return DirectionMask(degrees)
}

private fun Player.looks(): ByteArray {
    var position = 0
    val data = ByteArray(24)
    equipment.forEachIndexed { i, item ->
        if (item != null) {
            val value = 0x200 or item.id
            data[position++] = (value shr 8).toByte()
            data[position++] = (value and 0xFF).toByte()
        } else {
            val bodyPart = EQUIPMENT_BODY_PART[i]
            if (bodyPart != null) {
                val value = 0x100 or appearance.body[bodyPart]
                data[position++] = (value shr 8).toByte()
                data[position++] = (value and 0xFF).toByte()
            } else {
                data[position++] = 0
            }
        }
    }
    return data.copyOfRange(0, position)
}
