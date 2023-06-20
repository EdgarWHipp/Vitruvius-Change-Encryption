package tools.vitruv.change.encryption.tests.attributebased;

import static org.junit.jupiter.api.Assertions.assertTrue;



import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.metamodels.families.Member;
import junwei.cpabe.junwei.cpabe.Cpabe;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.encryption.impl.AsymmetricEncryptionSchemeImpl;
import tools.vitruv.change.encryption.impl.CpabeAdapterImpl;
import tools.vitruv.change.encryption.impl.EncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.symmetric.TestEncryptChangesSymmetricallyAlone;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;

public class TestEncryptChangesAsymmetricallyWithCPABE {
	private static final Logger logger = Logger.getLogger(TestEncrypChangesAsymmetricallyWithCPABE.class.getName());
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private AsymmetricEncryptionSchemeImpl encryptionScheme= new AsymmetricEncryptionSchemeImpl();
	private final EChangeCreationUtility creationUtil= EChangeCreationUtility.getInstance();
	private final EncryptionUtility encryptionUtil = EncryptionUtility.getInstance();
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
	public void testFailingFileAccess() throws Exception {
		String failingUserAttributes = getFailingUserAttributes();
		String policy = getPolicy();
		Cpabe test = new Cpabe();
		CpabeAdapterImpl adapter = new CpabeAdapterImpl();
		System.out.println("//start to setup");
		test.setup(publicKeyPath, masterKeyPath);
		System.out.println("//end to setup");

		System.out.println("//start to keygen");
		test.keygen(publicKeyPath, privateKeyPath, masterKeyPath, failingUserAttributes);
		System.out.println("//end to keygen");

		System.out.println("//start to enc");
		test.enc(publicKeyPath, policy, inputFile, encryptedFilePath);
		System.out.println("//end to enc");

		System.out.println("//start to dec");
		test.dec(publicKeyPath, privateKeyPath, encryptedFilePath, decryptedFilePath);
		System.out.println("//end to dec");
		File checkFile = new File(decryptedFilePath);
		
		assert checkFile.canWrite() == false;
		assert checkFile.canRead() == true;
	}
	/**
	 * Encrypts a set of valid changes and opens the decryptedChanges in read/write mode.
	 * @throws Exception
	 */
	@Test
	public void testPassingFileAccess() throws Exception {
		// PUBLIC KEY IS GIVEN
		// DECRYPT WITH PRIVATE KEY (PASSING CRITERIA = read/write access, otherwise only read access)
		
		
		
		String passingUserAttributes = getPassingUserAttributes();
		String policy = getPolicy();

		Cpabe test = new Cpabe();
		System.out.println("//start to setup");
		test.setup(publicKeyPath, masterKeyPath);
		System.out.println("//end to setup");

		System.out.println("//start to keygen");
		test.keygen(publicKeyPath, privateKeyPath, masterKeyPath, passingUserAttributes);
		System.out.println("//end to keygen");

		System.out.println("//start to enc");
		test.enc(publicKeyPath, policy, inputFile, encryptedFilePath);
		System.out.println("//end to enc");

		System.out.println("//start to dec");
		test.dec(publicKeyPath, privateKeyPath, encryptedFilePath, decryptedFilePath);
		System.out.println("//end to dec");
		
		File checkFile = new File(decryptedFilePath);
		assert checkFile.canWrite() == true;
		assert checkFile.canRead() == true;
		
	}
	
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
