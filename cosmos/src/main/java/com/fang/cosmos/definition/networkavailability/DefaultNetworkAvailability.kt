package com.fang.cosmos.definition.networkavailability

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import com.fang.cosmos.definition.CosmosDef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * Default implementation for [NetworkAvailability]
 */
abstract class DefaultNetworkAvailability(
    context: Context = CosmosDef.Context,
    externalCoroutineScope: CoroutineScope = CosmosDef.CoroutineScope,
) : NetworkAvailability {
    private val _availableState = MutableStateFlow(isNetworkAvailable(context))
    override val availableState = _availableState.asStateFlow()

    init {
        externalCoroutineScope.launch {
            callbackFlow {
                val connectivityManager =
                    context.applicationContext.getSystemService<ConnectivityManager>()

                val networkCallback =
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            trySend(true)
                        }

                        override fun onLost(network: Network) {
                            trySend(false)
                        }
                    }

                connectivityManager?.registerDefaultNetworkCallback(networkCallback)

                awaitClose {
                    connectivityManager?.unregisterNetworkCallback(networkCallback)
                }
            }.flowOn(Dispatchers.Default)
                .collectLatest {
                    _availableState.value = it
                }
        }
    }

    /**
     * Get initial network availability.
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.applicationContext.getSystemService<ConnectivityManager>() ?: return false
        val cap =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return listOf(
            NetworkCapabilities.TRANSPORT_CELLULAR,
            NetworkCapabilities.TRANSPORT_WIFI,
            NetworkCapabilities.TRANSPORT_ETHERNET,
            NetworkCapabilities.TRANSPORT_BLUETOOTH,
            NetworkCapabilities.TRANSPORT_VPN,
            NetworkCapabilities.NET_CAPABILITY_INTERNET,
            NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED,
        ).any { cap.hasTransport(it) }
    }
}
