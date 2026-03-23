package com.bcponline.dailyoffice.data

import dailyoffice.composeapp.generated.resources.Res

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlNode

object FileRegistry {
    private val cache = mutableMapOf<String, YamlNode>()
    private val mutex = Mutex()

    fun getFile(name: String): YamlNode? {
        return cache[name]
    }

    suspend fun loadFiles(vararg names: String) = mutex.withLock {
        names.forEach { name ->
            if (!cache.containsKey(name)) {
                try {
                    val bytes = Res.readBytes("files/$name.yml")
                    val parsed: YamlNode = Yaml.default.parseToYamlNode(bytes.decodeToString())
                    cache[name] = parsed
                } catch (e: Exception) {
                    println("Failed to load $name: ${e.message}")
                }
            }
        }
    }
}