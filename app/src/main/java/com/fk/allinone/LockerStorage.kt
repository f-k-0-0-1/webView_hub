package com.fk.allinone

import android.content.Context

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.first


private val Context.lockerDataStore by preferencesDataStore(
    name = "locker_storage"
)


class LockerStorage(
    private val context: Context
) {

    companion object {

        private val PIN_KEY =
            stringPreferencesKey("locker_pin")

        private val WEBSITES_KEY =
            stringPreferencesKey("locker_websites")
    }


    // ================================================================
    // PIN
    // ================================================================

    suspend fun savePin(
        pin: String
    ) {

        context.lockerDataStore.edit { preferences ->

            preferences[PIN_KEY] =
                pin
        }
    }


    suspend fun getPin(): String? {

        val preferences =
            context.lockerDataStore.data.first()

        return preferences[PIN_KEY]
    }


    suspend fun hasPin(): Boolean {

        return getPin() != null
    }


    suspend fun deletePin() {

        context.lockerDataStore.edit { preferences ->

            preferences.remove(
                PIN_KEY
            )
        }
    }


    // ================================================================
    // SAVE LOCKER WEBSITES
    // ================================================================

    suspend fun saveWebsites(
        websites: List<Website>
    ) {

        val data =
            websites.joinToString(
                separator = "|||"
            ) { website ->

                listOf(
                    website.id,
                    website.name,
                    website.url,
                    website.iconUrl ?: ""
                ).joinToString(
                    separator = ":::"
                )
            }


        context.lockerDataStore.edit { preferences ->

            preferences[WEBSITES_KEY] =
                data
        }
    }


    // ================================================================
    // LOAD LOCKER WEBSITES
    // ================================================================

    suspend fun loadWebsites():
            MutableList<Website> {

        val preferences =
            context.lockerDataStore.data.first()


        val data =
            preferences[WEBSITES_KEY]
                ?: ""


        if (data.isBlank()) {

            return mutableListOf()
        }


        val result =
            mutableListOf<Website>()


        data.split("|||")
            .forEach { websiteData ->

                val parts =
                    websiteData.split(":::")


                if (parts.size >= 4) {

                    result.add(

                        Website(

                            id =
                                parts[0],

                            name =
                                parts[1],

                            url =
                                parts[2],

                            iconUrl =
                                parts[3].ifBlank {
                                    null
                                }
                        )
                    )
                }
            }


        return result
    }


    // ================================================================
    // CLEAR LOCKER WEBSITES
    // ================================================================

    suspend fun clearWebsites() {

        context.lockerDataStore.edit { preferences ->

            preferences.remove(
                WEBSITES_KEY
            )
        }
    }
}