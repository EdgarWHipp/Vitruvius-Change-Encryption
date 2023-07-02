package tools.vitruv.change.encryption.tests;
import java.io.File;

import java.util.logging.Logger;
import java.util.stream.IntStream;

import tools.vitruv.change.encryption.impl.symmetric.SymmetricEncryptionSchemeImpl;
import tools.vitruv.change.encryption.impl.asymmetric.AsymmetricEncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.util.CSVWriter;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;

/**
 * This class contains a variety of variables that are used throughout the testing process.
 */
public abstract class TestChangeEncryption {
	public static final Logger LOGGER = Logger.getLogger(TestChangeEncryption.class.getName());
	private static final String csvFileNameAloneAsym = new File("").getAbsolutePath() + File.separator + "AsymmetricEncryptionAlone.csv";
	private static final String csvFileNameTogetherAsym = new File("").getAbsolutePath() + File.separator + "AsymmetricEncryptionTogether.csv";

	
	private static final String csvFileNameAloneSym = new File("").getAbsolutePath() + File.separator + "SymmetricEncryptionAlone.csv";
	private static final String csvFileNameTogetherSym = new File("").getAbsolutePath() + File.separator + "SymmetricEncryptionTogether.csv";

	public static final File FILE = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	public static final SymmetricEncryptionSchemeImpl SYM_ENCRYPTIONSCHEME 
		= new SymmetricEncryptionSchemeImpl(csvFileNameAloneSym,csvFileNameTogetherSym);
	
	public static final AsymmetricEncryptionSchemeImpl ASYM_ENCRYPTIONSCHEME 
		= new AsymmetricEncryptionSchemeImpl(csvFileNameAloneAsym,csvFileNameTogetherAsym);
	
	public static final EChangeCreationUtility CREATIONUTIL= EChangeCreationUtility.getInstance();
	public static final EncryptionUtility ENCRYPTIONUTIL = EncryptionUtility.getInstance();
	public static final CSVWriter WRITER = CSVWriter.getInstance();
	// ATTRIBUTE BASED VARIABLES (CPABE)
	static public final String publicKeyPath = new File("").getAbsolutePath() +"/public_key";
	static public final String masterKeyPath = new File("").getAbsolutePath() +"/master_key";
	static public final String privateKeyPath = new File("").getAbsolutePath() +"/private_key";
	
	static public final String inputFile = new File("").getAbsolutePath() +"/input.pdf";
	static public final String encryptedFilePath = new File("").getAbsolutePath() +"/encrypted.pdf";
	static public final String decryptedFilePath = new File("").getAbsolutePath() +"/decrypted.pdf";
	static public final String curveParamsFilePath = new File("").getAbsolutePath() + "/nl.sudohenk.kpabe.gpswabe.curves";
	public static File[] generateEncryptionFiles(int amount) {
		File[] setOfFiles = IntStream.range(0, amount)
		        .mapToObj(i -> new File("encryptionFile_" + i))
		        .toArray(File[]::new);

		return setOfFiles;
	}
	public static File[] generateDecryptionFiles(int amount) {
		File[] setOfFiles = IntStream.range(0, amount)
		        .mapToObj(i -> new File("decryptionFile_" + i))
		        .toArray(File[]::new);

		return setOfFiles;
	}
	public static void deleteFiles() {
		IntStream.range(0, 1000).forEach(x -> new File("encryptionFile_"+x).delete());
		
		IntStream.range(0, 1000).forEach(x -> new File("decryptionFile_"+x).delete());
	}
	
}
