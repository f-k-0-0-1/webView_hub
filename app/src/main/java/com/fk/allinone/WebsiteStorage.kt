package com.fk.allinone

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


// ====================================================================
// DATASTORE
// ====================================================================

private val Context.websiteDataStore by preferencesDataStore(
    name = "website_storage"
)


// ====================================================================
// WEBSITE STORAGE
// ====================================================================

class WebsiteStorage(
    private val context: Context
) {

    companion object {

        private val WEBSITES_KEY =
            stringPreferencesKey("websites")
    }


    // ====================================================================
    // SAVE WEBSITES
    // ====================================================================

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


        context.websiteDataStore.edit { preferences ->

            preferences[
                WEBSITES_KEY
            ] = data
        }
    }


    // ====================================================================
    // LOAD WEBSITES
    // ====================================================================

    suspend fun loadWebsites(): MutableList<Website> {

        val preferences =
            context.websiteDataStore.data.first()


        val data =
            preferences[
                WEBSITES_KEY
            ] ?: ""


        if (data.isBlank()) {

            return mutableListOf()
        }


        val result =
            mutableListOf<Website>()


        data.split("|||").forEach { websiteData ->

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


    // ====================================================================
    // CLEAR STORED WEBSITES
    // ====================================================================

    suspend fun clear() {

        context.websiteDataStore.edit { preferences ->

            preferences.remove(
                WEBSITES_KEY
            )
        }
    }
}