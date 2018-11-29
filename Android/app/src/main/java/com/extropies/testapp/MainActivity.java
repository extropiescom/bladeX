/**
 * More details:
 * https://github.com/extropiescom/bladeX/wiki
 * https://github.com/extropiescom/bladeX/wiki/bladeX-Android-API
 *
 * com.extropies.common.MiddlewareInterface is main interface to interact with the device
 * NOTE: All the method mentioned here should NOT be called in main thread, or bluetooth
 * communication will be blocked.
 *
 * 1. How to connect and disconnect:
 *    - Invoke MiddlewareInterface.getDeviceList() to get device name list, device names
 *    are in format of "device_name####device_address".
 *    - Use MiddlewareInterface.initContextWithDevName() to connect device, with devName
 *    chosen from MiddlewareInterface.getDeviceList() result.
 *    - User MiddlewareInterface.freeContext() or freeContextAndShutDown() to disconnect
 *    and power down the device.
 * 2. How to initialize device:
 *    - PIN initialize: If MiddlewareInterface.getDevInfo() returns device info with ucPINState == MiddlewareInterace.PAEW_DEV_INFO_PIN_UNSET,
 *    this means PIN haven't been set, and you should call MiddlewareInterface.initPIN() to initialize.
 *    - Seed initialize: If MiddlewareInterface.getDevInfo() returns device info with ucLifeCycle == MiddlewareInterace.PAEW_DEV_INFO_LIFECYCLE_PRODUCE,
 *    this means there're no seed inside device, and you should initialize device first (after device initialization, ucLifeCycle
 *    should be MiddlewareInterface.PAEW_DEV_INFO_LIFECYCLE_USER). Invoke MiddlewareInterface.generateSeed_GetMnes() + MiddlewareInterface.generateSeed_CheckMnes()
 *    to generate new seed, or invoke MiddlewareInterface.importMne() to import mnemonics to import seed.
 * 3. How to get EOS address:
 *    1) Invoke MiddlewareInterface.deriveTradeAddress(contextHandle, 0, PAEW_COIN_TYPE_EOS, derivePath), with
 *    derivePath = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000} according to https://github.com/satoshilabs/slips/blob/master/slip-0044.md.
 *    2) Invoke MiddlewareInterface.getTradeAddress(contextHandle, 0, PAEW_COIN_TYPE_EOS, bShowOnScreen, strAddress) to get EOS address.
 * 4. How to sign EOS transaction:
 *    1) Invoke MiddlewareInterface.deriveTradeAddress(contextHandle, 0, PAEW_COIN_TYPE_EOS, derivePath), with
 *    derivePath = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000}; according to https://github.com/satoshilabs/slips/blob/master/slip-0044.md
 *    2) (Optional) Invoke MiddlewareInterface.eos_tx_serialize to serialize json string to binary.
 *    NOTE1: ref_block_prefix field of json object MUST be wrapped by quotation marks ("") if you pass it to MiddlewareInterface.eos_tx_serialize, such as \"2642943355\" in the following.
 *    String jsonTxString = "{\"expiration\":\"2018-05-16T02:49:35\",\"ref_block_num\":4105,\"ref_block_prefix\":\"2642943355\",\"max_net_usage_words\":0,\"max_cpu_usage_ms\":0,\"delay_sec\":0,\"context_free_actions\":[],\"actions\":[{\"account\":\"eosio\",\"name\":\"newaccount\",\"authorization\":[{\"actor\":\"eosio\",\"permission\":\"active\"}],\"data\":\"0000000000ea30550000000000000e3d01000000010003224c02ca019e9c0c969d2c8006b89275abeeb5b05af68f2cf5f497bd6e1aff6d01000000010000000100038d424cbe81564f1e4338d342a4dc2b70d848d8b026d3f783bc7c8e6c3c6733cf01000000\"}],\"transaction_extensions\":[],\"signatures\":[],\"context_free_data\":[]}";
 *    MiddlewareInterface.eos_tx_serialize(jsonTxString, serializeData, serializeDataLen);
 *    NOTE2: serializeData is the binary form of transaction, you should prefix it with 32 bytes of chain_id, and padding with 32 bytes of zeros, then pass it to MiddlewareInterface.EOSSign() to sign.
 *    3) Invoke MiddlewareInterface.EOSSign(contextHandle, 0, signCallback, transaction, signature, sigLen)
 *    test transaction = {(byte)0x74, (byte)0x09, (byte)0x70, (byte)0xd9, (byte)0xff, (byte)0x01, (byte)0xb5, (byte)0x04, (byte)0x63, (byte)0x2f, (byte)0xed, (byte)0xe1, (byte)0xad, (byte)0xc3, (byte)0xdf, (byte)0xe5, (byte)0x59, (byte)0x90, (byte)0x41, (byte)0x5e, (byte)0x4f, (byte)0xde, (byte)0x01, (byte)0xe1, (byte)0xb8, (byte)0xf3, (byte)0x15, (byte)0xf8, (byte)0x13, (byte)0x6f, (byte)0x47, (byte)0x6c, (byte)0x14, (byte)0xc2, (byte)0x67, (byte)0x5b, (byte)0x01, (byte)0x24, (byte)0x5f, (byte)0x70, (byte)0x5d, (byte)0xd7, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0xa6, (byte)0x82, (byte)0x34, (byte)0x03, (byte)0xea, (byte)0x30, (byte)0x55, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x57, (byte)0x2d, (byte)0x3c, (byte)0xcd, (byte)0xcd, (byte)0x01, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xa8, (byte)0xed, (byte)0x32, (byte)0x32, (byte)0x21, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x90, (byte)0x55, (byte)0x8c, (byte)0x86, (byte)0x77, (byte)0x95, (byte)0x4c, (byte)0x3c, (byte)0x10, (byte)0x27, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x45, (byte)0x4f, (byte)0x53, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00}
 *    this transaction is serialized result of a json transaction string, prefixed with chain_id (32 bytes) and tailed with zeros (32 bytes)
 * 5. Sign Callbacks
 *    Sign callbacks are invoked in the following sequence
 *    1) Invoke getAuthResult(), return PAEW_RET_SUCCESS or PAEW_RET_DEV_OP_CANCEL, indicates user chooses OK or Cancel on UI. If returns PAEW_RET_SUCCESS, signature will go on; if returns PAEW_RET_DEV_OP_CANCEL, you should call abort() to end this sign procedure.
 *    2) Invoke getAuthType(), return PAEW_SIGN_AUTH_TYPE_PIN or PAEW_SIGN_AUTH_TYPE_FP.
 *    3) If getAuthType() returns PAEW_SIGN_AUTH_TYPE_PIN, then call getPINResult(). getPINResult() returns PAEW_RET_SUCCESS or PAEW_RET_DEV_OP_CANCEL, indicates user choosesOK or Cancel on UI.If returns PAEW_RET_SUCCESS, signature will go on; if returns PAEW_RET_DEV_OP_CANCEL, you should call abort() to end this sign procedure.
 *    4) If getAuthType() returns PAEW_SIGN_AUTH_TYPE_PIN, and getPINResult() returns PAEW_RET_SUCCESS, then call getPIN() to get PIN from UI.
 *    5) Do signature according to user's option.
 *
 *    pseudo-code of signature method:
 *    if (MiddlewareInterface.PAEW_RET_SUCCESS != getAuthResult()) {
 *        return;
 *    }
 *    nAuthType = getAuthType();
 *    if (Middleware.PAEW_SIGN_AUTH_TYPE_PIN == nAuthType) {
 *        if (MiddlewareInterface.PAEW_RET_SUCCESS != getPINResult()) {
 *            return;
 *        }
 *        strPIN = getPIN();
 *    }
 *    nResult = (do signature with user selected authenticate type (finger print or PIN))
 *    if ((MiddlewareInterface.PAEW_RET_SUCCESS != nResult) && (Middleware.PAEW_SIGN_AUTH_TYPE_PIN != nAuthType)) {
 *        if (MiddlewareInterface.PAEW_RET_SUCCESS != getPINResult()) {
 *            return;
 *        }
 *        strPIN = getPIN();
 *        (do signature with PIN authority)
 *    }
 */
