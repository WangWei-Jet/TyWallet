package com.whty.blockchain.tyblockchainlib.api.core;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.whty.adiplugins.ADIPlugin;
import com.whty.blockchain.tyblockchainlib.api.util.GPMethods;
import com.whty.bluetooth.manage.util.BlueToothConfig;
import com.whty.bluetooth.manage.util.BlueToothConfigRes;
import com.whty.bluetooth.manage.util.BlueToothUtil;
import com.whty.bluetoothsdk.util.Utils;
import com.whty.comm.inter.ICommunication;
import com.whty.device.inter.AndroidDeviceApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TyWalletBtDriver {

    Context context;

    AndroidDeviceApi<Boolean, Context, Object> androidDeviceApi;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final long COMMAND_TIMEOUT = 3000L;
    public String g_SessionsKey;

    public TyWalletBtDriver(Context context) {
        androidDeviceApi = new AndroidDeviceApi<>();

        this.context = context;
    }

    public void init() {
        androidDeviceApi.init(context,
                ICommunication.BLUETOOTH_DEVICE, null, null,
                BlueToothConfigRes.BT_DIALOG_INVISIBLE,
                BlueToothConfigRes.BT_TOAST_INVISIBLE, false);
    }

    public boolean connectWallet(BluetoothDevice bluetoothDevice) {
        boolean connectResult = ADIPlugin.connectBTDevice(androidDeviceApi, bluetoothDevice.getAddress(), context);
        if (connectResult) {
            generateEncryKey();
        }
        return connectResult;
    }

    private boolean generateEncryKey() {
        logger.debug("generateEncryKey ");
        String encryDev = "";
        String Rdev = "";
        String Rsdk = GPMethods.yieldHexRand(16);//"00112233445566778899001122334455";
        try {
            byte[] cmd_head = GPMethods.str2bytes("D400000010");
            byte[] cmd_send = new byte[21];
            System.arraycopy(cmd_head, 0, cmd_send, 0, 5);
            System.arraycopy(GPMethods.str2bytes(Rsdk), 0, cmd_send, 5, 16);
            byte[] response = new byte[300];
            int sessionCount = 3;
            while (sessionCount-- > 0) {
                logger.debug("prepare to generate encry Key");
                int ret = androidDeviceApi.transCommand(cmd_send, cmd_send.length,
                        response, COMMAND_TIMEOUT);
                if (ret > 0) {
                    byte[] res = new byte[ret];
                    System.arraycopy(response, 0, res, 0, ret);
                    if (res[ret - 2] == (byte) 0x90 && res[ret - 1] == 0x00) {
                        String responseStr = Utils.bytesToHexString(res, ret - 2);
                        logger.debug("responseStr:" + responseStr);
                        if (responseStr.length() >= 16 * 2) {
                            Rdev = responseStr.substring(0, 16 * 2);
                            if (responseStr.length() >= 48 * 2) {
                                encryDev = responseStr.substring(16 * 2, 48 * 2);
                            }
                        }
                        String SHAsdk = GPMethods.generateSHA256(Rsdk);
                        logger.debug("SHAsdk:" + SHAsdk);
                        String key = SHAsdk.substring(4 * 2, 28 * 2);
                        String iv1 = SHAsdk.substring(0, 4 * 2) + SHAsdk.substring(28 * 2, 32 * 2);
                        String decryStr = GPMethods.descbc(key, responseStr, iv1, 1);
                        logger.debug("encryStr:" + decryStr);
                        if (decryStr != null) {
                            String SHAdev = GPMethods.generateSHA256(decryStr.substring(0, 16 * 2));
                            logger.debug("SHAdev:" + SHAdev);
                            if (SHAdev.equals(decryStr.substring(16 * 2, 48 * 2))) {
                                String innerKey = GPMethods.generateSHA256(GPMethods.str2HexStr("whtyBW10"));
                                logger.debug("innerKey:" + innerKey);
                                String randomDns = Rsdk + decryStr.substring(0, 16 * 2);
                                String SHAdsn = GPMethods.generateSHA256(randomDns);
                                String iv2 = SHAdsn.substring(0, 4 * 2) + SHAdsn.substring(28 * 2, 32 * 2);
                                g_SessionsKey = GPMethods.descbc(innerKey.substring(0, 24 * 2), SHAdsn.substring(4 * 2, 28 * 2), iv2, 0);
                                logger.debug("g_SessionsKey:" + g_SessionsKey);
                                return true;
                            }
                        }
                    } else {
                        logger.error("error code:" + GPMethods
                                .bytesToHexString(new byte[]{res[ret - 2], res[ret - 1]}));
                    }
                } else {
                    logger.debug("command interaction timeout!");
                }
            }
        } catch (Exception e) {
            logger.debug("error occurs trying to generate encry Key", e);
        }
        return false;
    }

    public boolean isConnected() {
        return BlueToothConfig.BT_CONN_STATE
                && BlueToothUtil.ACL_BT_CONN_STATE;
    }

    public boolean disconnectWallet() {
        g_SessionsKey = null;
        return androidDeviceApi.disConnect();
    }

    public int transCommandAPDU(byte[] req, int len, byte[] res, long timeout) {
        return androidDeviceApi.transCommand(req, len, res, timeout);
    }

    public int transCommand(byte[] req, int len, byte[] res, long timeout) {
        if (g_SessionsKey == null && g_SessionsKey.length() == 0) {
            generateEncryKey();
        }
        logger.debug("req:" + GPMethods.bytesToHexString(req));
        String encryData = GPMethods.desecb(g_SessionsKey, GPMethods.bytesToHexString(req), 0);
        logger.debug("encryData:" + encryData);
        String sendStr = "E000" + encryData;
        byte[] resData = new byte[300];
        int resLen = androidDeviceApi.transCommand(GPMethods.str2bytes(sendStr), sendStr.length() / 2, resData, timeout);
        if (resLen < 0) {
            return resLen;
        } else {
            byte[] encryRes = new byte[resLen];
            System.arraycopy(resData, 0, encryRes, 0, resLen);
            logger.debug("encryRes:" + GPMethods.bytesToHexString(encryRes));
            if (encryRes[0] == (byte) 0xE0) {
                if (encryRes[1] == 0x01) {
                    generateEncryKey();
                    return -5;
                }
            } else {
                return -6;
            }
            int deviceResLen = resLen - 2;
            byte[] response = new byte[deviceResLen];
            System.arraycopy(encryRes, 2, response, 0, deviceResLen);
            logger.debug("response:" + GPMethods.bytesToHexString(response));
            String decryData = GPMethods.desecb(g_SessionsKey, GPMethods.bytesToHexString(response), 1);
            logger.debug("decryData:" + decryData);
            if (decryData == null) {
                generateEncryKey();
                return -5;
            }
            byte[] deviceRes;
            deviceRes = GPMethods.str2bytes(decryData);
            System.arraycopy(deviceRes, 0, res, 0, deviceResLen);
            logger.debug("res:" + GPMethods.bytesToHexString(res));
            return deviceResLen;
        }
    }

}
