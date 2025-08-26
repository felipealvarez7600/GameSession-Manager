package pt.isel.ls.project.api.model

import kotlinx.serialization.Serializable

@Serializable
data class GameQuery(val genres: List<String>, val developer: String)
