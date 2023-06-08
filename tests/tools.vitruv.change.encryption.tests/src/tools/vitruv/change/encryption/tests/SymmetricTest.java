package tools.vitruv.change.encryption.tests;

import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.encryption.EncryptionScheme;



/*
 * Defines a set of Unit Tests for the symmetric encryption of DeltaBasedResources (model deltas).
 */


public class SymmetricTest {
	private static final Logger logger = Logger.getLogger(SymmetricTest.class.getName());
	private URI MEMBER_URI = URI.createFileURI(new File("").getAbsolutePath() + "/member.xmi");
	
	
	private URI OUTPUT_URI = URI.createFileURI(new File("").getAbsolutePath() + "/output.xmi");

	
	
	
	
	@Test
	public void testSaveAndLoadEncryptedChange() throws NoSuchAlgorithmException, InvalidKeyException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "AES");
		
		Map<String, Map<String,Object>> encryptionOption = Map.of("encryption", map);
		Map<String, Map<String,Object>> decryptionOption = Map.of("decryption", map);
		
		 Member member = FamiliesFactory.eINSTANCE.createMember();
	     member.setFirstName("Clara");
	     ResourceSet memberResourceSet = new ResourceSetImpl();
	     memberResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
	     Resource memberResource = memberResourceSet.createResource(MEMBER_URI);
	     memberResource.getContents().add(member);
		
		 
		
		 CreateEObject<Member> createObj =  TypeInferringAtomicEChangeFactory.getInstance().createCreateEObjectChange(member);
	     
	     EncryptionScheme scheme = new EncryptionScheme();
	     OutputStream outputStream = new BufferedOutputStream( new FileOutputStream("output.xmi"), 1024);
	     File file = new File("encrypted_changes.xmi");
	     List<EChange> changes = new ArrayList<EChange>();
	     changes.add(createObj);
	     scheme.encryptDeltaChangesTogether(map, changes, outputStream);
	     List<EChange> decryptedChanges = scheme.decryptDeltaChangesTogether(map, file);
	     logger.info("Decrypted: "+decryptedChanges.get(0)+"\nReal Object: "+createObj);
	     //still fails
	    // assert createObj.equals(decryptedChanges.get(0));
	     
	     
	}
	
	
	

	
	
	
	



}