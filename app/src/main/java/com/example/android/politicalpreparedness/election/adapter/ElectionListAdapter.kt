package com.example.android.politicalpreparedness.election.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ElectionDetailBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val electionList:List<Election>): ListAdapter<Election,ElectionListAdapter.ElectionViewHolder>(
    diffUtilCallback) {

    class ElectionViewHolder(val binding: ElectionDetailBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindView(election:Election){
            binding.apply{
                electionDetailTitle.text = election.name
                electionDetailDate.text = election.electionDay.toString()
            }
            binding.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putParcelable("election",election)
                Navigation.findNavController(it).navigate(R.id.action_electionsFragment_to_electionInformation,bundle)
            }
        }
    }


    override fun getItemCount(): Int {
        return electionList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
       val binding =  ElectionDetailBinding.inflate(LayoutInflater.from(parent.context))
        return ElectionViewHolder(binding)
    }

    // Bind ViewHolder
    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }


    //TODO: Add companion object to inflate ViewHolder (from)
}

//TODO: Create ElectionViewHolder

//TODO: Create ElectionDiffCallback

private val diffUtilCallback = object:DiffUtil.ItemCallback<Election>(){
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
       return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }

}

//TODO: Create ElectionListener