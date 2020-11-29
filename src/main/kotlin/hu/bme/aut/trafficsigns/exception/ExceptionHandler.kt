package hu.bme.aut.trafficsigns.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.io.PrintWriter
import java.io.StringWriter

@ControllerAdvice
class ExceptionHandler () {
    private val log = LoggerFactory.getLogger(hu.bme.aut.trafficsigns.exception.ExceptionHandler::class.java)


    @ExceptionHandler(InvalidDetectorResponse::class)
    fun internalError(e: InvalidDetectorResponse): ResponseEntity<ErrorResponse> {
        return ResponseEntity(e.toErrorResponse(), HttpStatus.BAD_GATEWAY)
    }


    @ExceptionHandler(Throwable::class)
    fun genericException(e: Throwable): ResponseEntity<ErrorResponse> {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        log.error(sw.toString())
        return ResponseEntity(ErrorResponse(1000, e.message ?: "Internal server error"),
                HttpStatus.INTERNAL_SERVER_ERROR)
    }
}