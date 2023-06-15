package tools.vitruv.change.encryption.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import edu.kit.ipd.sdq.commons.util.java.Pair;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.encryption.EncryptionScheme;
import tools.vitruv.change.encryption.utils.EncryptionUtils;

/**
 * Contains the logic for writing a List<EncryptedEChange> to an OutputStream and to load a List<EChange> from an InputStream.
 * @author Edgar Hipp
 *	
 */
public class EncryptionSchemeImpl implements EncryptionScheme{
	private static final Logger logger = Logger.getLogger(EncryptionSchemeImpl.class.getName());
	private final EncryptionUtils encryptionUtils = EncryptionUtils.getInstance();
	public EncryptionSchemeImpl() {
	
	}
	public void encryptDeltaChangeAloneAsymmetrically(Map<?,?> encryptionOption,EChange change,File encryptedChangesFile) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().add(change);
	    
	    
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    FileOutputStream fileOutputStream = new FileOutputStream(encryptedChangesFile);
	    
	    byte[] encryptedData = encryptionUtils.cryptographicFunctionAsymmetric(encryptionOption,Cipher.ENCRYPT_MODE,byteArrayOutputStream.toByteArray());
	    fileOutputStream.write(encryptedData);
	    byteArrayOutputStream.close();
	    fileOutputStream.close();
		
		
		
	}
	
	public EChange decryptDeltaChangeAloneAsymmetrically(Map<?,?> decryptionOption,File encryptedChangesFile) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		FileInputStream fileInputStream = new FileInputStream(encryptedChangesFile);
		
        byte[] encryptedData = fileInputStream.readAllBytes();

        byte[] decryptedData = encryptionUtils.cryptographicFunctionAsymmetric(decryptionOption,Cipher.DECRYPT_MODE,encryptedData);

        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedData);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        EChange decryptedChange = (EChange) resource.getContents().get(0);
        return decryptedChange;
	}
	
	public void encryptDeltasCustomKeys(Map<EChange,Pair<SecretKey,String>> options,File encryptedChangesFile) {
		
	}
	/**
	 * @throws IOException 
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * 
	 */
	public void encryptDeltaChangeAlone(Map<?,?> encryptionOption,EChange change,File encryptedChangesFile) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {



		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().add(change);
	    
	    
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    FileOutputStream fileOutputStream = new FileOutputStream(encryptedChangesFile);
	    
	    byte[] encryptedData = encryptionUtils.cryptographicFunctionSymmetric(encryptionOption,Cipher.ENCRYPT_MODE,byteArrayOutputStream.toByteArray());
	    fileOutputStream.write(encryptedData);
	    byteArrayOutputStream.close();
	    fileOutputStream.close();

		
		
	}
	/**
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * 
	 */
	public EChange decryptDeltaChangeAlone(Map<?,?> decryptionOption,File encryptedChangesFile) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException {
		
		
		FileInputStream fileInputStream = new FileInputStream(encryptedChangesFile);
		
        byte[] encryptedData = fileInputStream.readAllBytes();

        byte[] decryptedData = encryptionUtils.cryptographicFunctionSymmetric(decryptionOption,Cipher.DECRYPT_MODE,encryptedData);

        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedData);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        EChange decryptedChange = (EChange) resource.getContents().get(0);
        return decryptedChange;
		
		
	}
	
	/**
	 * Writes a list of EncryptedEChanges to the OutputStream, these contain the EChanges in the encryptedBytes field.
	 * @param encryptionMap
	 * @param changes
	 * @param out
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public void encryptDeltaChangesTogether(Map<?,?> encryptionMap, List<EChange> changes,File encryptedChangesFile) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		
        
		 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		 ResourceSet resourceSet = new ResourceSetImpl();
	     resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	     Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	     resource.getContents().addAll(changes);
	     resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);

		    
		    try {
		        byte[] encryptedData = encryptionUtils.cryptographicFunctionSymmetric(encryptionMap,Cipher.ENCRYPT_MODE,byteArrayOutputStream.toByteArray());
		        FileOutputStream fileOutputStream = new FileOutputStream(encryptedChangesFile);
		        try {
		            fileOutputStream.write(encryptedData);
		        } finally {
		            fileOutputStream.close();
		        }
		    } finally {
		    	byteArrayOutputStream.close();
		    }
		

			    

		
	}
	/**
	 * Reads the List<EncryptedEChange> from the InputStream, and reconstructs the EChange from the encryptedBytes field of each EncryptedEChange object.
	 * @param encryptionMap
	 * @param in
	 * @return List<EChange>
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException 
	 */

	public  List<EChange> decryptDeltaChangesTogether(Map<?,?> decryptionMap, File encryptedChangesFile) throws IOException, ClassNotFoundException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException{

		FileInputStream fileInputStream = new FileInputStream(encryptedChangesFile);
		
        byte[] encryptedData = fileInputStream.readAllBytes();

        byte[] decryptedData = encryptionUtils.cryptographicFunctionSymmetric(decryptionMap,Cipher.DECRYPT_MODE,encryptedData);

        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedData);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        List<EChange> decryptedChange = resource.getContents().stream().map(it -> (EChange) it).toList();
        return decryptedChange;
        
	

	}
	
	
	
}
