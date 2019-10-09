package com.whty.blockchain.tyblockchainlib.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TLVParse 对象，根据TAGS解析 tlv
 *
 */
public class TLVParser {

	private byte[] tags;
	private byte[][] multiTags;

	public TLVParser(byte[] tags) {
		super();
		this.tags = tags;
	}

	public TLVParser(String[] tags) {
		super();
		parseTags(tags);
	}

	private void parseTags(String[] tags2) {
		if (tags2 != null && tags2.length > 0) {
			multiTags = new byte[tags2.length][];
			for (int i = 0; i < tags2.length; i++) {
				multiTags[i] = str2bytes(tags2[i]);
				System.out.println(Arrays.toString(multiTags[i]));
			}
		}
	}

	// @TargetApi(value = 9)
	public TLV[] getTLVs(byte[] tlvstr) {
		List<TLV> list = new ArrayList<TLV>();

		if (tags != null) {
			int index = 0;
			while (index < tlvstr.length) {
				byte tag = tlvstr[index];
				boolean found = false;
				for (int i = 0; i < tags.length; i++) {
					if (tag == tags[i]) {
						int len = tlvstr[++index] & 0xff;
						byte[] data = new byte[len];
						System.arraycopy(tlvstr, ++index, data, 0, len);
						TLV tlv = new TLV(String.valueOf(tag), len, data);
						list.add(tlv);
						index += len;
						found = true;
					}
				}

				if (!found) {
					index++;
				}

			}
		} else if (multiTags != null) {

			int index = 0;

			// 待验证

			while (index < tlvstr.length) {
				boolean found = false;
				for (int i = 0; i < multiTags.length; i++) {
					byte[] tagArray = multiTags[i];
					// byte[] target = Arrays.copyOfRange(tlvstr, index, index
					// + tagArray.length);
					byte[] target = null;
					if (tagArray.length > 0) {
						target = new byte[tagArray.length];
					}
					System.arraycopy(tlvstr, index, target, 0, target.length);
					int cIndex = index;
					cIndex += tagArray.length;
					if (Arrays.equals(tagArray, target)) {
						int len = tlvstr[cIndex] & 0xff;
						byte[] data = new byte[len];
						System.arraycopy(tlvstr, ++cIndex, data, 0, len);
						TLV tlv = new TLV(GPMethods.bytesToHexString(tagArray),
								len, data);
						list.add(tlv);
						cIndex += len;
						index = cIndex;
						System.out.println(tlv);
						found = true;
						break;
					}
				}
				if (!found)
					index++;
			}

		}

		return list.toArray(new TLV[0]);
	}

	// @TargetApi(value = 9)
	public TLV[] getTLs(byte[] tlvstr) {
		List<TLV> list = new ArrayList<TLV>();

		if (tags != null) {
			int index = 0;
			while (index < tlvstr.length) {
				byte tag = tlvstr[index];
				for (int i = 0; i < tags.length; i++) {
					if (tag == tags[i]) {
						int len = tlvstr[++index];
						TLV tlv = new TLV(String.valueOf(tag), len, null);
						list.add(tlv);
					}
				}
			}
		} else if (multiTags != null) {

			int index = 0;

			// 待验证

			while (index < tlvstr.length) {
				boolean found = false;
				for (int i = 0; i < multiTags.length; i++) {
					byte[] tagArray = multiTags[i];
					// byte[] target = Arrays.copyOfRange(tlvstr, index, index
					// + tagArray.length);
					byte[] target = null;
					if (tagArray.length > 0) {
						target = new byte[tagArray.length];
					}
					System.arraycopy(tlvstr, index, target, 0, target.length);
					int cIndex = index;
					cIndex += tagArray.length;
					if (Arrays.equals(tagArray, target)) {
						int len = tlvstr[cIndex] & 0xff;
						TLV tlv = new TLV(GPMethods.bytesToHexString(tagArray),
								len, null);
						list.add(tlv);
						index = cIndex;
						System.out.println(tlv);
						found = true;
						break;
					}
				}
				if (!found)
					index++;
			}

		}

		return list.toArray(new TLV[0]);
	}

	public static byte[] str2bytes(String src) {
		if (src == null || src.length() == 0 || src.length() % 2 != 0) {
			return null;
		}
		int nSrcLen = src.length();
		byte byteArrayResult[] = new byte[nSrcLen / 2];
		StringBuffer strBufTemp = new StringBuffer(src);
		String strTemp;
		int i = 0;
		while (i < strBufTemp.length() - 1) {
			strTemp = src.substring(i, i + 2);
			byteArrayResult[i / 2] = (byte) Integer.parseInt(strTemp, 16);
			i += 2;
		}
		return byteArrayResult;
	}
}
