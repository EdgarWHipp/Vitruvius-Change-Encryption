package tools.vitruv.change.encryption.impl;


import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.encryption.EChangeAdapter;
import tools.vitruv.change.encryption.EncryptedEChange;
import tools.vitruv.change.encryption.utils.EncryptionUtils;
/**
 * This class contains an array of bytes that contains a serialized encrypted EChange object.
 * @author Edgar Hipp
 *
 */
public class EncryptedEChangeImpl extends EObjectImpl implements EncryptedEChange,Serializable{
	private static final Logger logger = Logger.getLogger(EncryptedEChangeImpl.class.getName());
	private static final long serialVersionUID = 1L;
	// add to constants
	private byte[] encryptedChanges;
	
	public EncryptedEChangeImpl(){
	
	}
	
	public byte[] getEncryptedChanges() {
		return this.encryptedChanges;
	}

	/**
	 * Uses the key and the given algorithm to decrypt and deserialize this.encryptedChanges. A EChange object is returned.
	 * @param key
	 * @param algorithm
	 * @return EChange 
	 */
	public EChange reconstruct(SecretKey key, String algorithm) {
		EChange change=null;
		byte[] decryptedBytes= {};
		try {
			decryptedBytes = EncryptionUtils.getInstance().decryptBytes(this.encryptedChanges,key,algorithm);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
				| NoSuchPaddingException e) {
			logger.info("The decryption of the encrypted bytes did not succeed");
			System.exit(0);
		}
		change = deserializeToEChange(decryptedBytes);
		
		return change;
		
	}
	/**
	 * Deserealizes the decrypted EChange.
	 * @param decryptedBytes
	 * @return EChange
	 */
	private EChange deserializeToEChange(byte[] decryptedBytes) {
		String serializedDecryptedString = new String(decryptedBytes);
		
		EChangeAdapter adapter = new EChangeAdapterImpl();
		return adapter.deserialize(serializedDecryptedString).get();
		
	    
		
	}
	/**
	 * Sets the encryptedBytes field of an EncryptedEChangeImpl instance.
	 */
	public void setEncryptedChanges(SecretKey key, EChange change,String algorithm) {
		
		EChangeAdapter adapter = new EChangeAdapterImpl();
		String serializedEChangeString = adapter.serialize(change).get();
		byte[] clearTextBytes =serializedEChangeString.getBytes();
	
        try {
			this.encryptedChanges=EncryptionUtils.getInstance().encryptBytes(clearTextBytes,key,algorithm);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			logger.info("The encryption of the cleartext bytes did not succeed");
			System.exit(0);
		}
		
		
	}
	
	

	

	

	

	

	

	

	

}
