package tools.vitruv.change.encryption.impl.symmetric;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.encryption.utils.EncryptionUtils;

/**
 * Contains the logic for writing a List<EncryptedEChange> to an OutputStream and to load a List<EChange> from an InputStream.
 * @author Edgar Hipp
 *	
 */
public class SymmetricEncryptionSchemeImpl{
	private static final Logger logger = Logger.getLogger(SymmetricEncryptionSchemeImpl.class.getName());
	private final EncryptionUtils encryptionUtils = EncryptionUtils.getInstance();
	private String csvFileNameAlone;
	private String csvFileNameTogether;
	
	public SymmetricEncryptionSchemeImpl(String csvFileNameAlone,String csvFileNameTogether) {
		this.csvFileNameAlone=csvFileNameAlone;
		this.csvFileNameTogether =csvFileNameTogether;
	}
	public String getCSVFileNameAlone() {
		return this.csvFileNameAlone;
	}
	public String getCSVFileNameTogether() {
		return this.csvFileNameTogether;
	}
	
	
	/** Encrypts a single EChange and saved it to the encryptedChangesFile.
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

	    resource.save(byteArrayOutputStream, Collections.EMPTY_MAP);

	    byte[] encryptedData = encryptionUtils.cryptographicFunctionSymmetric(encryptionOption, Cipher.ENCRYPT_MODE, byteArrayOutputStream.toByteArray());

	    try (FileOutputStream fileOutputStream = new FileOutputStream(encryptedChangesFile)) {
	        fileOutputStream.write(encryptedData);
	        fileOutputStream.close();
	    }
	    
	   

		
		
	}
	/** Reads a single EChange from the encryptedChangesFile, decrypts it and returns the EChange.
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws IOException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @return decrypted EChange
	 * 
	 */
	public EChange decryptDeltaChangeAlone(Map<?,?> decryptionOption,File encryptedChangesFile) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, IllegalBlockSizeException, BadPaddingException {
		
		
		FileInputStream fileInputStream = new FileInputStream(encryptedChangesFile);
		
        byte[] encryptedData = fileInputStream.readAllBytes();
        fileInputStream.close();
        
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
	 * Writes an encrypted List<EChange> to the encryptedChangesFile.,
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

		    
		   
         byte[] encryptedData = encryptionUtils.cryptographicFunctionSymmetric(encryptionMap,Cipher.ENCRYPT_MODE,byteArrayOutputStream.toByteArray());
         FileOutputStream fileOutputStream = new FileOutputStream(encryptedChangesFile);
        
         fileOutputStream.write(encryptedData);
         
         fileOutputStream.close();
         byteArrayOutputStream.close();
    
		

			    

		
	}
	/**
	 * Reads a List<EChange> from the encryptedChangesFile, decrypts them and returns the decrypted List<EChange>.
	 * @param decryptionMap
	 * @param encryptedChangesFile
	 * @return decrypted changes
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 */

	public List<EChange> decryptDeltaChangesTogether(Map<?,?> decryptionMap, File encryptedChangesFile) throws IOException, ClassNotFoundException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException{

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
