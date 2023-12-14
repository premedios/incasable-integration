package incasable.integration.plugins.IncasaBLE

import android.content.Context
import android.content.SharedPreferences
import com.bewellinnovations.incasable.IncasaBleDevice
import com.bewellinnovations.incasable.IncasaBleDevicePersistenceManager
import java.util.*

class CustomIncasaBleDevicePersistenceManager constructor(appCtx: Context) : IncasaBleDevicePersistenceManager {
    private val sharedPreferences: SharedPreferences = appCtx.getSharedPreferences(appCtx.packageName, Context.MODE_PRIVATE)

    override fun setPeripheralUUIDForDevice(device: IncasaBleDevice, uuid: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("_native_device_UUID+${device.id}", uuid)
        editor.apply()
    }

    override fun getPeripheralUUIDForDevice(device: IncasaBleDevice): String? {
        return sharedPreferences.getString("_native_device_UUID+${device.id}", null)
    }

    override fun setPeripheralMacAddressForDevice(device: IncasaBleDevice, mac: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("_native_device_MAC+${device.id}", mac)
        editor.apply()
    }

    override fun getPeripheralMacAddressForDevice(device: IncasaBleDevice): String? {
        return sharedPreferences.getString("_native_device_MAC+${device.id}", null)
    }

    fun setLastSyncForDevice(device: IncasaBleDevice, date: Date?) {
        val editor = sharedPreferences.edit()
        if (date != null) {
            editor.putLong("_last_sync+${device.id}", date.time)
        } else {
            editor.putLong("_last_sync+${device.id}", 0L)
        }
        editor.apply()
    }

    fun getLastSyncForDevice(device: IncasaBleDevice): Date? {
        val timestamp = sharedPreferences.getLong("_last_sync+${device.id}", 0)
        if (timestamp == 0L) {
            return null
        }
        return Date(timestamp)
    }
}