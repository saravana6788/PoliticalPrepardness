package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionInformationBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionInformationFragment : Fragment() {


    private lateinit var viewModel: ElectionInformationViewModel

    private lateinit var binding:FragmentElectionInformationBinding
    private lateinit var election:Election

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =  FragmentElectionInformationBinding.inflate(inflater)
        val viewModelFactory = ElectionInformationViewModelFactory(ElectionDatabase.getInstance(requireContext()))
        viewModel = ViewModelProvider(this, viewModelFactory)[ElectionInformationViewModel::class.java]
        election = ElectionInformationFragmentArgs.fromBundle(arguments!!).election
        binding.election = election
        viewModel.getElectionById(election)
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel

        with(binding){
            electionInformationLayout.setOnClickListener {
                openVotingLocations()
            }

            electionBallotInformation.setOnClickListener {
                openBallotLocations()
            }

            saveElection.setOnClickListener {
                this@ElectionInformationFragment.viewModel .followOrUnFollowElection(this@ElectionInformationFragment.election)

            }
        }




        viewModel.isElectionSaved.observe(viewLifecycleOwner){
            if(it) {
                binding.saveElection.text = requireContext().getString(R.string.unfollow_election_button_text)
            }else
                binding.saveElection.text = requireContext().getString(R.string.follow_election_button_text)
        }

        viewModel.error.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(),it,Toast.LENGTH_LONG).show()
        }

    }

    private fun openVotingLocations(){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://myvote.wi.gov"))
        startActivity(intent)
    }

    private fun openBallotLocations(){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://myvote.wi.gov"))
        startActivity(intent)
    }



}