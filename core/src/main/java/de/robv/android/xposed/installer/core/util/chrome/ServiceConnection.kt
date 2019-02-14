package de.robv.android.xposed.installer.core.util.chrome

import android.content.ComponentName
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection

import java.lang.ref.WeakReference

/**
 * Implementation for the CustomTabsServiceConnection that avoids leaking the
 * ServiceConnectionCallback
 */
class ServiceConnection(connectionCallback: ServiceConnectionCallback) : CustomTabsServiceConnection() {
    // A weak reference to the ServiceConnectionCallback to avoid leaking it.
    private val mConnectionCallback: WeakReference<ServiceConnectionCallback> = WeakReference(connectionCallback)

    override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
        val connectionCallback = mConnectionCallback.get()
        connectionCallback?.onServiceConnected(client)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        val connectionCallback = mConnectionCallback.get()
        connectionCallback?.onServiceDisconnected()
    }
}