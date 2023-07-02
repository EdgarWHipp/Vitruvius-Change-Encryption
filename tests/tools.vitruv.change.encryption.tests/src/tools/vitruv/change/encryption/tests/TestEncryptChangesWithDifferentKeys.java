package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.encryption.impl.differentkeys.ChangeAndKey;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
/**
 * Test Class for encryption and decryption of single changes with different keys.
 * @author Edgar Hipp
 *
 */

public class TestEncryptChangesWithDifferentKeys extends TestChangeEncryption{
	private final String filePath = new File("").getAbsolutePath()+"/differentKeyEncryption";

	
	@Test 
	public void testSplitMemberCreationInto3() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException, InvalidKeySpecException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		Map<String,SecretKey[]> map = new HashMap<>();
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		
		// Create keys
		SecretKey secretKey1 =  keyGenerator.generateKey();
		SecretKey secretKey2 =  keyGenerator.generateKey();
		SecretKey secretKey3 =  keyGenerator.generateKey();
		
		List<ChangeAndKey> changesAndKeys = new ArrayList<>();
		changesAndKeys.add(new ChangeAndKey(changes.get(0),secretKey1));
		changesAndKeys.add(new ChangeAndKey(changes.get(1),secretKey2));
		changesAndKeys.add(new ChangeAndKey(changes.get(2),secretKey3));
		
		SecretKey[] keys= {secretKey1,secretKey2,secretKey3};
		map.put("differentEncryption", keys);
	    
		long startTime = System.currentTimeMillis();
	    TestChangeEncryption.SYM_ENCRYPTIONSCHEME.encryptDeltaChangesDifferently(TestChangeEncryption.FILE, changesAndKeys);
		
		// Write the encrypted Objects to a file.
		try (FileOutputStream fileOutputStream = new FileOutputStream(this.filePath);
	             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
	            // Serialize and save each encrypted object
	            for (EObject encryptedObject : encryptedObjects) {
	                objectOutputStream.writeObject(encryptedObject);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	   // List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
	    long endTime = System.currentTimeMillis();

		long totalTime = endTime - startTime;
		
		
	}
}

