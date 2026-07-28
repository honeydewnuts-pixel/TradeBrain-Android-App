package com.tradebrain.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtAccount = findViewById<EditText>(R.id.edtAccount)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val edtServer = findViewById<EditText>(R.id.edtServer)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val account = edtAccount.text.toString()
            val password = edtPassword.text.toString()
            val server = edtServer.text.toString()
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            DeviceLock.checkDevice(account, deviceId, server) { allowed ->
                if(allowed){
                    val prefs = getSharedPreferences("TradeBrain", MODE_PRIVATE)
                    prefs.edit().putString("account", account).putString("server", server).apply()
                    startService(Intent(this, ScreenshotService::class.java))
                    Toast.makeText(this, "TradeBrain Started", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Account already active on another device", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
