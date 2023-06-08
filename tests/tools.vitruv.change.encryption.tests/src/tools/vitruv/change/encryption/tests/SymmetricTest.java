package tools.vitruv.change.encryption.tests;

import java.io.BufferedInputStream;







import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.IntStream;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.changederivation.DeltaBasedResource;
import tools.vitruv.change.changederivation.DeltaBasedResourceFactory;
import tools.vitruv.change.encryption.EncryptionScheme;
import tools.vitruv.change.serialization.EChangeDeserializer;
import tools.vitruv.change.serialization.EChangeSerializer;



/*
 * Defines a set of Unit Tests for the symmetric encryption of DeltaBasedResources (model deltas).
 */


public class SymmetricTest {
	private static final Logger logger = Logger.getLogger(SymmetricTest.class.getName());
	private URI MEMBER_URI = URI.createFileURI(new File("").getAbsolutePath() + "/member.xmi");
	
	
	private URI DELTA_URI = URI.createFileURI(new File("").getAbsolutePath() + "/test.delta");

	
	
	@Test
	public void testDoLoadAndDoSaveCreateEObjectEncryptionTest() throws IOException, NoSuchAlgorithmException {
		long startTime = System.currentTimeMillis();
		
		
		Member member = FamiliesFactory.eINSTANCE.createMember();
		member.setFirstName("Clara");
		
		
		
		// Create DeltaBasedResource
		ResourceSet resourceSetDeltaBased = new ResourceSetImpl();
		
		resourceSetDeltaBased.getResourceFactoryRegistry().getExtensionToFactoryMap().put("delta", new DeltaBasedResourceFactory());

		
		Resource deltaResource = resourceSetDeltaBased.createResource(DELTA_URI);
		deltaResource.getContents().add(member);
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "AES");
		
		Map<String, Map<String,Object>> encryptionOption = Map.of("encryption", map);
		Map<String, Map<String,Object>> decryptionOption = Map.of("decryption", map);
		
		OutputStream outputStream = new BufferedOutputStream( new FileOutputStream("output.xmi"), 1024);
		
		// save the resource
		
		((DeltaBasedResource) deltaResource).doSave(outputStream,encryptionOption);
		
		//load the resource
		((DeltaBasedResource) deltaResource).doLoad(inputStream,decryptionOption);
		long endtime = System.currentTimeMillis();
		
		
	}
	
	
	@Test
	public void testSerializationAndDeserializationOfCreateEObjectImpl() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();

	    // Register the serializer and deserializer with the ObjectMapper
	    objectMapper.registerModule(new SimpleModule()
	        .addSerializer(new EChangeSerializer())
	        .addDeserializer(EChange.class, new EChangeDeserializer()));

	    // Create an instance of the concrete class
	    Member member = FamiliesFactory.eINSTANCE.createMember();
	    member.setFirstName("Clara");
	    CreateEObjectImpl<Member> createObj =  (CreateEObjectImpl<Member>) TypeInferringAtomicEChangeFactory.getInstance().createCreateEObjectChange(member);
	   
	    createObj.setAffectedEObjectID("123");
	    createObj.setIdAttributeValue("test");
	    createObj.setAffectedEObjectType(FamiliesFactory.eINSTANCE.getFamiliesPackage().getMember());
        createObj.setAffectedEObject(member);
	   // CreateEObjectImpl<Member> = 
	 // Print the object
	    System.out.println("Object " + createObj);
	   // createObj.setAffectedEObjectType();
	  //  createObj.set
	    // Serialize the object to JSON
	    String json = objectMapper.writeValueAsString(createObj);

	    // Print the JSON
	    System.out.println("JSON "+ json);

	    // Deserialize the JSON to an object
	    EChange identifiable = objectMapper.readValue(json, EChange.class);

	    // Print the object
	    System.out.println("deserialized Object" + ((CreateEObjectImpl) identifiable));
	    // check if objects match
		assert createObj.equals(identifiable);
		
	}	
	@Test
	public void testtestSerializationAndDeserializationOfCreateEObjectImpl() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();

	    // Register the serializer and deserializer with the ObjectMapper
	    objectMapper.registerModule(new SimpleModule()
	        .addSerializer(new EChangeSerializer())
	        .addDeserializer(EChange.class, new EChangeDeserializer()));
		 Member member = FamiliesFactory.eINSTANCE.createMember();
	     member.setFirstName("Clara");
	     ResourceSet resourceSet = new ResourceSetImpl();
	     
		 Resource resource = resourceSet.createResource(DELTA_URI);
		 InsertRootEObject<Member> createObj =  TypeInferringAtomicEChangeFactory.getInstance().createInsertRootChange(member,resource,0);
		 
		 String json = objectMapper.writeValueAsString(createObj);
		 
	}
	@Test 
	public void testDoLoad10Members() throws NoSuchAlgorithmException, IOException {
long startTime = System.currentTimeMillis();
		
		
		Member member = FamiliesFactory.eINSTANCE.createMember();
		member.setFirstName("Clara");
		
		
		
		// Create DeltaBasedResource
		ResourceSet resourceSetDeltaBased = new ResourceSetImpl();
		
		resourceSetDeltaBased.getResourceFactoryRegistry().getExtensionToFactoryMap().put("delta", new DeltaBasedResourceFactory());

		
		Resource deltaResource = resourceSetDeltaBased.createResource(DELTA_URI);
		IntStream.range(0, 10)
	    .forEach(x -> deltaResource.getContents().add(member));
		
		
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "AES");
		
		Map<String, Map<String,Object>> encryptionOption = Map.of("encryption", map);
		Map<String, Map<String,Object>> decryptionOption = Map.of("decryption", map);
		
		OutputStream outputStream = new BufferedOutputStream( new FileOutputStream("output.xmi"), 1024);
		InputStream inputStream = new BufferedInputStream( new FileInputStream("output.xmi"), 1024);
		// save the resource
		
		((DeltaBasedResource) deltaResource).doSave(outputStream,encryptionOption);
		
		//load the resource
		((DeltaBasedResource) deltaResource).doLoad(inputStream,decryptionOption);
		long endtime = System.currentTimeMillis();
	}
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
	     ResourceSet resourceSet = new ResourceSetImpl();
	        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
	     
		 Resource resource = resourceSet.createResource(MEMBER_URI);
		 resource.getContents().add(member);
		 
	     InsertRootEObject<Member> createObj =  TypeInferringAtomicEChangeFactory.getInstance().createInsertRootChange(member,resource,0);
	     EncryptionScheme scheme = new EncryptionScheme();
	     OutputStream outputStream = new BufferedOutputStream( new FileOutputStream("output.xmi"), 1024);
	     File file = new File("encrypted_changes.xmi");
	     List<EChange> changes = new ArrayList<EChange>();
	     changes.add(createObj);
	     scheme.encryptDeltaChangesTogether(map, changes, outputStream);
	     scheme.decryptDeltaChangesTogether(map, file);
	     
	     resource.delete(null);
	}
	
	
	

	
	
	
	



}