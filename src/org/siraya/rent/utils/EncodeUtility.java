package org.siraya.rent.utils;
 
import java.security.Key;
import java.security.MessageDigest; 
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.siraya.rent.utils.RentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Encryp utility.
 * 
 * @author User
 *
 */
@Service("encodeUtility")
public class EncodeUtility {
	private static final String ALGO = "AES";
    private static Logger logger = LoggerFactory.getLogger(EncodeUtility.class);
    @Autowired
    private IApplicationConfig applicationConfig;
    private Map<String,Key> keyCache = new java.util.HashMap<String,Key>();


	public EncodeUtility() {
		
	}
	
	private static byte[] hex2Byte(String str)
    {
       byte[] bytes = new byte[str.length() / 2];
       for (int i = 0; i < bytes.length; i++)
       {
          bytes[i] = (byte) Integer
                .parseInt(str.substring(2 * i, 2 * i + 2), 16);
       }
       return bytes;
    }
	
	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}
	
	public static String sha1(String s) {
		MessageDigest sha = null;

		try {
			sha = MessageDigest.getInstance("SHA-1");
			sha.update(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		return byte2hex(sha.digest());

	}
	

	/**
	 * encrypt
	 * @param Data
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String Data,String keyName){
		try {
			Key key = generateKey(keyName);
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.ENCRYPT_MODE, key);
			byte[] encVal = c.doFinal(Data.getBytes());
			String encryptedValue = byte2hex(encVal);
			return encryptedValue;
		}catch(Exception e){
			logger.error("encrypt",e);
			throw new RentException(RentException.RentErrorCode.ErrorGeneral,e.getMessage());
		}
	}

	/**
	 * decrypt
	 * @param encryptedData
	 * @return
	 * @throws Exception
	 */
	public  String decrypt(String encryptedData,String keyName) {
		try{
			Key key = generateKey(keyName);
			Cipher c = Cipher.getInstance(ALGO);
			c.init(Cipher.DECRYPT_MODE, key);
			byte[] decordedValue = hex2Byte(encryptedData);
			byte[] decValue = c.doFinal(decordedValue);
			String decryptedValue = new String(decValue);
			return decryptedValue;
		}catch(Exception e){
			logger.error("error decrypt",e);
			throw new RentException(RentException.RentErrorCode.ErrorGeneral,
					"decrypt data:"+encryptedData+" error:"+e.getMessage());

		}
	}

	private  Key generateKey(String keyName) throws Exception {
		if (keyCache.containsKey(keyName)){
			return keyCache.get(keyName);
		}
    	Map<String,Object> setting = applicationConfig.get("keydb");
    	Object keyString = setting.get(keyName);
    	if (keyString == null) {
    		throw new RentException(RentException.RentErrorCode.ErrorEncrypt,
    				"key "+keyName+" not found");
    	}

    	byte[] keyValue =((String)keyString).getBytes();
		Key key = new SecretKeySpec(keyValue, ALGO);
		keyCache.put(keyName, key);
		return key;
	}
	
	public IApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}

	public void setApplicationConfig(IApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}
}
