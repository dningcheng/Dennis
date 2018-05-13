package com.seally.utils;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

/**
 * 加密工具类
 * @author dnc
 */
public class EncryptUtil {
	
	private static final Logger logger = Logger.getLogger(EncryptUtil.class);
			  
	private static final String default_charset = "UTF-8";
	
	public static final String ENC_MD5="MD5";//常用
	public static final String ENC_SHA1="SHA-1";
	public static final String ENC_SHA224="SHA-224";
	public static final String ENC_SHA256="SHA-256";//常用
	public static final String ENC_SHA384="SHA-384";
	public static final String ENC_SHA512="SHA-512";
	public static final char hexDigitsLower[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	public static final char hexDigitsUpper[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	public static char[] RC4key={0x57, 0x34, 0x72, 0x85, 0x50, 0x53, 0x7A, 0x0F, 0x95, 0x54, 0xB4, 0x05, 0xA8, 0x28, 0x8C, 0xF9};
	public static byte[] AESkey={0x2d,0x35,0x0a,0x00,0x56,0x7e,0x4e,0x1f,0x1b,0x59,0x2f,0x35,0x30,0x2d,0x55,0x0a};
	public static String AESKeyStr="hJ1LiHfjCdiILldI";
	/**
	 * @param source 需要加密的字符串、默认算法：SHA-256
	 * @return 加密后结果、字母小写
	 */
	public static String Encrypt(String source) {
		return EncryptUtil.Encrypt(source,ENC_SHA256,false);
	}
	
	/**
	 * @param source 需要加密的字符串
	 * @param encName 加密类型、见EncryptUtil的常量
	 * @return 加密后结果、字母小写
	 */
	public static String Encrypt(String source,String encName) {
		return EncryptUtil.Encrypt(source,encName,false);
	}
	
	/**
	 * @param source 需要加密的字符串、默认算法：SHA-256
	 * @param toUpper 字母是否转大写
	 * @return 加密后结果
	 */
	public static String Encrypt(String source,boolean toUpper) {
		return EncryptUtil.Encrypt(source,ENC_SHA256,toUpper);
	}

	/**
	 * @param source 需要加密的字符串
	 * @param encName 加密类型、见EncryptUtil的常量
	 * @param toUpper 字母是否转大写
	 * @return 加密后结果
	 */
	public static String Encrypt(String source,String encName,boolean toUpper) {
		StringBuffer sb = new StringBuffer();
		char[] usedHexDigits=hexDigitsLower;
		if(toUpper)
			usedHexDigits=hexDigitsUpper;
		try {
			MessageDigest md = MessageDigest.getInstance(encName);
			md.update(source.getBytes());
			byte[] encryptStr = md.digest();
			for (int i = 0; i < encryptStr.length; i++) {
				int iRet = encryptStr[i];
				if (iRet < 0) {
					iRet += 256;
				}
				int iD1 = iRet / 16;
				int iD2 = iRet % 16;
				sb.append(usedHexDigits[iD1]).append(usedHexDigits[iD2]);
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}
	
	/**
	 * RC4加密获取明文或是密文字符串方法
	 * @param StringSource 长度为偶数倍16进制字符串
	 * @return 对应密文或是明文字符串
	 */
	public static String getEncryptResult(String StringSource){
		return getEncryptResult(StringSource,RC4key);
	}
	
	/**
	 * RC4加密获取明文或是密文字符串方法
	 * @param StringSource 长度为偶数倍16进制字符串
	 * @param sourceKey java中16进制表示的char[]类型密匙：如 char[] key={0x57, 0x34, 0x72,....}
	 * @return 对应密文或是明文字符串
	 */
	public static String getEncryptResult(String StringSource,char[] sourceKey){
		//处理输入数据
		int srcDataLength=StringSource.length()/2;
		byte[] S=new byte[256];
		byte[] srcData=new byte[srcDataLength];
		byte[] key=new byte[sourceKey.length];
		for(int i=0;i<srcDataLength;i++){
			srcData[i]=(byte)Integer.parseInt(StringSource.substring(2*i, 2*i+2),16);
		}
		for(int i=0;i<sourceKey.length;i++){
			key[i]=(byte)sourceKey[i];
		}
		init(S,key,key.length);
		return encrypt(S,srcData,srcData.length);
	}
	
	/**
	 * RC4加密初始化
	 * @param S
	 * @param key
	 * @param keyLengh
	 */
	private static void init(byte[] S,byte[] key,int keyLengh){
		byte[] T=new byte[256];
		for(int i=0;i<256;i++){
			S[i]=(byte)i;
			T[i]=key[i%keyLengh];
		}
		int j=0;
		for(int k=0;k<256;k++){
			j=(j+(S[k]&0xff)+(T[k]&0xff))%256;
			S[k]^=S[j];
			S[j]^=S[k];
			S[k]^=S[j];
		}
	}
	
	/**
	 * RC4加解密主方法
	 * @param S
	 * @param source
	 * @param sourceLengh
	 * @return
	 */
	public static String encrypt(byte[] S,byte[] source,int sourceLengh){
		int i=0,j=0;
		byte[] endSource=new byte[sourceLengh];
		for(int k=0;k<sourceLengh;k++){
			i=(i+1)%256;
			j=(j+(S[i]&0xff))%256;
			S[i]^=S[j];
			S[j]^=S[i];
			S[i]^=S[j];
			endSource[k]=(byte)((source[k]&0xff)^S[((S[i]&0xff)+(S[j]&0xff))%256]);
		}
		return bytes2Hex2String(endSource);
	}
	
	/**
	 * 字节数组转十六进制字符串
	 * @param source
	 * @return
	 */
	private static String bytes2Hex2String(byte[] source) {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<source.length;i++){
			String format = String.format("%02X", source[i]&0xff);
			sb.append(format);
		}
		return sb.toString();
	}
	
	 /**将16进制转字节数组
     * @param hexStr 
     * @return 
     */  
    public static byte[] parseHexStr2Byte(String hexStr) {  
        if (hexStr.length() < 1)  
            return null;  
        byte[] result = new byte[hexStr.length()/2];  
        for (int i = 0;i< hexStr.length()/2; i++) {  
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
            result[i] = (byte) (high * 16 + low);  
        }  
        return result;  
    }
	
    /**
     * AES加密算法
     * @param content 需要加密的内容
     * @param key 加密密匙（可取 128位/16字节     192位/24字节     256位/32字节）
     * @param iv 初始化向量
     * @return 加密后的字节数据
     * 
     *  算法/模式/填充                                         16字节加密后数据长度                            不满16字节加密后长度
	 *	01 AES/CBC/NoPadding             16                                                                 不支持
	 *	01 AES/CBC/PKCS5Padding          32                          16
	 *	01 AES/CBC/ISO10126Padding       32                          16
	 *
	 *	02 AES/CFB/NoPadding             16                                                                 原始数据长度
	 *	02 AES/CFB/PKCS5Padding          32                          16
	 *	02 AES/CFB/ISO10126Padding       32                          16
	 *
	 *	03 AES/ECB/NoPadding             16                                                                 不支持
	 *	03 AES/ECB/PKCS5Padding          32                          16
	 *	03 AES/ECB/ISO10126Padding       32                          16
	 *
	 *	04 AES/OFB/NoPadding             16                                                                 原始数据长度
	 *	04 AES/OFB/PKCS5Padding          32                          16
	 *	04 AES/OFB/ISO10126Padding       32                          16
	 *
	 *	05 AES/PCBC/NoPadding            16                                                                不支持
	 *	05 AES/PCBC/PKCS5Padding         32                          16
	 *	05 AES/PCBC/ISO10126Padding      32                          16
     */
    public static byte[] aesEncrypt(byte[] content, byte[] key, byte[] iv) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); //"算法/模式/补码方式"
            //IvParameterSpec ivps = new IvParameterSpec(iv);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            //cipher.init(Cipher.ENCRYPT_MODE, skeySpec,ivps);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            return cipher.doFinal(content);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }
 
     
    public static byte[] aesDecrypt(byte[] content, byte[] key, byte[] iv) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); //"算法/模式/补码方式"
            //IvParameterSpec ivps = new IvParameterSpec(iv);//使用CBC模式，需要一个向量iv，可增加加密算法的强度
            //cipher.init(Cipher.DECRYPT_MODE, skeySpec,ivps);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec); 
            return cipher.doFinal(content);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }
    
