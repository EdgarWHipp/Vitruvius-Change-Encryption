package tools.vitruv.change.encryption.tests.attributebased;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import edu.kit.ipd.sdq.metamodels.families.Member;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.impl.CreateEObjectImpl;
import tools.vitruv.change.encryption.impl.attributebased.KpabeAdapterImpl;
import tools.vitruv.change.encryption.tests.TestChangeEncryption;

public class TestEncryptChangesAsymmetricallyWithKPABE extends TestChangeEncryption{
	
	private String[] getPassingAttributes() {
		final String[] attributes = {"application1", "module1", "solution1"};
		return attributes;
	}
	private String[] getFailingAttributes() {
		final String[] attributes = {"application1", "module1222", "solution1"};
		return attributes;
		
		
	}
	private boolean checkCorrectness(EChange change,String[] attributes) throws Exception {
		try {
			//init adapter
			KpabeAdapterImpl adapter = new KpabeAdapterImpl(inputFile);
			
			//encrypt
			// TO- DO pass a policy into this!! (hard coded access tree)
			adapter.encryptAloneAndGenerateKeys(attributes, change);
			
			//decryption should fail here.
		
			EChange decryptedChange = adapter.decryptAlone();
			
			assertNotEquals(decryptedChange,null);
			assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange));
		}catch(Exception e) {
			return false;
		}
		return true;
		
		
		
		
	}
	@AfterAll
	static void deleteGeneratedFiles() {
		TestChangeEncryption.deleteFiles();
	}
	@Test
	public void testPassingAttributesCreateEObjectChange() throws Exception {
		List<EChange> changes = new ArrayList<EChange>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		

		
		
		assertTrue(this.checkCorrectness(change,getPassingAttributes()));
		//this.collectData(change);
	}
	@Test
	public void testFailingAttributesCreateEObjectChange() throws Exception {
		List<EChange> changes = new ArrayList<EChange>();
		ResourceSet set = new ResourceSetImpl();
		TestChangeEncryption.CREATIONUTIL.createCreateMemberChangeSequence(changes, set,1);
		CreateEObjectImpl<Member> change = (CreateEObjectImpl<Member>) changes.get(0);
		

		
		
		assertFalse(this.checkCorrectness(change,getFailingAttributes()));
		//this.collectData(change);
	}
	
}
