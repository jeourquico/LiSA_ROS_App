package com.example.lisaapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lisaapp.sub.MyAdapter
import com.example.lisaapp.sub.MyItem
import com.example.lisaapp.sub.SSHViewModel


class RoamSelector : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var btnRoam : Button

    private val dialog = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_roam_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.toDestinationRecyclerView)
        btnRoam = view.findViewById(R.id.btn_goRoam)

        val items = mutableListOf(
            MyItem("Location 1", true),
            MyItem("Location 2", true),
            MyItem("Location 3", true),
            MyItem("Location 4", true)
        )

        adapter = MyAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                adapter.swapItems(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Not needed
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        btnRoam.setOnClickListener {
            goRoam()
            findNavController().navigate(R.id.action_roamSelector_to_ThirdFragment)
            val showPopup = PopupFragment4()
            showPopup.show((activity as AppCompatActivity).supportFragmentManager, "showPopup")
        }
    }

    private fun getCurrentListHierarchy(): List<MyItem> {
        return adapter.getItems()
    }

    fun goRoam() {
        val listToRoam = getCurrentListHierarchy()
        val listToShow = mutableListOf<String>()
        for (places in listToRoam) {
            if (places.isChecked) {
                listToShow += places.text
                Log.e("List", places.text)
                Log.e("List", listToShow.toString())
            }
        }
    }

}
