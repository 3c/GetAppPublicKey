package com.cx.testappkey;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

public class GetPublicKey {

	/**
	 * 获取签名公钥
	 * 
	 * @param mContext
	 * @return
	 */
	protected static String getSignInfo(Context mContext, String packageName) {
		String signcode = "";
		try {
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_SIGNATURES);
			Signature[] signs = packageInfo.signatures;
			Signature sign = signs[0];

			signcode = parseSignature(sign.toByteArray());
			signcode = signcode.toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return signcode;
	}

	protected static String parseSignature(byte[] signature) {
		String sign = "";
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory
					.generateCertificate(new ByteArrayInputStream(signature));
			String pubKey = cert.getPublicKey().toString();
			String ss = subString(pubKey);
			ss = ss.replace(",", "");
			ss = ss.toLowerCase();
			int aa = ss.indexOf("modulus");
			int bb = ss.indexOf("publicexponent");
			sign = ss.substring(aa + 8, bb);
		} catch (CertificateException e) {
			e.printStackTrace();
		}
		return sign;
	}

	public static String subString(String sub) {
		Pattern pp = Pattern.compile("\\s*|\t|\r|\n");
		Matcher mm = pp.matcher(sub);
		return mm.replaceAll("");
	}
}