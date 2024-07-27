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
import androidx.fragment.app.activityViewModels
import com.example.lisaapp.sub.SSHConnect
import com.example.lisaapp.sub.SSHViewModel
import java.util.Locale


class PopupFragment2 (private val goToString: String, private val locationText: String) : DialogFragment() {

    private lateinit var destinationText : TextView
    private lateinit var btnDone : Button
    private val sshViewModel: SSHViewModel by activityViewModels()

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

        if (goToString == PopupFragment3().homePositionText) {
            destinationText.text = goToString
        } else {
            destinationText.text = "Moving to $locationText"
        }

        (activity as? MainActivity)?.dialogText?.value = destinationText.text.toString()

        btnDone.setOnClickListener {
            dismiss()
            if (goToString != PopupFragment3().homePositionText) {
                val showPopup = PopupFragment3()
                showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // StopTextToSpeech when the fragment is destroyed
        (activity as? MainActivity)?.ttsStop()
        //Close SSH session
        SSHConnect(sshViewModel).disconnectSession()
    }

}