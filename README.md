# NeoXML
This is my attempt to rewrite dom4j xml library for newer version of java (1.8 and above). 
I have renamed it to eliminate some licensing issues.

My intent is to keep this library simple in design and use. 

I am using dom4j in many my projects, but it is now too old and whithout maintentnace

GOALS:
- Focus is on simple traversal API based on java.util Collections framework ( as in dom4j )
- I have (and will) remove some parts of former dom4j that are not necessary and that only added some dependencies. I will maybe - if asked for - create some separate modules for theese parts. (Swing, Datatype, Xpp, etc)

WHAT IS DONE:
- All code is refactored and reorganised, nearly all code duplications are removed
- I have added API for NodeList for better manipulation with collections of Nodes
- Branch (and all container nodes) now implements Iterable
- I have added some new APIs that are more functional and are compatible with java 1.8 lambdas.
- I have also extended implementation of visitor pattern - which is one of top features of former dom4j library
- Done some benchmarks (based on JMH) against original dom4j, neoxml performs better in some cases and nearly same in other scenarios, whole library is optimised for XML documents with many nodes without subnodes or with only one subnode 

NOTICE:
This is early version of NeoXML, and I am now focusing on JUnit test coverage and code clarity
