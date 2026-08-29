package com.fk.allinone

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.floatingactionbutton.FloatingActionButton

import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    // ================================================================
    // VIEWS
    // ================================================================

    private lateinit var websiteRecyclerView: RecyclerView
    private lateinit var addWebsiteButton: FloatingActionButton


    // ================================================================
    // ADAPTER
    // ================================================================

    private lateinit var websiteAdapter


            : WebsiteAdapter


    // ================================================================
    // STORAGE
    // ================================================================

    private lateinit var websiteStorage: WebsiteStorage


    // ================================================================
    // WEBSITE DATA
    // ================================================================

    private val websites =
        mutableListOf<Website>()


    // ================================================================
    // ON CREATE
    // ================================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )


        setContentView(
            R.layout.activity_main
        )


        setupSystemBars()

        initializeViews()

        setupWebsiteGrid()

        setupAddButton()


        // ------------------------------------------------------------
        // STORAGE
        // ------------------------------------------------------------

        websiteStorage =
            WebsiteStorage(
                applicationContext
            )


        // ------------------------------------------------------------
        // LOAD WEBSITES
        // ------------------------------------------------------------

        loadWebsites()
    }


    // ================================================================
    // SYSTEM BARS
    // ================================================================

    private fun setupSystemBars() {

        WindowCompat.setDecorFitsSystemWindows(
            window,
            true
        )


        @Suppress("DEPRECATION")
        window.statusBarColor =
            Color.WHITE


        @Suppress("DEPRECATION")
        window.navigationBarColor =
            Color.WHITE


        val controller =
            WindowInsetsControllerCompat(
                window,
                window.decorView
            )


        controller.isAppearanceLightStatusBars =
            true


        controller.isAppearanceLightNavigationBars =
            true
    }


    // ================================================================
    // INITIALIZE VIEWS
    // ================================================================

    private fun initializeViews() {

        websiteRecyclerView =
            findViewById(
                R.id.websiteRecyclerView
            )


        addWebsiteButton =
            findViewById(
                R.id.addWebsiteButton
            )
    }


    // ================================================================
    // WEBSITE GRID
    // ================================================================

    private fun setupWebsiteGrid() {

        websiteRecyclerView.layoutManager =
            GridLayoutManager(
                this,
                2
            )


        websiteAdapter =
            WebsiteAdapter(

                websites =
                    websites,


                // ----------------------------------------------------
                // CARD CLICK
                // ----------------------------------------------------

                onWebsiteClicked = { website ->

                    openWebsite(
                        website
                    )
                },


                // ----------------------------------------------------
                // CARD DELETE
                // ----------------------------------------------------

                onWebsiteDeleted = {

                    lifecycleScope.launch {

                        saveWebsites()
                    }
                }
            )


        websiteRecyclerView.adapter =
            websiteAdapter
    }


    // ================================================================
    // DEFAULT WEBSITES
    // ================================================================

    private fun getDefaultWebsites():
            List<Website> {

        return listOf(

            Website(
                id = "amazon",
                name = "Amazon",
                url = "https://www.amazon.in",
                iconUrl =
                    "https://www.amazon.in/favicon.ico"
            ),

            Website(
                id = "facebook",
                name = "Facebook",
                url = "https://www.facebook.com",
                iconUrl =
                    "https://www.facebook.com/favicon.ico"
            ),

            Website(
                id = "instagram",
                name = "Instagram",
                url = "https://www.instagram.com",
                iconUrl =
                    "https://www.instagram.com/favicon.ico"
            ),

            Website(
                id = "reddit",
                name = "Reddit",
                url = "https://www.reddit.com",
                iconUrl =
                    "https://www.reddit.com/favicon.ico"
            )
        )
    }


    // ================================================================
    // LOAD WEBSITES
    // ================================================================

    private fun loadWebsites() {

        lifecycleScope.launch {

            val savedWebsites =
                websiteStorage.loadWebsites()


            websites.clear()


            // --------------------------------------------------------
            // FIRST LAUNCH
            // --------------------------------------------------------

            if (
                savedWebsites.isEmpty()
            ) {

                /*
                 * Nothing has been saved yet.
                 *
                 * This means this is the first launch.
                 *
                 * Create the four default cards.
                 */

                websites.addAll(
                    getDefaultWebsites()
                )


                /*
                 * IMPORTANT:
                 *
                 * Save the defaults immediately.
                 *
                 * From this point onward, the defaults
                 * behave exactly like user-created cards.
                 */

                saveWebsites()

            } else {

                /*
                 * Data already exists.
                 *
                 * Load EXACTLY what was saved.
                 *
                 * Do NOT recreate deleted cards.
                 */

                websites.addAll(
                    savedWebsites
                )
            }


            // --------------------------------------------------------
            // REFRESH UI
            // --------------------------------------------------------

            websiteAdapter.notifyDataSetChanged()
        }
    }


    // ================================================================
    // ADD BUTTON
    // ================================================================

    private fun setupAddButton() {

        addWebsiteButton.setOnClickListener {

            showAddWebsiteDialog()
        }
    }


    // ================================================================
    // ADD WEBSITE DIALOG
    // ================================================================

    private fun showAddWebsiteDialog() {

        val input =
            EditText(this)


        input.hint =
            "https://example.com"


        input.setSingleLine(
            true
        )


        input.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_URI


        // ------------------------------------------------------------
        // INPUT SPACING
        // ------------------------------------------------------------

        input.setPadding(
            32,
            12,
            32,
            14
        )


        input.minHeight =
            56


        // ------------------------------------------------------------
        // DIALOG
        // ------------------------------------------------------------

        val dialog =
            AlertDialog.Builder(this)

                .setTitle(
                    "Add Website"
                )

                .setMessage(
                    "Enter the website URL"
                )

                .setView(
                    input
                )

                .setNegativeButton(
                    "Cancel",
                    null
                )

                .setPositiveButton(
                    "Add",
                    null
                )

                .create()


        // ------------------------------------------------------------
        // ADD BUTTON
        // ------------------------------------------------------------

        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val enteredUrl =
                    input.text
                        .toString()
                        .trim()


                addWebsite(
                    enteredUrl,
                    dialog
                )
            }
        }


        dialog.show()
    }


    // ================================================================
    // ADD WEBSITE
    // ================================================================

    private fun addWebsite(
        enteredUrl: String,
        dialog: AlertDialog
    ) {

        // ------------------------------------------------------------
        // EMPTY URL
        // ------------------------------------------------------------

        if (
            enteredUrl.isBlank()
        ) {

            showMessage(
                "Please enter a website URL"
            )

            return
        }


        var url =
            enteredUrl


        // ------------------------------------------------------------
        // ADD HTTPS
        // ------------------------------------------------------------

        if (
            !url.startsWith(
                "http://",
                ignoreCase = true
            ) &&
            !url.startsWith(
                "https://",
                ignoreCase = true
            )
        ) {

            url =
                "https://$url"
        }


        // ------------------------------------------------------------
        // VALIDATE URL
        // ------------------------------------------------------------

        if (
            !Patterns.WEB_URL
                .matcher(url)
                .matches()
        ) {

            showMessage(
                "Please enter a valid website URL"
            )

            return
        }


        // ------------------------------------------------------------
        // PARSE URL
        // ------------------------------------------------------------

        val uri =
            Uri.parse(
                url
            )


        val host =
            uri.host


        if (
            host.isNullOrBlank()
        ) {

            showMessage(
                "Invalid website address"
            )

            return
        }


        // ------------------------------------------------------------
        // WEBSITE NAME
        // ------------------------------------------------------------

        val websiteName =
            createWebsiteName(
                host
            )


        // ------------------------------------------------------------
        // FAVICON URL
        // ------------------------------------------------------------

        val iconUrl =
            "${uri.scheme}://${uri.host}/favicon.ico"


        // ------------------------------------------------------------
        // CREATE WEBSITE
        // ------------------------------------------------------------

        val website =
            Website(

                id =
                    System.currentTimeMillis()
                        .toString(),

                name =
                    websiteName,

                url =
                    url,

                iconUrl =
                    iconUrl
            )


        // ------------------------------------------------------------
        // ADD TO LIST
        // ------------------------------------------------------------

        websites.add(
            website
        )


        // ------------------------------------------------------------
        // UPDATE UI
        // ------------------------------------------------------------

        websiteAdapter.notifyItemInserted(
            websites.lastIndex
        )


        // ------------------------------------------------------------
        // SAVE EVERYTHING
        // ------------------------------------------------------------

        lifecycleScope.launch {

            saveWebsites()
        }


        // ------------------------------------------------------------
        // CLOSE DIALOG
        // ------------------------------------------------------------

        dialog.dismiss()


        showMessage(
            "$websiteName added"
        )
    }


    // ================================================================
    // SAVE ALL WEBSITES
    // ================================================================

    private suspend fun saveWebsites() {

        /*
         * IMPORTANT:
         *
         * We now save ALL websites.
         *
         * This includes:
         *
         * - Amazon
         * - Facebook
         * - Instagram
         * - Reddit
         * - User-created websites
         *
         * Therefore deleting ANY card is persistent.
         */

        websiteStorage.saveWebsites(
            websites
        )
    }


    // ================================================================
    // CREATE WEBSITE NAME
    // ================================================================

    private fun createWebsiteName(
        host: String
    ): String {

        val cleanHost =
            host.removePrefix(
                "www."
            )


        val firstPart =
            cleanHost.substringBefore(
                "."
            )


        return firstPart.replaceFirstChar {

            if (
                it.isLowerCase()
            ) {

                it.titlecase()

            } else {

                it.toString()
            }
        }
    }


    // ================================================================
    // OPEN WEBSITE
    // ================================================================

    private fun openWebsite(
        website: Website
    ) {

        val intent =
            Intent(
                this,
                WebViewActivity::class.java
            )


        intent.putExtra(
            WebViewActivity.EXTRA_URL,
            website.url
        )


        intent.putExtra(
            WebViewActivity.EXTRA_NAME,
            website.name
        )


        startActivity(
            intent
        )
    }


    // ================================================================
    // SHOW MESSAGE
    // ================================================================

    private fun showMessage(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}