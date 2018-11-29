# Wookong for bio
com.extropies.common.MiddlewareInterface is main interface to interact with the device

_NOTE: All the method mentioned here should NOT be called in main thread, or bluetooth
communication will be blocked._

## More details:
* [wiki: home](https://github.com/extropiescom/bladeX/wiki)
* [wiki: android API](https://github.com/extropiescom/bladeX/wiki/bladeX-Android-API-(For-hackathon))

## 1. How to connect and disconnect:
   - Invoke `MiddlewareInterface.getDeviceList()` to get device name list, device names
   are in format of "device_name####device_address".
   - Use `MiddlewareInterface.initContextWithDevName()` to connect device, with devName
   chosen from `MiddlewareInterface.getDeviceList()` result.
   - User `MiddlewareInterface.freeContext()` or `freeContextAndShutDown()` to disconnect
   and power down the device.
## 2. How to initialize device:
   - PIN initialize: If `MiddlewareInterface.getDevInfo()` returns device info with `ucPINState == MiddlewareInterace.PAEW_DEV_INFO_PIN_UNSET`,
   this means PIN haven't been set, and you should call `MiddlewareInterface.initPIN()` to initialize.
   - Seed initialize: If `MiddlewareInterface.getDevInfo()` returns device info with `ucLifeCycle == MiddlewareInterace.PAEW_DEV_INFO_LIFECYCLE_PRODUCE`,
   this means there're no seed inside device, and you should initialize device first (after device initialization, ucLifeCycle
   should be `MiddlewareInterface.PAEW_DEV_INFO_LIFECYCLE_USER`). Invoke `MiddlewareInterface.generateSeed_GetMnes()` + `MiddlewareInterface.generateSeed_CheckMnes()`
   to generate new seed, or invoke `MiddlewareInterface.importMne()` to import mnemonics to import seed.
## 3. How to get EOS address:
   - Invoke `MiddlewareInterface.deriveTradeAddress(contextHandle, 0, PAEW_COIN_TYPE_EOS, derivePath)`, with
   `derivePath = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000}` according to [slip-44](https://github.com/satoshilabs/slips/blob/master/slip-0044.md).
   - Invoke `MiddlewareInterface.getTradeAddress(contextHandle, 0, PAEW_COIN_TYPE_EOS, bShowOnScreen, strAddress)` to get EOS address.
## 4. How to sign EOS transaction:
   - Invoke `MiddlewareInterface.deriveTradeAddress(contextHandle, 0, PAEW_COIN_TYPE_EOS, derivePath)`, with
   `derivePath = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};` according to [slip-44](https://github.com/satoshilabs/slips/blob/master/slip-0044.md).
   - (Optional) Invoke `MiddlewareInterface.eos_tx_serialize()` to serialize json string to binary.
   
   _NOTE1: ref_block_prefix field of json object MUST be wrapped by quotation marks ("") if you pass it to `MiddlewareInterface.eos_tx_serialize()`, such as \"2642943355\" in the following._
   
   _NOTE2: serializeData is the binary form of transaction, you should prefix it with 32 bytes of chain_id, and padding with 32 bytes of zeros, then pass it to `MiddlewareInterface.EOSSign()` to sign._
   
   ```java
   String jsonTxString = "{\"expiration\":\"2018-05-16T02:49:35\",\"ref_block_num\":4105,\"ref_block_prefix\":\"2642943355\",\"max_net_usage_words\":0,\"max_cpu_usage_ms\":0,\"delay_sec\":0,\"context_free_actions\":[],\"actions\":[{\"account\":\"eosio\",\"name\":\"newaccount\",\"authorization\":[{\"actor\":\"eosio\",\"permission\":\"active\"}],\"data\":\"0000000000ea30550000000000000e3d01000000010003224c02ca019e9c0c969d2c8006b89275abeeb5b05af68f2cf5f497bd6e1aff6d01000000010000000100038d424cbe81564f1e4338d342a4dc2b70d848d8b026d3f783bc7c8e6c3c6733cf01000000\"}],\"transaction_extensions\":[],\"signatures\":[],\"context_free_data\":[]}";
   byte[] serializeData = null;
   int[] serializeDataLen = new int[1];
   //get buffer size using serializeData = null
   iRtn = MiddlewareInterface.eos_tx_serialize(m_strEOSTxString, null, serializeDataLen);
   if (iRtn == MiddlewareInterface.PAEW_RET_SUCCESS) {
      //malloc buffer
      serializeData = new byte[serializeDataLen[0]];
      //serialize transaction
      iRtn = MiddlewareInterface.eos_tx_serialize(m_strEOSTxString, serializeData, serializeDataLen);
   }
   ```   
   - Invoke `MiddlewareInterface.EOSSign(contextHandle, 0, signCallback, transaction, signature, sigLen)`, 
   _this transaction is serialized result of a json transaction string, prefixed with chain_id (32 bytes) and tailed with zeros (32 bytes)_
   ```java
   byte[] transaction = {(byte)0x74, (byte)0x09, (byte)0x70, (byte)0xd9, (byte)0xff, (byte)0x01, (byte)0xb5, (byte)0x04, (byte)0x63, (byte)0x2f, (byte)0xed, (byte)0xe1, (byte)0xad, (byte)0xc3, (byte)0xdf, (byte)0xe5, (byte)0x59, (byte)0x90, (byte)0x41, (byte)0x5e, (byte)0x4f, (byte)0xde, (byte)0x01, (byte)0xe1, (byte)0xb8, (byte)0xf3, (byte)0x15, (byte)0xf8, (byte)0x13, (byte)0x6f, (byte)0x47, (byte)0x6c, (byte)0x14, (byte)0xc2, (byte)0x67, (byte)0x5b, (byte)0x01, (byte)0x24, (byte)0x5f, (byte)0x70, (byte)0x5d, (byte)0xd7, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0xa6, (byte)0x82, (byte)0x34, (byte)0x03, (byte)0xea, (byte)0x30, (byte)0x55, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x57, (byte)0x2d, (byte)0x3c, (byte)0xcd, (byte)0xcd, (byte)0x01, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xa8, (byte)0xed, (byte)0x32, (byte)0x32, (byte)0x21, (byte)0x20, (byte)0x29, (byte)0xc2, (byte)0xca, (byte)0x55, (byte)0x7a, (byte)0x73, (byte)0x57, (byte)0x90, (byte)0x55, (byte)0x8c, (byte)0x86, (byte)0x77, (byte)0x95, (byte)0x4c, (byte)0x3c, (byte)0x10, (byte)0x27, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04, (byte)0x45, (byte)0x4f, (byte)0x53, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
   byte[] signature = new byte[MiddlewareInterface.PAEW_ETH_SIG_MAX_LEN];
   int[] sigLen = new int[1];
   sigLen[0] = MiddlewareInterface.PAEW_ETH_SIG_MAX_LEN;
   iRtn = MiddlewareInterface.EOSSign(contextHandle, 0, signCallback, transaction, signature, sigLen);
   ```
## 5. Sign Callbacks
   
   _Sign callbacks are invoked in the following sequence:_
   - Invoke `MiddlewareInterface.getAuthResult()`, return `PAEW_RET_SUCCESS` or `PAEW_RET_DEV_OP_CANCEL`, indicates user chooses OK or Cancel on UI. If returns `PAEW_RET_SUCCESS`, signature will go on; if returns `PAEW_RET_DEV_OP_CANCE`L, you should call `abort()` to end this sign procedure.
   - Invoke `MiddlewareInterface.getAuthType()`, return `PAEW_SIGN_AUTH_TYPE_PIN` or `PAEW_SIGN_AUTH_TYPE_FP`.
   - If `MiddlewareInterface.getAuthType()` returns `PAEW_SIGN_AUTH_TYPE_PIN`, then call `MiddlewareInterface.getPINResult()`. `MiddlewareInterface.getPINResult()` returns `PAEW_RET_SUCCESS` or `PAEW_RET_DEV_OP_CANCEL`, indicates user choosesOK or Cancel on UI.If returns` PAEW_RET_SUCCES`S, signature will go on; if returns `PAEW_RET_DEV_OP_CANCEL`, you should call `abort()` to end this sign procedure.
   - If `MiddlewareInterface.getAuthType()` returns `PAEW_SIGN_AUTH_TYPE_PIN`, and `MiddlewareInterface.getPINResult()` returns `PAEW_RET_SUCCESS`, then call `MiddlewareInterface.getPIN()` to get PIN from UI.
   - Do signature according to user's option.
   - pseudo-code of signature method
```java
   if (MiddlewareInterface.PAEW_RET_SUCCESS != getAuthResult()) {
       return;
   }
   nAuthType = getAuthType();
   if (Middleware.PAEW_SIGN_AUTH_TYPE_PIN == nAuthType) {
       if (MiddlewareInterface.PAEW_RET_SUCCESS != getPINResult()) {
           return;
       }
       strPIN = getPIN();
   }
   nResult = (do signature with user selected authenticate type (finger print or PIN))
   if ((MiddlewareInterface.PAEW_RET_SUCCESS != nResult) && (Middleware.PAEW_SIGN_AUTH_TYPE_PIN != nAuthType)) {
       if (MiddlewareInterface.PAEW_RET_SUCCESS != getPINResult()) {
           return;
       }
       strPIN = getPIN();
       (do signature with PIN authority)
   }
```
