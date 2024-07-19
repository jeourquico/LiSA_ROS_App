package com.example.lisaapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.util.Locale


class PopupFragment2 (private val goToString: String) : DialogFragment(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var destinationText : TextView
    private lateinit var btnDone : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popup2, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        destinationText = view.findViewById(R.id.toDestination)
        btnDone = view.findViewById(R.id.btn_done)

        tts = TextToSpeech(requireContext(), this)

        if (goToString == PopupFragment3().homePositionText) {
            destinationText.text = goToString
        } else {
            destinationText.text = "Moving to $goToString"
        }

        btnDone.setOnClickListener {
            dismiss()
            if (goToString != PopupFragment3().homePositionText) {
                val showPopup = PopupFragment3()
                showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")
            }
        }
    }

    override fun onDestroy() {
        // Shutdown TextToSpeech when the fragment is destroyed
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language specified is not supported!")
            } else {
                Log.i("TTS", "TextToSpeech Initialized")

                // Change voice characteristics here
                val pitch = 1.2f // Change pitch level
                val speed = 0.8f // Change speech speed

                val voice = Voice(null, Locale.US, Voice.QUALITY_HIGH, Voice.LATENCY_NORMAL, false, null)
                tts.voice = voice

                tts.setPitch(pitch)
                tts.setSpeechRate(speed)

                // Now you can use the TTS engine
            }
        } else {
            Log.e("TTS", "Initialization Failed!")
        }
        // Speak the destination text
        speakOut(destinationText.text.toString())
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}