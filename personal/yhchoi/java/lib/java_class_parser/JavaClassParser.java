/**
 * 
 *  JavaClassParser.java - A class that retrieves data from a java .class file.
 *  Copyright (C) 2024 YH Choi
 *
 *  This program is licensed under BSD 3-Clause License.
 *  See LICENSE.txt for details.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 *  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package personal.yhchoi.java.lib.java_class_parser;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import personal.yhchoi.java.lib.java_class_parser.attributes.Attribute;
import personal.yhchoi.java.lib.java_class_parser.attributes.ConstantValueAttribute;
import personal.yhchoi.java.lib.java_class_parser.attributes.SignatureAttribute;
import personal.yhchoi.java.lib.java_class_parser.constants.Constant;
import personal.yhchoi.java.lib.java_class_parser.constants.ConstantClass;
import personal.yhchoi.java.lib.java_class_parser.constants.ConstantDouble;
import personal.yhchoi.java.lib.java_class_parser.constants.ConstantFloat;
import personal.yhchoi.java.lib.java_class_parser.constants.ConstantInteger;
import personal.yhchoi.java.lib.java_class_parser.constants.ConstantLong;
import personal.yhchoi.java.lib.java_class_parser.constants.ConstantString;
import personal.yhchoi.java.lib.java_class_parser.members.Field;
import personal.yhchoi.java.lib.java_class_parser.members.Member;
import personal.yhchoi.java.lib.java_class_parser.members.Method;

/**
 * A parser to retrieve data from java .class files.
 *
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public class JavaClassParser implements ConstPoolRetriever
{
    // fields
    private File file;
    
    private boolean isClassFileValid;
    private int minorVersion;
    private int majorVersion;
    private Constant[] constPool;
    
    private int accessFlags;
    
    private ConstantClass thisClass;
    private ConstantClass superClass;
    
    private ConstantClass[] interfaces;
    
    private Field[] fields;
    
    private Method[] methods;
    
    private Attribute[] attributes;
    
    private static int ACC_PUBLIC     = 0x0001;
    private static int ACC_FINAL      = 0x0010;
    private static int ACC_SUPER      = 0x0020;
    private static int ACC_INTERFACE  = 0x0200;
    private static int ACC_ABSTRACT   = 0x0400;
    private static int ACC_SYNTHETIC  = 0x1000;
    private static int ACC_ANNOTATION = 0x2000;
    private static int ACC_ENUM       = 0x4000;
    private static int ACC_MODULE     = 0x8000;
    
    /**
     * Constructor for objects of class JavaClassParser.
     * 
     * @param filePath the file path to the .class file
     */
    public JavaClassParser(String filePath)
    {
        this(new File(filePath));
    }
    
    /**
     * Constructor for objects of class JavaClassParser.
     * 
     * @param file the .class file
     */
    public JavaClassParser(File file)
    {
        this.file = file;
        
        isClassFileValid = false;
        minorVersion = 0;
        majorVersion = 0;
        constPool = null;
        
        accessFlags = 0;
        
        thisClass = null;
        superClass = null;
        
        interfaces = null;
        
        fields = null;
        
        methods = null;
        
        attributes = null;
    }
    
    /**
     * Parses the .class file.
     * You may invoke this method on another thread and come back when parsing is finished.
     * @throws IOException if I/O error occurs during parsing
     * @throws JavaClassFormatException if an illegal java class format is detected
     */
    public void parse() throws IOException, JavaClassFormatException
    {
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }
        final DataInputStream inStream = new DataInputStream(
            new BufferedInputStream(new FileInputStream(file)));
        
        // parse magic number
        // check if class file is valid
        final int magicNumber = inStream.readInt();
        isClassFileValid = (magicNumber == (int)0xCAFEBABE);
        
        if (!isClassFileValid) {
            inStream.close();
            return;
        }
        
        // parse minor version
        minorVersion = inStream.readUnsignedShort();
        
        // parse major version
        majorVersion = inStream.readUnsignedShort();
        
        // parse const pool count
        final int constPoolCount = inStream.readUnsignedShort();
        
        // parse constant pool
        constPool = new Constant[constPoolCount - 1];
        for (int i = 0; i < constPool.length; i++) {
            constPool[i] = Constant.createConst(inStream, this);
            if (constPool[i] instanceof ConstantLong || constPool[i] instanceof ConstantDouble) {
                // ConstantLong and ConstantDouble take up 2 entries in the constant pool.
                // We need to skip an index
                // The skipped index is valid but unusable.
                // What a bad design.
                i++;
            }
        }
        
        // parse access flags
        accessFlags = inStream.readUnsignedShort();
        
        // parse this class index
        final int thisClassIndex = inStream.readUnsignedShort();
        thisClass = (ConstantClass)getConstPool(thisClassIndex);
        
        // parse super class index
        final int superClassIndex = inStream.readUnsignedShort();
        superClass = ((superClassIndex != 0) ? (ConstantClass)getConstPool(superClassIndex) : null);
        
        // parse interfaces count
        final int interfacesCount = inStream.readUnsignedShort();
        
        // parse interfaces
        interfaces = new ConstantClass[interfacesCount];
        for (int i = 0; i < interfacesCount; i++) {
            final int interfaceIndex = inStream.readUnsignedShort();
            interfaces[i] = (ConstantClass)getConstPool(interfaceIndex);
        }
        
        // parse fields count
        final int fieldsCount = inStream.readUnsignedShort();
        
        // parse fields
        fields = new Field[fieldsCount];
        for (int i = 0; i < fieldsCount; i++) {
            fields[i] = Field.createField(inStream, this);
        }
        
        // parse methods count
        final int methodsCount = inStream.readUnsignedShort();
        
        // parse methods
        methods = new Method[methodsCount];
        for (int i = 0; i < methodsCount; i++) {
            methods[i] = Method.createMethod(inStream, this);
        }
        
        // parse attributes count
        final int attributesCount = inStream.readUnsignedShort();
        
        // parse attributes
        attributes = new Attribute[attributesCount];
        for (int i = 0; i < attributesCount; i++) {
            attributes[i] = Attribute.createAttribute(inStream, this);
        }
        
        inStream.close();
    }
    
    /**
     * Gets whether the .class file is valid.
     * 
     * @return true if the .class file is valid, false otherwise
     */
    public final boolean isClassFileValid()
    {
        return isClassFileValid;
    }
    
    /**
     * Gets the minor version of the .class file.
     * 
     * @return the minor version of the .class file
     */
    public final int getMinorVersion()
    {
        return minorVersion;
    }
    
    /**
     * Gets the major version of the .class file.
     * 
     * @return the major version of the .class file
     */
    public final int getMajorVersion()
    {
        return majorVersion;
    }
    
    /**
     * Gets a constant element from the constant pool.
     * 
     * @param index the index of the constant element to be obtained
     * @return the constant element to be obtained
     * @throws IndexOutOfBoundsException if <code>((index &lt; 1) || (index &gt;= constPool.length + 1))</code>
     */
    @Override
    public final Constant getConstPool(int index) throws IndexOutOfBoundsException
    {
        final int mappedIndex = index - 1; // Note that: The constant_pool table is indexed from 1 to constant_pool_count-1.
        if (mappedIndex < 0 || mappedIndex >= constPool.length) {
            throw new IndexOutOfBoundsException();
        }
        return constPool[mappedIndex];
    }
    
    /**
     * Checks if an access flag is set or not.
     * 
     * @param flag the flag to be checked
     * @return true if the specific access flag is set, false otherwise
     */
    private boolean checkAccessFlag(int flag)
    {
        return (accessFlags & flag) != 0;
    }
    
    /**
     * Checks if this class is <code>public</code>.
     * If this is the case, then the class may be accessed from outside its package.
     * 
     * @return <code>true</code> if declared <code>public</code>; may be accessed from outside its package
     */
    public final boolean isPublic()
    {
        return checkAccessFlag(ACC_PUBLIC);
    }
    
    /**
     * Checks if this class is <code>final</code>.
     * If this is the case, then no subclasses are allowed.
     * 
     * @return <code>true</code> if declared <code>final</code>; no subclasses allowed
     */
    public final boolean isFinal()
    {
        return checkAccessFlag(ACC_FINAL);
    }
    
    /**
     * Checks if this class treats superclass methods specially
     * when invoked by the <code>invokespecial</code> instruction.
     * 
     * @return <code>true</code> if treat superclass methods specially when invoked by the <code>invokespecial</code> instruction
     */
    public final boolean isSuper()
    {
        return checkAccessFlag(ACC_SUPER);
    }
    
    /**
     * Checks if this file represents an <code>interface</code>.
     * 
     * @return <code>true</code> if is an <code>interface</code>, not a <code>class</code>
     * @see #isAbstract()
     * @see #isEnum()
     * @see #isModule()
     */
    public final boolean isInterface()
    {
        return checkAccessFlag(ACC_INTERFACE);
    }
    
    /**
     * Checks if this class is <code>abstract</code>.
     * Note that <code>interface</code> are always <code>abstract</code>.
     * 
     * @return <code>true</code> if declared <code>abstract</code>; must not be instantiated
     * @see #isInterface()
     */
    public final boolean isAbstract()
    {
        return checkAccessFlag(ACC_ABSTRACT);
    }
    
    /**
     * Checks if this class is <code>synthetic</code>.
     * If this is the case, then it is not present in the original source code.
     * 
     * @return <code>true</code> if declared <code>synthetic</code>; not present in the source code
     */
    public final boolean isSynthetic()
    {
        return checkAccessFlag(ACC_SYNTHETIC);
    }
    
    /**
     * Checks if this class is of annotation type.
     * 
     * @return <code>true</code> if declared as an annotation type
     */
    public final boolean isAnnotation()
    {
        return checkAccessFlag(ACC_ANNOTATION);
    }
    
    /**
     * Checks if this file represents an <code>enum</code>.
     * 
     * @return <code>true</code> if declared as an <code>enum</code> type
     * @see #isInterface()
     * @see #isModule()
     */
    public final boolean isEnum()
    {
        return checkAccessFlag(ACC_ENUM);
    }
    
    /**
     * Checks if this file represents a <code>module</code>.
     * 
     * @return <code>true</code> if declared the file as a module but not a class nor interface
     * @see #isInterface()
     * @see #isEnum()
     */
    public final boolean isModule()
    {
        return checkAccessFlag(ACC_MODULE);
    }
    
    /**
     * Gets the name of this class.
     * 
     * @return the name of this class
     * @see #getNameOfSuperClass()
     */
    public final String getNameOfThisClass()
    {
        return thisClass.getName();
    }
    
    /**
     * Gets the name of super class.
     * 
     * @return the name of super class, or null if this class is java.lang.Object
     * @see #getNameOfThisClass()
     */
    public final String getNameOfSuperClass()
    {
        if (superClass == null) {
            return null;
        }
        return superClass.getName();
    }
    
    /**
     * Gets the number of interfaces in this class file.
     * 
     * @return the number of interfaces in this class file
     * @see #getInterface(int)
     */
    public final int getInterfacesCount()
    {
        return interfaces.length;
    }
    
    /**
     * Gets a specific interface in this class file.
     * 
     * @param index the index of the interface
     * @return the requested interface
     * @throws IndexOutOfBoundsException if <code>((index &lt; 0) || (index &gt;= getInterfacesCount()))</code>
     * @see #getInterfacesCount()
     */
    public final ConstantClass getInterface(int index)
    {
        return interfaces[index];
    }
    
    /**
     * Gets the number of fields in this class file.
     * 
     * @return the number of fields in this class file
     * @see #getField(int)
     */
    public final int getFieldsCount()
    {
        return fields.length;
    }
    
    /**
     * Gets a specific field in this class file.
     * 
     * @param index the index of the field
     * @return the requested field
     * @throws IndexOutOfBoundsException if <code>((index &lt; 0) || (index &gt;= getFieldsCount()))</code>
     * @see #getFieldsCount()
     */
    public final Field getField(int index) throws IndexOutOfBoundsException
    {
        return fields[index];
    }
    
    /**
     * Gets the number of methods in this class file.
     * 
     * @return the number of methods in this class file
     * @see #getMethod(int)
     */
    public final int getMethodsCount()
    {
        return methods.length;
    }
    
    /**
     * Gets a specific method in this class file.
     * 
     * @param index the index of the method
     * @return the requested method
     * @throws IndexOutOfBoundsException if <code>((index &lt; 0) || (index &gt;= getMethodsCount()))</code>
     * @see #getMethodsCount()
     */
    public final Method getMethod(int index) throws IndexOutOfBoundsException
    {
        return methods[index];
    }
    
    /**
     * Retrieves the class header of the processing class
     * and provides a C-header-file-like Java code.
     * 
     * @param writer a buffered writer to write strings to the destination
     */
    public final void writeClassHeader(BufferedOutput writer)
    {
        // maybe auto-generated by compiler
        if (isSynthetic()) {
            writer.append("// This class is auto-generated by compiler.\n");
        }
        
        // visibility modifier
        writer.append(isPublic() ? "public " : "private ");
        
        // inheritance situation
        if (isFinal()) {
            writer.append("final ");
        }
        if (isAbstract()) {
            writer.append("abstract ");
        }
        
        // type
        if (isInterface()) {
            writer.append("interface ");
        } else if (isAnnotation()) {
            writer.append("@interface ");
        } else if (isEnum()) {
            writer.append("enum ");
        } else if (isModule()) {
            writer.append("module ");
        } else {
            writer.append("class ");
        }
        
        // class name
        final String thisClassName = getNameOfThisClass().replace('/', '.');
        writer.append(thisClassName);
        
        // maybe extends a super class
        final String superClassName = getNameOfSuperClass().replace('/', '.').replace('$', '.');
        if (superClassName != null) {
            if (!"java.lang.Object".equals(superClassName) && !"java.lang.Enum".equals(superClassName)) {
                writer.append(" extends " + superClassName);
            }
        }
        
        // maybe implements some interfaces
        if (getInterfacesCount() > 0) {
            writer.append(" implements " + getInterface(0).getName().replace('/', '.').replace('$', '.'));
            for (int i = 1; i < getInterfacesCount(); i++) {
                writer.append(", " + getInterface(i).getName().replace('/', '.').replace('$', '.'));
            }
        }
        
        writer.append(" {\n");
        
        // fields
        final int fieldsCount = getFieldsCount();
        if (fieldsCount > 0) {
            writer.append("    // fields\n");
            for (int i = 0; i < fieldsCount; i++) {
                final Field field = getField(i);
                
                // indentation
                writer.append("    ");
                
                // special case for enum objects
                if (field.isEnum()) {
                    writer.append(field.getName());
                    if (((i + 1) < getFieldsCount()) && getField(i + 1).isEnum()) {
                        writer.append(",");
                    }
                    writer.append("\n");
                    continue;
                }
                
                // visibility modifier
                if (field.isPublic()) {
                    writer.append("public ");
                }
                if (field.isProtected()) {
                    writer.append("protected ");
                }
                if (field.isPrivate()) {
                    writer.append("private ");
                }
                if (field.isStatic()) {
                    writer.append("static ");
                }
                if (field.isFinal()) {
                    writer.append("final ");
                }
                if (field.isTransient()) {
                    writer.append("transient ");
                }
                if (field.isVolatile()) {
                    writer.append("volatile ");
                }
                
                // field type
                String fieldType = field.getType();
                for (int j = 0; j < field.getAttributesCount(); j++) {
                    final Attribute attr = field.getAttribute(j);
                    if (attr instanceof SignatureAttribute) {
                        fieldType = Member.getCompoundTypeFromSignature(((SignatureAttribute)attr).getSignature());
                        break;
                    }
                }
                writer.append(fieldType.replace('/', '.').replace('$', '.') + " ");
                
                // field identifier
                writer.append(field.getName());
                
                // static final fields maybe have values attached if it is of primitive type or a string literal
                if (field.isStatic() && field.isFinal()) {
                    for (int j = 0; j < field.getAttributesCount(); j++) {
                        final Attribute attr = field.getAttribute(j);
                        if (attr instanceof ConstantValueAttribute) {
                            final Constant c = ((ConstantValueAttribute)attr).getConstantValue();
                            
                            if (c instanceof ConstantString) {
                                writer.append(" = " + unparseString(((ConstantString)c).getString()));
                            } else if (c instanceof ConstantInteger) {
                                writer.append(" = " + ((ConstantInteger)c).getValue());
                            } else if (c instanceof ConstantFloat) {
                                writer.append(" = " + ((ConstantFloat)c).getValue() + "f");
                            } else if (c instanceof ConstantLong) {
                                writer.append(" = " + ((ConstantLong)c).getValue());
                            } else if (c instanceof ConstantDouble) {
                                writer.append(" = " + ((ConstantDouble)c).getValue());
                            }
                        }
                    }
                }
                
                // line ending semicolon
                writer.append(";");
                
                // maybe auto-generated by compiler
                if (field.isSynthetic()) {
                    writer.append(" // This synthetic field is auto-generated by compiler.");
                }
                
                // new line
                writer.append("\n");
            }
        }
        
        final int methodsCount = getMethodsCount();
        if (fieldsCount > 0 && methodsCount > 0) {
            writer.append("    \n");
        }
        
        // methods
        if (methodsCount > 0) {
            writer.append("    // methods\n");
            for (int i = 0; i < getMethodsCount(); i++) {
                final Method method = getMethod(i);
                final String methodName = method.getName();
                
                // indentation
                writer.append("    ");
                
                // visibility modifier
                if (method.isPublic()) {
                    writer.append("public ");
                }
                if (method.isProtected()) {
                    writer.append("protected ");
                }
                if (method.isPrivate()) {
                    writer.append("private ");
                }
                if (method.isAbstract()) {
                    writer.append("abstract ");
                }
                if (method.isStatic()) {
                    writer.append("static ");
                }
                if (method.isFinal()) {
                    writer.append("final ");
                }
                if (method.isSynchronized()) {
                    writer.append("synchronized ");
                }
                if (method.isNative()) {
                    writer.append("native ");
                }
                if (method.isStrict()) {
                    writer.append("strictfp ");
                }
                
                if ("<clinit>".equals(methodName)) {
                    // method is static field initializer
                    writer.append(thisClassName);
                } else if ("<init>".equals(methodName)) {
                    // method is constructor
                    writer.append(thisClassName);
                } else {
                    // method return type
                    writer.append(method.getReturnType().replace('/', '.').replace('$', '.') + " ");
                    
                    // method identifier
                    writer.append(method.getName());
                }
                
                // method parameters
                writer.append("(" + method.getParameters().replace('/', '.').replace('$', '.') + ")");
                
                // line ending semicolon
                writer.append(";");
                
                // maybe auto-generated by compiler
                if (method.isBridge()) {
                    writer.append(" // This bridge method is auto-generated by compiler.");
                }
                if (method.isSynthetic()) {
                    writer.append(" // This synthetic method is auto-generated by compiler.");
                }
                
                // maybe static field initializer
                if ("<clinit>".equals(methodName)) {
                    writer.append(" // This static method is used by JVM to initialize static fields in this class.");
                }
                
                // new line
                writer.append("\n");
            }
        }
        
        writer.append("}");
    }
    
    /**
     * Converts a string back to Java compiling format.
     * 
     * @param input the raw string
     * @return the Java compiling format of string
     */
    private static String unparseString(String input)
    {
        String toReturn = "\"";
        if (input == null || input.isEmpty()) {
            return "";
        }
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            switch (c) {
                case '\\':
                    toReturn += "\\\\";
                    break;
                case '\'':
                    toReturn += "\\\'";
                    break;
                case '\"':
                    toReturn += "\\\"";
                    break;
                case '\b':
                    toReturn += "\\b";
                    break;
                case '\f':
                    toReturn += "\\f";
                    break;
                case '\n':
                    toReturn += "\\n";
                    break;
                case '\r':
                    toReturn += "\\r";
                    break;
                case '\s':
                    toReturn += "\\s";
                    break;
                case '\t':
                    toReturn += "\\t";
                    break;
                default:
                    if (c >= 0x20 && c <= 0x7e) {
                        toReturn += c;
                    } else {
                        String strRep = Integer.toString((int)c, 16);
                        while (strRep.length() < 4) {
                            strRep = "0" + strRep;
                        }
                        toReturn += "\\u" + strRep;
                    }
                    break;
            }
        }
        toReturn += "\"";
        return toReturn;
    }
    
    /**
     * Retrieves the class header of the processing class.
     * 
     * @return provides a C-header-file-like Java code
     * @see #writeClassHeader(BufferedOutput)
     */
    public final String getClassHeader()
    {
        class MyWriter implements BufferedOutput {
            private String result;
            public MyWriter() {
                result = "";
            }
            @Override
            public void append(String text) {
                result += text;
            }
            public String getResult() {
                return result;
            }
        }
        
        final MyWriter writer = new MyWriter();
        writeClassHeader(writer);
        return writer.getResult();
    }
    
    /**
     * Retrieves the class header of the processing class
     * and prints a C-header-file-like Java code.
     * 
     * @see #writeClassHeader(BufferedOutput)
     * @see #getClassHeader()
     */
    public final void printClassHeader()
    {
        writeClassHeader(t -> System.out.print(t));
    }
    
    /**
     * An interface to allow appending text
     * with sufficient buffer to avoid overcrowding.
     * 
     * @author Yui Hei Choi
     * @version 2025.01.10
     */
    public interface BufferedOutput
    {
        /**
         * Appends a string of text to the buffered output.
         * 
         * @param text the text to be appended
         */
        public void append(String text);
    }
}
