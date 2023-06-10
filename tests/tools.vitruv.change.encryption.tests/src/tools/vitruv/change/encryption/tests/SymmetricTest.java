package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.Family;
import edu.kit.ipd.sdq.metamodels.families.Member;
import edu.kit.ipd.sdq.metamodels.persons.Male;
import edu.kit.ipd.sdq.metamodels.persons.Person;
import edu.kit.ipd.sdq.metamodels.persons.PersonRegister;
import edu.kit.ipd.sdq.metamodels.persons.PersonsFactory;
import edu.kit.ipd.sdq.metamodels.persons.PersonsPackage;
import edu.kit.ipd.sdq.metamodels.persons.impl.FemaleImpl;
import edu.kit.ipd.sdq.metamodels.persons.impl.MaleImpl;
import edu.kit.ipd.sdq.metamodels.persons.impl.PersonRegisterImpl;
import edu.kit.ipd.sdq.metamodels.persons.impl.PersonsFactoryImpl;
import tools.vitruv.change.atomic.AtomicFactory;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.CreateEObject;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.atomic.root.InsertRootEObject;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.RootFactory;
import tools.vitruv.change.atomic.root.impl.RemoveRootEObjectImpl;
import tools.vitruv.change.atomic.uuid.UuidResolver;
import tools.vitruv.change.encryption.EncryptionScheme;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.changederivation.DefaultStateBasedChangeResolutionStrategy;



/*
 * Defines a set of Unit Tests for the symmetric encryption of model deltas.
 */


public class SymmetricTest {
	private static final Logger logger = Logger.getLogger(SymmetricTest.class.getName());
	private URI MEMBER_URI = URI.createFileURI(new File("").getAbsolutePath() + "/member.ecore");
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private EncryptionScheme encryptionScheme = new EncryptionScheme();
	
	
	

	
	
	
	@Test 
	public void testSaveAndLoadCreateReplaceSingleAttributeChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		
		
	
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		createReplaceSingleAttributeChange(changes, set);

	 
	     
