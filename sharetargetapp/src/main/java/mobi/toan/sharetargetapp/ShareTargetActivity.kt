package mobi.toan.sharetargetapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.OpenableColumns
import android.util.Log
import java.io.File

class ShareTargetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_target)

        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("application/pdf") == true) {
                    handlePdf(intent)
                }
            }
            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
    }

    private fun handlePdf(returnIntent: Intent) {
        returnIntent.data?.let { returnUri ->
            val outputFile = File.createTempFile("receivedPdf", ".pdf", cacheDir)
            val fileOutputStream = outputFile.outputStream()
            with(contentResolver.openInputStream(returnUri)) {
                this?.copyTo(fileOutputStream)
            }

            // TODO if printer needs fileOutputStream then you can use the stream here
            // I have nothing todo here so I close it

            fileOutputStream.close()

            Log.e("TOAN2", "file received: ${outputFile.length()}")
            // TODO Start the printing action here?
            contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            Log.e("TOAN2", "name: ${cursor.getString(nameIndex)}")
            Log.e("TOAN2", "size: ${cursor.getString(sizeIndex)}")

            // TODO do this on a background thread to write file to temp folder for further processing
        }
    }
}