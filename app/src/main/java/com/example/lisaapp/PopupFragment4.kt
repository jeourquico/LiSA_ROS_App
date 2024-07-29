package com.example.lisaapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
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
import com.example.lisaapp.sub.ShowToastPopup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PopupFragment4 : DialogFragment() {
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


        (activity as? MainActivity)?.dialogText?.value = destinationText.text.toString()

        btnDone.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // StopTextToSpeech when the fragment is destroyed
        (activity as? MainActivity)?.ttsStop()
        //Close SSH session
        SSHConnect(sshViewModel).disconnectSession()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun connectSSHinBG(command: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                SSHConnect(sshViewModel).connectSSH(command, "shell") // Call the suspend function here
            }
            // Handle the result here
            if (isAdded) {
                handleSSHResult(result)
            }
        }
    }

    private fun handleSSHResult(result: String) {
        // Ensure the fragment is still attached before accessing context
        if (isAdded) {
            // Handle the result of the SSH command, possibly updating LiveData or UI elements
            if (result != "true") {
                // Proceed to next fragment and Initialize ROS
                Log.d(ContentValues.TAG, "SSH output: $result")
                // Show a toast message
                ShowToastPopup(requireContext(),layoutInflater).showToast(result)
                // Move to next fragment. Move this to true case once done debug or during testing
                dismiss()
                val showPopup = PopupFragment2(null.toString(), null.toString())
                showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")
            } else {
                ShowToastPopup(requireActivity(), layoutInflater).showToast(result)
            }
        }
    }

}