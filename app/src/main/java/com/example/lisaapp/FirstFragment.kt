package com.example.lisaapp

import android.content.ContentValues
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lisaapp.databinding.FragmentFirstBinding
import com.example.lisaapp.sub.SSHConnectExec
import com.example.lisaapp.sub.ShowToastPopup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class FirstFragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech

    private var _binding: FragmentFirstBinding? = null
    private val dialogText = "Hello! I'm happy to help you today"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)

        // Connect to ROS
        binding.btnConnectSSH.setOnClickListener {
            connectSSHinBG("ls -l")
        }

        // Move to Fragment: Manual Connect
        binding.txtBtnManualConnect.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_connectionCredentialsSSH)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        speakOut(dialogText)
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    //SSH Connection
    @OptIn(DelicateCoroutinesApi::class)
    fun connectSSHinBG(command: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {

                SSHConnectExec().connectSSH(command) // Call the suspend function here
            }
            // Handle the result here
            Log.d(ContentValues.TAG, "SSH output: $result")

            // change to (result == "true") after debug
            if (result == "true") {
                //show messages or connection status
                ShowToastPopup(requireContext(),layoutInflater).showToast("Connection Success")
                // Move to next fragment. Move this to true case once done debug or during testing
                findNavController().navigate(R.id.action_FirstFragment_to_ThirdFragment)
            } else {
                ShowToastPopup(requireContext(),layoutInflater).showToast(result)
                binding.txtBtnManualConnect.visibility = View.VISIBLE
            }
        }
    }

}
