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


class PopupFragment3 : DialogFragment() {

    private lateinit var btnYes : Button
    private lateinit var  btnNo : Button
    private lateinit var  dialogText : TextView
    private val sshViewModel: SSHViewModel by activityViewModels()

    val dialog = "Do you want to go somewhere else? "
    val homePositionText = "Moving back to Home Position"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? MainActivity)?.dialogText?.value = dialog
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popup3, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnYes = view.findViewById(R.id.btn_yes)
        btnNo = view.findViewById(R.id.btn_no)
        dialogText = view.findViewById((R.id.toDestination))

        dialogText.text = dialog

        // Dismiss pop up and goes back to selection
        btnYes.setOnClickListener {
            dismiss()
        }

        // Returns to home
        btnNo.setOnClickListener {
            connectSSHinBG("rosrun lisa pose.py")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // StopTextToSpeech when the fragment is destroyed
        (activity as? MainActivity)?.ttsStop()
    }

    //SSH Connections logic
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
                val showPopup = PopupFragment2(homePositionText, null.toString())
                showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")
            } else {
                ShowToastPopup(requireActivity(), layoutInflater).showToast(result)
            }
        }
    }

}







