package com.whty.blockchain.tywallet.util;

public class UIMessage {
    public static String exit_dialog_title;
    public static String exit_dialog_message;
    public static String exit_dialog_positive;
    public static String exit_dialog_negative;
    
    public static String device_list_title;
    public static String device_list_positive;
    public static String device_list_negative;
    
    public static String show_result_text;
    public static String click_button;
    
    public static String init_device_success;
    public static String init_device_fail;
    
    public static String donot_select_device;
    public static String donot_connect_device;
    public static String selected_device;
    public static String connected_device;
    public static String scanning_device;
    public static String connecting_device;
    
    public static String connected_device_success;
    public static String connected_device_fail;
    public static String disconnected_device_success;
    public static String disconnected_device_fail;
    
    public static String update_TDK;
    public static String update_PIK;
    public static String update_MAK;
    public static String update_working_key_fail;
    public static String read_card_fail;
    public static String calculate_mac_fail;
    public static String please_enter_password;
    public static String downGrade_transaction;
    
    public static void setMessage(int languageID) {
        if (languageID == 0) {
            exit_dialog_title = "提示";
            exit_dialog_message = "确认退出工具？";
            exit_dialog_positive = "确认";
            exit_dialog_negative = "取消";
            device_list_title = "扫描到的蓝牙设备";
            device_list_positive = "重新扫描";
            device_list_negative = "确定";
            show_result_text = "结果显示区域";
            click_button = "单击按钮";
            init_device_success = "初始化成功";
            init_device_fail = "初始化失败";
            donot_select_device = "没有选择设备";
            donot_connect_device = "当前没有连接设备";
            selected_device = "已选择设备";
            connected_device = "已连接设备";
            scanning_device = "正在扫描设备";
            connecting_device = "正在连接设备";
            connected_device_success = "连接设备成功";
            connected_device_fail = "连接设备失败";
            disconnected_device_success = "断开设备成功";
            disconnected_device_fail = "断开设备失败";
            update_TDK = "更新磁道密钥";
            update_PIK = "更新pin密钥";
            update_MAK = "更新mac密钥";
            update_working_key_fail = "更新工作密钥失败";
            read_card_fail = "刷卡没有成功";
            calculate_mac_fail = "计算mac失败";
            please_enter_password = "请输入密码";
            downGrade_transaction = "发生降级交易";
        } else if (languageID == 1) {
            exit_dialog_title = "提示";
            exit_dialog_message = "確認退出工具？";
            exit_dialog_positive = "確認";
            exit_dialog_negative = "取消";
            device_list_title = "掃描到的藍牙設備";
            device_list_positive = "重新掃描";
            device_list_negative = "確定";
            show_result_text = "結果顯示區域";
            click_button = "單擊按鈕";
            init_device_success = "初始化成功";
            init_device_fail = "初始化失敗";
            donot_select_device = "沒有選擇設備";
            donot_connect_device = "當前沒有連接設備";
            selected_device = "已選擇設備";
            connected_device = "已連接設備";
            scanning_device = "正在掃描設備";
            connecting_device = "正在連接設備";
            connected_device_success = "連接設備成功";
            connected_device_fail = "連接設備失敗";
            disconnected_device_success = "斷開設備成功";
            disconnected_device_fail = "斷開設備失敗";
            update_TDK = "更新磁道密鑰";
            update_PIK = "更新pin密鑰";
            update_MAK = "更新mac密鑰";
            update_working_key_fail = "更新工作密鑰失敗";
            read_card_fail = "刷卡沒有成功";
            calculate_mac_fail = "計算mac失敗";
            please_enter_password = "請輸入密碼";
            downGrade_transaction = "發生降級交易";
        } else if (languageID == 2) {
            exit_dialog_title = "Prompt";
            exit_dialog_message = "Confirm exit the tool?";
            exit_dialog_positive = "Confirm";
            exit_dialog_negative = "Cancel";
            device_list_title = "Scan to Bluetooth devices";
            device_list_positive = "Rescan";
            device_list_negative = "Confirm";
            click_button = "Click on the button";
            init_device_success = "Successful initialization";
            init_device_fail = "Failed to initialize";
            show_result_text = "Results display area";
            donot_select_device = "No device is selected";
            donot_connect_device = "Not currently connected device";
            selected_device = "Selected device";
            connected_device = "Connected device";
            scanning_device = "Scanning Device";
            connecting_device = "Connecting device";
            connected_device_success = "Connected device successfully";
            connected_device_fail = "Connect device failed";
            disconnected_device_success = "Disconnect device successfully";
            disconnected_device_fail = "Disconnect device failed";
            update_TDK = "Update track key";
            update_PIK = "Update pin key";
            update_MAK = "Update mac key";
            update_working_key_fail = "Update the work key failed";
            read_card_fail = "Credit card without success";
            calculate_mac_fail = "Calculation mac failed";
            please_enter_password = "Please enter password";
            downGrade_transaction = "Demotion transaction occurred";
        }
    }
    
}
