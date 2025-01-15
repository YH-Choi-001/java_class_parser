/**
 * 
 *  Member.java - A class that holds a member (either field or method) in a .class file.
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

package personal.yhchoi.java.lib.java_class_parser.members;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import personal.yhchoi.java.lib.java_class_parser.ConstPoolRetriever;
import personal.yhchoi.java.lib.java_class_parser.attributes.Attribute;
import personal.yhchoi.java.lib.java_class_parser.constants.Constant;
import personal.yhchoi.java.lib.java_class_parser.constants.ConstantUTF8;

/**
 * A member (either field or method) in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public abstract class Member
{
    private static final int ACC_PUBLIC         = 0x0001;
    private static final int ACC_PRIVATE        = 0x0002;
    private static final int ACC_PROTECTED      = 0x0004;
    private static final int ACC_STATIC         = 0x0008;
    private static final int ACC_FINAL          = 0x0010;
    private static final int ACC_SYNTHETIC      = 0x1000;
    
    // fields
    private final int accessFlags;
    private final int nameIndex;
    private final int descriptorIndex;
    private final Attribute[] attributes;
    
    private final ConstPoolRetriever consts;
    
    /**
     * Constructor for objects of class Member.
     * 
     * @param consts the constant pool retriever
     * @param accessFlags the access flags of this member
     * @param nameIndex the index of name of this member
     * @param descriptorIndex the index of descriptor of this member
     * @param attributes attributes of this member
     */
    protected Member(ConstPoolRetriever consts, int accessFlags, int nameIndex, int descriptorIndex, Attribute[] attributes)
    {
        this.consts = consts;
        this.accessFlags = accessFlags;
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
        this.attributes = attributes;
    }
    
    
    /**
     * Creates a field or method from the input stream.
     * 
     * @param inStream the input stream to read the .class file
     * @param consts the constant pool retriever
     * @param isMethod true to create a method, false to create a field
     * @return the newly created field or method, or null if operation failed
     * @throws IOException if the input stream fails to read the entire member
     */
    protected static final Member createMember(DataInputStream inStream, ConstPoolRetriever consts, boolean isMethod) throws IOException
    {
        final int accessFlags = inStream.readUnsignedShort();
        final int nameIndex = inStream.readUnsignedShort();
        final int descriptorIndex = inStream.readUnsignedShort();
        final int attributesCount = inStream.readUnsignedShort();
        final Attribute[] attributes = new Attribute[attributesCount];
        for (int i = 0; i < attributesCount; i++) {
            attributes[i] = Attribute.createAttribute(inStream, consts);
        }
        
        if (isMethod) {
            return new Method(consts, accessFlags, nameIndex, descriptorIndex, attributes);
        } else {
            return new Field(consts, accessFlags, nameIndex, descriptorIndex, attributes);
        }
    }
    
    /**
     * Checks if an access flag is set or not.
     * 
     * @param flag the flag to be checked
     * @return true if the specific access flag is set, false otherwise
     */
    protected final boolean checkAccessFlag(int flag)
    {
        return (accessFlags & flag) != 0;
    }
    
    /**
     * Checks if this member is <code>public</code>.
     * If this is the case, then this member could be accessed outside its class.
     * 
     * @return <code>true</code> if declared <code>public</code>
     */
    public final boolean isPublic()
    {
        return checkAccessFlag(ACC_PUBLIC);
    }
    
    /**
     * Checks if this member is <code>private</code>.
     * If this is the case, then this member could only be accessed by its own class.
     * 
     * @return <code>true</code> if declared <code>private</code>
     */
    public final boolean isPrivate()
    {
        return checkAccessFlag(ACC_PRIVATE);
    }
    
    /**
     * Checks if this member is <code>protected</code>.
     * If this is the case, then this member could only be accessed by its own class
     * and its ancestors and descendants classes.
     * 
     * @return <code>true</code> if declared <code>protected</code>
     */
    public final boolean isProtected()
    {
        return checkAccessFlag(ACC_PROTECTED);
    }
    
    /**
     * Checks if this member is <code>static</code>.
     * If this is the case, then this member exists independently of any objects of its class.
     * 
     * @return <code>true</code> if declared <code>static</code>
     */
    public final boolean isStatic()
    {
        return checkAccessFlag(ACC_STATIC);
    }
    
    /**
     * Checks if this member is <code>final</code>.
     * If this is the case, then this member could not be: 
     *     field) edited; or 
     *     method) overriden 
     * by its subclasses.
     * 
     * @return <code>true</code> if declared <code>final</code>
     */
    public final boolean isFinal()
    {
        return checkAccessFlag(ACC_FINAL);
    }
    
    /**
     * Checks if this member is <code>synthetic</code>.
     * If this is the case, then this member is generated by compiler
     * and does not exist in source code.
     * 
     * @return <code>true</code> if code is <code>synthetic</code>; generated by compiler and does not exist in source code
     */
    public final boolean isSynthetic()
    {
        return checkAccessFlag(ACC_SYNTHETIC);
    }
    
    /**
     * Gets a constant from the constant pool.
     * 
     * @param index the index of the constant
     * @return the requested constant
     */
    private Constant getConstFromPool(int index)
    {
        return consts.getConstPool(index);
    }
    
    /**
     * Gets the name constant of this member.
     * 
     * @return the name constant of this member
     */
    public final String getName()
    {
        return ((ConstantUTF8)getConstFromPool(nameIndex)).getString();
    }
    
    /**
     * Gets the descriptor constant of this member.
     * 
     * @return the descriptor constant of this member
     */
    protected final String getDescriptor()
    {
        return ((ConstantUTF8)getConstFromPool(descriptorIndex)).getString();
    }
    
    /**
     * Gets the number of attributes held by this member.
     * 
     * @return the number of attributes held by this member
     */
    public final int getAttributesCount()
    {
        return attributes.length;
    }
    
    /**
     * Gets an attribute held by this member.
     * 
     * @param index the index of the attribute
     * @return the requested attribute
     */
    public final Attribute getAttribute(int index)
    {
        return attributes[index];
    }

    /**
     * Gets only 1 type from a descriptor.
     * 
     * @param descriptor the descriptor
     * @return the type in the form <code>"java.util.Object"</code>, or null if not exactly 1 type is detected
     */
    protected static final String getTypeFromDescriptor(String descriptor)
    {
        final List<String> types = getTypesFromDescriptor(descriptor);
        if (types == null || types.size() != 1) {
            return null;
        }
        return types.get(0);
    }
    
    /**
     * Gets the types from a descriptor.
     * 
     * @param descriptor the descriptor
     * @return a list of the type in the form <code>"java.util.Object"</code>
     */
    protected static final List<String> getTypesFromDescriptor(String descriptor)
    {
        ArrayList<String> types = new ArrayList<>();
        while (!descriptor.isEmpty()) {
            String type = "";
            boolean sameType = false;
            do {
                sameType = false;
                switch (descriptor.charAt(0)) {
                    case '[':
                        type += "[]";
                        sameType = true;
                        descriptor = descriptor.substring(1);
                        break;
                    case 'L':
                        final int nextSemicolonIndex = descriptor.indexOf(";");
                        type = descriptor.substring(1, nextSemicolonIndex) + type;
                        descriptor = descriptor.substring(nextSemicolonIndex + 1);
                        break;
                    case 'B':
                        type = "byte" + type;
                        descriptor = descriptor.substring(1);
                        break;
                    case 'C':
                        type = "char" + type;
                        descriptor = descriptor.substring(1);
                        break;
                    case 'D':
                        type = "double" + type;
                        descriptor = descriptor.substring(1);
                        break;
                    case 'F':
                        type = "float" + type;
                        descriptor = descriptor.substring(1);
                        break;
                    case 'I':
                        type = "int" + type;
                        descriptor = descriptor.substring(1);
                        break;
                    case 'J':
                        type = "long" + type;
                        descriptor = descriptor.substring(1);
                        break;
                    case 'S':
                        type = "short" + type;
                        descriptor = descriptor.substring(1);
                        break;
                    case 'Z':
                        type = "boolean" + type;
                        descriptor = descriptor.substring(1);
                        break;
                }
            } while (sameType);
            // type = type.replace('/', '.');
            // type = type.replace('$', '.');
            types.add(type);
        }
        return types;
    }
    
    /**
     * Gets a compound type (a type with generic type) from a signature.
     * 
     * @param signature the signature
     * @return a type in the form <code>"java.util.HashMap&lt;java.lang.String, java.util.Integer&gt;"</code>
     */
    public static final String getCompoundTypeFromSignature(String signature)
    {
        // HashMap<int[], int[]>
        // Ljava/util/HashMap<[I[I>;
        
        // HashMap<Integer, Constant.ConstPoolTab>
        // Ljava/util/HashMap<Ljava/lang/Integer;LConstant$ConstPoolTag;>;
        
        // HashMap<HashMap<String, Integer>, ArrayList<String>>
        // Ljava/util/HashMap<Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Integer;>;Ljava/util/ArrayList<Ljava/lang/String;>;>;
        
        // HashMap<HashMap<String, int[]>, ArrayList<int[]>>
        // Ljava/util/HashMap<Ljava/util/HashMap<Ljava/lang/String;[I>;Ljava/util/ArrayList<[I>;>;
        // Ljava/util/HashMap< Ljava/util/HashMap<Ljava/lang/String;[I>; Ljava/util/ArrayList<[I>; >;
        
        String toReturn = "";
        
        boolean withinClass = false;
        int arrayDepth = 0;
        
        char[] signatureArray = signature.toCharArray();
        
        for (int i = 0; i < signatureArray.length; i++) {
            final char c = signatureArray[i];
            boolean endOfOneType = false;
            if (withinClass) {
                switch (c) {
                    case '<':
                        withinClass = false;
                        toReturn += c;
                        break;
                    case ';':
                        withinClass = false;
                        endOfOneType = true;
                        break;
                    default:
                        toReturn += c;
                }
            } else {
                switch (c) {
                    case '[':
                        arrayDepth++;
                        break;
                    case 'L':
                        withinClass = true;
                        break;
                    case '>':
                        withinClass = true;
                        toReturn += c;
                        break;
                    case 'B':
                        toReturn += "byte";
                        endOfOneType = true;
                        break;
                    case 'C':
                        toReturn += "char";
                        endOfOneType = true;
                        break;
                    case 'D':
                        toReturn += "double";
                        endOfOneType = true;
                        break;
                    case 'F':
                        toReturn += "float";
                        endOfOneType = true;
                        break;
                    case 'I':
                        toReturn += "int";
                        endOfOneType = true;
                        break;
                    case 'J':
                        toReturn += "long";
                        endOfOneType = true;
                        break;
                    case 'S':
                        toReturn += "short";
                        endOfOneType = true;
                        break;
                    case 'Z':
                        toReturn += "boolean";
                        endOfOneType = true;
                        break;
                }
            }
            if (endOfOneType) {
                for (int z = 0; z < arrayDepth; z++) toReturn += "[]";
                arrayDepth = 0;
                if ((i + 1) < signatureArray.length && signatureArray[i + 1] != '>') {
                    toReturn += ", ";
                }
            }
        }
        
        return toReturn;
    }
}
