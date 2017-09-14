package wikiEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Main {
	public static Set<String> stopWords = new HashSet<>();
	public static String stopWord[]= {"coord","gr","tr","td","nbsp","http","https","www","a","about","above","across","after","again","against","all","almost","alone","along","already","also","although","always","among","an","and","another","any","anybody","anyone","anything","anywhere","are","area","areas","around","as","ask","asked","asking","asks","at","away","b","back","backed","backing","backs","be","became","because","become","becomes","been","before","began","behind","being","beings","best","better","between","big","both","but","by","c","came","can","cannot","case","cases","certain","certainly","clear","clearly","come","could","d","did","differ","different","differently","do","does","done","down","down","downed","downing","downs","during","e","each","early","either","end","ended","ending","ends","enough","even","evenly","ever","every","everybody","everyone","everything","everywhere","f","face","faces","fact","facts","far","felt","few","find","finds","first","for","four","from","full","fully","further","furthered","furthering","furthers","g","gave","general","generally","get","gets","give","given","gives","go","going","good","goods","got","great","greater","greatest","group","grouped","grouping","groups","h","had","has","have","having","he","her","here","herself","high","high","high","higher","highest","him","himself","his","how","however","i","if","important","in","interest","interested","interesting","interests","into","is","it","its","itself","j","just","k","keep","keeps","kind","knew","know","known","knows","l","large","largely","last","later","latest","least","less","let","lets","like","likely","long","longer","longest","m","made","make","making","man","many","may","me","member","members","men","might","more","most","mostly","mr","mrs","much","must","my","myself","n","necessary","need","needed","needing","needs","never","new","new","newer","newest","next","no","nobody","non","noone","not","nothing","now","nowhere","number","numbers","o","of","off","often","old","older","oldest","on","once","one","only","open","opened","opening","opens","or","order","ordered","ordering","orders","other","others","our","out","over","p","part","parted","parting","parts","per","perhaps","place","places","point","pointed","pointing","points","possible","present","presented","presenting","presents","problem","problems","put","puts","q","quite","r","rather","really","right","right","room","rooms","s","said","same","saw","say","says","second","seconds","see","seem","seemed","seeming","seems","sees","several","shall","she","should","show","showed","showing","shows","side","sides","since","small","smaller","smallest","so","some","somebody","someone","something","somewhere","state","states","still","still","such","sure","t","take","taken","than","that","the","their","them","then","there","therefore","these","they","thing","things","think","thinks","this","those","though","thought","thoughts","three","through","thus","to","today","together","too","took","toward","turn","turned","turning","turns","two","u","under","until","up","upon","us","use","used","uses","v","very","w","want","wanted","wanting","wants","was","way","ways","we","well","wells","went","were","what","when","where","whether","which","while","who","whole","whose","why","will","with","within","without","work","worked","working","works","would","x","y","year","years","yet","you","young","younger","youngest","your","yours","z"};
	public static String outputFile="";
	public static String titleFile="";
	public static int doc=0;
	public static int val=0;
	public static int titleVal=0;
	public static int chunkSize=5000;
	public static void main(String[] args) 
    {
		long start = System.currentTimeMillis();
		
		for(int i=0;i<435;i++)
    	{
			stopWords.add(Main.stopWord[i]);
    		
    	}
		try {
			//File inputFile = new File("resource/small.xml");
			//File inputFile = new File("resource/wiki-search-small.xml");
			//File inputFile = new File("resource/pages.xml");
			File inputFile = new File("/home/nikita/workspace/wikiEngine/resource/pages.xml");
			Main.outputFile="/home/index/output.txt";
			Main.titleFile="/home/index/title.txt";
	        //For parsing the entire xml dump
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
	        SAXParser saxParser = saxParserFactory.newSAXParser();
	        // UserHandler parses the document id, title and text.
	        UserHandler userhandler = new UserHandler();
	        saxParser.parse(inputFile, userhandler);
	        UserHandler.writeFile(Main.outputFile);
	        UserHandler.writeTitle(Main.titleFile);
	        // It merges the index files and title-id mapping files and creates secondary and tertiary index of data and title-id file.
	        MergeFile.merge();
	        long end= System.currentTimeMillis();
	        System.out.println((end-start)/1000 +"s");
	    } 
	    catch (Exception e){
	    	e.printStackTrace();
	    }	    
    }
}

