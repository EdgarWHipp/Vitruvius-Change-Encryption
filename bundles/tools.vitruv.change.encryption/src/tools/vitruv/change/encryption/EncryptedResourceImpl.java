package tools.vitruv.change.encryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

public class EncryptedResourceImpl extends XMIResourceImpl{
	private static final String ENCRYPTION_KEY = "com.ibm.enav.key";
	private static final String ENCRYPTION_SCHEME = "DES";
	private static final String UNICODE_FORMAT = "UTF-8"; 
	private boolean encryptionEnabled = true;
	
	
	public EncryptedResourceImpl() {
		super();
		}
	public EncryptedResourceImpl(URI uri) {
		super(uri);
		}
	public void doLoad(InputStream inputStream, Map options) throws IOException {

		CipherInputStream decryptedStream = null;
	
		if ( encryptionEnabled ) {
			decryptedStream = decrypt(inputStream);
			super.doLoad(decryptedStream, options);
			decryptedStream.close();
		} else {
			
		}
	}
	public void doSave(OutputStream outputStream, Map options) throws IOException {
	
	CipherOutputStream encryptedStream = null;

	if ( encryptionEnabled ) {
		encryptedStream = encrypt(outputStream);
		super.doSave(encryptedStream, options);
		encryptedStream.flush();
		encryptedStream.close();
	} else {
		super.doSave(outputStream, options);
		}
	}
	
	protected SecretKey getKey() throws NoSuchAlgorithmException {
		SecretKeyFactory keyFactory = null;
		DESKeySpec keySpec = null;
		SecretKey key = null;

		try {
			keyFactory = SecretKeyFactory.getInstance( ENCRYPTION_SCHEME );
			keySpec = new DESKeySpec( ENCRYPTION_KEY.getBytes(UNICODE_FORMAT) );
			key = keyFactory.generateSecret( keySpec );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

		return key;
		}

	public CipherInputStream decrypt(InputStream inputStream){
		Cipher cipher = null;

	try {
		cipher = Cipher.getInstance( ENCRYPTION_SCHEME );
		cipher.init( Cipher.DECRYPT_MODE, getKey() );
		return new CipherInputStream(inputStream, cipher);
	} catch ( Exception e ) {
		e.printStackTrace();
	}

	return null;
	}

	public CipherOutputStream encrypt(OutputStream outputStream){
		Cipher cipher = null;

	try {
		cipher = Cipher.getInstance( ENCRYPTION_SCHEME );
		cipher.init( Cipher.ENCRYPT_MODE, getKey() );
		return new CipherOutputStream(outputStream, cipher);
	} catch ( Exception e ) {
		e.printStackTrace();
	}

	return null;
	}

	
	
}
