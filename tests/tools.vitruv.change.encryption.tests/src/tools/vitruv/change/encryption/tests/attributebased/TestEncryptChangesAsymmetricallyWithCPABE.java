package tools.vitruv.change.encryption.tests.attributebased;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.commons.util.java.Pair;
import edu.kit.ipd.sdq.metamodels.families.Member;
import junwei.cpabe.junwei.cpabe.Cpabe;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.encryption.impl.attributebased.CpabeAdapterImpl;
import tools.vitruv.change.encryption.tests.TestChangeEncryption;

public class TestEncryptChangesAsymmetricallyWithCPABE extends TestChangeEncryption{
	
	
	@AfterAll
	public static void deleteAllFiles() {
		TestChangeEncryption.deleteFiles();
	}
	
	private boolean checkCorrectness(EChange change,String attributes) throws Exception {
		String passingUserAttributes = this.getPassingUserAttributes();
		String policy = getPolicy();

		Cpabe test = new Cpabe();
		//init adapter
		CpabeAdapterImpl adapter = new CpabeAdapterImpl(test,privateKeyPath,publicKeyPath,masterKeyPath,decryptedFilePath,encryptedFilePath,inputFile);
		
		//encrypt
		adapter.encryptAloneAndGenerateKeys(attributes, policy,change);
		
		//decryption should fail here.
		try {
			EChange decryptedChange = adapter.decryptAlone();
			
			assertNotEquals(decryptedChange,null);
			assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		} catch (Exception e) {
			
			if (e.getMessage().equals("decryption has failed")) { return false;}
		}
		return true;
	}
	private void collectData(EChange change) throws Exception {
		Cpabe cpabeInstance = new Cpabe();
		String policy = this.getPolicy();
		String passingUserAttributes = this.getPassingUserAttributes();
		Map<String,Pair<String,long[]>> mainMap = new HashMap<String,Pair<String,long[]>>();
		long[][] timeArray = new long[10][3];
		int[] amounts = {1,10,100,1000,10000};
			for (int x=0;x<amounts.length;x++) {
				for (int i=0;i<10;i++) {
					File[] decryptionFiles = TestChangeEncryption.generateDecryptionFiles(amounts[x]);
					File[] encryptionFiles = TestChangeEncryption.generateEncryptionFiles(amounts[x]);
				    
					CpabeAdapterImpl[] adapters = IntStream.range(0,decryptionFiles.length)
							.mapToObj(l -> 
							new CpabeAdapterImpl
							(cpabeInstance,privateKeyPath,publicKeyPath,masterKeyPath,decryptionFiles[l].getAbsolutePath(),encryptionFiles[l].getAbsolutePath(),inputFile))
							.toArray(CpabeAdapterImpl[]::new);
				    
				    long startTime = System.currentTimeMillis();
					IntStream.range(0,decryptionFiles.length).forEach(j -> {
						try {
							adapters[j].encryptAloneAndGenerateKeys(passingUserAttributes, policy, change);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					
					long betweenTime = System.currentTimeMillis();
					
					IntStream.range(0,decryptionFiles.length).forEach(k -> {
						try {
							adapters[k].decryptAlone();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					
					long endTime = System.currentTimeMillis();
					
					long totalTime = endTime - startTime;
					long decryptionTime = endTime - betweenTime;
					long encryptionTime = betweenTime - startTime;
					timeArray[i]= new long[] {encryptionTime,decryptionTime,totalTime};
					IntStream.range(0,decryptionFiles.length).forEach(l -> 
					{BufferedWriter writer,writerEnc;
					try {
						writer = Files.newBufferedWriter(Paths.get(decryptionFiles[l].getAbsolutePath()));
						writerEnc = Files.newBufferedWriter(Paths.get(decryptionFiles[l].getAbsolutePath()));
						writerEnc.write("");
						writerEnc.flush();
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
			mainMap.put("result",new Pair<String, long[]>("CPABE"+amounts[x],mean));

		TestChangeEncryption.WRITER.writeToCsv(policy, mainMap, passingUserAttributes);
	}
}
	/**
	 * Encrypts a set of valid changes and opens the decryptedChanges in read-only mode.
	 */
	@Test
	public void testFailingFileAccessWithCreateEObjectChange() throws Exception {
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
		EChange change = TypeInferringAtomicEChangeFactory.getInstance().createCreateEObjectChange(member);

		
		
		assertFalse(this.checkCorrectness(change, this.getFailingUserAttributes()));
		
		
	}
	/**
	 * Encrypts a set of valid changes and opens the decryptedChanges in read/write mode.
	 * @throws Exception
	 */
	@Test
	public void testPassingFileAccessWithCreateEObjectChange() throws Exception {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		

		
		
		assertTrue(this.checkCorrectness(change,this.getPassingUserAttributes()));
		//this.collectData(change);
		
		
		
	}
	@Test 
	public void testPassingFileAccessWithReplaceSingleReferenceChange() {
		
	}
	@Test
	public void testPassingFileAccessWithRemoveReferenceChange() {}
	@Test
	public void testPassingFileAccessWithInsertEReferenceChange() {}
	@Test
	public void testPasingFileAccessWithInsertEAttributeValueChange() {}
	@Test
	public void testPassingFileAccessWithRootEObjectDeletedChange() {}
	@Test 
	public void testPassingFileAccessWithEObjecteDeletedChange() {}
	
	
	
	
	
	// These tests should be extended to a wider range of policies and attributes to test the scalability.
	private String getPassingUserAttributes() {
		String passing = 
				"priority:8 name:LaraWeide title:seniorDeveloper";
		return passing;
	}
	private String getFailingUserAttributes() {
		String failing = 
				"priority:4 name:MaxMustermann title:workingStudent";
		return failing;
	}
	private String getPolicy() {
		String policy = 
				"priority:8 title:seniorDeveloper 1of2";
		return policy;
	}
}
