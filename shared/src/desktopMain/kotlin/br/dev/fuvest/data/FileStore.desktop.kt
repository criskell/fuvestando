package br.dev.fuvest.data

import java.io.File

private class DesktopFileStore : FileStore {
    private val dir: File by lazy {
        File(System.getProperty("user.home"), ".fuvest").apply { mkdirs() }
    }

    override fun read(name: String): String? {
        val file = File(dir, name)
        return if (file.exists()) file.readText() else null
    }

    override fun write(name: String, content: String) {
        File(dir, name).writeText(content)
    }
}

actual fun createFileStore(): FileStore = DesktopFileStore()
