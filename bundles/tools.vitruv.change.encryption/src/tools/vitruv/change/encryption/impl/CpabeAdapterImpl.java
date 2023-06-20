package tools.vitruv.change.encryption.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;

import javax.crypto.Cipher;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import junwei.cpabe.junwei.cpabe.Cpabe;
import tools.vitruv.change.atomic.EChange;
/**
 * Adapter for the CPABE implementation provided by Junwei.
 */
public class CpabeAdapterImpl {
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
	public void encrypt(String attributes,String policy,EChange change) throws Exception {
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
	public EChange decrypt() throws Exception {
		System.out.println("//start to dec");
		instance.dec(publicKeyPath, privateKeyPath, encryptedFilePath, decryptedFilePath);
		System.out.println("//end to dec");
		return null;
	}
}
