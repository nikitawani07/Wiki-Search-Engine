# wikiSearchEngine

Implemented efficient and scalable search engine on Wikipedia data.(approx 62.7 GB)

Data dump link: https://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2

## Index Creation

Initially the content of XML File is read with the help of SAX Parser. The data is then passed to UserHandler which contains the following functions:
1. startElement: It identifies open tag in XML file.
2. character: It reads the data enclosed between opentag and closetag.
3. endElement: It identifies close tag in XML file.
The data between id,title and text tag is read using these functions.

While parsing the data identify the text as infobox, category, title, body, external link and references. The words are then passed to respective helper functions.
The helper functions perform the steps in the following order:
1. Convert into lower case.
2. Trim the string.
3. Remove the string if it is a stop word.
4. Perform stemming of the word using PortStemmer class.
5. Remove the string if length is less than 2.
6. Enter the string into map alongwith docId and field information.

Since the dump is too large, it cannot be stored in main memory. So, after reading 5000 documents, the data stored in the map is written into a file. Also the titles along with the id are written in the title file after reading 5000 documents.

After reading the whole data in files, index file needs to be created which contain all the strings along with their posting list in sorted order. This is done using external merge sort. After writing 100 lines in primary index file, write the string in the secondary file along with the character count calculated till then. Similarly after writing 100 lines in secondary file, write the string in the tertiary file along with the character count calculated till then. Similarly merging of title files is done resulting into primary and secondary title index file. While writing the posting list in index file, sort the posting list on the basis of the score. (Giving different weights to different fields.)

## Query Process

Query can be a normal query like sachin tendulkar or a field query like t:sachin c:cricketer where t for title field, b for body, c for category, i for infobox, e for external links and r for references.

1. Query is read from the user.
2. Perform tokenization and identify whether it is normal or field query.
3. Processing on each token is done similar to text processing (lowercase, stopword removal, stemming).
4. Calculate the TFIDF (Term Frequency-Inverse Document Frequency) for each word and document.
5. Retrieve the top 10 results(sorted on the basis of score) representing list of Wikipedia article titles.

## To Run:

Run Main.java:
	
    javac Main.java
    java Main

    i) Input: xml dump
    ii) Output: index1.txt, index2.txt, index3.txt (Primary,Secondary and tertiary index files)
	PrimaryTitleIndex, SecondaryTitleIndex (Primary and Secondary title files)

	
Run QueryProcess.java which will use the index files created by running Main.java
	
    javac QueryProcess.java
	java QueryProcess

After running the program, enter the number of queries and then enter the query which you want to search.

## Files:
1. Main.java
2. Parser.java
3. PortStemmer.java
4. Fields.java
5. MergeFile.java
6. QueryProcess.java
