package tools.vitruv.change.encryption.impl.attributebased;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import nl.sudohenk.kpabe.KeyPolicyAttributeBasedEncryption;
import nl.sudohenk.kpabe.gpswabe.gpswabePolicy;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.encryption.utils.EncryptionUtils;
/**
 * Adapter for the KPABE implementation provided by ----.
 */
public class KpabeAdapterImpl {
	private final String privateKeyPath = "/Users/edgarhipp/git/Vitruvius-Change-Encryption/bundles/tools.vitruv.change.encryption/src/tools/vitruv/change/encryption/impl/attributebased/publickey";
	private final String publicKeyPath ="/Users/edgarhipp/git/Vitruvius-Change-Encryption/bundles/tools.vitruv.change.encryption/src/tools/vitruv/change/encryption/impl/attributebased/policy";
	private final String masterKeyPath ="/Users/edgarhipp/git/Vitruvius-Change-Encryption/bundles/tools.vitruv.change.encryption/src/tools/vitruv/change/encryption/impl/attributebased/mastersecretkey";

	private final String inputFileString;
	private final String curveParamsFileString ="/Users/edgarhipp/git/Vitruvius-Change-Encryption/bundles/tools.vitruv.change.encryption/src/tools/vitruv/change/encryption/impl/attributebased/curveparams";
	KeyPolicyAttributeBasedEncryption instance;
	
	public KpabeAdapterImpl( String inputFileString) {
		this.instance= new KeyPolicyAttributeBasedEncryption();
		this.inputFileString = inputFileString;

		
		
		
	}
	/**
	 * 
	 * @param attributes
	 * @param policy
	 * @param change
	 * @throws Exception
	 */
	public void encryptAloneAndGenerateKeys(String[] attrs_univ,EChange change) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().add(change);
	    
	    System.out.println(new File(this.curveParamsFileString).exists());
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    
		instance.setup(this.publicKeyPath, this.masterKeyPath,attrs_univ,this.curveParamsFileString);
		// Build up the access tree structure:
		// TO DO - rewrite this for the general case. A Tree Strucutre should be pased through the parameters.
		gpswabePolicy sub1_policy = new gpswabePolicy("solution1", 1, null);
        gpswabePolicy sub2_policy = new gpswabePolicy(null, 1, null);
        gpswabePolicy[] sub2_children = new gpswabePolicy[] {new gpswabePolicy("application1", 1, null), new gpswabePolicy("module1", 1, null)};
        sub2_policy.setChildren(sub2_children);
        // create the root object
        gpswabePolicy kpabepolicy = new gpswabePolicy(null, 2, null);
        gpswabePolicy[] policy_children = new gpswabePolicy[] {sub1_policy, sub2_policy};
        kpabepolicy.setChildren(policy_children);
		
		
		

		// if attributes are passing here the file should be encrypted correctly, otherwise it fails.
		instance.keygen(this.publicKeyPath, this.masterKeyPath,
				this.privateKeyPath, kpabepolicy);
		
		// create a file with the content of a change;
		byte[] bytes = byteArrayOutputStream.toByteArray();
		byte[] encryptedBytes = instance.enc(this.publicKeyPath, bytes, attrs_univ);
		FileOutputStream fileOutputStream = new FileOutputStream(this.inputFileString);
	    
		 // write the encrypted bytes to the File(this.InputFileString)
	    fileOutputStream.write(encryptedBytes);
	    byteArrayOutputStream.close();
	    fileOutputStream.close();
		
	}
	public EChange decryptAlone() throws Exception {
		
		FileInputStream fileInputStream = new FileInputStream(this.inputFileString);
		byte[] encryptedBytes = fileInputStream.readAllBytes();
		byte[] decryptedBytes = instance.dec(publicKeyPath, privateKeyPath,encryptedBytes);
		
		
     
		
        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedBytes);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        EChange decryptedChange = (EChange) resource.getContents().get(0);
        fileInputStream.close();
        decryptedStream.close();
        String[] files = {privateKeyPath,publicKeyPath,masterKeyPath};
        for (String file:files){
        	 try (FileWriter writer = new FileWriter(file)) {
                 // Writing an empty string to clear the file
                 writer.write("");
             } catch (IOException e) {
                 e.printStackTrace();
             }
        }
        return decryptedChange;
		
	}
	public void encryptTogetherAndGenerateKeys(String[] attrs_univ,String policy,List<EChange> changes) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().addAll(changes);
	    
	    
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    
		instance.setup(this.publicKeyPath, this.masterKeyPath,attrs_univ,this.curveParamsFileString);
		// Build up the access tree structure:
		// TO DO - rewrite this for the general case. A Tree Strucutre should be pased through the parameters.
		gpswabePolicy sub1_policy = new gpswabePolicy("solution1", 1, null);
        gpswabePolicy sub2_policy = new gpswabePolicy(null, 1, null);
        gpswabePolicy[] sub2_children = new gpswabePolicy[] {new gpswabePolicy("application1", 1, null), new gpswabePolicy("module1", 1, null)};
        sub2_policy.setChildren(sub2_children);
        // create the root object
        gpswabePolicy kpabepolicy = new gpswabePolicy(null, 2, null);
        gpswabePolicy[] policy_children = new gpswabePolicy[] {sub1_policy, sub2_policy};
        kpabepolicy.setChildren(policy_children);
		
		
		

		// if attributes are passing here the file should be encrypted correctly, otherwise it fails.
		instance.keygen(this.publicKeyPath, this.masterKeyPath,
				this.privateKeyPath, kpabepolicy);
		
		// create a file with the content of a change;
		byte[] bytes = byteArrayOutputStream.toByteArray();
		byte[] encryptedBytes = instance.enc(this.publicKeyPath, bytes, attrs_univ);
		FileOutputStream fileOutputStream = new FileOutputStream(this.inputFileString);
	    
		 // write the encrypted bytes to the File(this.InputFileString)
	    fileOutputStream.write(encryptedBytes);
	    byteArrayOutputStream.close();
	    fileOutputStream.close();
		
	}
	public List<EChange> decryptTogether() throws Exception {
	
		FileInputStream fileInputStream = new FileInputStream(this.inputFileString);
		byte[] encryptedBytes = fileInputStream.readAllBytes();
		byte[] decryptedBytes = instance.dec(publicKeyPath, privateKeyPath,encryptedBytes);
		
		
     
		
        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedBytes);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        List<EChange> changes = new ArrayList<EChange>();
        List<EObject> decryptedChanges = resource.getContents();
        for (EObject obj : decryptedChanges) {
        	if (obj instanceof EChange){
        		changes.add((EChange)obj);
        	}
        }
        fileInputStream.close();
        decryptedStream.close();
        return changes;
	}
}
