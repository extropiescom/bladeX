#ifndef _PA_EWALLET_H_
#define _PA_EWALLET_H_

#include <stdint.h>

#ifndef _WIN32
#include <stddef.h> //for size_t
#endif //_WIN32

#define PAEW_RET_SUCCESS					0x00000000 //success
#define PAEW_RET_UNKNOWN_FAIL				0x80000001 //unknown failure
#define PAEW_RET_ARGUMENTBAD				0x80000002 //argument bad
#define PAEW_RET_HOST_MEMORY				0x80000003 //malloc memory failed

#define PAEW_RET_DEV_ENUM_FAIL				0x80000004 //enum device failed
#define PAEW_RET_DEV_OPEN_FAIL				0x80000005 //open device failed
#define PAEW_RET_DEV_COMMUNICATE_FAIL		0x80000006 //communicate failed
#define PAEW_RET_DEV_NEED_PIN				0x80000007 //device need user input pin to "unlock"
#define PAEW_RET_DEV_OP_CANCEL				0x80000008 //operation canceled
#define PAEW_RET_DEV_KEY_NOT_RESTORED		0x80000009 //operation need seed restored while current state is not restored
#define PAEW_RET_DEV_KEY_ALREADY_RESTORED	0x8000000A //seed already restored
#define PAEW_RET_DEV_COUNT_BAD				0x8000000B //errors such as no device, or device count must equal to N when init, device count must >=T and <=N when restore or sign
#define PAEW_RET_DEV_RETDATA_INVALID		0x8000000C //received data length less than 2 or ret data structure invalid
#define PAEW_RET_DEV_AUTH_FAIL				0x8000000D
#define PAEW_RET_DEV_STATE_INVALID			0x8000000E //life cycle or other device state not matched to current operation
#define PAEW_RET_DEV_WAITING				0x8000000F
#define PAEW_RET_DEV_COMMAND_INVALID		0x80000010 //command can not recognized by device
#define PAEW_RET_DEV_RUN_COMMAND_FAIL		0x80000011 //received data not 9000
#define PAEW_RET_DEV_HANDLE_INVALID			0x80000012
#define PAEW_RET_COS_TYPE_INVALID			0x80000013 //device cos type value must be PAEW_DEV_INFO_COS_TYPE_XXX
#define PAEW_RET_COS_TYPE_NOT_MATCH			0x80000014 //device cos type not matched to current operation, such as dragon ball spec function calls personal e-wallet, or passed argument implies specific cos type while current cos type not match, or current insert devices' types are not the same
#define PAEW_RET_DEV_BAD_SHAMIR_SPLIT		0x80000015
#define PAEW_RET_DEV_NOT_ONE_GROUP			0x80000016 //dragon ball device is not belong to one group

#define PAEW_RET_BUFFER_TOO_SAMLL			0x80000017 //size of input buffer not enough to store return data
#define PAEW_RET_TX_PARSE_FAIL				0x80000018 //input transaction parse failed
#define PAEW_RET_TX_UTXO_NEQ				0x80000019 //count of input and UTXO is not equal
#define PAEW_RET_TX_INPUT_TOO_MANY			0x8000001A //input count shouldn't larger than 100

#define PAEW_RET_MUTEX_ERROR				0x8000001B //mutex error, such as create/free/lock/unlock
#define PAEW_RET_COIN_TYPE_INVALID			0x8000001C //value of coin type must be PAEW_COIN_TYPE_XXX
#define PAEW_RET_COIN_TYPE_NOT_MATCH		0x8000001D //value of coin type must be equal to the value passed to PAEW_DeriveTradeAddress
#define PAEW_RET_DERIVE_PATH_INVALID		0x8000001E //derive path must start by 0x00000000, indicates m
#define PAEW_RET_NOT_SUPPORTED				0x8000001F
#define PAEW_RET_INTERNAL_ERROR				0x80000020 //library internal errors, such as internal structure definition mistake
#define PAEW_RET_BAD_N_T					0x80000021 //value of N or T is invalid
#define PAEW_RET_TARGET_DEV_INVALID			0x80000022 //when getting address or signing, dragon ball must select a target device by calling PAEW_DeriveTradeAddress successfully first
#define PAEW_RET_CRYPTO_ERROR				0x80000023
#define PAEW_RET_DEV_TIMEOUT				0x80000024 //device time out
#define PAEW_RET_DEV_PIN_LOCKED				0x80000025 //pin locked
#define PAEW_RET_DEV_PIN_CONFIRM_FAIL		0x80000026 //set new pin error when confirm
#define PAEW_RET_DEV_PIN_VERIFY_FAIL		0x80000027 //input pin error when change pin or other operation
#define PAEW_RET_DEV_CHECKDATA_FAIL			0x80000028 //input data check failed in device
#define PAEW_RET_DEV_DEV_OPERATING			0x80000029 //user is operating device, please wait
#define PAEW_RET_DEV_PIN_UNINIT				0x8000002A
#define PAEW_RET_DEV_BUSY					0x8000002B //if enroll or verify fp is called twice when the first process is not over
#define PAEW_RET_DEV_ALREADY_AVAILABLE		0x8000002C //if abort when no operation is processing
#define PAEW_RET_DEV_DATA_NOT_FOUND			0x8000002D
#define PAEW_RET_DEV_SENSOR_ERROR			0x8000002E
#define PAEW_RET_DEV_STORAGE_ERROR			0x8000002F
#define PAEW_RET_DEV_STORAGE_FULL			0x80000030

#define PAEW_RET_DEV_FP_COMMON_ERROR		0x80000031
#define PAEW_RET_DEV_FP_REDUNDANT			0x80000032
#define PAEW_RET_DEV_FP_GOOG_FINGER			0x80000033
#define PAEW_RET_DEV_FP_NO_FINGER			0x80000034
#define PAEW_RET_DEV_FP_NOT_FULL_FINGER		0x80000035
#define PAEW_RET_DEV_FP_BAD_IMAGE			0x80000036

#define PAEW_RET_DEV_LOW_POWER				0x80000037
#define PAEW_RET_DEV_TYPE_INVALID			0x80000038

#define PAEW_RET_NO_VERIFY_COUNT			0x80000039
#define PAEW_RET_AUTH_CANCEL				0x8000003A
#define PAEW_RET_PIN_LEN_ERROR				0x8000003B

#if (defined(_WIN32) && defined(_USRDLL))

#ifdef _EWALLET_DLL_
#define EWALLET_API	__declspec(dllexport)
#else //_EWALLET_DLL_
#define EWALLET_API
#endif //_EWALLET_DLL_

#else //_WIN32

#define EWALLET_API

#endif //_WIN32

