package tools.vitruv.change.encryption.impl.attributebased;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
	private final String privateKeyPath;
	private final String publicKeyPath;
	private final String masterKeyPath;
	private final String encryptedFilePath;
	private final String decryptedFilePath;
	private final String inputFileString;
	Cpabe instance;
	public CpabeAdapterImpl(Cpabe cpInstance,String privateKeyPath,String publicKeyPath,String masterKeyPath, String decryptedFilePath, String encryptedFilePath, String inputFileString) {
		this.instance=cpInstance;
		this.privateKeyPath=privateKeyPath;
		this.publicKeyPath=publicKeyPath;
		this.masterKeyPath=masterKeyPath;
		this.encryptedFilePath = encryptedFilePath;
		this.decryptedFilePath = decryptedFilePath;
		this.inputFileString = inputFileString;
		
		
	}
	/**
	 * Encrypts an EChange and saves it in the encryptedFilePath.
	 * Also creates appropriate keys based on the user attributes and includes the policy inside the encrypted EChange.
	 * @param attributes
	 * @param policy
	 * @param change
	 * @throws Exception
	 */
	public void encryptAloneAndGenerateKeys(String attributes,String policy,EChange change) throws Exception {
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
		instance.setup(publicKeyPath, masterKeyPath);
		
		// if attributes are passing here the file should be encrypted correctly, otherwise it fails.
		instance.keygen(publicKeyPath, privateKeyPath, masterKeyPath, attributes);
		
		// create a file with the content of a change;
		
		instance.enc(publicKeyPath, policy, this.inputFileString, encryptedFilePath);
		
	}
	public EChange decryptAlone() throws Exception {
		

		instance.dec(publicKeyPath, privateKeyPath, encryptedFilePath, decryptedFilePath);
		
		FileInputStream fileInputStream = new FileInputStream(decryptedFilePath);
		
        byte[] decryptedDataInFile = fileInputStream.readAllBytes();

        fileInputStream.close();

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
		instance.setup(publicKeyPath, masterKeyPath);

		// if attributes are passing here the file should be encrypted correctly, otherwise it fails.
		instance.keygen(publicKeyPath, privateKeyPath, masterKeyPath, attributes);

		// create a file with the content of a change;
		
		instance.enc(publicKeyPath, policy, this.inputFileString, encryptedFilePath);
		
	}
	public List<EChange> decryptTogether() throws Exception {
		instance.dec(publicKeyPath, privateKeyPath, encryptedFilePath, decryptedFilePath);
		
		FileInputStream fileInputStream = new FileInputStream(decryptedFilePath);
		
        byte[] decryptedDataInFile = fileInputStream.readAllBytes();

     
        fileInputStream.close();
        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedDataInFile);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        
        List<EChange> decryptedChanges = resource.getContents().stream().map(it -> (EChange) it).toList();
        return decryptedChanges;
		
	}
}
