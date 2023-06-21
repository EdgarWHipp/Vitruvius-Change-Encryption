package tools.vitruv.change.encryption.impl.asymmetric;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.encryption.EncryptionScheme;
import tools.vitruv.change.encryption.utils.EncryptionUtils;

public class AsymmetricEncryptionSchemeImpl{
	private static final Logger logger = Logger.getLogger(AsymmetricEncryptionSchemeImpl.class.getName());
	private final EncryptionUtils encryptionUtils = EncryptionUtils.getInstance();
	private String csvFileNameAlone;
	private String csvFileNameTogether;

	public AsymmetricEncryptionSchemeImpl(String csvFileNameAlone, String csvFileNameTogether) {
		this.csvFileNameAlone = csvFileNameAlone;
		this.csvFileNameTogether =csvFileNameTogether;
	}
	public String getCSVFileNameAlone()
	{
		return this.csvFileNameAlone;
	}	
	public String getCSVFileNameTogether()
	{
		return this.csvFileNameTogether;
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
	
	public void encryptDeltaChangeTogetherAsymmetrically(Map<?,?> encryptionOption,List<EChange> changes,File encryptedChangesFile) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ResourceSet resourceSet = new ResourceSetImpl();
	    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
	    
	    Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/dummy.ecore"));
	    resource.getContents().addAll(changes);
	    
	    
	    resource.save(byteArrayOutputStream,Collections.EMPTY_MAP);
	    FileOutputStream fileOutputStream = new FileOutputStream(encryptedChangesFile);
	    
	    byte[] encryptedData = encryptionUtils.cryptographicFunctionAsymmetric(encryptionOption,Cipher.ENCRYPT_MODE,byteArrayOutputStream.toByteArray());
	    fileOutputStream.write(encryptedData);
	    byteArrayOutputStream.close();
	    fileOutputStream.close();
		
		
		
	}
	
	public List<EChange> decryptDeltaChangeTogetherAsymmetrically(Map<?,?> decryptionOption,File encryptedChangesFile) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		FileInputStream fileInputStream = new FileInputStream(encryptedChangesFile);
		
        byte[] encryptedData = fileInputStream.readAllBytes();

        byte[] decryptedData = encryptionUtils.cryptographicFunctionAsymmetric(decryptionOption,Cipher.DECRYPT_MODE,encryptedData);

        ByteArrayInputStream decryptedStream = new ByteArrayInputStream(decryptedData);
       
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put( "ecore", new EcoreResourceFactoryImpl());
        Resource resource = resourceSet.createResource(URI.createFileURI(new File("").getAbsolutePath() + "/decrypted.ecore"));
        resource.load(decryptedStream,Collections.EMPTY_MAP);
        List<EChange> decryptedChanges = resource.getContents().stream().map(it -> (EChange) it).toList();
        return decryptedChanges;
	}
	 
	
}
