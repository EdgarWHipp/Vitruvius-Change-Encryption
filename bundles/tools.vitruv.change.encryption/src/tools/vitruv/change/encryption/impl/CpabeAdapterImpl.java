package tools.vitruv.change.encryption.impl;

import java.io.File;

import junwei.cpabe.junwei.cpabe.Cpabe;
import tools.vitruv.change.atomic.EChange;

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
	public void encrypt(String attributes,String policy) throws Exception {
		System.out.println("//start to setup");
		instance.setup(publicKeyPath, masterKeyPath);
		System.out.println("//end to setup");

		System.out.println("//start to keygen");
		instance.keygen(publicKeyPath, privateKeyPath, masterKeyPath, attributes);
		System.out.println("//end to keygen");

		System.out.println("//start to enc");
		// create a file with the content of a change;
		String inputFile = "";
		instance.enc(publicKeyPath, policy, inputFile, encryptedFilePath);
		System.out.println("//end to enc");
	}
	public EChange decrypt() throws Exception {
		System.out.println("//start to dec");
		instance.dec(publicKeyPath, privateKeyPath, encryptedFilePath, decryptedFilePath);
		System.out.println("//end to dec");
		return null;
	}
}
