package com.whty.blockchain.tyblockchainlib.api.util;
import java.util.Arrays;

/**
 * TLV 数据格式
 *
 */
public class TLV {
	private String tag;
	private int len;
	private byte[] data;

	public TLV(String tag, int len, byte[] data) {
		super();
		this.tag = tag;
		this.len = len;
		this.data = data;
	}

	public TLV() {
		super();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "TLV [tag=" + tag + ", len=" + len + ", data="
				+ Arrays.toString(data) + "]";
	}
}
