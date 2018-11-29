package com.extropies.testapp;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.extropies.common.MiddlewareInterface;
import com.extropies.common.CommonUtility;

public class BlueToothWrapper extends Thread {
    public static final int INVALID_WRAPPER = -1;

    public static final int INIT_CONTEXT_WITH_DEVNAME_WRAPPER = 8;
    public static final int FREE_CONTEXT_WRAPPER = 10;
    public static final int GET_DEV_INFO_WRAPPER = 11;
    public static final int INIT_PIN_WRAPPER = 12;
    public static final int VERIFY_PIN_WRAPPER = 13;
    public static final int CHANGE_PIN_WRAPPER = 14;
    public static final int GET_FP_LIST_WRAPPER = 15;
    public static final int ENROLL_FP_WRAPPER = 16;
    public static final int VERIFY_FP_WRAPPER = 17;
    public static final int DELETE_FP_WRAPPER = 18;
    public static final int FORMAT_DEV_WRAPPER = 19;
    public static final int GEN_SEED_WRAPPER = 20;
    public static final int ETH_SIGN_WRAPPER = 21;
    public static final int EOS_SIGN_WRAPPER = 22;
    public static final int IMPORT_MNE_WRAPPER = 23;
    public static final int GET_ADDRESS_WRAPPER = 24;
    public static final int CALIBRATE_FP_WRAPPER = 25;
    public static final int ABORT_FP_WRAPPER = 26;
    public static final int GET_DEV_LIST_WRAPPER = 27;
    public static final int FREE_CONTEXT_AND_SHUT_DOWN_WRAPPER = 28;
    public static final int CLEAR_SCREEN_WRAPPER = 29;
    public static final int CYB_SIGN_WRAPPER = 30;
    public static final int EOS_SERIALIZE_WRAPPER = 42;

    //messages
    public static final int MSG_ENUM_START = 0;
    public static final int MSG_ENUM_UPDATE = 1;
    public static final int MSG_ENUM_FINISH = 2;
    public static final int MSG_INIT_CONTEXT_START = 13;
    public static final int MSG_INIT_CONTEXT_FINISH = 14;
    public static final int MSG_ENROLL_START = 15;
    public static final int MSG_ENROLL_UPDATE = 16;
    public static final int MSG_ENROLL_FINISH = 17;
    public static final int MSG_GET_DEV_INFO_START = 18;
    public static final int MSG_GET_DEV_INFO_FINISH = 19;
    public static final int MSG_VERIFYFP_START = 20;
    public static final int MSG_VERIFYFP_UPDATE = 21;
    public static final int MSG_VERIFYFP_FINISH = 22;
    public static final int MSG_FREE_CONTEXT_START = 23;
    public static final int MSG_FREE_CONTEXT_FINISH = 24;
    public static final int MSG_GET_FP_LIST_START = 25;
    public static final int MSG_GET_FP_LIST_FINISH = 26;
    public static final int MSG_CALIBRATE_FP_START = 27;
    public static final int MSG_CALIBRATE_FP_FINISH = 28;
    public static final int MSG_DELETE_FP_START = 29;
    public static final int MSG_DELETE_FP_FINISH = 30;
    public static final int MSG_ABORT_FP_START = 31;
    public static final int MSG_ABORT_FP_FINISH = 32;
    public static final int MSG_INIT_PIN_START = 33;
    public static final int MSG_INIT_PIN_FINISH = 34;
    public static final int MSG_CHANGE_PIN_START = 35;
    public static final int MSG_CHANGE_PIN_FINISH = 36;
    public static final int MSG_VERIFY_PIN_START = 37;
    public static final int MSG_VERIFY_PIN_FINISH = 38;
    public static final int MSG_GEN_SEED_START = 39;
    public static final int MSG_GEN_SEED_FINISH = 40;
    public static final int MSG_FORMAT_DEVICE_START = 41;
    public static final int MSG_FORMAT_DEVICE_FINISH = 42;
    public static final int MSG_EOS_SIGN_START = 43;
    public static final int MSG_EOS_SIGN_FINISH = 44;
    public static final int MSG_ETH_SIGN_START = 45;
    public static final int MSG_ETH_SIGN_FINISH = 46;
    public static final int MSG_GET_ADDRESS_START = 47;
    public static final int MSG_GET_ADDRESS_FINISH = 48;
    public static final int MSG_SIGN_UPDATE = 49;
    public static final int MSG_IMPORT_MNE_START = 50;
    public static final int MSG_IMPORT_MNE_FINISH = 51;
    public static final int MSG_CLEAR_SCREEN_START = 52;
    public static final int MSG_CLEAR_SCREEN_FINISH = 53;

    public static final int MSG_GET_AUTH_TYPE = 54;
    public static final int MSG_GET_USER_PIN = 55;
    public static final int MSG_RESET_UI_LOCK = 56;

    public static final int MSG_HEART_BEAT_DATA_UPDATE = 57;
    public static final int MSG_CONNECT_STATE_UPDATE = 58;

    public static final int MSG_CYB_SIGN_START = 59;
    public static final int MSG_CYB_SIGN_FINISH = 60;

    public static final int MSG_EOS_SERIALIZE_START = 83;
    public static final int MSG_EOS_SERIALIZE_FINISH = 84;

