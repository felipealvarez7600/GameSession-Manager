package pt.isel.ls.project.api.model

import kotlinx.serialization.Serializable
import pt.isel.ls.project.api.serializers.InstantSerializer
import java.time.Instant

@Serializable
data class RegisteringSession(val capacity: Int, val gameName: String, @Serializable(with = InstantSerializer::class) val date: Instant)

