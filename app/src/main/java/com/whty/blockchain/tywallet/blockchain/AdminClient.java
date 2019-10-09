package com.whty.blockchain.tywallet.blockchain;

import com.whty.blockchain.tybitcoinlib.api.BlockExplorerAPI;
import com.whty.blockchain.tywallet.util.ENV;

import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.JsonRpc2_0Admin;
import org.web3j.protocol.http.HttpService;

public class AdminClient {

    private volatile static Admin admin;

    private volatile static BlockExplorerAPI blockExplorerAPI;

    private static Object lock = new Object();

    private AdminClient() {

    }

    //节点主机地址，可通过使用免费的Infura接入测试网络和主网络
//	private static final String hostAndPort = "http://127.0.0.1:8001";
    private static final String testHostAndPort = "https://rinkeby.infura" +
            ".io/v3/bb8c3e75013b45c99123932614ce513e";

    private static final String mainHostAndPort = "https://mainnet.infura" +
            ".io/v3/bb8c3e75013b45c99123932614ce513e";

    private static String currentHostAndPort = mainHostAndPort;

    private static ENV currentEnv = ENV.ETHEREUM;

    private static BlockExplorerAPI.BlockExplorer currentBitcoinEnv = BlockExplorerAPI
            .BlockExplorer.Blockchain_Testnet;

    public static ENV getCurrentEnv() {
        return currentEnv;
    }

    private static void setCurrentBitcoinEnv(BlockExplorerAPI.BlockExplorer blockExplorer) {
        currentBitcoinEnv = blockExplorer;
    }

    public static BlockExplorerAPI.BlockExplorer getCurrentBitcoinEnv() {
        return currentBitcoinEnv;
    }

    public static BlockExplorerAPI getBitcoinExplorerAPI() {
        if (blockExplorerAPI == null) {
            synchronized (lock) {
                if (blockExplorerAPI == null) {
                    //默认测试环境
                    setCurrentBitcoinEnv(BlockExplorerAPI.BlockExplorer
                            .Blockchain_Testnet);
                    blockExplorerAPI = new BlockExplorerAPI(BlockExplorerAPI.BlockExplorer
                            .Blockchain_Testnet);
                }
            }
        }
        return blockExplorerAPI;
    }

    public static Admin newInstance() {

        if (admin == null) {
            synchronized (lock) {
                if (admin == null) {
                    //android
                    admin = new JsonRpc2_0Admin(new HttpService(currentHostAndPort));
                    //java
//					admin = Admin.build(new HttpService(hostAndPort));
                }
            }
        }
        return admin;
    }

    public static void switchBitcoinEnv(BlockExplorerAPI.BlockExplorer blockExplorer) {
        synchronized (lock) {
            setCurrentBitcoinEnv(blockExplorer);
            blockExplorerAPI = new BlockExplorerAPI(blockExplorer);
        }
    }

    public static void switchEnv(ENV env) {
        synchronized (lock) {
            currentEnv = env;
            switch (env) {
                case TESTNET:
                    currentHostAndPort = testHostAndPort;
                    break;
                case ETHEREUM:
                    currentHostAndPort = mainHostAndPort;
                    break;
            }
            admin = new JsonRpc2_0Admin(new HttpService(currentHostAndPort));
        }
    }
}
