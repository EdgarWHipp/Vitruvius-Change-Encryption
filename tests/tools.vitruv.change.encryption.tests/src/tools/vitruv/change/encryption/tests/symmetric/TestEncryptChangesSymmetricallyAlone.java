package tools.vitruv.change.encryption.tests.symmetric;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import java.io.FileWriter;
import au.com.bytecode.opencsv.CSVWriter;
/**
 * In this test class the symmetric encryption of each Atomic EChange is tested.
 * @author Edgar Hipp
 *
 */
public class TestEncryptChangesSymmetricallyAlone extends TestChangeEncryption{
	private final static File csvFile = new File(new File("").getAbsolutePath() + File.separator + "mainSymmetricAlone.csv");
	private final java.net.URI fileURI = csvFile.toURI();
	
	
	public void ALL(String change,Map<String,Pair<String,Long>> map) throws IOException {
		
		Pair<String,Long> results = (Pair<String,Long>) map.get("result");
		String csvLine = new String(change+","+results.getFirst()+","+results.getSecond()+"\n");
		
		 
		 try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
	            writer.write(csvLine);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	     
	}
	
	
	
	@Test
	public void testCreateEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<String> times = new ArrayList<>();
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
			CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
			long startTime = System.currentTimeMillis();
			//
			TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map,change,TestChangeEncryption.FILE);
			
			CreateEObjectImpl<Member> decryptedChange =  (CreateEObjectImpl<Member>) TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
			//
			long endTime = System.currentTimeMillis();
			
			long totalTime = endTime - startTime;
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
			assertTrue(new EcoreUtil.EqualityHelper().equals(change, decryptedChange));   
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		
	}
	@Test 
	public void testCreateRootEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<String> times = new ArrayList<>();
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
			InsertRootEObjectImpl<Member> change = (InsertRootEObjectImpl<Member>) changes.get(1);
		    long startTime = System.currentTimeMillis();
			//
			TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map,change,TestChangeEncryption.FILE);
			InsertRootEObjectImpl<Member> decryptedChange =  (InsertRootEObjectImpl<Member>) TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
			//
			long endTime = System.currentTimeMillis();
			
			long totalTime = endTime - startTime;
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
			assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange));
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		
	}
	@Test
	public void testReplaceSingleValuedEAttributeChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<String> times = new ArrayList<>();
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
			
			ReplaceSingleValuedEAttributeImpl<Member,String> change = (ReplaceSingleValuedEAttributeImpl<Member,String>) changes.get(2);
		    long startTime = System.currentTimeMillis();
			//
			TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map,change,TestChangeEncryption.FILE);
			ReplaceSingleValuedEAttributeImpl<Member,String> decryptedChange =  (ReplaceSingleValuedEAttributeImpl<Member, String>) TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
			//
			long endTime = System.currentTimeMillis();
			
			long totalTime = endTime - startTime;
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
			assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		
	}
	
	@Test 
	public void testEObjecteDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<String> times = new ArrayList<>();
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
			Member member = (Member) memberResource.getContents().get(0);
			
			DeleteEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createDeleteEObjectChange(member);
			long startTime = System.currentTimeMillis();
			//
			TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map,change,TestChangeEncryption.FILE);
			DeleteEObject<Member> decryptedChange = (DeleteEObject<Member>) TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
			//
			long endTime = System.currentTimeMillis();
			
			long totalTime = endTime - startTime;
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
			assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		
	}
	@Test
	public void testRootEObjectDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<String> times = new ArrayList<>();

		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
			Member member = (Member) memberResource.getContents().get(0);
		
			RemoveRootEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createRemoveRootChange(member,member.eResource(),0);
			change.setResource(null);
		    long startTime = System.currentTimeMillis();
			//
			TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map,change,TestChangeEncryption.FILE);
			RemoveRootEObject<Member> decryptedChange = (RemoveRootEObject<Member>) TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
			//
			long endTime = System.currentTimeMillis();
			
			long totalTime = endTime - startTime;
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
			assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		
	}
	@Test
	public void testInsertEAttributeValueChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map<String,Pair<String,Long>> mainMap = new HashMap<String,Pair<String,Long>>();
		long[] timeArray = new long[10];
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			for (int i=0;i<10;i++) {
				Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
				Member member = (Member) memberResource.getContents().get(0);
				
	
			    InsertEAttributeValue<Member, String> change = TypeInferringAtomicEChangeFactory.getInstance().createInsertAttributeChange(member, member.eClass().getEAllAttributes().get(0), 0, "test");
			    long startTime = System.currentTimeMillis();
				//
			    TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map,change,TestChangeEncryption.FILE);
				InsertEAttributeValue<Member, String>  decryptedChange = (InsertEAttributeValueImpl<Member,String>) TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
				//
				long endTime = System.currentTimeMillis();
				
				long totalTime = endTime - startTime;
				timeArray[i]=totalTime;
				assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
			}
		
			long mean;
			long sum=0;
			for (int i=0;i<10;i++) {
				sum+=timeArray[i];
			}
		mean=sum/10;
		mainMap.put("result",new Pair((String)map.get("algorithm"),mean));
		this.ALL("InsertEAttributeValueChange",mainMap);
		}
		
		
		
	}
	@Test
	public void testInsertEReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<String> times = new ArrayList<>();
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
			Member member = (Member) memberResource.getContents().get(0);
	
			Resource family = TestChangeEncryption.CREATIONUTIL.createFamily();
			Family familyImpl = (Family) family.getContents().get(0);
			

			InsertEReference<Family,EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
					.createInsertReferenceChange(familyImpl,familyImpl.eClass().getEAllReferences().get(3),member,0);
			long startTime = System.currentTimeMillis();
			//
			TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map,change,TestChangeEncryption.FILE);
			InsertEReference<Family,EObject>  decryptedChange = (InsertEReference<Family,EObject>) TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
			//
			long endTime = System.currentTimeMillis();
			
			long totalTime = endTime - startTime;
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
			assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
		
	}
	@Test
	public void testcreateRemoveReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map<String,Pair<String,Long>> mainMap = new HashMap<String,Pair<String,Long>>();
		long[] timeArray = new long[10];
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			
			for (int i=0;i<10;i++) {
				Resource family = TestChangeEncryption.CREATIONUTIL.createFamily();
				Family familyImpl = (Family) family.getContents().get(0);
				
				Member daughter1Impl = (Member) family.getContents().get(2);
				
				RemoveEReference<EObject, EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
						.createRemoveReferenceChange(familyImpl, familyImpl.eClass().getEAllReferences().get(3), daughter1Impl, 0);
				change.setOldValue(null);
				
				
				long startTime = System.currentTimeMillis();
				//
				TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map,change,TestChangeEncryption.FILE);
				RemoveEReference<EObject, EObject>  decryptedChange = (RemoveEReference<EObject, EObject>) TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
				//
				long endTime = System.currentTimeMillis();
					
				long totalTime = endTime - startTime;
				timeArray[i]=totalTime;
			
				assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
			}
			long mean;
			long sum=0;
			for (int i=0;i<10;i++) {
				sum+=timeArray[i];
			}
			mean=sum/10;
			mainMap.put("result", new Pair((String)map.get("algorithm"),mean));
			this.ALL("RemoveEReferenceChange",mainMap);
		}
		
		
		
	
		
	}
	@Test
	public void testcreateReplaceSingleReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<String> times = new ArrayList<>();
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
			Member member = (Member) memberResource.getContents().get(0);
			
			Resource family = TestChangeEncryption.CREATIONUTIL.createFamily();
			Family familyImpl = (Family) family.getContents().get(0);
			Member motherImpl = (Member) family.getContents().get(1);
			EReference mothersreference = familyImpl.eClass().getEAllReferences().get(3);
			ReplaceSingleValuedEReference<Family, Member>  change = TypeInferringAtomicEChangeFactory.getInstance()
					.createReplaceSingleReferenceChange(familyImpl, mothersreference, motherImpl, member);
			long startTime = System.currentTimeMillis();
			//
			TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map,change,TestChangeEncryption.FILE);
			ReplaceSingleValuedEReference<Family, Member>  decryptedChange = (ReplaceSingleValuedEReference<Family, Member>) TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
			//
			long endTime = System.currentTimeMillis();
			
			long totalTime = endTime - startTime;
			times.add("Time spent with "+map.get("algorithm")+":"+totalTime+"ms");
			
			assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		}
		String result = times.stream()
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
	
		
		
	}
	
	//		TypeInferringAtomicEChangeFactory.getInstance().createUnsetFeatureChange(null, null) fehlt.
		
	
	
	
	
	
}
