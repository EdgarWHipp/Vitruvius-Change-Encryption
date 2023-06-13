package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
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
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.encryption.impl.AsymmetricEncryptionSchemeImpl;
import tools.vitruv.change.encryption.impl.EncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;
import javax.xml.bind.DatatypeConverter;

public class TestEncrypChangesAsymmetrically {
	private static final Logger logger = Logger.getLogger(TestEncryptChangesSymmetricallyAlone.class.getName());
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private AsymmetricEncryptionSchemeImpl encryptionScheme= new AsymmetricEncryptionSchemeImpl();
	private final EChangeCreationUtility creationUtil= EChangeCreationUtility.getInstance();
	private final EncryptionUtility encryptionUtil = EncryptionUtility.getInstance();
	private final KeyPair keys=null;
	
	@Test
	public void testAsymmetric() throws NoSuchAlgorithmException {
		SecureRandom secureRandom
        = new SecureRandom();

    KeyPairGenerator keyPairGenerator
        = KeyPairGenerator.getInstance("RSA");

    keyPairGenerator.initialize(
        2048, secureRandom);

    KeyPair keypair = keyPairGenerator.generateKeyPair();
		// check for keys
     System.out.println(
         "Public Key is: "
         + 
               keypair.getPublic().getEncoded());

     System.out.println(
         "Private Key is: "
         + 
               keypair.getPrivate().getEncoded());
	}
	
	@Test
	public void testAsymmetrically() throws NoSuchAlgorithmException {
		// PUBLIC KEY IS GIVEN
		// DECRYPT WITH PRIVATE KEY (PASSING CRITERIA = read/write access, otherwise only read access)
		Map map = encryptionUtil.getEncryptionDetailsMap();
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		
		CreateEObjectImpl<Member> decryptedChange =  (CreateEObjectImpl<Member>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		
		assertTrue(new EcoreUtil.EqualityHelper().equals(change, decryptedChange));   
	}
	private void getPassingUserAttributes() {
		
	}
	private void getFailingUserAttributes() {
		
	}
}
