package tools.vitruv.change.encryption.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
/**
 * This singleton class handles the encryption and decryption of bytes with a given key and algortihm.
 * @author Edgar Hipp
 *
 */
public final class EncryptionUtils {
	private static EncryptionUtils INSTANCE;
	 private EncryptionUtils() {        
	    }
	 
	 
	 /**
	  * Gets the instance of the EncryptionUtils singleton.
	  */
	 public static EncryptionUtils getInstance() {
	        if(INSTANCE == null) {
	            INSTANCE = new EncryptionUtils();
	        }
	        
	        return INSTANCE;
	    }
	 /**
	  * Encrypts a given byte[] with a given secret key and algorithm.
	  * @param clearTextBytes
	  * @param key
	  * @param algorithm
	  * @return byte[]
	  * @throws NoSuchAlgorithmException
	  * @throws NoSuchPaddingException
	  * @throws InvalidKeyException
	  * @throws IllegalBlockSizeException
	  * @throws BadPaddingException
	  */
	public byte[] encryptBytes (byte[] clearTextBytes,SecretKey key, String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		  Cipher cipher = Cipher.getInstance(algorithm);
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	        return cipher.doFinal(clearTextBytes);
	}
	/**
	 * Decrypts a given byte[] with a given secret key and algorithm.
	 * @param encryptedBytes
	 * @param key
	 * @param algorithm
	 * @return byte[]
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 */
	public byte[] decryptBytes (byte[] encryptedBytes,SecretKey key,String algorithm) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		 Cipher cipher = Cipher.getInstance(algorithm);
		 cipher.init(Cipher.DECRYPT_MODE, key);
		 return cipher.doFinal(encryptedBytes);
	}
}
