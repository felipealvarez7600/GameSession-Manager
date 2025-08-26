package pt.isel.ls.project.storage.model

import kotlinx.serialization.Serializable
import pt.isel.ls.project.api.serializers.InstantSerializer
import java.time.Instant

@Serializable
data class Session (
    val id: Int,
    val capacity: Int,
    @Serializable(with = InstantSerializer::class) val date: Instant,
    val gameId: Int,
    val playersId: HashSet<Int>,
    val state: SessionState
)

enum class SessionState {
    OPEN,
    CLOSED
}