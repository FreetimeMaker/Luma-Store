package com.freetime.appstore.store

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.freetime.appstore.BuildConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class StoreCatalogActivity : AppCompatActivity() {
    private val client = OkHttpClient()
    private lateinit var list: LinearLayout
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Luma Store"

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(24))
        }
        val heading = TextView(this).apply {
            text = "Approved Android apps"
            textSize = 24f
            setPadding(0, 0, 0, dp(8))
        }
        val intro = TextView(this).apply {
            text = "Apps and metadata are loaded from the Luma Store API."
            textSize = 14f
            setPadding(0, 0, 0, dp(16))
        }
        status = TextView(this).apply {
            text = "Loading apps…"
            textSize = 14f
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, dp(12), 0, dp(12))
        }
        val progress = ProgressBar(this).apply {
            isIndeterminate = true
        }
        list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        root.addView(heading)
        root.addView(intro)
        root.addView(progress, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        })
        root.addView(status)
        root.addView(list)

        val scroll = ScrollView(this).apply { addView(root) }
        setContentView(scroll)

        loadApps(progress)
    }

    private fun loadApps(progress: ProgressBar) {
        val base = BuildConfig.LUMA_API_BASE_URL.trimEnd('/')
        val request = Request.Builder()
            .url("$base/api/v2/lumastore/apps?platform=Android")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progress.visibility = ProgressBar.GONE
                    status.text = "Could not load apps: ${e.message ?: "network error"}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val body = it.body?.string().orEmpty()
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            progress.visibility = ProgressBar.GONE
                            status.text = "Could not load apps (HTTP ${it.code})."
                        }
                        return
                    }

                    val apps = try { JSONArray(body) } catch (_: Exception) { JSONArray() }
                    runOnUiThread {
                        progress.visibility = ProgressBar.GONE
                        list.removeAllViews()
                        if (apps.length() == 0) {
                            status.text = "No approved Android apps found."
                            return@runOnUiThread
                        }
                        status.text = "${apps.length()} app(s)"
                        for (i in 0 until apps.length()) {
                            val app = apps.optJSONObject(i) ?: continue
                            val id = app.optString("id")
                            if (id.isBlank()) continue

                            val card = LinearLayout(this@StoreCatalogActivity).apply {
                                orientation = LinearLayout.VERTICAL
                                setPadding(dp(14), dp(14), dp(14), dp(14))
                                background = android.graphics.drawable.GradientDrawable().apply {
                                    setColor(0xFFF4F4F6.toInt())
                                    cornerRadius = dp(14).toFloat()
                                }
                            }
                            val name = TextView(this@StoreCatalogActivity).apply {
                                text = app.optString("name", "Unnamed app")
                                textSize = 19f
                                setTextColor(0xFF111827.toInt())
                            }
                            val description = TextView(this@StoreCatalogActivity).apply {
                                text = app.optString("short_description").ifBlank { app.optString("description") }
                                textSize = 14f
                                setTextColor(0xFF4B5563.toInt())
                                setPadding(0, dp(6), 0, dp(8))
                            }
                            val categoryName = app.optJSONObject("category")?.optString("name").orEmpty()
                            val meta = TextView(this@StoreCatalogActivity).apply {
                                text = listOf(app.optString("version"), categoryName).filter { value -> value.isNotBlank() }.joinToString(" · ")
                                textSize = 12f
                                setTextColor(0xFF6B7280.toInt())
                            }
                            val open = Button(this@StoreCatalogActivity).apply {
                                text = "Open details"
                                setOnClickListener {
                                    startActivity(Intent(this@StoreCatalogActivity, StoreAppDetailActivity::class.java).putExtra(StoreAppDetailActivity.EXTRA_APP_ID, id))
                                }
                            }
                            card.addView(name)
                            card.addView(description)
                            card.addView(meta)
                            card.addView(open)
                            list.addView(card, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                                bottomMargin = dp(12)
                            })
                        }
                    }
                }
            }
        })
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
