package com.tuonome.datamobile;

import android.app.AppOpsManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!hasUsagePermission()) {
            new AlertDialog.Builder(this)
                .setTitle("Permesso necessario")
                .setMessage("Per mostrare i GB consumati, devi attivare l'accesso all'utilizzo dati.\n\n" +
							"Nella pagina che si apre, cerca \"Dati Mobile\" e attiva l'interruttore.")
                .setCancelable(false)
                .setPositiveButton("Apri impostazioni", (d, w) ->
			startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)))
			.setNegativeButton("Esci", (d, w) -> finish())
			.show();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasUsagePermission()) {
            finish();
        }
    }

    private boolean hasUsagePermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(
			AppOpsManager.OPSTR_GET_USAGE_STATS,
			Process.myUid(),
			getPackageName()
        );
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
