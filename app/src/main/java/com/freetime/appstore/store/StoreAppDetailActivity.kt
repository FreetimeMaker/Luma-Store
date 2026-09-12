package com.freetime.appstore.store

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import org.json.JSONObject
import java.io.IOException

class StoreAppDetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_APP_ID = "app_id"
    }

    private val client = OkHttpClient()
    private lateinit var root: LinearLayout
    private lateinit var progress: ProgressBar
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "App details"

        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(28))
        }
        progress = ProgressBar(this).apply { isIndeterminate = true }
        status = TextView(this).apply {
            text = "Loading app details…"
            textSize = 14f
            setPadding(0, dp(8), 0, dp(8))
        }
        root.addView(progress)
        root.addView(status)
        setContentView(ScrollView(this).apply { addView(root) })

        val appId = intent.getStringExtra(EXTRA_APP_ID).orEmpty()
        if (appId.isBlank()) {
            progress.visibility = ProgressBar.GONE
            status.text = "Missing app id."
            return
        }
        loadApp(appId)
    }

    private fun loadApp(appId: String) {
        val base = BuildConfig.LUMA_API_BASE_URL.trimEnd('/')
        val request = Request.Builder().url("$base/api/v2/lumastore/apps/$appId").get().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    progress.visibility = ProgressBar.GONE
                    status.text = "Could not load app details: ${e.message ?: "network error"}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val body = it.body?.string().orEmpty()
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            progress.visibility = ProgressBar.GONE
                            status.text = "Could not load app details (HTTP ${it.code})."
                        }
                        return
                    }
                    val app = try { JSONObject(body) } catch (_: Exception) { null }
                    runOnUiThread {
                        progress.visibility = ProgressBar.GONE
                        if (app == null) {
                            status.text = "Invalid app response."
                            return@runOnUiThread
                        }
                        status.visibility = TextView.GONE
                        renderApp(app)
                    }
                }
            }
        })
    }

    private fun renderApp(app: JSONObject) {
        addHeading(app.optString("name", "Unnamed app"))
        addText(app.optString("short_description"))
        addSection("Description")
        addText(app.optString("description"))

        addSection("App information")
        addRow("Version", app.optString("version"))
        addRow("Version code", app.optString("version_code"))
        addRow("Package name", app.optString("package_name"))
        addRow("License", app.optString("license_type"))
        addRow("Category", app.optJSONObject("category")?.optString("name").orEmpty())
        addRow("Developer", app.optString("developer_name"))

        addSection("Author")
        addRow("Name", app.optString("author_name"))
        addClickable("Email", app.optString("author_email"), if (app.optString("author_email").isBlank()) null else "mailto:${app.optString("author_email")}")
        addClickable("Author website", app.optString("author_website"), app.optString("author_website"))

        addSection("Links")
        addClickable("Website", app.optString("website_url"), app.optString("website_url"))
        val sourceCode = app.optString("source_code_url").ifBlank { app.optString("repo_url") }
        addClickable("Source code", sourceCode, sourceCode)
        addClickable("Issue tracker", app.optString("issue_tracker_url"), app.optString("issue_tracker_url"))
        addClickable("Translation", app.optString("translation_url"), app.optString("translation_url"))
        addClickable("Changelog", app.optString("changelog_url"), app.optString("changelog_url"))

        addSection("Donations")
        addClickable("Donate", app.optString("donate_url"), app.optString("donate_url"))
        addRow("Liberapay", app.optString("liberapay"))
        addRow("OpenCollective", app.optString("opencollective"))
        addRow("Bitcoin", app.optString("bitcoin"))
        addRow("Litecoin", app.optString("litecoin"))

        val antiFeatures = jsonArrayToText(app.optJSONArray("ant_features"))
        addSection("Anti-Features")
        addText(if (antiFeatures.isBlank()) "None set by review." else antiFeatures)

        val downloadUrl = findAndroidDownloadUrl(app.optJSONArray("platforms"))
        if (downloadUrl.isNotBlank()) {
            val download = Button(this).apply {
                text = "Download Android app"
                setOnClickListener { openUri(downloadUrl) }
            }
            root.addView(download, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                topMargin = dp(20)
            })
        }
    }

    private fun addHeading(value: String) {
        root.addView(TextView(this).apply {
            text = value
            textSize = 26f
            setPadding(0, 0, 0, dp(8))
        })
    }

    private fun addSection(title: String) {
        root.addView(TextView(this).apply {
            text = title
            textSize = 19f
            setPadding(0, dp(20), 0, dp(8))
        })
    }

    private fun addText(value: String) {
        if (value.isBlank()) return
        root.addView(TextView(this).apply {
            text = value
            textSize = 14f
            setPadding(0, 0, 0, dp(6))
        })
    }

    private fun addRow(label: String, value: String) {
        if (value.isBlank() || value == "null") return
        root.addView(TextView(this).apply {
            text = "$label: $value"
            textSize = 14f
            setPadding(0, dp(3), 0, dp(3))
        })
    }

    private fun addClickable(label: String, value: String, uri: String?) {
        if (value.isBlank() || value == "null") return
        root.addView(TextView(this).apply {
            text = "$label: $value"
            textSize = 14f
            setPadding(0, dp(5), 0, dp(5))
            if (!uri.isNullOrBlank()) {
                setTextColor(0xFF3F51B5.toInt())
                setOnClickListener { openUri(uri) }
            }
        })
    }

    private fun openUri(uri: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
        } catch (_: Exception) {
            // Ignore unsupported URI schemes instead of crashing the store.
        }
    }

    private fun findAndroidDownloadUrl(platforms: JSONArray?): String {
        if (platforms == null) return ""
        for (i in 0 until platforms.length()) {
            val platform = platforms.optJSONObject(i) ?: continue
            if (platform.optString("platform").equals("Android", ignoreCase = true)) {
                return platform.optString("download_url")
            }
        }
        return ""
    }

    private fun jsonArrayToText(values: JSONArray?): String {
        if (values == null) return ""
        val result = mutableListOf<String>()
        for (i in 0 until values.length()) {
            val value = values.optString(i)
            if (value.isNotBlank()) result += value
        }
        return result.joinToString(", ")
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
