package pt.isel.ls.project.api.model

import kotlinx.serialization.Serializable
import pt.isel.ls.project.api.serializers.InstantSerializer
import pt.isel.ls.project.storage.model.SessionState
import java.time.Instant

@Serializable
data class GetSessionReturn(
    val id: Int,
    val capacity: Int,
    @Serializable(with = InstantSerializer::class) val date: Instant,
    val gameId: Int,
    val playersId: HashSet<Int>,
    val state: SessionState,
    val gameName: String
)