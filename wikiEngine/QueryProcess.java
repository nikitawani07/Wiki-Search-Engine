package wikiEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

// For string stored in tertiary index file
class Word{
	String term;
	long charcnt;
	Word(String str,long cnt){
        this.term = str;
        this.charcnt=cnt;
    }
}

// For title stored in secondary title file
class Title{
	long docId;
	long charcnt;
	Title(long id, long cnt){
	    this.docId=id;
	    this.charcnt=cnt;
	}   
}
class CompareWord implements Comparator<Word>
{
	public int compare(Word x, Word y) 
    {
      return x.term.compareTo(y.term);
    }
}
class CompareTitle implements Comparator<Title>
{
	public int compare(Title x, Title y)
	{
		return (int)(x.docId-y.docId);
	}
};
  

public class QueryProcess {
	public static Set<String> stopWords = new HashSet<>();
	public static String terIndexFile="/home/nikita/index/index3.txt";
	public static String secIndexFile="/home/nikita/index/index2.txt";
	public static String indexFile="/home/nikita/index/index1.txt";
	public static String secTitleFile="/home/nikita/index/SecondaryTitleIndex";
	public static String titleFile="/home/nikita/index/PrimaryTitleIndex";
	
	public static Map<Long,Long> calculateRank(Map<Long,Long> docScore, String postingList,int ltcount,int lbcount,int licount,int lcount,int lecount, int lrcount){
		long score=0;
		Long docId;
		int tcount=0,bcount=0,icount=0,ccount=0,rcount=0,ecount=0;
		int i,len,title=1000,body=2,infobox=25,category=20,externalLink=1,reference=1;
		char c,ch;
		String token;
		StringTokenizer st=new StringTokenizer(postingList,"|");
		//int counttoken=0;
		while(st.hasMoreElements() ){
			//counttoken=counttoken+1;
			token=st.nextToken();
			StringBuilder tstr=new StringBuilder();
			StringBuilder bstr=new StringBuilder();
			StringBuilder istr=new StringBuilder();
			StringBuilder cstr=new StringBuilder();
			StringBuilder estr=new StringBuilder();
			StringBuilder rstr=new StringBuilder();
			tcount=0;bcount=0;icount=0;ccount=0;rcount=0;ecount=0;
			
			docId=Long.parseLong(token.substring(0,token.indexOf("-")));
			token=token.substring(token.indexOf("-")+1);
			len=token.length();
			c=token.charAt(0);
			for(i=0;i<len;i++){
				if(token.charAt(i)>='a' && token.charAt(i)<='z'){
					c=token.charAt(i);
				}
				else{
					ch=token.charAt(i);
					if(c=='t'){
						tstr.append(ch);
					}
					else if(c=='d'){
						bstr.append(ch);
					}
					else if(c=='i'){
						istr.append(ch);
					}
					else if(c=='c'){
						cstr.append(ch);
					}
					else if(c=='e'){
						estr.append(ch);
					}
					else if(c=='r'){
						rstr.append(ch);
					}
				}
			}
			if(tstr.length()!=0){
				tcount=Integer.parseInt(tstr.toString());
			}
			if(bstr.length()!=0){
				bcount=Integer.parseInt(bstr.toString());
			}
			if(istr.length()!=0){
				icount=Integer.parseInt(istr.toString());
			}
			if(cstr.length()!=0){
				ccount=Integer.parseInt(cstr.toString());
			}
			if(estr.length()!=0){
				ecount=Integer.parseInt(estr.toString());
			}
			if(rstr.length()!=0){
				rcount=Integer.parseInt(rstr.toString());
			}
			score=(ltcount*title*tcount)+(lbcount*body*bcount)+(licount*infobox*icount)+(lcount*category*ccount)+(lecount*externalLink*ecount)+(lrcount*reference*rcount); 
			score=(long)Math.log10(score);
			docScore.put(docId,docScore.getOrDefault(docId, (long)0)+score);
			
		}	
		return docScore;
	}
	