    private static Map<String, Object> m_listCommLock;
    private Object m_objCommLock;

    private static Map<String, Object> m_listPriorityLock;
    private Object m_objPriorityLock;
    private boolean m_bPriority;

    private int m_wrapperType;
    private Handler m_mainHandler;
    private static Handler m_heartBeatHandler = null;
    private Context m_activity = null; //for INIT_WRAPPER
    private String m_strFilter = null; //for ENUM_WRAPPER
    private String m_strDevName = null; //for CONNECT_WRAPPER
    private long m_devHandle = 0; // for SEND_CMD_WRAPPER
    private byte[] m_sendData = null; // for SEND_CMD_WRAPPER
    private boolean m_bAutoSendData; //for AUTO_RECV_CMD_WRAPPER
    private String m_strPIN; //for INIT_PIN_WRAPPER
    private String m_strNewPIN; //for CHANGE_PIN_WRAPPER
    private MiddlewareInterface.FingerPrintID[] m_fpList;
    private int m_seedLen;
    private byte m_coinType;
    private int[] m_derivePath;
    private byte[] m_trasaction;
    private String m_strMnes;
    private String m_strSerialNumber;
    private byte[] m_seedData;
    private byte m_imageIndex;
    private byte[] m_imageData;
    private int m_imageWidth;
    private int m_imageHeight;
    private byte m_showMode;
    private String m_strImageName;
    private String m_strEOSTxString;

    private byte[] m_cosData;
    private boolean m_bRestart;

    private static Lock m_commonLock = null;
    private static boolean m_bAborting;

    private long m_contextHandle = 0;
    private int m_devIndex = 0;

    private ReentrantLock m_uiLock = null;
    private boolean m_bShowGetAuthDlgCalled = false;

    public static class ConnectReturnValue {
        private long m_devHandle;
        private int m_returnValue;
        private String m_strDeviceName;

        ConnectReturnValue(String strDeviceName, long devHandle, int returnValue) {
            m_strDeviceName = strDeviceName;
            m_devHandle = devHandle;
            m_returnValue = returnValue;
        }

        public String getDeviceName() {
            return m_strDeviceName;
        }
        public long getDeviceHandle() {
            return m_devHandle;
        }
        public int getReturnValue() {
            return m_returnValue;
        }
    }

    public static class RecvReturnValue {
        private byte[] m_recvData;
        private int m_returnValue;
        private String m_strMessage;

        RecvReturnValue(byte[] recvData, int returnValue, String strMessage) {
            m_recvData = recvData;
            m_returnValue = returnValue;
            if (strMessage != null) {
                m_strMessage = strMessage;
            } else {
                m_strMessage = "";
            }
        }

        public byte[] getRecvData() {
            return m_recvData;
        }
        public int getReturnValue() {
            return m_returnValue;
        }
        public String getMessage() {
            return m_strMessage;
        }
    }

    public static class InitContextReturnValue {
        private int m_returnValue;
        private long m_contextHandle;
        private int m_devCount;
        InitContextReturnValue(int returnValue, int devCount, long contextHandle) {
            m_returnValue = returnValue;
            m_devCount = devCount;
            m_contextHandle = contextHandle;
        }

        public int getReturnValue() {
            return m_returnValue;
        }
        public int getDevCount() {
            return m_devCount;
        }
        public long getContextHandle() {
            return m_contextHandle;
        }
    }

    public static class GetFPListReturnValue {
        private int m_returnValue;
        private int m_fpCount;
        private MiddlewareInterface.FingerPrintID[] m_fpList;
        GetFPListReturnValue(int returnValue, int fpCount, MiddlewareInterface.FingerPrintID[] fpList) {
            m_returnValue = returnValue;
            m_fpCount = fpCount;
            m_fpList = fpList;
        }
        public int getReturnValue() {
            return m_returnValue;
        }
        public int getFPCount() {
            return m_fpCount;
        }
        public MiddlewareInterface.FingerPrintID[] getFPList() {
            return m_fpList;
        }
    }

    public static class GetDevInfoReturnValue {
        private int m_returnValue;
        private MiddlewareInterface.PAEW_DevInfo m_devInfo;
        GetDevInfoReturnValue(int returnValue, MiddlewareInterface.PAEW_DevInfo devInfo) {
            m_returnValue = returnValue;
            m_devInfo = devInfo;
        }

        public int getReturnValue() {
            return m_returnValue;
        }
        public MiddlewareInterface.PAEW_DevInfo getDeviceInfo() { return m_devInfo; }
    }

    public static class SignReturnValue {
        private int m_returnValue;
        private byte[] m_signature;
        SignReturnValue(int returnValue, byte[] signature, int sigLen) {
            m_returnValue = returnValue;
            if (signature != null) {
                m_signature = new byte[sigLen];
                System.arraycopy(signature, 0, m_signature, 0, sigLen);
            } else {
                m_signature = null;
            }
        }
        public int getReturnValue() { return m_returnValue; }
        public byte[] getSignature() { return m_signature; }
    }

    public static class GetAddressReturnValue {
        private int m_returnValue;
        private byte m_coinType;
        private String m_address;
        GetAddressReturnValue(int returnValue, String address, byte coinType) {
            m_returnValue = returnValue;
            m_address = address;
            m_coinType = coinType;
        }
        public int getReturnValue() { return m_returnValue; }
        public String getAddress() { return m_address; }
        public byte getCoinType() {
            return m_coinType;
        }
    }

