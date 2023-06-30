package tools.vitruv.change.encryption.impl.attributebased;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nl.sudohenk.kpabe.KeyPolicyAttributeBasedEncryption;
import java.nl.sudohenk.kpabe.gpswabe.gpswabePolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import kpabe.kpabe.*;
import kpabe.kpabe.policy.*;
import kpabe.gpswabe.*;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.encryption.utils.EncryptionUtils;
/**
 * Adapter for the KPABE implementation provided by ----.
 */
public class KpabeAdapterImpl {
	private final EncryptionUtils encryptionUtils = EncryptionUtils.getInstance();
	private final String privateKeyPath;
	private final String publicKeyPath;
	private final String masterKeyPath;
	private final String encryptedFilePath;
	private final String decryptedFilePath;
	private final String inputFileString;
	private final String curveParamsFileString;
	KeyPolicyAttributeBasedEncryption instance;
	
	public KpabeAdapterImpl(String privateKeyPath,String publicKeyPath,String masterKeyPath, String decryptedFilePath, String encryptedFilePath, String inputFileString, String curveParamsFileString) {
		this.instance= new KeyPolicyAttributeBasedEncryption();
		this.privateKeyPath=privateKeyPath;
		this.publicKeyPath=publicKeyPath;
		this.masterKeyPath=masterKeyPath;
		this.encryptedFilePath = encryptedFilePath;
		this.decryptedFilePath = decryptedFilePath;
		this.inputFileString = inputFileString;
		this.curveParamsFileString = curveParamsFileString;
		
		
	}
	/**
	 * 
	 * @param attributes
	 * @param policy
	 * @param change
	 * @throws Exception
	 */
	public void encryptAloneAndGenerateKeys(String[] attrs_univ,String policy,EChange change) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().add(change);
	    
	    
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    FileOutputStream fileOutputStream = new FileOutputStream(this.inputFileString);
	    
	 
	    fileOutputStream.write(byteArrayOutputStream.toByteArray());
	    byteArrayOutputStream.close();
	    fileOutputStream.close();
		instance.setup(this.publicKeyPath, this.masterKeyPath,attrs_univ,this.curveParamsFileString);
		// Build up the access tree structure:
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
		instance.enc(this.publicKeyPath, bytes, attrs_univ);
		
	}
	public EChange decryptAlone() throws Exception {
		System.out.println("//start to dec");
		

		instance.dec(publicKeyPath, privateKeyPath, encryptedFilePath, decryptedFilePath);
		System.out.println("//end to dec");
		
		FileInputStream fileInputStream = new FileInputStream(decryptedFilePath);
		
        byte[] decryptedDataInFile = fileInputStream.readAllBytes();

     

        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedDataInFile);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        EChange decryptedChange = (EChange) resource.getContents().get(0);
        return decryptedChange;
		
	}
	public void encryptTogetherAndGenerateKeys(String attributes,String policy,List<EChange> changes) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().addAll(changes);
	    
	    
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    FileOutputStream fileOutputStream = new FileOutputStream(this.inputFileString);
	    
	 
	    fileOutputStream.write(byteArrayOutputStream.toByteArray());
	    byteArrayOutputStream.close();
	    fileOutputStream.close();
		System.out.println("//start to setup");
		instance.setup(publicKeyPath, masterKeyPath);
		System.out.println("//end to setup");

		System.out.println("//start to keygen");
		// if attributes are passing here the file should be encrypted correctly, otherwise it fails.
		instance.keygen(publicKeyPath, privateKeyPath, masterKeyPath, attributes);
		System.out.println("//end to keygen");

		System.out.println("//start to enc");
		// create a file with the content of a change;
		
		instance.enc(publicKeyPath, policy, this.inputFileString, encryptedFilePath);
		
	}
	public List<EChange> decryptTogether() throws Exception {
		System.out.println("//start to dec");
		instance.dec(publicKeyPath, privateKeyPath, encryptedFilePath, decryptedFilePath);
		System.out.println("//end to dec");
		
		FileInputStream fileInputStream = new FileInputStream(decryptedFilePath);
		
        byte[] decryptedDataInFile = fileInputStream.readAllBytes();

     

        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedDataInFile);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        
        List<EChange> decryptedChanges = new ArrayList<EChange>();
        for (EObject obj : resource.getContents()) {
        	if (obj instanceof EChange) {
        		decryptedChanges.add((EChange)obj);
        	}
        }
        return decryptedChanges;
		
	}
}
