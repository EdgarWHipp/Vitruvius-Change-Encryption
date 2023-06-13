package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
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
	private void initSetup() {
		KeyPair PublicAndPrivateKey = encryptionUtil.generateRSAKkeyPair();
		String keys = DatatypeConverter.printHexBinary(
				PublicAndPrivateKey.getPublic().getEncoded());
	}
	@Test
	public void testAsymmetrically() throws NoSuchAlgorithmException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		
		CreateEObjectImpl<Member> decryptedChange =  (CreateEObjectImpl<Member>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		
		assertTrue(new EcoreUtil.EqualityHelper().equals(change, decryptedChange));   
	}
}
