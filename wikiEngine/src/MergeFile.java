package wikiEngine;

import java.io.*;
import java.util.*;

// For string 
class WordInfo{
	int docId; // the file from which it is read.
	String word;
    String list; 
}

// For title
class TitleInfo{
	int docId;
	Long id;
	String word;
}

// To maintain score along with list
class Rank{
	int score;
	String list;	
}

class Compare implements Comparator<WordInfo> 
{ 
    public int compare(WordInfo x, WordInfo y) 
    { 
        return x.word.compareTo(y.word);
    } 
}
class CompareRank implements Comparator<Rank> 
{ 
    public int compare(Rank x, Rank y) 
    { 
        return (y.score-x.score);
    } 
}
class CompareId implements Comparator<TitleInfo>
{
	public int compare(TitleInfo x,TitleInfo y)
	{
		return (x.id.compareTo(y.id));
	}
}
public class MergeFile {
	
	// calculate rank along the basis of the score
	public static String calculateRank(String finalList){
		StringBuilder res=new StringBuilder();
		res.setLength(0);
		String token;
		int i,len;
		int score;
		List<Rank> lists=new ArrayList<>();
		StringTokenizer st=new StringTokenizer(finalList,"|");

		while(st.hasMoreElements()){
			score=0;
			Rank r=new Rank();
			token=st.nextToken();
			int body=0,title=0,ext=0,ref=0,cat=0,info=0;
			r.list=token;
			token=token.substring(token.indexOf("-")+1);
			StringBuilder str = new StringBuilder();
			len=token.length();
			
			int j=0;
			while(j<len){
				
				if(token.charAt(j)=='t'){
					j++;
					str.setLength(0);
					while(j<len && token.charAt(j)>='0' && token.charAt(j)<='9'){
						str.append(token.charAt(j));
						j++;
					}
					title=Integer.parseInt(str.toString());
					score+=1000*title;
				}
				else if(token.charAt(j)=='d'){
					
					j++;
					str.setLength(0);
					while(j<len && token.charAt(j)>='0' && token.charAt(j)<='9'){
						str.append(token.charAt(j));
						j++;
					}
					body=Integer.parseInt(str.toString());
					score+=2*body;
				}
				else if(token.charAt(j)=='c'){
					
					j++;
					str.setLength(0);
					while(j<len && token.charAt(j)>='0' && token.charAt(j)<='9'){
						
						str.append(token.charAt(j));
						j++;
					}
					cat=Integer.parseInt(str.toString());
					score+=20*cat;
				}
				else if(token.charAt(j)=='i'){
					
					j++;
					str.setLength(0);
					while(j<len && token.charAt(j)>='0' && token.charAt(j)<='9'){
						
						str.append(token.charAt(j));
						j++;
					}
					info=Integer.parseInt(str.toString());
					score+=25*info;
				}
				else if(token.charAt(j)=='e'){
					
					j++;
					str.setLength(0);
					while(j<len && token.charAt(j)>='0' && token.charAt(j)<='9'){
						
						str.append(token.charAt(j));
						j++;
					}
					ext=Integer.parseInt(str.toString());
					score+=ext;
				}
				else if(token.charAt(j)=='r'){
					
					j++;
					str.setLength(0);
					while(j<len && token.charAt(j)>='0' && token.charAt(j)<='9'){
						
						str.append(token.charAt(j));
						j++;
					}
					ref=Integer.parseInt(str.toString());
					score+=ref;
				}
			} 
            r.score=score;
            lists.add(r);
		}
		Collections.sort(lists,new CompareRank());
		for(i=0;i<lists.size();i++)
        {
            res.append(lists.get(i).list);
            res.append("|");
        }
		return res.toString();
	}
	// For merging the titles
	public static void mergeTitle(int val){
		try{
			int i;
			int lCnt=0,emptyFile=0;
			int id;
			Long finalId,tempId;
			long charcnt=0;
			String line,str;
			String finalWord="",tempWord="";
			Comparator<TitleInfo> comp=new CompareId();
			PriorityQueue<TitleInfo> pq=new PriorityQueue<TitleInfo>(val+2,comp);
			
			boolean[] full=new boolean[val+2];
			for(i=1;i<=val;i++){
				full[i]=true;
			}
			BufferedReader br[]=new BufferedReader[val+2];
			for(i=1;i<=val;i++){
				FileReader fr=new FileReader("/home/index/title.txt"+i);
				br[i]=new BufferedReader(fr);	
			}
			File indexFile1=new File("/home/index/titleindex1.txt");
	        if (!indexFile1.exists()){
	           indexFile1.createNewFile();
	        }
	        File indexFile2=new File("/home/index/titleindex2.txt");
	        if (!indexFile2.exists()){
	           indexFile2.createNewFile();
	        }
	        
	        FileWriter fw1=new FileWriter(indexFile1.getAbsoluteFile());
	        BufferedWriter bw1=new BufferedWriter(fw1);
	        FileWriter fw2=new FileWriter(indexFile2.getAbsoluteFile());
	        BufferedWriter bw2=new BufferedWriter(fw2);
	        
	        for(i=1;i<=val;i++){
	        	if(full[i])
	            {                  
	               line = br[i].readLine();
	              
	               if(line==null)
	               {
	                    emptyFile++;
	                    full[i]=false;    
	                    br[i].close();
	               }
	               else
	               {
	                    str = line.substring(0,line.indexOf(':'));
	                    line=line.substring(line.indexOf(':')+1);
	                    TitleInfo top = new TitleInfo();
	                    top.docId=i;
	                    top.id=Long.parseLong(str);
	                    top.word=line;
	                    pq.add(top);
	               }	         
	            }
	        }
	        TitleInfo top=pq.poll();
	        id=top.docId;
	        finalId=top.id;
	        finalWord=top.word;
	        tempId=0L;
	        tempWord="";
	        while(emptyFile<val)
	        {
	        	//The file is completely read.
	            if(!full[id])
	            {
	                top = pq.poll();
	                id=top.docId;
	                tempId=top.id;
	                tempWord=top.word;
	            }
	            else
	            {
	                line = br[id].readLine();
	                if(line==null)
	                {
	                	emptyFile++;
	                    full[id]=false;    
	                    br[id].close();
	                }
	                else
	                {
	                	str=line.substring(0,line.indexOf(':'));
	                    line=line.substring(line.indexOf(':')+1);
	                    top=new TitleInfo();
	                    top.docId=id;
	                    top.id=Long.parseLong(str);
	                    top.word=line;
	                    pq.add(top);
	                    top=pq.poll();
	                    id=top.docId;
	                    tempId=top.id;
	                    tempWord=top.word;
	                }
	            }
                if(lCnt%100==0)
                {
                	if(finalId!=0){
                		bw2.write(finalId.toString());
                        bw2.write(":"+charcnt);
                        bw2.write("\n");
                	}                    
                }
                if(finalId!=0){
                	lCnt++;
                	bw1.write(finalId.toString()+":"+finalWord+"\n");
                    charcnt+=Long.toString(finalId).length()+finalWord.length()+2;
                }
                finalWord=tempWord;
                finalId=tempId;
                tempId=0L;
                tempWord="";
	        }
	        bw1.close();
	        bw2.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// For merging the small index files created into one primary index file
	public static void merge(){
		try{
			int i,val=3529;
			int lCnt=0,lCntSec=0,emptyFile=0;
			int id;
			long charcnt=0,charcntSec=0;
			String line,str;
			String tempWord,tempList,finalWord,finalList;
			// Merge the title files
			mergeTitle(val);
			Comparator<WordInfo> comp=new Compare();
			PriorityQueue<WordInfo> pq=new PriorityQueue<WordInfo>(val+2,comp);
			
			boolean[] full=new boolean[val+2];
			for(i=1;i<=val;i++){
				full[i]=true;
			}
			BufferedReader br[]=new BufferedReader[val+2];
			for(i=1;i<=val;i++){
				FileReader fr=new FileReader("src/index/output.txt"+i);
				br[i]=new BufferedReader(fr);	
			}
			File indexFile1=new File("/home/index/index1.txt");
	        if (!indexFile1.exists()){
	           indexFile1.createNewFile();
	        }
	        File indexFile2=new File("/home/index/index2.txt");
	        if (!indexFile2.exists()){
	           indexFile2.createNewFile();
	        }
	        File indexFile3=new File("/home/index/index3.txt");
	        if (!indexFile3.exists()){
	           indexFile3.createNewFile();
	        }
	        
	        FileWriter fw1=new FileWriter(indexFile1.getAbsoluteFile());
	        BufferedWriter bw1=new BufferedWriter(fw1);
	        FileWriter fw2=new FileWriter(indexFile2.getAbsoluteFile());
	        BufferedWriter bw2=new BufferedWriter(fw2);
	        FileWriter fw3=new FileWriter(indexFile3.getAbsoluteFile());
	        BufferedWriter bw3=new BufferedWriter(fw3);
	        for(i=1;i<=val;i++){
	        	if(full[i])
	            {                  
	               line = br[i].readLine();
	               if(line==null)
	               {
	                    emptyFile++;
	                    full[i]=false;    
	                    br[i].close();

	               }
	               else
	               {
	                    str = line.substring(0,line.indexOf(':'));
	                    line=line.substring(line.indexOf(':')+1);
	                    WordInfo top = new WordInfo();
	                    top.docId=i;
	                    top.word=str;
	                    top.list=line;
	                    pq.add(top);
	               }
	         
	            }
	        }
	        WordInfo top=pq.poll();
	        id=top.docId;
	        finalWord=top.word;
	        finalList=top.list;
	        tempList=null;
	        tempWord=null;
	        while(emptyFile < val)
	        {
	        	//The whole file is read
	            if(!full[id])
	            {
	                top=pq.poll();
	                id=top.docId;
	                tempList=top.list;
	                tempWord=top.word;
	            }
	            else
	            {
	                line = br[id].readLine();
	                if(line==null)
	                {
	                	emptyFile++;
	                    full[id]=false;    
	                    br[id].close();
	                }
	                else
	                {
	                	str = line.substring(0,line.indexOf(':'));
	                    line=line.substring(line.indexOf(':')+1);
	                    top=new WordInfo();
	                    top.docId=id;
	                    top.word=str;
	                    top.list=line;
	                    pq.add(top);
	                    
	                    top=pq.poll();
	                    id=top.docId;
	                    tempWord=top.word;
	                    tempList=top.list;
	                }
	            }
	            if(tempWord.equals(finalWord) && finalWord!=null)
	            {
	            	finalList=finalList+tempList;
	            }
	            else
	            {
	            	
	                if(lCnt%100==0)
	                {	
	                    if(lCntSec%100==0 && finalWord.length()>0){
	                    	
	                    	System.out.println(":"+lCntSec/100);
	                    	bw3.write(finalWord);
	                		bw3.write(":"+charcntSec);
	                		bw3.write("\n");
	                	}
	                    if(finalWord.length()>0){
	                    	lCntSec++;
	                    	bw2.write(finalWord);
		                    bw2.write(":"+charcnt);
		                    bw2.write("\n");
		                    charcntSec+=finalWord.length()+Long.toString(charcnt).length()+2;
	                    }	                    
	                } 
	                if(finalWord.length()>0){
	                	lCnt++;
		                finalList=calculateRank(finalList);
		                bw1.write(finalWord+":"+finalList+"\n");
		                charcnt+=finalWord.length()+finalList.length()+2; 
	                }
	                finalWord=tempWord;
	                finalList=tempList;
	                tempWord=tempList="";
	            }
	                
	        }
	        bw1.close();
	        bw2.close();
	        bw3.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}