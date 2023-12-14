package incasable.integration.plugins.IncasaBLE

import android.Manifest
import android.util.Log
import com.bewellinnovations.incasable.IncasaBleDevice
import com.bewellinnovations.incasable.IncasaBleState
import com.bewellinnovations.incasable.IncasaBleStateMetaData
import com.bewellinnovations.incasable.ble.managers.IncasaBleProvider
import com.bewellinnovations.incasable.models.IncasaActiv8015Measurement
import com.getcapacitor.JSObject
import com.getcapacitor.PermissionState
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.annotation.Permission
import com.getcapacitor.annotation.PermissionCallback
import kotlinx.coroutines.runBlocking

@CapacitorPlugin(
    name = "Kdoc",
    permissions = [
        Permission(
            alias = "bluetooth",
            strings = [
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            ]
        ),
        Permission(
            alias = "push_notifications",
            strings = [
                Manifest.permission.POST_NOTIFICATIONS
            ])
    ])
class IncasaBLEPlugin : Plugin() {

    private var implementation: IncasaBLE? = null
    private var device: IncasaBleDevice? = null

    override fun load() {
        implementation = IncasaBLE(activity)
        runBlocking { setupEvents() }
    }


    private suspend fun setupEvents() {
        implementation?.manager?.events?.collect{
            when(it) {
                is IncasaEvent.DeviceLinkingChanged -> {
                    val stateObject = JSObject()
                    stateObject.put("paired", it.isLinked);
                    notifyListeners("pairingChange", stateObject)
                }
                is IncasaEvent.MeasurementReceived -> {
                    val stateObject = JSObject().put("measure", (it.measurement as Number))
                    notifyListeners("measureChange", stateObject)
                }
                else -> {

                }
            }
        }
    }

    @PluginMethod
    fun verifyRequiredPermissions(call: PluginCall) {
        val missingPermissions = ArrayList<String>()

        if (getPermissionState("bluetooth") != PermissionState.GRANTED) {
            missingPermissions.add("bluetooth")
        }

        Log.d("getPermissionsState", getPermissionState("push_notifications").toString())
        if (getPermissionState("push_notifications") != PermissionState.GRANTED) {
            missingPermissions.add("push_notifications")
        }

        if (missingPermissions.isNotEmpty()) {
            requestPermissionForAliases(missingPermissions.toTypedArray(), call, "permissionsCallback")
        }

        val ret = JSObject()
        ret.put("verified", true);
        call.resolve(ret)
    }

    @PermissionCallback
    private fun permissionsCallback(call: PluginCall) {
        call.resolve();
    }

    private fun setDevice(deviceName: String) {
        when(deviceName) {
            "A&D UA-651" -> device = IncasaBleDevice.AND_UA_651
            "A&D UC-352" -> device = IncasaBleDevice.AND_UC_352
            "A&D UT-201" -> device = IncasaBleDevice.AND_UT_201
            "Nonin 3230" -> device = IncasaBleDevice.NONIN_3230
            "Activ8 A8015" -> device = IncasaBleDevice.ACTIV8_8015
            "Activ8 A8016" -> device = IncasaBleDevice.ACTIV8_8016
            "Wellue DuoEK" -> device = IncasaBleDevice.WELLUE_DUO
        }
    }

    @PluginMethod
    fun startDevice(call: PluginCall) {
        call.getString("deviceName")?.let { setDevice(it) }
        implementation?.manager?.start(device!!)
    }
}
