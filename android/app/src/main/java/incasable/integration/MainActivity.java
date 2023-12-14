package incasable.integration;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;
import incasable.integration.plugins.IncasaBLE.IncasaBLEPlugin;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(IncasaBLEPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
