package com.example.lisaapp

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.lisaapp.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var tts: TextToSpeech
    private var isTtsInitialized = false
    val ttsInitializationStatus = MutableLiveData<Boolean>()
    val dialogText = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize TextToSpeech
        tts = TextToSpeech(this, this)

        // Observe dialog text
        dialogText.observe(this) { text ->
            if (isTtsInitialized) {
                speakOut(text)
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun setTtsLanguage(locale: Locale) {
        if (isTtsInitialized) {
            val result = tts.setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The selected language is not supported.")
            } else {
                Log.i("TTS", "TTS Language changed to ${locale.displayName}")
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                Log.i("TTS", "TextToSpeech Initialized")
                isTtsInitialized = true
                tts.setPitch(1.2f)
                tts.setSpeechRate(0.8f)
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
        ttsInitializationStatus.value = isTtsInitialized
    }

    fun speakOut(text: String) {
        if (isTtsInitialized) {
            Log.i("TTS", "Speaking out: $text")
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        } else {
            Log.e("TTS", "TextToSpeech not initialized")
        }
    }

    fun ttsStop() {
        tts.stop()
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}