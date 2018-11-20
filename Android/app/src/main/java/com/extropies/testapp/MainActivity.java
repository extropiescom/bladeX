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
    public static final int TOOL_TYPE_CURRENT = TOOL_TYPE_DEMO/*TOOL_TYPE_PRODUCT_TEST*/;

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
