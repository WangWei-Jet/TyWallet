package com.whty.blockchain.tyblockchainlib;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    EthereumWeb3jService web3jService = new EthereumWeb3jServiceImpl();
    String tag = this.getClass().getSimpleName();

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.whty.blockchain.tyblockchainlib", appContext.getPackageName());
    }
    @Test
    public void testWeb3jGetTokenBalance() {
        try {
            web3jService.ethGetTokenBalance("0x819F07d5e4b7a0A26fF992173FB0B7Dd016A5273",
                    "0x5b893eace2c63c7a368bf5c845b6dc84209edb35");
            // 如果转换得到的结果数据?
        } catch (Exception e) {
            Log.e(tag,"异常");
        }
    }
}
