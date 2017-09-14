package wikiEngine;

import java.util.HashMap;

public class Parser {
	//Return stem word for given string temp
	public static String stemming(String temp){
		PortStemmer s = new PortStemmer();
		s.add(temp.toCharArray(),temp.length());
		String str=s.stem();
		return str;
	}
	
	// Add the string into map along with posting list and docID
	public static void addIntoMap(String str){
		if(!UserHandler.gmap.containsKey(str))
     	{
			HashMap<String,Fields> mp=new HashMap<String,Fields>();
     		UserHandler.gmap.put(str, mp);
     	}
     	
		HashMap<String,Fields> mpStr=new HashMap<String,Fields>();
    	mpStr=UserHandler.gmap.get(str);
    	if(!mpStr.containsKey(UserHandler.id))
     	{
     		mpStr.put(UserHandler.id, new Fields());
     	}
	}
	
	// For processing title
	public static void processTitle(String temp)
	{
		temp=temp.toLowerCase().trim();
        if(temp.length()<1)
        	return;
        
        String str=stemming(temp);
        if(str.length()<1)
        	return;
    	addIntoMap(str);
    	HashMap<String,Fields> mpStr=UserHandler.gmap.get(str);
     	mpStr.get(UserHandler.id).title++;
		
    }
	
	// For processing text
	public static void processText(String temp) 
    {
		temp=temp.toLowerCase().trim();
	    		
    	if(temp=="" || temp.length()<=2)
    		return;
    	if(!Main.stopWords.contains(temp))
		{
    		String str=stemming(temp);
    	
    		if(temp.length()<=2)
        		return;
    		addIntoMap(str);
    		HashMap<String,Fields> mpStr=UserHandler.gmap.get(str);
         	mpStr.get(UserHandler.id).text++;
    		
		}	
    }
	
	// For processing infobox
	public static void processInfoBox(String temp) 
    {
		temp=temp.toLowerCase().trim();
	    		
    	if(temp=="" || temp.length()<=2)
    		return;
    	if(!Main.stopWords.contains(temp))
		{
    		String str=stemming(temp);
    	
    		if(temp.length()<=2)
        		return;
    		
    		addIntoMap(str);
    		HashMap<String,Fields> mpStr=UserHandler.gmap.get(str);
         	mpStr.get(UserHandler.id).infobox++;	
		}	
    }
	
	// For processing category
	public static void processCategory(String temp) 
    {
		temp=temp.toLowerCase().trim();
	    		
    	if(temp=="" || temp.length()<=2)
    		return;
    	if(!Main.stopWords.contains(temp))
		{
    		String str=stemming(temp);
    		if(temp.length()<=2)
        		return;
    		
    		addIntoMap(str);
    		HashMap<String,Fields> mpStr=UserHandler.gmap.get(str);
         	mpStr.get(UserHandler.id).category++;	
		}	
    }
	
	// For processing references
	public static void processReferences(String temp) 
    {		
		temp=temp.toLowerCase().trim();
	    		
    	if(temp=="" || temp.length()<=2)
    		return;
    	if(!Main.stopWords.contains(temp))
		{
    		String str=stemming(temp);
    	
    		if(temp.length()<=2)
        		return;
    		
    		addIntoMap(str);
    		HashMap<String,Fields> mpStr=UserHandler.gmap.get(str);
         	mpStr.get(UserHandler.id).reference++;	
		}	
    }
	
	// For processing links
	public static void processLinks(String temp) 
    {
		temp=temp.toLowerCase().trim();
	    		
    	if(temp=="" || temp.length()<=2)
    		return;
    	if(!Main.stopWords.contains(temp))
		{
    		String str=stemming(temp);
    		if(temp.length()<=2)
        		return;
    		addIntoMap(str);
    		HashMap<String,Fields> mpStr=UserHandler.gmap.get(str);
         	mpStr.get(UserHandler.id).extlink++;	
		}	
    }
	
