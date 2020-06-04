package com.hectordelgado.wethepeople

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    var liveData = MutableLiveData<List<PetitionModel>>()
    lateinit var data: List<PetitionModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CurrentPetition.getPetitions()?.let { petition ->
            petitionTitle.text = petition.title
            petitionBody.text = petition.body
            val count = "${petition.signatureCount} / ${petition.signaturesNeeded}"
            petitionCount.text = count
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
