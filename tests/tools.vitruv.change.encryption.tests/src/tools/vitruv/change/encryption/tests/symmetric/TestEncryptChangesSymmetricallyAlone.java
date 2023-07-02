package tools.vitruv.change.encryption.tests.symmetric;

import static org.junit.jupiter.api.Assertions.assertTrue;






import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.commons.util.java.Pair;
import edu.kit.ipd.sdq.metamodels.families.Family;
import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.impl.InsertEAttributeValueImpl;
import tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.impl.InsertRootEObjectImpl;
import tools.vitruv.change.encryption.tests.TestChangeEncryption;
/**
 * In this test class the symmetric encryption of each Atomic EChange is tested.
 * @author Edgar Hipp
 *
 */
public class TestEncryptChangesSymmetricallyAlone extends TestChangeEncryption{
	private final String csvFileName = new File("").getAbsolutePath() + File.separator + "SymmetricEncryptionAlone.csv";
	
	
	private void testChangeAloneWithAssertion(EChange change) throws InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, SignatureException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		/*
		for (Map map: TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			TestChangeEncryption.SYM_ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map, change, TestChangeEncryption.FILE);
			EChange decryptedChange = TestChangeEncryption.SYM_ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
			assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange));
		}
		*/
	}
	
	@AfterAll 
	public static void deletecreatedFiles() {
		TestChangeEncryption.deleteFiles();
	}
	
	private void collectData(EChange change) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Map<String,Pair<String,long[]>> mainMap = new HashMap<String,Pair<String,long[]>>();
		long[][] timeArray = new long[10][3];
		int[] amounts = {1,10,100,1000,10000};
			for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
				for (int x=0;x<amounts.length;x++) {
					
					File[] files = TestChangeEncryption.generateEncryptionFiles(amounts[x]);

					for (int i=0;i<10;i++) {
						
					    long startTime = System.currentTimeMillis();
						//
					   
						for (int iter = 0;iter<files.length;iter++) {
							TestChangeEncryption.SYM_ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map, change, files[iter]);
							

						}
								
							
					   
					    long betweenTime = System.currentTimeMillis();
					   
						for (int iterdecr = 0 ;iterdecr<files.length;iterdecr++) {
							EChange decryptedChange = TestChangeEncryption.SYM_ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, files[iterdecr]);
							//assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
						}
						
						
						
						long endTime = System.currentTimeMillis();
						
						long totalTime = endTime - startTime;
						long decryptionTime = endTime - betweenTime;
						long encryptionTime = betweenTime - startTime;
						timeArray[i]= new long[] {encryptionTime,decryptionTime,totalTime};
						//clear the files.
						IntStream.range(0,files.length).forEach(l -> files[l].delete());
					
					    	
						
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
			TestChangeEncryption.WRITER.writeToCsv(change.getClass().getSimpleName()+amounts[x],mainMap,TestChangeEncryption.SYM_ENCRYPTIONSCHEME.getCSVFileNameAlone());
			TestChangeEncryption.LOGGER.info("run complete");
			}
		}
	}
	
	
	@Test
	public void testCreateEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getCreateEObjectChange();
		try {
			this.testChangeAloneWithAssertion(change);
			this.collectData(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
		
	}
	@Test 
	public void testCreateRootEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getInsertRootEObjectChange();
		try {
			this.testChangeAloneWithAssertion(change);

			this.collectData(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
	}
	@Test
	public void testReplaceSingleValuedEAttributeChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getReplaceSingleValuedEAttributeChange();
		try {
			this.testChangeAloneWithAssertion(change);

			this.collectData(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
	}
	
	@Test 
	public void testEObjecteDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getDeleteEObjectChange();
		try {
			this.testChangeAloneWithAssertion(change);

			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
		
	}
	@Test
	public void testRootEObjectDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getRemoveRootEObjectChange();
		try {
			this.testChangeAloneWithAssertion(change);

			this.collectData(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
		
	}
	@Test
	public void testInsertEAttributeValueChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getInsertEAttributeValue();
	    try {
			this.testChangeAloneWithAssertion(change);

			this.collectData(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
	}
		
	
	@Test
	public void testInsertEReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getInsertEReferenceChange();
		try {
			this.testChangeAloneWithAssertion(change);

			this.collectData(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
	}
	@Test
	public void testcreateRemoveReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getRemoveEReferenceChange();
		
		try {
			this.testChangeAloneWithAssertion(change);

			this.collectData(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;

		}
		
		
	@Test
	public void testcreateReplaceSingleReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getReplaceSingleValuedReferenceChange();
		
		try {
			this.testChangeAloneWithAssertion(change);

			this.collectData(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
		
	}
	
	

}
