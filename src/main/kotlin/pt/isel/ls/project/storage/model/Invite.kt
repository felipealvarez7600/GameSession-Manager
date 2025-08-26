package pt.isel.ls.project.storage.model

import kotlinx.serialization.Serializable
import pt.isel.ls.project.api.serializers.InstantSerializer
import java.time.Instant

@Serializable
data class Invite (
    val fromPid: Int,
    val toPid: Int,
    val sessionId: Int,
    @Serializable(with = InstantSerializer::class)val expiration: Instant
)