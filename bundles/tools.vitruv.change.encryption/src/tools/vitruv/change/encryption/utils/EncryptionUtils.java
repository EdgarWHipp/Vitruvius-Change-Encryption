package tools.vitruv.change.encryption.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * This singleton class handles the encryption and decryption of bytes with a given key and algortihm.
 * @author Edgar Hipp
 *
 */
public final class EncryptionUtils {
	private final Logger logger = Logger.getLogger(EncryptionUtils.class.getName());

	private final Path filePathOfSymmetricKey = Paths.get( new File("").getAbsolutePath() + "/symmetric.key");
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
	public byte[] cryptographicFunctionAsymmetric(Map<?,?> options,int opMode,byte[] bytes) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		PublicKey publicKey = (PublicKey) options.get("publicKey");
		PrivateKey privateKey = (PrivateKey) options.get("privateKey");
		String algorithm =  (String) options.get("algorithm");

		// check if symmetrickey exists.
		SecretKey symmetricKey = (SecretKey) options.get("symmetricKey");
		// check if a hybrid approach is used for encryption/decryption.
		if (symmetricKey !=null) {
			switch (opMode){
			case Cipher.ENCRYPT_MODE:
				Cipher cipher = Cipher.getInstance("AES");
				cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
				byte[] encryptedData = cipher.doFinal(bytes);
				Cipher asymmetricCipher = Cipher.getInstance(algorithm);
				asymmetricCipher.init(Cipher.ENCRYPT_MODE, publicKey);
				byte[] encryptedKey = asymmetricCipher.doFinal(symmetricKey.getEncoded());
				Files.write(filePathOfSymmetricKey, encryptedKey, StandardOpenOption.CREATE);
				return encryptedData;
				
			case Cipher.DECRYPT_MODE:
				byte[] encryptedSymmetricKey = Files.readAllBytes(filePathOfSymmetricKey);
				Cipher rsaDecryptCipher = Cipher.getInstance("RSA");
				rsaDecryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
				byte[] decryptedKey = rsaDecryptCipher.doFinal(encryptedSymmetricKey);
				SecretKey decryptedSymmetricKey = new SecretKeySpec(decryptedKey, algorithm);
				Cipher decryptCipher = Cipher.getInstance("AES");
				decryptCipher.init(Cipher.DECRYPT_MODE, decryptedSymmetricKey);
				byte[] decryptedData = decryptCipher.doFinal(bytes);
				return decryptedData;
			default:
				logger.severe("no valid encryption option was selected during the hybrid asymmetric approach ");
				throw new IOException();
			}
			

		}else {
			Cipher asymmetricCipher = Cipher.getInstance(algorithm);
			switch (opMode){
			case Cipher.ENCRYPT_MODE:
				asymmetricCipher.init(Cipher.ENCRYPT_MODE, publicKey);
				byte[] encryptedBytes = asymmetricCipher.doFinal(bytes);
				return encryptedBytes;
			case Cipher.DECRYPT_MODE:
				asymmetricCipher.init(Cipher.DECRYPT_MODE, privateKey);
				byte[] decryptedBytes = asymmetricCipher.doFinal(bytes);
				return decryptedBytes;
			default:
				logger.severe("no valid encryption option was selected during the asymmetric approach ");
				throw new IOException();
			
			}
			
			
		
		
	
	}

}
