package com.example.lisaapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lisaapp.databinding.FragmentThirdBinding
import com.example.lisaapp.sub.ShowToastPopup
import java.util.Locale


class ThirdFragment : Fragment() {

    private var _binding: FragmentThirdBinding? = null
    private val fromBottom : Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim) }
    private val toBottom : Animation by lazy { AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim) }

    private var clicked = false
    private var langSwitch = false
    private val langSelected = FirstFragment.DataHolder.langSelected

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (langSelected == "English") {
            langSwitch = false
        } else {
            langSwitch = true
        }
        _binding = FragmentThirdBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Floating App Bar function
        binding.lisaFab.setOnClickListener {
            onLisaFabButtonClicked()
        }

        binding.firstActionFab.setOnClickListener {
            langSwitch = !langSwitch
            if (!langSwitch) {
                (activity as? MainActivity)?.setTtsLanguage(Locale.US)
                ShowToastPopup(requireContext(),
                    layoutInflater
                ).showToast("Language changed to ENGLISH")
                (activity as? MainActivity)?.dialogText?.value = "Language changed to ENGLISH"
            } else {
                (activity as? MainActivity)?.setTtsLanguage(Locale("tl", "PH"))
                ShowToastPopup(
                    requireContext(),
                    layoutInflater
                ).showToast("Language changed to TAGALOG")
                (activity as? MainActivity)?.dialogText?.value = "Language changed to TAGALOG"
            }
        }

        binding.secondActionFab.setOnClickListener {
            // Move to roam selector fragment
            findNavController().navigate(R.id.action_ThirdFragment_to_roamSelector)
        }

        binding.thirdActionFab.setOnClickListener {
            ShowToastPopup(requireContext(),layoutInflater).showToast("Extra button")
        }

        binding.callFab.setOnClickListener {
            openContactsApp()
        }

        // TOP CLICKBAITS
        // rosrun 1st nav point
        binding.btnSelectDestinationNum1.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("goal", "Location 1")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

        // rosrun 2nd nav point
        binding.btnSelectDestinationNum2.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("goal1", "Location 2")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

        // rosrun 3rd nav point
        binding.btnSelectDestinationNum3.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("goal2", "Location 3")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

        // rosrun 4th nav point
        binding.btnSelectDestinationNum4.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("goal3", "Location 4")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

        // rosrun pose nav point
        binding.btnSelectDestinationNum5.setOnClickListener {
            // pop up "going to ..."
            val showPopup = SelectDestination1("pose", "Home Position")
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onLisaFabButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if(!clicked) {
            binding.firstActionFab.visibility = View.VISIBLE
            binding.secondActionFab.visibility = View.VISIBLE
            binding.thirdActionFab.visibility = View.VISIBLE
        } else {
            binding.firstActionFab.visibility = View.INVISIBLE
            binding.secondActionFab.visibility = View.INVISIBLE
            binding.thirdActionFab.visibility = View.INVISIBLE
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if(!clicked) {
            binding.firstActionFab.startAnimation(fromBottom)
            binding.secondActionFab.startAnimation(fromBottom)
            binding.thirdActionFab.startAnimation(fromBottom)
        } else {
            binding.firstActionFab.startAnimation(toBottom)
            binding.secondActionFab.startAnimation(toBottom)
            binding.thirdActionFab.startAnimation(toBottom)
        }
    }

    private fun setClickable(clicked: Boolean) {
        if(!clicked) {
            binding.firstActionFab.isClickable = true
            binding.secondActionFab.isClickable = true
            binding.thirdActionFab.isClickable = true
        } else {
            binding.firstActionFab.isClickable = false
            binding.secondActionFab.isClickable = false
            binding.thirdActionFab.isClickable = false
        }
    }

    private fun openContactsApp() {
        val intent = Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}



