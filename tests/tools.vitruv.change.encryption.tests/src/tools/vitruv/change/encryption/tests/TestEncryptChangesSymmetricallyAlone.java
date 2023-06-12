package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;
import edu.kit.ipd.sdq.commons.util.java.Pair;
import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.Family;
import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.atomic.eobject.impl.DeleteEObjectImpl;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.impl.InsertEAttributeValueImpl;
import tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.impl.InsertRootEObjectImpl;
import tools.vitruv.change.atomic.root.impl.RemoveRootEObjectImpl;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.encryption.impl.EncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
/**
 * In this test class the symmetric encryption of each Atomic EChange is tested.
 * @author Edgar Hipp
 *
 */
public class TestEncryptChangesSymmetricallyAlone {
	private static final Logger logger = Logger.getLogger(TestEncryptChangesSymmetricallyTogether.class.getName());
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private EncryptionSchemeImpl encryptionScheme = new EncryptionSchemeImpl();
	private final EChangeCreationUtility creationUtil= EChangeCreationUtility.getInstance();
	private URI MEMBER_URI = URI.createFileURI(new File("").getAbsolutePath() + "/member.ecore");

	
	@Test
	public void testCreateEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = getEncryptionDetailsMap();
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		CreateEObjectImpl<Member> decryptedChange =  (CreateEObjectImpl<Member>) encryptionScheme.decryptDeltaChangeAlone(map, null);
		
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(null);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));   
	}
	@Test 
	public void testCreateRootEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = getEncryptionDetailsMap();
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set,1);
		InsertRootEObjectImpl<Member> change = (InsertRootEObjectImpl<Member>) changes.get(1);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		InsertRootEObjectImpl<Member> decryptedChange =  (InsertRootEObjectImpl<Member>) encryptionScheme.decryptDeltaChangeAlone(map, null);
		
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(null);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange));
	}
	@Test
	public void testReplaceSingleValuedEAttributeChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = getEncryptionDetailsMap();
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set,1);
		
		ReplaceSingleValuedEAttributeImpl<Member,String> change = (ReplaceSingleValuedEAttributeImpl<Member,String>) changes.get(2);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		ReplaceSingleValuedEAttributeImpl<Member,String> decryptedChange =  (ReplaceSingleValuedEAttributeImpl<Member, String>) encryptionScheme.decryptDeltaChangeAlone(map, null);
		
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(null);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	
	@Test 
	public void testEObjecteDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = getEncryptionDetailsMap();
		Member member = this.createMember();
		DeleteEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createDeleteEObjectChange(member);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		DeleteEObject<Member> decryptedChange = (DeleteEObject<Member>) encryptionScheme.decryptDeltaChangeAlone(map, null);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(null);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	@Test
	public void testRootEObjectDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = getEncryptionDetailsMap();
		Member member = this.createMember();
		RemoveRootEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createRemoveRootChange(member,null,0);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		RemoveRootEObject<Member> decryptedChange = (RemoveRootEObject<Member>) encryptionScheme.decryptDeltaChangeAlone(map, null);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(null);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	@Test
	public void testInsertEAttributeValueChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = getEncryptionDetailsMap();
		Member member = this.createMember();
	    InsertEAttributeValue<Member, String> change = TypeInferringAtomicEChangeFactory.getInstance().createInsertAttributeChange(member, member.eClass().getEAllAttributes().get(0), 0, "test");
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		InsertEAttributeValue<Member, String>  decryptedChange = (InsertEAttributeValueImpl<Member,String>) encryptionScheme.decryptDeltaChangeAlone(map, null);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(null);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	@Test
	public void testInsertEReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = getEncryptionDetailsMap();
		Member member = creationUtil.getMember();

		Pair<Family,Member> familyAndMother = createFamily();
		InsertEReference<Family,EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
				.createInsertReferenceChange(familyAndMother.getFirst(),familyAndMother.getFirst().eClass().getEAllReferences().get(3),member,0);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		InsertEReference<Family,EObject>  decryptedChange = (InsertEReference<Family,EObject>) encryptionScheme.decryptDeltaChangeAlone(map, null);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(null);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	@Test
	public void testcreateRemoveReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = getEncryptionDetailsMap();
		Pair<Family,Member> familyAndMother = createFamily();
		RemoveEReference<EObject, EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
				.createRemoveReferenceChange(familyAndMother.getFirst(), familyAndMother.getFirst().eClass().getEAllReferences().get(3), familyAndMother.getSecond(), 0);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		RemoveEReference<EObject, EObject>  decryptedChange = (RemoveEReference<EObject, EObject>) encryptionScheme.decryptDeltaChangeAlone(map, null);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(null);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		
	}
	@Test
	public void test() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = getEncryptionDetailsMap();
		Member member = creationUtil.getMember();
		Pair<Family,Member> familyAndMother = createFamily();
		ReplaceSingleValuedEReference<Member, Member>  change = TypeInferringAtomicEChangeFactory.getInstance()
				.createReplaceSingleReferenceChange(familyAndMother.getSecond(), familyAndMother.getFirst().eClass().getEAllReferences().get(3), familyAndMother.getSecond(), member);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		RemoveEReference<EObject, EObject>  decryptedChange = (RemoveEReference<EObject, EObject>) encryptionScheme.decryptDeltaChangeAlone(map, null);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(null);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		
	}
	
	//		TypeInferringAtomicEChangeFactory.getInstance().createUnsetFeatureChange(null, null) fehlt.
		
	private Member createMember() {
		Member member = creationUtil.getMember();
		ResourceSet set = new ResourceSetImpl();
	    creationUtil.withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
	    memberResource.getContents().add(member);
	    return member;
	}
	private HashMap<String,Object> getEncryptionDetailsMap() throws NoSuchAlgorithmException{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(128);
		
		// Create map of encryptionOptions
		SecretKey secretKey = keyGenerator.generateKey();
		HashMap <String,Object> map = new HashMap<String, Object>();
		map.put("secretKey", secretKey);
		map.put("algorithm", "AES");
		return map;
	}
	private Pair<Family,Member> createFamily() {
		ResourceSet set = new ResourceSetImpl();
		Member member = creationUtil.getMember();
		Family family = FamiliesFactory.eINSTANCE.createFamily();
		family.setMother(member);
		
		creationUtil.withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
		return new Pair<>(family,member);
	}
	
	
}
