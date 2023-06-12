package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
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

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.encryption.impl.EncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
/**
 * Test Class for encryption and decryption of single changes with different keys.
 * @author Edgar Hipp
 *
 */

public class TestEncryptChangesWithDifferentKeys {
	private static final Logger logger = Logger.getLogger(TestEncryptChangesSymmetricallyTogether.class.getName());
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private EncryptionSchemeImpl encryptionScheme = new EncryptionSchemeImpl();
	private final EChangeCreationUtility creationUtil= EChangeCreationUtility.getInstance();
	private static final String ENCRYPTION_KEY = "com.ibm.enav.key";
	private static final String ENCRYPTION_SCHEME = "DES";
	private static final String UNICODE_FORMAT = "UTF-8"; 
	
	@Test 
	public void testSplitMemberCreationInto3() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException, InvalidKeySpecException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set);
		Map<String,SecretKey[]> map = new HashMap<>();
		
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance( ENCRYPTION_SCHEME );
		DESKeySpec keySpec = new DESKeySpec( ENCRYPTION_KEY.getBytes(UNICODE_FORMAT) );
		
	
		
		// Create map of encryptionOptions
		SecretKey secretKey1 = keyFactory.generateSecret( keySpec );
		SecretKey secretKey2 = keyFactory.generateSecret( keySpec );
		SecretKey secretKey3 = keyFactory.generateSecret( keySpec );
		
		
		
		SecretKey[] keys= {secretKey1,secretKey2,secretKey3};
		map.put("differentEncryption", keys);
	    
		long startTime = System.currentTimeMillis();
	   // encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
	   // List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
	    long endTime = System.currentTimeMillis();

		long totalTime = endTime - startTime;

		logger.severe("Time spend:\t"+totalTime+"ms");
	    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));

	    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));
	}
}
