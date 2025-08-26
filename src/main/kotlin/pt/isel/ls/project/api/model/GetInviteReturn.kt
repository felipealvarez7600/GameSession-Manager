package pt.isel.ls.project.api.model

import kotlinx.serialization.Serializable
import pt.isel.ls.project.storage.model.Player
import pt.isel.ls.project.storage.model.Session

@Serializable
data class GetInviteReturn (
    val session : GetSessionReturn,
    val fromPlayer : Player
)