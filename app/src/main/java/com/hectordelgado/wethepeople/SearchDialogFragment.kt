package com.hectordelgado.wethepeople

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.lang.ClassCastException

/**
 *  We The People
 *
 *  @author Hector Delgado
 *
 *  Created on June 03, 2020.
 *  Copyright © 2020 Hector Delgado. All rights reserved.
 */
class SearchDialogFragment : DialogFragment() {
    internal lateinit var listener: SearchDialogListener

    interface SearchDialogListener {
        fun onSearchClick(dialog: DialogFragment, keyword: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = context as SearchDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("${context.toString()} must implement SearchDialogListener")
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val mainView = inflater.inflate(R.layout.dialog_search, null)
            val searchEditText: EditText = mainView.findViewById(R.id.searchEditText)

            // Inflate and set the layout for the dialog
            builder.setView(mainView)
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    listener.onSearchClick(this, searchEditText.text.toString())
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}