package com.fk.allinone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var lockerButton: ImageButton


    // ================================================================
    // ADAPTER
    // ================================================================

    private lateinit var websiteAdapter: WebsiteAdapter


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


        // ------------------------------------------------------------
        // SYSTEM BARS
        // ------------------------------------------------------------

        SystemBarUtils.setup(
            window,
            findViewById(
                R.id.rootLayout
            )
        )


        // ------------------------------------------------------------
        // INITIALIZE
        // ------------------------------------------------------------

        initializeViews()

        setupWebsiteGrid()

        setupAddButton()

        setupLockerButton()


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


        lockerButton =
            findViewById(
                R.id.lockerButton
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


            if (
                savedWebsites.isEmpty()
            ) {

                websites.addAll(
                    getDefaultWebsites()
                )


                saveWebsites()

            } else {

                websites.addAll(
                    savedWebsites
                )
            }


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
    // LOCKER BUTTON
    // ================================================================

    private fun setupLockerButton() {

        lockerButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    LockerActivity::class.java
                )


            startActivity(
                intent
            )
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


        input.setPadding(
            32,
            12,
            32,
            14
        )


        input.minHeight =
            56


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


        val websiteName =
            createWebsiteName(
                host
            )


        val iconUrl =
            "${uri.scheme}://${uri.host}/favicon.ico"


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


        websites.add(
            website
        )


        websiteAdapter.notifyItemInserted(
            websites.lastIndex
        )


        lifecycleScope.launch {

            saveWebsites()
        }


        dialog.dismiss()


        showMessage(
            "$websiteName added"
        )
    }


    // ================================================================
    // SAVE WEBSITES
    // ================================================================

    private suspend fun saveWebsites() {

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
    // MESSAGE
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