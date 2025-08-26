package pt.isel.ls.project.api.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisteringGame(val name: String, val developer: String, val genres: List<String>)