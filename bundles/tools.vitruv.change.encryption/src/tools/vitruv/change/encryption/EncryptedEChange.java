package tools.vitruv.change.encryption;


import javax.crypto.SecretKey;

import tools.vitruv.change.atomic.EChange;

public interface EncryptedEChange {
	
	/**
	 * Returns the value of the '<em><b>encryptedChanges</b></em>' attribute.
	 * @return
	 */
	public byte[] getEncryptedChanges();
	
	/**
	 * Sets the value of '{@link tools.vitruv.change.encryption.EncryptedEChange#getEncryptedChanges <em>encryptedChanges</em>}' attribute.
	 * @param encryptedChanges
	 */
	public void setEncryptedChanges(SecretKey key, EChange change, String algorithm);
	
}
