package tools.vitruv.change.encryption.tests.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import edu.kit.ipd.sdq.commons.util.java.Pair;

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
	
	
	public void writeToCsv(String change,Map<String,Pair<String,long[]>> map, String csvFile) throws IOException {
		
			Pair<String,long[]> results = (Pair<String,long[]>) map.get("result");
			String csvLine = new String(change+","+results.getFirst()+","+results.getSecond()[0]+","+results.getSecond()[1]+","+results.getSecond()[2]+"\n");
			
			 
			 try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
		            writer.write(csvLine);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		     
		}
	
}
