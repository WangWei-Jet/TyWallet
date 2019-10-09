package com.whty.blockchain.wallet.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whty.blockchain.bottomdialog.widget.ActionSheetDialog;
import com.whty.blockchain.tybitcoinlib.entity.BlockchainUTXOResponse;
import com.whty.blockchain.wallet.R;
import com.whty.blockchain.wallet.WalletApplication;
import com.whty.blockchain.wallet.entity.Env;
import com.whty.blockchain.wallet.utils.view.SettingItemRelative;
import com.whty.blockchain.wallet.view.ISettingFragmentView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

public class SettingFragment extends Fragment implements ISettingFragmentView {

    SettingItemRelative userInfoSettingItemRelative;
    SettingItemRelative environmentSettingItemRelative;
    SettingItemRelative browserUrlSettingItemRelative;
    SettingItemRelative walletManagementSettingItemRelative;
    SettingItemRelative coldWalletSettingItemRelative;

    private final String DESCRIPTION_ETHEREUM = "以太坊";
    private final String DESCRIPTION_ETHEREUM_TESTNET = "以太坊测试链";
    private final String DESCRIPTION_BITCOIN = "比特币";
    private final String DESCRIPTION_BITCOIN_TEST = "比特币测试链";

    private MainActivity parentActivity;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        logger.info("onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger.info("onCreate");
        parentActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        logger.info("onCreateView");
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        //个人信息
        userInfoSettingItemRelative = view.findViewById(R.id.si_user_info);
        userInfoSettingItemRelative.getIvLeft().setImageResource(R.mipmap.ic_setting_userinfo);
        userInfoSettingItemRelative.getTvKey().setText("登录|注册");
        userInfoSettingItemRelative.getTvKey().setTextSize(22);
        userInfoSettingItemRelative.getTvValue().setText("");
        userInfoSettingItemRelative.getIbMore().setVisibility(View.GONE);
        //环境设置
        environmentSettingItemRelative = view.findViewById(R.id.si_environment);
        environmentSettingItemRelative.getIvLeft().setImageResource(R.mipmap
                .ic_setting_environment);
        environmentSettingItemRelative.getTvKey().setText("区块链环境");
        environmentSettingItemRelative.getTvValue().setText(getEnvironmentDescription(
                ((WalletApplication) parentActivity.getApplication()).getEnv()));
        environmentSettingItemRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.info("点击了区块链环境设置");
                //底部弹出list
                new ActionSheetDialog(parentActivity)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem("以太坊", ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        //填写事件
                                        logger.debug("点击了Ethereum");
                                        ((WalletApplication) parentActivity.getApplication())
                                                .setEnv(Env.ETHEREUM);
                                        parentActivity.getBrowserAPIPresentor().setEnv(Env.ETHEREUM);
                                        environmentSettingItemRelative.getTvValue().setText
                                                (getEnvironmentDescription(
                                                ((WalletApplication) parentActivity
                                                        .getApplication()).getEnv()));
                                    }
                                })
                        .addSheetItem("以太坊测试链", ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        //填写事件
                                        logger.debug("点击了Ethereum-Testnet");
                                        ((WalletApplication) parentActivity.getApplication())
                                                .setEnv(Env.ETHEREUM_TESTNET);
                                        parentActivity.getBrowserAPIPresentor().setEnv(Env.ETHEREUM_TESTNET);
                                        environmentSettingItemRelative.getTvValue().setText
                                                (getEnvironmentDescription(
                                                        ((WalletApplication) parentActivity
                                                                .getApplication()).getEnv()));
                                    }
                                })
                        .addSheetItem("比特币", ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        //填写事件
                                        logger.debug("点击了Bitcoin");
                                        ((WalletApplication) parentActivity.getApplication())
                                                .setEnv(Env.BITCOIN);
                                        parentActivity.getBrowserAPIPresentor().setEnv(Env.BITCOIN);
                                        environmentSettingItemRelative.getTvValue().setText
                                                (getEnvironmentDescription(
                                                        ((WalletApplication) parentActivity
                                                                .getApplication()).getEnv()));
                                    }
                                })
                        .addSheetItem("比特币测试链", ActionSheetDialog.SheetItemColor.Blue
                                , new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        //填写事件
                                        logger.debug("点击了Bitcoin-Testnet");
                                        ((WalletApplication) parentActivity.getApplication())
                                                .setEnv(Env.BITCOIN_TESTNET);
                                        parentActivity.getBrowserAPIPresentor().setEnv(Env.BITCOIN_TESTNET);
                                        environmentSettingItemRelative.getTvValue().setText
                                                (getEnvironmentDescription(
                                                        ((WalletApplication) parentActivity
                                                                .getApplication()).getEnv()));
                                    }
                                }).show();
            }
        });
        //浏览器api url
        browserUrlSettingItemRelative = view.findViewById(R.id.si_browser_url);
        browserUrlSettingItemRelative.getIvLeft().setImageResource(R.mipmap.ic_setting_browser);
        browserUrlSettingItemRelative.getTvKey().setText("浏览器API");
        browserUrlSettingItemRelative.getTvValue().setText("");
        browserUrlSettingItemRelative.getIbMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.info("点击了浏览器API设置");
            }
        });
        //钱包管理，切换当前使用的钱包
        walletManagementSettingItemRelative = view.findViewById(R.id.si_wallet_management);
        walletManagementSettingItemRelative.getIvLeft().setImageResource(R.mipmap
                .ic_setting_wallet_manage);
        walletManagementSettingItemRelative.getTvKey().setText("钱包管理");
        walletManagementSettingItemRelative.getTvValue().setText("");
        walletManagementSettingItemRelative.getIbMore().setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.info("点击了钱包管理");
            }
        });
        //冷钱包管理，展示当前绑定的冷钱包
        coldWalletSettingItemRelative = view.findViewById(R.id.si_cold_wallet);
        coldWalletSettingItemRelative.getIvLeft().setImageResource(R.mipmap.ic_setting_cold_wallet);
        coldWalletSettingItemRelative.getTvKey().setText("冷钱包");
        coldWalletSettingItemRelative.getTvValue().setText("");
        coldWalletSettingItemRelative.getIbMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.info("点击了浏览器API设置");
            }
        });
        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
        logger.info("onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        logger.info("onResume");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        logger.info("onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        logger.info("onDetach");
    }

    class SettingItemViewSelector extends RecyclerView.ViewHolder {

        TextView tvItem;

        public SettingItemViewSelector(View itemView) {
            super(itemView);
            tvItem = (TextView) itemView;
        }

        public TextView getTvItem() {
            return tvItem;
        }
    }

    String getEnvironmentDescription(Env env) {
        switch (env) {
            case ETHEREUM:
                return DESCRIPTION_ETHEREUM;

            case ETHEREUM_TESTNET:
                return DESCRIPTION_ETHEREUM_TESTNET;

            case BITCOIN:
                return DESCRIPTION_BITCOIN;

            case BITCOIN_TESTNET:
                return DESCRIPTION_BITCOIN_TEST;
            default:
                return "";
        }
    }

    @Override
    public void showLoadingDialog() {

    }

    @Override
    public void showLoadingDialogWithContentMsg(String contentMsg) {

    }

    @Override
    public void dismissLoadingDialog() {

    }

    @Override
    public void onError(String errorMsg) {

    }

    @Override
    public void showEthereumEnvironmentBalanceResult(BigInteger balance) {

    }

    @Override
    public void showBitcoinEnvironmentBalanceResult(BlockchainUTXOResponse blockchainUTXOResponse) {

    }
}
