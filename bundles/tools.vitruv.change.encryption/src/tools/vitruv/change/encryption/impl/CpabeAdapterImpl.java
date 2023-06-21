package tools.vitruv.change.encryption.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.crypto.Cipher;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import junwei.cpabe.junwei.cpabe.Cpabe;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.encryption.utils.EncryptionUtils;
/**
 * Adapter for the CPABE implementation provided by Junwei.
 */
public class CpabeAdapterImpl {
	private final EncryptionUtils encryptionUtils = EncryptionUtils.getInstance();
	private final String privateKeyPath;
	private final String publicKeyPath;
	private final String masterKeyPath;
	private final String encryptedFilePath;
	private final String decryptedFilePath;
	Cpabe instance;
	public CpabeAdapterImpl(Cpabe cpInstance,String privateKeyPath,String publicKeyPath,String masterKeyPath, String decryptedFilePath, String encryptedFilePath) {
		this.instance=cpInstance;
		this.privateKeyPath=privateKeyPath;
		this.publicKeyPath=publicKeyPath;
		this.masterKeyPath=masterKeyPath;
		this.encryptedFilePath = encryptedFilePath;
		this.decryptedFilePath = decryptedFilePath;
		
	}
	public void encryptAlone(String attributes,String policy,EChange change) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().add(change);
	    
	    
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    String inputFile = "InputForCPABEEncryption";
	    FileOutputStream fileOutputStream = new FileOutputStream(inputFile);
	    
	 
	    fileOutputStream.write(byteArrayOutputStream.toByteArray());
	    byteArrayOutputStream.close();
	    fileOutputStream.close();
		System.out.println("//start to setup");
		instance.setup(publicKeyPath, masterKeyPath);
		System.out.println("//end to setup");

		System.out.println("//start to keygen");
		instance.keygen(publicKeyPath, privateKeyPath, masterKeyPath, attributes);
		System.out.println("//end to keygen");

		System.out.println("//start to enc");
		// create a file with the content of a change;
		
		instance.enc(publicKeyPath, policy, inputFile, encryptedFilePath);
		
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
	public void encryptTogether(String attributes,String policy,List<EChange> changes) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().addAll(changes);
	    
	    
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    String inputFile = "InputForCPABEEncryption";
	    FileOutputStream fileOutputStream = new FileOutputStream(inputFile);
	    
	 
	    fileOutputStream.write(byteArrayOutputStream.toByteArray());
	    byteArrayOutputStream.close();
	    fileOutputStream.close();
		System.out.println("//start to setup");
		instance.setup(publicKeyPath, masterKeyPath);
		System.out.println("//end to setup");

		System.out.println("//start to keygen");
		instance.keygen(publicKeyPath, privateKeyPath, masterKeyPath, attributes);
		System.out.println("//end to keygen");

		System.out.println("//start to enc");
		// create a file with the content of a change;
		
		instance.enc(publicKeyPath, policy, inputFile, encryptedFilePath);
		
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
