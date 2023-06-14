package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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
import tools.vitruv.change.atomic.feature.attribute.impl.InsertEAttributeValueImpl;
import tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.impl.InsertRootEObjectImpl;
import tools.vitruv.change.changederivation.DefaultStateBasedChangeResolutionStrategy;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.encryption.impl.EncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;

public class TestEncryptChangesSymmetricallyAloneAndResolveAndApply {
	private static final Logger logger = Logger.getLogger(TestEncryptChangesSymmetricallyAlone.class.getName());
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private EncryptionSchemeImpl encryptionScheme = new EncryptionSchemeImpl();
	private final EChangeCreationUtility creationUtil= EChangeCreationUtility.getInstance();
	private final EncryptionUtility encryptionUtil = EncryptionUtility.getInstance();
	@Test
	public void testCreateEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		
		CreateEObjectImpl<Member> decryptedChange =  (CreateEObjectImpl<Member>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		ArrayList<EChange> changeInList = new ArrayList<EChange>();
		changeInList.add(decryptedChange);
		changeInList.add(changes.get(1));
		changeInList.add(changes.get(2));
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changeInList);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change, decryptedChange));   
	}
	@Test 
	public void testCreateRootEObjectChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set,1);
		InsertRootEObjectImpl<Member> change = (InsertRootEObjectImpl<Member>) changes.get(1);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		InsertRootEObjectImpl<Member> decryptedChange =  (InsertRootEObjectImpl<Member>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		ArrayList<EChange> changeInList = new ArrayList<EChange>();

		
		changeInList.add(changes.get(0));
		changeInList.add(changes.get(2));
		changeInList.add(decryptedChange);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changeInList);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange));
	}
	@Test
	public void testReplaceSingleValuedEAttributeChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		creationUtil.createCreateMemberChangeSequence(changes, set,1);
		
		ReplaceSingleValuedEAttributeImpl<Member,String> change = (ReplaceSingleValuedEAttributeImpl<Member,String>) changes.get(2);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		ReplaceSingleValuedEAttributeImpl<Member,String> decryptedChange =  (ReplaceSingleValuedEAttributeImpl<Member, String>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		ArrayList<EChange> changeInList = new ArrayList<EChange>();

		changeInList.add(changes.get(0));
		changeInList.add(changes.get(1));
		changeInList.add(decryptedChange);

		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changeInList);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	
	@Test 
	public void testEObjecteDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		List<Member> members = creationUtil.createCreateMemberChangeSequence(changes, set,1);
		DeleteEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createDeleteEObjectChange(members.get(0));
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		DeleteEObject<Member> decryptedChange = (DeleteEObject<Member>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		ArrayList<EChange> changeInList = new ArrayList<EChange>();

		
		changeInList.add(changes.get(0));
		changeInList.add(changes.get(0));

		changeInList.add(changes.get(1));
		changeInList.add(changes.get(2));
		changeInList.add(decryptedChange);

		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changeInList);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	@Test
	public void testRootEObjectDeletedChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		List<Member> members = creationUtil.createCreateMemberChangeSequence(changes, set,1);
		RemoveRootEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createRemoveRootChange(members.get(0),members.get(0).eResource(),0);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		RemoveRootEObject<Member> decryptedChange = (RemoveRootEObject<Member>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		ArrayList<EChange> changeInList = new ArrayList<EChange>();
		DeleteEObject<Member> deleteMember= TypeInferringAtomicEChangeFactory.getInstance().createDeleteEObjectChange(members.get(0));
		changeInList.add(decryptedChange);
		changeInList.add(changes.get(0));
		changeInList.add(changes.get(1));
		changeInList.add(changes.get(2));
		// delete Object to make a valid change.
		changeInList.add(deleteMember);

		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changeInList);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	@Test
	public void testInsertEAttributeValueChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		Resource memberResource = creationUtil.createCompleteMember();
		Member member =(Member) memberResource.getContents().get(0);
		List<EChange> allChanges = new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges();
	    InsertEAttributeValue<Member, String> change = TypeInferringAtomicEChangeFactory.getInstance().createInsertAttributeChange(member, member.eClass().getEAllAttributes().get(0), 0, "test");
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		InsertEAttributeValue<Member, String>  decryptedChange = (InsertEAttributeValueImpl<Member,String>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		ArrayList<EChange> changeInList = new ArrayList<EChange>();
		changeInList.add(decryptedChange);
		changeInList.addAll(allChanges);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changeInList);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	@Test
	public void testInsertEReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		Resource memberResource = creationUtil.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);

		Resource family = creationUtil.createFamily();
		Family familyImpl = (Family) family.getContents().get(0);
		Member motherImpl = (Member) family.getContents().get(1);
		Member daughterimpl =(Member) family.getContents().get(2);
		EReference daughtersReference = familyImpl.eClass().getEAllReferences().get(1);
		List<EChange> allChanges = new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(family).getEChanges();

		InsertEReference<Family,EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
				.createInsertReferenceChange(familyImpl,daughtersReference,member,2);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		InsertEReference<Family,EObject>  decryptedChange = (InsertEReference<Family,EObject>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		ArrayList<EChange> changeInList = new ArrayList<EChange>();
		changeInList.add(decryptedChange);
		changeInList.addAll(allChanges);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changeInList);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
	}
	@Test
	public void testcreateRemoveReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		Resource family = creationUtil.createFamily();
		Family familyImpl = (Family) family.getContents().get(0);
		Member motherImpl = (Member) family.getContents().get(1);
		Member daughterimpl =(Member) family.getContents().get(2);
		EReference daughtersReference = familyImpl.eClass().getEAllReferences().get(1);
		List<EChange> allChanges = new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(family).getEChanges();

		RemoveEReference<EObject, EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
				.createRemoveReferenceChange(familyImpl, daughtersReference, daughterimpl, 0);
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		RemoveEReference<EObject, EObject>  decryptedChange = (RemoveEReference<EObject, EObject>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		ArrayList<EChange> changeInList = new ArrayList<EChange>();
		changeInList.add(decryptedChange);
		changeInList.addAll(allChanges);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changeInList);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		
	}
	@Test
	public void testcreateReplaceSingleReferenceChangeEncryption() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException {
		Map map = encryptionUtil.getEncryptionDetailsMap();
		Resource memberResource = creationUtil.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
		
		Resource family = creationUtil.createFamily();
		Family familyImpl = (Family) family.getContents().get(0);
		Member motherImpl = (Member) family.getContents().get(1);
		Member daughterimpl =(Member) family.getContents().get(2);
		EReference daughtersReference = familyImpl.eClass().getEAllReferences().get(1);
		List<EChange> allChanges = new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(family).getEChanges();
		ReplaceSingleValuedEReference<Family, Member>  change = TypeInferringAtomicEChangeFactory.getInstance()
				.createReplaceSingleReferenceChange(familyImpl, daughtersReference, daughterimpl, member);
		change.setAffectedEObjectID("EObject dummy ID");
		encryptionScheme.encryptDeltaChangeAlone(map,change,fileWithEncryptedChanges);
		ReplaceSingleValuedEReference<Member, Member>  decryptedChange = (ReplaceSingleValuedEReference<Member, Member>) encryptionScheme.decryptDeltaChangeAlone(map, fileWithEncryptedChanges);
		ArrayList<EChange> changeInList = new ArrayList<EChange>();
		changeInList.add(decryptedChange);
		changeInList.addAll(allChanges);
		TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changeInList);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    creationUtil.withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
		assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
		
	}
	
}
