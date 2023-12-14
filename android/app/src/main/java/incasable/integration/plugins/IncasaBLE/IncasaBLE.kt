package incasable.integration.plugins.IncasaBLE

import androidx.appcompat.app.AppCompatActivity

class IncasaBLE(private val activity: AppCompatActivity, var permissionsVerified: Boolean = true, var manager: IncasaBleManager? = null) {
    init {
        manager = IncasaBleManager.getInstance(activity.applicationContext)
    }
}