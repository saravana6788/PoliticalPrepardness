package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter

class ElectionsFragment: Fragment() {

    //Declare ViewModel
    private lateinit var electionsViewModel: ElectionsViewModel
    private lateinit var binding:FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {


        // Add ViewModel values and create ViewModel


        // Add binding values
        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val viewModelFactory = ElectionsViewModelFactory(ElectionDatabase.getInstance(requireContext()))
        electionsViewModel = ViewModelProvider(this,viewModelFactory)[ElectionsViewModel::class.java]
        binding.vModel = electionsViewModel


        //TODO: Link elections to voter info

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        electionsViewModel.upcomingElections.observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()) {
                val adapter = ElectionListAdapter(it)
                adapter.submitList(it)
                binding.upcomingElectionsList.adapter = adapter
            }

        }


        electionsViewModel.savedElections.observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()) {
                it.let {
                    val adapter = ElectionListAdapter(it)
                    adapter.submitList(it)
                    binding.savedElectionsList.adapter = adapter
                }
            }
        }

        electionsViewModel.error.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(),it, Toast.LENGTH_LONG).show()
        }



    }

    override fun onResume() {
        super.onResume()
        electionsViewModel.getUpcomingElections()
        electionsViewModel.getSavedElections()
    }


}