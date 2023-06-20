package tools.vitruv.change.encryption.tests;
import java.io.File;
import java.util.logging.Logger;

import tools.vitruv.change.encryption.impl.AsymmetricEncryptionSchemeImpl;
import tools.vitruv.change.encryption.impl.EncryptionSchemeImpl;
import tools.vitruv.change.encryption.tests.symmetric.TestEncryptChangesSymmetricallyAlone;
import tools.vitruv.change.encryption.tests.util.CSVWriter;
import tools.vitruv.change.encryption.tests.util.EChangeCreationUtility;
import tools.vitruv.change.encryption.tests.util.EncryptionUtility;

public abstract class TestChangeEncryption {
	private static final Logger LOGGER = Logger.getLogger(TestChangeEncryption.class.getName());
	public static final File FILE = new File(new File("").getAbsolutePath() +"/encrypted_changes");
	public static final EncryptionSchemeImpl ENCRYPTIONSCHEME = new EncryptionSchemeImpl();
	public static final AsymmetricEncryptionSchemeImpl ASYM_ENCRYPTIONSCHEME = new AsymmetricEncryptionSchemeImpl();
	public static final EChangeCreationUtility CREATIONUTIL= EChangeCreationUtility.getInstance();
	public static final EncryptionUtility ENCRYPTIONUTIL = EncryptionUtility.getInstance();
	public static final CSVWriter WRITER = CSVWriter.getInstance();

	
}
