package com.example.lisaapp

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.lisaapp.databinding.FragmentFirstBinding
import com.example.lisaapp.sub.SSHConnect
import com.example.lisaapp.sub.SSHViewModel
import com.example.lisaapp.sub.ShowToastPopup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirstFragment : Fragment() {

    private val sshViewModel: SSHViewModel by activityViewModels()
    private var _binding: FragmentFirstBinding? = null
    private val dialog = "Hello! I'm happy to help you today"
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize SSH Credentials
        sshViewModel

        // Inflate the layout for this fragment
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe TTS initialization status
        (activity as? MainActivity)?.ttsInitializationStatus?.observe(viewLifecycleOwner, Observer { isInitialized ->
            if (isInitialized) {
                // TTS is initialized
                (activity as? MainActivity)?.dialogText?.value = dialog
            }
        })

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

    // SSH Connection
    @OptIn(DelicateCoroutinesApi::class)
    fun connectSSHinBG(command: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                SSHConnect(sshViewModel).connectSSH(command, "exec") // Call the suspend function here
            }
            // Handle the result here
            Log.d(ContentValues.TAG, "SSH output: $result")

            // Change to (result == "true") after debug
            if (result == "true") {
                // Show messages or connection status
                ShowToastPopup(requireContext(), layoutInflater).showToast("Connection Success")
                // Move to next fragment
                findNavController().navigate(R.id.action_FirstFragment_to_ThirdFragment)
            } else {
                ShowToastPopup(requireContext(), layoutInflater).showToast(result)
                binding.txtBtnManualConnect.visibility = View.VISIBLE
            }
        }
    }
}
