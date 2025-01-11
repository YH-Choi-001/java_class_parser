# java_class_parser  
## A powerful tool to parse java class files.  
  
Title: Java Class Parser  
Brief: A library that parses java .class files into Java programs.  
Author: YH Choi  
Build: This is a library under `package personal.yhchoi.java.lib.java_class_parser;`. See details below for how to use.  
  
### How to Use  
  
#### Read class files  
import statement:  
```
import personal.yhchoi.java.lib.java_class_parser.JavaClassParser;      // for using JavaClassParser  
```  
  
constructor:  
`JavaClassParser parser = new JavaClassParser(new File("./Hello.class"));`  
or  
`JavaClassParser parser = new JavaClassParser("./Hello.class");`  
  
read:  
```
try {
    parser.parse();
} catch (IOException e) {
    e.printStackTrace();
}
if (parser.isClassFileValid()) {
    final int majorVers = parser.getMajorVersion();
    final int minorVers = parser.getMinorVersion();
    // ... other operations
}
```