    public static class GetCheckCodeReturnValue {
        private int m_returnValue;
        private byte[] m_checkCode;

        GetCheckCodeReturnValue(int returnValue, byte[] checkCode) {
            m_returnValue = returnValue;
            m_checkCode = checkCode;
        }
        public int getReturnValue() { return m_returnValue; }
        public byte[] getCheckCode() { return m_checkCode; }
    }

    public static class RecoverSeedReturnValue {
        private int m_returnValue;
        private byte[] m_seedData;

        RecoverSeedReturnValue(int returnValue, byte[] seedData) {
            m_returnValue = returnValue;
            m_seedData = seedData;
        }
        public int getReturnValue() { return m_returnValue; }
        public byte[] getSeedData() { return m_seedData; }
    }

    public static class RecoverAddressReturnValue {
        private int m_returnValue;
        private byte[] m_privateKey;
        private byte[] m_tradeAddress;

        RecoverAddressReturnValue(int returnValue, byte[] privateKey, byte[] tradeAddress) {
            m_returnValue = returnValue;
            m_privateKey = privateKey;
            m_tradeAddress = tradeAddress;
        }
        public int getReturnValue() { return m_returnValue; }
        public byte[] getPrivateKey() { return m_privateKey; }
        public byte[] getTradeAddress() { return m_tradeAddress; }
    }

    public static class GetImageNameReturnValue {
        private int m_returnValue;
        private String m_strImageName;

        GetImageNameReturnValue(int returnValue, String strImageName) {
            m_returnValue = returnValue;
            m_strImageName = strImageName;
        }
        public int getReturnValue() { return m_returnValue; }
        public String getImageName() { return m_strImageName; }
    }

    public static class GetImageCountReturnValue {
        private int m_returnValue;
        private int m_imageCount;

        GetImageCountReturnValue(int returnValue, int imageCount) {
            m_returnValue = returnValue;
            m_imageCount = imageCount;
        }
        public int getReturnValue() { return m_returnValue; }
        public int getImageCount() { return m_imageCount; }
    }

    public static class EOSTxSerializeReturn {
        private int m_returnValue;
        private byte[] m_serializeData;

        EOSTxSerializeReturn(int returnValue, byte[] serializeData) {
            m_returnValue = returnValue;
            m_serializeData = serializeData;
        }
        public int getReturnValue() { return m_returnValue; }
        public byte[] getSerializeData() { return m_serializeData; }
    }

    public static class GetFWVersionReturnValue {
        private int m_returnValue;
        private MiddlewareInterface.PAEW_FWVersion m_fwVersion;
        GetFWVersionReturnValue(int returnValue, MiddlewareInterface.PAEW_FWVersion fwVersion) {
            m_returnValue = returnValue;
            m_fwVersion = fwVersion;
        }

        public int getReturnValue() {
            return m_returnValue;
        }
        public MiddlewareInterface.PAEW_FWVersion getFWVersion() { return m_fwVersion; }
    }

    private CommonUtility.enumCallback m_enumCallback = new CommonUtility.enumCallback() {
        @Override
        public void discoverDevice(String[] strDeviceNames) {
            Message msg;
            msg = m_mainHandler.obtainMessage();
            msg.what = MSG_ENUM_UPDATE;
            msg.obj = strDeviceNames;
            msg.sendToTarget();
        }
    };

    private CommonUtility.fpStateCallback m_enrollCallback = new CommonUtility.fpStateCallback() {
        @Override
        public void pushFingerPrintState(int nFPState) {
            Message msg;

            msg = m_mainHandler.obtainMessage();
            msg.what = MSG_ENROLL_UPDATE;
            msg.arg1 = nFPState;
            msg.sendToTarget();

            if (m_bAborting) {
                m_commonLock.unlock();
                while(m_bAborting);
                m_commonLock.lock();
            }
        }
    };

    private CommonUtility.fpStateCallback m_verifyCallback = new CommonUtility.fpStateCallback() {
        @Override
        public void pushFingerPrintState(int nFPState) {
            Message msg;

            msg = m_mainHandler.obtainMessage();
            msg.what = MSG_VERIFYFP_UPDATE;
            msg.arg1 = nFPState;
            msg.sendToTarget();

            if (m_bAborting) {
                m_commonLock.unlock();
                while(m_bAborting);
                m_commonLock.lock();
            }
        }
    };

    public int showGetAuthDialog() {
        int iRtn = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;

        m_bShowGetAuthDlgCalled = true;

        if (m_uiLock != null) {
            while(!m_uiLock.isLocked()); //wait ui thread to lock

            Message msg;
            msg = m_mainHandler.obtainMessage();
            msg.what = MSG_GET_AUTH_TYPE;
            msg.sendToTarget();
        }

        return iRtn;
    }

    public int showInputPINDialog() {
        int iRtn = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;

        if (m_uiLock != null) {
            while(!m_uiLock.isLocked()); //wait ui thread to lock

            Message msg;
            msg = m_mainHandler.obtainMessage();
            msg.what = MSG_GET_USER_PIN;
            msg.sendToTarget();
        }

        return iRtn;
    }

