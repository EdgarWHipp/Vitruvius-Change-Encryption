package tools.vitruv.change.encryption.tests;

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
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.encryption.EncryptedResourceFactoryImpl;
import tools.vitruv.change.encryption.EncryptionScheme;



/*
 * Defines a set of Unit Tests for the symmetric encryption of DeltaBasedResources (model deltas).
 */


public class SymmetricTest {
	private static final Logger logger = Logger.getLogger(SymmetricTest.class.getName());
	private URI MEMBER_URI = URI.createFileURI(new File("").getAbsolutePath() + "/member.enc");
	
	

	
	
	
	
	@Test
	public void testSaveAndLoadEncryptedChange() throws NoSuchAlgorithmException, InvalidKeyException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "AES");
		
	
		 Member member = FamiliesFactory.eINSTANCE.createMember();
	     member.setFirstName("Clara");
	     ResourceSet memberResourceSet = new ResourceSetImpl();
	     memberResourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("enc", new EncryptedResourceFactoryImpl());
	     Resource memberResource = memberResourceSet.createResource(MEMBER_URI);
	     memberResource.getContents().add(member);
		
		 CreateEObject<Member> createObj =  TypeInferringAtomicEChangeFactory.getInstance().createCreateEObjectChange(member);
		 
		 createObj.setAffectedEObjectID("123");
		 createObj.setIdAttributeValue("testAttribute");
	     EncryptionScheme scheme = new EncryptionScheme();
	     File file = new File("encrypted_changes");
	     
	     scheme.encryptDeltaChange(map, createObj, file);
	     EChange decryptedChange = scheme.decryptDeltaChange(map, file);
	     logger.severe("Decrypted: "+((CreateEObject<Member>)decryptedChange+"\nReal Object: "+createObj));
	     
	     
	     //file.delete();
	     
	     
	     //Iterate over the fields
	     Class<? extends EChange> concreteClass = createObj.getClass();
	        while (concreteClass != null) {
	            Field[] fields = concreteClass.getDeclaredFields();
	            for (Field field : fields) {
	                field.setAccessible(true); // Set field accessible to read its value
	                try {
	                	Object value1 = field.get(createObj);
	                    Object value2 =  field.get(decryptedChange);
	                    
	                    
	                    if (!value1.equals(value2)) {
	                        assert false;
	                    }
	                } catch (IllegalAccessException e) {
	                    e.printStackTrace();
	                }
	            }
	            concreteClass =  (Class<? extends EChange>) concreteClass.getSuperclass(); // Move to the next class in the inheritance hierarchy
	        }
	     
	     
	}
	
	
	

	
	
	
	



}