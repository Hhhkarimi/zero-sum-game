package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class AudioHapticsEngine(private val context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 44100
    private var soundEnabled = true
    private var hapticEnabled = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun setSoundEnabled(enabled: Boolean) {
        soundEnabled = enabled
    }

    fun setHapticEnabled(enabled: Boolean) {
        hapticEnabled = enabled
    }

    fun playMove() {
        if (hapticEnabled) triggerHaptic(15, 60)
        if (!soundEnabled) return
        coroutineScope.launch {
            generateTone(freq = 420.0, durationMs = 45, volume = 0.25f, decay = true)
        }
    }

    fun playFusion() {
        if (hapticEnabled) triggerHaptic(30, 120)
        if (!soundEnabled) return
        coroutineScope.launch {
            // Ascending harmonic chime
            generateChord(freqs = doubleArrayOf(523.25, 659.25, 783.99), durationMs = 120, volume = 0.4f)
        }
    }

    fun playReduction() {
        if (hapticEnabled) triggerHaptic(25, 100)
        if (!soundEnabled) return
        coroutineScope.launch {
            // Spark laser chirp
            generateChirp(startFreq = 880.0, endFreq = 440.0, durationMs = 90, volume = 0.35f)
        }
    }

    fun playAnnihilation() {
        if (hapticEnabled) {
            triggerHeavyHaptic()
        }
        if (!soundEnabled) return
        coroutineScope.launch {
            // Epic Quantum shockwave: deep sub-bass drop followed by shimmer
            generateSubBassDrop(startFreq = 160.0, endFreq = 40.0, durationMs = 320, volume = 0.7f)
            generateChord(freqs = doubleArrayOf(1046.50, 1318.51, 1567.98), durationMs = 250, volume = 0.45f)
        }
    }

    fun playCombo(comboCount: Int) {
        if (hapticEnabled) triggerHaptic(35, 160)
        if (!soundEnabled) return
        coroutineScope.launch {
            val baseFreq = 440.0 + (comboCount * 110.0)
            generateTone(freq = baseFreq, durationMs = 140, volume = 0.5f, decay = true)
        }
    }

    fun playVictory() {
        if (hapticEnabled) triggerCelebrationHaptic()
        if (!soundEnabled) return
        coroutineScope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            for (note in notes) {
                generateTone(freq = note, durationMs = 110, volume = 0.5f, decay = true)
                kotlinx.coroutines.delay(80)
            }
        }
    }

    fun playGameOver() {
        if (hapticEnabled) triggerHaptic(80, 180)
        if (!soundEnabled) return
        coroutineScope.launch {
            generateChirp(startFreq = 320.0, endFreq = 110.0, durationMs = 260, volume = 0.5f)
        }
    }

    private fun triggerHaptic(durationMs: Long, amplitude: Int) {
        vibrator?.let {
            if (it.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255)))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(durationMs)
                }
            }
        }
    }

    private fun triggerHeavyHaptic() {
        vibrator?.let {
            if (it.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 30, 20, 60)
                    val amplitudes = intArrayOf(0, 180, 0, 255)
                    it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(90)
                }
            }
        }
    }

    private fun triggerCelebrationHaptic() {
        vibrator?.let {
            if (it.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 40, 30, 40, 30, 80)
                    val amplitudes = intArrayOf(0, 150, 0, 200, 0, 255)
                    it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(200)
                }
            }
        }
    }

    private fun generateTone(freq: Double, durationMs: Int, volume: Float, decay: Boolean) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            var env = 1.0
            if (decay) {
                env = 1.0 - (i.toDouble() / numSamples)
            }
            val sample = sin(2.0 * Math.PI * freq * t) * env * volume * Short.MAX_VALUE
            buffer[i] = sample.toInt().toShort()
        }
        playPcm(buffer)
    }

    private fun generateChord(freqs: DoubleArray, durationMs: Int, volume: Float) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val env = 1.0 - (i.toDouble() / numSamples)
            var sum = 0.0
            for (f in freqs) {
                sum += sin(2.0 * Math.PI * f * t)
            }
            sum = (sum / freqs.size) * env * volume * Short.MAX_VALUE
            buffer[i] = sum.toInt().toShort()
        }
        playPcm(buffer)
    }

    private fun generateChirp(startFreq: Double, endFreq: Double, durationMs: Int, volume: Float) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val p = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * p
            val t = i.toDouble() / sampleRate
            val env = 1.0 - p
            val sample = sin(2.0 * Math.PI * currentFreq * t) * env * volume * Short.MAX_VALUE
            buffer[i] = sample.toInt().toShort()
        }
        playPcm(buffer)
    }

    private fun generateSubBassDrop(startFreq: Double, endFreq: Double, durationMs: Int, volume: Float) {
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val p = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * (p * p)
            val t = i.toDouble() / sampleRate
            val env = if (p < 0.1) p * 10 else 1.0 - (p - 0.1) / 0.9
            val sample = sin(2.0 * Math.PI * currentFreq * t) * env * volume * Short.MAX_VALUE
            buffer[i] = sample.toInt().toShort()
        }
        playPcm(buffer)
    }

    private fun playPcm(buffer: ShortArray) {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val trackSize = buffer.size.coerceAtLeast(minBufferSize)
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(trackSize * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            coroutineScope.launch {
                kotlinx.coroutines.delay((buffer.size * 1000L / sampleRate) + 50)
                audioTrack.release()
            }
        } catch (_: Exception) {
            // Silently ignore if audio subsystem is busy
        }
    }
}
