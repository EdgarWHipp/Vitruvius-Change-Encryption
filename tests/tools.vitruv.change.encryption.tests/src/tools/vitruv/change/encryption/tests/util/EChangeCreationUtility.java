package tools.vitruv.change.encryption.tests.util;

import java.io.File;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.Family;
import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.TypeInferringAtomicEChangeFactory;
import tools.vitruv.change.atomic.eobject.DeleteEObject;
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue;
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute;
import tools.vitruv.change.atomic.feature.reference.InsertEReference;
import tools.vitruv.change.atomic.root.RemoveRootEObject;
import tools.vitruv.change.changederivation.DefaultStateBasedChangeResolutionStrategy;

public class EChangeCreationUtility {
	private static EChangeCreationUtility util;
    
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
	private URI MEMBER_URI = URI.createFileURI(new File("").getAbsolutePath() + "/member.ecore");

	// Helper Functions
	public void withFactories(ResourceSet set) {
		set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	}
	private Member getMember() {
		Member member = FamiliesFactory.eINSTANCE.createMember();
		member.setFirstName("Clara");
		return member;
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
	    memberResource.getContents().add(change);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
		
	}
	
	
	/**
	 * Adds a ReplaceSingleValuedEAttribute to a resource and uses addAll() to add all changes of the resource to the List<EChange>.
	 *
	 */
	public void createReplaceSingleAttributeChange(List<EChange> changes, ResourceSet set) {
		Member member = getMember();
	    ReplaceSingleValuedEAttribute<Member,String> changeAttribute = TypeInferringAtomicEChangeFactory.getInstance().createReplaceSingleAttributeChange(member, member.eClass().getEAttributes().get(0), "Clara", "Greta");

		
		withFactories(set);
		Resource memberResource = set.createResource(MEMBER_URI);
	    memberResource.getContents().add(member);
	    memberResource.getContents().add(changeAttribute);
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());
		System.out.println(changes);
		
	}
	

	/**
	 * Adds the CreateEObject, CreateRootEObject and the CreateEAttribute change to a resource and uses addAll() to add all changes of the resource to the List<EChange>.
	 */
	public void createCreateMemberChangeSequence(List<EChange> changes, ResourceSet set) {
		
		withFactories(set);
	    Resource memberResource = set.createResource(MEMBER_URI);
	    memberResource.getContents().add(getMember());
		changes.addAll(new DefaultStateBasedChangeResolutionStrategy().getChangeSequenceForCreated(memberResource).getEChanges());

	}
	
}
