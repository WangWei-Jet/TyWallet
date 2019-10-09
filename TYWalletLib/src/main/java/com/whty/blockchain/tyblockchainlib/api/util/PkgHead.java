package com.whty.blockchain.tyblockchainlib.api.util;

public class PkgHead {
	//	private byte[] productId=new byte[8];           //产品Id，ANSI
//	private byte[] productType=new byte[68];//生产线名称,ANSI
//	private byte[] devEnumName=new byte[132];//设备枚举名称，如TianYu AnyPay等,ANSI
//	private byte[] upgradeName=new byte[36];//设备升级模式名称,ANSI
//	private byte[] appLen=new byte[4];
//	private byte[] fontLen=new byte[4];
//	private byte[] isoLen=new byte[4];
//	private byte[] xor32AppSrc=new byte[4];
//	private byte[] appVer=new byte[16];        //App固件版本号,ANSI
//    private byte[] fontVer=new byte[16];       //Font字库版本,ANSI
//    private byte[] isoVer=new byte[16];              //Iso客户端版本,ANSI
//    private byte[] key3=new byte[16];         //加密后的key3
//    private byte[] encryptTransKey=new byte[16];    //key3加密后的传输密钥，来自授权key
//    private byte  upgradeInApp;          //是否在APP下升级（带SPIFLASH）
//    private byte[] md5=new byte[16];
	private byte[] productId=null;           //产品Id，ANSI
	private byte[] productType=null;//生产线名称,ANSI
	private byte[] devEnumName=null;//设备枚举名称，如TianYu AnyPay等,ANSI
	private byte[] upgradeName=null;//设备升级模式名称,ANSI
	private byte[] appLen=null;
	private byte[] fontLen=null;
	private byte[] isoLen=null;
	private byte[] xor32AppSrc=null;
	private byte[] appVer=null;        //App固件版本号,ANSI
	private byte[] fontVer=null;       //Font字库版本,ANSI
	private byte[] isoVer=null;              //Iso客户端版本,ANSI
	private byte[] key3=null;         //加密后的key3
	private byte[] encryptTransKey=null;    //key3加密后的传输密钥，来自授权key
	private byte  upgradeInApp;          //是否在APP下升级（带SPIFLASH）
	private byte[] md5=null;
	private byte[] hardVer = null;
	private byte signflag;
//	UINT32   appLen;                //APP固件长度,0表示没有
//	UINT32   fontLen;                //Font字库长度,0表示没有
//	UINT32   isoLen;                 //Iso客户端长度,0表示没有
//	UINT32   xor32AppSrc;           //APP固件原始明文异或和

	public byte[] getAppLen() {
		return appLen;
	}
	public void setAppLen(byte[] appLen) {
		this.appLen = appLen;
	}
	public byte[] getFontLen() {
		return fontLen;
	}
	public void setFontLen(byte[] fontLen) {
		this.fontLen = fontLen;
	}
	public byte[] getIsoLen() {
		return isoLen;
	}
	public void setIsoLen(byte[] isoLen) {
		this.isoLen = isoLen;
	}
	public byte[] getXor32AppSrc() {
		return xor32AppSrc;
	}
	public void setXor32AppSrc(byte[] xor32AppSrc) {
		this.xor32AppSrc = xor32AppSrc;
	}

	public byte[] getProductId() {
		return productId;
	}
	public void setProductId(byte[] productId) {
		this.productId = productId;
	}
	public byte[] getProductType() {
		return productType;
	}
	public void setProductType(byte[] productType) {
		this.productType = productType;
	}
	public byte[] getDevEnumName() {
		return devEnumName;
	}
	public void setDevEnumName(byte[] devEnumName) {
		this.devEnumName = devEnumName;
	}
	public byte[] getUpgradeName() {
		return upgradeName;
	}
	public void setUpgradeName(byte[] upgradeName) {
		this.upgradeName = upgradeName;
	}
	public byte[] getAppVer() {
		return appVer;
	}
	public void setAppVer(byte[] appVer) {
		this.appVer = appVer;
	}
	public byte[] getFontVer() {
		return fontVer;
	}
	public void setFontVer(byte[] fontVer) {
		this.fontVer = fontVer;
	}
	public byte[] getIsoVer() {
		return isoVer;
	}
	public void setIsoVer(byte[] isoVer) {
		this.isoVer = isoVer;
	}
	public byte[] getKey3() {
		return key3;
	}
	public void setKey3(byte[] key3) {
		this.key3 = key3;
	}
	public byte[] getEncryptTransKey() {
		return encryptTransKey;
	}
	public void setEncryptTransKey(byte[] encryptTransKey) {
		this.encryptTransKey = encryptTransKey;
	}
	public byte getUpgradeInApp() {
		return upgradeInApp;
	}
	public void setUpgradeInApp(byte upgradeInApp) {
		this.upgradeInApp = upgradeInApp;
	}
	public byte[] getMd5() {
		return md5;
	}
	public void setMd5(byte[] md5) {
		this.md5 = md5;
	}
	public byte[] getHardVer(){
		return hardVer;
	}
	public void setHardVer(byte[] hardVer){
		this.hardVer = hardVer;
	}
	public byte  getSignflag(){
		return signflag;
	}
	public void setSignflag(byte signflag){
		this.signflag = signflag;
	}

}
