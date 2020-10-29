package hu.bme.aut.trafficsigns.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Exception
import java.util.logging.LogManager
import java.util.logging.Logger

@ControllerAdvice
class ExceptionHandler () {
    private val log = LoggerFactory.getLogger(hu.bme.aut.trafficsigns.exception.ExceptionHandler::class.java)


    @ExceptionHandler(InternalException::class)
    fun internalError(e: InternalException): ResponseEntity<ErrorResponse> {
        return ResponseEntity(e.toErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @ExceptionHandler(Exception::class)
    fun genericException(e: Exception): ResponseEntity<ErrorResponse> {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        log.error(sw.toString())
        return ResponseEntity(ErrorResponse(1000, e.message ?: "Internal server error"),
                HttpStatus.INTERNAL_SERVER_ERROR)
    }
}