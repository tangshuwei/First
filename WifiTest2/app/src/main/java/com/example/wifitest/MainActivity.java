package com.example.wifitest;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "WifiConnectTest";
    private WifiManager mWifiManager;
    private Handler mMainHandler;
    private boolean mHasPermission;
    private EditText nameEdit, pwdEdit;
    private Button connectBt;
    private WifiBroadcastReceiver wifiReceiver;
    private static int flag = 0;
//    private static final int WIFICIPHER_NOPASS = 0;
//    private static final int WIFICIPHER_WEP = 1;
//    private static final int WIFICIPHER_WPA = 2;
//    private static final int WIFICIPHER_PSK = 3;
//    private static final int WIFICIPHER_EAP = 4;

//    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
//    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    private LocationManager lm;
    private int type = 0;

//    static final String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION,
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        initView();

        //动态获取定位权限
        mHasPermission = checkPermission();
        if (!mHasPermission) {
            requestPermission();
        }
    }

    private void initView() {
        nameEdit = findViewById(R.id.name);
        pwdEdit = findViewById(R.id.password);
        connectBt = findViewById(R.id.connectBt);
        configChildViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册广播监听wifi状态
        wifiReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);//监听wifi是开关变化的状态
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);//监听wifiwifi连接状态广播
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);//监听wifi列表变化（开启一个热点或者关闭一个热点）
        registerReceiver(wifiReceiver, filter);

    }

    private void configChildViews() {
        connectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(true);
                }
                String ssid = nameEdit.getText().toString();
                String pwd = pwdEdit.getText().toString();
//                int netId = mWifiManager.addNetwork(createWifiConfig(ssid, pwd,type));
//                int i = 1;
//                while (flag!=2) {
//                    if (i == 1) {
//                        int netId = mWifiManager.addNetwork(createConfiguration(ssid, pwd, "wpa"));
//                        boolean enable = mWifiManager.enableNetwork(netId, true);
//                        Log.d("ZJTest", "enable: " + enable);
////                        if (flag==2) {   Log.d("flag", "i=1"+flag);break;}
//                    } else if (i == 2) {
//                        int netId = mWifiManager.addNetwork(createConfiguration(ssid, pwd, "wep"));
//                        boolean enable = mWifiManager.enableNetwork(netId, true);
//                        Log.d("ZJTest", "enable: " + enable);
////                        if (flag==2) {Log.d("flag", "i=2"+flag);break;}
//                    } else if (i == 3) {
//                        int netId = mWifiManager.addNetwork(createConfiguration(ssid, pwd, "ap"));
//                        boolean enable = mWifiManager.enableNetwork(netId, true);
//                        Log.d("ZJTest", "enable: " + enable);
////                        if (flag==2) {Log.d("flag", "i=3"+flag);break;}
//                    }
//                    i++;
//                }
                int netId = mWifiManager.addNetwork(createConfiguration(ssid, pwd));
                boolean enable = mWifiManager.enableNetwork(netId, true);
                Log.d("ZJTest", "enable: " + enable);
                System.out.println("1---:"+flag);

                /*if(flag==1) {
                    netId = mWifiManager.addNetwork(createConfiguration(ssid, pwd, "ap"));
                    enable = mWifiManager.enableNetwork(netId, true);
                    Log.d("ZJTest", "enable: " + enable);
                    System.out.println("2---:"+flag);
                    if(flag==1){
                        netId = mWifiManager.addNetwork(createConfiguration(ssid, pwd, "wpa"));
                        enable = mWifiManager.enableNetwork(netId, true);
                        Log.d("ZJTest", "enable: " + enable);
                        System.out.println("3---:"+flag);
                    }
                }*/
//                netId = mWifiManager.addNetwork(createConfiguration(ssid, pwd, "wpa"));
//                enable = mWifiManager.enableNetwork(netId, true);
//                Log.d("ZJTest", "enable: " + enable);


//        boolean reconnect = mWifiManager.reconnect();
//        Log.d("ZJTest", "reconnect: " + reconnect);

            }
        });
    }

    public WifiConfiguration createConfiguration(String ssid, String psd) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        config.preSharedKey = "\"" + psd + "\"";
