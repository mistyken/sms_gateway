package com.example.smsgateway

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private val READ_SMS_PERMISSIONS_REQUEST = 1
    private lateinit var messages: ListView
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var smsMessagesList = ArrayList<String>()
        messages = findViewById(R.id.messages)
        var input: EditText = findViewById(R.id.input)
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, smsMessagesList)
        messages.adapter = arrayAdapter

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "Please allow permission so the app can process sms", Toast.LENGTH_SHORT).show()
            }
            requestPermissions(arrayOf(Manifest.permission.READ_SMS), READ_SMS_PERMISSIONS_REQUEST)
        } else {
            refreshSmsInbox()
        }
    }

    private fun refreshSmsInbox() {
        var resolver: ContentResolver = contentResolver
        var smsIndexCursor = resolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)!!
        var indexBody = smsIndexCursor.getColumnIndex("body")
        var indexAddress = smsIndexCursor.getColumnIndex("address")
        if (indexBody < 0 || !smsIndexCursor.moveToFirst()) return
        arrayAdapter.clear()
        do {
            val str = "SMS from: " + smsIndexCursor.getString(indexAddress) + "\n" + smsIndexCursor.getString(indexBody) + "\n"
            arrayAdapter.add(str)
        } while (smsIndexCursor.moveToNext())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray){
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}