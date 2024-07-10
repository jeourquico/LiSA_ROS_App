package com.example.lisaapp

import android.annotation.SuppressLint
import android.content.ContentValues
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
import com.example.lisaapp.sub.SSHConnect
import com.example.lisaapp.sub.ShowToastPopup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


class SelectDestination1(private val goToString: String) : DialogFragment(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selectdestination_1, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val destinationText: TextView = view.findViewById(R.id.toDestination)
        val btnYes = view.findViewById<Button>(R.id.btn_yes)
        val btnNo = view.findViewById<Button>(R.id.btn_no)

        // Initialize TextToSpeech
        tts = TextToSpeech(requireContext(), this)

        // Text to display on floating window
        destinationText.text = "Go to $goToString?"


        // send goal commands to LiSA ROS
        btnYes.setOnClickListener {
            connectSSHinBG("rosrun lisa $goToString.py")
//            when (goToString) {
//                "goal" -> connectSSHinBG("rosrun lisa goal.py")
//                "goal1" -> connectSSHinBG("rosrun lisa goal1.py")
//                "goal2" -> connectSSHinBG("rosrun lisa goal2.py")
//                "goal3" -> connectSSHinBG("rosrun lisa goal3.py")
//                "pose" -> connectSSHinBG("rosrun lisa pose.py")
//            }
        }

        // Dismiss floating window
        btnNo.setOnClickListener {
            dismiss()
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

    //SSH Connection
    @OptIn(DelicateCoroutinesApi::class)
    fun connectSSHinBG(command: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {

                SSHConnect().connectSSH(command) // Call the suspend function here
            }
            // Handle the result here
            Log.d(ContentValues.TAG, "SSH output: $result")
            //show messages or connection status
            // change to (result == "true") after debug

            ShowToastPopup(requireContext(),layoutInflater).showToast(result)

            // change to (result == "true") after debug
            if (result == "true") {
                dismiss()
                val showPopup = PopupFragment2(goToString)
                showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")
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

                // Change voice characteristics here
                val pitch = 0.8f // Change pitch level
                val speed = 0.7f // Change speech speed

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
        speakOut("Go to $goToString? Is this correct. Click 'yes' if correct and 'no' if you want to go somewhere else.")
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}