    /**
     * Sign Callbacks
     * Callbacks are invoked in the following sequence
     * 1. Invoke getAuthResult(), return PAEW_RET_SUCCESS or PAEW_RET_DEV_OP_CANCEL, indicates user chooses OK or Cancel on UI. If returns PAEW_RET_SUCCESS, signature will go on; if returns PAEW_RET_DEV_OP_CANCEL, you should call abort() to end this sign procedure.
     * 2. Invoke getAuthType(), return PAEW_SIGN_AUTH_TYPE_PIN or PAEW_SIGN_AUTH_TYPE_FP.
     * 3. If getAuthType() returns PAEW_SIGN_AUTH_TYPE_PIN, then call getPINResult(). getPINResult() returns PAEW_RET_SUCCESS or PAEW_RET_DEV_OP_CANCEL, indicates user choosesOK or Cancel on UI.If returns PAEW_RET_SUCCESS, signature will go on; if returns PAEW_RET_DEV_OP_CANCEL, you should call abort() to end this sign procedure.
     * 4. If getAuthType() returns PAEW_SIGN_AUTH_TYPE_PIN, and getPINResult() returns PAEW_RET_SUCCESS, then call getPIN() to get PIN from UI.
     * 5. Do signature according to user's option.
     *
     * pseudo-code of signature method:
     * if (MiddlewareInterface.PAEW_RET_SUCCESS != getAuthResult()) {
     *     return;
     * }
     * nAuthType = getAuthType();
     * if (Middleware.PAEW_SIGN_AUTH_TYPE_PIN == nAuthType) {
     *     if (MiddlewareInterface.PAEW_RET_SUCCESS != getPINResult()) {
     *         return;
     *     }
     *     strPIN = getPIN();
     * }
     * nResult = (do signature with user selected authenticate type (finger print or PIN))
     * if ((MiddlewareInterface.PAEW_RET_SUCCESS != nResult) && (Middleware.PAEW_SIGN_AUTH_TYPE_PIN != nAuthType)) {
     *     if (MiddlewareInterface.PAEW_RET_SUCCESS != getPINResult()) {
     *         return;
     *     }
     *     strPIN = getPIN();
     *     (do signature with PIN authority)
     * }
     */
    private CommonUtility.signCallback m_signCallback = new CommonUtility.signCallback() {
        private String m_strPIN = "";
        private byte m_authType = MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_FP;

        @Override
        public int getAuthResult() {
            int iRtn = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;

            if (m_bShowGetAuthDlgCalled) {
                if (m_uiLock != null) {
                    Message msg;

                    m_uiLock.lock();

                    if (DevTestActivity.m_authTypeResult != MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iRtn = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;

                        m_uiLock.unlock();
                        msg = m_mainHandler.obtainMessage();
                        msg.what = MSG_RESET_UI_LOCK;
                        msg.sendToTarget();
                    } else {
                        iRtn = MiddlewareInterface.PAEW_RET_SUCCESS;
                        m_authType = DevTestActivity.m_authType;

                        if (m_authType != MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_PIN) {
                            m_uiLock.unlock();
                            msg = m_mainHandler.obtainMessage();
                            msg.what = MSG_RESET_UI_LOCK;
                            msg.sendToTarget();
                        }
                    }
                }
                m_bShowGetAuthDlgCalled = false;
            } else {
                if (m_uiLock != null) {
                    while(!m_uiLock.isLocked()); //wait ui thread to lock

                    Message msg;
                    msg = m_mainHandler.obtainMessage();
                    msg.what = MSG_GET_AUTH_TYPE;
                    msg.sendToTarget();

                    m_uiLock.lock();
                    if (DevTestActivity.m_authTypeResult != MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iRtn = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;

                        m_uiLock.unlock();
                        msg = m_mainHandler.obtainMessage();
                        msg.what = MSG_RESET_UI_LOCK;
                        msg.sendToTarget();
                    } else {
                        iRtn = MiddlewareInterface.PAEW_RET_SUCCESS;
                        m_authType = DevTestActivity.m_authType;

                        if (m_authType != MiddlewareInterface.PAEW_SIGN_AUTH_TYPE_PIN) {
                            m_uiLock.unlock();
                            msg = m_mainHandler.obtainMessage();
                            msg.what = MSG_RESET_UI_LOCK;
                            msg.sendToTarget();
                        }
                    }
                }
            }
            return iRtn;
        }

        @Override
        public byte getAuthType() {
            return m_authType;
        }

        @Override
        public int getPINResult() {
            int iRtn = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;

            if (m_uiLock != null) {
                Message msg;

                if (!m_uiLock.isHeldByCurrentThread()) {
                    showInputPINDialog();

                    while(!m_uiLock.isLocked()); //wait ui thread to lock
                    m_uiLock.lock();
                }

                if (DevTestActivity.m_getPINResult != MiddlewareInterface.PAEW_RET_SUCCESS) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_OP_CANCEL;
                } else {
                    iRtn = MiddlewareInterface.PAEW_RET_SUCCESS;
                    m_strPIN = DevTestActivity.m_getPINString;
                }

                if (m_uiLock.isHeldByCurrentThread()) {
                    m_uiLock.unlock();
                    msg = m_mainHandler.obtainMessage();
                    msg.what = MSG_RESET_UI_LOCK;
                    msg.sendToTarget();
                }
            }

            return iRtn;
        }

