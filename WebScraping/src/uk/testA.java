package uk;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.jaunt.*;
public class testA {

	public static void csvWriter(String[] x, String spokenDate, String fileName, String subjectText) throws IOException{
		String cleanFileName = fileName.replaceAll("[//\\+.^:,]",""); //remove some special characters to create the file names
		String noSpace = cleanFileName.replaceAll(" ", "_");
		String[] nameArray= x[0].split(",");
		String name = nameArray[1]+ " "+ nameArray[0];
		String cleanName = name.trim();
		File file = new File("../Data/"+ cleanName);
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
        FileWriter mFileWriter;
        System.out.println(cleanFileName);
        if(!spokenDate.equals("")){
		 mFileWriter = new FileWriter("../Data/" + cleanName + "/"+cleanFileName+ " "+spokenDate + ".txt", true);
		}else{
			 mFileWriter = new FileWriter("../Data/" + cleanName + "/"+cleanFileName + ".txt", true);
		}
		BufferedWriter   writer = new BufferedWriter (mFileWriter);

		//CSVWriter writer = new CSVWriter(new FileWriter("M:\\masters\\semester 4\\web scraping\\output files\\PoliticianToParties.csv"), ',');
	     // feed in your array (or convert your data to an array)   
		
		writer.write(subjectText);
	 
		writer.close();
	}
	
	public static void main(String[]args) throws InterruptedException, IOException{
		 try{
		     ArrayList<String>politians = new ArrayList<>();
		     ArrayList<String>parties = new ArrayList<>();
			 String[] polParty = new String[2];
			 UserAgent userAgent = new UserAgent();                       //create new userAgent (headless browser).
		     int year = 2016;
		      userAgent.visit("http://www.parliament.uk/business/publications/hansard/commons/");                        //visit a url.
		      String title = userAgent.doc.findFirst("<title>").getText(); //get child text of title element.
		      System.out.println("\nUK parl's website title: " + title);    //print the title
		      
		      /*getting the "<div class=\"section-promotion\">" elements from the intial page
		       *getting the 2nd element from the list because that would be "By MP 2006-2016"
		       *used the findFirst to find "<a" to get the href from it using ".getAt()"
		       **/
		      Elements divprom = userAgent.doc.findEvery("<div class=\"section-promotion\">");
		      String mphref = divprom.getElement(1).findFirst("<a").getAt("href");
		      System.out.println("the href is " + mphref);
		      
		      /*now going to use the "mphref" to proceed to the secondary page where the debates exists
		       *sorted by year, then getting all debate links sorted by year and save them in a list,
		       *the list contains 10 elements with 10 hyperLinks from 2006-2016, the list is saved in
		       *"debatesYearsLinks" variable.
		       *
		       *A next step would be to add the process to a for loop that would loop the elements 
		       *and do the same process for each of the items in the list
		       **/
		      
		      userAgent.visit(mphref);
		      Elements debatesYear = userAgent.doc.findEvery("<div id=\"ctl00_ctl00_FormContent_SiteSpecificPlaceholder_PageContent_ctlMainBody_wrapperDiv\" class=\"rte\">");
		      Elements debatesYearLinks = debatesYear.getElement(0).findEach("<a");
		      //this is where the loop would start id="loopA" - in a later stage
		      for(int y = 0; y < debatesYearLinks.size(); y ++){
		      System.out.println("there are "+debatesYearLinks.size()+" years worth of debates in those links");
		      String yearhref = debatesYearLinks.getElement(1).getAt("href");
		      System.out.println("the href of year 2015-2016 is " + yearhref);
		      int tempyear = year - 1;
		      String actualYear = year+"_"+ tempyear +""; 
		      System.out.println( actualYear);
		      year--;
		      
		      }//this is where the loop should end id="loopA"
		    }
		    catch(JauntException e){   //if title element isn't found or HTTP/connection error occurs, handle JauntException.
		      System.err.println(e);          
		    }
		 
	}
	
}
