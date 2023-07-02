package tools.vitruv.change.encryption.tests.asymmetric;

import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
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
import tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.impl.InsertRootEObjectImpl;
import tools.vitruv.change.encryption.tests.TestChangeEncryption;

public class TestEncryptChangesAsymmetricallyAlone extends TestChangeEncryption{
	

	
	@AfterAll
	public static void deleteCreatedFiles() {
		TestChangeEncryption.deleteFiles();
	}
	private boolean checkCorrectness(EChange change) throws InvalidKeyException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, SignatureException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		try {
			for (Map map: TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsAsymmetric()) {
				
				TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.encryptDeltaChangeAloneAsymmetrically(map, change, TestChangeEncryption.FILE);
				EChange decryptedChange = TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.decryptDeltaChangeAloneAsymmetrically(map, TestChangeEncryption.FILE);
				//assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange));
			}
		}catch (Exception e) {
			TestChangeEncryption.LOGGER.severe(e.getMessage());
			return false;
		}
		return true;
	}
		
	private void collectData(EChange change) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, SignatureException {
		Map<String,Pair<String,long[]>> mainMap = new HashMap<String,Pair<String,long[]>>();
		long[][] timeArray = new long[10][3];
		int[] amounts = {1,10,100,1000,10000};
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsAsymmetric()) {
			for (int x=0;x<amounts.length;x++) {
				for (int i=0;i<10;i++) {
					File[] files = TestChangeEncryption.generateEncryptionFiles(amounts[x]);
					
					CountDownLatch encryptionLatch = new CountDownLatch(files.length);
					CountDownLatch decryptionLatch = new CountDownLatch(files.length);

					long startTime = System.currentTimeMillis();

					for (int j = 0; j < files.length; j++) {
					    try {
					        TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.encryptDeltaChangeAloneAsymmetrically(map, change, files[j]);
					    } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
					             | NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
					        e.printStackTrace();
					    } finally {
					        encryptionLatch.countDown();
					    }
					}

					try {
					    encryptionLatch.await(); // Wait until all encryption operations complete
					} catch (InterruptedException e) {
					    e.printStackTrace();
					}

					long betweenTime = System.currentTimeMillis();

					for (int j = 0; j < files.length; j++) {
					    try {
					        EChange decryptedChange = TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.decryptDeltaChangeAloneAsymmetrically(map, files[j]);
					        // Remove assertion on the final run.
					    } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
					             | NoSuchAlgorithmException | NoSuchPaddingException | IOException e) {
					        e.printStackTrace();
					    } finally {
					        decryptionLatch.countDown();
					    }
					}

					try {
					    decryptionLatch.await(); // Wait until all decryption operations complete
					} catch (InterruptedException e) {
					    e.printStackTrace();
					}

					long endTime = System.currentTimeMillis();

					long totalTime = endTime - startTime;
					long decryptionTime = endTime - betweenTime;
					long encryptionTime = betweenTime - startTime;


					timeArray[i]= new long[] {encryptionTime,decryptionTime,totalTime};
					IntStream.range(0,files.length).forEach(l -> 
					{BufferedWriter writer;
					try {
						writer = Files.newBufferedWriter(Paths.get(files[l].getAbsolutePath()));
						writer.write("");
						writer.flush();	
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					});
				//	assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
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
		TestChangeEncryption.WRITER.writeToCsv(change.getClass().getSimpleName()+amounts[x],mainMap,TestChangeEncryption.ASYM_ENCRYPTIONSCHEME.getCSVFileNameAlone());
		TestChangeEncryption.LOGGER.info("run complete");
		}
	}
}
	@Test
	public void testCreateEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getCreateEObjectChange();
		
		try {
			assertTrue(this.checkCorrectness(change));
			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
		
		// implement this later
		//this.checkAsymmetricFunctionality();
		
		
	}
	@Test 
	public void testCreateRootEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getInsertRootEObjectChange();
		try {
			assertTrue(this.checkCorrectness(change));
			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
		
		
	}
	@Test
	public void testReplaceSingleValuedEAttributeChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getReplaceSingleValuedEAttributeChange();
		try {
			assertTrue(this.checkCorrectness(change));
			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
		
		
	}
	
	@Test 
	public void testEObjecteDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getDeleteEObjectChange();
		try {
			assertTrue(this.checkCorrectness(change));
			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
		
		
	}
	@Test
	public void testRootEObjectDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException, SignatureException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getRemoveRootEObjectChange();
		try {
			assertTrue(this.checkCorrectness(change));
			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
		
		
	}
	@Test
	public void testInsertEAttributeValueChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getInsertEAttributeValue();
	    try {
			assertTrue(this.checkCorrectness(change));
			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
	}
		
		
		
	
	@Test
	public void testInsertEReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getInsertEReferenceChange();
		try {
			assertTrue(this.checkCorrectness(change));
			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
		
		
	}
	@Test
	public void testcreateRemoveReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getRemoveEReferenceChange();
		try {
			assertTrue(this.checkCorrectness(change));
			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
	}
		
		
	@Test
	public void testcreateReplaceSingleReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		EChange change = TestChangeEncryption.CREATIONUTIL.getReplaceSingleValuedReferenceChange();
		try {
			assertTrue(this.checkCorrectness(change));
			this.collectData(change);
		}catch(Exception e) {
			TestChangeEncryption.LOGGER.severe(e+":\t"+e.getMessage());
			assert false;
		}
		
		
	}
	
	
}
