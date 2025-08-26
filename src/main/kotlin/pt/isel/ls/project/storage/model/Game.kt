package pt.isel.ls.project.storage.model

import kotlinx.serialization.Serializable


@Serializable
data class Game (
    val id: Int,
    val name: String,
    val developer: String,
    val genres: HashSet<String>
)
