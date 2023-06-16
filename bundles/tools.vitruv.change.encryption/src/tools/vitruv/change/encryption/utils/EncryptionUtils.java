package tools.vitruv.change.encryption.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import edu.kit.ipd.sdq.commons.util.java.Pair;
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
	 
	public byte[] cryptographicFunctionSymmetric(Map<?,?> options,int opMode,byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		SecretKey secretKey = (SecretKey) options.get("secretKey");
		String algorithm =  (String) options.get("algorithm");
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(opMode, secretKey);
		byte[] result= cipher.doFinal(bytes);
		return result;
	 	
	}
	public byte[] cryptographicFunctionAsymmetric(Map<?,?> options,int opMode,byte[] bytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		PublicKey publicKey = (PublicKey) options.get("publicKey");
		PrivateKey privateKey = (PrivateKey) options.get("privateKey");
		String algorithm =  (String) options.get("algorithm");
		Cipher cipher = Cipher.getInstance(algorithm);
		if (opMode==Cipher.ENCRYPT_MODE) {
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedBytes = cipher.doFinal(bytes);
			return encryptedBytes;
		}
			if (opMode==Cipher.DECRYPT_MODE) {
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedBytes = cipher.doFinal(bytes);
			return decryptedBytes;
		}
			System.out.println("no correct option has been found");
			return null;
	}
	
	
	 
	
	
}
