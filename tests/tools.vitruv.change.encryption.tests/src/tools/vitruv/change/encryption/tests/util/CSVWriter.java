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
		
	}
			
			Pair<String,long[]> results = (Pair<String,long[]>) map.get("result");
			String csvLine = new String(change+","+results.getFirst()+","+results.getSecond()[0]+","+results.getSecond()[1]+","+results.getSecond()[2]+"\n");
			
			 
			 try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
		            writer.write(csvLine);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		     
		}
	public void testChangeAlone(EChange change, String csvFile) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Map<String,Pair<String,long[]>> mainMap = new HashMap<String,Pair<String,long[]>>();
		long[][] timeArray = new long[10][3];
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsAsymmetric()) {
			for (int i=0;i<10;i++) {
				

			    long startTime = System.currentTimeMillis();
				//
			    TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangeAlone(map, change,TestChangeEncryption.FILE);
			    long betweenTime = System.currentTimeMillis();
				EChange decryptedChange = TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangeAlone(map, TestChangeEncryption.FILE);
				//
				long endTime = System.currentTimeMillis();
				
				long totalTime = endTime - startTime;
				long decryptionTime = endTime - betweenTime;
				long encryptionTime = betweenTime - startTime;
				timeArray[i]= new long[] {encryptionTime,decryptionTime,totalTime};
				assertTrue(new EcoreUtil.EqualityHelper().equals(change,decryptedChange)); 
			}
		
			long[] mean=new long[3];
			long sum=0;
			for (int j = 0;j<3;j++) {
				for (int i=0;i<10;i++) {
					sum+=timeArray[i][j];
				}
				mean[j]=sum/3;
			}
		mainMap.put("result",new Pair<String, long[]>((String)map.get("algorithm"),mean));
		this.writeToCsv(change.getClass().getSimpleName(),mainMap,csvFile);
		}
	
	}
	public void testChangesTogether(List<EChange> changes, String csvFile) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Map<String,Pair<String,long[]>> mainMap = new HashMap<String,Pair<String,long[]>>();
		long[][] timeArray = new long[10][3];
		for (Map map : TestChangeEncryption.ENCRYPTIONUTIL.getAllEncryptionMapsSymmetric()) {
			for (int i=0;i<10;i++) {
				

			    long startTime = System.currentTimeMillis();
				//
			    TestChangeEncryption.ENCRYPTIONSCHEME.encryptDeltaChangesTogether(map,changes,TestChangeEncryption.FILE);
			    long betweenTime = System.currentTimeMillis();
				List<EChange> decryptedChanges = TestChangeEncryption.ENCRYPTIONSCHEME.decryptDeltaChangesTogether(map, TestChangeEncryption.FILE);
				//
				long endTime = System.currentTimeMillis();
				
				long totalTime = endTime - startTime;
				long decryptionTime = endTime - betweenTime;
				long encryptionTime = betweenTime - startTime;
				timeArray[i]= new long[] {encryptionTime,decryptionTime,totalTime};
				assertTrue(new EcoreUtil.EqualityHelper().equals(changes,decryptedChanges)); 
			}
		
			long[] mean=new long[3];
			long sum=0;
			for (int j = 0;j<3;j++) {
				for (int i=0;i<10;i++) {
					sum+=timeArray[i][j];
				}
				mean[j]=sum/3;
			}
		mainMap.put("result",new Pair<String, long[]>((String)map.get("algorithm"),mean));
		this.writeToCsv(change.getClass().getSimpleName(),mainMap, csvFile);
		}
}
