package hu.bme.aut.trafficsigns.util

import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.file.Path
import java.util.*

fun imageToBase64(filePath: String): String {
    val folder = "src/main/resources/images/"
    val fileContent: ByteArray = FileUtils.readFileToByteArray(File(folder + filePath))
    return "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent)
}