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

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.encryption.EncryptedResourceFactoryImpl;
import tools.vitruv.change.encryption.EncryptionScheme;

/**
 * Contains the logic for writing a List<EncryptedEChange> to an OutpusStream and to load a List<EChange> from an InputStream.
 * @author Edgar Hipp
 *	
 */
public class EncryptionSchemeImpl implements EncryptionScheme{
	private static final Logger logger = Logger.getLogger(EncryptionSchemeImpl.class.getName());
	public EncryptionSchemeImpl() {
	
	}
	/**
	 * @throws IOException 
	 * 
	 */
	public void encryptDeltaChangeAlone(SecretKey key,EChange change,File encryptedChangesFile) throws IOException {
	    

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				ResourceSet resourceSet = new ResourceSetImpl();
			    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("enc", new EncryptedResourceFactoryImpl());
			    
			    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.enc"));
			    resource.getContents().add(change);
			    
			    
			    
			    ((EncryptedResourceImpl)resource).doSave(byteArrayOutputStream,key);
			    FileOutputStream fileOutputStream = new FileOutputStream(encryptedChangesFile);
			    byteArrayOutputStream.writeTo(fileOutputStream);
			    byteArrayOutputStream.close();
			    fileOutputStream.close();

		
		
		
	}
	/**
	 * 
	 */
	public void decryptDeltaChangeAlone() {
		
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
			
			if (encryptionMap.containsKey("differentEncryption")) {
				SecretKey[] keys = (SecretKey[]) encryptionMap.get("differentEncryption");
				int counter=0;
				for (EChange change : changes) {
					
					this.encryptDeltaChangeAlone(keys[counter], change,encryptedChangesFile);
					counter++;
				}
				return;
			}
		
			
		 	
	        

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ResourceSet resourceSet = new ResourceSetImpl();
		    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("enc", new EncryptedResourceFactoryImpl());
		    
		    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.enc"));
		    resource.getContents().addAll(changes);

		    ((EncryptedResourceImpl)resource).doSave(byteArrayOutputStream,Collections.EMPTY_MAP);
		    FileOutputStream fileOutputStream = new FileOutputStream(encryptedChangesFile);
		    byteArrayOutputStream.writeTo(fileOutputStream);
		    byteArrayOutputStream.close();
		    fileOutputStream.close();

		

			    

		
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
		if (decryptionMap.containsKey("differentDecryption")) {
			SecretKey[] keys = (SecretKey[]) decryptionMap.get("differentEncryption");
			int counter=0;
			// copied over has to filter each EChange objects from the file for different decryption!
			for (EChange change : changes) {
				
				EChange decryptedChange = this.decryptDeltaChangeAlone(keys[counter], change,encryptedChangesFile);
				counter++;
			}
			return;
		}
		
			FileInputStream fileInputStream = new FileInputStream(encryptedChangesFile);

		
	        byte[] encryptedData = fileInputStream.readAllBytes();

	        
	        ByteArrayInputStream encryptedStream = new ByteArrayInputStream(encryptedData);
	        
	        final ResourceSet resourceSet = new ResourceSetImpl();

	        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("enc", new EncryptedResourceFactoryImpl());
	        final Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.enc"));
	        ((EncryptedResourceImpl)resource).doLoad(encryptedStream,Collections.EMPTY_MAP);
	        
	        List<EChange> decryptedChanges = resource.getContents().stream().map(it -> (EChange) it).toList();
	        fileInputStream.close();
	        encryptedChangesFile.delete();
	        return decryptedChanges;
	       
		
	
	}
	
}
