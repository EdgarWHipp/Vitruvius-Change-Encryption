package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.encryption.EncryptionScheme;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.changederivation.DefaultStateBasedChangeResolutionStrategy;



/*
 * Defines a set of Unit Tests for the symmetric encryption of DeltaBasedResources (model deltas).
 */


public class SymmetricTest {
	private static final Logger logger = Logger.getLogger(SymmetricTest.class.getName());
	private URI MEMBER_URI = URI.createFileURI(new File("").getAbsolutePath() + "/member.ecore");
	
	

	
	
	
	
	@Test
	public void testSaveAndLoadEncryptedChange() throws NoSuchAlgorithmException, InvalidKeyException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "AES");
		
	
		List<EChange> changes = new ArrayList<>();
		var set = new ResourceSetImpl();
		createCreateMemberChangeSequence(changes, set);

	     EncryptionScheme scheme = new EncryptionScheme();
	     File file = new File("encrypted_changes");
	     
	     scheme.encryptDeltaChange(map, changes, file);
	     List<EChange> decryptedChange = scheme.decryptDeltaChange(map, file);
	     logger.info("Decrypted: "+decryptedChange+"\nReal Object: "+changes);
	     
	     var transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	     var newResourceSet = new ResourceSetImpl();
	     withFactories(newResourceSet);
	     transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));

	     assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
	}




	private void withFactories(ResourceSet set) {
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	}


	private void createCreateMemberChangeSequence(List<EChange> changes, ResourceSet set) {
		Member member = FamiliesFactory.eINSTANCE.createMember();
		 member.setFirstName("Clara");
		 withFactories(set);
	     Resource memberResource = set.createResource(MEMBER_URI);
	     memberResource.getContents().add(member);
		 changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
	}
	
	
	

	
	
	
	



}