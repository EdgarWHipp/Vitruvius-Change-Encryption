package tools.vitruv.change.encryption.tests.util;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.Family;
import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.attribute.impl.ReplaceSingleValuedEAttributeImpl;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.feature.reference.RemoveEReference;
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.atomic.root.impl.InsertRootEObjectImpl;
import tools.vitruv.change.changederivation.DefaultStateBasedChangeResolutionStrategy;
import tools.vitruv.change.encryption.tests.TestChangeEncryption;
/**
 * Manages the EChange creation, including the creation of valid resources, for the tests.
 */
public class EChangeCreationUtility {
	private static EChangeCreationUtility util;
	private URI MEMBER_URI = URI.createFileURI(new File("").getAbsolutePath() + "/member.ecore");
	private URI FAMILY_URI = URI.createFileURI(new File("").getAbsolutePath() + "/family.ecore");
	   private EChangeCreationUtility() {
	      // constructor of the SingletonExample class
	   }

	   public static EChangeCreationUtility getInstance() {
		   if(util == null) {
			   util = new EChangeCreationUtility();
		      }

		       // returns the singleton object
		       return util;
	   }

	// Helper Functions
	public void withFactories(ResourceSet set) {
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	}
	public Member getMember() {
		Member member = FamiliesFactory.eINSTANCE.createMember();
		member.setFirstName("Clara");
		
		return member;
	}
	private Member getDaughter1() {
		Member member = FamiliesFactory.eINSTANCE.createMember();
		member.setFirstName("Maria");
		
		return member;
	}
	private Member getDaughter2() {
		Member member = FamiliesFactory.eINSTANCE.createMember();
		member.setFirstName("Lara");
		
		return member;
	}
	public Resource createCompleteMember() {
		Member member = getMember();
		ResourceSet set = new ResourceSetImpl();
	    withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
	    memberResource.getContents().add(member);
	    return memberResource;
	}
	public Resource createFamily() {
			
			Member mother = getMember();
			Family family = FamiliesFactory.eINSTANCE.createFamily();
			Member daughter1 = getDaughter1();
			Member daughter2 = getDaughter2();
			daughter1.setFamilyDaughter(family);
			daughter2.setFamilyDaughter(family);
			
			family.setMother(mother);
			mother.setFamilyMother(family);
			ResourceSet familySet = new ResourceSetImpl();
		    withFactories(familySet);
			Resource familyResource = familySet.createResource(FAMILY_URI);
			familyResource.getContents().add(family);	
			familyResource.getContents().add(mother);
			familyResource.getContents().add(daughter1);
			familyResource.getContents().add(daughter2);
			
			return familyResource;
		}
	
