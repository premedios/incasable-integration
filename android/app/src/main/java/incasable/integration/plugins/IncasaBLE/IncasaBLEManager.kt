package incasable.integration.plugins.IncasaBLE

import android.content.Context
import com.bewellinnovations.incasable.*
import com.bewellinnovations.incasable.ble.managers.IncasaBleProvider
import com.bewellinnovations.incasable.models.IncasaMeasurement
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class IncasaBleManager private constructor(): IncasaBleListener {
    private val logger: Logger = Logger.getLogger(this.javaClass.name)
    private val _events = MutableSharedFlow<IncasaEvent>()
    val events = _events.asSharedFlow()
    var persistenceManager: CustomIncasaBleDevicePersistenceManager? = null

    companion object {
        private var instance : IncasaBleManager? = null

        fun getInstance(appCtx: Context): IncasaBleManager {
            if (instance == null) {
                val pm = CustomIncasaBleDevicePersistenceManager(appCtx)
                IncasaBleProvider.persistenceManager = pm
                instance = IncasaBleManager()
                instance!!.persistenceManager = pm

                IncasaBleDevice.entries.forEach {
                    val manager = IncasaBleProvider.initialize(appCtx = appCtx, device = it)
                    manager.listener = instance
                    instance?.startDeviceIfRequired(manager)
                }
            }
            return instance!!
        }
    }

    fun start(device: IncasaBleDevice) {
        IncasaBleProvider.getInstance(device = device).start()
    }

    fun stop(device: IncasaBleDevice) {
        IncasaBleProvider.getInstance(device = device).stop()
    }

    fun isPaired(device: IncasaBleDevice): Boolean {
        return IncasaBleProvider.getInstance(device = device).isLinked
    }

    fun unpair(device: IncasaBleDevice) {
        IncasaBleProvider.getInstance(device = device).unlink()
    }

    private fun invokeEvent(event: IncasaEvent) {
        runBlocking {
            launch { _events.emit(event) }
        }
    }

    override fun onBatteryLow(connectionManager: IncasaBleConnectionManager) {
        val event = IncasaEvent.BatteryLow(connectionManager.device)
        logger.log(Level.INFO, event.logDescription())
        invokeEvent(event)
    }

    override fun onError(connectionManager: IncasaBleConnectionManager, error: IncasaBleError) {
        val event = IncasaEvent.Error(connectionManager.device, error)
        logger.log(Level.INFO, event.logDescription())
        invokeEvent(event)
    }

    override fun onMeasurementReceived(
        connectionManager: IncasaBleConnectionManager,
        measurement: IncasaMeasurement
    ) {
        (IncasaBleProvider.persistenceManager as CustomIncasaBleDevicePersistenceManager).setLastSyncForDevice(connectionManager.device, Date())
        val event = IncasaEvent.MeasurementReceived(connectionManager.device, measurement)

        logger.log(Level.INFO, event.logDescription())
        invokeEvent(event)
    }

    override fun onPeripheralDisconnected(connectionManager: IncasaBleConnectionManager) {
        val event = IncasaEvent.DeviceDisconnected(connectionManager.device)
        logger.log(Level.INFO, event.logDescription())
        invokeEvent(event)
        startDeviceIfRequired(connectionManager)
    }

    private fun startDeviceIfRequired(manager: IncasaBleConnectionManager) {
        if (manager.isLinked &&
            !arrayOf(IncasaBleDevice.ACTIV8_8016, IncasaBleDevice.ACTIV8_8015, IncasaBleDevice.WELLUE_DUO).contains(manager.device)) {
            manager.start()
        }
    }

    override fun onPeripheralLinkingChanged(
        connectionManager: IncasaBleConnectionManager,
        linked: Boolean
    ) {
        val event = IncasaEvent.DeviceLinkingChanged(connectionManager.device, linked)
        logger.log(Level.INFO, event.logDescription())

        if (!linked) {
            (IncasaBleProvider.persistenceManager as CustomIncasaBleDevicePersistenceManager).setLastSyncForDevice(connectionManager.device, null)
        }
        invokeEvent(event)
    }

    override fun onStateChanged(
        connectionManager: IncasaBleConnectionManager,
        newState: IncasaBleState
    ) {
        val event = IncasaEvent.StateChanged(connectionManager.device, newState)
        logger.log(Level.INFO, event.logDescription())
        invokeEvent(event)

    }
}

sealed class IncasaEvent(val device: IncasaBleDevice) {
    class MeasurementReceived(device: IncasaBleDevice, val measurement: IncasaMeasurement): IncasaEvent(device)
    class DeviceLinkingChanged(device: IncasaBleDevice, val isLinked: Boolean): IncasaEvent(device)
    class StateChanged(device: IncasaBleDevice, val newState: IncasaBleState): IncasaEvent(device)
    class DeviceDisconnected(device: IncasaBleDevice): IncasaEvent(device)
    class Error(device: IncasaBleDevice, val error: IncasaBleError): IncasaEvent(device)
    class BatteryLow(device: IncasaBleDevice): IncasaEvent(device)

    fun logDescription(): String = when(this) {
        is MeasurementReceived -> "[Event - ${this.device.id}] Measurement received. Measurement: ${this.measurement.getDescription()}"
        is BatteryLow -> "[Event - ${this.device.id}] Battery low"
        is DeviceDisconnected -> "[Event - ${this.device.id}] Disconnected."
        is DeviceLinkingChanged -> "[Event - ${this.device.id}] Linking changed, now: ${this.isLinked}"
        is Error -> "[Event - ${this.device.id}] ERROR: ${this.error}"
        is StateChanged -> "[Event - ${this.device.id}] State: ${this.newState}"
    }
}
