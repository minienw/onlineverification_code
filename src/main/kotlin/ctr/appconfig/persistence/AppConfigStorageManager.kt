/*
 *  Copyright (c) 2021 De Staat der Nederlanden, Ministerie van Volksgezondheid, Welzijn en Sport.
 *   Licensed under the EUROPEAN UNION PUBLIC LICENCE v. 1.2
 *
 *   SPDX-License-Identifier: EUPL-1.2
 *
 */

package nl.rijksoverheid.ctr.appconfig.persistence

import okio.BufferedSource
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException

interface AppConfigStorageManager {
    fun storageFile(file: File, fileContents: String): StorageResult
    fun areConfigFilesPresent(): Boolean
    fun getFileAsBufferedSource(file: File): BufferedSource?
}

class AppConfigStorageManagerImpl(private val cacheDir: String): AppConfigStorageManager {
    override fun storageFile(file: File, fileContents: String): StorageResult {
        return try {
            file.parentFile?.mkdirs()
            file.bufferedWriter().use {
                it.write(fileContents)
            }
            StorageResult.Success
        } catch (exception: IOException) {
            StorageResult.Error(exception.message ?: "error storing: $file")
        }
    }

    //TODO file locations...
    override fun areConfigFilesPresent(): Boolean {
        val configFile = File(cacheDir, "config.json") //TODO general config +
        val publicKeysFile = File(cacheDir, "public_keys.json") //TODO required for the golib and HAVE to be in a specific folder for that to find them.
        val businessRulesFile = File(cacheDir, "business_rules.json") //TODO EU (cert config')
        val customBusinessRulesFile = File(cacheDir, "custom_business_rules.json") //TODO Local NL rules - from/to rules
        val valueSetsFile = File(cacheDir, "value_sets.json") //TODO big... definitions of vaccines etc... must go into the BR checker too..

        return configFile.exists() && publicKeysFile.exists() && businessRulesFile.exists() &&
                customBusinessRulesFile.exists() && valueSetsFile.exists()
    }

    override fun getFileAsBufferedSource(file: File): BufferedSource? {
        if (file.exists()) {
            return file.source().buffer()
        }
        return null
    }
}

sealed class StorageResult {
    object Success: StorageResult()
    data class Error(val message: String): StorageResult()
}