	// For processing body which identifies text as infobox, external link, references, category and remove the unnecessary data.
	public static void processData(StringBuilder data)
    {
    	try{
    		int len=data.length();
    		int brackets;
    		char c;
    		boolean bextlink=false;
	    	StringBuilder text=new StringBuilder();
	    	StringBuilder ibox=new StringBuilder();
	    	StringBuilder category=new StringBuilder();
	    	StringBuilder extLink=new StringBuilder();
	    	StringBuilder reference=new StringBuilder();
			text.setLength(0);
	    	for(int i=0;i<len;i++)
	    	{
	    		if(i+1<len  && data.charAt(i)=='{' && data.charAt(i+1)=='{')
	    		{	
	    			brackets=2;
	    			if(i+9<len && data.substring(i+2,i+9).equalsIgnoreCase("infobox"))
	    			{
	    				i=i+9;
	    	        	while(i<len)
	        			{
	        				if(brackets==0){
	        					break;
	        				}
	        				c=data.charAt(i);
	        				if(c=='{'){
	        					brackets++;
	        				}
	        				else if(c=='}')
	        					brackets--;
	        	
	        				else{
	        					if((c>='a' && c<='z')||(c>='A' && c<='Z'))
	        					{
	        						ibox.append(c);
	            				}
	        					else
	        					{
	        						if(ibox.length()>1){
	        							processInfoBox(ibox.toString());
	        						}
	        						ibox.setLength(0);
	        					}	
	        				}
	        				i++;
	        			}
	        			if(ibox.length()>1){
	        				processInfoBox(ibox.toString());
	        			}
	        			ibox.setLength(0);
	    			} 
	    			else if(i+6<len && data.substring(i+2, i+6).equalsIgnoreCase("cite"))
	    			{		    			
		    			i=i+6;
		    			while(i<len){
		    				if(brackets==0){
	        					break;
	        				}
		    				c=data.charAt(i);
		    				if(c=='{'){
		    					brackets++;
		    				}
		    				if(c=='}'){
		    					brackets--;
		    				}
		    				i++;
		    			}
		    		}
	    			else if(i+10<len && data.substring(i+2, i+10).equalsIgnoreCase("citation"))
	    			{		    			
		    			i=i+10;
		    			while(i<len){
		    				if(brackets==0){
	        					break;
	        				}
		    				c=data.charAt(i);
		    				if(c=='{'){
		    					brackets++;
		    				}
		    				if(c=='}'){
		    					brackets--;
		    				}
		    				i++;
		    			}
		    		}
	    			else if(i+7<len && data.substring(i+2, i+7).equalsIgnoreCase("coord"))
	    			{		    			
		    			i=i+7;
		    			while(i<len){
		    				if(brackets==0){
	        					break;
	        				}
		    				c=data.charAt(i);
		    				if(c=='{'){
		    					brackets++;
		    				}
		    				if(c=='}'){
		    					brackets--;
		    				}
		    				i++;
		    			}
		    		}
	    			else if(i+8<len && data.substring(i+2, i+8).equalsIgnoreCase("geobox"))
	    			{		    			
		    			i=i+8;
		    			while(i<len){
		    				if(brackets==0){
	        					break;
	        				}
		    				c=data.charAt(i);
		    				if(c=='{'){
		    					brackets++;
		    				}
		    				if(c=='}'){
		    					brackets--;
		    				}
		    				i++;
		    			}
		    		}
	    			else if(i+4<len && data.substring(i+2, i+4).equalsIgnoreCase("gr"))
	    			{		    			
		    			i=i+4;
		    			while(i<len){
		    				if(brackets==0){
	        					break;
	        				}
		    				c=data.charAt(i);
		    				if(c=='{'){
		    					brackets++;
		    				}
		    				if(c=='}'){
		    					brackets--;
		    				}
		    				i++;
		    			}
		    		}	    			
	    		}
	    		else if(i+1<len &&  data.charAt(i)=='[' && data.charAt(i+1) == '[')
	    		{
	    			brackets=2;
	    			if(i+11<len && data.substring(i+2,i+11).equalsIgnoreCase("category:")){
		    			i=i+11;
		    			while(i<len )
		    			{
		    				if(brackets==0){
		    					break;
		    				}
		    				c=data.charAt(i);
		    				if(c=='['){
		    					brackets++;
		    				}
		    				else if(c==']'){
		    					brackets--;
		    				}
		    				else{
		    					if((c>='a' && c<='z')||(c>='A' && c<='Z')){	
		    	    				category.append(c);
		    	    			}
		    					else{
		    						if(category.length()>1){
		    							processCategory(category.toString());
		    						}
		    						category.setLength(0);
		    	    			}
		    				}
		    				i++;
		    			}
		    			if(category.length()>1){
		    				processCategory(category.toString());
		    			}
		    			category.setLength(0);
	    			}
	    			else if(i+7<len && data.substring(i+2,i+7).equalsIgnoreCase("file:")){
		    			i=i+7;
		    			while(i<len)
		    			{
		    				if(brackets==0){
		    					break;
		    				}
		    				c=data.charAt(i);
		    				if(c=='['){
		    					brackets++;
		    				}
		    				else if(c==']'){
		    					brackets--;
		    				}
		    				i++;
		    			}
	    			}
	    			else if(i+8<len && data.substring(i+2,i+8).equalsIgnoreCase("image:")){
		    			i=i+8;
		    			while(i<len)
		    			{
		    				if(brackets==0){
		    					break;
		    				}
		    				c=data.charAt(i);
		    				if(c=='['){
		    					brackets++;
		    				}
		    				else if(c==']'){
		    					brackets--;
		    				}
		    				i++;
		    			}
	    			}
	    		}
	    		else if(data.charAt(i)=='<') 
	    		{
	    			if (i+4<len && data.substring(i+1,i+4).equals("!--")) 
	    			{
	    				i=i+4;
	    				int ind = data.indexOf("-->",i);
	    				if(ind+2<len && ind>0)
	    					 i=ind+2;
	    			}
	    			else if(i+5<len && data.substring(i+1,i+5).equalsIgnoreCase("ref>"))
                    {
                     	 i=i+5;
                     	 int ind = data.indexOf("</ref>" , i+1);
                     	 if(ind+5<len && ind>0)
                     		 i=ind+6;
				    }
	    			
	    		}
	    		else if((i+14<len && data.substring(i,i+14).equalsIgnoreCase("==references==")) || i+16<len && data.substring(i,i+16).equalsIgnoreCase("== references ==")){
	    			
	    			if(data.charAt(i+13)=='='){
	    				i=i+14;
	    			}
	    			else{
	    				i=i+16;
	    			}
	    			while(i<len){
	    				c=data.charAt(i);
    					if((i+1<len && data.substring(i,i+2).equalsIgnoreCase("==")) ||(i+11<len && data.substring(i,i+10).equalsIgnoreCase("[[category"))){
    						i--;
    						break;
    					}
    					if(i+4<len && data.substring(i,i+4).equalsIgnoreCase("http")){
    		    			i=i+4;
    		    			while(i<len && data.charAt(i)!=' '){
    		    				i++;
    		    			}
    		    		}
    					else if((c>='a' && c<='z') || (c>='A' && c<='Z')){
                            reference.append(c);
    					}
                        else
                        {	
                        	if(reference.length()>1){
    	    					processReferences(reference.toString());
    	    				}
                        	reference.setLength(0);
                        }
    					i++;
    				}
    				if(reference.length()>1){
    					processReferences(reference.toString());
    				}
    				reference.setLength(0);
	    		}
	    		else if((i+18<len && data.substring(i,i+18).equalsIgnoreCase("==External Links==")) || i+20<len && data.substring(i,i+20).equalsIgnoreCase("== External Links ==")){
	    			//System.out.println("external links:");
	    			if(data.charAt(i+17)=='='){
	    				i=i+17;
	    			}
	    			else{
	    				i=i+19;
	    			}
	    			bextlink=true;
	    		}
	    		else if(data.charAt(i)=='*' && bextlink){
	    			while(i<len && data.charAt(i)!='['){
	    				i++;
	    			}
	    			if(i<len && data.charAt(i)=='['){
	    				while(i<len && data.charAt(i)!=' '){
	    					i++;
	    				}
	    				extLink.setLength(0);
	    				while(i<len){
	    					
	    					if(data.charAt(i)==']'){
	    						break;
	    					}
	    					c=data.charAt(i);
	    					if((c>='a' && c<='z') || (c>='A' && c<='Z')){
	                            extLink.append(c);
	    					}
	                        else
	                        {	
	                        	if(extLink.length()>1){
	    	    					processLinks(extLink.toString());
	    	    				}
	                        	extLink.setLength(0);
	                        }
	    					i++;
	    				}
	    				if(extLink.length()>1){
	    					processLinks(extLink.toString());
	    				}
	    				extLink.setLength(0);
	    			}
	    		}
	    		else{
	    			if((data.charAt(i)>='A' && data.charAt(i)<='Z') || (data.charAt(i)>='a' && data.charAt(i)<='z') ){
	    				text.append(data.charAt(i));
	    			}
	    			else{
	    				if(text.length()>1){
	    					processText(text.toString());
	    				}
	    				text.setLength(0);
	    			}
	    		}
	    	}
	    }
	    catch(Exception e){
	    	e.printStackTrace();
	    }
    
    }
}