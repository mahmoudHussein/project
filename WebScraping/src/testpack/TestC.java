package testpack;

import com.jaunt.*;

public class TestC {
	 public static void main(String[] args){
		 //example 1 user agents and calling webpages   
		 try{
		      UserAgent userAgent = new UserAgent();                       //create new userAgent (headless browser).
		      System.out.println("SETTINGS:\n" + userAgent.settings);      //print the userAgent's default settings.
		      userAgent.settings.autoSaveAsHTML = true;                    //change settings to autosave last visited page. 
		      
		      userAgent.visit("http://oracle.com");                        //visit a url.
		      String title = userAgent.doc.findFirst("<title>").getText(); //get child text of title element.
		      System.out.println("\nOracle's website title: " + title);    //print the title

		      userAgent.visit("http://amazon.com");                        //visit another url.
		      title = userAgent.doc.findFirst("<title>").getText();        //get child text of first title element.
		      System.out.println("\nAmazon's website title: " + title);    //print the title
		    }
		    catch(JauntException e){   //if title element isn't found or HTTP/connection error occurs, handle JauntException.
		      System.err.println(e);          
		    }
		   //example 2 
		    try{
		        UserAgent userAgent = new UserAgent();                       
		                                                                      //open HTML from a String.
		        userAgent.openContent("<html><body>WebPage <div>Hobbies:<p>beer<p>skiing</div> Copyright 2013</body></html>");
		        Element body = userAgent.doc.findFirst("<body>");
		        Element div = body.findFirst("<div>");
		     
		        System.out.println("body's text: " + body.getText());         //join child text of body element
		        System.out.println("body's innerText: " + body.innerText()); //join all text within body element
		        System.out.println("div's text: " + div.getText());           //join child of div element
		        System.out.println("div's innerText: " + div.innerText());    //join all text within the div element 
		      }
		      catch(JauntException e){
		        System.err.println(e);
		      }
		    //example 7 loops and nested elements
		    try{
		    	  UserAgent userAgent = new UserAgent();
		    	  userAgent.visit("http://amazon.com");   
		    	 
		    	  Elements tables = userAgent.doc.findEach("<table>");       //find non-nested tables   
		    	  System.out.println("Found " + tables.size() + " tables:");
		    	  for(Element table : tables){                               //iterate through Results
		    	    System.out.println(table.outerHTML() + "\n----\n");      //print each element and its contents
		    	  }   
		    	                                                         
		    	  Elements ols = userAgent.doc.findEach("<table>").findEach("<ol>");//find non-nested ol's within non-nested tables
		    	  System.out.println("Found " + ols.size() + " OLs:");
		    	  for(Element ol : ols){                                     //iterate through Results
		    	    System.out.println(ol.outerHTML() + "\n----\n");         //print each element and its contents
		    	  } 
		    	}
		    	catch(ResponseException e){
		    	  System.out.println(e);
		    	}
		    //example 8 findEvery vs. findEach
		    try{
		    	  UserAgent userAgent = new UserAgent();
		    	  userAgent.visit("http://jaunt-api.com/examples/food.htm");
		    	    
		    	  Elements elements = userAgent.doc.findEvery("<div>");             //find all divs in the document
		    	  System.out.println("Every div: " + elements.size() + " results"); //report number of search results.
		    	    
		    	  elements = userAgent.doc.findEach("<div>");                       //find all non-nested divs
		    	  System.out.println("Each div: " + elements.size() + " results");  //report number of search results.
		    	                                                                    //find non-nested divs within <p class='meat'>
		    	  elements = userAgent.doc.findFirst("<p class=meat>").findEach("<div>");
		    	  System.out.println("Meat search: " + elements.size() + " results");//report number of search results.
		    	}
		    	catch(JauntException e){
		    	  System.err.println(e);
		    	}
		  }
}
