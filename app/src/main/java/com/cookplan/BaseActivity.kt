package com.cookplan

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by DariaEfimova on 13.10.17.
 */

open class BaseActivity : AppCompatActivity(), BaseView {

    fun setTitle(title: String) {
        supportActionBar?.title = title
        super.setTitle(title)
    }

    fun setSubTitle(subTitle: String) {
        supportActionBar?.subtitle = subTitle
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun startActivityWithUpAnimation(intent: Intent) {
        isUpAnimation = true
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up_activity, R.anim.slide_out_up_activity)
    }


    fun startActivityForResultWithUpAnimation(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.slide_in_up_activity, R.anim.slide_out_up_activity)
    }

    fun startActivityWithLeftAnimation(intent: Intent) {
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_left_activity)
    }

    fun startActivityForResultWithLeftAnimation(intent: Intent, requestCode: Int) {
        startActivityForResult(intent, requestCode)
        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_left_activity)
    }

    fun hideNavigationIcon() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setHomeButtonEnabled(false)
    }

    fun setNavigationArrow() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun onBackPressed() {
        finish()
        if (isUpAnimation) {
            isUpAnimation = false
            overridePendingTransition(R.anim.slide_in_down_activity, R.anim.slide_out_down_activity)
        } else {
            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_right_activity)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun getContext(): Context {
        return this
    }

    companion object {

        private var isUpAnimation = false
    }
}
