package br.dev.fuvest.data

interface FileStore {
    fun read(name: String): String?
    fun write(name: String, content: String)
}

expect fun createFileStore(): FileStore