//        if (type.contains("wep")) {
//            /**
//             * special handling according to password length is a must for wep
//             */
//            int i = psd.length();
//            if (((i == 10 || (i == 26) || (i == 58))) && (psd.matches("[0-9A-Fa-f]*"))) {
//                config.wepKeys[0] = psd;
//            } else {
//                config.wepKeys[0] = "\"" + psd + "\"";
//            }
//            config.allowedAuthAlgorithms
//                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
//        } else if (type.contains("wpa")) {
//            config.preSharedKey = "\"" + psd + "\"";
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//        } else if (type.contains("ap")) {
//            config.preSharedKey = "\"" + psd + "\"";
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
//        } else {
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);//没加密
//        }
        return config;
    }
//    private WifiConfiguration createWifiConfig(String ssid, String password,int type) {
//        WifiConfiguration config = new WifiConfiguration();
//        config.allowedAuthAlgorithms.clear();
//        config.allowedGroupCiphers.clear();
//        config.allowedKeyManagement.clear();
//        config.allowedPairwiseCiphers.clear();
//        config.allowedProtocols.clear();
//        config.SSID = "\"" + ssid + "\"";
////
////        int type;
////        type=2;
////        Log.i(TAG, "加密方式"+type);
//        WifiConfiguration tempConfig = isExist(ssid);
//        if(tempConfig != null) {
//            mWifiManager.removeNetwork(tempConfig.networkId);
//        }
//        if(type == SECURITY_NONE) {
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        } else if(type == SECURITY_WEP) {
//            config.hiddenSSID = true;
//            config.wepKeys[0]= "\""+password+"\"";
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
//        } else if(type == SECURITY_PSK) {
//            config.preSharedKey = "\""+password+"\"";
//            config.hiddenSSID = true;
//            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//            config.status = WifiConfiguration.Status.ENABLED;
//        }
//        return config;
//    }
//    private int getSecurity(WifiConfiguration config) {
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
//            return WIFICIPHER_PSK;
//        }
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
//            return WIFICIPHER_EAP;
//        }
//        return (config.wepKeys[0] != null) ? WIFICIPHER_WEP : WIFICIPHER_NOPASS;
//    }

    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;

//    private int getSecurity(WifiConfiguration config) {
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
//            return SECURITY_PSK;
//        }
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
//            return SECURITY_EAP;
//        }
//        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
//    }

    private WifiConfiguration isExist(String ssid) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();

        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }

    private static final String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private boolean checkPermission() {
        for (String permission : NEEDED_PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private static final int PERMISSION_REQUEST_CODE = 0;

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                NEEDED_PERMISSIONS, PERMISSION_REQUEST_CODE);
    }

    //监听wifi状态广播接收器
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (state) {
                    /**
                     * WIFI_STATE_DISABLED    WLAN已经关闭
                     * WIFI_STATE_DISABLING   WLAN正在关闭
                     * WIFI_STATE_ENABLED     WLAN已经打开
                     * WIFI_STATE_ENABLING    WLAN正在打开
                     * WIFI_STATE_UNKNOWN     未知
                     */
                    case WifiManager.WIFI_STATE_DISABLED: {
                        Log.i(TAG, "已经关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_DISABLING: {
                        Log.i(TAG, "正在关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLED: {
                        Log.i(TAG, "已经打开");
//                        sortScaResult();
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLING: {
                        Log.i(TAG, "正在打开");
                        break;
                    }
                    case WifiManager.WIFI_STATE_UNKNOWN: {
                        Log.i(TAG, "未知状态");
                        break;
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Log.i(TAG, "--NetworkInfo--" + info.toString());
                if (NetworkInfo.State.DISCONNECTED == info.getState()) {//wifi没连接上
                    Log.i(TAG, "wifi没连接上");
                    flag = 1;
//                    configChildViews(flag);

//                    tryReconnect();
                } else if (NetworkInfo.State.CONNECTED == info.getState()) {//wifi连接上了
                    Log.i(TAG, "wifi连接上了");
                    flag = 2;
//                    configChildViews(flag);
                } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                    Log.i(TAG, "wifi正在连接");
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                Log.i(TAG, "网络列表变化了");
            }

        }
    }

//    private void tryReconnect(){
//        if(type==0){
//            type=1;
//        }else if (type==1){
//            type=2;
//        }else type=0;
//        myHandler.sendEmptyMessageDelayed(33, 2000);
//        configChildViews(type);
//    }
//
//    @SuppressLint("HandlerLeak")
//    private Handler myHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 33)//循环3次设置不同的WIFI加密方式
//            {
//                setupWifi();
//            }
//        }
//    };
}