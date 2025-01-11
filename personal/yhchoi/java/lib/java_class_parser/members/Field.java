/**
 * 
 *  Field.java - A class that holds a field in a .class file.
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

/**
 * A field in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2024.12.21
 */
public class Field extends Member
{
    /** Declared <code>volatile</code>; cannot be cached. */
    private static final int ACC_VOLATILE       = 0x0040;
    
    /** Declared <code>transient</code>; not written or read by a persistent object manager. */
    private static final int ACC_TRANSIENT      = 0x0080;
    
    /** Declared as an element of an <code>enum</code>. */
    private static final int ACC_ENUM           = 0x4000;
    
    /**
     * Constructor for objects of class Field.
     */
    protected Field(ConstPoolRetriever consts, int accessFlags, int nameIndex, int descriptorIndex, Attribute[] attributes)
    {
        super(consts, accessFlags, nameIndex, descriptorIndex, attributes);
    }
    
    /**
     * Creates a field from the input stream.
     * 
     * @param inStream the input stream to read the .class file
     * @param consts the constant pool retriever
     * @return the newly created field, or null if operation failed
     */
    public static final Field createField(DataInputStream inStream, ConstPoolRetriever consts) throws IOException
    {
        return (Field)createMember(inStream, consts, false);
    }
    
    /**
     * Gets the field type from the field descriptor.
     * 
     * @return the field type in the form <code>java.util.Object</code>
     */
    public final String getType()
    {
        return getTypeFromDescriptor(getDescriptor());
    }
    
    /**
     * Gets only 1 type from a descriptor.
     * 
     * @param descriptor the descriptor
     * @return the type in the form <code>"java.util.Object"</code>, or null if not exactly 1 type is detected
     */
    public static final String getTypeFromDescriptor(String descriptor)
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
    public static final List<String> getTypesFromDescriptor(String descriptor)
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
    
    /**
     * @return true if declared <code>volatile</code>; cannot be cached
     */
    public final boolean isVolatile()
    {
        return checkAccessFlag(ACC_VOLATILE);
    }
    
    /**
     * @return true if declared <code>transient</code>; not written or read by a persistent object manager
     */
    public final boolean isTransient()
    {
        return checkAccessFlag(ACC_TRANSIENT);
    }
    
    /**
     * @return true if declared as an element of an <code>enum</code>
     */
    public final boolean isEnum()
    {
        return checkAccessFlag(ACC_ENUM);
    }
}
