package com.fk.allinone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Patterns
import android.view.Gravity
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


class LockerActivity : AppCompatActivity() {

    // ================================================================
    // VIEWS
    // ================================================================

    private lateinit var lockerRecyclerView: RecyclerView

    private lateinit var lockerAddButton: FloatingActionButton

    private lateinit var backButton: ImageButton


    // ================================================================
    // ADAPTER
    // ================================================================

    private lateinit var lockerAdapter: WebsiteAdapter


    // ================================================================
    // STORAGE
    // ================================================================

    private lateinit var lockerStorage: LockerStorage


    // ================================================================
    // WEBSITE DATA
    // ================================================================

    private val lockerWebsites =
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
            R.layout.activity_locker
        )


        // ------------------------------------------------------------
        // SYSTEM BARS
        // ------------------------------------------------------------

        SystemBarUtils.setup(
            window,
            findViewById(
                R.id.lockerRoot
            )
        )


        // ------------------------------------------------------------
        // STORAGE
        // ------------------------------------------------------------

        lockerStorage =
            LockerStorage(
                applicationContext
            )


        // ------------------------------------------------------------
        // INITIALIZE
        // ------------------------------------------------------------

        initializeViews()

        setupRecyclerView()

        setupButtons()

        checkLockerStatus()
    }


    // ================================================================
    // INITIALIZE VIEWS
    // ================================================================

    private fun initializeViews() {

        lockerRecyclerView =
            findViewById(
                R.id.lockerRecyclerView
            )


        lockerAddButton =
            findViewById(
                R.id.lockerAddButton
            )


        backButton =
            findViewById(
                R.id.backButton
            )
    }


    // ================================================================
    // RECYCLER VIEW
    // ================================================================

    private fun setupRecyclerView() {

        lockerRecyclerView.layoutManager =
            GridLayoutManager(
                this,
                2
            )


        lockerAdapter =
            WebsiteAdapter(

                websites =
                    lockerWebsites,

                onWebsiteClicked = { website ->

                    openWebsite(
                        website
                    )
                },

                onWebsiteDeleted = {

                    lifecycleScope.launch {

                        saveLockerWebsites()
                    }
                }
            )


        lockerRecyclerView.adapter =
            lockerAdapter
    }


    // ================================================================
    // BUTTONS
    // ================================================================

    private fun setupButtons() {

        backButton.setOnClickListener {

            finish()
        }


        lockerAddButton.setOnClickListener {

            showAddWebsiteDialog()
        }
    }


    // ================================================================
    // CHECK LOCKER STATUS
    // ================================================================

    private fun checkLockerStatus() {

        lifecycleScope.launch {

            val hasPin =
                lockerStorage.hasPin()


            if (hasPin) {

                showPinVerification()

            } else {

                showPinSetup()
            }
        }
    }


    // ================================================================
    // PIN SETUP
    // ================================================================

    private fun showPinSetup() {

        val input =
            createPinInput()


        val dialog =
            AlertDialog.Builder(this)

                .setTitle(
                    "Create Locker PIN"
                )

                .setMessage(
                    "Create a 4-digit PIN to protect your locker."
                )

                .setView(
                    input
                )

                .setNegativeButton(
                    "Cancel"
                ) { _, _ ->

                    finish()
                }

                .setPositiveButton(
                    "Continue",
                    null
                )

                .create()


        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val pin =
                    input.text
                        .toString()


                if (
                    !isValidPin(pin)
                ) {

                    input.error =
                        "PIN must contain exactly 4 digits"

                    return@setOnClickListener
                }


                dialog.dismiss()


                showPinConfirmation(
                    pin
                )
            }
        }


        dialog.setOnCancelListener {

            finish()
        }


        dialog.show()
    }


    // ================================================================
    // PIN CONFIRMATION
    // ================================================================

    private fun showPinConfirmation(
        firstPin: String
    ) {

        val input =
            createPinInput()


        val dialog =
            AlertDialog.Builder(this)

                .setTitle(
                    "Confirm PIN"
                )

                .setMessage(
                    "Enter your PIN again."
                )

                .setView(
                    input
                )

                .setNegativeButton(
                    "Back",
                    null
                )

                .setPositiveButton(
                    "Save",
                    null
                )

                .create()


        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val confirmedPin =
                    input.text
                        .toString()


                if (
                    !isValidPin(
                        confirmedPin
                    )
                ) {

                    input.error =
                        "PIN must contain exactly 4 digits"

                    return@setOnClickListener
                }


                if (
                    confirmedPin != firstPin
                ) {

                    input.error =
                        "PINs do not match"

                    return@setOnClickListener
                }


                lifecycleScope.launch {

                    lockerStorage.savePin(
                        confirmedPin
                    )


                    dialog.dismiss()


                    loadLockerWebsites()
                }
            }
        }


        dialog.setOnCancelListener {

            finish()
        }


        dialog.show()
    }


    // ================================================================
    // PIN VERIFICATION
    // ================================================================

    private fun showPinVerification() {

        val input =
            createPinInput()


        val dialog =
            AlertDialog.Builder(this)

                .setTitle(
                    "Unlock Locker"
                )

                .setMessage(
                    "Enter your 4-digit PIN."
                )

                .setView(
                    input
                )

                .setNegativeButton(
                    "Cancel"
                ) { _, _ ->

                    finish()
                }

                .setPositiveButton(
                    "Unlock",
                    null
                )

                .create()


        dialog.setOnShowListener {

            dialog.getButton(
                AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener {

                val enteredPin =
                    input.text
                        .toString()


                if (
                    !isValidPin(
                        enteredPin
                    )
                ) {

                    input.error =
                        "Enter a 4-digit PIN"

                    return@setOnClickListener
                }


                lifecycleScope.launch {

                    val savedPin =
                        lockerStorage.getPin()


                    if (
                        enteredPin == savedPin
                    ) {

                        dialog.dismiss()


                        loadLockerWebsites()

                    } else {

                        input.error =
                            "Incorrect PIN"
                    }
                }
            }
        }


        dialog.setOnCancelListener {

            finish()
        }


        dialog.show()
    }


    // ================================================================
    // PIN VALIDATION
    // ================================================================

    private fun isValidPin(
        pin: String
    ): Boolean {

        return pin.length == 4 &&
                pin.all {
                    it.isDigit()
                }
    }


    // ================================================================
    // PIN INPUT
    // ================================================================

    private fun createPinInput():
            EditText {

        val input =
            EditText(this)


        input.inputType =
            InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_VARIATION_PASSWORD


        input.setSingleLine(
            true
        )


        input.gravity =
            Gravity.CENTER


        input.textSize =
            22f


        input.letterSpacing =
            0.25f


        input.filters =
            arrayOf(
                InputFilter.LengthFilter(
                    4
                )
            )


        input.setPadding(
            32,
            16,
            32,
            16
        )


        return input
    }


    // ================================================================
    // LOAD LOCKER WEBSITES
    // ================================================================

    private fun loadLockerWebsites() {

        lifecycleScope.launch {

            val savedWebsites =
                lockerStorage.loadWebsites()


            lockerWebsites.clear()


            lockerWebsites.addAll(
                savedWebsites
            )


            lockerAdapter.notifyDataSetChanged()
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


                addLockerWebsite(
                    enteredUrl,
                    dialog
                )
            }
        }


        dialog.show()
    }


    // ================================================================
    // ADD LOCKER WEBSITE
    // ================================================================

    private fun addLockerWebsite(
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


        lockerWebsites.add(
            website
        )


        lockerAdapter.notifyItemInserted(
            lockerWebsites.lastIndex
        )


        lifecycleScope.launch {

            saveLockerWebsites()
        }


        dialog.dismiss()


        showMessage(
            "$websiteName added to locker"
        )
    }


    // ================================================================
    // SAVE LOCKER WEBSITES
    // ================================================================

    private suspend fun saveLockerWebsites() {

        lockerStorage.saveWebsites(
            lockerWebsites
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