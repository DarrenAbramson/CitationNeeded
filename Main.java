import edu.jhu.nlp.wikipedia.*;
import org.apache.tools.bzip2.CBZip2InputStream;

class Main
{
    
    public static void main(String[] args) throws Exception
    {
        
        // Start time
        long startTime = System.currentTimeMillis();
        
        // Get a parser for the file
        WikiXMLParser wxsp = WikiXMLParserFactory.getSAXParser(args[0]);
        MyPageCallbackHandler handler = new MyPageCallbackHandler(args[0]);
        
        // All the action is in MyPageCallbackHandler
        try
        {
            wxsp.setPageCallback(handler);
            wxsp.parse();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    
        // Report back
        System.out.println("Number with: " + handler.pagesWith);
        System.out.println("Number total: " + handler.pagesTotal);
        System.out.println("Number of total citation needed: " + handler.numTotal);
    
        // Execution time
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        
        System.out.println("Total time in seconds: " + (elapsedTime/1000));
        
        handler.finishWriting();
    
    }
}


// Run 1:

// Ds-MacBook-Pro-2:4) Citation needed D$ java Test "enwiki-20130805-pages-articles.xml.bz2"
// Number with: 73441
// Number total: 13715113
// Number of total citation needed: 140491
// Total time in seconds: 10084

// Case insensitive:

// Number with: 216997
// Number total: 13715113
// Number of total citation needed: 460954

// Refactored Code:

// Number with: 216997
// Number total: 13715113
// Number of total citation needed: 460954
// Total time in seconds: 9537