    /**
     * AES加密
     * @param source 加密字符串
     * @param key 密匙字节数组
     * @return 加密后字节数组base64编码结果
     */
    public static String getAESEncrypt(String source,String key){
    	String encResult=null;
    	if(key==null){
    		key=AESKeyStr;
    	}
		try {
			byte[] bsource = source.getBytes(EncryptUtil.default_charset);
			byte[] bkey = key.getBytes(EncryptUtil.default_charset);
			byte[] encSourceBytes = EncryptUtil.aesEncrypt(bsource,bkey,null);
			encResult = new String(Base64.getEncoder().encode(encSourceBytes));//new String(EncryptUtil.bytes2Hex2String(encSourceBytes));
		} catch (Exception e) {
			logger.error("AES加密异常:"+e.getLocalizedMessage());
		}
		return encResult;
    }
    
    /**
     * AES解密
     * @param endSource 加密后字节数组base64编码后字符串
     * @param key 密匙字节数组
     * @return 解密后字符串
     */
    public static String getAESDecrypt(String endSource,String key){
    	String decResult=null;
    	if(key==null){
    		key=AESKeyStr;
    	}
    	try {
    		byte[] encSourceBytes = Base64.getDecoder().decode(endSource);//EncryptUtil.parseHexStr2Byte(endSource);
    		byte[] bkey=key.getBytes(EncryptUtil.default_charset);
    		byte[] decSourceBytes = EncryptUtil.aesDecrypt(encSourceBytes, bkey, null);
			decResult =new String(decSourceBytes, EncryptUtil.default_charset);
		} catch (Exception e) {
			logger.error("AES解密异常:"+e.getLocalizedMessage());
		}
    	return decResult;
    }
    
