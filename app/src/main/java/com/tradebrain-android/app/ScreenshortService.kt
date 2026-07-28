package com.tradebrain.app
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ScreenshotService : Service() {
    private val timer = Timer()
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer.scheduleAtFixedRate(object: TimerTask(){
            override fun run() {
                val bitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888) // Replace with real screenshot
                sendToBrain(bitmap)
            }
        }, 0, 60000) // Every 60 seconds
        return START_STICKY
    }
    
    private fun sendToBrain(bitmap: Bitmap){
        val file = File(cacheDir, "chart.jpg")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        val body = MultipartBody.Part.createFormData("file", "chart.jpg", file.asRequestBody("image/jpeg".toMediaType()))
        
        ApiClient.brainApi.analyze(body).enqueue(object: retrofit2.Callback<SignalResponse>{
            override fun onResponse(call: retrofit2.Call<SignalResponse>, response: retrofit2.Response<SignalResponse>) {
                val signal = response.body()?.signal
                if(signal != "WAIT") TradeExecutor.executeTrade(signal!!)
            }
            override fun onFailure(call: retrofit2.Call<SignalResponse>, t: Throwable) {}
        })
    }
    override fun onBind(intent: Intent?): IBinder? = null
}
