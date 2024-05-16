package com.github.mertunctuncer.projectsonar.domain

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

interface BluetoothConnection : AutoCloseable {
    val receivedMessages: Flow<String>
    fun sendMessage(message: String): Job
}