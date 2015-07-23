import edu.jhu.nlp.wikipedia.*;
import org.apache.tools.bzip2.CBZip2InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.FileWriter;

class MyPageCallbackHandler implements PageCallbackHandler
{
    public int pagesWith = 0;
    public int pagesTotal = 0;
    public int numTotal = 0;
    
    FileWriter fileWriter;
    String fileName;
    
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "pageName,tagContents,precedingSentence";
    private static final int PRECEDING_SENTENCE_LENGTH = 30;
    
    // Advance amount is the same regardless of case.
    private int ADVANCE_AMOUNT = "{{citation needed".length();
    
    private int x = 0;
    
    
    // Created each time there is a new run.
    // Outputs a single CSV file that should have a unique name based on system time.
    // Each call of process will write all citation needed tags to the file created
    // for the run.
    public MyPageCallbackHandler(String fileName)
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        fileName = sdf.format(cal.getTime()) + "." + fileName + ".csv";
        try
        {
            fileWriter = new FileWriter(fileName, true);
            fileWriter.append(FILE_HEADER);
            fileWriter.append(NEW_LINE_SEPARATOR);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    // To be called by the creating method after all parsing is complete.
    public void finishWriting()
    {
        try
        {
            fileWriter.flush();
            fileWriter.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    // Contents to write: "pageName,tagContents,precedingSentence"
    // NOTE: wikiPage is a whole page, and cnIndex references the whole page text.
    
    private void writeRecordToFile(WikiPage wikiPage, int cnIndex)
    {
        
        // Simplest approach, prior to any data processing:
        // Just get the whole {{}}, and the previous text delimited by . and .

        String pageText = wikiPage.getWikiText();
        
        try
        {
            // pageName
            // NOTE: titles seem to include a new line delimiter, so it is stripped.
            
            String pageName = wikiPage.getTitle().replace("\n", "").replace(",","").trim();
            
            int secondIndex = pageText.indexOf("}}", cnIndex);
            String tagContents = pageText.substring(cnIndex, secondIndex + 2);
            tagContents = tagContents.replace("\n","").replace(",","").trim();
            
            int precedingStartingIndex = cnIndex - PRECEDING_SENTENCE_LENGTH;
            
            String precedingSentence;
            
            if (precedingStartingIndex < 0)
            {
                System.out.println("cnIndex was " + cnIndex + " so I saved precedingSentence from 0 to cnIndex." + " The page was " + pageName + ".");
                precedingSentence = pageText.substring(0, cnIndex);
            }
            else
            {
                precedingSentence = pageText.substring(precedingStartingIndex, cnIndex);
                precedingSentence = precedingSentence.replace("\n","").replace(",","").trim();
            }
            
            
            fileWriter.append(pageName);
            fileWriter.append(COMMA_DELIMITER);
            
            fileWriter.append(tagContents);
            fileWriter.append(COMMA_DELIMITER);
            
            fileWriter.append(precedingSentence);
            fileWriter.append(NEW_LINE_SEPARATOR);
       
        }
        catch(Exception e)
        {
            System.out.println("Error! We caught an exception. cnIndex was " + cnIndex +
                               " on page " + wikiPage.getTitle() + ".");
            // e.printStackTrace();
        }
        
    }
    
    private int getNextCaseInsensitiveIndex(int cIndex, String wikiPageText)
    {
        String pageRemaining = wikiPageText.substring(cIndex);
        int nextIndex = cIndex;
        
        int indexL = pageRemaining.indexOf("{{citation needed");
        int indexU = pageRemaining.indexOf("{{Citation needed");
        
        // Since nextIndex references the WHOLE article, but indexL/indexU
        // reference the REMAINING article, indexL/indexU must be added
        // to the current index.
        
        if (indexL > -1)
        {
            if (indexU > -1)
                // Both present, take the min index
                nextIndex += Math.min(indexL, indexU);
            // L present but not U, take L
            else nextIndex += indexL;
        }
        // Not L, but U, then take U
        else if (indexU > -1)
            nextIndex += indexU;
        // Neither L nor U
        else nextIndex = -1;
        
        return nextIndex;
    }
    
    
    public void process(WikiPage page)
    {

        // Increment total number of pages
        pagesTotal++;
        
        String text = page.getWikiText();
        
        // Start at the beginning of the page.
        int currentIndex = 0;
        
        // Get the first index. If it's there, then we increment pagesWith.
        currentIndex = getNextCaseInsensitiveIndex(currentIndex, text);
        if(currentIndex != -1)
            pagesWith++;
        
        // Either start on the first index or skip if there aren't any.
        while(currentIndex != -1)
        {
            numTotal++;
            writeRecordToFile(page, currentIndex);
            currentIndex += ADVANCE_AMOUNT;
            
            // System.out.println("I found one and I'm asking for " + currentIndex);
            
            currentIndex = getNextCaseInsensitiveIndex(currentIndex, text);
        }
        
    }
}

//        // For processing animation
//        // String anim= "|/-\\";
//        
//        int index = -1;
//        
//        
//        // Duplicated code: find case insensitive next tag.
//        
//        int indexL = text.indexOf("{{citation needed");
//        int indexU = text.indexOf("{{Citation needed");
//        
//        if (indexL > -1)
//        {
//            if (indexU > -1)
//                // Both present, take the min index
//                index = Math.min(indexL, indexU);
//            // L present but not U, take L
//            else index = indexL;
//        }
//        // Not L, but U, then take U
//        else if (indexU > -1)
//            index = indexU;
//        else index = -1;
//        
//        if (index != -1) pagesWith++;
//
//        while (index != -1)
//        {
//            numTotal++;
//            
//            // Advance forward past the current found tag.
//            text = text.substring(index + "{{citation needed".length());
//            
//            // Write the current tag's data to file.
//            // The whole page's text must be passed.
//            writeRecordToFile(wikiPageText, index);
//            
//            // Find the next tag.
//            indexL = text.indexOf("{{citation needed");
//            indexU = text.indexOf("{{Citation needed");
//            
//            if (indexL > -1)
//            {
//                if (indexU > -1)
//                    // Both present, take the min index
//                    index = Math.min(indexL, indexU);
//                // L present but not U, take L
//                else index = indexL;
//            }
//            // Not L, but U, then take U
//            else if (indexU > -1)
//                index = indexU;
//            else index = -1;
//        }
//            
//        pagesTotal++;

        // For Animation
        //        x++;
        //
        //        x = x % 5;
        //
        //        String data = "  " + anim.charAt(x % anim.length())  +  "\r";
        //
        //        try
        //        {
        //            System.out.write(data.getBytes());
        //        }
        //        catch(Exception e)
        //        {
        //            e.printStackTrace();
        //        }
        
