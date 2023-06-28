package tools.vitruv.change.encryption.tests.attributebased;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;



import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.metamodels.families.Member;
import junwei.cpabe.junwei.cpabe.Cpabe;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.encryption.impl.asymmetric.AsymmetricEncryptionSchemeImpl;
import tools.vitruv.change.encryption.impl.attributebased.CpabeAdapterImpl;
import tools.vitruv.change.encryption.tests.TestChangeEncryption;
import tools.vitruv.change.encryption.tests.symmetric.TestEncryptChangesSymmetricallyAlone;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;

public class TestEncryptChangesAsymmetricallyWithCPABE extends TestChangeEncryption{
	private final String publicKeyPath = new File("").getAbsolutePath() +"/public_key";
	private final String masterKeyPath = new File("").getAbsolutePath() +"/master_key";
	private final String privateKeyPath = new File("").getAbsolutePath() +"/private_key";
	
	private final String inputFile = new File("").getAbsolutePath() +"/input.pdf";
	private final String encryptedFilePath = new File("").getAbsolutePath() +"/encrypted.pdf";
	private final String decryptedFilePath = new File("").getAbsolutePath() +"/decrypted.pdf";
	
	/**
	 * Encrypts a set of valid changes and opens the decryptedChanges in read-only mode.
	 */
	@Test
	public void testFailingFileAccessWithCreateEObjectChange() throws Exception {
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member)memberResource.getContents().get(0);
		EChange change = TypeInferringAtomicEChangeFactory.getInstance().createCreateEObjectChange(member);

		String failingUserAttributes = getFailingUserAttributes();
		String policy = getPolicy();
		Cpabe test = new Cpabe();
		//init adapter
		CpabeAdapterImpl adapter = 
				new CpabeAdapterImpl(test,privateKeyPath,publicKeyPath,masterKeyPath,decryptedFilePath,encryptedFilePath,inputFile);
		
		//encrypt
		adapter.encryptAloneAndGenerateKeys(failingUserAttributes, policy,change);
		
		//decrypt
		try {
			EChange decryptedChange = adapter.decryptAlone();
		} catch(Exception e) {
			assertEquals(e.getMessage(),"decryption has failed");
		}
		 
		
		
	}
	/**
	 * Encrypts a set of valid changes and opens the decryptedChanges in read/write mode.
	 * @throws Exception
	 */
	@Test
	public void testPassingFileAccessWithCreateEObjectChange() throws Exception {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		// PUBLIC KEY IS GIVEN
		// DECRYPT WITH PRIVATE KEY 
		
		
		String passingUserAttributes = getPassingUserAttributes();
		String policy = getPolicy();

		Cpabe test = new Cpabe();
		//init adapter
		CpabeAdapterImpl adapter = new CpabeAdapterImpl(test,privateKeyPath,publicKeyPath,masterKeyPath,decryptedFilePath,encryptedFilePath,inputFile);
		
		//encrypt
		adapter.encryptAloneAndGenerateKeys(passingUserAttributes, policy,change);
		
		//decryption should fail here.
		EChange decryptedChange = adapter.decryptAlone();
		assertNotEquals(decryptedChange,null);
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		
		
	}
	// These tests should be extended to a wider range of policies and attributes to test the scalability.
	private String getPassingUserAttributes() {
		String passing = 
				"priority:8 name:LaraWeide title:seniorDeveloper";
		return passing;
	}
	private String getFailingUserAttributes() {
		String failing = 
				"priority:4 name:MaxMustermann title:workingStudent";
		return failing;
	}
	private String getPolicy() {
		String policy = 
				"priority:8 title:seniorDeveloper 1of2";
		return policy;
	}
}
