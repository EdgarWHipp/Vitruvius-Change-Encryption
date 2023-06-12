package tools.vitruv.change.encryption.tests.util;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class EncryptionUtility {
	private static EncryptionUtility instance;
	private EncryptionUtility() {}
	public static EncryptionUtility getInstance() {
		   if(instance == null) {
			   instance = new EncryptionUtility();
		      }

		       // returns the singleton object
		       return instance;
	   }
	public HashMap<String,Object> getEncryptionDetailsMap() throws NoSuchAlgorithmException{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "AES");
		return map;
	}
}
