package tools.vitruv.change.encryption.tests.util;

import java.security.KeyPair;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	public List<HashMap<String,Object>> getAllEncryptionMapsSymmetric() throws NoSuchAlgorithmException{
		List<HashMap<String,Object>> maps = new ArrayList<>();
		maps.add(getEncryptionDetailsMapAES());
		maps.add(getEncryptionDetailsMapDES());
		maps.add(getEncryptionDetailsMapDESede());
		maps.add(getEncryptionDetailsMapARCFOUR());
		maps.add(getEncryptionDetailsMapBlowfish());
		
		return maps;
		}
	
	private HashMap<String,Object> getEncryptionDetailsMapAES() throws NoSuchAlgorithmException{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "AES");
		return map;
	}
	private HashMap<String,Object> getEncryptionDetailsMapDES() throws NoSuchAlgorithmException{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		keyGenerator.init(56);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "DES");
		return map;
	}
	private HashMap<String,Object> getEncryptionDetailsMapDESede() throws NoSuchAlgorithmException{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
		keyGenerator.init(168);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "DESede");
		return map;
	}
	private HashMap<String,Object> getEncryptionDetailsMapARCFOUR() throws NoSuchAlgorithmException{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("ARCFOUR");
		keyGenerator.init(256);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "ARCFOUR");
		return map;
	}
	private HashMap<String,Object> getEncryptionDetailsMapBlowfish() throws NoSuchAlgorithmException{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
		keyGenerator.init(256);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "Blowfish");
		return map;
	}
	
	
	public HashMap<String,Object> getEncryptionDetailsMapAsymmetricRSA() throws NoSuchAlgorithmException{
		HashMap <String,Object> map = new HashMap<String, Object>();
	
		SecureRandom secureRandom= new SecureRandom();

	    KeyPairGenerator keyPairGenerator;
		
		keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        keyPairGenerator.initialize(
            2048, secureRandom);
 
        KeyPair pair =  keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
		map.put("privateKey", privateKey);
		map.put("publicKey", publicKey);
		map.put("algorithm","RSA");
		return map;
	}
	
	
}
