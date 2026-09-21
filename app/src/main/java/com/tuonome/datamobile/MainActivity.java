package com.tuonome.datamobile;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("dati", MODE_PRIVATE);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 100, 50, 50);

        // Permesso
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                getPackageName()
        );

        if (mode != AppOpsManager.MODE_ALLOWED) {
            TextView avviso = new TextView(this);
            avviso.setTextSize(18);
            avviso.setText("Permesso necessario. Attivalo qui sotto.");
            layout.addView(avviso);

            Button btnPermesso = new Button(this);
            btnPermesso.setText("ATTIVA PERMESSO");
            btnPermesso.setOnClickListener(v -> {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            });
            layout.addView(btnPermesso);

        } else {
            TextView ok = new TextView(this);
            ok.setTextSize(16);
            ok.setText("Permesso attivo");
            layout.addView(ok);
        }

        // Giorno ciclo
        TextView titolo = new TextView(this);
        titolo.setTextSize(18);
        titolo.setText("\nGiorno inizio ciclo:");
        layout.addView(titolo);

        EditText campo = new EditText(this);
        campo.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        campo.setText(String.valueOf(prefs.getInt("giorno_ciclo", 1)));
        layout.addView(campo);

        Button salva = new Button(this);
        salva.setText("SALVA");
        salva.setOnClickListener(v -> {
            String testo = campo.getText().toString().trim();
            if (testo.isEmpty()) return;
            int giorno = Integer.parseInt(testo);
            if (giorno < 1 || giorno > 28) {
                Toast.makeText(this, "1-28", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putInt("giorno_ciclo", giorno).apply();
            Toast.makeText(this, "Salvato", Toast.LENGTH_SHORT).show();
        });
        layout.addView(salva);

        setContentView(layout);
    }
}
