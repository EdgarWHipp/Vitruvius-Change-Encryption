package tools.vitruv.change.encryption.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.Files;

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
	public EncryptionSchemeImpl() {
	
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
		Cipher cipher = EncryptionUtils.getInstance().initCipherMode(encryptionOption,1);



		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().add(change);
	    
	    
	    
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    FileOutputStream fileOutputStream = new FileOutputStream(encryptedChangesFile);
	    byte[] encryptedData = cipher.doFinal(byteArrayOutputStream.toByteArray());
	    fileOutputStream.write(encryptedData);
	    byteArrayOutputStream.close();
	    fileOutputStream.close();

		
		
	}
	/**
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * 
	 */
	public EChange decryptDeltaChangeAlone(Map<?,?> decryptionOption,String encryptedEChange) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher cipher = EncryptionUtils.getInstance().initCipherMode(decryptionOption,2);
		
		return null;
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
		// 1 = ENCRYPT_MODE
		Cipher cipher = EncryptionUtils.getInstance().initCipherMode(encryptionMap,1);
		
        
		 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		 ResourceSet resourceSet = new ResourceSetImpl();
	     resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	     Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	     resource.getContents().addAll(changes);
	     resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);

		    
		    try {
		        byte[] encryptedData = cipher.doFinal(byteArrayOutputStream.toByteArray());
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
		// 2 = DECRYPT_MODE
		Cipher cipher = EncryptionUtils.getInstance().initCipherMode(decryptionMap,2);

		FileInputStream fileInputStream = new FileInputStream(encryptedChangesFile);
		
        byte[] encryptedData = fileInputStream.readAllBytes();

        byte[] decryptedData = cipher.doFinal(encryptedData);
        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedData);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        List<EChange> decryptedChange = resource.getContents().stream().map(it -> (EChange) it).toList();
        return decryptedChange;
        
	

	}
	
	
	
}