package com.extropies.testapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.extropies.common.MiddlewareInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int TOOL_TYPE_DEMO = 0;
    public static final int TOOL_TYPE_PRODUCT_TEST = 1;
    public static final int TOOL_TYPE_CURRENT = TOOL_TYPE_DEMO;

    private Button m_btnEnum;
    private EditText m_editFilter;
    private ProgressBar m_progBar;

    private Thread m_scanThread;
    private Thread m_testThread;
    private MainHandler m_mainHandler;

    private DevListAdapter m_devListAdapter;
    private ArrayList<String> m_devNameList;
    private ProgressBar m_devListProgBar;
    private ImageView m_devListImage;
    private String m_devListCurrentDevName;

    private static final int DEV_TSET_ACTIVITY = 0;

    private static final String FILTER_FILE_NAME = "filterString.txt";

    class DevListAdapter extends BaseAdapter {
        private ArrayList<String> m_devNameList;
        private int m_itemResId;
        private Context m_context;
        private Thread m_testThread;
        private MainHandler m_mainHandler;

        DevListAdapter(Context context, int itemResId, ArrayList<String> devNameList, MainHandler mainHandler) {
            m_context = context;
            m_itemResId = itemResId;
            m_devNameList = devNameList;
            m_mainHandler = mainHandler;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View destView;

            if (convertView == null) {
                destView = LayoutInflater.from(m_context).inflate(m_itemResId, null);
            } else {
                destView = convertView;
            }

            final String strDeviceName = m_devNameList.get(position);

            final ProgressBar progBar = destView.findViewById(R.id.connect_prog);
            if (!strDeviceName.equals(m_devListCurrentDevName)) {
                progBar.setVisibility(View.GONE);
            }

            final ImageView imgView = destView.findViewById(R.id.image_ble);
            if (strDeviceName.equals(m_devListCurrentDevName)) {
                imgView.setVisibility(View.GONE);
            }

            final TextView devText = destView.findViewById(R.id.text_devlist);
            devText.setText(strDeviceName);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        //connect device
                        m_devListProgBar = progBar;
                        m_devListImage = imgView;
                        m_devListCurrentDevName = strDeviceName;

                        if (TOOL_TYPE_CURRENT == TOOL_TYPE_DEMO) {
                            m_editFilter.setText(strDeviceName);
                            ((MainActivity)m_context).setFilterString(strDeviceName);
                        }

                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setInitContextWithDevNameWrapper((Activity) m_context, strDeviceName);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_context, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            };

            destView.setOnClickListener(clickListener);

            return destView;
        }

        @Override
        public Object getItem(int position) {
            if (m_devNameList == null) {
                return null;
            }
            return m_devNameList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            if (m_devNameList == null) {
                return 0;
            }
            return m_devNameList.size();
        }
    }

    class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case BlueToothWrapper.MSG_ENUM_START:
                    //clear device list
                    m_devNameList.clear();
                    m_devListAdapter.notifyDataSetChanged();
                    //disable button
                    m_btnEnum.setEnabled(false);
                    m_progBar.setVisibility(View.VISIBLE);
                    break;
                case BlueToothWrapper.MSG_ENUM_UPDATE:
                    //add device list
                    String[] devNames = (String[])msg.obj;
                    if (devNames != null) {
                        for(int i = 0; i < devNames.length; i++) {
                            m_devNameList.add(devNames[i]);
                        }
                        m_devListAdapter.notifyDataSetChanged();
                    }
                    break;
                case BlueToothWrapper.MSG_ENUM_FINISH:
                    m_btnEnum.setEnabled(true);
                    m_progBar.setVisibility(View.GONE);
                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_START:
                    if (m_devListProgBar != null) {
                        m_devListProgBar.setVisibility(View.VISIBLE);
                    }
                    if (m_devListImage != null) {
                        m_devListImage.setVisibility(View.GONE);
                    }
                    break;
                case BlueToothWrapper.MSG_INIT_CONTEXT_FINISH:
                    if (m_devListProgBar != null) {
                        m_devListProgBar.setVisibility(View.GONE);
                        m_devListProgBar = null;
                    }
                    if (m_devListImage != null) {
                        m_devListImage.setVisibility(View.VISIBLE);
                        m_devListImage = null;
                    }
                    m_devListCurrentDevName = null;
                    BlueToothWrapper.InitContextReturnValue returnValue = (BlueToothWrapper.InitContextReturnValue)msg.obj;
                    if ((returnValue != null) && (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS)) {
                        //if device connect success, then move to next activity
                        Intent intent = new Intent(MainActivity.this, DevTestActivity.class);
                        intent.putExtra("contextHandle", returnValue.getContextHandle());
                        intent.putExtra("devIndex", 0);
                        startActivityForResult(intent, DEV_TSET_ACTIVITY);
                    }
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        long contextHandle;

        if (requestCode == DEV_TSET_ACTIVITY) {
            if (data != null) {
                contextHandle = data.getLongExtra("contextHandle", 0);
                if (contextHandle != 0) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setFreeContextWrapper(contextHandle);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }
        }
    }

    void initListeners() {
        m_btnEnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilterString(m_editFilter.getText().toString());

                if ((m_scanThread == null) || (m_scanThread.getState() == Thread.State.TERMINATED))
                {
                    //enumerate devices
                    m_scanThread = new BlueToothWrapper(m_mainHandler);
                    ((BlueToothWrapper)m_scanThread).setGetDevListWrapper(/*MainActivity.this*/getApplicationContext(), m_editFilter.getText().toString());
                    m_scanThread.start();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Test Still Running", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    private String getFilterString() {
        String strFilter = "";
        String strFilterFileName = this.getFilesDir().getAbsolutePath() + FILTER_FILE_NAME;
        File filterFile = new File(strFilterFileName);
        if (filterFile.exists()) {
            try{
                byte[] bufferData = new byte[1024];
                int dataLen = 0;// 一次读取1024字节大小，没有数据后返回-1.

                FileInputStream fis = new FileInputStream(filterFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((dataLen = fis.read(bufferData)) != -1) {
                    baos.write(bufferData, 0, dataLen);
                }
                byte[] fileData = baos.toByteArray();
                baos.close();
                fis.close();

                strFilter = new String(fileData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return strFilter;
    }

    private int setFilterString(String strFilter) {
        try {
            String strFilterFileName = this.getFilesDir().getAbsolutePath() + FILTER_FILE_NAME;
            File filterFile = new File(strFilterFileName);

            if (!filterFile.exists()) {
                filterFile.createNewFile();
            }

            FileOutputStream outStream = new FileOutputStream(filterFile);
            outStream.write(strFilter.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //request
    public static int BLUETOOTH_PERMISSIONS_REQUEST = 0;
    private static boolean requestPermissions(Activity activity) {
        if (activity == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= 23) {
            //check location permission
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=  PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BLUETOOTH_PERMISSIONS_REQUEST);
            }
        }

        //check blue tooth permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) !=  PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH}, BLUETOOTH_PERMISSIONS_REQUEST);
        }

        //check blue tooth admin permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) !=  PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, BLUETOOTH_PERMISSIONS_REQUEST);
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //request permissions
        requestPermissions(this);

        m_mainHandler = new MainHandler();

        m_btnEnum = findViewById(R.id.btn_start_scan);
        m_editFilter = findViewById(R.id.text_filter);
        m_progBar = findViewById(R.id.enum_prog);
        m_devListProgBar = null;
        m_devListImage = null;

        m_progBar.setVisibility(View.GONE);

        if (MainActivity.TOOL_TYPE_CURRENT == MainActivity.TOOL_TYPE_PRODUCT_TEST) {
            if (getFilterString() == "") {
                setFilterString("WOOKONG BIO");
            }
        }
        m_editFilter.getText().append(getFilterString());

        m_devNameList = new ArrayList<>(0);
        m_devListAdapter = new DevListAdapter(this, R.layout.devlist_btn_item, m_devNameList, m_mainHandler);
        ListView devListView = findViewById(R.id.list_devname);
        if (devListView != null) {
            devListView.setAdapter(m_devListAdapter);
        }

        m_scanThread = null;

        initListeners();

        return;
    }
}
