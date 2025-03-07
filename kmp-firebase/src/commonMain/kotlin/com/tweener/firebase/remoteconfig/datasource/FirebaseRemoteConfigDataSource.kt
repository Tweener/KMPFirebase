package com.tweener.firebase.remoteconfig.datasource

import com.tweener.firebase.remoteconfig.FirebaseRemoteConfigService
import io.github.aakira.napier.Napier

/**
 * @author Vivien Mahe
 * @since 16/12/2023
 */
class FirebaseRemoteConfigDataSource(
    private val firebaseRemoteConfigService: FirebaseRemoteConfigService,
) {

    suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        firebaseRemoteConfigService
            .get<Boolean>(key = key, defaultValue = defaultValue)
            .also { Napier.d { "Boolean '$key has value: $it" } }

    suspend fun getLong(key: String, defaultValue: Long): Long =
        firebaseRemoteConfigService
            .get<Long>(key = key, defaultValue = defaultValue)
            .also { Napier.d { "Long '$key' has value: $it" } }

    suspend fun getString(key: String, defaultValue: String): String =
        firebaseRemoteConfigService
            .get<String>(key = key, defaultValue = defaultValue)
            .also { Napier.d { "String '$key' has value: $it" } }
}