#ifdef __cplusplus
extern "C"
{
#endif

typedef enum _process_step
{
	pstep_invalid,

	pstep_comm_enum_dev,
	pstep_comm_open_dev,
	pstep_comm_close_dev,
	pstep_comm_get_devinfo,
	pstep_comm_dev_select,

	pstep_init_seed_gen,
	pstep_init_mne_show,
	pstep_init_mne_confirm,
	pstep_init_seed_import,
	pstep_init_seed_import_comfirm,

	pstep_init_keypair_gen,
	pstep_init_key_agree_init,
	pstep_init_key_agree_update,
	pstep_init_key_agree_final,
	pstep_init_key_agree_show,
	pstep_init_key_agree_confirm,

	pstep_init_shamir_transmit_init,
	pstep_init_shamir_export,
	pstep_init_shamir_import,
	pstep_init_shamir_confirm,

	pstep_sig_output_data,
	pstep_sig_confirm,

	pstep_comm_addr_gen,
	pstep_comm_shamir_transmit_init,
	pstep_comm_shamir_export,
	pstep_comm_shamir_import,

	pstep_comm_addr_get,
	pstep_comm_addr_confirm,

	pstep_comm_format,
	pstep_comm_format_confirm,

	pstep_comm_clearcos,
	pstep_comm_clearcos_confirm,

	pstep_comm_updatecos,

	pstep_comm_changepin,
	pstep_comm_changepin_confirm,

	pstep_comm_addr_info_get,
	pstep_comm_addr_info_set,

	pstep_comm_erc20_info_set,

	pstep_init_mne_check,
	pstep_init_pin_init,

	pstep_comm_pin_verify,

	pstep_comm_fp_getlist,
	pstep_comm_fp_enroll,
	pstep_comm_fp_verify,
	pstep_comm_fp_getstate,
	pstep_comm_fp_abort,
	pstep_comm_fp_delete,
	pstep_comm_fp_calibrate,
} process_step;

typedef enum _process_status
{
	pstatus_invalid,
	pstatus_start,
	pstatus_finish,
} process_status;

#define INVALID_DEV_INDEX			((size_t)(-1))
#define PAEW_CALLBACK_DATA_MAX_LEN	(1024)

#pragma pack(1)
typedef struct _callback_data_addr_get
{
	unsigned char	nCoinType;
	size_t			nAddressLen;
	unsigned char	pbAddressData[PAEW_CALLBACK_DATA_MAX_LEN - sizeof(size_t) - 1];
} callback_data_addr_get;
typedef struct _callback_data_cos_update
{
	uint32_t		nProgress;
	unsigned char	pbUnused[PAEW_CALLBACK_DATA_MAX_LEN - sizeof(uint32_t)];
} callback_data_cos_update;
#pragma pack()

typedef struct _callback_param
{
	process_step	pstep;
	process_status	pstatus;
	int				ret_value; //when pstatus==pstatus_finish, check this value

	size_t			dev_index; //current main device index, from 0 to dev_count-1, not valid in pstep_comm_enum_dev

	//user define data
	size_t			dev_count; //valid in pstep_comm_enum_dev

	unsigned char	data[PAEW_CALLBACK_DATA_MAX_LEN]; //store addr or other informations
} callback_param;

typedef int(*tFunc_Proc_Callback)(callback_param * const pCallbackParam);

typedef int(*tFunc_GetAuthType)(void * const pCallbackContext, unsigned char * const pnAuthType); //*pnAuthType should be PAEW_SIGN_AUTH_TYPE_XXX when successfully returned
typedef int(*tFunc_GetPIN)(void * const pCallbackContext, unsigned char * const pbPIN, size_t * const pnPINLen);
typedef int(*tFunc_PutSignState)(void * const pCallbackContext, const int nSignState);
typedef struct _signCallbacks {
	tFunc_GetAuthType	getAuthType;
	tFunc_GetPIN		getPIN;
	tFunc_PutSignState	putSignState;
} signCallbacks;

//==============dev info=============
//dev info type
#define PAEW_DEV_INFOTYPE_PIN_STATE		0x00000001
#define PAEW_DEV_INFOTYPE_COS_TYPE		0x00000002
#define PAEW_DEV_INFOTYPE_CHAIN_TYPE	0x00000004
#define PAEW_DEV_INFOTYPE_SN			0x00000008
#define PAEW_DEV_INFOTYPE_COS_VERSION	0x00000010
#define PAEW_DEV_INFOTYPE_LIFECYCLE		0x00000020
#define PAEW_DEV_INFOTYPE_SESSKEY_HASH	0x00000040
#define PAEW_DEV_INFOTYPE_N_T			0x00000080
#define PAEW_DEV_INFOTYPE_LCD_STATE		0x00000100

//pin state
#define PAEW_DEV_INFO_PIN_INVALID_STATE		0xFF
#define PAEW_DEV_INFO_PIN_LOGOUT			0x00
#define PAEW_DEV_INFO_PIN_LOGIN				0x01
#define PAEW_DEV_INFO_PIN_LOCKED			0x02
#define PAEW_DEV_INFO_PIN_UNSET				0x03
//chain type
#define PAEW_DEV_INFO_CHAIN_TYPE_FORMAL		0x01
#define PAEW_DEV_INFO_CHAIN_TYPE_TEST		0x02
//sn
#define PAEW_DEV_INFO_SN_LEN				0x20
//cos version
//1st byte means cos architecture, usually essential cos upgrade
//2nd byte means cos type, currently 00 dragon ball, 01 personal wallet
//3rd and 4th bytes means minor version
#define PAEW_DEV_INFO_COS_VERSION_LEN		0x04
#define PAEW_DEV_INFO_COS_TYPE_INDEX		0x01
#define PAEW_DEV_INFO_COS_TYPE_INVALID		0xFF
#define PAEW_DEV_INFO_COS_TYPE_DRAGONBALL	0x00
#define PAEW_DEV_INFO_COS_TYPE_PERSONAL		0x01
#define PAEW_DEV_INFO_COS_TYPE_BIO			0x02
//life cycle
#define PAEW_DEV_INFO_LIFECYCLE_INVALID		0xFF
#define PAEW_DEV_INFO_LIFECYCLE_AGREE		0x01
#define PAEW_DEV_INFO_LIFECYCLE_USER		0x02
#define PAEW_DEV_INFO_LIFECYCLE_PRODUCE		0x04
//session key hash
#define PAEW_DEV_INFO_SESSKEY_HASH_LEN		0x04
//n/t
#define PAEW_DEV_INFO_N_T_INVALID			0xFF
//LCD State
#define PAEW_DEV_INFO_LCD_NULL				0x00000000
#define PAEW_DEV_INFO_LCD_SHOWLOGO			0x00000001
#define PAEW_DEV_INFO_LCD_WAITTING			0x00000002
#define PAEW_DEV_INFO_LCD_SHOWOK			0x00000004
#define PAEW_DEV_INFO_LCD_SHOWCANCEL		0x00000008
#define PAEW_DEV_INFO_LCD_SHOWSKEYHASH		0x00000010
#define PAEW_DEV_INFO_LCD_SHOWADDRESS		0x00000020
#define PAEW_DEV_INFO_LCD_SHOWBTCSIGN		0x00000040
#define PAEW_DEV_INFO_LCD_SHOWETHSIGN		0x00000080
#define PAEW_DEV_INFO_LCD_SETNEWPIN			0x00000100
#define PAEW_DEV_INFO_LCD_CHANGEPIN			0x00000200
#define PAEW_DEV_INFO_LCD_VERIFYPIN			0x00000400
#define PAEW_DEV_INFO_LCD_PINLOCKED			0x00000800
#define PAEW_DEV_INFO_LCD_FORMAT			0x00001000
#define PAEW_DEV_INFO_LCD_REBOOT			0x00002000
#define PAEW_DEV_INFO_LCD_SHOWBIP39			0x00004000
#define PAEW_DEV_INFO_LCD_CHECKBIP39		0x00008000
#define PAEW_DEV_INFO_LCD_SHOWBTSSIGN		0x00010000
#define PAEW_DEV_INFO_LCD_PINERROR 			0x00020000
#define PAEW_DEV_INFO_LCD_SELECT_MNENUM		0x00040000
#define PAEW_DEV_INFO_LCD_SHOWM				0x00080000
#define PAEW_DEV_INFO_LCD_SHOWTIMEOUT		0x00100000
#define PAEW_DEV_INFO_LCD_SHOWEOSSIGN		0x00200000
#define PAEW_DEV_INFO_LCD_SHOWFAIL			0x00400000
#define PAEW_DEV_INFO_LCD_SHOWNEOSIGN		0x00800000
#define PAEW_DEV_INFO_LCD_WAITING_TIMEOUT	0x01000000
#define PAEW_DEV_INFO_LCD_GET_MNENUM		0x02000000
#define PAEW_DEV_INFO_LCD_GETMNE_BYDEV		0x04000000 

typedef struct _PAEW_DevInfo
{
	unsigned char	ucPINState; //PAEW_DEV_INFO_PIN_XX
	unsigned char	ucCOSType; //PAEW_DEV_INFO_COS_TYPE_XXX
	unsigned char	ucChainType; //PAEW_DEV_INFO_CHAIN_TYPE_XXX
	unsigned char	pbSerialNumber[PAEW_DEV_INFO_SN_LEN];
	unsigned char	pbCOSVersion[PAEW_DEV_INFO_COS_VERSION_LEN];
	unsigned char	ucLifeCycle; // PAEW_DEV_INFO_LIFECYCLE_XXX
	uint64_t		nLcdState; // PAEW_DEV_INFO_LCD_XXX

	//dragon ball device info
	unsigned char	pbSessKeyHash[PAEW_DEV_INFO_SESSKEY_HASH_LEN];
	uint8_t			nN;
	uint8_t			nT;
} PAEW_DevInfo;

//coin type
#define PAEW_COIN_TYPE_INVALID	0xFF
#define PAEW_COIN_TYPE_BTC		0x00 //bit coin P2PKH
#define PAEW_COIN_TYPE_ETH		0x01 //eth
#define PAEW_COIN_TYPE_CYB		0x02 //cybex
#define PAEW_COIN_TYPE_EOS		0x03 //eos
#define PAEW_COIN_TYPE_LTC		0x04 //lite
#define PAEW_COIN_TYPE_NEO		0x05 //neo
#define PAEW_COIN_TYPE_ETC		0x06 //etc
#define PAEW_COIN_TYPE_BTC_WIT	0x07 //bit coin P2WPKH nested in BIP16 P2SH
#define PAEW_COIN_TYPE_BTC_SIGWIT	0x08 //bit coin P2WPKH

//sig btc
#define PAEW_BTC_SIG_MAX_LEN			0x70
#define PAEW_SIG_BTC_TX_HEADER			0x00
#define PAEW_SIG_BTC_TX_IN				0x01
#define PAEW_SIG_BTC_TX_OUT_HEADER		0x02
#define PAEW_SIG_BTC_TX_OUT				0x03
#define PAEW_SIG_BTC_TX_FINAL			0x04
//sig eth
#define PAEW_ETH_SIG_MAX_LEN			0x45
#define PAEW_SIG_ETH_TX					0x00
//sig etc
#define PAEW_ETC_SIG_MAX_LEN			0x45
#define PAEW_SIG_ETC_TX					0x00
//sig cyb
#define PAEW_CYB_SIG_MAX_LEN			0x41
#define PAEW_SIG_CYB_TX					0x00
//sig eos
#define PAEW_EOS_SIG_MAX_LEN			0x80
#define PAEW_SIG_EOS_TX_HEADER			0x00
#define PAEW_SIG_EOS_TX_ACTION_COUNT	0x01
#define PAEW_SIG_EOS_TX_ACTION			0x02
#define PAEW_SIG_EOS_TX_CF_HASH			0x03 //context_free hash
//sig ltc
#define PAEW_LTC_SIG_MAX_LEN			0x70
#define PAEW_SIG_LTC_TX_HEADER			0x00
#define PAEW_SIG_LTC_TX_IN				0x01
#define PAEW_SIG_LTC_TX_OUT_HEADER		0x02
#define PAEW_SIG_LTC_TX_OUT				0x03
#define PAEW_SIG_LTC_TX_FINAL			0x04
//sig neo
#define PAEW_NEO_SIG_MAX_LEN			0x70
#define PAEW_SIG_NEO_TX_HEADER			0x00
#define PAEW_SIG_NEO_TX_SPEC_COUNT		0x01
#define PAEW_SIG_NEO_TX_SPEC_DATA		0x02
#define PAEW_SIG_NEO_TX_ATTRIBUTE		0x03
#define PAEW_SIG_NEO_TX_IN_COUNT		0x04
#define PAEW_SIG_NEO_TX_IN				0x05
#define PAEW_SIG_NEO_TX_OUT_COUNT		0x06
#define PAEW_SIG_NEO_TX_OUT				0x07
//#define PAEW_SIG_NEO_TX_SCRIPTS			0x06
//sig btc wit
#define PAEW_BTC_WIT_SIG_MAX_LEN			0x70
#define PAEW_SIG_BTC_WIT_TX_HEADER			0x00
#define PAEW_SIG_BTC_WIT_ALL_TX_IN			0x01
#define PAEW_SIG_BTC_WIT_TX_IN				0x02
#define PAEW_SIG_BTC_WIT_TX_OUT_HEADER		0x03
#define PAEW_SIG_BTC_WIT_TX_OUT				0x04
#define PAEW_SIG_BTC_WIT_TX_FINAL			0x05
//sig btc sigwit

//address
#define PAEW_COIN_ADDRESS_MAX_LEN		0x80

//seed
#define PAEW_ROOT_SEED_LEN				0x40

//FingerPrint
#define PAEW_FP_ID_LEN	0x01
typedef struct _FingerPrintID
{
	unsigned char	data[PAEW_FP_ID_LEN];
} FingerPrintID;

//Device Type
#define PAEW_DEV_TYPE_HID	0x00
#define PAEW_DEV_TYPE_BT	0x01

//Mne
#define PAEW_MNE_MAX_LEN			512
#define PAEW_MNE_INDEX_MAX_COUNT	32

//Authenticate Type
#define PAEW_SIGN_AUTH_TYPE_PIN		0x00
#define PAEW_SIGN_AUTH_TYPE_FP		0x01

//PIN
#define PAEW_PIN_MAX_LEN	0x20

//ECR20
#define PAEW_ERC20_TOKEN_NAME_MAX_LEN	0x08
#define PAEW_ERC20_TOKEN_ADDRESS_LEN	0x14
typedef struct _PAEW_ERC20Info
{
	unsigned char	pbTokenName[PAEW_ERC20_TOKEN_NAME_MAX_LEN];
	unsigned char	nTokenPrecision;
	unsigned char	pbTokenAddress[PAEW_ERC20_TOKEN_ADDRESS_LEN];
} PAEW_ERC20Info;

/*
��ȡ��İ汾��
[OUT] pbVersion�����ڴ洢�汾�ŵĵ�ַ��Ŀǰ�Ķ���Ϊ��PA_VERSION_PRODUCT || PA_VERSION_RESERVED || PA_VERSION_MAJOR || PA_VERSION_MINOR
[OUT] pnVersionLen�����ذ汾�ų���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetLibraryVersion(unsigned char * const pbVersion, size_t * const pnVersionLen);

/*
�����豸���ͣ���ȡ��ǰ�豸�б�
[IN] nDeviceType���豸���ͣ�ȡֵΪPAEW_DEV_TYPE_XXX
[OUT] szDeviceNames�����ڴ洢ö�ٵ����豸�б�ĵ�ַ����ö�ٳɹ����˵�ַ�б��صĸ�ʽΪ��devName1 + \0 + devName2 + \0 + ... + devNameN + \0\0
[OUT] pnDeviceNameLen������ʱ��ʾszDevNames�Ļ��������ȣ����ʱ��ʾ��Ч���豸�б�ĳ��ȣ�����ΪNULL
[OUT] pnDevCount������ö�ٵ����豸����������ΪNULL
[IN OUT] pDevContext���豸������
[IN] nDevContextLen���豸�����ĵĳ���
[IN] pProcCallback���������ս�����Ϣ�Ļص�����
[IN] pCallbackParam���ص������Ĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetDeviceListWithDevContext(const unsigned char nDeviceType, char * const szDeviceNames, size_t * const pnDeviceNameLen, size_t * const pnDevCount, void * const pDevContext, const size_t nDevContextLen);

/*
ʹ���豸�����ģ�ö�ٵ�ǰ���豸�б�
[OUT] ppPAEWContext�����ڴ洢��ʼ���õ������Ľṹ���ָ��ĵ�ַ������ΪNULL��ʹ����Ϻ�*ppPAEWContext��Ҫ����PAEW_FreeContext���ͷ���Դ
[OUT] pnDevCount������ö�ٵ����豸����
[IN OUT] pDevContext���豸������
[IN] nDevContextLen���豸�����ĵĳ���
[IN] pProcCallback���������ս�����Ϣ�Ļص�����
[IN] pCallbackParam���ص������Ĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_InitContextWithDevContext(void ** const ppPAEWContext, size_t * const pnDevCount, void * const pDevContext, const size_t nDevContextLen, const tFunc_Proc_Callback pProcCallback, callback_param * const pCallbackParam);

/*
���벢��ʼ��Ǯ��������
[OUT] ppPAEWContext�����ڴ洢��ʼ���õ������Ľṹ���ָ��ĵ�ַ������ΪNULL��ʹ����Ϻ�*ppPAEWContext��Ҫ����PAEW_FreeContext���ͷ���Դ
[OUT] pnDevCount������ö�ٵ����豸����
[IN] pProcCallback���������ս�����Ϣ�Ļص�����
[IN] pCallbackParam���ص������Ĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_InitContext(void ** const ppPAEWContext, size_t * const pnDevCount, const tFunc_Proc_Callback pProcCallback, callback_param * const pCallbackParam);

/*
ʹ���ض����豸���ƺ��豸�����ģ����벢��ʼ��Ǯ��������
[OUT] ppPAEWContext�����ڴ洢��ʼ���õ������Ľṹ���ָ��ĵ�ַ������ΪNULL��ʹ����Ϻ�*ppPAEWContext��Ҫ����PAEW_FreeContext���ͷ���Դ
[IN] szDeviceName����node-usbģ�����豸��εõ����豸���ƣ��������ֶ���ɣ���busNumber:deviceAddress:interfaceNumber����ʹ�á�%04x:%04x:%02x���ĸ�ʽ���и�ʽ����������ϵͳ�õ���������ַ��xx:xx:xx:xx��
[IN] nDeviceType���豸���ͣ�ȡֵΪPAEW_DEV_TYPE_XXX
[IN OUT] pDevContext���豸������
[IN] nDevContextLen���豸�����ĵĳ���
[IN] pProcCallback���������ս�����Ϣ�Ļص�����
[IN] pCallbackParam���ص������Ĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_InitContextWithDevNameAndDevContext(void ** const ppPAEWContext, const char * const szDeviceName, const unsigned char nDeviceType, void * const pDevContext, const size_t nDevContextLen, const tFunc_Proc_Callback pProcCallback, callback_param * const pCallbackParam);

/*
ʹ���ض����豸���ƣ����벢��ʼ��Ǯ��������
[OUT] ppPAEWContext�����ڴ洢��ʼ���õ������Ľṹ���ָ��ĵ�ַ������ΪNULL��ʹ����Ϻ�*ppPAEWContext��Ҫ����PAEW_FreeContext���ͷ���Դ
[IN] szDeviceName����node-usbģ�����豸��εõ����豸���ƣ��������ֶ���ɣ���busNumber:deviceAddress:interfaceNumber����ʹ�á�%04x:%04x:%02x���ĸ�ʽ���и�ʽ����������ϵͳ�õ���������ַ��xx:xx:xx:xx��
[IN] nDeviceType���豸���ͣ�ȡֵΪPAEW_DEV_TYPE_XXX
[IN] pProcCallback���������ս�����Ϣ�Ļص�����
[IN] pCallbackParam���ص������Ĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_InitContextWithDevName(void ** const ppPAEWContext, const char * const szDeviceName, const unsigned char nDeviceType, const tFunc_Proc_Callback pProcCallback, callback_param * const pCallbackParam);

/*
�ͷ�������
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_FreeContext(void * const pPAEWContext);

/*
�����豸������
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ������ΧΪ[0, nDevCount-1]��������ָ���豸�������ģ����ֵΪINVALID_DEV_INDEX�������������豸�������ģ�����ȡֵ��Ч
[IN] pDevContext���豸������
[IN] nDevContextLen���豸�����ĵĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_SetDevContext(void * const pPAEWContext, const size_t nDevIndex, void * const pDevContext, const size_t nDevContextLen);

/*
��ȡ�豸������
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[OUT] ppDevContext��ָ�򷵻ص��豸�����ĵ�ָ��
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetDevContext(void * const pPAEWContext, const size_t nDevIndex, void ** const ppDevContext);

/*
��ȡĳһ�豸��Ӳ����Ϣ
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nDevInfoType����ȡ���豸��Ϣ���ͣ�ȡֵΪPAEW_DEV_INFOTYPE_XXX��������
[OUT] pDevInfo�����ص��豸��Ϣ������ΪNULL
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetDevInfo(void * const pPAEWContext, const size_t nDevIndex, const uint32_t nDevInfoType, PAEW_DevInfo * const pDevInfo);

/*
���������Գ�ʼ���豸
������Ǯ��������Э�̻Ự��Կ���������ӡ��ַ����ӡ��������Ǵʡ����ԡ��ɹ����л��豸��������״̬
���˰�Ǯ������ʼ���豸�������������ӡ��������Ǵʡ����ԡ��ɹ����л��豸��������״̬
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nSeedLen�������˰�Ǯ��ר�ã����Ǵ�ֱ�Ӷ�Ӧ�����ӳ��ȣ�ȡֵΪ[16, 32]֮�ڵ�4�ı���
[IN] nN����������Ǯ��ר�ã�ϵͳ��������Կ��ɢ��������4<=N<=7���ҵ�ǰ������豸����������nN���
[IN] nT����������Ǯ��ר�ã�ϵͳ��������Կ�ָ���������2<=M<=N
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GenerateSeed(void * const pPAEWContext, const size_t nDevIndex, const unsigned char nSeedLen, const uint8_t nN, const uint8_t nT);

/*
���������Գ�ʼ���豸��������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nSeedLen�����Ǵ�ֱ�Ӷ�Ӧ�����ӳ��ȣ�ȡֵΪ[16, 32]֮�ڵ�4�ı���
[OUT] pbMneWord�����շ��ص����ǴʵĻ�����
[OUT] pnMneWordLen�����ص����Ǵʵĳ���
[OUT] pbCheckIndex�����շ��صĿ��������Ļ�����
[OUT] pnCheckIndexCount�����صĿ��������ĸ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GenerateSeed_GetMnes(void * const pPAEWContext, const size_t nDevIndex, const unsigned char nSeedLen, unsigned char * const pbMneWord, size_t * const pnMneWordLen, size_t * const pnCheckIndex, size_t * const pnCheckIndexCount);

/*
���������Գ�ʼ���豸��������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nSeedLen�����Ǵ�ֱ�Ӷ�Ӧ�����ӳ��ȣ�ȡֵΪ[16, 32]֮�ڵ�4�ı���
[IN] pbMneWord���洢���������ǴʵĻ�����
[IN] nMneWordLen�����������Ǵʵĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GenerateSeed_CheckMnes(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbMneWord, const size_t nMneWordLen);

/*
�����˰�Ǯ��ר�ã��������Ǵ��Գ�ʼ���豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] pbMneWord�����������ǴʵĻ�������ַ
[IN] nMneWordLen�����Ǵ����ݵ���Ч���ȣ�����Ϊ0
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_ImportSeed(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbMneWord, const size_t nMneWordLen);

/*
�����˰�Ǯ��ר�ã�ͨ�����Ǵʻָ����ӣ����㷨������ʼ���豸��
[IN] pbWordBuf��ָ�򱣴������ǴʵĻ�������ַ
[IN] nWordBufLen����������������Ǵ����ݵ���Ч����
[OUT] pbPrvSeed������ָ��������ӵĻ���������С����С��PAEW_ROOT_SEED_LEN�ֽ�
[IN OUT] pnPrvSeedLen������ʱ��ֵ��ʾpbPrvSeed�������ĳ��ȣ������ֵΪʵ�ʷ��صĻָ��������ӵĳ��ȣ��̶�ΪPAEW_ROOT_SEED_LEN
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_RecoverSeedFromMne(const unsigned char * const pbWordBuf, const size_t nWordBufLen, unsigned char * const pbPrvSeed, size_t * const pnPrvSeedLen);

/*
��������Ǯ��ר�ã�ͨ�����Ǵ���ָ����Ǵʣ����㷨������ʼ���豸��
[IN] nGroupCount������ķ����ĸ���
[IN] ppbWordBuf��ÿ��Ԫ��ָ��һ�����������ǴʵĻ�������ַ
[IN] pnWordBufLen��ÿ��Ԫ�ر�ʾ��Ӧ�±�Ļ�������������Ǵ����ݵ���Ч����
[OUT] pbMneWord������ָ��������ǴʵĻ�����������ΪNULL
[IN OUT] pnMneWordLen������ʱ��ֵ��ʾpbMneWord�������ĳ��ȣ������ֵΪʵ�ʷ��صĻָ��������Ǵʵĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_RecoverMneFromMneGroup(const size_t nGroupCount, const unsigned char * const * const ppbWordBuf, const size_t * const pnWordBufLen, unsigned char * const pbMneWord, size_t * const pnMneWordLen);

/*
ͨ�����Ӻ�����·�����ɵ�ַ�����㷨������ʼ���豸��
[IN] pbSeed���洢�������ݵĻ�����������ΪNULL
[IN] nSeedLen���������ݵ���Ч����
[IN] puiDerivePath����������·��������ΪNULL����һ�����ݱ���Ϊ0����ʾ·��m�����������ɸ�˽Կ��
[IN] nDerivePathLen����������·���ĳ��ȣ�Ԫ�ظ�����������Ϊ1
[OUT] pbPrivateKey������˽Կ�Ļ�����������ΪNULL
[IN OUT] pnPrivateKeyLen������ʱ��ֵ��ʾpbPrivateKey�������ĳ��ȣ������ֵΪ˽Կ�ĳ���
[IN] bTestNet�������ȡ��ַ����ȷ���Ƿ�Ϊ��������������Ϊ��0����ʽ��Ϊ0
[IN] nCoinType�������ȡ��ַ�����������������ΪPAEW_COIN_TYPE_XXX
[OUT] pbTradeAddress���������ֻ��ҵ�ַ�Ļ��������������ȡ��ַ���������ΪNULL
[IN OUT] pnTradeAddressLen������ʱ��ֵ��ʾpbTradeAddress�������ĳ��ȣ������ֵΪ���ֻ��ҵ�ַ�ĳ��ȣ��������ȡ��ַ���������ΪNULL
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetTradeAddressFromSeed(const unsigned char * const pbSeed, const size_t nSeedLen, const uint32_t * const puiDerivePath, const size_t nDerivePathLen, unsigned char * const pbPrivateKey, size_t * const pnPrivateKeyLen, const unsigned char bTestNet, const unsigned char nCoinType, unsigned char * const pbTradeAddress, size_t * const pnTradeAddressLen);

/*
��·���ָ����Ӻ͹�˽Կ
������Ǯ������·���ָ����Ӳ���������������˽Կ���ָ������ѡ���ĳ���豸�ڣ��ָ�֮��ɽ���ǩ����ȡ��ַ����
���˰�Ǯ������·����������������˽Կ���ָ�֮��ɽ���ǩ����ȡ��ַ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nCoinType����������PAEW_COIN_TYPE_XXX
[IN] puiDerivePath����������·��������ΪNULL����һ�����ݱ���Ϊ0����ʾ·��m�����������ɸ�˽Կ��
[IN] nDerivePathLen����������·���ĳ��ȣ�Ԫ�ظ�����������Ϊ1
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_DeriveTradeAddress(void * const pPAEWContext, const size_t nDevIndex, const unsigned char nCoinType, const uint32_t * const puiDerivePath, const size_t nDerivePathLen);

/*
��ȡ���ֻ��ҵ�ַ��Ҫ���ȵ���PAEW_DeriveTradeAddress
������Ǯ�����������豸ΪPAEW_DeriveTradeAddressѡ�е��豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nCoinType����������PAEW_COIN_TYPE_XXX��������PAEW_DeriveTradeAddressʱ����ı���һ��
[OUT] pbTradeAddress���������ֻ��ҵ�ַ�Ļ�����������ΪNULL
[IN OUT] pnTradeAddressLen������ʱ��ֵ��ʾpbTradeAddress�������ĳ��ȣ������ֵΪ���ֻ��ҵ�ַ�ĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetTradeAddress(void * const pPAEWContext, const size_t nDevIndex, const unsigned char nCoinType, unsigned char * const pbTradeAddress, size_t * const pnTradeAddressLen);

/*
���ر�ǩ����Ҫ���ȵ���PAEW_DeriveTradeAddress
������Ǯ�����������豸ΪPAEW_DeriveTradeAddressѡ�е��豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nUTXOCount�����ν���ʹ�õ�UTXO�ĸ������뱾�ν���ʹ�õ�Input�������
[IN] ppbUTXO�����ν���ʹ�õ�UTXO��������Ϣ
[IN] pnUTXOLen�����ν���ʹ�õ�UTXO����Ϣ���ȣ�ÿ��Ԫ�ض�ӦppbUTXO��һ��Ԫ��ָ�����Ϣ
[IN] pbCurrentTX�����ν��׵�δǩ���Ľ�����Ϣ
[IN] nCurrentTXLen�����ν��׵�δǩ��������Ϣ�ĳ���
[OUT] ppbTXSig��DER������ǩ�����ݣ�������Input������ȣ�����ΪNULL������Ԫ�ز���ΪNULL
[IN OUT] pnTXSigLen�����ν��׵Ľ�����Ϣǩ���ĳ��ȣ�����ʱ��ֵ��ʾppbTXSig�������ĳ��ȣ������ֵΪʵ�ʷ��ص�ǩ����Ľ������ݵĳ���
[IN] pSignCallbacks��ǩ���ص������ṹ�壬������ȡ��֤��ʽ����ȡPIN�롢����ǩ��״̬�Ȼص�
[IN OUT] pSignCallbackContext��ǩ���ص����������ģ���������������������Է���ص������ڲ�ͬ�����µ�ʵ��
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_BTC_TXSign(void * const pPAEWContext, const size_t nDevIndex, const size_t nUTXOCount, const unsigned char * const * const ppbUTXO, const size_t * const pnUTXOLen, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const * const ppbTXSig, size_t * const pnTXSigLen);
int EWALLET_API PAEW_BTC_TXSign_Ex(void * const pPAEWContext, const size_t nDevIndex, const size_t nUTXOCount, const unsigned char * const * const ppbUTXO, const size_t * const pnUTXOLen, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const * const ppbTXSig, size_t * const pnTXSigLen, const signCallbacks * const pSignCallbacks, void * const pSignCallbackContext);

/*
��̫��ǩ����Ҫ���ȵ���PAEW_DeriveTradeAddress
������Ǯ�����������豸ΪPAEW_DeriveTradeAddressѡ�е��豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] pbCurrentTX�����ν��׵�δǩ���Ľ�����Ϣ
[IN] nCurrentTXLen�����ν��׵�δǩ��������Ϣ�ĳ���
[OUT] pbTXSig��ǩ�����ݣ�R��32�ֽڣ�+S��32�ֽڣ�+V��1�ֽڣ�������ΪNULL
[IN OUT] pnTXSigLen�����ν��׵Ľ�����Ϣǩ���ĳ��ȣ�����ʱ��ֵ��ʾpbTXSig�������ĳ��ȣ������ֵΪʵ�ʷ��ص�ǩ����Ľ������ݵĳ���
[IN] pSignCallbacks��ǩ���ص������ṹ�壬������ȡ��֤��ʽ����ȡPIN�롢����ǩ��״̬�Ȼص�
[IN OUT] pSignCallbackContext��ǩ���ص����������ģ���������������������Է���ص������ڲ�ͬ�����µ�ʵ��
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_ETH_TXSign(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen);
int EWALLET_API PAEW_ETH_TXSign_Ex(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen, const signCallbacks * const pSignCallbacks, void * const pSignCallbackContext);

/*
��̫�ֽ�ǩ����Ҫ���ȵ���PAEW_DeriveTradeAddress
������Ǯ�����������豸ΪPAEW_DeriveTradeAddressѡ�е��豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] pbCurrentTX�����ν��׵�δǩ���Ľ�����Ϣ
[IN] nCurrentTXLen�����ν��׵�δǩ��������Ϣ�ĳ���
[OUT] pbTXSig��ǩ�����ݣ�R��32�ֽڣ�+S��32�ֽڣ�+V��1�ֽڣ�������ΪNULL
[IN OUT] pnTXSigLen�����ν��׵Ľ�����Ϣǩ���ĳ��ȣ�����ʱ��ֵ��ʾpbTXSig�������ĳ��ȣ������ֵΪʵ�ʷ��ص�ǩ����Ľ������ݵĳ���
[IN] pSignCallbacks��ǩ���ص������ṹ�壬������ȡ��֤��ʽ����ȡPIN�롢����ǩ��״̬�Ȼص�
[IN OUT] pSignCallbackContext��ǩ���ص����������ģ���������������������Է���ص������ڲ�ͬ�����µ�ʵ��
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_ETC_TXSign(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen);
int EWALLET_API PAEW_ETC_TXSign_Ex(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen, const signCallbacks * const pSignCallbacks, void * const pSignCallbackContext);

/*
CYBǩ����Ҫ���ȵ���PAEW_DeriveTradeAddress
������Ǯ�����������豸ΪPAEW_DeriveTradeAddressѡ�е��豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] pbCurrentTX�����ν��׵�δǩ���Ľ�����Ϣ
[IN] nCurrentTXLen�����ν��׵�δǩ��������Ϣ�ĳ���
[OUT] pbTXSig��ǩ�����ݣ�R��32�ֽڣ�+S��32�ֽڣ�������ΪNULL
[IN OUT] pnTXSigLen�����ν��׵Ľ�����Ϣǩ���ĳ��ȣ�����ʱ��ֵ��ʾpbTXSig�������ĳ��ȣ������ֵΪʵ�ʷ��ص�ǩ����Ľ������ݵĳ���
[IN] pSignCallbacks��ǩ���ص������ṹ�壬������ȡ��֤��ʽ����ȡPIN�롢����ǩ��״̬�Ȼص�
[IN OUT] pSignCallbackContext��ǩ���ص����������ģ���������������������Է���ص������ڲ�ͬ�����µ�ʵ��
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_CYB_TXSign(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen);
int EWALLET_API PAEW_CYB_TXSign_Ex(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen, const signCallbacks * const pSignCallbacks, void * const pSignCallbackContext);

/*
EOSǩ����Ҫ���ȵ���PAEW_DeriveTradeAddress
������Ǯ�����������豸ΪPAEW_DeriveTradeAddressѡ�е��豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] pbCurrentTX�����ν��׵�δǩ���Ľ�����Ϣ
[IN] nCurrentTXLen�����ν��׵�δǩ��������Ϣ�ĳ���
[OUT] pbTXSig��ǩ�����ݣ�Ϊ������ǩ�����ݣ�����ΪNULL
[IN OUT] pnTXSigLen�����ν��׵Ľ�����Ϣǩ���ĳ��ȣ�����ʱ��ֵ��ʾpbTXSig�������ĳ��ȣ������ֵΪʵ�ʷ��ص�ǩ����Ľ������ݵĳ���
[IN] pSignCallbacks��ǩ���ص������ṹ�壬������ȡ��֤��ʽ����ȡPIN�롢����ǩ��״̬�Ȼص�
[IN OUT] pSignCallbackContext��ǩ���ص����������ģ���������������������Է���ص������ڲ�ͬ�����µ�ʵ��
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_EOS_TXSign(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen);
int EWALLET_API PAEW_EOS_TXSign_Ex(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen, const signCallbacks * const pSignCallbacks, void * const pSignCallbackContext);

/*
���ر�ǩ����Ҫ���ȵ���PAEW_DeriveTradeAddress
������Ǯ�����������豸ΪPAEW_DeriveTradeAddressѡ�е��豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nUTXOCount�����ν���ʹ�õ�UTXO�ĸ������뱾�ν���ʹ�õ�Input�������
[IN] ppbUTXO�����ν���ʹ�õ�UTXO��������Ϣ
[IN] pnUTXOLen�����ν���ʹ�õ�UTXO����Ϣ���ȣ�ÿ��Ԫ�ض�ӦppbUTXO��һ��Ԫ��ָ�����Ϣ
[IN] pbCurrentTX�����ν��׵�δǩ���Ľ�����Ϣ
[IN] nCurrentTXLen�����ν��׵�δǩ��������Ϣ�ĳ���
[OUT] ppbTXSig��DER������ǩ�����ݣ�������Input������ȣ�����ΪNULL������Ԫ�ز���ΪNULL
[IN OUT] pnTXSigLen�����ν��׵Ľ�����Ϣǩ���ĳ��ȣ�����ʱ��ֵ��ʾppbTXSig�������ĳ��ȣ������ֵΪʵ�ʷ��ص�ǩ����Ľ������ݵĳ���
[IN] pSignCallbacks��ǩ���ص������ṹ�壬������ȡ��֤��ʽ����ȡPIN�롢����ǩ��״̬�Ȼص�
[IN OUT] pSignCallbackContext��ǩ���ص����������ģ���������������������Է���ص������ڲ�ͬ�����µ�ʵ��
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_LTC_TXSign(void * const pPAEWContext, const size_t nDevIndex, const size_t nUTXOCount, const unsigned char * const * const ppbUTXO, const size_t * const pnUTXOLen, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const * const ppbTXSig, size_t * const pnTXSigLen);
int EWALLET_API PAEW_LTC_TXSign_Ex(void * const pPAEWContext, const size_t nDevIndex, const size_t nUTXOCount, const unsigned char * const * const ppbUTXO, const size_t * const pnUTXOLen, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const * const ppbTXSig, size_t * const pnTXSigLen, const signCallbacks * const pSignCallbacks, void * const pSignCallbackContext);

/*
���ر�ǩ����Ҫ���ȵ���PAEW_DeriveTradeAddress
������Ǯ�����������豸ΪPAEW_DeriveTradeAddressѡ�е��豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nUTXOCount�����ν���ʹ�õ�UTXO�ĸ������뱾�ν���ʹ�õ�Input�������
[IN] ppbUTXO�����ν���ʹ�õ�UTXO��������Ϣ
[IN] pnUTXOLen�����ν���ʹ�õ�UTXO����Ϣ���ȣ�ÿ��Ԫ�ض�ӦppbUTXO��һ��Ԫ��ָ�����Ϣ
[IN] pbCurrentTX�����ν��׵�δǩ���Ľ�����Ϣ
[IN] nCurrentTXLen�����ν��׵�δǩ��������Ϣ�ĳ���
[OUT] pbTXSig��ǩ�����ݣ�Ϊ������ǩ�����ݣ�����ΪNULL
[IN OUT] pnTXSigLen�����ν��׵Ľ�����Ϣǩ���ĳ��ȣ�����ʱ��ֵ��ʾpbTXSig�������ĳ��ȣ������ֵΪʵ�ʷ��ص�ǩ����Ľ������ݵĳ���
[IN] pSignCallbacks��ǩ���ص������ṹ�壬������ȡ��֤��ʽ����ȡPIN�롢����ǩ��״̬�Ȼص�
[IN OUT] pSignCallbackContext��ǩ���ص����������ģ���������������������Է���ص������ڲ�ͬ�����µ�ʵ��
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_NEO_TXSign(void * const pPAEWContext, const size_t nDevIndex, const size_t nUTXOCount, const unsigned char * const * const ppbUTXO, const size_t * const pnUTXOLen, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen);
int EWALLET_API PAEW_NEO_TXSign_Ex(void * const pPAEWContext, const size_t nDevIndex, const size_t nUTXOCount, const unsigned char * const * const ppbUTXO, const size_t * const pnUTXOLen, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const pbTXSig, size_t * const pnTXSigLen, const signCallbacks * const pSignCallbacks, void * const pSignCallbackContext);

/*
���ر�WITǩ������P2WPKH nested in BIP16 P2SH��Ҫ���ȵ���PAEW_DeriveTradeAddress
������Ǯ�����������豸ΪPAEW_DeriveTradeAddressѡ�е��豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nUTXOCount�����ν���ʹ�õ�UTXO�ĸ������뱾�ν���ʹ�õ�Input�������
[IN] ppbUTXO�����ν���ʹ�õ�UTXO��������Ϣ
[IN] pnUTXOLen�����ν���ʹ�õ�UTXO����Ϣ���ȣ�ÿ��Ԫ�ض�ӦppbUTXO��һ��Ԫ��ָ�����Ϣ
[IN] pbCurrentTX�����ν��׵�δǩ���Ľ�����Ϣ
[IN] nCurrentTXLen�����ν��׵�δǩ��������Ϣ�ĳ���
[OUT] ppbTXSig��DER������ǩ�����ݣ�������Input������ȣ�����ΪNULL������Ԫ�ز���ΪNULL
[IN OUT] pnTXSigLen�����ν��׵Ľ�����Ϣǩ���ĳ��ȣ�����ʱ��ֵ��ʾppbTXSig�������ĳ��ȣ������ֵΪʵ�ʷ��ص�ǩ����Ľ������ݵĳ���
[IN] pSignCallbacks��ǩ���ص������ṹ�壬������ȡ��֤��ʽ����ȡPIN�롢����ǩ��״̬�Ȼص�
[IN OUT] pSignCallbackContext��ǩ���ص����������ģ���������������������Է���ص������ڲ�ͬ�����µ�ʵ��
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_BTC_WIT_TXSign(void * const pPAEWContext, const size_t nDevIndex, const size_t nUTXOCount, const unsigned char * const * const ppbUTXO, const size_t * const pnUTXOLen, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const * const ppbTXSig, size_t * const pnTXSigLen);
int EWALLET_API PAEW_BTC_WIT_TXSign_Ex(void * const pPAEWContext, const size_t nDevIndex, const size_t nUTXOCount, const unsigned char * const * const ppbUTXO, const size_t * const pnUTXOLen, const unsigned char * const pbCurrentTX, const size_t nCurrentTXLen, unsigned char * const * const ppbTXSig, size_t * const pnTXSigLen, const signCallbacks * const pSignCallbacks, void * const pSignCallbackContext);

/*
����û�COS
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_ClearCOS(void * const pPAEWContext, const size_t nDevIndex);

/*
�޸�PIN
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_ChangePIN(void * const pPAEWContext, const size_t nDevIndex);

/*
�޸�PIN��������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] szOldPIN���豸�ĵ�ǰPIN�룬����ΪNULL
[IN] szNewPIN����Ҫ���õ���PIN�룬����ΪNULL
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_ChangePIN_Input(void * const pPAEWContext, const size_t nDevIndex, const char * const szOldPIN, const char * const szNewPIN);

/*
���ó�ʼPIN��������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] szPIN����Ҫ���õ�PIN�룬����ΪNULL
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_InitPIN(void * const pPAEWContext, const size_t nDevIndex, const char * const szPIN);

/*
��֤PIN��������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] szPIN����Ҫ��֤��PIN�룬����ΪNULL
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_VerifyPIN(void * const pPAEWContext, const size_t nDevIndex, const char * const szPIN);

/*
��ʽ���豸������豸���Ѵ��е����ӻ�������ٴ�ʹ����Ҫ���³�ʼ���豸
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_Format(void * const pPAEWContext, const size_t nDevIndex);

/*
�����˰�Ǯ��ר�ã������豸��COS
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] pbCOSData��COS���������ݻ�����������ΪNULL
[IN] nCOSDataLen��COS���������ݵĳ��ȣ�����Ϊ0
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_UpdateCOS(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbCOSData, const size_t nCOSDataLen);

/*
EOS transaction �ַ�����Json�����л������㷨������ʼ���豸��
[IN] szTransactionString��transaction�ַ���������ref_block_prefix�ֶ���Ҫ��Ϊ�ַ������루ʹ��˫������������
[OUT] pbTransactionData���洢���л�֮��Ķ��������ݵĻ�����������ΪNULL
[IN OUT] pnTransactionLen�����л�֮��Ķ��������ݵĳ��ȣ�����ʱ��ֵ��ʾpbTransactionData�������ĳ��ȣ������ֵΪʵ�ʷ��ص����л��������ݵĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_EOS_TX_Serialize(const char * const szTransactionString, unsigned char * const pbTransactionData, size_t * pnTransactionLen);

/*
EOS transaction �ַ�����Json�����л������㷨������ʼ���豸��
[IN] nPartIndex��EOS�������л���������ֵΪPAEW_SIG_EOS_TX_XXX
[IN] szTransactionString��transaction�ַ���������ref_block_prefix�ֶ���Ҫ��Ϊ�ַ������루ʹ��˫������������
[OUT] pbTransactionData���洢���л�֮��Ķ��������ݵĻ�����������ΪNULL
[IN OUT] pnTransactionLen�����л�֮��Ķ��������ݵĳ��ȣ�����ʱ��ֵ��ʾpbTransactionData�������ĳ��ȣ������ֵΪʵ�ʷ��ص����л��������ݵĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_EOS_TX_Part_Serialize(const unsigned int nPartIndex, const char * const szTransactionString, unsigned char * const pbTransactionData, size_t * pnTransactionLen);

/*
����ERC20���ҵ���Ϣ��������ʾ��
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex�������˰�Ǯ��ר�ã��������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] nCoinType���������ͣ�Ŀǰ��֧��PAEW_COIN_TYPE_ETH��PAEW_COIN_TYPE_ETC
[IN] szTokenName�����ֵ����ƣ�����ΪNULL���������ݳ��Ȳ���Ϊ0
[IN] nPrecision�����־��ȣ���ʾ10���ݴ�
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_SetERC20Info(void * const pPAEWContext, const size_t nDevIndex, const unsigned char nCoinType, const char * const szTokenName, const unsigned char nPrecision);


/*
��ȡָ���б�������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[OUT] pFPList���洢ָ���б�Ļ�����������ΪNULL
[OUT] pnFPListCount������ʱָ��pFPList��Ԫ�ظ���������ʱ��ʾ��ȡ����ָ�Ƶĸ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetFPList(void * const pPAEWContext, const size_t nDevIndex, FingerPrintID * const pFPList, size_t * const pnFPListCount);

/*
��ָ֤�ƣ�������ָ��Ǯ����֮����ʹ��PAEW_GetFPState��ȡ״̬��
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_VerifyFP(void * const pPAEWContext, const size_t nDevIndex);

/*
��ȡ��֤ͨ����ָ���б�������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[OUT] pFPList���洢ָ���б�Ļ�����������ΪNULL
[OUT] pnFPListCount������ʱָ��pFPList��Ԫ�ظ���������ʱ��ʾ��ȡ����ָ�Ƶĸ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetVerifyFPList(void * const pPAEWContext, const size_t nDevIndex, FingerPrintID * const pFPList, size_t * const pnFPListCount);

/*
¼��ָ�ƣ�������ָ��Ǯ����֮����ʹ��PAEW_GetFPState��ȡ״̬��
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_EnrollFP(void * const pPAEWContext, const size_t nDevIndex);

/*
��ȡָ�Ʋ�����״̬��������ָ��Ǯ����ǰ�ýӿ�ΪPAEW_VerifyFP��PAEW_EnrollFP��
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetFPState(void * const pPAEWContext, const size_t nDevIndex);

/*
�ж�ָ�Ʋ�����������ָ��Ǯ���������ж�¼�����ָ֤�ƣ�
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_AbortFP(void * const pPAEWContext, const size_t nDevIndex);

/*
ɾ��ָ�ƣ�������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] pFPID�������˴�ɾ����ָ��ID����Ļ��������׵�ַ��������NULLʱ����ʾȫ��ɾ��
[IN] nFPCount����ɾ����ָ��ID����ĸ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_DeleteFP(void * const pPAEWContext, const size_t nDevIndex, const FingerPrintID * const pFPID, const size_t nFPCount);

/*
У��ָ�ƴ�������������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_CalibrateFP(void * const pPAEWContext, const size_t nDevIndex);

/*
�����Ļ��������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_ClearLCD(void * const pPAEWContext, const size_t nDevIndex);

/*
�رյ�Դ��������ָ��Ǯ����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_PowerOff(void * const pPAEWContext, const size_t nDevIndex);

/*
��ȡ�ͻ�����������
[OUT] pbData���洢�������ݵĻ�����
[OUT] pnDataLen������ʱָ��pnDataLen�Ļ��������ȣ�����ʱ��ʾ��ȡ�����������ݳ���
*/
int EWALLET_API PAEW_GetClientConfigData(unsigned char * const pbData, size_t * const pnDataLen);

/*
��ȡ�豸У����
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[OUT] pbCheckCode���洢У����Ļ�����������ΪNULL
[OUT] pnCheckCodeLen������ʱָ��pbCheckCode���ֽڳ��ȣ�����ʱ��ʾ��ȡ����У����ĳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetDeviceCheckCode(void * const pPAEWContext, const size_t nDevIndex, unsigned char * const pbCheckCode, size_t * const pnCheckCodeLen);

/*
��ȡERC20��¼�б�
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[OUT] pERC20InfoList���洢ERC20��Ϣ�Ļ�����������ΪNULL
[OUT] pnERC20InfoCount������ʱָ��pERC20InfoList��Ԫ�ظ���������ʱ��ʾ��ȡ����PAEW_ECR20Info��Ԫ�ظ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_GetERC20List(void * const pPAEWContext, const size_t nDevIndex, PAEW_ERC20Info * const pERC20InfoList, size_t * const pnERC20InfoCount);

/*
����ERC20��¼��Ϣ
[IN] pPAEWContext�������Ľṹ��ָ�룬����ΪNULL
[IN] nDevIndex���������豸�����ţ���ΧΪ[0, nDevCount-1]
[IN] pbERC20RecordData���洢ERC20��¼�Ļ�����������ΪNULL
[IN] nERC20RecordLen��pbERC20RecordData���ֽڳ���
[RETURN] PAEW_RET_SUCCESSΪ�ɹ�����PAEW_RET_SUCCESSֵΪʧ��
*/
int EWALLET_API PAEW_ImportERC20Record(void * const pPAEWContext, const size_t nDevIndex, const unsigned char * const pbERC20RecordData, const size_t nERC20RecordLen);

#ifdef __cplusplus
};
#endif
#endif //_PA_EWALLET_H_