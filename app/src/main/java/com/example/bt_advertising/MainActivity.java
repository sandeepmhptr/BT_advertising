package com.example.bt_advertising;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = "MySuperClass";
    static final int REQUEST = 112;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) this, PERMISSIONS, REQUEST );
                //imagePath.setText("SDK>23,has no permission");
                Toast.makeText(this, "SDK greater than 23,has no permission", Toast.LENGTH_LONG).show();
                example2();

            } else {
                //do here
                //imagePath.setText("SDK>23,has no permission");
                Toast.makeText(this, "SDK lesser than 23,has no permission", Toast.LENGTH_LONG).show();
                example2();

            }
        } else {
            //do here
            //imagePath.setText("SDK<23");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(this, "The app was  allowed to read your store.", Toast.LENGTH_LONG).show();
                example2();
            }

        }
    }
    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                    Toast.makeText(this, "The app was  allowed to read your store.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "The app was not allowed to read your store.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void example2() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothLeAdvertiser advertiser =
                BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();

        // Check if all features are supported
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!adapter.isLe2MPhySupported()) {
                Log.e(LOG_TAG, "2M PHY not supported!");
                return;
            }


            if (!adapter.isLeExtendedAdvertisingSupported()) {
                Log.e(LOG_TAG, "LE Extended Advertising not supported!");
                return;
            }



            int maxDataLength = adapter.getLeMaximumAdvertisingDataLength();


        AdvertisingSetParameters.Builder parameters = null;

            parameters = (new AdvertisingSetParameters.Builder())
                    .setLegacyMode(false)
                    .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
                    .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
                    .setPrimaryPhy(BluetoothDevice.PHY_LE_1M)
                    .setSecondaryPhy(BluetoothDevice.PHY_LE_2M);


        AdvertiseData data = (new AdvertiseData.Builder()).addServiceData(new
                        ParcelUuid(UUID.randomUUID()),
                "You should be able to fit large amounts of data up to maxDataLength. This goes up to 1650 bytes. For legacy advertising this would not work".getBytes()).build();
        final AdvertisingSet[] currentAdvertisingSet = new AdvertisingSet[1];
        AdvertisingSetCallback callback = null;

            callback = new AdvertisingSetCallback() {
                @Override
                public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                    Log.i(LOG_TAG, "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
                            + status);
                    currentAdvertisingSet[0] = advertisingSet;
                }

                @Override
                public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                    Log.i(LOG_TAG, "onAdvertisingSetStopped():");
                }
            };



            advertiser.startAdvertisingSet(parameters.build(), data, null, null, null, callback);


        // After the set starts, you can modify the data and parameters of currentAdvertisingSet.
       
            currentAdvertisingSet[0].setAdvertisingData((new
                    AdvertiseData.Builder()).addServiceData(new ParcelUuid(UUID.randomUUID()),
                    "Without disabling the advertiser first, you can set the data, if new data is less than 251 bytes long.".getBytes()).build());


        // Wait for onAdvertisingDataSet callback...

        // Can also stop and restart the advertising
        currentAdvertisingSet[0].enableAdvertising(false, 0, 0);
        // Wait for onAdvertisingEnabled callback...
        currentAdvertisingSet[0].enableAdvertising(true, 0, 0);
        // Wait for onAdvertisingEnabled callback...

        // Or modify the parameters - i.e. lower the tx power
        currentAdvertisingSet[0].enableAdvertising(false, 0, 0);
        // Wait for onAdvertisingEnabled callback...
        currentAdvertisingSet[0].setAdvertisingParameters(parameters.setTxPowerLevel
                (AdvertisingSetParameters.TX_POWER_LOW).build());
        // Wait for onAdvertisingParametersUpdated callback...
        currentAdvertisingSet[0].enableAdvertising(true, 0, 0);
        // Wait for onAdvertisingEnabled callback...

        // When done with the advertising:

            advertiser.stopAdvertisingSet(callback);
        }
    }
}