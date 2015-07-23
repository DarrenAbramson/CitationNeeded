# CitationNeeded
The purpose of this code is to provide an example of a behavioral analysis of Wikipedia. The intended application is for providing empirical justification for a controversial epistemological category.

You'll need `bzip2.jar` and `wikixmlj-r43.jar` extracted to where you compile. 

On my computer the following worked:

`javac Main.java`

`java Main "WikipediaXML_Compressed.bz2"`

At the command line, this should give you three numbers along with other possible output:

1. Number of pages in the XML dump with a `{{citation needed}}` tag.

2. Total number of pages in the XML dump. 

3. Total number of `{{citation needed}}` tags in the XML dump.

4. Any malformed 'citation needed' items. At present, there are only two, both of which are found on `https://en.wikipedia.org/wiki/Wikipedia:Biographies_of_living_persons/Risk_lists/List_2` in the form of partially quoted citation needed tags.

5. Any tags that appear on a page prior to `PRECEDING_SENTENCE_LENGTH` characters from the beginning. Note that this includes markup characters. Each such page, with the character number of the first appearance of a citation needed tag, is listed in the output.

To pipe the output to a log for a run enter something like the following:

`java Main "enwiki-pages-articles.xml.bz2" -option | tee enwiki-pages-articles-DateTime.log` 

It will also create a file with a name like the following:

`2015.07.21 AD at 13:42:17 ADT.WikiDataFileName.bz2.csv`

At present, this is a table with three columns:

* `pageName`
* `tagContents`
* `precedingSentence`

`pageName` is the name of a Wikipedia page that contains a citation needed tag. `tagcontents` is the information contained in the citation needed tag. In virtually every case this contains date information for when the tag was created. `precedingSentence` is, rather arbitrarily, the 30 characters preceding the tag.

In R studio, the following code imports the csv file into a table with the correct dimensions, matching the terminal output for number of tags (excel seems to mangle some of the delimiters).

`cnTable <- test read.csv("2015.07.21 AD at 13:42:17 ADT.WikiDataFileName.bz2.csv", header=TRUE, quote="")`

It may be preferable to add a `stringsAsFactors=FALSE` argument.

The next important step is to strip out and import date information. I think there's already enough information to get halflife from a single dump.