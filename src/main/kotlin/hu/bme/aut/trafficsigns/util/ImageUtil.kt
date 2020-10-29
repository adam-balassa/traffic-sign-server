package hu.bme.aut.trafficsigns.util

import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.geometry.Positions
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.time.OffsetDateTime
import java.util.*
import javax.imageio.ImageIO


fun imageToBase64(filePath: String): String {
    val folder = "src/main/resources/images"
    val image = Thumbnails.of(File(Path.of(folder, filePath).toUri()))
            .crop(Positions.CENTER)
            .size(256, 256)
            .asBufferedImage()
    val out = ByteArrayOutputStream()

    ImageIO.write(image, "PNG", out)
    val bytes: ByteArray = out.toByteArray()

    return Base64.getEncoder().encodeToString(bytes)
}

fun base64ToImage(base64: String): String {
    val decodedBytes: ByteArray = Base64.getDecoder().decode(base64)
    val path = Path.of("tmp", "${OffsetDateTime.now().toEpochSecond()}.png")
    Files.createDirectories(path.parent)
    Files.write(path, decodedBytes)
    return path.toAbsolutePath().toString()
}

fun deleteImage(path: String) {
    Files.deleteIfExists(Path.of(path))
}