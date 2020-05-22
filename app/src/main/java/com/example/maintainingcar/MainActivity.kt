package com.example.maintainingcar

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.maintainingcar.fragment.AddFragment
import com.example.maintainingcar.fragment.CardFragment
import com.example.maintainingcar.fragment.CountFragment
import com.example.maintainingcar.fragment.RecordListFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val tagAdd = "TAG_ADD"
    private val tagCard = "TAG_CARD"
    private val tagList = "TAG_LIST"
    private val countList = "TAG_Count"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
            it.title = getString(R.string.in_and_ex_card)
            changeFragment(R.id.inAndExCard)
        }

        naviView.setCheckedItem(R.id.inAndExCard)
        naviView.setNavigationItemSelectedListener {
            modifyTitle(it.itemId)
            changeFragment(it.itemId)
            drawerLayout.closeDrawers()
            true
        }

        fab.setOnClickListener {
            v ->
            Snackbar.make(v, "确认提交？", Snackbar.LENGTH_LONG).setAction("提交") {
                val addFragment = supportFragmentManager.findFragmentByTag(tagAdd) as AddFragment
                addFragment.insert()
                Toast.makeText(this, "提交成功！",Toast.LENGTH_SHORT).show()
            }.show()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    private fun modifyTitle(itemId:Int){
        supportActionBar?.let {
            if (itemId == R.id.inAndExCard) {
                it.title = getString(R.string.in_and_ex_card)
            } else if (itemId == R.id.inAndExAdd) {
                it.title = getString(R.string.in_and_ex_add)
            } else if (itemId == R.id.inAndExCount) {
                it.title = getString(R.string.in_and_ex_count)
            } else if (itemId == R.id.inAndExList) {
                it.title = getString(R.string.in_and_ex_list)
            }
        }
    }

    private fun changeFragment(itemId:Int){
        if (itemId == R.id.inAndExCard) {
            fab.visibility = View.GONE
            replaceFragment(CardFragment(), tagCard)
        } else if (itemId == R.id.inAndExAdd) {
            fab.visibility = View.VISIBLE
            replaceFragment(AddFragment(), tagAdd)
        } else if (itemId == R.id.inAndExCount) {
            fab.visibility = View.GONE
            replaceFragment(CountFragment(), countList)
        } else if (itemId == R.id.inAndExList) {
            fab.visibility = View.GONE
            replaceFragment(RecordListFragment(), tagList)
        }
    }

    private fun replaceFragment(fragment: Fragment, tag :String) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fl_view, fragment, tag)
        transaction.commit()
    }
}
