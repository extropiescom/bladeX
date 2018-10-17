package com.extropies.testapp;

import android.app.Activity;
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
    public static final int INIT_WRAPPER = 0;
    public static final int ENUM_WRAPPER = 1;
    public static final int CONNECT_WRAPPER = 2;
    public static final int DISCONNECT_WRAPPER = 3;
    public static final int SEND_CMD_WRAPPER = 4;
    public static final int RECV_CMD_WRAPPER = 5;
    public static final int SEND_AND_RECV_CMD_WRAPPER = 6;
    public static final int AUTO_RECV_CMD_WRAPPER = 7;

    public static final int INIT_CONTEXT_WITH_DEVNAME_WRAPPER = 8;
    public static final int INIT_CONTEXT_WRAPPER = 9;
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
    public static final int GET_CHECK_CODE_WRAPPER = 31;
    public static final int CLEAR_COS_WRAPPER = 32;
    public static final int WRITE_SN_WRAPPER = 33;
    public static final int RECOVER_SEED_WRAPPER = 34;
    public static final int RECOVER_ADDRESS_WRAPPER = 35;
    public static final int SET_IMAGE_DATA_WRAPPER = 36;
    public static final int SHOW_IMAGE_WRAPPER = 37;
    public static final int SET_IMAGE_NAME_WRAPPER = 38;
    public static final int GET_IMAGE_NAME_WRAPPER = 39;
    public static final int SET_LOGO_IMAGE_WRAPPER = 40;
    public static final int GET_IMAGE_COUNT_WRAPPER = 41;
    public static final int EOS_SERIALIZE_WRAPPER = 42;

    //messages
    public static final int MSG_INIT_START = 0;
    public static final int MSG_INIT_FINISH = 1;
    public static final int MSG_ENUM_START = 2;
    public static final int MSG_ENUM_UPDATE = 3;
    public static final int MSG_ENUM_FINISH = 4;
    public static final int MSG_CONNECT_START = 5;
    public static final int MSG_CONNECT_FINISH = 6;
    public static final int MSG_DISCONNECT_START = 7;
    public static final int MSG_DISCONNECT_FINISH = 8;
    public static final int MSG_SEND_CMD_START = 9;
    public static final int MSG_SEND_CMD_FINISH = 10;
    public static final int MSG_RECV_CMD_START = 11;
    public static final int MSG_RECV_CMD_FINISH = 12;

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

    public static final int MSG_GET_CHECK_CODE_START = 61;
    public static final int MSG_GET_CHECK_CODE_FINISH = 62;
    public static final int MSG_CLEAR_COS_START = 63;
    public static final int MSG_CLEAR_COS_FINISH = 64;
    public static final int MSG_WRITE_SN_START = 65;
    public static final int MSG_WRITE_SN_FINISH = 66;
    public static final int MSG_RECOVER_SEED_START = 67;
    public static final int MSG_RECOVER_SEED_FINISH = 68;
    public static final int MSG_RECOVER_ADDRESS_START = 69;
    public static final int MSG_RECOVER_ADDRESS_FINISH = 70;
    public static final int MSG_SET_IMAGE_DATA_START = 71;
    public static final int MSG_SET_IMAGE_DATA_FINISH = 72;
    public static final int MSG_SHOW_IMAGE_START = 73;
    public static final int MSG_SHOW_IMAGE_FINISH = 74;
    public static final int MSG_SET_IMAGE_NAME_START = 75;
    public static final int MSG_SET_IMAGE_NAME_FINISH = 76;
    public static final int MSG_GET_IMAGE_NAME_START = 77;
    public static final int MSG_GET_IMAGE_NAME_FINISH = 78;
    public static final int MSG_SET_LOGO_IMAGE_START = 79;
    public static final int MSG_SET_LOGO_IMAGE_FINISH = 80;
    public static final int MSG_GET_IMAGE_COUNT_START = 81;
    public static final int MSG_GET_IMAGE_COUNT_FINISH = 82;
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
    private Activity m_activity = null; //for INIT_WRAPPER
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

    /*
     * 签名回调接口
     * 签名函数调用回调接口的流程如下：
     * 1. 调用getAuthResult，用户选择签名认证类型的操作结果是确认或是取消。此接口应返回PAEW_RET_SUCCESS或PAEW_RET_DEV_OP_CANCEL。此接口若返回PAEW_RET_SUCCESS，签名函数继续执行，此接口若返回PAEW_RET_DEV_OP_CANCEL，则在返回之前，需要调用abort接口以终止签名流程。
     * 2. 调用getAuthType，获取签名认证类型。
     * 3. 若签名认证类型为PIN认证，则调用getPINResult，用户输入PIN的操作结果是确认或取消。此接口应返回PAEW_RET_SUCCESS或PAEW_RET_DEV_OP_CANCEL。此接口若返回PAEW_RET_SUCCESS，签名函数继续执行，此接口若返回PAEW_RET_DEV_OP_CANCEL，则在返回之前，需要调用abort接口以终止签名流程。
     * 4. 若签名认证类型为PIN认证，且用户输入PIN的结果是确认，则调用getPIN，获取用户输入的PIN码。
     * 5. 使用用户选择的签名认证类型，执行签名操作。
     * 6. 若签名操作返回错误，且之前选择的不是PIN认证，则调用getPINResult，用户输入PIN的操作结果是确认或取消。此接口应返回PAEW_RET_SUCCESS或PAEW_RET_DEV_OP_CANCEL。此接口若返回PAEW_RET_SUCCESS，签名函数继续执行，此接口若返回PAEW_RET_DEV_OP_CANCEL，则在返回之前，需要调用abort接口以终止签名流程。
     * 7. 若签名操作返回错误，且之前选择的不是PIN认证，且用户输入PIN的结果是确认，则调用getPIN，获取用户输入的PIN码。
     * 5. 若签名操作返回错误，且之前选择的不是PIN认证，则使用PIN码认证，执行签名操作。
     *
     * 签名函数的伪代码如下：
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

    boolean setInitWrapper(Activity activity) {
        m_wrapperType = INIT_WRAPPER;
        m_activity = activity;
        return true;
    }

    boolean setEnumWrapper(Activity activity, String strFilter) {
        m_wrapperType = ENUM_WRAPPER;
        if (strFilter == null) {
            m_strFilter = "";
        } else {
            m_strFilter = strFilter;
        }

        m_activity = activity;

        return true;
    }

    boolean setConnectWrapper(Activity activity, String strDevName) {
        m_wrapperType = CONNECT_WRAPPER;
        if (strDevName == null) {
            return false;
        }

        m_activity = activity;

        m_objCommLock = m_listCommLock.get(strDevName);
        if (m_objCommLock == null) {
            m_objCommLock = new Object();
            m_listCommLock.put(strDevName, m_objCommLock);
        }

        m_objPriorityLock = m_listPriorityLock.get(strDevName);
        if (m_objPriorityLock == null) {
            m_objPriorityLock = new Object();
            m_listPriorityLock.put(strDevName, m_objPriorityLock);
        }

        int iOffset = strDevName.indexOf("####");
        if (iOffset == -1) {
            m_strDevName = strDevName;
        } else {
            m_strDevName = strDevName.substring(iOffset + 4);
            if (m_strDevName == null) {
                return false;
            }
        }

        return true;
    }

    boolean setSendCmdWrapper(String strDevName, long devHandle, String strDataString) {
        m_wrapperType = SEND_CMD_WRAPPER;
        if ((strDevName == null) || (strDataString == null)) {
            return false;
        }

        m_objCommLock = m_listCommLock.get(strDevName);
        if (m_objCommLock == null) {
            m_objCommLock = new Object();
            m_listCommLock.put(strDevName, m_objCommLock);
        }

        m_objPriorityLock = m_listPriorityLock.get(strDevName);
        if (m_objPriorityLock == null) {
            m_objPriorityLock = new Object();
            m_listPriorityLock.put(strDevName, m_objPriorityLock);
        }

        int iOffset = strDevName.indexOf("####");
        if (iOffset == -1) {
            m_strDevName = strDevName;
        } else {
            m_strDevName = strDevName.substring(iOffset + 4);
            if (m_strDevName == null) {
                return false;
            }
        }

        m_devHandle = devHandle;

        m_sendData = CommonUtility.hexStringToUnsignByte(strDataString);

        return true;
    }

    boolean setRecvCmdWrapper(String strDevName, long devHandle) {
        m_wrapperType = RECV_CMD_WRAPPER;
        if (strDevName == null) {
            return false;
        }

        m_objCommLock = m_listCommLock.get(strDevName);
        if (m_objCommLock == null) {
            m_objCommLock = new Object();
            m_listCommLock.put(strDevName, m_objCommLock);
        }

        m_objPriorityLock = m_listPriorityLock.get(strDevName);
        if (m_objPriorityLock == null) {
            m_objPriorityLock = new Object();
            m_listPriorityLock.put(strDevName, m_objPriorityLock);
        }

        int iOffset = strDevName.indexOf("####");
        if (iOffset == -1) {
            m_strDevName = strDevName;
        } else {
            m_strDevName = strDevName.substring(iOffset + 4);
            if (m_strDevName == null) {
                return false;
            }
        }

        m_devHandle = devHandle;

        return true;
    }

    boolean setAutoRecvCmdWrapper(String strDevName, long devHandle, String strDataString) {
        m_wrapperType = AUTO_RECV_CMD_WRAPPER;
        if (strDevName == null) {
            return false;
        }

        m_objCommLock = m_listCommLock.get(strDevName);
        if (m_objCommLock == null) {
            m_objCommLock = new Object();
            m_listCommLock.put(strDevName, m_objCommLock);
        }

        m_objPriorityLock = m_listPriorityLock.get(strDevName);
        if (m_objPriorityLock == null) {
            m_objPriorityLock = new Object();
            m_listPriorityLock.put(strDevName, m_objPriorityLock);
        }

        int iOffset = strDevName.indexOf("####");
        if (iOffset == -1) {
            m_strDevName = strDevName;
        } else {
            m_strDevName = strDevName.substring(iOffset + 4);
        }

        m_devHandle = devHandle;

        m_sendData = CommonUtility.hexStringToUnsignByte(strDataString);

        m_bAutoSendData = true;
        return true;
    }
    boolean stopAutoRecvCmd() {
        m_bAutoSendData = false;
        return true;
    }

    boolean setSendAndRecvCmdWrapper(String strDevName, long devHandle, String strDataString) {
        m_wrapperType = SEND_AND_RECV_CMD_WRAPPER;
        if (strDataString == null) {
            return false;
        }

        m_objCommLock = m_listCommLock.get(strDevName);
        if (m_objCommLock == null) {
            m_objCommLock = new Object();
            m_listCommLock.put(strDevName, m_objCommLock);
        }

        m_objPriorityLock = m_listPriorityLock.get(strDevName);
        if (m_objPriorityLock == null) {
            m_objPriorityLock = new Object();
            m_listPriorityLock.put(strDevName, m_objPriorityLock);
        }
        m_bPriority = true;

        int iOffset = strDevName.indexOf("####");
        if (iOffset == -1) {
            m_strDevName = strDevName;
        } else {
            m_strDevName = strDevName.substring(iOffset + 4);
        }

        m_devHandle = devHandle;

        m_sendData = CommonUtility.hexStringToUnsignByte(strDataString);

        return true;
    }

    public boolean setInitContextWrapper(Activity activity, String strFilter) {
        m_wrapperType = INIT_CONTEXT_WRAPPER;

        if (strFilter == null) {
            m_strFilter = "";
        } else {
            m_strFilter = strFilter;
        }

        m_activity = activity;

        return true;
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

    boolean setGetDevListWrapper(Activity activity, String strFilter) {
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

    boolean setGetCheckCodeWrapper(long contextHandle, int devIndex) {
        m_wrapperType = GET_CHECK_CODE_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        return true;
    }

    boolean setClearCOSWrapper(long contextHandle, int devIndex) {
        m_wrapperType = CLEAR_COS_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;
        return true;
    }

    boolean setWriteSNWrapper(long contextHandle, int devIndex, String strSerialNumber) {
        m_wrapperType = WRITE_SN_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        m_strSerialNumber = strSerialNumber;
        return true;
    }

    boolean setRecoverSeedWrapper(String strMnes) {
        m_wrapperType = RECOVER_SEED_WRAPPER;

        m_strMnes = strMnes;
        return true;
    }

    boolean setRecoverAddressWrapper(byte coinType, byte[] seedData, int[] derivePath) {
        m_wrapperType = RECOVER_ADDRESS_WRAPPER;

        m_coinType = coinType;
        m_seedData = seedData;
        m_derivePath = derivePath;
        return true;
    }

    boolean setSetImageData(long contextHandle, int devIndex, byte imageIndex, byte[] imageData, int width, int height) {
        m_wrapperType = SET_IMAGE_DATA_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        m_imageIndex = imageIndex;
        m_imageData = imageData;
        m_imageWidth = width;
        m_imageHeight = height;

        return true;
    }

    boolean setShowImageWrapper(long contextHandle, int devIndex, byte imageIndex, byte showMode) {
        m_wrapperType = SHOW_IMAGE_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        m_imageIndex = imageIndex;
        m_showMode = showMode;

        return true;
    }

    boolean setSetImageNameWrapper(long contextHandle, int devIndex, byte imageIndex, String strImageName) {
        m_wrapperType = SET_IMAGE_NAME_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        m_imageIndex = imageIndex;
        m_strImageName = strImageName;

        return true;
    }

    boolean setGetImageNameWrapper(long contextHandle, int devIndex, byte imageIndex) {
        m_wrapperType = GET_IMAGE_NAME_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        m_imageIndex = imageIndex;

        return true;
    }

    boolean setSetLogoImageWrapper(long contextHandle, int devIndex, byte imageIndex) {
        m_wrapperType = SET_LOGO_IMAGE_WRAPPER;

        m_contextHandle = contextHandle;
        m_devIndex = devIndex;

        m_imageIndex = imageIndex;

        return true;
    }

    boolean setGetImageCountWrapper(long contextHandle, int devIndex) {
        m_wrapperType = GET_IMAGE_COUNT_WRAPPER;

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
    private int sendCmd() {
        Message msg;
        int iRtn;

        msg = m_mainHandler.obtainMessage();
        msg.what = MSG_SEND_CMD_START;
        msg.sendToTarget();

        Log.v("com.extropies.testapp", "SEND_CMD" + " Time: " + System.currentTimeMillis() + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        iRtn = MiddlewareInterface.sendCommand(MiddlewareInterface.BT_CMD_DIRECTION_OUT, m_devHandle, m_sendData, null, null);

        msg = m_mainHandler.obtainMessage();
        msg.obj = iRtn;
        msg.what = MSG_SEND_CMD_FINISH;
        msg.sendToTarget();

        return iRtn;
    }

    private int recvCmd() {
        Message msg;
        int iRtn;

        msg = m_mainHandler.obtainMessage();
        msg.what = MSG_RECV_CMD_START;
        msg.sendToTarget();

        int[] recvLen = new int[1];
        byte[] recvDataBuffer = new byte[1024];
        byte[] recvData = null;

        iRtn = MiddlewareInterface.sendCommand(MiddlewareInterface.BT_CMD_DIRECTION_IN, m_devHandle, null, recvDataBuffer, recvLen);
        if (iRtn == CommonUtility.DEVICEIO_RET_OK) {
            recvData = new byte[recvLen[0]];
            System.arraycopy(recvDataBuffer, 0, recvData, 0, recvLen[0]);
        }
        Log.v("com.extropies.testapp", "RECV_CMD" + " Time: " + System.currentTimeMillis() + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        msg = m_mainHandler.obtainMessage();
        msg.obj = new RecvReturnValue(recvData, iRtn, null);
        msg.what = MSG_RECV_CMD_FINISH;
        msg.sendToTarget();

        return iRtn;
    }

    private int sendAndRecvCmd() {
        Message msg;
        int iRtn;

        msg = m_mainHandler.obtainMessage();
        msg.what = MSG_SEND_CMD_START;
        msg.sendToTarget();

        msg = m_mainHandler.obtainMessage();
        msg.what = MSG_RECV_CMD_START;
        msg.sendToTarget();

        int[] recvLen = new int[1];
        byte[] recvDataBuffer = new byte[1024];
        byte[] recvData = null;
        long t1, t2;

        t1 = System.currentTimeMillis();
        Log.v("com.extropies.testapp", "SEND_AND_RECV_CMD START" + " Time: " + t1 + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        iRtn = MiddlewareInterface.sendCommand(MiddlewareInterface.BT_CMD_DIRECTION_OUTIN, m_devHandle, m_sendData, recvDataBuffer, recvLen);
        if (iRtn == CommonUtility.DEVICEIO_RET_OK) {
            recvData = new byte[recvLen[0]];
            System.arraycopy(recvDataBuffer, 0, recvData, 0, recvLen[0]);
        }
        t2 = System.currentTimeMillis();
        Log.v("com.extropies.testapp", "SEND_AND_RECV_CMD END" + " Time: " + t2 + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        msg = m_mainHandler.obtainMessage();
        msg.obj = iRtn;
        msg.what = MSG_SEND_CMD_FINISH;
        msg.sendToTarget();

        msg = m_mainHandler.obtainMessage();
        msg.obj = new RecvReturnValue(recvData, iRtn, "" + (t2 - t1) + "ms");
        msg.what = MSG_RECV_CMD_FINISH;
        msg.sendToTarget();

        return iRtn;
    }

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
            case INIT_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_INIT_START;
                msg.sendToTarget();

                //m_btUtility.initUtility(m_activity);

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_INIT_FINISH;
                msg.sendToTarget();
                break;
            case ENUM_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_ENUM_START;
                msg.sendToTarget();

                /*
                iRtn = m_btUtility.enumDevice(m_strFilter, m_enumCallback);
                msg = m_mainHandler.obtainMessage();
                if (iRtn == CommonUtility.DEVICEIO_RET_OK) {
                    strDeviceNames = m_btUtility.getEnumResult();
                    for (int i = 0; i < strDeviceNames.length; i++) {
                        strDeviceNames[i] = strDeviceNames[i] + "####" + m_btUtility.getDeviceAddress(strDeviceNames[i]);
                    }
                    msg.obj = strDeviceNames;
                }
                msg.what = MSG_ENUM_FINISH;
                msg.sendToTarget();
                */

                devCount = new int[1];
                String[] strMaxDeviceNames = new String[16];
                iRtn = MiddlewareInterface.enumDevice(m_activity, m_strFilter, m_enumCallback, strMaxDeviceNames, devCount);
                if ((iRtn == CommonUtility.DEVICEIO_RET_OK) && (devCount[0] > 0)) {
                    strDeviceNames = new String[devCount[0]];
                    System.arraycopy(strMaxDeviceNames, 0, strDeviceNames, 0, devCount[0]);
                } else {
                    strDeviceNames = null;
                }

                msg = m_mainHandler.obtainMessage();
                msg.obj = strDeviceNames;
                msg.what = MSG_ENUM_FINISH;
                msg.sendToTarget();
                break;
            case CONNECT_WRAPPER:
                synchronized (m_objCommLock) {
                    msg = m_mainHandler.obtainMessage();
                    msg.what = MSG_CONNECT_START;
                    msg.sendToTarget();

                    long[] devHandle = new long[1];
                    iRtn = MiddlewareInterface.connectDevice(m_activity, m_strDevName, devHandle);

                    msg = m_mainHandler.obtainMessage();
                    msg.what = MSG_CONNECT_FINISH;
                    msg.obj = new ConnectReturnValue(m_strDevName, devHandle[0], iRtn);
                    msg.sendToTarget();
                }
                break;
            case SEND_CMD_WRAPPER:
                sendCmd();
                break;
            case RECV_CMD_WRAPPER:
                recvCmd();
                break;
            case SEND_AND_RECV_CMD_WRAPPER:
                if (m_bPriority) {
                    synchronized (m_objPriorityLock) {
                        synchronized (m_objCommLock) {
                            //sendCmd();
                            //recvCmd();
                            sendAndRecvCmd();
                        }
                    }
                } else {
                    synchronized (m_objCommLock) {
                        //sendCmd();
                        //recvCmd();
                        sendAndRecvCmd();
                    }
                }
                break;
            case AUTO_RECV_CMD_WRAPPER:
                while(m_bAutoSendData) {
                    synchronized (m_objPriorityLock) {
                        m_bAutoSendData = m_bAutoSendData;
                    }
                    synchronized (m_objCommLock) {
                        //iRtn = sendCmd();
                        //if (iRtn != CommonUtility.DEVICEIO_RET_OK) {
                        //    break;
                       // }
                        //iRtn = recvCmd();
                        //if (iRtn != CommonUtility.DEVICEIO_RET_OK) {
                        //    break;
                        //}
                        iRtn = sendAndRecvCmd();
                        if (iRtn != CommonUtility.DEVICEIO_RET_OK) {
                            break;
                        }
                    }
                }
                break;
            case DISCONNECT_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_DISCONNECT_START;
                msg.sendToTarget();

                MiddlewareInterface.disconnectDevice(m_devHandle);

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_DISCONNECT_FINISH;
                msg.sendToTarget();
                break;
            case INIT_CONTEXT_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_INIT_CONTEXT_START;
                msg.sendToTarget();

                contextHandles = new long[1];
                devCount = new int[1];
                iRtn = MiddlewareInterface.initContext(m_activity, m_strFilter, 4000, m_enumCallback, devCount, contextHandles);

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_INIT_CONTEXT_FINISH;
                msg.obj = new InitContextReturnValue(iRtn, devCount[0], contextHandles[0]);
                msg.sendToTarget();
                break;
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
                    iRtn = MiddlewareInterface.initPIN(m_contextHandle, m_devIndex, m_strPIN);
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
                m_commonLock.lock();
                iRtn = MiddlewareInterface.getDeviceList(m_activity, m_strFilter, 4000, m_enumCallback, strDeviceNames, devCount);
                m_commonLock.unlock();

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
            case GET_CHECK_CODE_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_CHECK_CODE_START;
                msg.sendToTarget();

                checkCodeLen = new int[1];
                checkCodeData = null;

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.getDeviceCheckCode(m_contextHandle, m_devIndex, null, checkCodeLen);
                    if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        checkCodeData = new byte[checkCodeLen[0]];
                        iRtn = MiddlewareInterface.getDeviceCheckCode(m_contextHandle, m_devIndex, checkCodeData, checkCodeLen);
                    }
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_CHECK_CODE_FINISH;
                msg.obj= new GetCheckCodeReturnValue(iRtn, checkCodeData);
                msg.sendToTarget();
                break;
            case CLEAR_COS_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CLEAR_COS_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.clearCOS(m_contextHandle, m_devIndex);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_CLEAR_COS_FINISH;
                msg.arg1= iRtn;
                msg.sendToTarget();
                break;
            case WRITE_SN_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_WRITE_SN_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.writeSN(m_contextHandle, m_devIndex, m_strSerialNumber);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_WRITE_SN_FINISH;
                msg.arg1= iRtn;
                msg.sendToTarget();
                break;
            case RECOVER_SEED_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_RECOVER_SEED_START;
                msg.sendToTarget();

                m_commonLock.lock();
                seedData = null;
                seedDataLen = new int[1];
                iRtn = MiddlewareInterface.recoverSeedFromMne(m_strMnes, null, seedDataLen);
                if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                    seedData = new byte[seedDataLen[0]];
                    iRtn = MiddlewareInterface.recoverSeedFromMne(m_strMnes, seedData, seedDataLen);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_RECOVER_SEED_FINISH;
                msg.obj = new RecoverSeedReturnValue(iRtn, seedData);
                msg.sendToTarget();
                break;
            case RECOVER_ADDRESS_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_RECOVER_ADDRESS_START;
                msg.sendToTarget();

                m_commonLock.lock();
                privateKey = null;
                privateKeyLen = new int[1];
                tradeAddress = null;
                tradeAddressLen = new int[1];
                iRtn = MiddlewareInterface.getTradeAddressFromSeed(m_coinType, m_seedData, m_derivePath, null, privateKeyLen, null, tradeAddressLen);
                if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                    privateKey = new byte[privateKeyLen[0]];
                    tradeAddress = new byte[MiddlewareInterface.PAEW_COIN_ADDRESS_MAX_LEN];
                    iRtn = MiddlewareInterface.getTradeAddressFromSeed(m_coinType, m_seedData, m_derivePath, privateKey, privateKeyLen, tradeAddress, tradeAddressLen);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_RECOVER_ADDRESS_FINISH;
                msg.obj = new RecoverAddressReturnValue(iRtn, privateKey, tradeAddress);
                msg.sendToTarget();
                break;
            case SET_IMAGE_DATA_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_SET_IMAGE_DATA_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    destImageData = null;
                    destImageLen = new int[1];
                    iRtn = MiddlewareInterface.covertBMP(m_imageData, m_imageWidth, m_imageHeight, null, destImageLen);
                    if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                        destImageData = new byte[destImageLen[0]];
                        iRtn = MiddlewareInterface.covertBMP(m_imageData, m_imageWidth, m_imageHeight, destImageData, destImageLen);
                        if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
                            iRtn = MiddlewareInterface.setImageData(m_contextHandle, m_devIndex, m_imageIndex, destImageData);
                        }
                    }
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_SET_IMAGE_DATA_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case SHOW_IMAGE_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_SHOW_IMAGE_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.showImage(m_contextHandle, m_devIndex, m_imageIndex, m_showMode);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_SHOW_IMAGE_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case SET_IMAGE_NAME_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_SET_IMAGE_NAME_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.setImageName(m_contextHandle, m_devIndex, m_imageIndex, m_strImageName);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_SET_IMAGE_NAME_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case GET_IMAGE_NAME_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_IMAGE_NAME_START;
                msg.sendToTarget();

                strImageName = new String[1];

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.getImageName(m_contextHandle, m_devIndex, m_imageIndex, strImageName);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_IMAGE_NAME_FINISH;
                msg.obj = new GetImageNameReturnValue(iRtn, strImageName[0]);
                msg.sendToTarget();
                break;
            case SET_LOGO_IMAGE_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_SET_LOGO_IMAGE_START;
                msg.sendToTarget();

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.setLogoImage(m_contextHandle, m_devIndex, m_imageIndex);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_SET_LOGO_IMAGE_FINISH;
                msg.arg1 = iRtn;
                msg.sendToTarget();
                break;
            case GET_IMAGE_COUNT_WRAPPER:
                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_IMAGE_COUNT_START;
                msg.sendToTarget();

                imageCount = new int[1];

                m_commonLock.lock();
                if (m_contextHandle == 0) {
                    iRtn = MiddlewareInterface.PAEW_RET_DEV_COMMUNICATE_FAIL;
                } else {
                    iRtn = MiddlewareInterface.getImageCount(m_contextHandle, m_devIndex, imageCount);
                }
                m_commonLock.unlock();

                msg = m_mainHandler.obtainMessage();
                msg.what = MSG_GET_IMAGE_COUNT_FINISH;
                msg.obj = new GetImageCountReturnValue(iRtn, imageCount[0]);
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
