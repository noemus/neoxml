# XTree
This is my attempt to rewrite dom4j xml library for newer version of java (1.7 and above). 
I have renamed it to eliminate some licensing issues.

My intent is to keep this library simple in design and use. 

I am using dom4j in many my projects, but it is now too old and whithout maintentnace

GOALS:
- Focus is on simple traversal API based on java.util Collections framework (as in dom4j)
- I have (and will) remove some parts of former dom4j that are not necessary and that only added some dependencies. I will maybe - if asked for - create some separate modules for theese parts. (Swing, Datatype, Xpp) 
- I have also added some new APIs that are more functional and are compatible with java 1.8 lambdas.
- I have extended implementation of visitor pattern - which is one of top features of former dom4j library

NOTICE:
This is early version of XTree and I am now focusing on JUnit test coverage and code clarity
