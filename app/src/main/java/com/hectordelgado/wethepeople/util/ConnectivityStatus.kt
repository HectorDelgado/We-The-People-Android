package com.hectordelgado.wethepeople.util

import android.content.Context
import android.net.*

/**
 *  We The People
 *
 *  @author Hector Delgado
 *
 *  Created on June 02, 2020.
 *  Copyright © 2020 Hector Delgado. All rights reserved.
 */
class ConnectivityStatus private constructor(context: Context) {

    //private
    private var networkRequest: NetworkRequest
    private var networkCallback: ConnectivityManager.NetworkCallback
    private var isAvailable = false


    init {
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkRequest = NetworkRequest.Builder().build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                isAvailable = true
                super.onAvailable(network)
            }

            override fun onBlockedStatusChanged(network: Network, blocked: Boolean) {
                super.onBlockedStatusChanged(network, blocked)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties)
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
            }

            override fun onLost(network: Network) {
                isAvailable = false
                super.onLost(network)
            }

            override fun onUnavailable() {
                super.onUnavailable()
            }
        }
        cm.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun isConnected() = isAvailable

    // Used to create a singleton instance
    companion object {
        @Volatile private var INSTANCE: ConnectivityStatus? = null

        fun getInstance(context: Context): ConnectivityStatus {
            val i1 = INSTANCE
            if (i1 != null) {
                return i1
            }

            return synchronized(this) {
                val i2 = INSTANCE
                if (i2 != null) {
                    i2
                } else {
                    val temp = ConnectivityStatus(context)
                    INSTANCE = temp
                    temp
                }
            }
        }
    }
}