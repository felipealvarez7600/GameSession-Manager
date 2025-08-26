package pt.isel.ls.project.api.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdatePlayer (
    val details : String? = null,
    val image : String? = null
)