	// Retrieve title on the basis of title. Perform binary search on secondary title file and then search in primary title file.
	public static String retrieveTitle(Long docId, List<Title> secTitle) throws Exception {
		long index,startIndex;
		Long id;
		String title="",line;
		int i;
		index=Collections.binarySearch(secTitle, new Title(docId,0),new CompareTitle());

        if(index<0)
        {
            index*=-1;
            if(index>2){
            	startIndex=secTitle.get((int)index-2).charcnt;
            }   
            else{
            	startIndex=0;
            }	                    
        }
        else if(index>3){
        	startIndex=secTitle.get((int)index-3).charcnt;
        }   
        else{
        	startIndex=0;
        }
		RandomAccessFile file=new RandomAccessFile(titleFile, "r");
		file.seek(startIndex);
		line=file.readLine();

		for(i=1;i<=120 && line!=null;i++){
			line=file.readLine();
			id=Long.parseLong(line.substring(0, line.indexOf(":")));
        	if(id.longValue()==docId.longValue()){
        		title = line.substring(line.indexOf(":")+1);
        		break;
        	}
        }
		file.close();
		return title;
	}
	
	public static void main(String args[]){
		int i,j,queryCount=0,index;
		long charcnt;
		long startTime,endTime;
		long startIndex;
		String query,str,postingList;
		String line,term;
		String docId;
		stopWords.clear();
		// Load the stopwords
		for(i=0;i<435;i++)
		{
			stopWords.add(Main.stopWord[i]); 
		}
		try{
			//For reading input query
			BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
			
			// For reading tertiary index file which maintain string and the char count i.e, the offset.
			BufferedReader brTerIndex=new BufferedReader(new FileReader(terIndexFile));
			List<Word> terIndex=new ArrayList<Word>();		
			line =brTerIndex.readLine();
			while(line!=null){
				term=line.substring(0,line.indexOf(":"));
				charcnt=Long.parseLong(line.substring(line.indexOf(":")+1));
				Word w = new Word(term,charcnt);	
				terIndex.add(w);
				line=brTerIndex.readLine();
	        }
			brTerIndex.close();
			
			// For reading secondary index file which maintain docId and the char count i.e, the offset.
			BufferedReader brSecTitle = new BufferedReader(new FileReader(secTitleFile));
			List<Title> secTitle = new ArrayList<Title>();
			line=brSecTitle.readLine();
	        while(line!=null)
	        {
	            docId=line.substring(0,line.indexOf(":"));
	            charcnt=Long.parseLong(line.substring(line.indexOf(":")+1));
	            Title e = new Title(Long.parseLong(docId),charcnt);
	            secTitle.add(e);
	            line=brSecTitle.readLine();
	        }
	        brSecTitle.close();
	        
	        // Tokenize the query
	        queryCount=Integer.parseInt(input.readLine());
	        while(queryCount>0){
	        	query=input.readLine();
	        	Map<Long,Long> docScore=new HashMap<>();
	        	query=query.replaceAll("[!@#$%+^&;*'.><]", "");
	        	startTime=System.currentTimeMillis();
	        	StringTokenizer st=new StringTokenizer(query," ");
	        	String token="";
	        	while(st.hasMoreElements()){
	        		token=st.nextToken();
	        		token=token.toLowerCase().trim();
	        		int ltcount=10000,lbcount=2,licount=25,lccount=30,lecount=1,lrcount=1;
	                // identify whether it is a field query
	        		if(token.contains("t:"))
	                {
	                    ltcount*=1000;
	                    token=token.substring(token.indexOf(":")+1);
	                }
	                if(token.contains("b:"))
	                {              
	                    lbcount*=1000;   
	                    token=token.substring(token.indexOf(":")+1);
	                }
	                if(token.contains("i:"))
	                {
	                    licount*=1000;
	                    token=token.substring(token.indexOf(":")+1);
	                }
	                if(token.contains("c:"))
	                {
	                    lccount*=1000;
	                    token=token.substring(token.indexOf(":")+1);
	                }
	                if(token.contains("e:"))
	                { 
	                    lecount*=1000;
	                    token=token.substring(token.indexOf(":")+1);
	                }
	                if(token.contains("r:")){
	                	lrcount*=1000;
	                    token=token.substring(token.indexOf(":")+1);
	                }
	                if(stopWords.contains(token)) 
		            {
		        		continue;
		            }
		            token=Parser.stemming(token);
		            
		            // Binary search in tertiary index file.
		            index = Collections.binarySearch(terIndex, new Word(token,0),new CompareWord());

		            if(index<0)
		            {
		                index*=-1;
		                if(index>2){
		                	startIndex=terIndex.get(index-2).charcnt;
		                }   
		                else{
		                	startIndex=0;
		                }	                    
		            }
		            else if(index>3){
		            	startIndex=terIndex.get(index-3).charcnt;
		            }   
		            else{
		            	startIndex=0;
		            }
		            RandomAccessFile secFile = new RandomAccessFile(secIndexFile, "r");
		            List<Word> secIndex=new ArrayList<Word>();
		            secFile.seek(startIndex);
		            line =secFile.readLine();
		            for(j=0;j<=120 && line!=null;j++)
		            {	
	    				term=line.substring(0,line.indexOf(":"));
	    				charcnt=Long.parseLong(line.substring(line.indexOf(":")+1));
	    				Word w = new Word(term,charcnt);	
	    				secIndex.add(w);
	    				line=secFile.readLine();
		            }		          
		            
		            // Binary search in secondary index file
		            index = Collections.binarySearch(secIndex, new Word(token,0), new CompareWord());
		            if(index<0)
		            {
		                index*=-1;
		                if(index>2){
		                	startIndex=secIndex.get(index-2).charcnt;
		                }   
		                else{
		                	startIndex=0;
		                }	                    
		            }
		            else if(index>3){
		            	startIndex=secIndex.get(index-3).charcnt;
		            }   
		            else{
		            	startIndex=0;
		            }
		            secIndex.clear();
		            secFile.close();
		            
		            // Now search in primary index file.
		            RandomAccessFile file = new RandomAccessFile(indexFile, "r");
		            file.seek(startIndex);
		            if(startIndex!=-1){
		            	line =file.readLine();
		            	postingList=null;
		            	for(j=0;j<=120 && line!=null;j++){
		            		str=line.substring(0,line.indexOf(":"));
		            		if(str.equals(token)){
		            			postingList=line.substring(line.indexOf(":")+1);
		            			break;
		            		}
		            		line=file.readLine();
		            	}
		            	if(postingList!=null){
		            		// Rank documents on the basis of score
		            		docScore = calculateRank(docScore, postingList,ltcount,lbcount,licount,lccount,lecount,lrcount);
		            	}
		            }
		            file.close();
	        	}
	        	Set<Entry<Long,Long>> set=docScore.entrySet();
	            List<Entry<Long,Long>> list=new ArrayList<Entry<Long,Long>>(set);
	            Collections.sort(list,new Comparator<Map.Entry<Long,Long>>()
	            {
	                public int compare( Map.Entry<Long,Long> entry1, Map.Entry<Long,Long> entry2 )
	                {
	                    return (int)(entry2.getValue()-entry1.getValue());
	                }
	            });
	        	int cnt=0;
	        	// Retrieve the top 10 documents
	        	for(j=0;cnt<10 && i<list.size();j++){
	        		// Retrieve title using the docId
	        		String result=retrieveTitle(list.get(j).getKey(),secTitle);
	        		if(result.length()>0){
	        			System.out.println(result);
	        			cnt++;
	        		}
	        	}
	        	docScore.clear();
	        	endTime=System.currentTimeMillis();
	            System.out.println("Time: "+(endTime-startTime)+"ms");
	            queryCount=queryCount-1;
	        }	        
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
}