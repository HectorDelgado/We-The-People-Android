package com.hectordelgado.wethepeople

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hectordelgado.wethepeople.util.ConnectivityStatus
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cs = ConnectivityStatus.getInstance(this)

        recentPetitionsBtn.setOnClickListener {
            if (cs.isConnected()) {
                Intent(this, RecentPetitionsActivity::class.java).also {
                    startActivity(it)
                }
            } else {
                AlertDialog.Builder(this).apply {
                    setTitle("Oops")
                    setMessage("You need an internet connection to run this app.")
                    setPositiveButton("Ok") { _, _ -> }
                }.also {
                    it.show()
                }
            }

        }
    }
}
