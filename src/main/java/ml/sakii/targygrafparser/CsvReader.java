package ml.sakii.targygrafparser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class CsvReader {
	public static String[][] readCsv(String path) {
		
		ArrayList<String[]> lines = new ArrayList<>();
		int maxlength=0;
		try(CSVReader reader = new CSVReader(new FileReader(path)))/*(BufferedReader reader = new BufferedReader(new FileReader(path)))*/{
		     
			/*String line = reader.readLine();
			while (line != null) {
				//System.out.println(line);
				String[] cells = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				if(cells.length>maxlength) {
					maxlength=cells.length;
				}
				lines.add(cells);
				line = reader.readLine();
			}
			reader.close();*/
		     
		     String [] nextLine;
		     while ((nextLine = reader.readNext()) != null) {
		        lines.add(nextLine);
		        if(nextLine.length>maxlength) {
		        	maxlength=nextLine.length;
		        }
		     }
			
			
		} catch (IOException | CsvValidationException e) {
			e.printStackTrace();
			return null;
		}
		
		
		String[][] result = new String[lines.size()][maxlength];
		for(int i=0;i<lines.size();i++) {
			String[] line = lines.get(i);
			for(int j=0;j<maxlength;j++) {
				if(line.length<=j || line[j] == null) {
					result[i][j] = "<html></html>";
				}else {
					result[i][j] = "<html>"+lines.get(i)[j].replace("*", "").replace("\n", "<br>")+"</html>";
				}
			}
		}
		
		return result;
	}
	
	public static String WriteCsv(String path, Iterable<String[]> lines) {
		try(CSVWriter writer = new CSVWriter(new FileWriter(path))){
			writer.writeAll(lines);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		
		
		
	}
}
