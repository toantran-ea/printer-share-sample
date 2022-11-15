package mobi.toan.sharesourceapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import mobi.toan.sharesourceapp.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream


class MainShareActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.shareButton.setOnClickListener {
            shareFile()
        }

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun shareFile() {
        val pdfFile = getPdfFile()
        with(pdfFile) {
            val intent = createShareIntent(this)
            startActivity(Intent.createChooser(intent, "Share pdf using"))
            // TODO share intent
        }
    }

    private fun createShareIntent(file: File): Intent {
        Log.e("TOAN", "original: ${file.length() ?: 0}")
        val sharedFileUri = FileProvider.getUriForFile(
            this,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
        checkUri(sharedFileUri)
        return Intent().apply {
            action = "android.intent.action.XPRINTER_PRINT"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            setDataAndType(sharedFileUri, "application/pdf")
        }
    }

    private fun checkUri(uri: Uri) {
        Log.e("TOAN", "uri fileProvider: $uri")
        val file: File? = uri.path?.let { File(it) }
        Log.e("TOAN", "file size: ${file?.length() ?: 0}")
    }

    private fun getPdfFile(): File {
        val rawStream = resources.openRawResource(
            resources.getIdentifier(
                "sample",
                "raw", packageName
            )
        )
        // writer to internal file (in real production app - this is the process of downloading
        // pdf file to printer from network)
        val outputFile = File.createTempFile("sample", ".pdf", cacheDir)
        val fileWritOutputStream = FileOutputStream(outputFile)
        fileWritOutputStream.write(rawStream.readBytes())
        fileWritOutputStream.close()
        rawStream.close()
        return outputFile
    }
}