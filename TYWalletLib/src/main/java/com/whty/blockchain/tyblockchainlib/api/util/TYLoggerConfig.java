package com.whty.blockchain.tyblockchainlib.api.util;

import android.util.Log;

import java.util.logging.Formatter;

import cn.jesse.nativelogger.NLogger;
import cn.jesse.nativelogger.NLoggerConfig;
import cn.jesse.nativelogger.logger.LoggerLevel;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class TYLoggerConfig {

    private static boolean enableLog;

    private static String tag;

    private static boolean fileLogger;

    private static String fileDirectory;

    private static Formatter fileFormatter;

    public static boolean isEnableLog() {
        return enableLog;
    }

    public static String getTag() {
        return tag;
    }

    public static boolean isFileLogger() {
        return fileLogger;
    }

    public static String getFileDirectory() {
        return fileDirectory;
    }

    public static Formatter getFileFormatter() {
        return fileFormatter;
    }

    public static class Builder {
        private boolean enableLog;

        private String tag;

        private boolean fileLogger;

        private String fileDirectory;

        private Formatter fileFormatter;

        public Builder enableLog(boolean enableLog){
            this.enableLog = enableLog;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder fileLogger(boolean fileLogger) {
            this.fileLogger = fileLogger;
            return this;
        }

        public Builder fileDirectory(String fileDirectory) {
            this.fileDirectory = fileDirectory;
            return this;
        }

        public Builder fileFormatter(Formatter formatter) {
            this.fileFormatter = formatter;
            return this;
        }

        public void build() {
            TYLoggerConfig.enableLog = enableLog;
            TYLoggerConfig.tag = tag;
            TYLoggerConfig.fileLogger = fileLogger;
            TYLoggerConfig.fileDirectory = fileDirectory;
            TYLoggerConfig.fileFormatter = fileFormatter;

            try {
                //框架设置
                NLoggerConfig.Builder builder = NLoggerConfig.getInstance()
                        .builder();
                builder.tag(tag).loggerLevel(LoggerLevel.DEBUG)
                        .fileLogger(fileLogger)
                        .fileDirectory(fileDirectory)
                        .fileFormatter(fileFormatter)
                        .catchException(true, new Function2<Thread, Throwable, Unit>() {
                            @Override
                            public Unit invoke(Thread thread, Throwable throwable) {
                                NLogger.e(tag, throwable);
//                                android.os.Process.killProcess(android.os.Process.myPid());
                                return null;
                            }
                        }).build();

                Log.d(tag, "build => fileDirectory:"+fileDirectory);
            } catch (Exception e) {
                Log.w(tag, "build: 框架log加载异常", e);
            }

        }
    }
}
