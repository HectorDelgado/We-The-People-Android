package com.hectordelgado.wethepeople

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hectordelgado.wethepeople.util.ConnectivityStatus
import com.hectordelgado.wethepeople.util.Failure
import com.hectordelgado.wethepeople.util.Result
import com.hectordelgado.wethepeople.util.Success
import kotlinx.android.synthetic.main.activity_recent_petitions.*
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL

class RecentPetitionsActivity : AppCompatActivity(),
    PetitionAdapter.PetitionViewHolder.PetitionListener,
    SearchDialogFragment.SearchDialogListener {
    private val TAG_DEBUG = "DEBUGZ"
    private val petitionAdapter = PetitionAdapter(mutableListOf<PetitionModel>(), this)
    private val cs = ConnectivityStatus.getInstance(this)
    private val baseURL = "https://api.whitehouse.gov/v1/petitions.json?"
    private val limit = 25

    private var currentPetitions = mutableListOf<PetitionModel>()
    private var unfilteredPetitions = mutableListOf<PetitionModel>()
    private var offset = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_petitions)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.apply {
            setHasFixedSize(false)
            this.layoutManager = LinearLayoutManager(this@RecentPetitionsActivity)
            this.adapter = petitionAdapter
            addItemDecoration(DividerItemDecoration(this@RecentPetitionsActivity, RecyclerView.VERTICAL))
        }

        loadData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.optionsmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                val dialog = SearchDialogFragment()
                dialog.show(supportFragmentManager, "SearchDialogFragment")
                true
            }
            R.id.reset -> {
                currentPetitions.clear()
                currentPetitions.addAll(unfilteredPetitions)

                petitionAdapter.updateData(currentPetitions)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun previousBtnClicked(view: View) {
        if (offset > 0) {
            offset -= 25
            loadData()
        }
    }

    fun nextBtnClicked(view: View) {
        if (offset < 500 - limit) {
            offset+= 25
            loadData()
        } else {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Oops")
            alertDialog.setMessage("Max petitions reached!")
            alertDialog.setPositiveButton("Ok") { _, _ -> }
            alertDialog.show()
        }
    }

    private fun filterResults(petitions: List<PetitionModel>, keywords: List<String>, result: (MutableList<PetitionModel>) -> Unit) {
        val tempResults = mutableListOf<PetitionModel>()

        petitions.forEach { petition ->
            keywords.forEach { keyword ->
                if (petition.title.contains(keyword, true)) {
                    tempResults.add(petition)
                }
            }
        }.also {
            result(tempResults)
        }
    }

    private fun loadData() {
        progressBar.visibility = View.VISIBLE
        dimView.visibility = View.VISIBLE
        nextBtn.isEnabled = false
        prevBtn.isEnabled = false

        GlobalScope.launch(context = Dispatchers.IO) {
            if (cs.isConnected()) {
                when(val result = getPetitions("${baseURL}limit=$limit&offset=$offset")) {
                    is Success -> {
                        unfilteredPetitions.clear()
                        currentPetitions.clear()

                        unfilteredPetitions.addAll(result.value)
                        currentPetitions.addAll(result.value)

                        runOnUiThread {
                            supportActionBar?.title = "Petitions ${offset + 1}-${offset + limit}"
                            petitionAdapter.updateData(currentPetitions)
                        }
                    }
                    is Failure -> {
                        runOnUiThread {
                            val builder = AlertDialog.Builder(this@RecentPetitionsActivity)
                            builder
                                .setTitle("Oops!")
                                .setMessage("Something went wrong!\n${result.reason.localizedMessage}")
                                .setPositiveButton("Ok") { dialog, which ->

                                }
                            builder.show()
                        }
                    }
                }
            } else {
                runOnUiThread {
                    AlertDialog.Builder(this@RecentPetitionsActivity).apply {
                        setTitle("Oops")
                        setMessage("You need an internet connection!")
                        setPositiveButton("Ok") { dialog, which ->  }
                    }.also {
                        it.show()
                    }
                }
            }


            runOnUiThread {
                progressBar.visibility = View.INVISIBLE
                dimView.visibility = View.GONE
                nextBtn.isEnabled = true
                prevBtn.isEnabled = true
            }
        }
    }

    private suspend fun getPetitions(url: String): Result<MutableList<PetitionModel>, Exception> {
        val petitions = mutableListOf<PetitionModel>()

        try {
            withTimeout(8000L) {
                val apiResponse = JSONObject(URL(url).readText())
                val results = apiResponse.getJSONArray("results")

                for (i in 0 until results.length()) {
                    val result = JSONObject(results[i].toString())
                    val id = result["id"].toString()
                    val title = result["title"].toString()
                    val body = result["body"].toString()
                    val signatureCount = result["signatureCount"].toString().toInt()
                    val signaturesNeeded = result["signaturesNeeded"].toString().toInt()
                    petitions.add(
                        PetitionModel(
                            id = id,
                            title = title,
                            body = body,
                            signatureCount = signatureCount,
                            signaturesNeeded = signaturesNeeded))
                }
            }

            return Success(petitions)
        } catch (ex: MalformedURLException) {
            Log.e(TAG_DEBUG, ex.toString())
            return Failure(Exception("MalformedURLException"))
        } catch (ex: JSONException) {
            Log.e(TAG_DEBUG, ex.toString())
            return Failure(Exception("Bad JSON"))
        } catch (ex: FileNotFoundException) {
            Log.e(TAG_DEBUG, ex.toString())
            return Failure(Exception("File not Found"))
        } catch (ex: TimeoutCancellationException) {
            Log.e(TAG_DEBUG, ex.toString())
            return Failure(Exception("Timeout occurred"))
        } catch (ex: Exception) {
            Log.e(TAG_DEBUG, ex.toString())
            return Failure(Exception("Unknown error"))
        }
    }

    override fun onItemClicked(petition: PetitionModel) {
        Intent(this, DetailActivity::class.java).also {
            CurrentPetition.setPetition(petition)
            startActivity(it)
        }
    }

    override fun onSearchClick(dialog: DialogFragment, keyword: String) {
        val keywords = keyword.split(",")
        filterResults(currentPetitions, keywords) { results ->
            if (results.size > 0) {
                //filteredPetitions.clear()
                //filteredPetitions.addAll(results)

                currentPetitions.clear()
                currentPetitions.addAll(results)

                petitionAdapter.updateData(currentPetitions)
            } else {
                Log.d("debugz", "No results found")
            }

        }

    }

    override fun onCancelClick(dialog: DialogFragment) {

    }
}
