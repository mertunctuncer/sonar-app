package com.github.mertunctuncer.projectsonar.utilities

import android.content.Context
import android.content.pm.PackageManager

interface ContextOwner {
    val context: Context

    fun hasPermission(permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}