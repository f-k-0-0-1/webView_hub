package com.fk.allinone

import android.graphics.Color
import android.view.View
import android.view.Window

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat


object SystemBarUtils {

    fun setup(
        window: Window,
        rootView: View
    ) {

        // ============================================================
        // NORMAL SYSTEM BAR MODE
        // ============================================================

        WindowCompat.setDecorFitsSystemWindows(
            window,
            true
        )


        // ============================================================
        // WHITE STATUS BAR
        // ============================================================

        window.statusBarColor =
            Color.WHITE


        // ============================================================
        // WHITE NAVIGATION BAR
        // ============================================================

        window.navigationBarColor =
            Color.WHITE


        // ============================================================
        // DARK SYSTEM BAR ICONS
        // ============================================================

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
}