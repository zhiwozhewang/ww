package com.loopeer.android.apps.lreader.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileDES {
	/** 加密解密的key */
	private Key mKey;
	/** 解密的密码 */
	private Cipher mDecryptCipher;
	/** 加密的密码 */
	private Cipher mEncryptCipher;

	private String keyString = "";

	public FileDES(String key) throws Exception {
		keyString = key;
		initKey(keyString);
		initCipher();
	}

	/**
	 * 创建一个加密解密的key
	 * 
	 * @param keyRule
	 */
	public void initKey(String keyRule) {
		byte[] keyByte = keyRule.getBytes();
		// 创建一个空的八位数组,默认情况下为0
		byte[] byteTemp = new byte[8];
		// 将用户指定的规则转换成八位数组
		for (int i = 0; i < byteTemp.length && i < keyByte.length; i++) {
			byteTemp[i] = keyByte[i];
		}

		mKey = new SecretKeySpec(byteTemp, "DES");
	}

	/***
	 * 初始化加载密码
	 * 
	 * @throws Exception
	 */
	private void initCipher() throws Exception {
		mEncryptCipher = Cipher.getInstance("DES");
		mEncryptCipher.init(Cipher.ENCRYPT_MODE, mKey);

		// SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// byte[] keyData = "DSEPUB86".getBytes();
		// DESKeySpec keySpec = new DESKeySpec(keyData);
		// mKey = keyFactory.generateSecret(keySpec);

		mDecryptCipher = Cipher.getInstance("DES/ECB/NoPadding");
		mDecryptCipher.init(Cipher.DECRYPT_MODE, mKey);
	}

	/**
	 * 加密文件
	 * 
	 * @param in
	 * @param savePath
	 *            加密后保存的位置
	 */
	public void doEncryptFile(InputStream in, String savePath) {
		if (in == null) {
			System.out.println("inputstream is null");
			return;
		}
		try {
			CipherInputStream cin = new CipherInputStream(in, mEncryptCipher);
			OutputStream os = new FileOutputStream(savePath);
			byte[] bytes = new byte[1024];
			int len = -1;
			while ((len = cin.read(bytes)) > 0) {
				os.write(bytes, 0, len);
				os.flush();
			}
			os.close();
			cin.close();
			in.close();
			System.out.println("加密成功");
		} catch (Exception e) {
			System.out.println("加密失败");
			e.printStackTrace();
		}
	}

	/**
	 * 加密文件
	 * 
	 * @param filePath
	 *            需要加密的文件路径
	 * @param savePath
	 *            加密后保存的位置
	 * @throws java.io.FileNotFoundException
	 */
	public void doEncryptFile(String filePath, String savePath) throws FileNotFoundException {
		doEncryptFile(new FileInputStream(filePath), savePath);
	}

	/**
	 * 解密文件
	 * 
	 * @param in
	 */
	public void doDecryptFile(InputStream in, String savePath) {
		if (in == null) {
			System.out.println("inputstream is null");
			return;
		}
		try {
			CipherInputStream cin = new CipherInputStream(in, mDecryptCipher);
			// BufferedReader reader = new BufferedReader(new
			// InputStreamReader(cin));
			OutputStream os = new FileOutputStream(savePath);
			byte[] bytes = new byte[1024];
			int len = -1;
			while ((len = cin.read(bytes)) > 0) {
				os.write(bytes, 0, len);
				os.flush();
			}
			// String line = null;
			// while ((line = reader.readLine()) != null) {
			// System.out.println("=========================" + line);
			// }
			os.close();
			// reader.close();
			cin.close();
			in.close();
			System.out.println("解密成功");
		} catch (Exception e) {
			System.out.println("解密失败");
			e.printStackTrace();
		}
	}

	/**
	 * 解密文件srcFile到目标文件distFile
	 * 
	 * @param srcFile
	 *            密文文件
	 * @param distFile
	 *            解密后的文件
	 * @throws Exception
	 */
	public void DecryptFile(String srcFile, String distFile) throws Exception {

		InputStream is = null;
		OutputStream out = null;
		CipherOutputStream cos = null;
		try {
			int mode = Cipher.DECRYPT_MODE;
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			byte[] keyData = keyString.getBytes();
			DESKeySpec keySpec = new DESKeySpec(keyData);
			Key key = keyFactory.generateSecret(keySpec);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(keyString.getBytes("UTF-8"));
			cipher.init(mode, key, iv);
			byte[] buffer = new byte[1024];
			is = new FileInputStream(srcFile);
			out = new FileOutputStream(distFile);
			cos = new CipherOutputStream(out, cipher);

			int r;
			while ((r = is.read(buffer)) >= 0) {
				cos.write(buffer, 0, r);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			cos.close();
			is.close();
			out.close();
		}
	}

	/**
	 * 解密文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @throws Exception
	 */
	public void doDecryptFile(String filePath, String savePath) throws Exception {
		doDecryptFile(new FileInputStream(filePath), savePath);
	}
	
	
/*
	private final class Button2OnClickListener implements View.OnClickListener {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			key = keyEditText.getText().toString();
			decryptFrom = editText3.getText().toString();
			decryptTo = editText4.getText().toString();
			FileDES fileDES;
			try {
				fileDES = new FileDES("DSEPUB86");
				fileDES.DecryptFile("sdcard/" + decryptFrom, "sdcard/"
						+ decryptTo); // 加密
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	*/
}
