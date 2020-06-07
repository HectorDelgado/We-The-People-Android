package com.hectordelgado.wethepeople

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
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