    public static void main(String[] args) throws Exception {
    	String source="59790331*";
    	
    	byte[] key={0x2d,0x35,0x0a,0x00,0x56,0x7e,0x4e,0x1f,0x1b,0x59,0x2f,0x35,0x30,0x2d,0x55,0x0a};
    	
    	//密匙
    	/*byte[] key={0x2d,0x35,0x0a,0x00,0x56,0x7e,0x4e,0x1f,0x1b,0x59,0x2f,0x35,0x30,0x2d,0x55,0x0a};
    	
    	source+=System.currentTimeMillis();
    	
    	System.out.println("加密前明文字符串："+source);
    	
    	String aesEncrypt = EncryptUtil.getAESEncrypt(source, null);
    	
    	System.out.println("密文字节base64编码后字符串："+aesEncrypt);
    	
    	String aesDncrypt = EncryptUtil.getAESDecrypt(aesEncrypt, key);
    	
    	System.out.println("解密后明文字符串："+aesDncrypt);*/
    	
        String strKey= "hJ1LiHfjCdiILldI";
    	
    	//System.out.println(strKey.getBytes("utf-8").length);
    	
    	//String content="59790331*1500257646493";
		//byte[] aesEncrypt = EncryptUtil.aesEncrypt(content, strKey.getBytes("utf-8"));
    	String encsource="97905835*1500435151";
    	String authorization=EncryptUtil.getAESEncrypt(encsource, strKey);
		
    	System.out.println(authorization);
		
		System.out.println(EncryptUtil.getAESDecrypt(authorization, null));
		
		
		//String s="NDwbt2o0zpkDZBTKVID1eruF7eTgoPxYnMaXyKElKbI=";
		
		
		
	}
    
    
}
