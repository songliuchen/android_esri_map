/**==================================================================
 *
 *        工程名称:  tszs.system
 *        文件名称:  Security.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年12月6日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.system;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class TSecurity
{
	public static final java.lang.String ALGORITHM_DES = "AES/ECB/PKCS5Padding";
	private static java.lang.String innerkeyword = "TSZS_SEC@1989083";
	private final static java.lang.String HEX = "0123456789ABCDEF";
	private static final byte[] KEY_VI = { 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8}; 
	/**
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param kewword
	 *            加密密码
	 * @return
	 */
	public static java.lang.String EncryptAes(java.lang.String content)
	{
		if(TString.IsNullOrEmpty(content))
			return "";
		try
		{
			return EncryptAes(innerkeyword, content);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param kewword
	 *            加密密码
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static java.lang.String EncryptAes(java.lang.String kewword, java.lang.String content) throws UnsupportedEncodingException
	{
		if(TString.IsNullOrEmpty(content))
			return "";
		return toHex(EncryptAes(kewword, content.getBytes("utf-8")));
	}

	/**
	 * 加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param kewword
	 *            加密密码
	 * @return
	 */
	public static byte[] EncryptAes(java.lang.String kewword, byte[] content)
	{
		try
		{
			if(content == null || content.length==0)
				return null;
			
			IvParameterSpec zeroIv = new IvParameterSpec(KEY_VI);  
			SecretKeySpec key = new SecretKeySpec(kewword.getBytes(), "AES");  
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  
	        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);  
	        byte[] encryptedData = cipher.doFinal(content);  
			return encryptedData;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace();
		}
		catch (IllegalBlockSizeException e)
		{
			e.printStackTrace();
		}
		catch (BadPaddingException e)
		{
			e.printStackTrace();
		}
		catch (InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param kewword
	 *            解密密钥
	 * @return
	 */
	public static java.lang.String DecryptAes(java.lang.String content)
	{
		if(TString.IsNullOrEmpty(content))
			return "";
		try
		{
			return DecryptAes(innerkeyword, content);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param kewword
	 *            解密密钥
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static java.lang.String DecryptAes(java.lang.String kewword, java.lang.String content) throws UnsupportedEncodingException
	{
		if(TString.IsNullOrEmpty(content))
			return "";
		return new java.lang.String(DecryptAes(kewword, toByte(content)));
	}

	/**
	 * 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param kewword
	 *            解密密钥
	 * @return
	 */
	public static byte[] DecryptAes(java.lang.String kewword, byte[] content)
	{
		try
		{
			if(content == null || content.length == 0)
				return null;

			IvParameterSpec zeroIv = new IvParameterSpec(KEY_VI);  
	        SecretKeySpec key = new SecretKeySpec(kewword.getBytes(), "AES");  
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  
	        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);  
	        byte[] decryptedData = cipher.doFinal(content);
			return decryptedData;
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		catch (InvalidKeyException e)
		{
			e.printStackTrace();
		}
		catch (IllegalBlockSizeException e)
		{
			e.printStackTrace();
		}
		catch (BadPaddingException e)
		{
			e.printStackTrace();
		}
		catch (InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * DES算法，加密
	 *
	 * @param content
	 *            待加密字符串
	 * @param keyword
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	public static byte[] EncodeDes(java.lang.String keyword, byte[] content)
	{
		try
		{
			DESKeySpec dks = new DESKeySpec(keyword.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("********".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

			return cipher.doFinal(content);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * DES算法，解密
	 *
	 * @param content
	 *            待解密字符串
	 * @param keyword
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	public static java.lang.String DecodeDes(java.lang.String content)
	{
		if(TString.IsNullOrEmpty(content))
			return "";
		try
		{
			return DecodeDes(innerkeyword, content);
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * DES算法，解密
	 *
	 * @param content
	 *            待解密字符串
	 * @param keyword
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 *             异常
	 */
	public static java.lang.String DecodeDes(java.lang.String keyword, java.lang.String content) throws UnsupportedEncodingException
	{
		if(TString.IsNullOrEmpty(content))
			return "";
		return new java.lang.String(DecodeDes(keyword, content.getBytes("utf-8")));
	}

	/**
	 * DES算法，解密
	 *
	 * @param content
	 *            待解密字符串
	 * @param keyword
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	public static byte[] DecodeDes(java.lang.String keyword, byte[] content)
	{
		try
		{
			DESKeySpec dks = new DESKeySpec(keyword.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("********".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return cipher.doFinal(content);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * BASE64算法，加密
	 *
	 * @param content
	 *            待解密字符串
	 * @return 解密后的字节数组
	 */
	public static java.lang.String EncodeBase64(java.lang.String content)
	{
		if(TString.IsNullOrEmpty(content))
			return "";

		return new java.lang.String(EncodeBase64(content.getBytes()));
	}

	/**
	 * BASE64算法，加密
	 *
	 * @param content
	 *            待解密字符串
	 * @return 解密后的字节数组
	 */
	public static byte[] EncodeBase64(byte[] content)
	{
		if(content == null || content.length == 0)
			return null;

		return Base64.encode(content, Base64.DEFAULT);
	}

	/**
	 * BASE64算法，解密
	 *
	 * @param content
	 *            待解密字符串
	 * @return 解密后的字节数组
	 */
	public static java.lang.String DecodeBase64(java.lang.String content)
	{
		if(TString.IsNullOrEmpty(content))
			return "";

		return new java.lang.String(DecodeBase64(content.getBytes()));
	}

	/**
	 * BASE64算法，解密
	 *
	 * @param content
	 *            待解密字符串
	 * @return 解密后的字节数组
	 */
	public static byte[] DecodeBase64(byte[] content)
	{
		if(content == null || content.length == 0)
			return null;

		return Base64.decode(content, Base64.DEFAULT);
	}

	private static byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException, NoSuchProviderException
	{
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	private static byte[] toByte(java.lang.String hexString)
	{
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
		return result;
	}

	private static java.lang.String toHex(byte[] buf)
	{
		if(buf == null)
			return "";
		StringBuffer result = new StringBuffer(2 * buf.length);
		for (int i = 0; i < buf.length; i++)
		{
			appendHex(result, buf[i]);
		}
		return result.toString();
	}

	private static void appendHex(StringBuffer sb, byte b)
	{
		sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
	}
}
