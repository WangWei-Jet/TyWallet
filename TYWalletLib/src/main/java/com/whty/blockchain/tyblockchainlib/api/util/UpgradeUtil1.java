package com.whty.blockchain.tyblockchainlib.api.util;

import android.util.Log;

import com.whty.blockchain.tyblockchainlib.api.core.TyWalletBtDriver;
import com.whty.bluetoothsdk.util.Utils;

public class UpgradeUtil1 {

    private TyWalletBtDriver tyWalletBtDriver;

    private PkgHead pkgHead;

    private byte[] fileContent = null;
    private byte[] fileData = new byte[256];

    private String TAG = this.getClass().getName();

    public static short ccitt_crc_table[] = {0x0000, 0x1021, 0x2042, 0x3063,
            0x4084, 0x50a5, 0x60c6, 0x70e7, (short) 0x8108, (short) 0x9129,
            (short) 0xa14a, (short) 0xb16b, (short) 0xc18c, (short) 0xd1ad,
            (short) 0xe1ce, (short) 0xf1ef, 0x1231, 0x0210, 0x3273, 0x2252,
            0x52b5, 0x4294, 0x72f7, 0x62d6, (short) 0x9339, (short) 0x8318,
            (short) 0xb37b, (short) 0xa35a, (short) 0xd3bd, (short) 0xc39c,
            (short) 0xf3ff, (short) 0xe3de, 0x2462, 0x3443, 0x0420, 0x1401,
            0x64e6, 0x74c7, 0x44a4, 0x5485, (short) 0xa56a, (short) 0xb54b,
            (short) 0x8528, (short) 0x9509, (short) 0xe5ee, (short) 0xf5cf,
            (short) 0xc5ac, (short) 0xd58d, 0x3653, 0x2672, 0x1611, 0x0630,
            0x76d7, 0x66f6, 0x5695, 0x46b4, (short) 0xb75b, (short) 0xa77a,
            (short) 0x9719, (short) 0x8738, (short) 0xf7df, (short) 0xe7fe,
            (short) 0xd79d, (short) 0xc7bc, 0x48c4, 0x58e5, 0x6886, 0x78a7,
            0x0840, 0x1861, 0x2802, 0x3823, (short) 0xc9cc, (short) 0xd9ed,
            (short) 0xe98e, (short) 0xf9af, (short) 0x8948, (short) 0x9969,
            (short) 0xa90a, (short) 0xb92b, 0x5af5, 0x4ad4, 0x7ab7, 0x6a96,
            0x1a71, 0x0a50, 0x3a33, 0x2a12, (short) 0xdbfd, (short) 0xcbdc,
            (short) 0xfbbf, (short) 0xeb9e, (short) 0x9b79, (short) 0x8b58,
            (short) 0xbb3b, (short) 0xab1a, 0x6ca6, 0x7c87, 0x4ce4, 0x5cc5,
            0x2c22, 0x3c03, 0x0c60, 0x1c41, (short) 0xedae, (short) 0xfd8f,
            (short) 0xcdec, (short) 0xddcd, (short) 0xad2a, (short) 0xbd0b,
            (short) 0x8d68, (short) 0x9d49, 0x7e97, 0x6eb6, 0x5ed5, 0x4ef4,
            0x3e13, 0x2e32, 0x1e51, 0x0e70, (short) 0xff9f, (short) 0xefbe,
            (short) 0xdfdd, (short) 0xcffc, (short) 0xbf1b, (short) 0xaf3a,
            (short) 0x9f59, (short) 0x8f78, (short) 0x9188, (short) 0x81a9,
            (short) 0xb1ca, (short) 0xa1eb, (short) 0xd10c, (short) 0xc12d,
            (short) 0xf14e, (short) 0xe16f, 0x1080, 0x00a1, 0x30c2, 0x20e3,
            0x5004, 0x4025, 0x7046, 0x6067, (short) 0x83b9, (short) 0x9398,
            (short) 0xa3fb, (short) 0xb3da, (short) 0xc33d, (short) 0xd31c,
            (short) 0xe37f, (short) 0xf35e, 0x02b1, 0x1290, 0x22f3, 0x32d2,
            0x4235, 0x5214, 0x6277, 0x7256, (short) 0xb5ea, (short) 0xa5cb,
            (short) 0x95a8, (short) 0x8589, (short) 0xf56e, (short) 0xe54f,
            (short) 0xd52c, (short) 0xc50d, 0x34e2, 0x24c3, 0x14a0, 0x0481,
            0x7466, 0x6447, 0x5424, 0x4405, (short) 0xa7db, (short) 0xb7fa,
            (short) 0x8799, (short) 0x97b8, (short) 0xe75f, (short) 0xf77e,
            (short) 0xc71d, (short) 0xd73c, 0x26d3, 0x36f2, 0x0691, 0x16b0,
            0x6657, 0x7676, 0x4615, 0x5634, (short) 0xd94c, (short) 0xc96d,
            (short) 0xf90e, (short) 0xe92f, (short) 0x99c8, (short) 0x89e9,
            (short) 0xb98a, (short) 0xa9ab, 0x5844, 0x4865, 0x7806, 0x6827,
            0x18c0, 0x08e1, 0x3882, 0x28a3, (short) 0xcb7d, (short) 0xdb5c,
            (short) 0xeb3f, (short) 0xfb1e, (short) 0x8bf9, (short) 0x9bd8,
            (short) 0xabbb, (short) 0xbb9a, 0x4a75, 0x5a54, 0x6a37, 0x7a16,
            0x0af1, 0x1ad0, 0x2ab3, 0x3a92, (short) 0xfd2e, (short) 0xed0f,
            (short) 0xdd6c, (short) 0xcd4d, (short) 0xbdaa, (short) 0xad8b,
            (short) 0x9de8, (short) 0x8dc9, 0x7c26, 0x6c07, 0x5c64, 0x4c45,
            0x3ca2, 0x2c83, 0x1ce0, 0x0cc1, (short) 0xef1f, (short) 0xff3e,
            (short) 0xcf5d, (short) 0xdf7c, (short) 0xaf9b, (short) 0xbfba,
            (short) 0x8fd9, (short) 0x9ff8, 0x6e17, 0x7e36, 0x4e55, 0x5e74,
            0x2e93, 0x3eb2, 0x0ed1, 0x1ef0};

