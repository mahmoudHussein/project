package uk;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.jaunt.*;
import com.opencsv.CSVWriter;
public class StatisticsScrapping {

	public static void csvWriter(String[] x, String fileName, String subjectDate) throws IOException{
		String cleanFileName = fileName.replaceAll("[\\/\\+.^:,']",""); //remove some special characters to create the file names
		//FileWriter mFileWriter = new FileWriter("M:\\masters\\semester 4\\web scraping\\output files\\TopicStats1\\" + cleanFileName + ".csv", true);
		FileWriter mFileWriter = new FileWriter("/home/mosman/ThesisProject/ProjectData/TopicStats/2015_2014/" + cleanFileName + ".csv", true);
		CSVWriter  writer = new CSVWriter(mFileWriter, ',');

		//CSVWriter writer = new CSVWriter(new FileWriter("M:\\masters\\semester 4\\web scraping\\output files\\PoliticianToParties.csv"), ',');
	     // feed in your array (or convert your data to an array)   
		String[] nameArray= x[0].split(",");
		String name = nameArray[1]+ " "+ nameArray[0];
		String partyName = x[1];
		String[] entries = new String[3];
		entries[0] = name.replaceAll("[\\/\\+.^:,']","");
		entries[1] = partyName.replaceAll("[\\/\\+.^:,']","");
		entries[2] = subjectDate.replaceAll("[\\/\\+.^:,']","");
		writer.writeNext(entries);
	 
		writer.close();
	}
	public static void main(String[]args) throws InterruptedException, IOException{
		 try{
		     ArrayList<String>politians = new ArrayList<>();
		     ArrayList<String>parties = new ArrayList<>();
			 String[] polParty = new String[2];
			 UserAgent userAgent = new UserAgent();                       //create new userAgent (headless browser).
		     
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
		      System.out.println("there are "+debatesYearLinks.size()+" years worth of debates in those links");
		      String year1516href = debatesYearLinks.getElement(1).getAt("href"); //from 0 to 9 for the 10 years
		      System.out.println("the href of year 2015-2016 is " + year1516href);
		      
		      userAgent.visit(year1516href);
		      Elements mpsList = userAgent.doc.findEvery("<ul class=\"mpList\">"); //list with Groups on MP names sorted by letters
		      /*this is where a loop id = "loopB" should start to loop the list of letters with size 24,
		      so we can get each MP that is inside each letter in a third loop id ="loopC"*/
		      //start of id="loopB"
		     
		      //for(int k = 12; k < mpsList.size(); k++){  //size 24 for each alphabet
		      for(int k = 0; k < 12; k++){
		      Elements mpNameList = mpsList.getElement(k).findEach("<a");
		      Thread.sleep(2000);
		      //start of id="loopC"
		      System.out.println(mpNameList.size());
		      for(int i= 0; i < mpNameList.size(); i++){
		      //for(int i= 0; i < 14; i++){
		      String mpPagehref = mpNameList.getElement(i).getAt("href");
		      String mpName = mpNameList.getElement(i).getText(); // we will need to save the name
		      politians.add(mpName);
		      polParty[0] = mpName; //used for csvwriter
		      System.out.println("this MPs Name is "+ mpName + " and their page href is "+ mpPagehref);
		      
		     userAgent.visit(mpPagehref); //visiting the MPs page
		     String mpPartyName = userAgent.doc.findEvery("<p>").getElement(1).getText(); //gets the name of the MPs party to which he belongs
		     polParty[1]= mpPartyName.trim(); //used for csvwriter
		     if(!parties.contains(mpPartyName)){
		    	 parties.add(mpPartyName.trim());
		     }
		     
		     //csvWriter(polParty);
		     String getMPSubjectsPageHref = userAgent.doc.findEvery("<p>").getElement(6).findEach("<a").getElement(0).getAt("href"); //gets the href for the subject to which the MP spoke about
		    
		     //System.out.println("number of ps "+ userAgent.doc.findEvery("<p>").size());
		     //System.out.println("the name of the MP party: "+ mpPartyName);
		     //System.out.println("the subjects page href: " + getMPSubjectsPageHref);
		     
		     userAgent.visit(getMPSubjectsPageHref);
		     Elements mpSubjectsList =  userAgent.doc.findEvery("p class=\"mpLink\"").findEach("<a"); //list of all subjects that the mp spoke of
		     Elements mpSubjectSpokenDateList= userAgent.doc.findEvery("p class=\"mpLink\"");
		     System.out.println("the number of subjects: " + mpSubjectsList.size());
		     /*there should be another loop here to loop through the list and save the subject material
		      *and text id="loopD"
		      * */
		     //Start of loopD
		     for(int j =0; j <mpSubjectsList.size(); j++ ){
		     String mpSubjectSpokenDate =  mpSubjectSpokenDateList.getElement(j).getText().trim();
		     String mpSubjectText = mpSubjectsList.getElement(j).getText();
		     System.out.println("mpSubjectText= " + mpSubjectText);
		     System.out.println(mpSubjectSpokenDate);
		     csvWriter(polParty, mpSubjectText, mpSubjectSpokenDate);
		     Thread.sleep(2000);
		     }
		     		     
		     
		     //end loopD
		     //Thread.sleep(5000);
		      }//end of id="loopC"
		     //break;
		      }//end of id="loopB"
		      
		      //this is where the loop should end id="loopA"
		    }
		    catch(JauntException e){   //if title element isn't found or HTTP/connection error occurs, handle JauntException.
		      System.err.println(e);          
		    }

	}
}
