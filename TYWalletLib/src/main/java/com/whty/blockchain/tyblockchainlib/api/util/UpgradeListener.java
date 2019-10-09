package com.whty.blockchain.tyblockchainlib.api.util;

public interface UpgradeListener {
	public static final int UPGRADE_FILE_NOT_FOUND = 31;

	public static final int SAVE_FILE_FAIL = 41;
	public static final int COMPARE_PN_FAIL = 42;
	public static final int COMPARE_VERSION_FAIL = 43;
	public static final int EXPERIMENTAL_FAIL = 44;
	public static final int CLEAR_APP_FAIL = 45;
	public static final int DEVICE_DOWNLOAD_FAIL = 46;
	public static final int DOWNLOAD_BACKUP_AREA_FAIL = 47;
	public static final int CONDITION_NOT_MEET = 48;
	public static final int NO_STORAGE_SPACE = 49;
	public static final int EXCEPTION_FAIL = 50;
	public static final int NOT_WRITTEN_KEY = 51;
	public static final int VERIFY_SIGN_FAIL = 52;
	public static final int DATA_BACKUP_FAIL = 53;
	public static final int COMPARE_HDVER_FAIL = 54;

	/**
	 * 升级固件成功
	 */
	public void upgradeDeviceSuccess();

	/**
	 * 当前升级固件的进度
	 *
	 * @param progressValue
	 *            百分比数值
	 */
	public void showProgress(int progressValue);

	/**
	 * 升级固件失败
	 *
	 * @param errorCode
	 *            错误码
	 */
	public void upgradeFail(int errorCode);
}
