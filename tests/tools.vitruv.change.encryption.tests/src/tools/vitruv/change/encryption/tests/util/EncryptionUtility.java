package tools.vitruv.change.encryption.tests.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * This singleton class handles the creation of appropriate Hashmaps 
 * that contain the key and algorithm logic necessary for the tests.
 */
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
	//----SYMMETRIC
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
	
	//----ASYMMETRIC
	
	public List<HashMap<String,Object>> getAllEncryptionMapsAsymmetric() throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, SignatureException{
		List<HashMap<String,Object>> maps = new ArrayList<>();
		maps.add(getEncryptionDetailsMapAsymmetricRSA());
		maps.add(getEncryptionDetailsMapAsymmetricDSA());
		maps.add(getEncryptionDetailsMapAsymmetricDiffieHellman());
		

		return maps;
	}
	
	private HashMap<String,Object> getEncryptionDetailsMapAsymmetricRSA() throws NoSuchAlgorithmException{
		HashMap <String,Object> map = new HashMap<String, Object>();
	
		SecureRandom secureRandom= new SecureRandom();

	    KeyPairGenerator keyPairGenerator;
		
		keyPairGenerator = KeyPairGenerator.getInstance("RSA");

        keyPairGenerator.initialize(
        		4500, secureRandom);
 
        KeyPair pair =  keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
		map.put("privateKey", privateKey);
		map.put("publicKey", publicKey);
		map.put("algorithm","RSA");
		return map;
	}
	private HashMap<String,Object> getEncryptionDetailsMapAsymmetricDSA() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException{
		HashMap <String,Object> map = new HashMap<String, Object>();
		
		SecureRandom secureRandom= new SecureRandom();

	    KeyPairGenerator keyPairGenerator;
		
		keyPairGenerator = KeyPairGenerator.getInstance("DSA");

        keyPairGenerator.initialize(
        		1024, secureRandom);
 
        KeyPair pair =  keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
		map.put("privateKey", privateKey);
		map.put("publicKey", publicKey);
		map.put("algorithm","DSA");
		return map;
	}
	private HashMap<String,Object> getEncryptionDetailsMapAsymmetricDiffieHellman() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, SignatureException{
		HashMap <String,Object> map = new HashMap<String, Object>();
		
		SecureRandom secureRandom= new SecureRandom();

	    KeyPairGenerator keyPairGenerator;
		
		keyPairGenerator = KeyPairGenerator.getInstance("DiffieHellman");

        keyPairGenerator.initialize(
        		1024, secureRandom);
 
        KeyPair pair =  keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
		map.put("privateKey", privateKey);
		map.put("publicKey", publicKey);
		map.put("algorithm","DiffieHellman");
		return map;
	}
	private HashMap<String,Object> getEncryptionDetailsMapAsymmetricHybridRSA() throws NoSuchAlgorithmException{
		HashMap <String,Object> map = new HashMap<String, Object>();

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(1024); // Choose appropriate key size, e.g., 2048 or 4096
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(1024); // Choose appropriate key size, e.g., 128, 192, or 256
		SecretKey symmetricKey = keyGenerator.generateKey();
		map.put("symmetricKey", symmetricKey);
		map.put("publicKey", publicKey);
		map.put("privateKey", privateKey);
		
		
		
		return map;
	}
	
	
}
