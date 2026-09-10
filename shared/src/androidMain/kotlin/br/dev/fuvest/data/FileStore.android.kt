package br.dev.fuvest.data

import android.content.Context
import java.io.File

object AndroidStorage {
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    internal fun requireDir(): File {
        val ctx = appContext
            ?: error("AndroidStorage.init(context) não foi chamado antes de usar o FileStore")
        return File(ctx.filesDir, "fuvest").apply { mkdirs() }
    }
}

private class AndroidFileStore : FileStore {
    override fun read(name: String): String? {
        val file = File(AndroidStorage.requireDir(), name)
        return if (file.exists()) file.readText() else null
    }

    override fun write(name: String, content: String) {
        File(AndroidStorage.requireDir(), name).writeText(content)
    }
}

actual fun createFileStore(): FileStore = AndroidFileStore()
