package uk;
import com.jaunt.*;
public class Scrapping {

	public static void main(String[]args) throws InterruptedException{
		 try{
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
		      String year1516href = debatesYear.getElement(0).findEach("<a").getElement(0).getAt("href");
		      System.out.println("the href of year 2015-2016 is " + year1516href);
		      
		      userAgent.visit(year1516href);
		      Elements mpsList = userAgent.doc.findEvery("<ul class=\"mpList\">"); //list with Groups on MP names sorted by letters
		      /*this is where a loop id = "loopB" should start to loop the list of letters with size 24,
		      so we can get each MP that is inside each letter in a third loop id ="loopC"*/
		      //start of id="loopB"
		      Elements mpNameList = mpsList.findEach("<a");
		      //start of id="loopC"
		      String mpPagehref = mpNameList.getElement(0).getAt("href");
		      String mpName = mpNameList.getElement(0).getText(); // we will need to save the name
		      System.out.println("this MPs Name is "+ mpName + " and their page href is "+ mpPagehref);
		      
		     userAgent.visit(mpPagehref); //visiting the MPs page
		     String mpPartyName = userAgent.doc.findEvery("<p>").getElement(1).getText(); //gets the name of the MPs party to which he belongs
		     String getMPSubjectsPageHref = userAgent.doc.findEvery("<p>").getElement(6).findEach("<a").getElement(0).getAt("href"); //gets the href for the subject to which the MP spoke about
		    
		     System.out.println("number of ps "+ userAgent.doc.findEvery("<p>").size());
		     System.out.println("the name of the MP party: "+ mpPartyName);
		     System.out.println("the subjects page href: " + getMPSubjectsPageHref);
		     
		     userAgent.visit(getMPSubjectsPageHref);
		     Elements mpSubjectsList =  userAgent.doc.findEvery("p class=\"mpLink\"").findEach("<a"); //list of all subjects that the mp spoke of
		     System.out.println("the number of subjects: " + mpSubjectsList.size());
		     /*there should be another loop here to loop through the list and save the subject material
		      *and text id="loopD"
		      * */
		     //Start of loopD
		     String mpSubjectText = mpSubjectsList.getElement(0).getText();
		     String mpSubjectLink = mpSubjectsList.getElement(0).getAt("href");
		     System.out.println(mpSubjectLink);
		     System.out.println(mpSubjectText);
		     userAgent.visit(mpSubjectLink);
		     Elements debateText = userAgent.doc.findEvery("<p>");
		     String opinionHolder;
		     int bTagSize;
		     System.out.println(debateText.size());
		    for(Element debate: debateText){
		    	String[] mpNameSplit = mpName.split(",");
		        String mpLastName = mpNameSplit[0];
		    	bTagSize = debate.findEvery("<b>").size();
		    	if(bTagSize == 2){
		    	opinionHolder = debate.findEvery("<b>").getElement(1).getText();
		    	if(opinionHolder.contains(mpLastName)){
		    	System.out.println("the opinion holder is " + opinionHolder + " and they said "+ debate.getText());
		    	}
		    	}else if(bTagSize == 1){
		    		opinionHolder = debate.findEvery("<b>").getElement(0).getText();
		    		if(opinionHolder.contains(mpLastName)){
				    	System.out.println("the opinion holder is " + opinionHolder + " and they said "+ debate.getText());
				    	}
		    	}else{
		    		System.out.println(debate.getText());	
		    	}
		    	
		    	//System.out.println(debate.getText());
		    	Thread.sleep(5000);
		    }
		    //*/
		     
		     //end loopD
		     
		      //end of id="loopC"
		      //end of id="loopB"
		      
		      //this is where the loop should end id="loopA"
		    }
		    catch(JauntException e){   //if title element isn't found or HTTP/connection error occurs, handle JauntException.
		      System.err.println(e);          
		    }

	}
}
