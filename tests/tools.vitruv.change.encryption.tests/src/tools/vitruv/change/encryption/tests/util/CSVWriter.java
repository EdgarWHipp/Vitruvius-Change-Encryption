package tools.vitruv.change.encryption.tests.util;

import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.encryption.tests.TestChangeEncryption;
import tools.vitruv.change.encryption.tests.asymmetric.EChange;
import tools.vitruv.change.encryption.tests.symmetric.Pair;

public class CSVWriter {
	private static CSVWriter instance;
	private CSVWriter() {}
	public static CSVWriter getInstance() {
		if (instance== null) {
			return new CSVWriter();
		}else {
			return instance;
		}
	}
	
	
	private void writeToCsv(String change,Map<String,Pair<String,long[]>> map, String csvFile) throws IOException {
		
	
			
			Pair<String,long[]> results = (Pair<String,long[]>) map.get("result");
			String csvLine = new String(change+","+results.getFirst()+","+results.getSecond()[0]+","+results.getSecond()[1]+","+results.getSecond()[2]+"\n");
			
			 
			 try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
		            writer.write(csvLine);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		     
		}
	
}
