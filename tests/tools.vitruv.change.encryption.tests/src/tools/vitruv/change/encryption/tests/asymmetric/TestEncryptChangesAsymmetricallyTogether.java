package tools.vitruv.change.encryption.tests.asymmetric;

import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.EqualityHelper;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.commons.util.java.Pair;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.changederivation.DefaultStateBasedChangeResolutionStrategy;

import tools.vitruv.change.encryption.tests.TestChangeEncryption;

import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;



/*
 * Defines a set of Unit Tests for the symmetric encryption of model deltas. All the changes in this test class are applied to a Member object
 *  and uses the new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges() function to create ONLY valid changes. One key 
 *  and one algorithm is used for the entire encryption and decryption process.
 */


public class TestEncryptChangesAsymmetricallyTogether extends TestChangeEncryption{
	private static final Logger logger = Logger.getLogger(TestEncryptChangesAsymmetricallyTogether.class.getName());
	
	private void checkCorrectness(List<EChange> changes) throws InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, SignatureException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, IOException {
		for (Map map: TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsAsymmetric()) {
			TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.encryptDeltaChangeTogetherAsymmetrically(map, changes, TestChangeEncryption.FILE);
			List<EChange> decryptedChanges = TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.decryptDeltaChangeTogetherAsymmetrically(map, TestChangeEncryption.FILE);
			EqualityHelper helper = new EcoreUtil.EqualityHelper();
			IntStream.range(0, changes.size()).forEach(x -> assertTrue(helper.equals(changes.get(x),decryptedChanges.get(x))));
			
			
		}
	}
	private void testChangesTogether(List<EChange> changes) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException {
		Map<String,Pair<String,long[]>> mainMap = new HashMap<String,Pair<String,long[]>>();
		long[][] timeArray = new long[10][3];
		int[] amounts = {1,10,100,1000,10000};

		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsAsymmetric()) {
			for (int x =0;x<amounts.length ;x++){
				for (int i=0;i<10;i++) {
					File[] files =TestChangeEncryption.generateEncryptionFiles(amounts[x]);
					
	
				    long startTime = System.currentTimeMillis();
					//
				    IntStream.range(0,amounts[x])
				    .forEach(j -> {
						try {
							TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.encryptDeltaChangeTogetherAsymmetrically(map,changes,files[j]);
						} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
								| NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
				    long betweenTime = System.currentTimeMillis();
				    IntStream.range(0,amounts[x])
				    .forEach(j->{
						try {
							TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.decryptDeltaChangeTogetherAsymmetrically(map, files[j]);
						} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
								| NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					//
					long endTime = System.currentTimeMillis();
					
					long totalTime = endTime - startTime;
					long decryptionTime = endTime - betweenTime;
					long encryptionTime = betweenTime - startTime;
					timeArray[i]= new long[] {encryptionTime,decryptionTime,totalTime};
				}
		
			long[] mean=new long[3];
			long sum=0;
			for (int j = 0;j<3;j++) {
				for (int i=0;i<10;i++) {
					sum+=timeArray[i][j];
				}
				mean[j]=sum/3;
			}
		mainMap.put("result",new Pair<String, long[]>((String)map.get("algorithm"),mean));
		String concatenatedClassNames = changes.stream()
		        .map(change -> change.getClass().getSimpleName())
		        .collect(Collectors.joining());
		TestChangeEncryption.WRITER.writeToCsv(concatenatedClassNames,mainMap, TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.getCSVFileNameTogether());
		logger.info("run complete");
			}
		}
	}
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
		
		

		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createReplaceSingleAttributeChange(changes, set);

		try {
			this.checkCorrectness(changes);
			this.testChangesTogether(changes);
		}catch(Exception e) {
			logger.info(e+":\t"+e.getMessage());			assert false;
		}
		assert true;

		
	    
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
			
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
			try {
				this.checkCorrectness(changes);
				this.testChangesTogether(changes);
			}catch(Exception e) {
				logger.info(e+":\t"+e.getMessage());				assert false;
			}
			assert true;
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
		
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createDeleteEObjectChange(changes, set);
	    
	     
		try {
			this.checkCorrectness(changes);
			this.testChangesTogether(changes);
		}catch(Exception e) {
			logger.info(e+":\t"+e.getMessage());			assert false;
		}
		assert true;
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
		
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createRemoveAttributeChange(changes, set);	    
	     
	    try {
	    	this.checkCorrectness(changes);
	    	this.testChangesTogether(changes);
		}catch(Exception e) {
			logger.info(e+":\t"+e.getMessage());			assert false;
		}
		assert true;
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
		
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			TestChangeEncryption.CREATIONUTIL.createDeleteRootEObjectChange(changes, set);
			
			try {
				this.checkCorrectness(changes);
				this.testChangesTogether(changes);
			}catch(Exception e) {
				logger.info(e+":\t"+e.getMessage());				assert false;
			}
			assert true;
		     
		
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
		
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createInsertEAttributeValueChange(changes, set);
		
		try {
			this.checkCorrectness(changes);
			this.testChangesTogether(changes);
		}catch(Exception e) {
			logger.info(e+":\t"+e.getMessage());			assert false;
		}
		assert true;
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
		
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createInsertReferenceChange(changes, set);
	   
		try {
			this.checkCorrectness(changes);
			this.testChangesTogether(changes);
		}catch(Exception e) {
			logger.info(e+":\t"+e.getMessage());			assert false;
		}
		assert true;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	



}