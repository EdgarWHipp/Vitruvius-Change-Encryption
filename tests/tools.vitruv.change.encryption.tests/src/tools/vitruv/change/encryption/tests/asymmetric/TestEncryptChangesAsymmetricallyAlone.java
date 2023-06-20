package tools.vitruv.change.encryption.tests.asymmetric;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	private final static File csvFile = new File(new File("").getAbsolutePath() + File.separator + "mainAsymmetricAlone.csv");

private void writeToCsv(String change,Map<String,Pair<String,long[]>> map) throws IOException {
		
		Pair<String,long[]> results = (Pair<String,long[]>) map.get("result");
		String csvLine = new String(change+","+results.getFirst()+","+results.getSecond()[0]+","+results.getSecond()[1]+","+results.getSecond()[2]+"\n");
		
		 
		 try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
	            writer.write(csvLine);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	     
	}
	
	@Test
	public void testCreateEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		
		try {
			this.testChangeAlone(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		// implement this later
		//this.checkAsymmetricFunctionality();
		
		
	}
	@Test 
	public void testCreateRootEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		InsertRootEObjectImpl<Member> change = (InsertRootEObjectImpl<Member>) changes.get(1);
		List<String> times = new ArrayList<>();
		try {
			this.testChangeAlone(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
	}
	@Test
	public void testReplaceSingleValuedEAttributeChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		
		ReplaceSingleValuedEAttributeImpl<Member,String> change = (ReplaceSingleValuedEAttributeImpl<Member,String>) changes.get(2);
		try {
			this.testChangeAlone(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
	}
	
	@Test 
	public void testEObjecteDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
		
		DeleteEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createDeleteEObjectChange(member);
		try {
			this.testChangeAlone(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
		
	}
	@Test
	public void testRootEObjectDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
	
		RemoveRootEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createRemoveRootChange(member,member.eResource(),0);
		change.setResource(null);
		try {
			this.testChangeAlone(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
		
	}
	@Test
	public void testInsertEAttributeValueChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
		

	    InsertEAttributeValue<Member, String> change = TypeInferringAtomicEChangeFactory.getInstance().createInsertAttributeChange(member, member.eClass().getEAllAttributes().get(0), 0, "test");
	    try {
			this.testChangeAlone(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
	}
		
		
		
	
	@Test
	public void testInsertEReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);

		Resource family = TestChangeEncryption.CREATIONUTIL.createFamily();
		Family familyImpl = (Family) family.getContents().get(0);
		

		InsertEReference<Family,EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
				.createInsertReferenceChange(familyImpl,familyImpl.eClass().getEAllReferences().get(3),member,0);
		try {
			this.testChangeAlone(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
		
	}
	@Test
	public void testcreateRemoveReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Resource family = TestChangeEncryption.CREATIONUTIL.createFamily();
		Family familyImpl = (Family) family.getContents().get(0);
		
		Member daughter1Impl = (Member) family.getContents().get(2);
		
		RemoveEReference<EObject, EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
				.createRemoveReferenceChange(familyImpl, familyImpl.eClass().getEAllReferences().get(1), daughter1Impl, 0);
		try {
			this.testChangeAlone(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;

		}
		
		
	@Test
	public void testcreateReplaceSingleReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
		
		Resource family = TestChangeEncryption.CREATIONUTIL.createFamily();
		Family familyImpl = (Family) family.getContents().get(0);
		Member motherImpl = (Member) family.getContents().get(1);
		EReference mothersreference = familyImpl.eClass().getEAllReferences().get(3);
		ReplaceSingleValuedEReference<Family, Member>  change = TypeInferringAtomicEChangeFactory.getInstance()
				.createReplaceSingleReferenceChange(familyImpl, mothersreference, motherImpl, member);
		change.setAffectedEObject(null);
		try {
			this.testChangeAlone(change);
		}catch(Exception e) {
			System.out.println(e+":\t"+e.getMessage());
			assert false;
		}
		assert true;
		
		
	}
	
	
}
