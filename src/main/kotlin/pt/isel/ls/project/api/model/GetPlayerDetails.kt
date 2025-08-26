package pt.isel.ls.project.api.model

import kotlinx.serialization.Serializable

@Serializable
class GetPlayerDetails (
    val id: Int,
    val name: String,
    val email: String,
    val details: String? = null,
    val image: String? = null,
)