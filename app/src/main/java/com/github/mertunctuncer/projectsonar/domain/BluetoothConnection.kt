package com.github.mertunctuncer.projectsonar.domain

import kotlinx.coroutines.Job
interface BluetoothConnection : AutoCloseable {
    fun sendMessage(message: String): Job
}