package ru.yarsu.files

import org.http4k.format.Jackson
import java.nio.file.Path
import kotlin.io.path.isReadable

class Settings(settingsPath : Path){
    val salt: String

    init {
        if (!settingsPath.isReadable()) throw SettingsFileException("Configuration file $settingsPath does not exist")

        val file = settingsPath.toFile()
        val jsonDocument = file.readText()
        val node = Jackson.parse(jsonDocument)

        if (!node.hasNonNull("salt")) throw SettingsFileException("Configure file does not have \"salt\" field")

        salt = node["salt"].asText()
    }
}

class SettingsFileException(message: String): RuntimeException(message)