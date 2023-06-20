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
import tools.vitruv.change.encryption.TestChangeEncryption.ENCRYPTIONSCHEME;
import tools.vitruv.change.encryption.impl.TestChangeEncryption.ENCRYPTIONSCHEMEImpl;
import tools.vitruv.change.encryption.tests.TestChangeEncryption;
import tools.vitruv.change.encryption.tests.symmetric.Pair;
import tools.vitruv.change.encryption.tests.util.EChangeTestChangeEncryption.CREATIONUTILity;
import tools.vitruv.change.encryption.tests.util.TestChangeEncryption.ENCRYPTIONUTILity;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;



/*
 * Defines a set of Unit Tests for the symmetric encryption of model deltas. All the changes in this test class are applied to a Member object
 *  and uses the new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges() function to create ONLY valid changes. One key 
 *  and one algorithm is used for the entire encryption and decryption process.
 */


public class TestEncryptChangesAsymmetricallyTogether extends TestChangeEncryption{
	private static final Logger logger = Logger.getLogger(TestEncryptChangesAsymmetricallyTogether.class.getName());
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private final static File csvFile = new File(new File("").getAbsolutePath() + File.separator + "AsymmetricEncryptionTogether.csv");
	
	
	
	private void testChangesTogether(List<EChange> changes, String csvFile) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Map<String,Pair<String,long[]>> mainMap = new HashMap<String,Pair<String,long[]>>();
		long[][] timeArray = new long[10][3];
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			for (int i=0;i<10;i++) {
				

			    long startTime = System.currentTimeMillis();
				//
			    TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangesTogether(map,changes,TestChangeEncryption.FILE);
			    long betweenTime = System.currentTimeMillis();
				List<EChange> decryptedChanges = TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangesTogether(map, TestChangeEncryption.FILE);
				//
				long endTime = System.currentTimeMillis();
				
				long totalTime = endTime - startTime;
				long decryptionTime = endTime - betweenTime;
				long encryptionTime = betweenTime - startTime;
				timeArray[i]= new long[] {encryptionTime,decryptionTime,totalTime};
				assertTrue(new EcoreUtil.EqualityHelper().equals(changes,decryptedChanges)); 
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
		TestChangeEncryption.WRITER.writeToCsvconcatenatedClassNames,mainMap, csvFile);
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
			TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
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
				TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
			}catch(Exception e) {
				System.out.println(e+":\t"+e.getMessage());
				assert false;
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
			TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
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
			TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
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
				TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
			}catch(Exception e) {
				System.out.println(e+":\t"+e.getMessage());
				assert false;
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
			TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
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
			TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
	}
	

	
	
	
	
	@Test
	public void testSaveAndLoadCreate10Members() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
			
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,10);
	    
		try {
			TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
	}
	@Test
	public void testSaveAndLoadCreate100Members() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,100);
	    
		try {
			TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
	}
	@Test
	public void testSaveAndLoadCreate1000Members() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1000);
	    
		try {
			TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
	}
	@Test
	public void testSaveAndLoadCreate10000Members() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		
			
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,10000);
	    
		try {
			TestChangeEncryption.WRITER.testChangesTogether(changes,csvFile);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
	}
	
	
	
	
	
	
	
	
	
	



}