class UserHandler extends DefaultHandler {
	   boolean bpage=false;
	   boolean btitle=false;
	   boolean bid=false;
	   boolean btext=false;
	   boolean flagId=false;
	   static int chunk=Main.chunkSize;
	   char c;
	   static String id=new String();
	   StringBuilder title=new StringBuilder();
	   StringBuilder text= new StringBuilder();
	   //Data structure to maintain docId and title mapping.
	   public static TreeMap<Long,String> titlemap=new TreeMap<>(); 
	   //Data structure to maintain term and posting list associated with that term.
	   //Posting list contains docId and count with fields in which it is occurring.
	   public static Map<String,HashMap<String,Fields> > gmap = new TreeMap();
	   @Override
	   public void startElement(String uri, String localName, String qName, Attributes attributes)
	         throws SAXException {
		   try{ 
	            if (qName.equalsIgnoreCase("page")) 
	            {
	            	if(Main.doc%chunk==0 && Main.doc>0)
	                {
	                    try
		            	{
		            		writeFile(Main.outputFile);
		            	}
	                    catch(Exception e){
	                    	e.printStackTrace();
	                    }
	                }
	                Main.doc++;
	            	bpage=true;
	            } 
	            else if(qName.equalsIgnoreCase("title"))
	            {
	            	btitle=true;
	            }
	            else if(qName.equalsIgnoreCase("id") && flagId)
	            {
	                bid=true;
	                flagId=false;
	            }
	            else if(qName.equalsIgnoreCase("text")) 
	            {
	                btext=true;
	                text.setLength(0);
	            }
	        }
	        catch(Exception e){
	        	e.printStackTrace();
	        }
	   }

	   @Override
	   public void endElement(String uri, String localName, String qName) throws SAXException {
		   try{
	            if(qName.equalsIgnoreCase("title")) 
	            {
	            	btitle=false;
	            	flagId=true;
	            }
	            else if(qName.equalsIgnoreCase("text")) 
	            {
	            	btext=false;
	            }
	            else if (qName.equalsIgnoreCase("id")){
	         	   	bid = false;
	            }
	            else if(qName.equalsIgnoreCase("page")){
	            	bpage=false;

	            	titlemap.put(Long.parseLong(id),title.toString());
	            	if(Main.doc%chunk==0 && Main.doc>0 ){
	            		try{
	            			writeTitle(Main.titleFile);
	            		}
	            		catch(Exception e){
	            			e.printStackTrace();
	            		}
	            		
	            	}
	            	//Process Title
	            	StringBuilder tempTitle=new StringBuilder();
	            	int i,n=title.length();
	            	for(i=0;i<n;i++){
	            		char current = title.charAt(i);
	            		if((current>='a' && current<='z')||(current>='A' && current<='Z')||(current>='0' && current<='9'))
	            		{
	            			tempTitle.append(current);
	            		}
	            		else{
	            			if(tempTitle.length()>1){
	    	            		Parser.processTitle(tempTitle.toString());
	    	            	}
	    	            	tempTitle.setLength(0);
	            		}
	            	}
	            	if(tempTitle.length()>1){
	            		Parser.processTitle(tempTitle.toString());
	            	}
	            	tempTitle.setLength(0);
	            	//Process text data
	            	Parser.processData(text);
	            	title.setLength(0);
	            	text.setLength(0);
	            }
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
		   
	   }
	   
	   @Override
	    public void characters(char ch[], int start, int length) throws SAXException 
	    {
		   	if(bid){
		   		id=new String(ch,start,length);
		   	}
		   	else if(btitle){
				title.append(ch,start,length);			
			}
	        else if(btext){
	        	text.append(ch,start,length);
	        	
	        }
	    }
	   
	   //Function  to write the title corresponding to the DocId in the file.
	    public static void writeTitle(String filename){
	    	
	    	try{
	    		Main.titleVal=(int)Math.ceil((double)Main.doc/chunk);
	           	filename=filename+(Main.titleVal);
		        File file = new File(filename);
			   	if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				for (Entry<Long,String> entry : titlemap.entrySet()) 
		    	{
					String id=entry.getKey().toString();
		    		String word=entry.getValue();
		    		String line=id+":"+word+"\n";
		    		bw.write(line);
		    	}
				bw.close();
				titlemap.clear();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	
	    }
	    
	    //Function to write term with its posting list in the index file.
		public static void writeFile(String filename)  {
		   try
	    	{  
			   	
               	Main.val=(int)Math.ceil((double)Main.doc/chunk);
               	filename=filename+(Main.val);
               	System.out.println(filename);
		        File file = new File(filename);
			   	if (!file.exists()) {
					file.createNewFile();
				}
				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
		    	for (Entry<String, HashMap<String, Fields>> entry : gmap.entrySet()) 
		    	{
		    		String word=entry.getKey();
		    		TreeMap<String,Fields> mp = new TreeMap<String,Fields>(entry.getValue());
		    		bw.write(word+":");
		    		for(Entry<String,Fields> mpEntry:mp.entrySet())
		    		{
		    			String id = mpEntry.getKey();
		    			Fields fields = mpEntry.getValue();
		    			String line=id+"-";	
		    			if(fields.title!=0){	
		    				line+="t"+fields.title;
		    			}
		    			if(fields.text!=0){
		    				line+="d"+fields.text;
		    			}
		    			if(fields.infobox!=0){
		    				line+="i"+fields.infobox;
		    			}
		    			if(fields.category!=0){
		    				line+="c"+fields.category;
		    			}
		    			if(fields.extlink!=0){
		    				line+="e"+fields.extlink;
		    			}
		    			if(fields.reference!=0){
		    				line+="r"+fields.reference;
		    			}
		    			line+="|";
		    			bw.write(line);
		    		}
		    		bw.write("\n");
		    	}
				bw.close();
				gmap.clear();		
			}
			catch(Exception e){
				e.printStackTrace();
			}
	   } 
	   
}