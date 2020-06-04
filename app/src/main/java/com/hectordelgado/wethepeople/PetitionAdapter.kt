package com.hectordelgado.wethepeople

import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

/**
 *  We The People
 *
 *  @author Hector Delgado
 *
 *  Created on May 28, 2020.
 *  Copyright © 2020 Hector Delgado. All rights reserved.
 */
class PetitionAdapter(
    private val data: MutableList<PetitionModel>,
    private val listener: PetitionViewHolder.PetitionListener)
    : RecyclerView.Adapter<PetitionAdapter.PetitionViewHolder>() {

    // Provide a reference to each of the views for each data item
    class PetitionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Properties
        val title: TextView = itemView.findViewById(R.id.titleTextView)
        val body: TextView = itemView.findViewById(R.id.bodyTextView)
        val signatures: TextView = itemView.findViewById(R.id.signaturesTextView)
        val parentLayout: ConstraintLayout = itemView.findViewById(R.id.parentLayout)

        // onClick Event Listener
        interface PetitionListener {
            fun onItemClicked(petition: PetitionModel)
        }
    }

    // Create new views (invoked by layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetitionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.petition_row, parent, false)
        return PetitionViewHolder(itemView)
    }

    // Returns the size of the data set
    override fun getItemCount() = data.size

    // Replace the content of the view (invoked by layout manager)
    override fun onBindViewHolder(holder: PetitionViewHolder, position: Int) {
        val signaturesText = "${data[position].signatureCount} / ${data[position].signaturesNeeded}"

        holder.title.text = data[position].title
        holder.body.text = data[position].body
        holder.signatures.text = signaturesText
        holder.parentLayout.setOnClickListener { listener.onItemClicked(data[position]) }
    }

    // Updates the list of
    fun updateData(data: List<PetitionModel>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }
}