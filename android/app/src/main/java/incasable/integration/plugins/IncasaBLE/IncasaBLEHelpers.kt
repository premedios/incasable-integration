package incasable.integration.plugins.IncasaBLE

import com.bewellinnovations.incasable.*
import com.bewellinnovations.incasable.models.*

fun IncasaBleDevice.getLabel() = when(this) {
    IncasaBleDevice.AND_UA_651 -> "A&D UA-651"
    IncasaBleDevice.AND_UC_352 -> "A&D UC-352"
    IncasaBleDevice.AND_UT_201 -> "A&D UT-201"
    IncasaBleDevice.NONIN_3230 -> "Nonin 3230"
    IncasaBleDevice.ACTIV8_8015 -> "Activ8 A8015"
    IncasaBleDevice.ACTIV8_8016 -> "Activ8 A8016"
    IncasaBleDevice.WELLUE_DUO -> "Wellue DuoEK"
}

fun IncasaMeasurementType.getDescription(): String = when(this) {
    IncasaMeasurementType.BLOOD_PRESSURE -> "Blood Pressure"
    IncasaMeasurementType.WEIGHT -> "Weight"
    IncasaMeasurementType.OXIMETRY -> "Oximetry"
    IncasaMeasurementType.ACTIVITY -> "Activity"
    IncasaMeasurementType.ECG -> "ECG"
    IncasaMeasurementType.TEMPERATURE -> "Temperature"
}

fun IncasaBleState.getDescription(): String = when(this) {
    IncasaBleState.AlmostDone -> "Almost done"
    IncasaBleState.AwaitingMeasurement -> "Awaiting measurement"
    IncasaBleState.Connecting -> "Connecting device"
    IncasaBleState.Linking -> "Pairing device"
    is IncasaBleState.ReceivingMeasurement -> "Receiving measurement"
    IncasaBleState.Searching -> "Searching device"
}

fun IncasaMeasurement.getDescription(): String = when(this) {
    is IncasaBloodPressureMeasurement -> "- MAP: ${this.bloodPressureMeanArterial} ${this.bloodPressureMeanArterialUnit} \\n- Time: ${this.date}\""
    is IncasaECGMeasurement -> "- ECG samples count: ${this.samples.count()} \n- Time: ${this.date}"
    is IncasaOximetryMeasurement -> "- SpO2: ${this.spo2} ${this.spo2Unit} \n- Time: ${this.date}"
    is IncasaWeightMeasurement -> "- Weight: ${this.weight} ${this.weightUnit} \n- Time: ${this.date}"
    is IncasaTemperatureMeasurement -> "- Temperature: ${this.temperature} ${this.temperatureUnit} \n- Time: ${this.date}"
    is IncasaActiv8015Measurement -> "- Activity frames count: ${this.frameCollections.fold(0) { sum, frameCollection -> sum + frameCollection.frames.count()}}"
    is IncasaActiv8016Measurement -> "- Activity frames count: ${this.frameCollections.fold(0) { sum, frameCollection -> sum + frameCollection.frames.count()}}"
    else -> "Unknown measurement"
}

fun IncasaBleError.getDescription(): String = when(this) {
    is IncasaBleError.BleCommsError -> "Something went wrong, please try again."
    is IncasaBleError.BluetoothDisabled -> "Can't start process, Bluetooth is disabled."
    is IncasaBleError.CustomDeviceError ->  when(this.details) {
        is IncasaBleWellueError -> "Measurement less then 30 seconds."
        is IncasaBleBloodPressureError -> when(this.details) {
            IncasaBleBloodPressureError.BODY_MOVEMENT -> "Body movement during measurement."
            IncasaBleBloodPressureError.CUFF_FIT -> "Wrong cuff fit."
            IncasaBleBloodPressureError.PULSE_RATE_RANGE -> "Wrong pulse rate range"
            IncasaBleBloodPressureError.MEASUREMENT_POSITION -> "Wrong measurement position."
            else -> ""
        }
        else -> "Unknown specific device error"
    }
    is IncasaBleError.DeviceIncompatible -> "This device could not perform BLE operations."
    is IncasaBleError.PermissionsNotGranted -> "Not all permissions are granted for BLE comms."
    is IncasaBleError.WrongMeasurement -> "Unexpected measurement values, please try again."
    is IncasaBleError.UnlinkNotLinkedDevice -> "Can't unlink device that is not currently linked."
}
