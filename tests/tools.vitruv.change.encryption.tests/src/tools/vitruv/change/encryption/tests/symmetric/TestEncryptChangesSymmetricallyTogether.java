package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.changederivation.DefaultStateBasedChangeResolutionStrategy;
import tools.vitruv.change.encryption.EncryptionScheme;
import tools.vitruv.change.encryption.impl.EncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;



/*
 * Defines a set of Unit Tests for the symmetric encryption of model deltas. All the changes in this test class are applied to a Member object
 *  and uses the new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges() function to create ONLY valid changes. One key 
 *  and one algorithm is used for the entire encryption and decryption process.
 */


public class TestEncryptChangesSymmetricallyTogether {
	private static final Logger logger = Logger.getLogger(TestEncryptChangesSymmetricallyTogether.class.getName());
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private EncryptionSchemeImpl encryptionScheme = new EncryptionSchemeImpl();
	private final EChangeCreationUtility creationUtil= EChangeCreationUtility.getInstance();
	private final EncryptionUtility encryptionUtil= EncryptionUtility.getInstance();
	
	
	

	/**
	 * Test the Encryption and Decryption of the ReplaceSingleAttribute change; 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test 
	public void testSaveAndLoadCreateReplaceSingleAttributeChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {

			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createReplaceSingleAttributeChange(changes, set);
	
		 
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		   
		     
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
	
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
			
			
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));  
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	    
	}
	/**
	 * Test the Encryption and Decryption of a complete member creation (CreateEObject,CreateRootEObject,CreateEAttribute).
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws ClassNotFoundException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchPaddingException
	 * @throws IOException
	 * @throws InvalidAlgorithmParameterException
	 */
	@Test
	public void testSaveAndLoadMemberCreation() throws NoSuchAlgorithmException, InvalidKeyException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {		
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createCreateMemberChangeSequence(changes, set,1);
		    
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		    
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	}
	/**
	 * Test the Encryption and Decryption of the DeleteEObject change; 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test 
	public void testSaveAndLoadcreateDeleteEObjectChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {		
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createDeleteEObjectChange(changes, set);
		    
		     
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		   
		     
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	}
	/**
	 * Test the Encryption and Decryption of the RemoveEAttributeValue change;
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testSaveAndLoadcreateRemoveAttributeChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {		
			List<EChange> changes = new ArrayList<>();
		
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createRemoveAttributeChange(changes, set);
		    
		     
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		    
		     
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));   
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	}
	/**
	 * Test the Encryption and Decryption of the DeleteRootEObject change;
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testSaveAndLoadDeleteRootEObjectChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createDeleteRootEObjectChange(changes, set);
		    
		     
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		   
		     
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	}
	/**
	 * Test the Encryption and Decryption of the InsertEAttributeValue change;
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testSaveAndLoadInsertEAttributeValueChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createInsertEAttributeValueChange(changes, set);
		    
		     
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		   
		     
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	}
	/**
	 * 
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@Test
	public void testSaveAndLoadcreateInsertReferenceChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {

			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createInsertReferenceChange(changes, set);
		    
		     
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		    
		     
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));   
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	}
	

	
	
	
	
	@Test
	public void testSaveAndLoadCreate10Members() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {		
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createCreateMemberChangeSequence(changes, set,10);
		    
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		   
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));   
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	}
	@Test
	public void testSaveAndLoadCreate100Members() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createCreateMemberChangeSequence(changes, set,100);
		    
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		   
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));  
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	}
	@Test
	public void testSaveAndLoadCreate1000Members() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createCreateMemberChangeSequence(changes, set,1000);
		    
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		  
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();
	
	  		long totalTime = endTime - startTime;
	
	  		times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));   
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		logger.severe(result);
	}
	@Test
	public void testSaveAndLoadCreate10000Members() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<String> times = new ArrayList<>();
		for (Map map : encryptionUtil.getAllEncryptionMapsSymmetric()) {
			
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createCreateMemberChangeSequence(changes, set,10000);
		    
			long startTime = System.currentTimeMillis();
		    encryptionScheme.encryptDeltaChangesTogether(map, changes, fileWithEncryptedChanges);
		    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChangesTogether(map, fileWithEncryptedChanges);
		   
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		    long endTime = System.currentTimeMillis();

			long totalTime = endTime - startTime;

			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));   
		}
		 String result = times.stream()
	                .reduce((a, b) -> a + ", " + b)
	                .orElse("");
		logger.severe(result);
	}
	
	
	//HELPER
	
	
	
	
	
	
	



}