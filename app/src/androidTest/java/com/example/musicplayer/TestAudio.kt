package com.example.musicplayer

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Writes a silent 16-bit mono PCM WAV that ExoPlayer can play. */
fun writeSilentWav(file: File, durationMs: Int): File {
    val sampleRate = 8_000
    val dataSize = sampleRate * durationMs / 1000 * 2
    val buffer = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN).apply {
        put("RIFF".toByteArray()); putInt(36 + dataSize); put("WAVE".toByteArray())
        put("fmt ".toByteArray()); putInt(16); putShort(1); putShort(1)
        putInt(sampleRate); putInt(sampleRate * 2); putShort(2); putShort(16)
        put("data".toByteArray()); putInt(dataSize)
    }
    file.parentFile?.mkdirs()
    file.writeBytes(buffer.array())
    return file
}

/** Polls [condition] until it holds, failing after [timeoutMs]. */
fun waitUntil(message: String, timeoutMs: Long = 10_000, condition: () -> Boolean) {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (!condition()) {
        if (System.currentTimeMillis() > deadline) throw AssertionError("Timed out waiting for: $message")
        Thread.sleep(50)
    }
}
