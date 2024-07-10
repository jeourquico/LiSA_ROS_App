package com.example.lisaapp

import android.app.Activity
import android.content.ContentValues
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lisaapp.sub.SSHConnect
import com.example.lisaapp.sub.ShowToastPopup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionCredentialsSSH : Fragment() {

    var ipAddressSSH = "192.168.1.100"
    var usernameSSH = "lisa"
    var passwordSSH = "lisa1234"

    private lateinit var ipAddressNew: EditText
    private lateinit var usernameNew: EditText
    private lateinit var passwordNew: EditText
    private lateinit var btnConnectListener: Button
    private lateinit var ivTogglePasswordVisibility: ImageView

    private var isPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection_credentials_ssh, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ipAddressNew = view.findViewById(R.id.IpaddressEdit)
        usernameNew = view.findViewById(R.id.UsernameEdit)
        passwordNew = view.findViewById(R.id.PasswordEdit)
        btnConnectListener = view.findViewById(R.id.btnConnectManually)
        ivTogglePasswordVisibility = view.findViewById(R.id.ivTogglePasswordVisibility)

        // Set hint texts to EditText fields
        ipAddressNew.setHint("Enter IP Address")
        usernameNew.setHint("Enter Username")
        passwordNew.setHint("Enter Password")

        // Set initial values to EditText fields
        ipAddressNew.setText(ipAddressSSH)
        usernameNew.setText(usernameSSH)
        passwordNew.setText(passwordSSH)

        btnConnectListener.setOnClickListener {
            // Get values from EditTexts
            val newIpAddress = ipAddressNew.text.toString()
            val newUsername = usernameNew.text.toString()
            val newPassword = passwordNew.text.toString()

            // Update the SSH credentials
            ipAddressSSH = newIpAddress
            usernameSSH = newUsername
            passwordSSH = newPassword

            //Connect to SSH
            connectSSHinBG("ls -l")

        }

        // Toggle password visible or hidden
        ivTogglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    // password visibility toggle logic
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide the password
            passwordNew.transformationMethod = PasswordTransformationMethod.getInstance()
            ivTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
        } else {
            // Show the password
            passwordNew.transformationMethod = HideReturnsTransformationMethod.getInstance()
            ivTogglePasswordVisibility.setImageResource(R.drawable.ic_visibility)
        }
        // Move the cursor to the end of the text
        passwordNew.setSelection(passwordNew.text.length)
        isPasswordVisible = !isPasswordVisible
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
            if (result == "true") {
                // Proceed to next fragment and Initialize ROS
                ShowToastPopup(
                    (context as Activity),
                    layoutInflater
                ).showToast("Connection Success")
                // Move to next fragment. Move this to true case once done debug or during testing
                findNavController().navigate(R.id.action_connectionCredentialsSSH_to_ThirdFragment)
            } else {
                ShowToastPopup((context as Activity), layoutInflater).showToast(result)
            }
        }
    }

}

