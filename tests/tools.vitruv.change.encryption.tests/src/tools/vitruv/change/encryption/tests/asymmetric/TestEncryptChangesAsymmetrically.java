package tools.vitruv.change.encryption.tests.asymmetric;

import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.encryption.impl.EncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.TestChangeEncryption;
import tools.vitruv.change.encryption.tests.symmetric.TestEncryptChangesSymmetricallyAlone;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;

public class TestEncryptChangesAsymmetrically {
	private static final Logger logger = Logger.getLogger(TestEncryptChangesSymmetricallyAlone.class.getName());
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private EncryptionSchemeImpl encryptionScheme = new EncryptionSchemeImpl();
	private final EChangeCreationUtility creationUtil= EChangeCreationUtility.getInstance();
	private final EncryptionUtility encryptionUtil = EncryptionUtility.getInstance();

	
	
	@Test
	public void testCreateEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		
		List<String> times = new ArrayList<>();
		HashMap<String,Object> map = TestChangeEncryption.ENCRYPTIONUTIL.getEncryptionDetailsMapAsymmetricRSA();
		
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		long startTime = System.currentTimeMillis();
		//
		encryptionScheme.encryptDeltaChangeAloneAsymmetrically(map,change,fileWithEncryptedChanges);
		
		CreateEObjectImpl<Member> decryptedChange =  (CreateEObjectImpl<Member>) encryptionScheme.decryptDeltaChangeAloneAsymmetrically(map, fileWithEncryptedChanges);
		//
		long endTime = System.currentTimeMillis();
		
		long totalTime = endTime - startTime;
		times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		assertTrue(new EcoreUtil.EqualityHelper().equals(change, decryptedChange));   
		
		
	}
	
}
