package tools.vitruv.change.encryption.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
	 
	public byte[] cryptographicFunction(Map<?,?> options,int opMode,byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		SecretKey secretKey = (SecretKey) options.get("secretKey");
		String algorithm =  (String) options.get("algorithm");
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(opMode, secretKey);
		byte[] result= cipher.doFinal(bytes);
		return result;
	 	
	}
	
	
}