	    encryptionScheme.encryptDeltaChange(getEncryptionDetailsMap(), changes, fileWithEncryptedChanges);
	    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChange(getEncryptionDetailsMap(), fileWithEncryptedChanges);
	     
	    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
	    
	    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));  
	    
	}
	
	@Test
	public void testSaveAndLoadMemberCreation() throws NoSuchAlgorithmException, InvalidKeyException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
		
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		createCreateMemberChangeSequence(changes, set);
	    
	     
	    encryptionScheme.encryptDeltaChange(getEncryptionDetailsMap(), changes, fileWithEncryptedChanges);
	    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChange(getEncryptionDetailsMap(), fileWithEncryptedChanges);
	     
	    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));

	    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
	}
	@Test 
	public void testSaveAndLoadcreateDleteEObjectChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		createDeleteEObjectChange(changes, set);
	    
	     
	    encryptionScheme.encryptDeltaChange(getEncryptionDetailsMap(), changes, fileWithEncryptedChanges);
	    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChange(getEncryptionDetailsMap(), fileWithEncryptedChanges);
	     
	    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));

	    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
	}
	
	@Test
	public void testSaveAndLoadcreateRemoveAttributeChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		createRemoveAttributeChange(changes, set);
	    
	     
	    encryptionScheme.encryptDeltaChange(getEncryptionDetailsMap(), changes, fileWithEncryptedChanges);
	    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChange(getEncryptionDetailsMap(), fileWithEncryptedChanges);
	     
	    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));

	    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
	}
	/**
	 * Test the Encryption and Decryption of the DeleteRootEObjectChange;
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
		createDeleteRootEObjectChange(changes, set);
	    
	     
	    encryptionScheme.encryptDeltaChange(getEncryptionDetailsMap(), changes, fileWithEncryptedChanges);
	    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChange(getEncryptionDetailsMap(), fileWithEncryptedChanges);
	     
	    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));

	    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
	}
	@Test
	public void test() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		//createDeleteRootEObjectChange(changes, set);
	    
	     
	    encryptionScheme.encryptDeltaChange(getEncryptionDetailsMap(), changes, fileWithEncryptedChanges);
	    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChange(getEncryptionDetailsMap(), fileWithEncryptedChanges);
	     
	    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));

	    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
	}
	@Test
	public void testSaveAndLoadInsertEAttributeValueChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		createInsertEAttributeValueChange(changes, set);
	    
	     
	    encryptionScheme.encryptDeltaChange(getEncryptionDetailsMap(), changes, fileWithEncryptedChanges);
	    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChange(getEncryptionDetailsMap(), fileWithEncryptedChanges);
	     
	    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));

	    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));    
	}
	@Test
	public void testSaveAndLoadcreateInsertReferenceChange() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException, ClassNotFoundException {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		createInsertReferenceChange(changes, set);
	    
	     
	    encryptionScheme.encryptDeltaChange(getEncryptionDetailsMap(), changes, fileWithEncryptedChanges);
	    List<EChange> decryptedChange = encryptionScheme.decryptDeltaChange(getEncryptionDetailsMap(), fileWithEncryptedChanges);
	     
	    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(decryptedChange);
	    ResourceSet newResourceSet = new ResourceSetImpl();
	    withFactories(newResourceSet);
	    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));

	    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));   
	}
	
	// Helper Functions
	/**
	 * 
	 */
	private void createInsertReferenceChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
		Family family = FamiliesFactory.eINSTANCE.createFamily();
		family.setMother(member);
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
		InsertEReference<Family,EObject> change =TypeInferringAtomicEChangeFactory.getInstance().createInsertReferenceChange(family,family.eClass().getEAllReferences().get(0),null,0);
		memberResource.getContents().add(family);
		memberResource.getContents().add(member);
	    memberResource.getContents().add(change);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
	}
	/**
	 * InsertEAttributeValueImpl
	 * @param changes
	 * @param set
	 */
	private void createInsertEAttributeValueChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
		InsertEAttributeValue<Member, String>  change =TypeInferringAtomicEChangeFactory.getInstance().createInsertAttributeChange(member, member.eClass().getEAllAttributes().get(0), 0, "");
		memberResource.getContents().add(member);
	    memberResource.getContents().add(change);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
	}
	private void createRemoveAttributeChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
		
	
		
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
		RemoveEAttributeValue<Member,String> change = TypeInferringAtomicEChangeFactory.getInstance().createRemoveAttributeChange(member,member.eClass().getEAttributes().get(0),0,"Clara");
		memberResource.getContents().add(member);
	    memberResource.getContents().add(change);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
	}
	
	/**
	 * CreateEObjectImpl
	 * InsertRootEObjectImpl
	 * ReplaceSingleValuedEAttributeImpl
	 */
	private void createDeleteRootEObjectChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
		

		
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
		RemoveRootEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createRemoveRootChange(member,memberResource,0);
		change.setResource(null);
		memberResource.getContents().add(member);
		

	    memberResource.getContents().add(change);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
	}
	/**
	 * DeleteEObjectChange
	 * CreateEObjectImpl
	 * InsertRootEObjectImpl
	 * ReplaceSingleValuedEAttributeImpl
	 * 
	 */
	private void createDeleteEObjectChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
		

			
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
		EChange change = TypeInferringAtomicEChangeFactory.getInstance().createDeleteEObjectChange(member);
		memberResource.getContents().add(member);
	    memberResource.getContents().add(change);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
		
	}
	
	
	/**
	 * CreateEObjectImpl
	 * InsertRootEObjectImpl
	 * ReplaceSingleValuedEAttributeImpl
	 * ReplaceSingleValuedEReferenceImpl
	 * 
	 */
	private void createReplaceSingleAttributeChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
	    EChange changeAttribute = TypeInferringAtomicEChangeFactory.getInstance().createReplaceSingleAttributeChange(member, member.eClass().getEAttributes().get(0), "Clara", "Greta");

		
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
	    memberResource.getContents().add(member);
	    memberResource.getContents().add(changeAttribute);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
		
	}
	

	/**

	 * CreateEObjectImpl
	 * InsertRootEObjectImpl
	 * ReplaceSingleValuedEAttributeImpl
	 * 
	 * 
	 */
	private void createCreateMemberChangeSequence(List<EChange> changes, ResourceSet set) {
		
		withFactories(set);
	    Resource memberResource = set.createResource(MEMBER_URI);
	    memberResource.getContents().add(getMember());
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
	}
	
	
	
	
	private void withFactories(ResourceSet set) {
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
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
	
	private Member getMember() {
		Member member = FamiliesFactory.eINSTANCE.createMember();
		member.setFirstName("Clara");
		return member;
	}
	
	
	
	



}