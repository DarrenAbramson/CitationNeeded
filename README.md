# CitationNeeded
The purpose of this code is to provide an example of a behavioral analysis of Wikipedia. The intended application is for providing empirical justification for a controversial epistemological category.

You'll need `bzip2.jar` and `wikixmlj-r43.jar` extracted to where you compile (and run?). 

On my computer the following worked:

`javac Main.java`

`java Main "WikipediaXML_Compressed.bz2"`

At first commit, this should give you three numbers:

1. Number of pages in the XML dump with a `{{citation needed}}` tag.

2. Total number of pages in the XML dump. 

3. Total number of `{{citation needed}}` tags in the XML dump.

On my computer ...

