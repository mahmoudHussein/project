package testpack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

public class OpencsvTester {

	public static void main(String[]args) throws IOException{
		 
		File file = new File("M:\\masters\\semester 4\\web scraping\\output files\\TestFolder");
	        if (!file.exists()) {
	            if (file.mkdir()) {
	                System.out.println("Directory is created!");
	            } else {
	                System.out.println("Failed to create directory!");
	            }
	        }

		CSVWriter writer = new CSVWriter(new FileWriter("M:\\masters\\semester 4\\web scraping\\output files\\TestFolder\\testcsv.csv"), ';');
		     // feed in your array (or convert your data to an array)
		  for(int i = 0; i <= 20; i++){   
		  String[] entries = "first#second#third".split("#");
		     writer.writeNext(entries);
		  }
			 writer.close();
	}
}
