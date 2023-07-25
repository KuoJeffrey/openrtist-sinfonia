// Copyright 2018 Carnegie Mellon University
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package edu.cmu.cs.openrtist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.content.Context;
import android.hardware.camera2.CameraManager;

import java.util.Map;

import edu.cmu.cs.gabriel.Const;
import edu.cmu.cs.gabriel.serverlist.ServerListFragment;
import edu.cmu.cs.sinfonia.SinfoniaActivity;
import edu.cmu.cs.sinfonia.SinfoniaFragment;
import edu.cmu.cs.sinfonia.SinfoniaService;


public class ServerListActivity extends AppCompatActivity  {
    CameraManager camMan = null;
    public static SinfoniaService sinfoniaService;
    private SharedPreferences mSharedPreferences;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 23;
    private static final String TAG = "OpenRTiST/ServerListActivity";

    private boolean isServiceBound = false;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(TAG, "onServiceConnected");
            SinfoniaService.MyBinder binder = (SinfoniaService.MyBinder) iBinder;
            sinfoniaService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(TAG, "onServiceDisconnected");
            sinfoniaService = null;
            isServiceBound = false;
        }

        @Override
        public void onBindingDied(ComponentName componentName) {
            Log.i(TAG, "onBindingDied");
            isServiceBound = false;
        }

        @Override
        public void onNullBinding(ComponentName componentName) {
            Log.i(TAG, "onNullBinding");
            isServiceBound = false;
        }
    };

    void loadPref(SharedPreferences sharedPreferences, String key) {
        Const.loadPref(sharedPreferences, key);
    }

    //activity menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(this.getString(R.string.about_message, BuildConfig.VERSION_NAME))
                        .setTitle(R.string.about_title);
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("", faceTable);
                this.startActivity(intent);
                return true;
            case R.id.find_cloudlets:
                intent = new Intent(this, SinfoniaActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        
        setContentView(R.layout.activity_serverlist);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        String pkg = getApplicationContext().getPackageName();

        Fragment fragment =  new ServerListFragment(pkg,pkg+ ".GabrielClientActivity");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_serverlist, fragment)
                .commitNow();

        mSharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        Map<String, ?> m = mSharedPreferences.getAll();
        for(Map.Entry<String,?> entry : m.entrySet()){
            Log.d("SharedPreferences",entry.getKey() + ": " +
                    entry.getValue().toString());
            this.loadPref(mSharedPreferences, entry.getKey());

        }
        camMan = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        Intent intent = new Intent(this, SinfoniaService.class)
                .setAction(SinfoniaService.ACTION_BIND);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if (isServiceBound) unbindService(serviceConnection);
        super.onDestroy();
    }

    void requestPermissionHelper(String[] permissions) {
        ActivityCompat.requestPermissions(this,
                permissions,
                MY_PERMISSIONS_REQUEST_CAMERA);
    }

    void requestPermission() {
        String[] permissions = {Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        this.requestPermissionHelper(permissions);
    }

}
