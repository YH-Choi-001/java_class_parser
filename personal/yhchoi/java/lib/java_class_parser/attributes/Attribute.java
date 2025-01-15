/**
 * 
 *  Attribute.java - A class that holds an attribute in a .class file.
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

package personal.yhchoi.java.lib.java_class_parser.attributes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import personal.yhchoi.java.lib.java_class_parser.ConstPoolRetriever;
import personal.yhchoi.java.lib.java_class_parser.constants.Constant;
import personal.yhchoi.java.lib.java_class_parser.constants.ConstantUTF8;

/**
 * An attribute in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public class Attribute
{
    // fields
    private final int nameIndex;
    private final byte[] info;
    
    private final ConstPoolRetriever consts;
    
    // private final AttrTag attrTag;
    
    /**
     * Attribute tags.
     *
     * @author Yui Hei Choi
     * @version 2025.01.10
     */
    protected enum AttrTag
    {
        /** A tag for constant value. */
        CONSTANT_VALUE,

        /** A tag for code. */
        CODE,

        /** A tag for stack map table. */
        STACK_MAP_TABLE,

        /** A tag for exceptions. */
        EXCEPTIONS,

        /** A tag for inner classes. */
        INNER_CLASSES,


        /** A tag for enclosing method. */
        ENCLOSING_METHOD,
        
        /** A tag for synthetic. */
        SYNTHETIC,

        /** A tag for signature. */
        SIGNATURE,
        
        /** A tag for source file. */
        SOURCE_FILE,
        
        /** A tag for source debug extension. */
        SOURCE_DEBUG_EXTENSION,


        /** A tag for line number table. */
        LINE_NUMBER_TABLE,
        
        /** A tag for local variable table. */
        LOCAL_VARIABLE_TABLE,
        
        /** A tag for local variable type table. */
        LOCAL_VARIABLE_TYPE_TABLE,

        
        /** A tag for deprecated. */
        DEPRECATED,
        
        /** A tag for runtime visible annotations. */
        RUNTIME_VISIBLE_ANNOTATIONS,
        
        /** A tag for runtime invisible annotations. */
        RUNTIME_INVISIBLE_ANNOTATIONS,


        /** A tag for runtime visible parameter annotations. */
        RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS,
        
        /** A tag for runtime invisible parameter annotations. */
        RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS,


        /** A tag for annotation default. */
        ANNOTATION_DEFAULT,
        
        /** A tag for bootstrap methods. */
        BOOTSTRAP_METHODS
    }
    
    private static final HashMap<String, AttrTag> attrTagsMap;  // the map from String to AttrTag
    static {
        attrTagsMap = new HashMap<>();
        
        attrTagsMap.put("ConstantValue", AttrTag.CONSTANT_VALUE);
        attrTagsMap.put("Code", AttrTag.CODE);
        attrTagsMap.put("StackMapTable", AttrTag.STACK_MAP_TABLE);
        attrTagsMap.put("Exceptions", AttrTag.EXCEPTIONS);
        attrTagsMap.put("InnerClasses", AttrTag.INNER_CLASSES);
        
        attrTagsMap.put("EnclosingMethod", AttrTag.ENCLOSING_METHOD);
        attrTagsMap.put("Synthetic", AttrTag.SYNTHETIC);
        attrTagsMap.put("Signature", AttrTag.SIGNATURE);
        attrTagsMap.put("SourceFile", AttrTag.SOURCE_FILE);
        attrTagsMap.put("SourceDebugExtension", AttrTag.SOURCE_DEBUG_EXTENSION);
        
        attrTagsMap.put("LineNumberTable", AttrTag.LINE_NUMBER_TABLE);
        attrTagsMap.put("LocalVariableTable", AttrTag.LOCAL_VARIABLE_TABLE);
        attrTagsMap.put("LocalVariableTypeTable", AttrTag.LOCAL_VARIABLE_TYPE_TABLE);
        
        attrTagsMap.put("Deprecated", AttrTag.DEPRECATED);
        attrTagsMap.put("RuntimeVisibleAnnotations", AttrTag.RUNTIME_VISIBLE_ANNOTATIONS);
        attrTagsMap.put("RuntimeInvisibleAnnotations", AttrTag.RUNTIME_INVISIBLE_ANNOTATIONS);
        
        attrTagsMap.put("RuntimeVisibleParameterAnnotations", AttrTag.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS);
        attrTagsMap.put("RuntimeInvisibleParameterAnnotations", AttrTag.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS);
        
        attrTagsMap.put("AnnotationDefault", AttrTag.ANNOTATION_DEFAULT);
        attrTagsMap.put("BootstrapMethods", AttrTag.BOOTSTRAP_METHODS);
    }
    
    /**
     * Constructor for objects of class Attribute.
     * 
     * @param consts the constant pool retriever
     * @param nameIndex the index of name of this attribute in the constant pool
     * @param info bytes of information held by this attribute
     */
    private Attribute(ConstPoolRetriever consts, int nameIndex, byte[] info)
    {
        this.consts = consts;
        this.nameIndex = nameIndex;
        this.info = info;
        // this.attrTag = attrTagsMap.get(getName());
    }
    
    /**
     * Constructor for objects of class Attribute.
     * To be invoked when recovering an attribute to a subclass attribute
     * that could provide more detailed information.
     * 
     * @param ref the original attribute
     */
    protected Attribute(Attribute ref)
    {
        if (ref != null) {
            this.consts = ref.consts;
            this.nameIndex = ref.nameIndex;
            this.info = ref.info;
            // this.attrTag = attrTagsMap.get(getName());
        } else {
            this.consts = null;
            this.nameIndex = -1;
            this.info = null;
            // this.attrTag = null;
        }
    }
    
    /**
     * Creates an attribute from the input stream.
     * 
     * @param inStream the input stream to read the .class file
     * @param consts the constant pool retriever
     * @return the newly created attribute, or null if operation failed
     * @throws IOException if the input stream fails to read the entire attribute
     */
    public static final Attribute createAttribute(DataInputStream inStream, ConstPoolRetriever consts) throws IOException
    {
        final int nameIndex = inStream.readUnsignedShort();
        final int attributeLength = inStream.readInt();
        final byte[] info = new byte[attributeLength];
        inStream.read(info);
        
        Attribute defaultAttribute = new Attribute(consts, nameIndex, info);
        
        final AttrTag tag = attrTagsMap.get(defaultAttribute.getName());
        if (tag == null) {
            return defaultAttribute;
        }
        switch (tag) {
            case CONSTANT_VALUE:
                return ConstantValueAttribute.recoverAttribute(defaultAttribute);
            case ENCLOSING_METHOD:
                return EnclosingMethodAttribute.recoverAttribute(defaultAttribute);
            case SYNTHETIC:
                return SyntheticAttribute.recoverAttribute(defaultAttribute);
            case SIGNATURE:
                return SignatureAttribute.recoverAttribute(defaultAttribute);
            case SOURCE_FILE:
                return SourceFileAttribute.recoverAttribute(defaultAttribute);
            case DEPRECATED:
                return DeprecatedAttribute.recoverAttribute(defaultAttribute);
            default:
                return defaultAttribute;
        }
    }
    
    /**
     * Recovers details from the attribute.
     *
     * @param attr the general attribute
     * @return the new attribute built with this class, or null if attribute details don't match
     * @throws IOException if the input stream fails to read the entire attribute
     */
    protected static Attribute recoverAttribute(Attribute attr) throws IOException
    {
        return new Attribute(attr);
    }
    
    /**
     * Creates a new info data input stream for subclasses to re-parse attributes.
     * 
     * @return a new <code>DataInputStream</code> for subclasses to read info[]
     */
    protected final DataInputStream createInfoInStream()
    {
        return new DataInputStream(new ByteArrayInputStream(info));
    }
    
    /**
     * Gets a constant from the constant pool.
     * 
     * @param index the index of the constant
     * @return the requested constant
     * @throws IndexOutOfBoundsException if <code>index</code> is outside of valid range
     */
    protected final Constant getConstFromPool(int index) throws IndexOutOfBoundsException
    {
        return consts.getConstPool(index);
    }
    
    /**
     * Gets the name of the attribute.
     * 
     * @return the name of the attribute
     */
    public final String getName()
    {
        return ((ConstantUTF8)getConstFromPool(nameIndex)).getString();
    }
    
    /**
     * Gets the length of the info in this attribute.
     * 
     * @return the length of the info in this attribute
     */
    public final int getInfoLength()
    {
        return info.length;
    }
    
    /**
     * Gets a single byte of info from this attribute.
     * 
     * @param index the index of the byte of info to be obtained
     * @return the requested byte of info
     */
    public final byte getInfo(int index)
    {
        return info[index];
    }
}
