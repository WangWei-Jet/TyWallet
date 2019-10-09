package com.whty.blockchain.tyblockchainlib.api.util;

import android.util.Log;

import cn.jesse.nativelogger.NLogger;

public class TYLog {

    public static void d(String tag, String msg){
        if(!TYLoggerConfig.isEnableLog()){
            return;
        }
        try {
            NLogger.d(tag,msg);
        } catch (Exception e) {
            Log.d(tag,msg);
        }
    }

    public static void i(String tag,String msg){
        if(!TYLoggerConfig.isEnableLog()){
            return;
        }
        try {
            NLogger.i(tag,msg);
        } catch (Exception e) {
            Log.d(tag,msg);
        }
    }

    public static void w(String tag,String msg){
        if(!TYLoggerConfig.isEnableLog()){
            return;
        }
        try {
            NLogger.w(tag,msg);
        } catch (Exception e) {
            Log.d(tag,msg);
        }
    }

    public static void e(String tag,String msg){
        if(!TYLoggerConfig.isEnableLog()){
            return;
        }
        try {
            NLogger.e(tag,msg);
        } catch (Exception e) {
            Log.d(tag,msg);
        }
    }

    public static void e(String tag,String msg,Throwable throwable){
        if(!TYLoggerConfig.isEnableLog()){
            return;
        }
        try {
            NLogger.e(tag,throwable);
        } catch (Exception e) {
            Log.d(tag,msg,throwable);
        }
    }

}
