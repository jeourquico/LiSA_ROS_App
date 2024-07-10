package com.example.lisaapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lisaapp.databinding.FragmentThirdBinding
import com.example.lisaapp.sub.SSHConnect
import com.example.lisaapp.sub.ShowToastPopup
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TOP CLICKBAITS
        // rosrun 1st nav point
        binding.btnSelectDestinationNum1.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("goal")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

        // rosrun 2nd nav point
        binding.btnSelectDestinationNum2.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("goal1")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

        // rosrun 3rd nav point
        binding.btnSelectDestinationNum3.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("goal2")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

        // rosrun 4th nav point
        binding.btnSelectDestinationNum4.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("goal3")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

        // rosrun pose nav point
        binding.btnSelectDestinationNum5.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("pose")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

        // BOTTOM CLICKBAITS
        // rosrun roam selection
        binding.btnSelectDestination.setOnClickListener {
            // opens fragment to roam selector
            //findNavController().navigate(R.id.action_ThirdFragment_to_roamSelector)
            // temporary function. uses roam.py command
            connectSSHinBG("rosrun lisa roam.py")
        }

        binding.btnStartStop.setOnClickListener {
            // sends stop code to ROS. This stops the ROS after completing its current goal point
            connectSSHinBG("rosrun lisa stop.py")
        }
        binding.btnHomingROS.setOnClickListener {
            activity?.finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            ShowToastPopup(requireContext(),layoutInflater).showToast(result)
        }
    }
}

