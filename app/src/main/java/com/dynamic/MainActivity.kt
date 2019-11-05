package com.dynamic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var mySessionId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        download?.setOnClickListener { downloadDynamicModule() }

        start?.setOnClickListener {
            val intent = Intent()
            intent.setClassName("com.dynamic", "com.dynamic.module.DynamicActivity")
            startActivity(intent)
        }

        download_new.setOnClickListener { downloadNewDynamicModule() }

        start_new.setOnClickListener {
            val intent = Intent()
            intent.setClassName("com.dynamic", "com.dynamic.dynamic_feature_blz" +
                    ".NewDynamicActivity")
            startActivity(intent)
        }

    }

    private fun downloadDynamicModule() {
        val splitInstallManager = SplitInstallManagerFactory.create(this)

        val request = SplitInstallRequest
                .newBuilder()
                .addModule("dynamic_module")
                .build()

        val listener = SplitInstallStateUpdatedListener { splitInstallSessionState ->
            if (splitInstallSessionState.sessionId() == mySessionId) {
                when (splitInstallSessionState.status()) {
                    SplitInstallSessionStatus.INSTALLED -> {
                        Log.d(TAG, "Dynamic Module downloaded")
                        toast("Dynamic Module downloaded")
                    }
                    SplitInstallSessionStatus.DOWNLOADING -> {
                        splitInstallSessionState.bytesDownloaded()
                    }
                }
            }
        }

        splitInstallManager.registerListener(listener)

        splitInstallManager.startInstall(request)
                .addOnFailureListener { e -> Log.d(TAG, "Exception: $e") }
                .addOnSuccessListener { sessionId -> mySessionId = sessionId }
    }

    private fun downloadNewDynamicModule() {
        val splitInstallManager = SplitInstallManagerFactory.create(this)
        val request = SplitInstallRequest
                .newBuilder()
                .addModule("dynamic_feature_blz")
                .build()

        download_module.visibility = View.VISIBLE

        val listener = SplitInstallStateUpdatedListener { splitInstallSessionState ->
            if (splitInstallSessionState.sessionId() == mySessionId) {
                when (splitInstallSessionState.status()) {
                    SplitInstallSessionStatus.INSTALLED -> {
                        download_module.visibility = View.GONE
                        Log.d(TAG, "New Dynamic Module downloaded")
                        toast("New Dynamic Module downloaded")
                    }
                    SplitInstallSessionStatus.DOWNLOADING -> {
                        splitInstallSessionState.bytesDownloaded()
                        download_module.progress = (splitInstallSessionState.bytesDownloaded()
                                / splitInstallSessionState.totalBytesToDownload()).toInt()
                    }
                    else -> {
                        toast("Error has occurred!")
                    }
                }
            }
        }

        splitInstallManager.registerListener(listener)

        splitInstallManager.startInstall(request)
                .addOnFailureListener { e -> Log.d(TAG, "Exception: $e") }
                .addOnSuccessListener { sessionId -> mySessionId = sessionId }
    }
}
