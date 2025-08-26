package pt.isel.ls.project.api.model

import kotlinx.serialization.Serializable
import org.eclipse.jetty.util.security.Password

@Serializable
data class RegisteringPlayer(val name: String, val email: String, val password: String)
