package com.freetime.appstore.english.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.freetime.appstore.R
import com.freetime.appstore.english.apps.freetime.*
import com.freetime.appstore.english.contributions.GH_Activity
import com.freetime.appstore.german.main.DE_MainActivity
import com.freetime.appstore.store.StoreCatalogActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        addLiveCatalogButton("Browse approved Luma Store apps")

        val gwInfobtn: Button = findViewById(R.id.gwInfobtn)
        gwInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, GW_Activity::class.java))
        }

        val donoInfobtn: Button = findViewById(R.id.donoInfobtn)
        donoInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, Dono_Activity::class.java))
        }

        val fasInfobtn: Button = findViewById(R.id.fasInfobtn)
        fasInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, FAS_Activity::class.java))
        }

        val catInfobtn: Button = findViewById(R.id.catInfobtn)
        catInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, CatClickerActivity::class.java))
        }

        val PLCInfobtn: Button = findViewById(R.id.PLCInfobtn)
        PLCInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PLC_Activity::class.java))
        }

        val plc2Infobtn: Button = findViewById(R.id.plc2Infobtn)
        plc2Infobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PLC2Activity::class.java))
        }

        val platInfobtn: Button = findViewById(R.id.platInfobtn)
        platInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlatformerActivity::class.java))
        }

        val osInfobtn: Button = findViewById(R.id.osInfobtn)
        osInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, OS_Activity::class.java))
        }

        val cryptoInfobtn: Button = findViewById(R.id.cryptoInfobtn)
        cryptoInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, CryptoClickerActivity::class.java))
        }

        val RPJInfobtn: Button = findViewById(R.id.RPJInfobtn)
        RPJInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, RadioPlayerJavaActivity::class.java))
        }

        val KPDHJInfobtn: Button = findViewById(R.id.KPDHJInfobtn)
        KPDHJInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, KPDHJ_Activity::class.java))
        }

        val SSMPCJInfobtn: Button = findViewById(R.id.SSMPCJInfobtn)
        SSMPCJInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, SSMPCJ_Activity::class.java))
        }

        val ghInfobtn: Button = findViewById(R.id.ghInfobtn)
        ghInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, GH_Activity::class.java))
        }

        val deInfobtn: Button = findViewById(R.id.deInfobtn)
        deInfobtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, DE_MainActivity::class.java))
        }
    }

    private fun addLiveCatalogButton(label: String) {
        val content = findViewById<ViewGroup>(android.R.id.content)
        val frame = content.getChildAt(0) as? ViewGroup ?: return
        val scroll = frame.getChildAt(0) as? ScrollView ?: return
        val list = scroll.getChildAt(0) as? LinearLayout ?: return
        val button = Button(this).apply {
            text = label
            setOnClickListener { startActivity(Intent(this@MainActivity, StoreCatalogActivity::class.java)) }
        }
        list.addView(button, 0, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            bottomMargin = (12 * resources.displayMetrics.density).toInt()
        })
    }
}
