package com.extropies.testapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.extropies.bluetoothlib.BlueToothUtility;
import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public static final int TOOL_TYPE_DEMO = 0;
    public static final int TOOL_TYPE_PRODUCT_TEST = 1;
    public static final int TOOL_TYPE_IO_TEST = 2;
    public static final int TOOL_TYPE_CURRENT = TOOL_TYPE_DEMO/*TOOL_TYPE_PRODUCT_TEST*/;

    private Button m_btnEnum;
    private EditText m_editFilter;
    private ProgressBar m_progBar;

    private static boolean m_bEnumStarted;
    private static boolean m_bConnectStarted;

    private DevListAdapter m_devListAdapter;
    private ArrayList<String> m_devNameList;
    private ProgressBar m_devListProgBar;
    private ImageView m_devListImage;
    private String m_devListCurrentDevName;

    private static final int DEV_TSET_ACTIVITY = 0;

    private static final String FILTER_FILE_NAME = "filterString.txt";

    public static class ConnectTestDialog extends DialogFragment {
        private EditText m_editResult = null;
        private EditText m_editLog = null;
        private Button m_btnCancel = null;

        private static String m_strDeviceName;
        private static String m_devHandle;
        private static int m_logIndex;
        private static byte m_channelID;

        private static int m_totalLoopTime;
        private static int m_curLoopTime;
        private static int m_errorCount;

        private static boolean m_bCancelTest;
        private static ConnectTestDialog m_curDlg;

        private static final int ENUM_TIME = 20000;
        private static final int DISCONNECT_FINAL_TIME = 3000;

        class CommonResponse {
            private int m_returnValue;
            CommonResponse(int returnValue) {
                m_returnValue = returnValue;
            }
            int getM_returnValue() {
                return m_returnValue;
            }
        }
        class CommonObserver implements Observer<CommonResponse> {
            private String m_strProcName;
            CommonResponse m_response;

            CommonObserver(String strProcName) {
                m_strProcName = strProcName;
                m_response = null;
            }

            @Override
            public void onSubscribe(Disposable d) {
                //show start
                Editable resEdit = m_editLog.getText();
                m_logIndex = (m_logIndex + 1) % 1000000;
                resEdit.append("\n=====================");
                resEdit.append("\n");
                resEdit.append("[" + m_logIndex + "]");
                resEdit.append(m_strProcName + " Start");
            }

            @Override
            public void onNext(CommonResponse commonResponse) {
                //show update
                m_response = commonResponse;

                Editable resEdit = m_editLog.getText();
                m_logIndex = (m_logIndex + 1) % 1000000;
                resEdit.append("\n");
                resEdit.append("[" + m_logIndex + "]");
                resEdit.append(BlueToothUtility.getErrorString(m_response.getM_returnValue()));
            }

            @Override
            public void onError(Throwable e) {
                Editable resEdit = m_editLog.getText();
                m_logIndex = (m_logIndex + 1) % 1000000;
                resEdit.append("\n");
                resEdit.append("[" + m_logIndex + "]");
                resEdit.append("Return Value: " + BlueToothUtility.getErrorString(m_response.getM_returnValue()));

                m_curLoopTime++;
                m_errorCount++;
                showResultInfo();
            }

            @Override
            public void onComplete() {
                //show final
                Editable resEdit = m_editLog.getText();
                m_logIndex = (m_logIndex + 1) % 1000000;
                resEdit.append("\n");
                resEdit.append("[" + m_logIndex + "]");
                resEdit.append("Return Value: " + BlueToothUtility.getErrorString(m_response.getM_returnValue()));
            }
        }

        private Observable<CommonResponse> m_getDeviceListObe = Observable.create(new ObservableOnSubscribe<CommonResponse>() {
            @Override
            public void subscribe(final ObservableEmitter<CommonResponse> emitter) throws Exception {
                BlueToothUtility.getInstance().initUtility(getContext());
                BlueToothUtility.getInstance().setScanTimeout(ENUM_TIME);
                int iRtn = BlueToothUtility.getInstance().enumDevice(m_strDeviceName, new CommonUtility.enumCallback() {
                    @Override
                    public void discoverDevice(String[] strDeviceNames) {
                        for (String strDeviceName: strDeviceNames) {
                            if (strDeviceName.equals(m_strDeviceName)) {
                                emitter.onNext(new CommonResponse(BlueToothUtility.BT_RET_SUCCESS));
                            }
                        }
                    }
                });

                emitter.onNext(new CommonResponse(iRtn));
                if (iRtn == BlueToothUtility.BT_RET_SUCCESS) {
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception());
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread(), true);
        private CommonObserver m_getDeviceListOb = new CommonObserver("Enum Device") {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                m_devHandle = "";

                showResultInfo();

                if (m_bCancelTest) {
                    m_bConnectStarted = false;
                    m_curDlg.dismiss();
                } else {
                    m_connectObe.subscribe(m_connectOb);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                m_devHandle = "";

                showResultInfo();

                if (m_bCancelTest) {
                    m_bConnectStarted = false;
                    m_curDlg.dismiss();
                } else {
                    m_connectObe.subscribe(m_connectOb);
                }
            }
        };

        class ConnectResponse extends CommonResponse{
            private String m_devHandle;
            ConnectResponse(String devHandle, int returnValue) {
                super(returnValue);
                m_devHandle = devHandle;
            }
            String getM_devHandle() {
                return m_devHandle;
            }
        }

        private Observable<ConnectResponse> m_connectObe = Observable.create(new ObservableOnSubscribe<ConnectResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ConnectResponse> emitter) throws Exception {
                int iRtn = BlueToothUtility.getInstance().connectDevice(m_strDeviceName, new CommonUtility.heartBeatCallback() {
                    @Override
                    public void pushHeartBeatData(byte[] heartBeatData) {
                    }
                    @Override
                    public void pushConnectState(boolean connected) {
                    }
                });
                emitter.onNext(new ConnectResponse(m_strDeviceName, iRtn));
                if (iRtn == BlueToothUtility.BT_RET_SUCCESS) {
                    m_channelID = (byte)0xD0;
                    emitter.onComplete();
                } else {
                    emitter.onError(new Exception());
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread(), true);
        private CommonObserver m_connectOb = new CommonObserver("Connect Device") {
            @Override
            public void onComplete() {
                m_devHandle = ((ConnectResponse)m_response).getM_devHandle();

                //start test
                m_testDeviceObe.subscribe(m_testDeviceOb);
            }
        };

        class ReceiveResponse extends CommonResponse {
            private byte[] m_cmdResp;
            ReceiveResponse(byte[] cmdResp, int returnValue) {
                super(returnValue);
                m_cmdResp = cmdResp;
            }
            byte[] getM_cmdResp() {
                return m_cmdResp;
            }
        }
        private Observable<ReceiveResponse> m_testDeviceObe = Observable.create(new ObservableOnSubscribe<ReceiveResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ReceiveResponse> emitter) throws Exception {
                byte[] cmd_resp = null;
                //test
                byte[] cmd_getdevinfo = {(byte)0x80, (byte)0x64, (byte)0x00, (byte)0x00, (byte)0x00};
                int iRtn = BlueToothUtility.getInstance().sendAndRecvCommand(m_devHandle, cmd_getdevinfo, m_channelID);
                if (iRtn == BlueToothUtility.BT_RET_SUCCESS) {
                    do {
                        cmd_resp = BlueToothUtility.getInstance().getRecvData(m_devHandle);
                        if (cmd_resp == null) {
                            iRtn = BlueToothUtility.BT_RET_RECV_CMD_DATA_INVALID;
                            break;
                        }
                        if ((cmd_resp.length == 1) && (cmd_resp[0] == (byte)0xF3)) {
                            continue;
                        }
                        if (cmd_resp.length != 43) {
                            iRtn = BlueToothUtility.BT_RET_RECV_CMD_DATA_INVALID;
                            break;
                        }
                        break;
                    } while(true);
                }
                emitter.onNext(new ReceiveResponse(cmd_resp, iRtn));
                if (iRtn != BlueToothUtility.BT_RET_SUCCESS) {
                    emitter.onError(new Exception());
                } else {
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread(), true);
        private CommonObserver m_testDeviceOb = new CommonObserver("Get Device Info") {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                //start disconnect (observable)
                m_disconnectObe.subscribe(m_disconnectOb);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                //start disconnect (observable)
                m_disconnectObe.subscribe(m_disconnectOb);

                m_curLoopTime++;
                showResultInfo();
            }
        };

        void showResultInfo() {
            m_editResult.setText("Progress: " + m_curLoopTime + "/" + m_totalLoopTime + " , Errors: " + m_errorCount);
        }

        private Observable<CommonResponse> m_disconnectObe = Observable.create(new ObservableOnSubscribe<CommonResponse>() {
            @Override
            public void subscribe(ObservableEmitter<CommonResponse> emitter) throws Exception {
                int iRtn = BlueToothUtility.getInstance().disconnectDevice(m_devHandle);
                emitter.onNext(new CommonResponse(iRtn));
                if (iRtn != BlueToothUtility.BT_RET_SUCCESS) {
                    emitter.onError(new Exception());
                } else {
                    Thread.sleep(DISCONNECT_FINAL_TIME);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread(), true);
        private CommonObserver m_disconnectOb = new CommonObserver("Disconnect Device") {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                m_devHandle = "";

                showResultInfo();

                if (m_bCancelTest) {
                    m_bConnectStarted = false;
                    m_curDlg.dismiss();
                } else {
                    if (m_curLoopTime < m_totalLoopTime) {
                        m_getDeviceListObe.subscribe(m_getDeviceListOb);
                    } else {
                        m_bConnectStarted = false;
                    }
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                m_devHandle = "";

                showResultInfo();

                if (m_bCancelTest) {
                    m_bConnectStarted = false;
                    m_curDlg.dismiss();
                } else {
                    if (m_curLoopTime < m_totalLoopTime) {
                        m_getDeviceListObe.subscribe(m_getDeviceListOb);
                    } else {
                        m_bConnectStarted = false;
                    }
                }
            }
        };

        public ConnectTestDialog() {
            m_strDeviceName = "";
            m_devHandle = "";
        }

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);

            m_strDeviceName = args.getString("deviceName");
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            m_logIndex = 0;
            m_totalLoopTime = 100;
            m_curLoopTime = 0;
            m_errorCount = 0;
            m_bCancelTest = false;
            m_curDlg = this;

            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            Dialog dlg = getDialog();
            if (dlg != null) {
                dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

                DisplayMetrics dm = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                WindowManager.LayoutParams attributes = dlg.getWindow().getAttributes();
                attributes.gravity = Gravity.TOP;//对齐方式
                attributes.y = 100;
                dlg.getWindow().setAttributes(attributes);
                dlg.getWindow().setLayout((int) (dm.widthPixels * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
            }


            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            View view = inflater.inflate(R.layout.dlg_io_test, container);

            m_editResult = view.findViewById(R.id.edit_result);

            m_editLog = view.findViewById(R.id.edit_log);

            m_btnCancel = view.findViewById(R.id.btn_cancel);
            m_btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (m_bConnectStarted) {
                        m_bCancelTest = true;
                    } else {
                        m_curDlg.dismiss();
                    }
                }
            });

            return view;
        }

        @Override
        public void onStart() {
            super.onStart();

            this.getDialog().setCancelable(false);
            this.getDialog().setCanceledOnTouchOutside(false);

            if (!m_bConnectStarted ) {
                m_bConnectStarted = true;
                showResultInfo();
                m_getDeviceListObe.subscribe(m_getDeviceListOb);
            }
        }
    }

    class DevListAdapter extends BaseAdapter {
        private ArrayList<String> m_devNameList;
        private int m_itemResId;
        private Context m_context;

        DevListAdapter(Context context, int itemResId, ArrayList<String> devNameList) {
            m_context = context;
            m_itemResId = itemResId;
            m_devNameList = devNameList;
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

            View.OnClickListener clickListener = null;

            if (TOOL_TYPE_CURRENT == TOOL_TYPE_IO_TEST) {
                clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!m_bConnectStarted && !m_bEnumStarted) {
                            ConnectTestDialog dlg = new ConnectTestDialog();
                            Bundle args = new Bundle();
                            args.putString("deviceName", strDeviceName);
                            dlg.setArguments(args);
                            dlg.show(getFragmentManager(), "");
                        } else {
                            Toast toast = Toast.makeText(m_context, "Test Still Running", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                };
            } else {
                clickListener = new View.OnClickListener() {
                    class ConnectResponse {
                        private long m_devHandle;
                        private int m_returnValue;
                        ConnectResponse(long devHandle, int returnValue) {
                            m_devHandle = devHandle;
                            m_returnValue = returnValue;
                        }
                        long getM_devHandle() {
                            return m_devHandle;
                        }
                        int getM_returnValue() {
                            return m_returnValue;
                        }
                    }

                    Observable<ConnectResponse> m_connectObe = Observable.create(new ObservableOnSubscribe<ConnectResponse>() {
                        @Override
                        public void subscribe(ObservableEmitter<ConnectResponse> emitter) throws Exception {
                            long[] contextHandles = new long[1];
                            int iRtn = MiddlewareInterface.initContextWithDevName(getApplicationContext(), m_devListCurrentDevName, new CommonUtility.heartBeatCallback() {
                                @Override
                                public void pushHeartBeatData(byte[] heartBeatData) {

                                }

                                @Override
                                public void pushConnectState(boolean connected) {

                                }
                            }, contextHandles);
                            emitter.onNext(new ConnectResponse(contextHandles[0], iRtn));
                            emitter.onComplete();
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

                    Observer<ConnectResponse> m_connectOb = new Observer<ConnectResponse>() {
                        private long m_contextHandle = 0;
                        private int m_retValue = MiddlewareInterface.PAEW_RET_UNKNOWN_FAIL;

                        @Override
                        public void onSubscribe(Disposable d) {
                            if (m_devListProgBar != null) {
                                m_devListProgBar.setVisibility(View.VISIBLE);
                            }
                            if (m_devListImage != null) {
                                m_devListImage.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onNext(ConnectResponse connectResponse) {
                            if (connectResponse != null) {
                                m_retValue = connectResponse.getM_returnValue();
                                m_contextHandle = connectResponse.getM_devHandle();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onComplete() {
                            if (m_devListProgBar != null) {
                                m_devListProgBar.setVisibility(View.GONE);
                                m_devListProgBar = null;
                            }
                            if (m_devListImage != null) {
                                m_devListImage.setVisibility(View.VISIBLE);
                                m_devListImage = null;
                            }
                            m_bConnectStarted = false;

                            if (m_retValue == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                Intent intent = new Intent(MainActivity.this, DevTestActivity.class);
                                intent.putExtra("contextHandle", m_contextHandle);
                                intent.putExtra("devIndex", 0);
                                startActivityForResult(intent, DEV_TSET_ACTIVITY);
                            }
                        }
                    };

                    @Override
                    public void onClick(View v) {
                        if (!m_bConnectStarted) {
                            m_bConnectStarted = true;

                            m_devListProgBar = progBar;
                            m_devListImage = imgView;
                            m_devListCurrentDevName = strDeviceName;

                            m_connectObe.subscribe(m_connectOb);
                        } else {
                            Toast toast = Toast.makeText(m_context, "Test Still Running", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                };
            }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Intent recvData = data;
        if (requestCode == DEV_TSET_ACTIVITY) {
            if (data != null) {
                m_bConnectStarted = true;
                Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        long contextHandle = recvData.getLongExtra("contextHandle", 0);
                        MiddlewareInterface.freeContext(contextHandle);
                        try {
                            Thread.sleep(3000);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        m_bConnectStarted = false;
                    }
                });
            }

            m_devListCurrentDevName = null;
        }
    }

    void initListeners() {
        m_btnEnum.setOnClickListener(new View.OnClickListener() {
            Observable<String[]> m_getDeviceListObe = Observable.create(new ObservableOnSubscribe<String[]>() {
                @Override
                public void subscribe(final ObservableEmitter<String[]> emitter) throws Exception {
                    int[] devCount = new int[1];
                    devCount[0] = MiddlewareInterface.PAEW_MAX_DEV_COUNT;
                    String[] strDeviceNames = new String[MiddlewareInterface.PAEW_MAX_DEV_COUNT];
                    MiddlewareInterface.getDeviceList(getApplicationContext(), m_editFilter.getText().toString(), 20000, new CommonUtility.enumCallback() {
                        @Override
                        public void discoverDevice(String[] strDeviceNames) {
                            emitter.onNext(strDeviceNames);
                        }
                    }, strDeviceNames, devCount);

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

            Observer<String[]> m_getDeviceListOb = new Observer<String[]>() {
                @Override
                public void onSubscribe(Disposable d) {
                    //clear device list
                    m_devNameList.clear();
                    m_devListAdapter.notifyDataSetChanged();
                    //disable button
                    m_btnEnum.setEnabled(false);
                    m_progBar.setVisibility(View.VISIBLE);
                    //set filter
                    setFilterString(m_editFilter.getText().toString());
                }

                @Override
                public void onNext(String[] strings) {
                    if (strings != null) {
                        for(int i = 0; i < strings.length; i++) {
                            m_devNameList.add(strings[i]);
                        }
                        m_devListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                    m_btnEnum.setEnabled(true);
                    m_progBar.setVisibility(View.GONE);
                    m_bEnumStarted = false;
                }
            };

            @Override
            public void onClick(View v) {
                if (!m_bEnumStarted) {
                    //set enum start
                    m_bEnumStarted = true;
                    m_getDeviceListObe.subscribe(m_getDeviceListOb);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Test Still Running", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    void initListenersForIoTest() {
        m_btnEnum.setOnClickListener(new View.OnClickListener() {
            Observable<String[]> m_getDeviceListObe = Observable.create(new ObservableOnSubscribe<String[]>() {
                @Override
                public void subscribe(final ObservableEmitter<String[]> emitter) throws Exception {
                    BlueToothUtility.getInstance().initUtility(getApplicationContext());
                    BlueToothUtility.getInstance().enumDevice(m_editFilter.getText().toString(), new CommonUtility.enumCallback() {
                        @Override
                        public void discoverDevice(String[] strDeviceNames) {
                            emitter.onNext(strDeviceNames);
                        }
                    });

                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

            Observer<String[]> m_getDeviceListOb = new Observer<String[]>() {
                @Override
                public void onSubscribe(Disposable d) {
                    //clear device list
                    m_devNameList.clear();
                    m_devListAdapter.notifyDataSetChanged();
                    //disable button
                    m_btnEnum.setEnabled(false);
                    m_progBar.setVisibility(View.VISIBLE);
                    //set filter
                    setFilterString(m_editFilter.getText().toString());
                }

                @Override
                public void onNext(String[] strings) {
                    if (strings != null) {
                        for(int i = 0; i < strings.length; i++) {
                            m_devNameList.add(strings[i]);
                        }
                        m_devListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                    m_btnEnum.setEnabled(true);
                    m_progBar.setVisibility(View.GONE);
                    m_bEnumStarted = false;
                }
            };

            @Override
            public void onClick(View v) {
                if (!m_bEnumStarted) {
                    //set enum start
                    m_bEnumStarted = true;
                    m_getDeviceListObe.subscribe(m_getDeviceListOb);
                } else {
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
                int dataLen;// 一次读取1024字节大小，没有数据后返回-1.

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

    private void setFilterString(String strFilter) {
        try {
            String strFilterFileName = this.getFilesDir().getAbsolutePath() + FILTER_FILE_NAME;
            File filterFile = new File(strFilterFileName);

            if (!filterFile.exists()) {
                if (!filterFile.createNewFile()) {
                    throw new FileNotFoundException();
                }
            }

            FileOutputStream outStream = new FileOutputStream(filterFile);
            outStream.write(strFilter.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //request
    public static int BLUETOOTH_PERMISSIONS_REQUEST = 0;
    private static void requestPermissions(Activity activity) {
        if (activity == null) {
            return;
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //request permissions
        requestPermissions(this);

        m_btnEnum = findViewById(R.id.btn_start_scan);
        m_editFilter = findViewById(R.id.text_filter);
        m_progBar = findViewById(R.id.enum_prog);
        m_bEnumStarted = false;
        m_bConnectStarted = false;
        m_devListProgBar = null;
        m_devListImage = null;

        m_progBar.setVisibility(View.GONE);

        if (MainActivity.TOOL_TYPE_CURRENT == MainActivity.TOOL_TYPE_PRODUCT_TEST) {
            if (getFilterString().equals("")) {
                setFilterString("WOOKONG BIO");
            }
        }
        m_editFilter.getText().append(getFilterString());

        m_devNameList = new ArrayList<>(0);
        m_devListAdapter = new DevListAdapter(this, R.layout.devlist_btn_item, m_devNameList);
        ListView devListView = findViewById(R.id.list_devname);
        if (devListView != null) {
            devListView.setAdapter(m_devListAdapter);
        }
        if (TOOL_TYPE_CURRENT == TOOL_TYPE_IO_TEST) {
            initListenersForIoTest();
        } else {
            initListeners();
        }
    }
}
