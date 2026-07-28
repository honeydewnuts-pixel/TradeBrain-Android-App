package com.tradebrain.app
object DeviceLock {
    fun checkDevice(account: String, deviceId: String, server: String, callback: (Boolean) -> Unit){
        // Later we call /login API from brain server
        callback(true) // For now allow all
    }
}