	/**
	 * Adds a InsertEReference change to a resource and uses addAll() to add all changes of the resource to the List<EChange>.
	 */
	public void createInsertReferenceChange(List<EChange> changes, ResourceSet set) {
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
	 * Adds a InsertEAttributeValue change to a resource and uses addAll() to add all changes of the resource to the List<EChange>.
	 * 
	 */
	public void createInsertEAttributeValueChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
		InsertEAttributeValue<Member, String>  change =TypeInferringAtomicEChangeFactory.getInstance().createInsertAttributeChange(member, member.eClass().getEAllAttributes().get(0), 0, "");
		memberResource.getContents().add(member);
	    memberResource.getContents().add(change);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
	}
	/**
	 * Adds a RemoveEAttributeValue change to a resource and uses addAll() to add all changes of the resource to the List<EChange>.
	 * 
	 */
	public void createRemoveAttributeChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
		
	
		
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
		RemoveEAttributeValue<Member,String> change = TypeInferringAtomicEChangeFactory.getInstance().createRemoveAttributeChange(member,member.eClass().getEAttributes().get(0),0,"Clara");
		memberResource.getContents().add(member);
	    memberResource.getContents().add(change);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
	}
	
	/**
	 * Adds a RemoveRootEObject change to a resource and uses addAll() to add all changes of the resource to the List<EChange>.
	 *
	 */
	public void createDeleteRootEObjectChange(List<EChange> changes, ResourceSet set) {
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
	 * Adds a DeleteEObject change to a resource and uses addAll() to add all changes of the resource to the List<EChange>.
	 *
	 */
	public void createDeleteEObjectChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
		

			
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
		DeleteEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createDeleteEObjectChange(member);
		memberResource.getContents().add(member);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
		changes.add(change);
		
		
	}
	
	
	/**
	 * Adds a ReplaceSingleValuedEAttribute to a resource and uses addAll() to add all changes of the resource to the List<EChange>.
	 *
	 */
	public void createReplaceSingleAttributeChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
	    withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
	    memberResource.getContents().add(member);
	    changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
	    ReplaceSingleValuedEAttribute<Member,String> changeAttribute = TypeInferringAtomicEChangeFactory.getInstance().createReplaceSingleAttributeChange(member, member.eClass().getEAttributes().get(0), "Clara", "Greta");
	    
	    
		changes.add(changeAttribute);
		
	}
	

	/**
	 * Adds the CreateEObject, CreateRootEObject and the CreateEAttribute change to a resource and uses addAll() to add all changes of the resource to the List<EChange>.
	 */
	public List<Member> createCreateMemberChangeSequence(List<EChange> changes, ResourceSet set,int amount) {
		
		withFactories(set);
	    Resource memberResource = set.createResource(MEMBER_URI);
	    List<Member> members = new ArrayList<>();
	    IntStream.range(0, amount)
        .forEach(index -> members.add(getMember()));
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
		
	    IntStream.range(0, amount)
        .forEach(index -> memberResource.getContents().add(members.get(index)));
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
		
		return members;

	}
	// ----- Return single changes
	
	public CreateEObjectImpl<Member> getCreateEObjectChange() {
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		return change;
		
	}
	public InsertRootEObjectImpl<Member> getInsertRootEObjectChange(){
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		InsertRootEObjectImpl<Member> change = (InsertRootEObjectImpl<Member>) changes.get(1);
		return change;
	}
	public ReplaceSingleValuedEAttributeImpl<Member,String> getReplaceSingleValuedEAttributeChange(){
		List<EChange> changes = new ArrayList<>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		
		ReplaceSingleValuedEAttributeImpl<Member,String> change = (ReplaceSingleValuedEAttributeImpl<Member,String>) changes.get(2);
		return change;
	}
	public DeleteEObject<Member> getDeleteEObjectChange(){
		
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
		
		DeleteEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createDeleteEObjectChange(member);
		return change;
	}
	public RemoveRootEObject<Member> getRemoveRootEObjectChange(){
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
	
		RemoveRootEObject<Member> change = TypeInferringAtomicEChangeFactory.getInstance().createRemoveRootChange(member,member.eResource(),0);
		change.setResource(null);
		return change;
	}
	public InsertEAttributeValue<Member, String> getInsertEAttributeValue(){
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
		

	    InsertEAttributeValue<Member, String> change = TypeInferringAtomicEChangeFactory.getInstance().createInsertAttributeChange(member, member.eClass().getEAllAttributes().get(0), 0, "test");
		return change;
	}
	public InsertEReference<Family,EObject> getInsertEReferenceChange(){
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);

		Resource family = TestChangeEncryption.CREATIONUTIL.createFamily();
		Family familyImpl = (Family) family.getContents().get(0);
		

		InsertEReference<Family,EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
				.createInsertReferenceChange(familyImpl,familyImpl.eClass().getEAllReferences().get(3),member,0);
		return change;
	}
	public RemoveEReference<EObject, EObject> getRemoveEReferenceChange() {
		Resource family = TestChangeEncryption.CREATIONUTIL.createFamily();
		Family familyImpl = (Family) family.getContents().get(0);
		
		Member daughter1Impl = (Member) family.getContents().get(2);
		
		RemoveEReference<EObject, EObject> change =TypeInferringAtomicEChangeFactory.getInstance()
				.createRemoveReferenceChange(familyImpl, familyImpl.eClass().getEAllReferences().get(1), daughter1Impl, 0);
		return change;
	}
	public ReplaceSingleValuedEReference<Family, Member> getReplaceSingleValuedReferenceChange(){
		Resource memberResource = TestChangeEncryption.CREATIONUTIL.createCompleteMember();
		Member member = (Member) memberResource.getContents().get(0);
		
		Resource family = TestChangeEncryption.CREATIONUTIL.createFamily();
		Family familyImpl = (Family) family.getContents().get(0);
		Member motherImpl = (Member) family.getContents().get(1);
		EReference mothersreference = familyImpl.eClass().getEAllReferences().get(3);
		ReplaceSingleValuedEReference<Family, Member>  change = TypeInferringAtomicEChangeFactory.getInstance()
				.createReplaceSingleReferenceChange(familyImpl, mothersreference, motherImpl, member);
		change.setAffectedEObject(null);
		return change;
		
		
	}
	
	
	
}
