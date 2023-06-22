package tools.vitruv.change.encryption.tests;
import java.io.File;

import java.util.logging.Logger;

import tools.vitruv.change.encryption.impl.symmetric.SymmetricEncryptionSchemeImpl;
import tools.vitruv.change.encryption.impl.asymmetric.AsymmetricEncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.util.CSVWriter;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;


public abstract class TestChangeEncryption {
	public static final Logger LOGGER = Logger.getLogger(TestChangeEncryption.class.getName());
	private final static String csvFileNameAloneAsym = new File("").getAbsolutePath() + File.separator + "AsymmetricEncryptionAlone.csv";
	private final static String csvFileNameTogetherAsym = new File("").getAbsolutePath() + File.separator + "AsymmetricEncryptionTogether.csv";

	
	private final static String csvFileNameAloneSym = new File("").getAbsolutePath() + File.separator + "SymmetricEncryptionAlone.csv";
	private final static String csvFileNameTogetherSym = new File("").getAbsolutePath() + File.separator + "SymmetricEncryptionTogether.csv";

	public static final File FILE = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	public static final SymmetricEncryptionSchemeImpl SYM_ENCRYPTIONSCHEME 
		= new SymmetricEncryptionSchemeImpl(csvFileNameAloneSym,csvFileNameTogetherSym);
	
	public static final AsymmetricEncryptionSchemeImpl ASYM_ENCRYPTIONSCHEME 
		= new AsymmetricEncryptionSchemeImpl(csvFileNameAloneAsym,csvFileNameTogetherAsym);
	
	public static final EChangeCreationUtility CREATIONUTIL= EChangeCreationUtility.getInstance();
	public static final EncryptionUtility ENCRYPTIONUTIL = EncryptionUtility.getInstance();
	public static final CSVWriter WRITER = CSVWriter.getInstance();

	
}
