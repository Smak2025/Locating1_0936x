import java.io.FileInputStream
import java.io.InputStream
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

val localProperties = Properties().apply {
    val localFile = rootProject.file("local.properties")
    if (localFile.exists()) {
        FileInputStream(localFile).use { load(it) }
    }
}

val mapkitApiKey = localProperties.getProperty("MAPKIT_API_KEY")?.takeIf { it.isNotBlank() }
    ?: error("MAPKIT_API_KEY не найден в local.properties! " +
            "Добавьте ключ в файл local.properties в корне проекта")

// Сохраняем ключ для использования в модулях
rootProject.extra["mapkitApiKey"] = mapkitApiKey