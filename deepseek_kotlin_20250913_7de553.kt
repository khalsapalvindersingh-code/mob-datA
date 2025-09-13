import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import android.util.Log
import android.widget.Toast
import java.net.NetworkInterface
import java.util.Collections

class MainActivity : AppCompatActivity() {

    private lateinit var targetUrlInput: EditText
    private lateinit var portInput: EditText
    private lateinit var generateButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var phishingLinkTextView: TextView
    private lateinit var disclaimerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        targetUrlInput = findViewById(R.id.targetUrlInput)
        portInput = findViewById(R.id.portInput)
        generateButton = findViewById(R.id.generateButton)
        statusTextView = findViewById(R.id.statusTextView)
        phishingLinkTextView = findViewById(R.id.phishingLinkTextView)
        disclaimerTextView = findViewById(R.id.disclaimerTextView)

        // Add ethical disclaimer
        disclaimerTextView.text = "FOR EDUCATIONAL AND AUTHORIZED TESTING PURPOSES ONLY\nUnauthorized use is illegal and unethical"

        generateButton.setOnClickListener {
            // Show warning dialog before proceeding
            showEthicalWarningDialog()
        }
    }

    private fun showEthicalWarningDialog() {
        AlertDialog.Builder(this)
            .setTitle("Ethical Warning")
            .setMessage("This tool should only be used for:\n\n" +
                    "• Authorized security testing\n" +
                    "• Educational purposes\n" +
                    "• Testing with explicit permission\n\n" +
                    "Unauthorized use may be illegal.")
            .setPositiveButton("I Understand") { dialog, which ->
                generatePhishingLink()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun generatePhishingLink() {
        val targetUrl = targetUrlInput.text.toString().trim()
        val port = portInput.text.toString().toIntOrNull() ?: 5000

        if (targetUrl.isEmpty()) {
            showError("Please enter a target URL")
            return
        }

        if (!isValidUrl(targetUrl)) {
            showError("Please enter a valid URL")
            return
        }

        try {
            val localIp = getLocalIpAddress() ?: run {
                showError("Cannot determine local IP address")
                return
            }

            val phishingLink = "http://$localIp:$port"
            phishingLinkTextView.text = "Generated Link: $phishingLink"

            // Create educational documentation instead of phishing page
            createEducationalDocumentation(phishingLink, targetUrl)

            statusTextView.text = "Link generated for educational purposes"
            
            Toast.makeText(this, "For authorized testing only", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Log.e("SecurityApp", "Error generating link", e)
            showError("Error generating link: ${e.message}")
        }
    }

    private fun createEducationalDocumentation(generatedLink: String, targetUrl: String) {
        val htmlContent = """
            <html>
            <head>
                <title>Educational Security Demo</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    .warning { color: red; font-weight: bold; }
                </style>
            </head>
            <body>
                <h1>Security Education Demo</h1>
                <p class="warning">⚠️ FOR EDUCATIONAL PURPOSES ONLY ⚠️</p>
                <p>This demonstration shows how phishing links can be created.</p>
                <p><strong>Generated Link:</strong> <a href="$generatedLink">$generatedLink</a></p>
                <p><strong>Target URL for demonstration:</strong> $targetUrl</p>
                <hr>
                <h2>Security Best Practices:</h2>
                <ul>
                    <li>Always verify URLs before clicking</li>
                    <li>Check for HTTPS and valid certificates</li>
                    <li>Be cautious of unsolicited links</li>
                    <li>Use security software and keep it updated</li>
                </ul>
            </body>
            </html>
        """.trimIndent()

        try {
            val file = File(getExternalFilesDir(null), "security_demo.html")
            FileOutputStream(file).use { fos ->
                fos.write(htmlContent.toByteArray())
            }
        } catch (e: Exception) {
            Log.e("SecurityApp", "Error creating documentation", e)
        }
    }

    private fun getLocalIpAddress(): String? {
        return try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr is java.net.Inet4Address) {
                        return addr.hostAddress
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            java.net.URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showError(message: String) {
        statusTextView.text = message
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        init {
            System.loadLibrary("security-lib")
        }
    }
}