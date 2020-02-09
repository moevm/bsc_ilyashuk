package com.cupofme.teststand

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import java.io.File

class AudioListActivity : AppCompatActivity() {

    private val filesRequestCode = 998
    private val directoryRequestCode = 999

    private val items = ArrayList<Uri>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_list)

        findViewById<Button>(R.id.add_files).setOnClickListener {
            var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.apply {
                type = "audio/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            chooseFile = Intent.createChooser(chooseFile, "Choose files")
            startActivityForResult(chooseFile, filesRequestCode)
        }

        findViewById<Button>(R.id.add_directory).setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1234
            )
            val chooseDirectory = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            startActivityForResult(chooseDirectory, directoryRequestCode)

        }

        findViewById<ListView>(R.id.list_view).setOnItemClickListener { parent, view, position, id ->
            MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(applicationContext, items[position])
                prepare()
                start()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_info) {
            Toast.makeText(this, "${getTotalLength()} seconds", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == filesRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                items.clear()
                val clipData = data?.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        items.add(clipData.getItemAt(i).uri)
                    }
                } else {
                    items.add(data?.data!!)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    items.map { getFileName(it) })
                findViewById<ListView>(R.id.list_view).adapter = adapter
            }
        } else if (requestCode == directoryRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val path = Environment.getExternalStorageDirectory().toString() + "/TestData/"
                val directory = File(path)

                val files = directory.listFiles()

                items.clear()
                for (file in files) {
                    items.add(file.toUri())
                }
                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    items.map { getFileName(it) })
                findViewById<ListView>(R.id.list_view).adapter = adapter
            }
        }
    }


    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }


    private fun getTotalLength(): Int {
        var totalLength = 0
        val mmr = MediaMetadataRetriever()
        items.forEach {
            mmr.setDataSource(this, it)
            totalLength += mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toInt()
        }
        return totalLength / 1000
    }
}
