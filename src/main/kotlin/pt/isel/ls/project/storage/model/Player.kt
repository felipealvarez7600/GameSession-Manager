package pt.isel.ls.project.storage.model

import kotlinx.serialization.Serializable
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

@Serializable
data class Player (
    val id: Int,
    val name: String,
    val email: String,
    val details: String? = null,
    val image: ByteArray? = null,
    val password:String
){
    companion object{
        private const val HASHED_PASSWORD_LENGTH = 64
        fun hashPassword(password: String): String {
            // Hash using SHA-256
            val digest = MessageDigest.getInstance("SHA-256")
            val encodedhash = digest.digest(
                password.toByteArray(StandardCharsets.UTF_8)
            )

            // Convert to hexadecimal
            val sb = StringBuilder()
            for (b in encodedhash) {
                val hex = Integer.toHexString(b.toInt())
                if (hex.length == 1) {
                    sb.append('0')
                }
                sb.append(hex)
            }

            return sb.toString().substring(0 until HASHED_PASSWORD_LENGTH)
        }

        fun checkPassword(password: String, hash: String) =
            hashPassword(password) == hash
    }
}