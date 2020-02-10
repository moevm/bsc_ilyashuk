package com.cupofme.teststand
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.list_button).setOnClickListener {
            startActivity(Intent(this, AudioListActivity::class.java))
        }


        findViewById<Button>(R.id.nn_button).setOnClickListener {
            startActivity(Intent(this, NNActivity::class.java))
        }
    }

}
