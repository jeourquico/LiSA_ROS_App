package com.example.lisaapp.sub

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.lisaapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionCredentialsSSH : Fragment() {

    private lateinit var ipAddressNew: EditText
    private lateinit var usernameNew: EditText
    private lateinit var passwordNew: EditText
    private lateinit var btnConnectListener: Button
    private lateinit var ivTogglePasswordVisibility: ImageView

    private var isPasswordVisible = false

    private val sshViewModel: SSHViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection_credentials_ssh, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the lateinit properties
        ipAddressNew = view.findViewById(R.id.IpaddressEdit)
        usernameNew = view.findViewById(R.id.UsernameEdit)
        passwordNew = view.findViewById(R.id.PasswordEdit)
        btnConnectListener = view.findViewById(R.id.btnConnectManually)
        ivTogglePasswordVisibility = view.findViewById(R.id.ivTogglePasswordVisibility)

        // Set hint texts to EditText fields
        ipAddressNew.hint = "Enter IP Address"
        usernameNew.hint = "Enter Username"
        passwordNew.hint = "Enter Password"

        // Set initial values to EditText fields
        ipAddressNew.setText(sshViewModel.ipAddressInit)
        usernameNew.setText(sshViewModel.usernameInit)
        passwordNew.setText(sshViewModel.passwordInit)

        btnConnectListener.setOnClickListener {
            // Get values from EditTexts
            val newIpAddress = ipAddressNew.text.toString()
            val newUsername = usernameNew.text.toString()
            val newPassword = passwordNew.text.toString()

            // Update the ViewModel
            sshViewModel.updateCredentials(newIpAddress, newUsername, newPassword)

            // Connect to SSH
            connectSSHinBG("ls -l")
        }

        // Toggle password visible or hidden
        ivTogglePasswordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }
    }

    // Password visibility toggle logic
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

    // SSH Connection
    private fun connectSSHinBG(command: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                SSHConnect(sshViewModel).connectSSH(command, "exec")
            }
            // Handle the result on the main thread
            if (isAdded) { // Ensure the fragment is still attached
                handleSSHResult(result)
            }
        }
    }

    private fun handleSSHResult(result: String) {
        // Handle the result of the SSH command, possibly updating LiveData or UI elements
        if (result != "true") {
            // Proceed to next fragment and Initialize ROS
            ShowToastPopup(requireActivity(), layoutInflater).showToast("Connection Success")
            // Move to next fragment. Move this to true case once done debug or during testing
            findNavController().navigate(R.id.action_connectionCredentialsSSH_to_ThirdFragment)
        } else {
            ShowToastPopup(requireActivity(), layoutInflater).showToast(result)
        }
    }
}

