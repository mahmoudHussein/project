package ukv2;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import com.jaunt.*;

public class DataScrappingV1Test {

	public static void csvWriter(String[] x, String spokenDate, String fileName, String subjectText, String actualYear) throws IOException{
		String cleanFileName = fileName.replaceAll("[//\\+.^:,]",""); //remove some special characters to create the file names
		String noSpaceCleanName = cleanFileName.replaceAll(" ", "_"); //removes the spaces from the file name and replace them with underscore
		String[] nameArray= x[0].split(","); //gets the name from the polParty array, splits the name at the "," to separate the first and last names of the MPs
		String noSpaceCleanDate = spokenDate.replaceAll(" ", "_"); //removes the spaces from the date and replace them with underscore
		String name = nameArray[1]+ " "+ nameArray[0]; //rearrange the name of the MP to have first name then last name 
		String cleanName = name.trim();// removes white spaces from the MPs name
		File file = new File("../Data/"+actualYear+"/" + cleanName); // Creates a new folder in our server for that MP in the year specified in case it didn't exist
        if (!file.exists()) {
            if (file.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
        FileWriter mFileWriter;
        System.out.println(cleanFileName);
        /*creates a new file as a .txt extension to save the text that the MP said on a certain subject depending if it has a date or not
         * 
         * Case 1 it has a date, then the file is saved [MPs clean name]/[Subject clean name]_[Subject clean date].txt 
         * 
         * Case 2 it doesn't have a date, then the file is saved [MPs clean name]/[Subject clean name].txt*/
        
        if(!spokenDate.equals("")){ 
		 mFileWriter = new FileWriter("../Data/" + actualYear+"/"+ cleanName + "/"+noSpaceCleanName+"_"+noSpaceCleanDate + ".txt", true);
		}else{
			 mFileWriter = new FileWriter("../Data/" +actualYear+"/"+ cleanName + "/"+noSpaceCleanName + ".txt", true);
		}
		BufferedWriter   writer = new BufferedWriter (mFileWriter);   
		
		writer.write(subjectText); //where the text is written in the file
	 
		writer.close();
	}
	
	public static void main(String[]args) throws InterruptedException, IOException{
		 try{
		     ArrayList<String>politians = new ArrayList<>();
		     ArrayList<String>parties = new ArrayList<>();
			 String[] polParty = new String[2];
			 UserAgent userAgent = new UserAgent();                       //create new userAgent (headless browser).
		     int year = 2011;
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
		      
		      /*this is where a loop that loop over the hyperlinks for each year should happen
		       * Element 0 = 2016-2015
		       * Element 1 = 2015-2014
		       * Element 2 = 2014-2013
		       * Element 3 = 2013-2012
		       * Element 4 = 2012-2011
		       * Element 5 = 2011-2010
		       * */
		      
		      //for(int y = 0; y < debatesYearLinks.size(); y ++){
		      System.out.println("there are "+debatesYearLinks.size()+" years worth of debates in those links");
		      String yearhref = debatesYearLinks.getElement(5).getAt("href");
		      System.out.println("the href of year 2015-2016 is " + yearhref);
		      
		      userAgent.visit(yearhref);
		      Elements mpsList = userAgent.doc.findEvery("<ul class=\"mpList\">"); //list with Groups on MP names sorted by Alphabetical letters
		     
		      /*this is where a loop id = "loopB" should start to loop the list of letters with size 24,
		      so we can get each MP that is inside each letter in a third loop id ="loopC"*/
		      
		      //start of id="loopB"
		     
		     for(int k =19; k < mpsList.size(); k++){  //size 24 for each alphabet
			      //for(int k = 10; k < 15; k++){
			      Elements mpNameList = mpsList.getElement(k).findEach("<a"); //contains an element list of all MPs in the alphabet in the particular index k
			      Thread.sleep(5000);
			      System.out.println(mpNameList.size());
			      
			      //start of id="loopC"
			      /*loops the MPs list in the particular index gotten from loop B above*/
			      for(int i= 0; i < mpNameList.size(); i++){
			      String mpPagehref = mpNameList.getElement(i).getAt("href"); //gets the element in the "i" index of the MPList and get the reference for their page
			      String mpName = mpNameList.getElement(i).getText(); // we will need to save the name
			      politians.add(mpName); //adds the name of the MP to the politians array list 
			      polParty[0] = mpName; //used for csvwriter
			      
			      System.out.println("this MPs Name is "+ mpName + " and their page href is "+ mpPagehref);
			      
			     userAgent.visit(mpPagehref); //visiting the MPs page
			     String mpPartyName = userAgent.doc.findEvery("<p>").getElement(1).getText(); //gets the name of the MPs party to which they belong
			     polParty[1]= mpPartyName.trim(); //used for csvwriter
			     
			     if(!parties.contains(mpPartyName)){
			    	 parties.add(mpPartyName.trim()); //adds the party to the parties list unless it is already there
			     }
			     
			     //csvWriter(polParty);
			     String getMPSubjectsPageHref = userAgent.doc.findEvery("<p>").getElement(6).findEach("<a").getElement(0).getAt("href"); //gets the href for the subject to which the MP spoke about
			    
			     //System.out.println("number of ps "+ userAgent.doc.findEvery("<p>").size());
			     //System.out.println("the name of the MP party: "+ mpPartyName);
			     //System.out.println("the subjects page href: " + getMPSubjectsPageHref);
			     
			     userAgent.visit(getMPSubjectsPageHref); //visits the MPs subjects page
			     
			     Elements mpSubjectsList =  userAgent.doc.findEvery("p class=\"mpLink\"").findEach("<a"); //list of all subjects that the mp spoke of
			     Elements mpSubjectSpokenDateList= userAgent.doc.findEvery("p class=\"mpLink\"");
			     System.out.println("the number of subjects: " + mpSubjectsList.size());
			     
				     /*there should be another loop here to loop through the list and save the subject material
				      *and text id="loopD"
				      * */
				     
				     //Start of loopD
				    for(int j =0; j <mpSubjectsList.size(); j++ ){
				     //for(int j = 12 ; j <mpSubjectsList.size(); j++ ){
				     String mpSubjectSpokenDate =  mpSubjectSpokenDateList.getElement(j).getText().trim(); //date is included inside a <p> tag
				     String mpSubjectText = mpSubjectsList.getElement(j).getText(); //text is included inside the <a> tags
				     System.out.println(mpSubjectText);
				     System.out.println(mpSubjectSpokenDate);
				     Thread.sleep(5000);
				     
				     String mpSubjectLink = mpSubjectsList.getElement(j).getAt("href"); //gets the href of the subject that is in that "j" index from the mpsubject list
				     System.out.println(mpSubjectLink);
				     
				     userAgent.visit(mpSubjectLink);
				     Elements debateText = userAgent.doc.findEvery("<p>");
				     String opinionHolder;
				     int bTagSize;
				     String[] mpNameSplit = mpName.split(",");
				     String mpLastName = mpNameSplit[0];
				     String primaryName = mpLastName;
				     System.out.println(debateText.size());
				     for(Element debate: debateText){
				    	bTagSize = debate.findEvery("<b>").size();
				    	/*The <b> tags is where the name of the speaker is held, there are three scenarios to this situation
				    	 * Scenario 1 : BTagSize = 2 that means that it contains a name and extra information about the MP ex. "<b><b>Ms Diane Abbott</b> (Hackney North and Stoke Newington) (Lab):</b>"
				    	 * in that scenario we are interested in the <b> at element 1 which is "<b>Ms Diane Abbott</b>"
				    	 * 
				    	 * Scenario 2: BTagSize = 1 that means that it contains only the last name of the MP ex."<b>Ms Abbott:</b>"
				    	 * 
				    	 * Scenario 3: there is no BTagSize as there is no speaker information for that paragraph in which we consider the previous speaker as the owner of that paragraph as well using 
				    	 * the comparison of the opinionHolder variable with the Primary name variable, as long as they are equal then its the same speaker, once a new MP is found,
				    	 * The primary name is changed.*/
				    	if(bTagSize == 2){
				    	opinionHolder = debate.findEvery("<b>").getElement(1).getText();
				    	if(opinionHolder.contains(mpLastName)){
				    		String text = debate.getText()+ "|";
				    		primaryName = mpLastName;
				    		int tempyear = year - 1;
			    			String actualYear = year+"_"+ tempyear +"";
				    		csvWriter(polParty, mpSubjectSpokenDate, mpSubjectText,text, actualYear);
				    		System.out.println("the opinion holder is " + opinionHolder + " and they said "+ debate.getText());
				    	}else if(!opinionHolder.matches(".*\\d+.*")){
				    		primaryName = opinionHolder; //Changes the Primary name to the new MPs name
				    		//System.out.println("Primary name now is " + primaryName);
				    	}
				    	}else if(bTagSize == 1){
				    		opinionHolder = debate.findEvery("<b>").getElement(0).getText();
				    		if(opinionHolder.contains(mpLastName)){
				    			String text = debate.getText() + "|";
				    			primaryName = mpLastName;
				    			int tempyear = year - 1;
				    			String actualYear = year+"_"+ tempyear +"";
				    			csvWriter(polParty, mpSubjectSpokenDate, mpSubjectText,text, actualYear);
				    			System.out.println("the opinion holder is " + opinionHolder + " and they said "+ debate.getText());
						    	}else if(!opinionHolder.matches(".*\\d+.*")){
						    		primaryName = opinionHolder; //Changes the Primary name to the new MPs name
						    	//	System.out.println("if b tage = 1 " + "Primary name now is " + primaryName);
						    	}
				    	}else if(bTagSize == 0){
				    		/* This part is to ensure that any continued text belonging to our MP without having his/her name
				    		 * existing in the bTags are still recorded in their records by ensuring that our Mp is the last
				    		 * saved name in the variable PrimaryName.
				    		 * */
				    		if(primaryName.equals(mpLastName)){
				    			String text = debate.getText() + "|";
				    			int tempyear = year - 1;
				    			String actualYear = year+"_"+ tempyear +""; 
				    			csvWriter(polParty, mpSubjectSpokenDate, mpSubjectText,text, actualYear);
				    			System.out.println("the opinion holder is " + primaryName + " and they said "+ debate.getText());
				    		}
				    		
				    	}
			    	
			    	//System.out.println(debate.getText());
			    	//Thread.sleep(100);
			    }
			     }
		     
		     
		     
		     //end loopD
		     Thread.sleep(3000);
		      }//end of id="loopC"
		     //break;
		      }//end of id="loopB"
		      System.out.println("done");
		      //year --;
		      Thread.sleep(5000);
		     // }//this is where the loop should end id="loopA"
		    }
		    catch(JauntException e){   //if title element isn't found or HTTP/connection error occurs, handle JauntException.
		      System.err.println(e);          
		    }
		 
	}
	
}
