package tools.vitruv.change.encryption;

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
import tools.vitruv.change.encryption.impl.EncryptedEChangeImpl;

/**
 * Contains the logic for writing a List<EncryptedEChange> to an OutpusStream and to load a List<EChange> from an InputStream.
 * @author Edgar Hipp
 *	
 */
public class EncryptionScheme {
	private static final Logger logger = Logger.getLogger(EncryptionScheme.class.getName());
	public EncryptionScheme() {
	
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
	public void encryptDeltaChange(Map<?,?> encryptionMap, EChange change,File encryptedChangesFile) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
			
		
		/*
			SecretKey secretKey = (SecretKey) encryptionMap.get("secretKey");
			String algorithm =  (String) encryptionMap.get("algorithm");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		 	*/
	        
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ResourceSet resourceSet = new ResourceSetImpl();
		    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("enc", new EncryptedResourceFactoryImpl());
		    
		    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.enc"));
		    resource.getContents().add(change);

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
	public EChange decryptDeltaChange(Map<?,?> decryptionMap, File encryptedChanges) throws IOException, ClassNotFoundException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException{
			FileInputStream fileInputStream = new FileInputStream(encryptedChanges);
		
	        byte[] encryptedData = fileInputStream.readAllBytes();

	        
	        ByteArrayInputStream encryptedStream = new ByteArrayInputStream(encryptedData);
	        
	        final ResourceSet resourceSet = new ResourceSetImpl();
	        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("enc", new EncryptedResourceFactoryImpl());
	        final Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.enc"));
	        ((EncryptedResourceImpl)resource).doLoad(encryptedStream,Collections.EMPTY_MAP);
	        
            EChange decryptedChange = (EChange) resource.getContents().get(0);
            return decryptedChange;
	       
		
	
	}
	
}
