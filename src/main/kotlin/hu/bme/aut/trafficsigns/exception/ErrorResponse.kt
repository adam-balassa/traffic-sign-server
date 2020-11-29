package hu.bme.aut.trafficsigns.exception

data class ErrorResponse (
        val errorCode: Int,
        val message: String
)

class InvalidDetectorResponse (private val errorCode: Int, override val message: String): RuntimeException() {
    fun toErrorResponse(): ErrorResponse {
        return ErrorResponse(errorCode, message)
    }
}