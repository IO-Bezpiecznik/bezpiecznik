package com.example.bezpiecznik.ui.start

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import com.example.bezpiecznik.R


class StartFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_start, container, false)

        view.findViewById<Button>(R.id.start_btn).setOnClickListener {

            it.findNavController().navigate(R.id.action_startFragment_to_nav_home)

        }

        return view
    }
}