    public UpgradeUtil1(TyWalletBtDriver tyWalletBtDriver) {
        this.tyWalletBtDriver = tyWalletBtDriver;

        pkgHead = new PkgHead();

    }


    public boolean upgradeDevice(byte[] file, UpgradeListener listener) {
        Log.d(TAG, "upgradeDevice invoked");
        boolean result = uD(file, listener);
        Log.d(TAG, "result:" + result);
        Log.d(TAG, "upgradeDevice invoked over");
        return result;
    }

    private boolean uD(byte[] file, UpgradeListener listener) {
        try {
            if (file == null) {
                Log.w(TAG, "upgrade file or the delegate is null");
                return false;
            }
            String pn = getDevicePN();
            if (pn == null) {
                Log.w(TAG, "failed fetching PN");
                return false;
            }
            pn = GPMethods.hexStr2Str(pn);
            Log.d(TAG, "pn = " + pn);
            if (pn.endsWith("71241")) {
                if (!saveFile41(file)) {
                    Log.w(TAG, "failed saving upgrade file");
                    if (listener != null) {
                        listener.upgradeFail(UpgradeListener.SAVE_FILE_FAIL);
                    }
                    return false;
                }
            } else if (pn.endsWith("71249") || pn.endsWith("63250")
                    || pn.endsWith("63251")) {
                if (!saveFile49(file)) {
                    Log.w(TAG, "failed saving upgrade file");
                    if (listener != null) {
                        listener.upgradeFail(UpgradeListener.SAVE_FILE_FAIL);
                    }
                    return false;
                }
            } else {
                if (!saveFile49(file)) {
                    Log.w(TAG, "failed saving upgrade file");

                    if (listener != null) {
                        listener.upgradeFail(UpgradeListener.SAVE_FILE_FAIL);
                    }
                    return false;
                }
            }

            if (!comparePN()) {
                Log.e(TAG, "PN not matched");
                if (listener != null) {
                    listener.upgradeFail(UpgradeListener.COMPARE_PN_FAIL);
                }
                return false;
            }

//            if (!compareHDVer()) {
//                Log.e(TAG, "hardware version not matched");
//                if (listener != null) {
//                    listener.upgradeFail(UpgradeListener.COMPARE_HDVER_FAIL);
//                }
//                return false;
//            }

//            if (!compareVersion()) {
//                Log.e(TAG, "version not matched");
//                listener.upgradeFail(UpgradeListener.COMPARE_VERSION_FAIL);
//                return false;
//            }

            if (Experimental()) {
                Log.d(TAG, "first step=>verification:success");
                if (clearAPP()) {
                    Log.d(TAG, "second step=>clear APP backup:success");
                    if (deviceDownload(listener)) {
                        Log.d(TAG, "third step=>download encrepted hardware program:success");
                        if (pn.endsWith("71249") || pn.endsWith("63250")
                                || pn.endsWith("63251")) {
                            if (downloadBackupArea49(listener)) {
                                Log.d(TAG, "fourth step=>download params for backup " +
                                        "area:success");
                                if (listener != null) {
                                    listener.upgradeDeviceSuccess();
                                }
                                return true;
                            } else {
                                Log.d(TAG, "fourth step=>download params for backup " +
                                        "area:fail");
                                // listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                            }
                        } else if (pn.endsWith("71241")) {
                            if (downloadBackupArea41(listener)) {
                                Log.d(TAG, "fourth step=>download params for backup " +
                                        "area:success");
                                if (listener != null) {
                                    listener.upgradeDeviceSuccess();
                                }
                                return true;
                            } else {
                                Log.d(TAG, "fourth step=>download params for backup " +
                                        "area:fail");
                                // listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                            }
                        } else {
                            Log.d(TAG, "pn = " + pn);
                            if (downloadBackupArea49(listener)) {
                                Log.d(TAG, "fourth step=>download params for backup " +
                                        "area:success");

                                if (listener != null) {
                                    listener.upgradeDeviceSuccess();
                                }
                                return true;
                            } else {
                                Log.d(TAG, "fourth step=>download params for backup " +
                                        "area:fail");
                                // listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                            }
                        }
                    } else {
                        // listener.upgradeFail(UpgradeListener.DEVICE_DOWNLOAD_FAIL);
                        Log.d(TAG, "third step=>download encrepted hardware program:fail");
                    }
                } else {
                    if (listener != null) {
                        listener.upgradeFail(UpgradeListener.CLEAR_APP_FAIL);
                    }
                    Log.d(TAG, "second step=>clear APP backup:fail");
                }
            } else {
                if (listener != null) {
                    listener.upgradeFail(UpgradeListener.EXPERIMENTAL_FAIL);
                }
                Log.d(TAG, "first step=>verification:fail");
            }
        } catch (Exception e) {
            Log.d(TAG, "error occues trying to upgrade device", e);
            if (listener != null) {
                listener.upgradeFail(UpgradeListener.EXCEPTION_FAIL);
            }
        }
        return false;
    }

    // 保存文件头部的PN和version,前512字节是文件头 41设备
    private boolean saveFile41(byte[] file) {
        if (fileContent != null) {
            fileContent = null;
        }
        if (file != null && file.length > 512) {
            byte[] PN = new byte[8];
            byte[] version = new byte[16];
            byte[] appLen = new byte[4];
            byte[] xor32App = new byte[4];
            byte[] encryptTransKey = new byte[16];
            byte[] hardVer = new byte[9];
            byte signflag;
            System.arraycopy(file, 0, PN, 0, 8);
            System.arraycopy(file, 244, appLen, 0, 4);
            System.arraycopy(file, 256, xor32App, 0, 4);
            System.arraycopy(file, 324, encryptTransKey, 0, 16);
            System.arraycopy(file, 357, hardVer, 0, 9);
            signflag = file[373];
            Log.d("singflag", file[372] + "  " + file[373] + "  "
                    + file[374]);
            if (signflag == 0x01) {
                Log.d(TAG, "flag with sign");
                System.arraycopy(file, 512, fileData, 0, 256);
            }
            Log.d(TAG,"file header appLen:" + Utils.bytesToHexString(appLen, 4)
                            + "\nxor：" + Utils.bytesToHexString(xor32App, 4));
            System.arraycopy(file, 260, version, 0, 16);
            pkgHead.setProductId(PN);
            pkgHead.setAppLen(appLen);
            pkgHead.setXor32AppSrc(xor32App);
            pkgHead.setAppVer(version);
            pkgHead.setEncryptTransKey(encryptTransKey);
            pkgHead.setHardVer(hardVer);
            pkgHead.setSignflag(signflag);
            Log.d(TAG,"encryptTransKey result of file header"
                            + Utils.bytesToHexString(encryptTransKey, 16));
            Log.d(TAG,"PN of file header:" + Utils.bytesToHexString(PN, 8)
                            + "\nversion："
                            + Utils.bytesToHexString(version, 16));
            fileContent = new byte[file.length - 512];
            if (pkgHead.getSignflag() == 0x00) {
                System.arraycopy(file, 512, fileContent, 0, file.length - 512);
            }
            if (pkgHead.getSignflag() == 0x01) {
                Log.d(TAG, "signflag = 1");
                System.arraycopy(file, 768, fileContent, 0, file.length - 768);
            }
            Log.d(TAG, "file content:"
                    + Utils.bytesToHexString(fileContent,
                    fileContent.length));
            return true;
        }
        return false;
    }

    // 49,50,51设备
    private boolean saveFile49(byte[] file) {
        if (fileContent != null) {
            fileContent = null;
        }
        if (file != null && file.length > 512) {
            byte[] PN = new byte[8];
            byte[] version = new byte[16];
            byte[] appLen = new byte[4];
            byte[] xor32App = new byte[4];
            byte[] encryptTransKey = new byte[16];
            byte[] key3 = new byte[16];
            byte[] hardVer = new byte[9];
            byte signflag;
            System.arraycopy(file, 0, PN, 0, 8);
            System.arraycopy(file, 244, appLen, 0, 4);
            System.arraycopy(file, 256, xor32App, 0, 4);
            System.arraycopy(file, 324, encryptTransKey, 0, 16);
            System.arraycopy(file, 308, key3, 0, 16);
            System.arraycopy(file, 357, hardVer, 0, 9);
            signflag = file[373];
            Log.d("singflag", file[372] + "  " + file[373] + "  "
                    + file[374]);
            if (signflag == 0x01) {
                Log.d(TAG, "flag with sign");
                System.arraycopy(file, 512, fileData, 0, 256);
            }
            Log.d(TAG,"file header appLen:" + Utils.bytesToHexString(appLen, 4)
                            + "\nxor：" + Utils.bytesToHexString(xor32App, 4));
            System.arraycopy(file, 260, version, 0, 16);
            pkgHead.setProductId(PN);
            pkgHead.setAppLen(appLen);
            pkgHead.setXor32AppSrc(xor32App);
            pkgHead.setAppVer(version);
            pkgHead.setEncryptTransKey(encryptTransKey);
            pkgHead.setKey3(key3);
            pkgHead.setHardVer(hardVer);
            pkgHead.setSignflag(signflag);
            Log.d(TAG,"PN of file header:"+Utils.bytesToHexString(PN, 8)
                    + "\nversion：" + Utils.bytesToHexString(version, 16));

            if (pkgHead.getSignflag() == 0x00) {
                fileContent = new byte[file.length - 512];
                System.arraycopy(file, 512, fileContent, 0, file.length - 512);
            }
            if (pkgHead.getSignflag() == 0x01) {
                Log.d(TAG, "signflag = 1");
                fileContent = new byte[file.length - 768];
                System.arraycopy(file, 768, fileContent, 0, file.length - 768);
            }
            return true;
        }
        return false;
    }

    // 比较pn号是否相同
    private boolean comparePN() {
        String devicePN = getDevicePN();
        if (devicePN == null) {
            Log.d(TAG, "failed fetching device PN");
            return false;
        }
        if (devicePN.endsWith("3633323530")) {
            byte[] cmd = {(byte) 0xfe, 0x01, 0x01, 0x02, 0x00};
            byte[] res = new byte[300];
            int ret = tyWalletBtDriver.transCommandAPDU(cmd, cmd.length, res, 3000);
            if (ret > 2 && res[ret - 1] == (byte) 0x00
                    && res[ret - 2] == (byte) 0x90) {
                String result = Utils.bytesToHexString(res, ret - 2).substring(
                        4, 6);
                Log.d(TAG, "result = " + result);
                if ("05".equals(result)) {
                    devicePN = "3633323532";
                }
            }
        }
        byte[] productId = pkgHead.getProductId();
        int offset = 0;
        if (productId != null) {
            for (int i = 0; i < productId.length; i++) {
                if (productId[i] != (byte) 0x00) {
                    offset += 1;
                } else {
                    break;
                }
            }
            String filePN = Utils.bytesToHexString(productId, offset);
            Log.d(TAG, "file PN:" + filePN);
            if (devicePN.indexOf(filePN) != -1) {
                return true;
            }
        }
        return false;
    }


    public String getDevicePN() {
        Log.d(TAG, "getDevicePN invoked");
        String result = gDPN();
        Log.d(TAG, "result:" + result);
        Log.d(TAG, "getDevicePN invoked over");
        return result;
    }

    private String gDPN() {
        try {
            byte[] cmd_send = GPMethods.str2bytes("FE01010300");
            byte[] response = new byte[300];
            Log.d(TAG, "prepare to get device PN");
            int ret = tyWalletBtDriver.transCommandAPDU(cmd_send, cmd_send.length,
                    response, 3000);
            if (ret > 0) {
                byte[] res = new byte[ret];
                System.arraycopy(response, 0, res, 0, ret);
                if (res[ret - 2] == (byte) 0x90 && res[ret - 1] == 0x00) {
                    // 返回的数据时TLV格式的
                    String devicePN = Utils.bytesToHexString(res, ret - 2);
                    TLVParser tlvParser = new TLVParser(new String[]{"03"});
                    TLV[] tlvs = tlvParser.getTLVs(GPMethods
                            .str2bytes(devicePN));
                    devicePN = GPMethods.bytesToHexString(tlvs[0].getData());
                    return devicePN;
                } else {
                    //
                }
            } else {
                Log.d(TAG, "command interaction timeout!");
            }
        } catch (Exception e) {
            Log.e(TAG, "error occurs trying to get device PN",e);
        }
        return null;
    }

    // 比较硬件版本号
    private boolean compareHDVer() {
        String HDVer = getHardwareVersion();
        if (HDVer == null) {
            Log.d(TAG, "failed fetching hardware version");
            return false;
        }
        HDVer = GPMethods.hexStr2Str(HDVer);
        byte[] hardVer = pkgHead.getHardVer();
        if ("000000000000000000".equals(GPMethods.bytesToHexString(hardVer))) {
            Log.d(TAG, "No HDVer in file");
            return true;
        }
        if (hardVer != null) {
            String hardVerASCII = convertHexToString(Utils.bytesToHexString(
                    hardVer, hardVer.length));
            Log.d(TAG, "hardware version of file:" + hardVerASCII);
            hardVerASCII = hardVerASCII.replace("V", "");
            hardVerASCII = hardVerASCII.replace("R", "");
            hardVerASCII = hardVerASCII.replace("D", "");
            hardVerASCII = hardVerASCII.replace(".", "");
            Log.d(TAG, "hardware version of file:" + hardVerASCII);
            HDVer = HDVer.replace("V", "");
            HDVer = HDVer.replace("R", "");
            HDVer = HDVer.replace("D", "");
            HDVer = HDVer.replace(".", "");
            Log.e(TAG, "hardware version of device:" + HDVer);
            boolean flag1 = HDVer.substring(0, 3).equals(
                    hardVerASCII.substring(0, 3));
            boolean flag2 = HDVer.charAt(4) == hardVerASCII.charAt(4);
            if (flag1 && flag2) {
                return true;
            }
        }
        return false;
    }

    public String getHardwareVersion() {
        Log.d(TAG, "getHardwareVersion invoked");
        String result = gHV();
        Log.d(TAG, "result:" + result);
        Log.d(TAG, "getHardwareVersion invoked over");
        return result;
    }

    public String gHV() {
        try {
            byte[] cmd_send = GPMethods.str2bytes("FE01010500");
            byte[] response = new byte[300];
            int ret = tyWalletBtDriver.transCommandAPDU(cmd_send, cmd_send.length,
                    response, 3000);
            if (ret > 0) {
                byte[] res = new byte[ret];
                System.arraycopy(response, 0, res, 0, ret);
                if (res[ret - 2] == (byte) 0x90 && res[ret - 1] == 0x00) {
                    // 返回的数据时TLV格式的
                    String hardwareVersion = Utils.bytesToHexString(res,
                            ret - 2);
                    TLVParser tlvParser = new TLVParser(new String[]{"05"});
                    TLV[] tlvs = tlvParser.getTLVs(GPMethods
                            .str2bytes(hardwareVersion));
                    hardwareVersion = GPMethods.bytesToHexString(tlvs[0]
                            .getData());
                    return hardwareVersion;
                } else {
                    //
                }
            } else {
                Log.d(TAG, "command interaction timeout!");
            }
        } catch (Exception e) {
            Log.e(TAG, "gHV: 异常", e);
        }
        return null;
    }

    // 比较版本号
    private boolean compareVersion() {
        String deviceVersion = getDeviceVersion();
        if (deviceVersion == null) {
            Log.d(TAG, "failed fetching device version");
            return false;
        }
        byte[] AppVer = pkgHead.getAppVer();
        if (AppVer != null) {
            String fileVerASCII = convertHexToString(Utils.bytesToHexString(
                    AppVer, AppVer.length));
            Log.d(TAG, "version of file:" + fileVerASCII);
            int index = fileVerASCII.indexOf("V");
            if (index != -1) {
                fileVerASCII = fileVerASCII.substring(index + 1,
                        fileVerASCII.length());
                fileVerASCII = fileVerASCII.replace("R", "");
                fileVerASCII = fileVerASCII.replace("D", "");
                fileVerASCII = fileVerASCII.replace(".", "");
                if (fileVerASCII.startsWith("0")) {
                    fileVerASCII = fileVerASCII.substring(1,
                            fileVerASCII.length());
                }
                Log.d(TAG, "version of file:" + fileVerASCII);
            }
            String deviceASCII = deviceVersion;
            Log.d(TAG, "version of device:" + deviceASCII);
            deviceASCII = deviceASCII.replace("M", "");
            int deindex = deviceASCII.indexOf("V");
            if (deindex != -1) {
                deviceASCII = deviceASCII.substring(deindex + 1,
                        deviceASCII.length());
                deviceASCII = deviceASCII.replace("R", "");
                deviceASCII = deviceASCII.replace("D", "");
                deviceASCII = deviceASCII.replace(".", "");
                Log.d(TAG, "version of device:" + deviceASCII);

            } else {
                Log.d(TAG, "failed fetching device version");
                return false;
            }
            Log.d(TAG, deviceASCII.substring(1, 3) + "----"
                    + fileVerASCII.substring(1, 3));
            boolean flag = deviceASCII.substring(1, 3).equals(
                    fileVerASCII.substring(1, 3)) ? (deviceASCII
                    .equals(fileVerASCII) ? false : true) : false;
            return flag;
        }
        return false;
    }

    private boolean Experimental() {
        String experimentalString = "WHTY_FIRMWARE_UPGRADE";
        String hexExper = convertStringToHex(experimentalString);
        byte[] hex = Utils.hexString2Bytes(hexExper);
        String len = Integer.toHexString(hex.length);
        byte[] len1 = Utils.hexString2Bytes(len);
        Log.d("Experimental", Utils.bytesToHexString(hex, hex.length));
        byte[] cmd = new byte[5 + hex.length];
        cmd[0] = (byte) 0xFD;
        cmd[1] = (byte) 0x04;
        cmd[2] = (byte) 0x00;
        cmd[3] = (byte) 0x00;
        cmd[4] = len1[0];
        System.arraycopy(hex, 0, cmd, 5, hex.length);
        Log.d(TAG, "verification command:"
                + Utils.bytesToHexString(cmd, cmd.length));
        byte[] res = new byte[300];
        int ret = tyWalletBtDriver.transCommandAPDU(cmd, cmd.length, res, 3000);
        if (ret > 0 && res[ret - 1] == (byte) 0x00
                && res[ret - 2] == (byte) 0x90) {
            return true;
        }
        return false;
    }


    public String getDeviceVersion() {
        Log.d(TAG, "getDeviceVersion invoked");
        String result = gDV();
        Log.d(TAG, "result:" + result);
        Log.d(TAG, "getDeviceVersion invoked over");
        return result;
    }

    private String gDV() {
        try {
            byte[] cmd_send = GPMethods.str2bytes("FE01010400");
            byte[] response = new byte[300];
            Log.d(TAG, "prepare to get device version");
            int ret = tyWalletBtDriver.transCommandAPDU(cmd_send, cmd_send.length,
                    response, 3000);
            if (ret > 0) {
                byte[] res = new byte[ret];
                System.arraycopy(response, 0, res, 0, ret);
                if (res[ret - 2] == (byte) 0x90 && res[ret - 1] == 0x00) {
                    // 返回的数据时TLV格式的
                    String deviceVersion = Utils.bytesToHexString(res, ret - 2);
                    TLVParser tlvParser = new TLVParser(new String[]{"04"});
                    TLV[] tlvs = tlvParser.getTLVs(GPMethods
                            .str2bytes(deviceVersion));
                    deviceVersion = GPMethods.bytesToHexString(tlvs[0]
                            .getData());
                    return convertHexToString(deviceVersion);
                } else {
                    //
                }
            } else {
                Log.d(TAG, "command interaction timeout!");
            }
        } catch (Exception e) {
            Log.e(TAG, "error occurs trying to get device version",e);
        }
        return null;
    }

    // 擦除App备份区
    private boolean clearAPP() {
        byte[] cmd = {(byte) 0xfd, 0x05, 0x00, 0x00, 0x00};
        byte[] res = new byte[300];
        int ret = tyWalletBtDriver.transCommandAPDU(cmd, cmd.length, res, 3000);
        if (ret > 0 && res[ret - 1] == (byte) 0x00
                && res[ret - 2] == (byte) 0x90) {
            return true;
        }
        return false;
    }

    // 下载固件密文
    private boolean deviceDownload(UpgradeListener listener) {
        int startAdress = 0;
        int len = 248;
        int offset = 0;
        if (fileContent != null) {
            byte[] cmd = new byte[259];
            cmd[0] = (byte) 0xFD;
            cmd[1] = 0x06;
            cmd[2] = 0x00;
            cmd[3] = 0x00;
            cmd[4] = 0x00;// 待修改的P3
            // 起始位置的四个字节 的长度,采用大端模式（数据高字节放在内存低地址，数据低字节放在内存高地址）
            int count = fileContent.length / len;
            if (count != 0) {
                for (int i = 0; i < count; i++) {
                    cmd[4] = (byte) (len + 6);
                    Log.d(TAG, "offset:" + startAdress + "");
                    cmd[5] = (byte) ((startAdress >> 24) & 0xff);
                    cmd[6] = (byte) ((startAdress >> 16) & 0xff);
                    cmd[7] = (byte) ((startAdress >> 8) & 0xff);
                    cmd[8] = (byte) (startAdress & 0xff);
                    System.arraycopy(fileContent, i * len, cmd, 9, 248);
                    byte[] crc = new byte[cmd.length - 5 - 2];
                    System.arraycopy(cmd, 5, crc, 0, crc.length);
                    String crc16 = GetCrc16(crc, crc.length);
                    byte[] crcbyte16 = Utils.hexString2Bytes(crc16);
                    cmd[257] = crcbyte16[0];
                    cmd[258] = crcbyte16[1];
                    offset += len;
                    startAdress += 248;
                    if (listener != null) {
                        listener.showProgress(startAdress * 100
                                / fileContent.length);
                    }
                    byte[] res = new byte[300];
                    Log.d(TAG, "download pkg"
                            + Utils.bytesToHexString(cmd, cmd.length));
                    int ret = tyWalletBtDriver
                            .transCommandAPDU(cmd, cmd.length, res, 3000);
                    if (ret > 0 && res[ret - 1] == (byte) 0x00
                            && res[ret - 2] == (byte) 0x90) {
                        Log.d(TAG, "success code:"
                                + Utils.bytesToHexString(res, ret));
                    } else {
                        if (ret > 0) {
                            Log.d(TAG, "fail code:"
                                    + Utils.bytesToHexString(res, ret));
                            if (res[ret - 1] == (byte) 0x82
                                    && res[ret - 2] == (byte) 0x6A) {
                                if (listener != null) {
                                    listener.upgradeFail(UpgradeListener.CONDITION_NOT_MEET);
                                }
                            } else if (res[ret - 1] == (byte) 0x84
                                    && res[ret - 2] == (byte) 0x6A) {
                                if (listener != null) {
                                    listener.upgradeFail(UpgradeListener.NO_STORAGE_SPACE);
                                }
                            } else if (res[ret - 1] == (byte) 0x88
                                    && res[ret - 2] == (byte) 0x69) {
                                if (listener != null) {
                                    listener.upgradeFail(UpgradeListener.DATA_BACKUP_FAIL);
                                }
                            } else {
                                if (listener != null) {
                                    listener.upgradeFail(UpgradeListener.DEVICE_DOWNLOAD_FAIL);
                                }
                            }
                        } else {
                            if (listener != null) {
                                listener.upgradeFail(UpgradeListener.DEVICE_DOWNLOAD_FAIL);
                            }
                        }
                        return false;
                    }
                }
            }
            int count1 = fileContent.length - count * len;
            Log.d("count1", count1 + "" + "count*len" + count * len);
            cmd = new byte[9 + count1 + 2];
            cmd[0] = (byte) 0xFD;
            cmd[1] = 0x06;
            cmd[2] = 0x00;
            cmd[3] = 0x00;
            if (count1 != 0) {
                Log.d("adress", startAdress + "");
                Log.d("adress", startAdress + "");
                cmd[4] = (byte) (count1 + 6);
                // 起始位置的四个字节 的长度
                cmd[5] = (byte) ((startAdress >> 24) & 0xff);
                cmd[6] = (byte) ((startAdress >> 16) & 0xff);
                cmd[7] = (byte) ((startAdress >> 8) & 0xff);
                cmd[8] = (byte) (startAdress & 0xff);
                System.arraycopy(fileContent, count * len, cmd, 9, count1);
                byte[] crc = new byte[cmd.length - 5 - 2];
                System.arraycopy(cmd, 5, crc, 0, crc.length);
                String crc16 = GetCrc16(crc, crc.length);
                byte[] crcbyte16 = Utils.hexString2Bytes(crc16);
                cmd[9 + count1] = crcbyte16[0];
                cmd[9 + count1 + 1] = crcbyte16[1];
                Log.d(TAG, "send last downloaded data:"
                        + Utils.bytesToHexString(cmd, cmd.length));
                byte[] res = new byte[300];
                int ret = tyWalletBtDriver.transCommandAPDU(cmd, cmd.length, res, 3000);
                if (ret > 0 && res[ret - 1] == (byte) 0x00
                        && res[ret - 2] == (byte) 0x90) {
                    return true;
                } else {
                    if (ret > 0) {
                        Log.d(TAG, "fail code of last step download"
                                + Utils.bytesToHexString(res, ret));
                        if (res[ret - 1] == (byte) 0x82
                                && res[ret - 2] == (byte) 0x6A) {
                            if (listener != null) {
                                listener.upgradeFail(UpgradeListener.CONDITION_NOT_MEET);
                            }
                        } else if (res[ret - 1] == (byte) 0x84
                                && res[ret - 2] == (byte) 0x6A) {
                            if (listener != null) {
                                listener.upgradeFail(UpgradeListener.NO_STORAGE_SPACE);
                            }
                        } else if (res[ret - 1] == (byte) 0x88
                                && res[ret - 2] == (byte) 0x69) {
                            if (listener != null) {
                                listener.upgradeFail(UpgradeListener.DATA_BACKUP_FAIL);
                            }
                        } else {
                            if (listener != null) {
                                listener.upgradeFail(UpgradeListener.DEVICE_DOWNLOAD_FAIL);
                            }
                        }
                    } else {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.DEVICE_DOWNLOAD_FAIL);
                        }
                    }
                    return false;
                }
            } else {
                Log.d(TAG, "every time 248 bytes and no left:success");
                return true;
            }
        }
        if (listener != null) {
            listener.upgradeFail(UpgradeListener.SAVE_FILE_FAIL);
        }
        return false;
    }

    // 下载备份区
    private boolean downloadBackupArea49(UpgradeListener listener) {
        byte[] applen = pkgHead.getAppLen();
        byte[] xor32 = pkgHead.getXor32AppSrc();
        byte[] key3 = pkgHead.getKey3();
        byte signflag = pkgHead.getSignflag();
        if (signflag == 0x00) {
            byte[] cmd = new byte[33];
            if (applen != null && xor32 != null & key3 != null) {
                cmd[0] = (byte) 0xFD;
                cmd[1] = (byte) 0x07;
                cmd[2] = (byte) 0x00;
                cmd[3] = (byte) 0x00;
                cmd[4] = (byte) 0x1C;
                System.arraycopy(key3, 0, cmd, 5, 16);
                byte[] a = new byte[4];
                for (int i = 0; i < applen.length; i++) {
                    a[i] = applen[4 - i - 1];
                }
                System.arraycopy(a, 0, cmd, 21, 4);
                System.arraycopy(xor32, 0, cmd, 25, 4);
                long c32 = calcXor32(fileContent, fileContent.length);
                Log.d(TAG, "hardware verification total:" + c32 + "");
                cmd[29] = (byte) ((c32 >> 24) & 0xff);
                cmd[30] = (byte) ((c32 >> 16) & 0xff);
                cmd[31] = (byte) ((c32 >> 8) & 0xff);
                cmd[32] = (byte) (c32 & 0xff);
                byte[] res = new byte[300];
                int ret = tyWalletBtDriver.transCommandAPDU(cmd, cmd.length, res, 3000);
                if (ret > 0 && res[ret - 1] == (byte) 0x00
                        && res[ret - 2] == (byte) 0x90) {
                    return true;
                }
                if (ret > 0) {
                    Log.d(TAG, "download backup area:"
                            + Utils.bytesToHexString(res, ret));
                    if (res[ret - 1] == (byte) 0x82
                            && res[ret - 2] == (byte) 0x6A) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.CONDITION_NOT_MEET);
                        }
                    } else if (res[ret - 1] == (byte) 0x85
                            && res[ret - 2] == (byte) 0x69) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.NOT_WRITTEN_KEY);
                        }
                    } else if (res[ret - 1] == (byte) 0x88
                            && res[ret - 2] == (byte) 0x69) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.DATA_BACKUP_FAIL);
                        }
                    } else if ((res[ret - 1] == (byte) 0x09 && res[ret - 2] == (byte) 0x80)
                            || (res[ret - 1] == (byte) 0xFF && res[ret - 2] == (byte) 0x80)
                            || (res[ret - 1] == (byte) 0x02 && res[ret - 2] == (byte) 0x82)
                            || (res[ret - 1] == (byte) 0x01 && res[ret - 2] == (byte) 0x84)) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.VERIFY_SIGN_FAIL);
                        }
                    } else {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                    }
                }
                return false;
            }
        }
        if (signflag == 0x01) {
            byte[] cmd = new byte[291];
            if (applen != null && xor32 != null & key3 != null) {
                cmd[0] = (byte) 0xFD;
                cmd[1] = (byte) 0x07;
                cmd[2] = (byte) 0x00;
                cmd[3] = (byte) 0x01;
                cmd[4] = (byte) 0x00;
                cmd[5] = (byte) 0x01;
                cmd[6] = (byte) 0x1C;
                System.arraycopy(key3, 0, cmd, 7, 16);
                byte[] a = new byte[4];
                for (int i = 0; i < applen.length; i++) {
                    a[i] = applen[4 - i - 1];
                }
                System.arraycopy(a, 0, cmd, 23, 4);
                System.arraycopy(xor32, 0, cmd, 27, 4);
                long c32 = calcXor32(fileContent, fileContent.length);
                Log.d(TAG, "enc hardware total:" + c32 + "");
                cmd[31] = (byte) ((c32 >> 24) & 0xff);
                cmd[32] = (byte) ((c32 >> 16) & 0xff);
                cmd[33] = (byte) ((c32 >> 8) & 0xff);
                cmd[34] = (byte) (c32 & 0xff);
                System.arraycopy(fileData, 0, cmd, 35, fileData.length);
                byte[] res = new byte[300];
                int ret = tyWalletBtDriver.transCommandAPDU(cmd, cmd.length, res, 3000);
                if (ret > 0 && res[ret - 1] == (byte) 0x00
                        && res[ret - 2] == (byte) 0x90) {
                    return true;
                }
                if (ret > 0) {
                    Log.d(TAG, "download backup area:"
                            + Utils.bytesToHexString(res, ret));
                    if (res[ret - 1] == (byte) 0x82
                            && res[ret - 2] == (byte) 0x6A) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.CONDITION_NOT_MEET);
                        }
                    } else if (res[ret - 1] == (byte) 0x85
                            && res[ret - 2] == (byte) 0x69) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.NOT_WRITTEN_KEY);
                        }
                    } else if (res[ret - 1] == (byte) 0x88
                            && res[ret - 2] == (byte) 0x69) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.DATA_BACKUP_FAIL);
                        }
                    } else if ((res[ret - 1] == (byte) 0x09 && res[ret - 2] == (byte) 0x80)
                            || (res[ret - 1] == (byte) 0xFF && res[ret - 2] == (byte) 0x80)
                            || (res[ret - 1] == (byte) 0x02 && res[ret - 2] == (byte) 0x82)
                            || (res[ret - 1] == (byte) 0x01 && res[ret - 2] == (byte) 0x84)) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.VERIFY_SIGN_FAIL);
                        }
                    } else {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                    }
                }
                return false;
            }
        }
        if (listener != null) {
            listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
        }
        return false;
    }

    // 下载备份区
    private boolean downloadBackupArea41(UpgradeListener listener) {
        byte[] applen = pkgHead.getAppLen();
        byte[] xor32 = pkgHead.getXor32AppSrc();
        byte[] encryptTransKey = pkgHead.getEncryptTransKey();
        byte signflag = pkgHead.getSignflag();
        if (signflag == 0x00) {
            byte[] cmd = new byte[33];
            if (applen != null && xor32 != null & encryptTransKey != null) {
                cmd[0] = (byte) 0xFD;
                cmd[1] = (byte) 0x07;
                cmd[2] = (byte) 0x00;
                cmd[3] = (byte) 0x00;
                cmd[4] = (byte) 0x1C;
                System.arraycopy(encryptTransKey, 0, cmd, 5, 16);
                byte[] a = new byte[4];
                for (int i = 0; i < applen.length; i++) {
                    a[i] = applen[4 - i - 1];
                }
                System.arraycopy(a, 0, cmd, 21, 4);
                System.arraycopy(xor32, 0, cmd, 25, 4);
                long c32 = calcXor32(fileContent, fileContent.length);
                Log.d(TAG, "enc hardware total:" + c32 + "");
                cmd[29] = (byte) ((c32 >> 24) & 0xff);
                cmd[30] = (byte) ((c32 >> 16) & 0xff);
                cmd[31] = (byte) ((c32 >> 8) & 0xff);
                cmd[32] = (byte) (c32 & 0xff);
                byte[] res = new byte[300];
                int ret = tyWalletBtDriver.transCommandAPDU(cmd, cmd.length, res, 3000);
                if (ret > 0 && res[ret - 1] == (byte) 0x00
                        && res[ret - 2] == (byte) 0x90) {
                    return true;
                }
                if (ret > 0) {
                    Log.d(TAG, "download backup area:"
                            + Utils.bytesToHexString(res, ret));
                    if (res[ret - 1] == (byte) 0x82
                            && res[ret - 2] == (byte) 0x6A) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.CONDITION_NOT_MEET);
                        }
                    } else if (res[ret - 1] == (byte) 0x85
                            && res[ret - 2] == (byte) 0x69) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.NOT_WRITTEN_KEY);
                        }
                    } else if (res[ret - 1] == (byte) 0x88
                            && res[ret - 2] == (byte) 0x69) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.DATA_BACKUP_FAIL);
                        }
                    } else if ((res[ret - 1] == (byte) 0x09 && res[ret - 2] == (byte) 0x80)
                            || (res[ret - 1] == (byte) 0xFF && res[ret - 2] == (byte) 0x80)
                            || (res[ret - 1] == (byte) 0x02 && res[ret - 2] == (byte) 0x82)
                            || (res[ret - 1] == (byte) 0x01 && res[ret - 2] == (byte) 0x84)) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.VERIFY_SIGN_FAIL);
                        }
                    } else {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                    }
                }
                return false;
            }
        }
        if (signflag == 0x01) {
            byte[] cmd = new byte[291];
            if (applen != null && xor32 != null & encryptTransKey != null) {
                cmd[0] = (byte) 0xFD;
                cmd[1] = (byte) 0x07;
                cmd[2] = (byte) 0x00;
                cmd[3] = (byte) 0x01;
                cmd[4] = (byte) 0x00;
                cmd[5] = (byte) 0x01;
                cmd[6] = (byte) 0x1C;
                System.arraycopy(encryptTransKey, 0, cmd, 7, 16);
                byte[] a = new byte[4];
                for (int i = 0; i < applen.length; i++) {
                    a[i] = applen[4 - i - 1];
                }
                System.arraycopy(a, 0, cmd, 23, 4);
                System.arraycopy(xor32, 0, cmd, 27, 4);
                long c32 = calcXor32(fileContent, fileContent.length);
                Log.d(TAG, "enc hardware total:" + c32 + "");
                cmd[31] = (byte) ((c32 >> 24) & 0xff);
                cmd[32] = (byte) ((c32 >> 16) & 0xff);
                cmd[33] = (byte) ((c32 >> 8) & 0xff);
                cmd[34] = (byte) (c32 & 0xff);
                System.arraycopy(fileData, 0, cmd, 35, fileData.length);
                byte[] res = new byte[300];
                int ret = tyWalletBtDriver.transCommandAPDU(cmd, cmd.length, res, 3000);
                if (ret > 0 && res[ret - 1] == (byte) 0x00
                        && res[ret - 2] == (byte) 0x90) {
                    return true;
                }
                if (ret > 0) {
                    Log.d(TAG, "download backup area:"
                            + Utils.bytesToHexString(res, ret));
                    if (res[ret - 1] == (byte) 0x82
                            && res[ret - 2] == (byte) 0x6A) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.CONDITION_NOT_MEET);
                        }
                    } else if (res[ret - 1] == (byte) 0x85
                            && res[ret - 2] == (byte) 0x69) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.NOT_WRITTEN_KEY);
                        }
                    } else if (res[ret - 1] == (byte) 0x88
                            && res[ret - 2] == (byte) 0x69) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.DATA_BACKUP_FAIL);
                        }
                    } else if ((res[ret - 1] == (byte) 0x09 && res[ret - 2] == (byte) 0x80)
                            || (res[ret - 1] == (byte) 0xFF && res[ret - 2] == (byte) 0x80)
                            || (res[ret - 1] == (byte) 0x02 && res[ret - 2] == (byte) 0x82)
                            || (res[ret - 1] == (byte) 0x01 && res[ret - 2] == (byte) 0x84)) {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.VERIFY_SIGN_FAIL);
                        }
                    } else {
                        if (listener != null) {
                            listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
                    }
                }
                return false;
            }
        }
        if (listener != null) {
            listener.upgradeFail(UpgradeListener.DOWNLOAD_BACKUP_AREA_FAIL);
        }
        return false;
    }

    // 将十六进制字符串转成ASCII码
    private String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        // 49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            if ("00".equals(output)) {
                return sb.toString();
            }
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

    // 将字符串转成16进制字符串
    private String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

    // crc16校验和
    private String GetCrc16(byte[] pBuf, int len) {
        short crc = 0;
        int i = 0;
        while (len-- > 0) {
            crc = (short) (ccitt_crc_table[((crc >> 8) ^ (pBuf[i])) & 0xFF] ^ (crc << 8));
            i++;
        }
        String string = Integer.toHexString(crc & 0xFFFF);
        while (string.length() < 4) {
            string = "0" + string;
        }
        return string;
    }

    private long calcXor32(byte[] data, int length) {
        long ulXor = -1;
        for (long i = 0; i < length; i += 4) {
            ulXor ^= (data[(int) i] | (data[(int) (i + 1)] << 1)
                    | (data[(int) (i + 2)] << 2) | (data[(int) (i + 3)] << 3));
        }
        return ulXor;
    }
}
