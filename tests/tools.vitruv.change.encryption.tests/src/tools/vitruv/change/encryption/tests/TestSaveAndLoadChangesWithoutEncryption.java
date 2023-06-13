package tools.vitruv.change.encryption.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.id.IdResolver;
import tools.vitruv.change.composite.description.TransactionalChange;
import tools.vitruv.change.composite.description.VitruviusChangeFactory;
import tools.vitruv.change.encryption.impl.EncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;

/**
 * These tests are used to compare the expenditure of time between classic saving and loading and the encryption-decryption process.
 * @author Edgar Hipp
 *
 */
public class TestSaveAndLoadChangesWithoutEncryption {
	private static final Logger logger = Logger.getLogger(TestEncryptChangesSymmetricallyTogether.class.getName());
	private final File fileWithEncryptedChanges = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	private EncryptionSchemeImpl encryptionScheme = new EncryptionSchemeImpl();
	private final EChangeCreationUtility creationUtil= EChangeCreationUtility.getInstance();
	private final EncryptionUtility encryptionUtil= EncryptionUtility.getInstance();
	
	@Test
	public void testSaveAndLoadCreateReplaceSingleAttributeChange() {
			List<String> times = new ArrayList<>();
	
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createReplaceSingleAttributeChange(changes, set);
	
		 
			long startTime = System.currentTimeMillis();
		 
		   
		     
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changes);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
	
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent without encryption:"+totalTime+"ms");
			
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));  
		
			String result = times.stream()
		            .reduce((a, b) -> a + ", " + b)
		            .orElse("");
			logger.severe(result);
	}
	@Test
	public void testSaveAndLoadMemberCreation() {
			List<String> times = new ArrayList<>();
	
			List<EChange> changes = new ArrayList<>();
			ResourceSet set = new ResourceSetImpl();
			creationUtil.createCreateMemberChangeSequence(changes, set,1);
	
		 
			long startTime = System.currentTimeMillis();
		 
		   
		     
		    TransactionalChange transactionalChange = VitruviusChangeFactory.getInstance().createTransactionalChange(changes);
		    ResourceSet newResourceSet = new ResourceSetImpl();
		    creationUtil.withFactories(newResourceSet);
		    transactionalChange.resolveAndApply(IdResolver.create(newResourceSet));
	
		    long endTime = System.currentTimeMillis();
	
			long totalTime = endTime - startTime;
	
			times.add("Time spent without encryption:"+totalTime+"ms");
			
		    assertTrue(new EcoreUtil.EqualityHelper().equals(set.getResources().get(0).getContents(), newResourceSet.getResources().get(0).getContents()));  
		
			String result = times.stream()
		            .reduce((a, b) -> a + ", " + b)
		            .orElse("");
			logger.severe(result);
	}
}
