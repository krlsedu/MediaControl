package com.krlsedu.cths.mediacontrol;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

public class Principal extends Activity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if(Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(Principal.this)) {
                intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }else{
                Thread tr = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                        intent = new Intent(Principal.this, ControleFutuante.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                        startService(intent);
                        }
                    }
                );
                tr.start();

                //finish();
            }
        }else{
            Thread tr = new Thread(
            new Runnable() {
                @Override
                public void run() {
                    intent = new Intent(Principal.this, ControleFutuante.class);
                    startService(intent);
                }
            });
            tr.start();
        }
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= 23){
            if (Settings.canDrawOverlays(Principal.this)) {
               // Intent intent = new Intent(Principal.this, ControleFutuante.class);
                //startService(intent);
                //finish();
            }else{
                Toast.makeText(this,"Para usar você deve permitir Sobrepor a outros Aplicativos!!!",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
