package com.extropies.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.extropies.common.CommonUtility;
import com.extropies.common.MiddlewareInterface;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class DevTestActivity extends AppCompatActivity {
    private TabLayout m_tabTests;
    private static final int TAB_DEVICE = 0;
    private static final int TAB_FINGERPRINT = 1;
    private static final int TAB_INIT_WALLET = 2;
    private static final int TAB_WALLET = 3;

    private static ViewPager m_viewPager;
    private ArrayList<Fragment> m_fragList;

    private EditText m_editResult;

    protected static ReentrantLock m_uiLock;
    protected static int m_coinChoiceIndex = 0;
    public static int m_authTypeResult = 0;
    public static int m_authTypeChoiceIndex = 0; //-1 means cancel
    public static byte m_authType;
    public static int m_getPINResult;
    public static String m_getPINString;

    private static final String m_strDefaultPIN = "12345678";
    private static final String m_strDefaultNewPIN = "88888888";
    private static final int m_minPINLen = 6;
    private static final int m_maxPINLen = 16;

    private static MainHandler m_mainHandler;
    private static Thread m_testThread;
    private static Thread m_abortThread;
    private static Thread m_freeContextThread;
    private static long m_contextHandle;
    private static int m_devIndex;
    private static Activity m_curContext;
    private TextView m_textDevState;
    private TextView m_textDevConnect;

    class MainHandler extends Handler {
        int m_lineIndex = 0;
        private String getProcessName(int msgType) {
            String strProcessName = "Unknown";

            switch(msgType) {
                case BlueToothWrapper.MSG_ENROLL_START:
                case BlueToothWrapper.MSG_ENROLL_UPDATE:
                case BlueToothWrapper.MSG_ENROLL_FINISH:
                    strProcessName = "Enroll FP";
                    break;
                case BlueToothWrapper.MSG_GET_FP_LIST_START:
                case BlueToothWrapper.MSG_GET_FP_LIST_FINISH:
                    strProcessName = "Get FP List";
                    break;
                case BlueToothWrapper.MSG_CALIBRATE_FP_START:
                case BlueToothWrapper.MSG_CALIBRATE_FP_FINISH:
                    strProcessName = "Calibrate";
                    break;
                case BlueToothWrapper.MSG_DELETE_FP_START:
                case BlueToothWrapper.MSG_DELETE_FP_FINISH:
                    strProcessName = "Delete FP";
                    break;
                case BlueToothWrapper.MSG_VERIFYFP_START:
                case BlueToothWrapper.MSG_VERIFYFP_UPDATE:
                case BlueToothWrapper.MSG_VERIFYFP_FINISH:
                    strProcessName = "Verify FP";
                    break;
                case BlueToothWrapper.MSG_GET_DEV_INFO_START:
                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:
                    strProcessName = "Get Device Info";
                    break;
                case BlueToothWrapper.MSG_ABORT_FP_START:
                case BlueToothWrapper.MSG_ABORT_FP_FINISH:
                    strProcessName = "Abort";
                    break;
                case BlueToothWrapper.MSG_VERIFY_PIN_START:
                case BlueToothWrapper.MSG_VERIFY_PIN_FINISH:
                    strProcessName = "Verify PIN";
                    break;
                case BlueToothWrapper.MSG_CHANGE_PIN_START:
                case BlueToothWrapper.MSG_CHANGE_PIN_FINISH:
                    strProcessName = "Change PIN";
                    break;
                case BlueToothWrapper.MSG_INIT_PIN_START:
                case BlueToothWrapper.MSG_INIT_PIN_FINISH:
                    strProcessName = "Init PIN";
                    break;
                case BlueToothWrapper.MSG_FORMAT_DEVICE_START:
                case BlueToothWrapper.MSG_FORMAT_DEVICE_FINISH:
                    strProcessName = "Format Device";
                    break;
                case BlueToothWrapper.MSG_GEN_SEED_START:
                case BlueToothWrapper.MSG_GEN_SEED_FINISH:
                    strProcessName = "Generate Seed";
                    break;
                case BlueToothWrapper.MSG_EOS_SIGN_START:
                case BlueToothWrapper.MSG_EOS_SIGN_FINISH:
                    strProcessName = "EOS Sign";
                    break;
                case BlueToothWrapper.MSG_ETH_SIGN_START:
                case BlueToothWrapper.MSG_ETH_SIGN_FINISH:
                    strProcessName = "ETH Sign";
                    break;
                case BlueToothWrapper.MSG_CYB_SIGN_START:
                case BlueToothWrapper.MSG_CYB_SIGN_FINISH:
                    strProcessName = "CYB Sign";
                    break;
                case BlueToothWrapper.MSG_GET_ADDRESS_START:
                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:
                    strProcessName = "Get Address";
                    break;
                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                    strProcessName = "Free Context";
                    break;
                case BlueToothWrapper.MSG_IMPORT_MNE_START:
                case BlueToothWrapper.MSG_IMPORT_MNE_FINISH:
                    strProcessName = "Import Mnemonic";
                    break;
                case BlueToothWrapper.MSG_CLEAR_SCREEN_START:
                case BlueToothWrapper.MSG_CLEAR_SCREEN_FINISH:
                    strProcessName = "Clear Screen";
                    break;
            }

            return strProcessName;
        }

        @Override
        public void handleMessage(Message msg) {
            int i;
            Editable resEdit = m_editResult.getText();
            AlertDialog dlg = null;

            switch(msg.what) {
                case BlueToothWrapper.MSG_GET_AUTH_TYPE:
                    final String[] authTypeString = {"Sign by Finger Print", "Sign by PIN"};
                    final byte[] authTypes = {MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_FP, MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_PIN};
                    m_authTypeChoiceIndex = 0;

                    dlg = new AlertDialog.Builder(DevTestActivity.this)
                            .setIcon(R.mipmap.icon_ble)
                            .setTitle("Please Select Sign Type:")
                            .setSingleChoiceItems(authTypeString, m_authTypeChoiceIndex, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_authTypeChoiceIndex = which;
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_authTypeResult = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;
                                    m_uiLock.unlock();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_authType = authTypes[m_authTypeChoiceIndex];
                                    m_authTypeResult = MiddlewareInterface.PAEW_RET_SUCCESS;

                                    if (m_authType == MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_PIN) {
                                        Message msg = m_mainHandler.obtainMessage();
                                        msg.what = BlueToothWrapper.MSG_GET_USER_PIN;
                                        msg.sendToTarget();
                                    } else {
                                        m_uiLock.unlock();
                                    }
                                }
                            })
                            .setCancelable(false)
                            .create();
                    dlg.show();
                    break;
                case BlueToothWrapper.MSG_GET_USER_PIN:
                    View dlgView = getLayoutInflater().inflate(R.layout.dlg_verify_pin, null);
                    final EditText editPIN = dlgView.findViewById(R.id.edit_pin);
                    editPIN.setText(m_strDefaultPIN);
                    editPIN.selectAll();
                    dlg = new AlertDialog.Builder(DevTestActivity.this)
                            .setIcon(R.mipmap.icon_ble)
                            .setTitle("Please Input PIN:")
                            .setView(dlgView).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    m_getPINResult = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;
                                    m_uiLock.unlock();
                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int PINLength = editPIN.getText().toString().length();
                                    if ((PINLength < m_minPINLen) || (PINLength > m_maxPINLen)) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Please input PIN between " + m_minPINLen + " and " + m_maxPINLen, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        return;
                                    }

                                    m_getPINResult = MiddlewareInterface.PAEW_RET_SUCCESS;
                                    m_getPINString = editPIN.getText().toString();
                                    m_uiLock.unlock();
                                }
                            })
                            .setCancelable(false)
                            .create();
                    dlg.show();
                    break;
                case BlueToothWrapper.MSG_RESET_UI_LOCK:
                    m_uiLock.lock();
                    break;
                case BlueToothWrapper.MSG_ENROLL_START:
                case BlueToothWrapper.MSG_GET_FP_LIST_START:
                case BlueToothWrapper.MSG_CALIBRATE_FP_START:
                case BlueToothWrapper.MSG_DELETE_FP_START:
                case BlueToothWrapper.MSG_VERIFYFP_START:
                case BlueToothWrapper.MSG_GET_DEV_INFO_START:
                case BlueToothWrapper.MSG_ABORT_FP_START:
                case BlueToothWrapper.MSG_VERIFY_PIN_START:
                case BlueToothWrapper.MSG_CHANGE_PIN_START:
                case BlueToothWrapper.MSG_INIT_PIN_START:
                case BlueToothWrapper.MSG_FORMAT_DEVICE_START:
                case BlueToothWrapper.MSG_GEN_SEED_START:
                case BlueToothWrapper.MSG_EOS_SIGN_START:
                case BlueToothWrapper.MSG_ETH_SIGN_START:
                case BlueToothWrapper.MSG_CYB_SIGN_START:
                case BlueToothWrapper.MSG_GET_ADDRESS_START:
                case BlueToothWrapper.MSG_FREE_CONTEXT_START:
                case BlueToothWrapper.MSG_IMPORT_MNE_START:
                case BlueToothWrapper.MSG_CLEAR_SCREEN_START:
                    //resEdit.clear();
                    //m_lineIndex = 0;
                    resEdit.append("\n===============================");
                    resEdit.append("\n");
                    resEdit.append("[" + m_lineIndex + "]");
                    resEdit.append(getProcessName(msg.what) + " Start");
                    break;
                case BlueToothWrapper.MSG_SIGN_UPDATE:
                case BlueToothWrapper.MSG_VERIFYFP_UPDATE:
                case BlueToothWrapper.MSG_ENROLL_UPDATE:
                    m_lineIndex = (m_lineIndex + 1) % 1000000;
                    resEdit.append("\n");
                    resEdit.append("[" + m_lineIndex + "]");
                    resEdit.append(MiddlewareInterface.getReturnString(msg.arg1));
                    break;
                case BlueToothWrapper.MSG_ENROLL_FINISH:
                case BlueToothWrapper.MSG_CALIBRATE_FP_FINISH:
                case BlueToothWrapper.MSG_DELETE_FP_FINISH:
                case BlueToothWrapper.MSG_ABORT_FP_FINISH:
                case BlueToothWrapper.MSG_VERIFY_PIN_FINISH:
                case BlueToothWrapper.MSG_CHANGE_PIN_FINISH:
                case BlueToothWrapper.MSG_INIT_PIN_FINISH:
                case BlueToothWrapper.MSG_FORMAT_DEVICE_FINISH:
                case BlueToothWrapper.MSG_GEN_SEED_FINISH:
                case BlueToothWrapper.MSG_FREE_CONTEXT_FINISH:
                case BlueToothWrapper.MSG_IMPORT_MNE_FINISH:
                case BlueToothWrapper.MSG_GET_FP_LIST_FINISH:
                case BlueToothWrapper.MSG_VERIFYFP_FINISH:
                case BlueToothWrapper.MSG_GET_DEV_INFO_FINISH:
                case BlueToothWrapper.MSG_GET_ADDRESS_FINISH:
                case BlueToothWrapper.MSG_EOS_SIGN_FINISH:
                case BlueToothWrapper.MSG_ETH_SIGN_FINISH:
                case BlueToothWrapper.MSG_CYB_SIGN_FINISH:
                case BlueToothWrapper.MSG_CLEAR_SCREEN_FINISH:
                    m_lineIndex = (m_lineIndex + 1) % 1000000;
                    resEdit.append("\n");
                    resEdit.append("[" + m_lineIndex + "]");
                    if (msg.obj != null) {
                        if (msg.what == BlueToothWrapper.MSG_GET_FP_LIST_FINISH) {
                            BlueToothWrapper.GetFPListReturnValue returnValue = (BlueToothWrapper.GetFPListReturnValue) msg.obj;
                            if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                MiddlewareInterface.FingerPrintID[] fpList = returnValue.getFPList();
                                resEdit.append("\n");
                                resEdit.append("FPCount: " + returnValue.getFPCount());
                                for (i = 0; i < returnValue.getFPCount(); i++) {
                                    resEdit.append("\n");
                                    resEdit.append("FP Index: " + CommonUtility.byte2hex(fpList[i].data));
                                }
                            }
                            resEdit.append("\nReturn Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                        } else if (msg.what == BlueToothWrapper.MSG_VERIFYFP_FINISH) {
                            BlueToothWrapper.GetFPListReturnValue returnValue = (BlueToothWrapper.GetFPListReturnValue) msg.obj;
                            if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                MiddlewareInterface.FingerPrintID[] fpList = returnValue.getFPList();
                                resEdit.append("\n");
                                resEdit.append("FPCount: " + returnValue.getFPCount());
                                for (i = 0; i < returnValue.getFPCount(); i++) {
                                    resEdit.append("\n");
                                    resEdit.append("FP Index: " + CommonUtility.byte2hex(fpList[i].data));
                                }
                            }
                            resEdit.append("\nReturn Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                        } else if (msg.what == BlueToothWrapper.MSG_GET_DEV_INFO_FINISH) {
                            BlueToothWrapper.GetDevInfoReturnValue returnValue = (BlueToothWrapper.GetDevInfoReturnValue) msg.obj;
                            if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                resEdit.append("\nDevice Info: " + returnValue.getDeviceInfo().toString(13));
                            }
                            resEdit.append("\nReturn Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                        } else if (msg.what == BlueToothWrapper.MSG_GET_ADDRESS_FINISH) {
                            BlueToothWrapper.GetAddressReturnValue returnValue = (BlueToothWrapper.GetAddressReturnValue) msg.obj;
                            if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                if (returnValue.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_EOS) {
                                    resEdit.append("\nEOS Address: " + returnValue.getAddress());
                                } else if (returnValue.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_ETH) {
                                    resEdit.append("\nETH Address: " + returnValue.getAddress());
                                } else if (returnValue.getCoinType() == MiddlewareInterface.PAEW_COIN_TYPE_CYB) {
                                    resEdit.append("\nCYB Address: " + returnValue.getAddress());
                                }
                            }
                            resEdit.append("\nReturn Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                        } else if (msg.what == BlueToothWrapper.MSG_EOS_SIGN_FINISH) {
                            BlueToothWrapper.SignReturnValue returnValue = (BlueToothWrapper.SignReturnValue)msg.obj;
                            if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                String strSignature = new String(returnValue.getSignature());
                                resEdit.append("\nSignature Value: " + strSignature);
                            }
                            resEdit.append("\nReturn Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                        } else if (msg.what == BlueToothWrapper.MSG_ETH_SIGN_FINISH) {
                            BlueToothWrapper.SignReturnValue returnValue = (BlueToothWrapper.SignReturnValue)msg.obj;
                            if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                String strSignature = CommonUtility.byte2hex(returnValue.getSignature());
                                resEdit.append("\nSignature Value: " + strSignature);
                            }
                            resEdit.append("\nReturn Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                        }
                        else if (msg.what == BlueToothWrapper.MSG_CYB_SIGN_FINISH) {
                            BlueToothWrapper.SignReturnValue returnValue = (BlueToothWrapper.SignReturnValue)msg.obj;
                            if (returnValue.getReturnValue() == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                String strSignature = CommonUtility.byte2hex(returnValue.getSignature());
                                resEdit.append("\nSignature Value: " + strSignature);
                            }
                            resEdit.append("\nReturn Value: " + MiddlewareInterface.getReturnString(returnValue.getReturnValue()));
                        }
                    } else {
                        resEdit.append("\nReturn Value: " + MiddlewareInterface.getReturnString(msg.arg1));
                    }
                    resEdit.append("\n" + getProcessName(msg.what) + " Finish");

                    if (msg.what == BlueToothWrapper.MSG_FREE_CONTEXT_FINISH) {
                        DevTestActivity.this.finish();
                    }
                    break;
                case BlueToothWrapper.MSG_HEART_BEAT_DATA_UPDATE:
                    String strDevState = getResources().getString(R.string.text_dev_state);
                    String strDevStateFormat = getResources().getString(R.string.text_dev_state_format);
                    if (msg.obj != null) {
                        byte[] heartBeatData = (byte[])msg.obj;
                        if (heartBeatData.length >= 3 ) {
                            strDevState = String.format(strDevStateFormat, (heartBeatData[1] == 0x00) ? ("USB") : ("Battery"), String.format("0x%02x", heartBeatData[2]));
                        }
                    }
                    m_textDevState.setText(strDevState);
                    break;
                case BlueToothWrapper.MSG_CONNECT_STATE_UPDATE:
                    if ((boolean)msg.obj) {
                        m_textDevConnect.setText("Connected");
                    } else {
                        m_textDevConnect.setText("Disconnected");
                        m_textDevState.setText(getResources().getString(R.string.text_dev_state));
                    }
                    break;
            }
        }
    }

    class TestPagerAdapter extends FragmentPagerAdapter {
        TestPagerAdapter(FragmentManager fragMgr) {
            super(fragMgr);
        }

        @Override
        public Fragment getItem(int position) {
            if (m_fragList == null) {
                return null;
            }
            return m_fragList.get(position);
        }

        @Override
        public int getCount() {
            if (m_fragList == null) {
                return 0;
            }
            return m_fragList.size();
        }
    }

    public static class DeviceTestFragment extends Fragment {
        public DeviceTestFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View viewRet = inflater.inflate(R.layout.fragment_device, container, false);

            initClickListener(viewRet);

            return viewRet;
        }

        void initClickListener(View curView) {
            Button curButton;

            curButton = curView.findViewById(R.id.btn_getdevinfo);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setGetInfoWrapper(m_contextHandle, m_devIndex);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_initpin);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setInitPINWrapper(m_contextHandle, m_devIndex, m_strDefaultPIN);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_verifypin);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        View dlgView = getLayoutInflater().inflate(R.layout.dlg_verify_pin, null);
                        final EditText editPIN = dlgView.findViewById(R.id.edit_pin);
                        editPIN.setText(m_strDefaultPIN);
                        editPIN.selectAll();
                        AlertDialog dlg = new AlertDialog.Builder(m_curContext)
                                .setIcon(R.mipmap.icon_ble)
                                .setTitle("Please Input PIN:")
                                .setView(dlgView).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int PINLength = editPIN.getText().toString().length();
                                        if ((PINLength < m_minPINLen) || (PINLength > m_maxPINLen)) {
                                            Toast toast = Toast.makeText(m_curContext, "Please input PIN between " + m_minPINLen + " and " + m_maxPINLen, Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            return;
                                        }
                                        if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                                        {
                                            m_testThread = new BlueToothWrapper(m_mainHandler);
                                            ((BlueToothWrapper)m_testThread).setVerifyPINWrapper(m_contextHandle, m_devIndex, editPIN.getText().toString());
                                            m_testThread.start();
                                        }
                                        else
                                        {
                                            Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    }
                                })
                                .setCancelable(false)
                                .create();
                        dlg.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_changepin);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        View dlgView = getLayoutInflater().inflate(R.layout.dlg_change_pin, null);
                        final EditText editPIN = dlgView.findViewById(R.id.edit_pin);
                        final EditText editNewPIN = dlgView.findViewById(R.id.edit_new_pin);
                        editPIN.setText(m_strDefaultPIN);
                        editPIN.selectAll();
                        editNewPIN.setText(m_strDefaultNewPIN);
                        AlertDialog dlg = new AlertDialog.Builder(m_curContext)
                                .setIcon(R.mipmap.icon_ble)
                                .setTitle("Please Input PIN:")
                                .setView(dlgView).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int PINLength = editPIN.getText().toString().length();
                                        if ((PINLength < m_minPINLen) || (PINLength > m_maxPINLen)) {
                                            Toast toast = Toast.makeText(m_curContext, "Please input PIN between " + m_minPINLen + " and " + m_maxPINLen, Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            return;
                                        }
                                        int NewPINLength = editNewPIN.getText().toString().length();
                                        if ((NewPINLength < m_minPINLen) || (NewPINLength > m_maxPINLen)) {
                                            Toast toast = Toast.makeText(m_curContext, "Please input PIN between " + m_minPINLen + " and " + m_maxPINLen, Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                            return;
                                        }
                                        if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                                        {
                                            m_testThread = new BlueToothWrapper(m_mainHandler);
                                            ((BlueToothWrapper)m_testThread).setChangePINWrapper(m_contextHandle, m_devIndex, editPIN.getText().toString(), editNewPIN.getText().toString());
                                            m_testThread.start();
                                        }
                                        else
                                        {
                                            Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    }
                                })
                                .setCancelable(false)
                                .create();
                        dlg.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_format);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setFormatDeviceWrapper(m_contextHandle, m_devIndex);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_clear_screen);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setClearScreenWrapper(m_contextHandle, m_devIndex);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_freeContext);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_freeContextThread == null) || (m_freeContextThread.getState() == Thread.State.TERMINATED))
                    {
                        m_freeContextThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_freeContextThread).setFreeContextWrapper(m_contextHandle);
                        m_freeContextThread.start();

                        m_contextHandle = 0;
                        m_devIndex = MiddlewareInterface.INVALID_DEV_INDEX;
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_freeContextAndShutDown);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_freeContextThread == null) || (m_freeContextThread.getState() == Thread.State.TERMINATED))
                    {
                        m_freeContextThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_freeContextThread).setFreeContextAndShutDownWrapper(m_contextHandle);
                        m_freeContextThread.start();

                        m_contextHandle = 0;
                        m_devIndex = MiddlewareInterface.INVALID_DEV_INDEX;
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }
    }

    public static class FingerPrintTestFragment extends Fragment {
        public FingerPrintTestFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View viewRet = inflater.inflate(R.layout.fragment_fprint, container, false);

            initClickListener(viewRet);

            return viewRet;
        }

        void initClickListener(View curView) {
            Button curButton;

            curButton = curView.findViewById(R.id.btn_calibrate);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setCalibrateFPWrapper(m_contextHandle, m_devIndex);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_getfplist);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setGetFPListWrapper(m_contextHandle, m_devIndex);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_enrollfp);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setEnrollFPWrapper(m_contextHandle, m_devIndex);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_verifyfp);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setVerifyFPWrapper(m_contextHandle, m_devIndex);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_deletefp);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setDeleteFPWrapper(m_contextHandle, m_devIndex, null);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_abort);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_abortThread == null) || (m_abortThread.getState() == Thread.State.TERMINATED))
                    {
                        m_abortThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_abortThread).setAbortFPWrapper(m_contextHandle, m_devIndex);
                        m_abortThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }
    }

    public static class InitWalletTestFragment extends Fragment {
        public InitWalletTestFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View viewRet = inflater.inflate(R.layout.fragment_initwallet, container, false);

            initClickListener(viewRet);

            return viewRet;
        }

        void initClickListener(View curView) {
            Button curButton;

            curButton = curView.findViewById(R.id.btn_genseed);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setGenerateSeedWrapper(m_contextHandle, m_devIndex, 32);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_importmne);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strMnes = "mass dust captain baby mass dust captain baby mass dust captain baby mass dust captain baby mass electric";
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setImportMneWrapper(m_contextHandle, m_devIndex, strMnes);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }
    }

    public static class WalletTestFragment extends Fragment {
        public WalletTestFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View viewRet = inflater.inflate(R.layout.fragment_wallet, container, false);

            initClickListener(viewRet);

            return viewRet;
        }

        void initClickListener(View curView) {
            Button curButton;

            curButton = curView.findViewById(R.id.btn_ethsign);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] derivePath = {0, 0x8000002c, 0x8000003c, 0x80000000, 0x00000000, 0x00000000};
                    byte[] transaction = {(byte)0xec, (byte)0x09, (byte)0x85, (byte)0x04, (byte)0xa8, (byte)0x17, (byte)0xc8, (byte)0x00, (byte)0x82, (byte)0x52, (byte)0x08, (byte)0x94, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x35, (byte)0x88, (byte)0x0d, (byte)0xe0, (byte)0xb6, (byte)0xb3, (byte)0xa7, (byte)0x64, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0x01, (byte)0x80, (byte)0x80};
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setETHSignWrapper(m_contextHandle, m_devIndex, m_uiLock, derivePath, transaction);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_eossign);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] derivePath = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};
                    byte[] transaction = {(byte)0x74, (byte)0x09, (byte)0x70, (byte)0xd9, (byte)0xff, (byte)0x01, (byte)0xb5, (byte)0x04, (byte)0x63, (byte)0x2f, (byte)0xed, (byte)0xe1, (byte)0xad, (byte)0xc3, (byte)0xdf, (byte)0xe5, (byte)0x59, (byte)0x90, (byte)0x41, (byte)0x5e, (byte)0x4f, (byte)0xde, (byte)0x01, (byte)0xe1, (byte)0xb8, (byte)0xf3, (byte)0x15, (byte)0xf8, (byte)0x13, (byte)0x6f, (byte)0x47, (byte)0x6c, (byte)0x14, (byte)0xc2, (byte)0x67, (byte)0x5b, (byte)0x01, (byte)0x24, (byte)0x5f, (byte)0x70, (byte)0x5d, (byte)0xd7, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0xa6, (byte)0x82, (byte)0x34, (byte)0x03, (byte)0xea, (byte)0x30, (byte)0x55, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x57, (byte)0x2d, (byte)0x3c, (byte)0xcd, (byte)0xcd, (byte)0x01, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xa8, (byte)0xed, (byte)0x32, (byte)0x32, (byte)0x21, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x90, (byte)0x55, (byte)0x8c, (byte)0x86, (byte)0x77, (byte)0x95, (byte)0x4c, (byte)0x3c, (byte)0x10, (byte)0x27, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x45, (byte)0x4f, (byte)0x53, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setEOSSignWrapper(m_contextHandle, m_devIndex, m_uiLock, derivePath, transaction);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_cybsign);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int[] derivePath = {0, 0, 1, 0x00000080, 0x00000000, 0x00000000};
                    byte[] transaction = {(byte)0x26, (byte)0xe9, (byte)0xbf, (byte)0x22, (byte)0x06, (byte)0xa1, (byte)0xd1, (byte)0x5c, (byte)0x7e, (byte)0x5b, (byte)0x01, (byte)0x00, (byte)0xe8, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x80, (byte)0xaf, (byte)0x02, (byte)0x80, (byte)0xaf, (byte)0x02, (byte)0x0a, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x04, (byte)0x0a, (byte)0x7a, (byte)0x68, (byte)0x61, (byte)0x6e, (byte)0x67, (byte)0x73, (byte)0x79, (byte)0x31, (byte)0x33, (byte)0x33, (byte)0x03, (byte)0x43, (byte)0x59, (byte)0x42, (byte)0x03, (byte)0x43, (byte)0x59, (byte)0x42, (byte)0x05, (byte)0x05, (byte)0x00};
                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        m_testThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_testThread).setCYBSignWrapper(m_contextHandle, m_devIndex, m_uiLock, derivePath, transaction);
                        m_testThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_getaddress);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String[] coinTypeString = {"EOS", "ETH", "CYB"};
                    final byte[] coinTypes = {MiddlewareInterface.PAEW_COIN_TYPE_EOS, MiddlewareInterface.PAEW_COIN_TYPE_ETH, MiddlewareInterface.PAEW_COIN_TYPE_CYB};
                    final int[][] derivePaths = {
                            {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000},
                            {0, 0x8000002c, 0x8000003c, 0x80000000, 0x00000000, 0x00000000},
                            {0, 0, 1, 0x00000080, 0x00000000, 0x00000000}
                    };
                    m_coinChoiceIndex = 0;

                    if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                    {
                        AlertDialog dlg = new AlertDialog.Builder(m_curContext)
                                .setIcon(R.mipmap.icon_ble)
                                .setTitle("Please Select Coin Type:")
                                .setSingleChoiceItems(coinTypeString, m_coinChoiceIndex, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        m_coinChoiceIndex = which;
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if ((m_testThread == null) || (m_testThread.getState() == Thread.State.TERMINATED))
                                        {
                                            m_testThread = new BlueToothWrapper(m_mainHandler);
                                            ((BlueToothWrapper)m_testThread).setGetAddressWrapper(m_contextHandle, m_devIndex, coinTypes[m_coinChoiceIndex], derivePaths[m_coinChoiceIndex]);
                                            m_testThread.start();
                                        }
                                        else
                                        {
                                            Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    }
                                })
                                .setCancelable(false)
                                .create();
                        dlg.show();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });

            curButton = curView.findViewById(R.id.btn_abort);
            curButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((m_abortThread == null) || (m_abortThread.getState() == Thread.State.TERMINATED))
                    {
                        m_abortThread = new BlueToothWrapper(m_mainHandler);
                        ((BlueToothWrapper)m_abortThread).setAbortFPWrapper(m_contextHandle, m_devIndex);
                        m_abortThread.start();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(m_curContext, "Test Still Running", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_test);

        m_curContext = this;

        m_contextHandle = getIntent().getLongExtra("contextHandle", 0);
        m_devIndex = getIntent().getIntExtra("devIndex", MiddlewareInterface.INVALID_DEV_INDEX);

        m_mainHandler = new MainHandler();
        BlueToothWrapper.setHeartBeatHandler(m_mainHandler);

        m_uiLock = new ReentrantLock();
        m_uiLock.lock();

        m_textDevState = findViewById(R.id.text_dev_state);
        m_textDevState.setText(R.string.text_dev_state);
        m_textDevConnect = findViewById(R.id.text_dev_connect);

        m_editResult = findViewById(R.id.edit_result);

        m_viewPager = findViewById(R.id.test_container);
        m_viewPager.setAdapter(new TestPagerAdapter(getSupportFragmentManager()));

        //init fragments
        m_fragList = new ArrayList<>(0);
        m_fragList.add(new DeviceTestFragment());
        m_fragList.add(new FingerPrintTestFragment());
        m_fragList.add(new InitWalletTestFragment());
        m_fragList.add(new WalletTestFragment());

        //!!!!!must do it!!!!!!
        m_viewPager.getAdapter().notifyDataSetChanged();

        m_tabTests = findViewById(R.id.test_tab);
        m_tabTests.addTab(m_tabTests.newTab().setText(R.string.tab_item_device).setTag(TAB_DEVICE));
        m_tabTests.addTab(m_tabTests.newTab().setText(R.string.tab_item_fp).setTag(TAB_FINGERPRINT));
        m_tabTests.addTab(m_tabTests.newTab().setText(R.string.tab_item_init).setTag(TAB_INIT_WALLET));
        m_tabTests.addTab(m_tabTests.newTab().setText(R.string.tab_item_wallet).setTag(TAB_WALLET));
        m_tabTests.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int layout_height = 0;
                LinearLayout.LayoutParams layoutParams = null;

                switch((int)tab.getTag()) {
                    case TAB_DEVICE:
                        m_viewPager.setCurrentItem(0);
                        break;
                    case TAB_FINGERPRINT:
                        m_viewPager.setCurrentItem(1);
                        break;
                    case TAB_INIT_WALLET:
                        m_viewPager.setCurrentItem(2);
                        break;
                    case TAB_WALLET:
                        m_viewPager.setCurrentItem(3);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("contextHandle", m_contextHandle);
            setResult(RESULT_CANCELED, intent);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