        @Override
        public String getPIN() {
            return m_strPIN;
        }

        @Override
        public void putSignState(int nSignState) {
            Message msg;

            msg = m_mainHandler.obtainMessage();
            msg.what = MSG_SIGN_UPDATE;
            msg.arg1 = nSignState;
            msg.sendToTarget();

            if (m_bAborting) {
                m_commonLock.unlock();
                while(m_bAborting);
                m_commonLock.lock();
            }
        }
    };

    private CommonUtility.heartBeatCallback m_heartBeatCallback = new CommonUtility.heartBeatCallback() {
        @Override
        public void pushHeartBeatData(byte[] heartBeatData) {
            synchronized (BlueToothWrapper.class) {
                if (m_heartBeatHandler != null) {
                    Message msg;

                    msg = m_heartBeatHandler.obtainMessage();
                    msg.what = MSG_HEART_BEAT_DATA_UPDATE;
                    msg.obj = heartBeatData;
                    msg.sendToTarget();
                }
            }
        }

        @Override
        public void pushConnectState(boolean connected) {
            synchronized (BlueToothWrapper.class) {
                if (m_heartBeatHandler != null) {
                    Message msg;

                    msg = m_heartBeatHandler.obtainMessage();
                    msg.what = MSG_CONNECT_STATE_UPDATE;
                    msg.obj = connected;
                    msg.sendToTarget();
                }
            }
        }
    };

    public static void setHeartBeatHandler(Handler heartBeatHandler) {
        synchronized (BlueToothWrapper.class) {
            m_heartBeatHandler = heartBeatHandler;
        }
    }

    BlueToothWrapper(Handler mainHandler) {
        m_wrapperType = INVALID_WRAPPER;
        m_mainHandler = mainHandler;
        m_bAutoSendData = false;

        if (m_listCommLock == null) {
            m_listCommLock = new HashMap<>();
        }
        m_objCommLock = null;

        if (m_listPriorityLock == null) {
            m_listPriorityLock = new HashMap<>();
        }
        m_objPriorityLock = null;
        m_bPriority = false;

        if (m_commonLock == null) {
            m_commonLock = new ReentrantLock();
        }
        m_bAborting = false;
    }

    public boolean setInitContextWithDevNameWrapper(Activity activity, String strDevName) {
        m_wrapperType = INIT_CONTEXT_WITH_DEVNAME_WRAPPER;

        if (strDevName == null) {
            return false;
        }

        m_activity = activity;
        m_strDevName = strDevName;

        return true;
    }

    public boolean setFreeContextWrapper(long contextHandle) {
        m_wrapperType = FREE_CONTEXT_WRAPPER;

        m_contextHandle = contextHandle;
        return true;
    }

    public boolean setFreeContextAndShutDownWrapper(long contextHandle) {
        m_wrapperType = FREE_CONTEXT_AND_SHUT_DOWN_WRAPPER;

        m_contextHandle = contextHandle;
        return true;
    }

    public boolean setGetInfoWrapper(long contextHandle, int devIndex) {
        m_wrapperType = GET_DEV_INFO_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        return true;
    }

    public boolean setInitPINWrapper(long contextHandle, int devIndex, String strPIN) {
        if ((strPIN == null) || (strPIN == "")) {
            return false;
        }

        m_wrapperType = INIT_PIN_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        m_strPIN = strPIN;

        return true;
    }

    public boolean setVerifyPINWrapper(long contextHandle, int devIndex, String strPIN) {
        if ((strPIN == null) || (strPIN == "")) {
            return false;
        }

        m_wrapperType = VERIFY_PIN_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        m_strPIN = strPIN;

        return true;
    }

    public boolean setChangePINWrapper(long contextHandle, int devIndex, String strOldPIN, String strNewPIN) {
        if ((strOldPIN == null) || (strOldPIN == "")) {
            return false;
        }
        if ((strNewPIN == null) || (strNewPIN == "")) {
            return false;
        }

        m_wrapperType = CHANGE_PIN_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        m_strPIN = strOldPIN;
        m_strNewPIN = strNewPIN;

        return true;
    }

    public boolean setGetFPListWrapper(long contextHandle, int devIndex) {
        m_wrapperType = GET_FP_LIST_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        return true;
    }

    public boolean setEnrollFPWrapper(long contextHandle, int devIndex) {
        m_wrapperType = ENROLL_FP_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        return true;
    }

    public boolean setVerifyFPWrapper(long contextHandle, int devIndex) {
        m_wrapperType = VERIFY_FP_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        return true;
    }

    public boolean setDeleteFPWrapper(long contextHandle, int devIndex, MiddlewareInterface.FingerPrintID[] fpList) {
        m_wrapperType = DELETE_FP_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        m_fpList = fpList;

        return true;
    }

    public boolean setAbortFPWrapper(long contextHandle, int devIndex) {
        m_wrapperType = ABORT_FP_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        return true;
    }

    public boolean setFormatDeviceWrapper(long contextHandle, int devIndex) {
        m_wrapperType = FORMAT_DEV_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        return true;
    }

    public boolean setGenerateSeedWrapper(long contextHandle, int devIndex, int seedLen) {
        m_wrapperType = GEN_SEED_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        m_seedLen = seedLen;

        return true;
    }

    public boolean setETHSignWrapper(long contextHandle, int devIndex, ReentrantLock uiLock, int[] derivePath, byte[] transaction) {
        m_wrapperType = ETH_SIGN_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        m_uiLock = uiLock;
        m_derivePath = derivePath;
        m_trasaction = transaction;

        return true;
    }

    public boolean setEOSSignWrapper(long contextHandle, int devIndex, ReentrantLock uiLock, int[] derivePath, byte[] transaction) {
        m_wrapperType = EOS_SIGN_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        m_uiLock = uiLock;
        m_derivePath = derivePath;
        m_trasaction = transaction;

        return true;
    }

    public boolean setCYBSignWrapper(long contextHandle, int devIndex, ReentrantLock uiLock, int[] derivePath, byte[] transaction) {
        m_wrapperType = CYB_SIGN_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        m_uiLock = uiLock;
        m_derivePath = derivePath;
        m_trasaction = transaction;

        return true;
    }

    public boolean setImportMneWrapper(long contextHandle, int devIndex, String strMnes) {
        m_wrapperType = IMPORT_MNE_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        m_strMnes = strMnes;

        return true;
    }

    public boolean setGetAddressWrapper(long contextHandle, int devIndex, byte coinType, int[] derivePath) {
        m_wrapperType = GET_ADDRESS_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        m_derivePath = derivePath;
        m_coinType = coinType;

        return true;
    }

    public boolean setCalibrateFPWrapper(long contextHandle, int devIndex) {
        m_wrapperType = CALIBRATE_FP_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        return true;
    }

    boolean setGetDevListWrapper(Context activity, String strFilter) {
        m_wrapperType = GET_DEV_LIST_WRAPPER;
        if (strFilter == null) {
            m_strFilter = "";
        } else {
            m_strFilter = strFilter;
        }

        m_activity = activity;

        return true;
    }

    boolean setClearScreenWrapper(long contextHandle, int devIndex) {
        m_wrapperType = CLEAR_SCREEN_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        return true;
    }

    boolean setEOSTxSerializeWrapper(String strEOSTxString) {
        m_wrapperType = EOS_SERIALIZE_WRAPPER;

        m_strEOSTxString = strEOSTxString;

        return true;
    }

    //////////////////////////////////////////////////////////////////////////////////
    @Override
    public void run() {
        Message msg;
        int iRtn;
        String[] strDeviceNames;
        long[] contextHandles;
        int[] devCount;

        int[] fpListCount;
        MiddlewareInterface.FingerPrintID[] fpList;
        MiddlewareInterface.PAEW_DevInfo[] devInfo;
        MiddlewareInterface.PAEW_FWVersion[] fwVersion;
        int devInfoType;
        String[] strMnes;
        int[] checkIndex, checkIndexCount;

        String[] strAddress;
        byte[] signature;
        int[] sigLen;

        byte[] checkCodeData;
        int[] checkCodeLen;

        byte[] seedData;
        int[] seedDataLen;

        byte[] privateKey;
        int[] privateKeyLen;

        byte[] tradeAddress;
        int[] tradeAddressLen;

        byte[] destImageData;
        int[] destImageLen;

        String[] strImageName;
        int[] imageCount;

        byte[] serializeData;
        int[] serializeDataLen;

        switch(m_wrapperType) {
            case INIT_CONTEXT_WITH_DEVNAME_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_INIT_CONTEXT_START;
                msg.sendToTarget();

                contextHandles = new long[1];
                iRtn = MiddlewareInterface.initContextWithDevName(m_activity, m_strDevName, m_heartBeatCallback, contextHandles);

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_INIT_CONTEXT_FINISH;
                msg.obj = new InitContextReturnValue(iRtn, 1, contextHandles[0]);
                msg.sendToTarget();
                break;
            case FREE_CONTEXT_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_FREE_CONTEXT_START;
                msg.sendToTarget();

                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    m_bAborting = true;
                    m_commonLock.lock();
                    iRtn = MiddlewareInterface.freeContext(m_contextHandle);
                    m_bAborting = false;
                    m_commonLock.unlock();
                }

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_FREE_CONTEXT_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case FREE_CONTEXT_AND_SHUT_DOWN_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_FREE_CONTEXT_START;
                msg.sendToTarget();

                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    m_bAborting = true;
                    m_commonLock.lock();
                    iRtn = MiddlewareInterface.freeContextAndShutDown(m_contextHandle);
                    m_bAborting = false;
                    m_commonLock.unlock();
                }

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_FREE_CONTEXT_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case ENROLL_FP_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_ENROLL_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.enrollFP(m_contextHandle, m_devIndex, m_enrollCallback);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_ENROLL_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case GET_FP_LIST_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_FP_LIST_START;
                msg.sendToTarget();

                fpListCount = new int[1];
                fpList = null;

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.getFPList(m_contextHandle, m_devIndex, null, fpListCount);
                    if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        fpList = new MiddlewareInterface.FingerPrintID[fpListCount[0]];
                        iRtn = MiddlewareInterface.getFPList(m_contextHandle, m_devIndex, fpList, fpListCount);
                    }
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_FP_LIST_FINISH;
                msg.obj = new GetFPListReturnValue(iRtn, fpListCount[0], fpList);
                msg.sendToTarget();
                break;
            case CALIBRATE_FP_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CALIBRATE_FP_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.calibrateFP(m_contextHandle, m_devIndex);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CALIBRATE_FP_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case DELETE_FP_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_DELETE_FP_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.deleteFP(m_contextHandle, m_devIndex, m_fpList);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_DELETE_FP_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case VERIFY_FP_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_VERIFYFP_START;
                msg.sendToTarget();

                fpListCount = new int[1];
                fpListCount[0] = 1;
                fpList = new MiddlewareInterface.FingerPrintID[1];

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.verifyFP(m_contextHandle, m_devIndex, m_verifyCallback, fpList, fpListCount);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_VERIFYFP_FINISH;
                msg.obj = new GetFPListReturnValue(iRtn, fpListCount[0], fpList);
                msg.sendToTarget();
                break;
            case GET_DEV_INFO_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_DEV_INFO_START;
                msg.sendToTarget();

                devInfo = new MiddlewareInterface.PAEW_DevInfo[1];

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    devInfoType = MiddlewareInterface.PAEW_DEV_INFOTYPE_COS_VERSION + MiddlewareInterface.PAEW_DEV_INFOTYPE_PIN_STATE +
                            MiddlewareInterface.PAEW_DEV_INFOTYPE_COS_TYPE + MiddlewareInterface.PAEW_DEV_INFOTYPE_CHAIN_TYPE +
                            MiddlewareInterface.PAEW_DEV_INFOTYPE_SN + MiddlewareInterface.PAEW_DEV_INFOTYPE_LIFECYCLE;
                    iRtn = MiddlewareInterface.getDevInfo(m_contextHandle, m_devIndex, devInfoType, devInfo);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_DEV_INFO_FINISH;
                msg.obj = new GetDevInfoReturnValue(iRtn, devInfo[0]);
                msg.sendToTarget();
                break;
            case ABORT_FP_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_ABORT_FP_START;
                msg.sendToTarget();

                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    m_bAborting = true;
                    m_commonLock.lock();
                    iRtn = MiddlewareInterface.abortFP(m_contextHandle, m_devIndex);
                    m_bAborting = false;
                    m_commonLock.unlock();
                }

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_ABORT_FP_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case INIT_PIN_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_INIT_PIN_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.initPIN(m_contextHandle, m_devIndex, m_strPIN, null);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_INIT_PIN_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case CHANGE_PIN_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CHANGE_PIN_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.changePIN(m_contextHandle, m_devIndex, m_strPIN, m_strNewPIN);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CHANGE_PIN_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case VERIFY_PIN_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_VERIFY_PIN_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.verifyPIN(m_contextHandle, m_devIndex, m_strPIN);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_VERIFY_PIN_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case GEN_SEED_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GEN_SEED_START;
                msg.sendToTarget();

                devInfo = new MiddlewareInterface.PAEW_DevInfo[1];
                strMnes = new String[1];
                checkIndex = new int[MiddlewareInterface.PAEW_MNE_INDEX_MAX_COUNT];
                checkIndexCount = new int[1];
                checkIndexCount[0] = MiddlewareInterface.PAEW_MNE_INDEX_MAX_COUNT;
                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.getDevInfo(m_contextHandle, m_devIndex, MiddlewareInterface.PAEW_DEV_INFOTYPE_COS_TYPE, devInfo);
                    if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        if (devInfo[0].ucCOSType == MiddlewareInterface.PAEW_DEV_INFO_COS_TYPE_BIO) {
                            iRtn = MiddlewareInterface.generateSeed_GetMnes(m_contextHandle, m_devIndex, m_seedLen, strMnes, checkIndex, checkIndexCount);
                            if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                                String[] strMneArray = strMnes[0].split(" ");
                                StringBuffer destMnesBuffer = new StringBuffer(0);
                                for (int i = 0; i < checkIndexCount[0]; i++) {
                                    destMnesBuffer.append(strMneArray[checkIndex[i]]);
                                    if (i != (checkIndexCount[0] - 1)) {
                                        destMnesBuffer.append(" ");
                                    }
                                }
                                String strDestMnes = destMnesBuffer.toString();
                                iRtn = MiddlewareInterface.generateSeed_CheckMnes(m_contextHandle, m_devIndex, strDestMnes);
                            }
                        } else {
                            iRtn = MiddlewareInterface.generateSeed(m_contextHandle, m_devIndex, m_seedLen, (byte)0, (byte)0);
                        }
                    }
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GEN_SEED_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case ETH_SIGN_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_ETH_SIGN_START;
                msg.sendToTarget();

                strAddress = new String[1];
                signature = null;
                sigLen = new int[1];

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.deriveTradeAddress(m_contextHandle, m_devIndex, MiddlewareInterface.PAEW_COIN_TYPE_ETH, m_derivePath);
                    if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iRtn = MiddlewareInterface.getTradeAddress(m_contextHandle, m_devIndex, MiddlewareInterface.PAEW_COIN_TYPE_ETH, false, strAddress);
                        if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                            showGetAuthDialog();

                            signature = new byte[MiddlewareInterface.PAEW_ETH_SIG_MAX_LEN];
                            sigLen[0] = MiddlewareInterface.PAEW_ETH_SIG_MAX_LEN;
                            iRtn = MiddlewareInterface.ETHSign(m_contextHandle, m_devIndex, m_signCallback, m_trasaction, signature, sigLen);
                        }
                    }
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_ETH_SIGN_FINISH;
                msg.obj = new SignReturnValue(iRtn, signature, sigLen[0]);
                msg.sendToTarget();
                break;
            case EOS_SIGN_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_EOS_SIGN_START;
                msg.sendToTarget();

                strAddress = new String[1];
                signature = null;
                sigLen = new int[1];

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.deriveTradeAddress(m_contextHandle, m_devIndex, MiddlewareInterface.PAEW_COIN_TYPE_EOS, m_derivePath);
                    if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iRtn = MiddlewareInterface.getTradeAddress(m_contextHandle, m_devIndex, MiddlewareInterface.PAEW_COIN_TYPE_EOS, false, strAddress);
                        if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                            showGetAuthDialog();

                            signature = new byte[MiddlewareInterface.PAEW_EOS_SIG_MAX_LEN];
                            sigLen[0] = MiddlewareInterface.PAEW_EOS_SIG_MAX_LEN;
                            iRtn = MiddlewareInterface.EOSSign(m_contextHandle, m_devIndex, m_signCallback, m_trasaction, signature, sigLen);
                        }
                    }
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_EOS_SIGN_FINISH;
                msg.obj = new SignReturnValue(iRtn, signature, sigLen[0]);
                msg.sendToTarget();
                break;
            case CYB_SIGN_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CYB_SIGN_START;
                msg.sendToTarget();

                strAddress = new String[1];
                signature = null;
                sigLen = new int[1];

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.deriveTradeAddress(m_contextHandle, m_devIndex, MiddlewareInterface.PAEW_COIN_TYPE_CYB, m_derivePath);
                    if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iRtn = MiddlewareInterface.getTradeAddress(m_contextHandle, m_devIndex, MiddlewareInterface.PAEW_COIN_TYPE_CYB, false, strAddress);
                        if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                            showGetAuthDialog();

                            signature = new byte[MiddlewareInterface.PAEW_CYB_SIG_MAX_LEN];
                            sigLen[0] = MiddlewareInterface.PAEW_CYB_SIG_MAX_LEN;
                            iRtn = MiddlewareInterface.CYBSign(m_contextHandle, m_devIndex, m_signCallback, m_trasaction, signature, sigLen);
                        }
                    }
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CYB_SIGN_FINISH;
                msg.obj = new SignReturnValue(iRtn, signature, sigLen[0]);
                msg.sendToTarget();
                break;
            case FORMAT_DEV_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_FORMAT_DEVICE_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.formatDevice(m_contextHandle, m_devIndex);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_FORMAT_DEVICE_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case GET_ADDRESS_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_ADDRESS_START;
                msg.sendToTarget();

                strAddress = new String[1];

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.deriveTradeAddress(m_contextHandle, m_devIndex, m_coinType, m_derivePath);
                    if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        iRtn = MiddlewareInterface.getTradeAddress(m_contextHandle, m_devIndex, m_coinType, true, strAddress);
                    }
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_ADDRESS_FINISH;
                msg.obj = new GetAddressReturnValue(iRtn, strAddress[0], m_coinType);
                msg.sendToTarget();
                break;
            case IMPORT_MNE_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_IMPORT_MNE_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.importMne(m_contextHandle, m_devIndex, m_strMnes);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_IMPORT_MNE_FINISH;
                msg.arg1= iRtn;
                msg.sendToTarget();
                break;
            case GET_DEV_LIST_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_ENUM_START;
                msg.sendToTarget();

                devCount = new int[1];
                devCount[0] = MiddlewareInterface.PAEW_MAX_DEV_COUNT;
                strDeviceNames = new String[MiddlewareInterface.PAEW_MAX_DEV_COUNT];
                //m_commonLock.lock();
                iRtn = MiddlewareInterface.getDeviceList(m_activity, m_strFilter, 20000, m_enumCallback, strDeviceNames, devCount);
                //m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_ENUM_FINISH;
                msg.arg1= iRtn;
                msg.sendToTarget();
                break;
            case CLEAR_SCREEN_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CLEAR_SCREEN_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.clearScreen(m_contextHandle, m_devIndex);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CLEAR_SCREEN_FINISH;
                msg.arg1= iRtn;
                msg.sendToTarget();
                break;
            case EOS_SERIALIZE_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_EOS_SERIALIZE_START;
                msg.sendToTarget();

                serializeData = null;
                serializeDataLen = new int[1];

                m_commonLock.lock();
                iRtn = MiddlewareInterface.eos_tx_serialize(m_strEOSTxString, null, serializeDataLen);
                if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                    serializeData = new byte[serializeDataLen[0]];
                    iRtn = MiddlewareInterface.eos_tx_serialize(m_strEOSTxString, serializeData, serializeDataLen);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_EOS_SERIALIZE_FINISH;
                msg.obj = new EOSTxSerializeReturn(iRtn, serializeData);
                msg.sendToTarget();
                break;
        }
    }
}
