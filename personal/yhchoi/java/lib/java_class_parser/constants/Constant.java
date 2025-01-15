/**
 * 
 *  Constant.java - A class that holds a constant in the constant pool in a .class file.
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

package personal.yhchoi.java.lib.java_class_parser.constants;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import personal.yhchoi.java.lib.java_class_parser.ConstPoolRetriever;
import personal.yhchoi.java.lib.java_class_parser.JavaClassFormatException;

/**
 * A constant in the constant pool of a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public abstract class Constant
{
    /**
     * Const pool tags.
     *
     * @author Yui Hei Choi
     * @version 2024.12.21
     */
    protected enum ConstPoolTag
    {
        /** A tag for class. */
        CLASS,

        /** A tag for field ref. */
        FIELDREF,

        /** A tag for method ref. */
        METHODREF,

        /** A tag for interface-method ref. */
        INTERFACE_METHODREF,


        /** A tag for string. */
        STRING,
        
        /** A tag for intger. */
        INTEGER,
        
        /** A tag for float. */
        FLOAT,
        
        /** A tag for long. */
        LONG,
        
        /** A tag for double. */
        DOUBLE,


        /** A tag for name and type. */
        NAME_AND_TYPE,

        /** A tag for UTF-8. */
        UTF8,


        /** A tag for method handle. */
        METHOD_HANDLE,
        
        /** A tag for method type. */
        METHOD_TYPE,


        /** A tag for dynamic. */
        DYNAMIC,
        
        /** A tag for invoke dynamic. */
        INVOKE_DYNAMIC,

        
        /** A tag for module. */
        MODULE,
        
        /** A tag for package. */
        PACKAGE
    }
    
    private static final HashMap<Integer, ConstPoolTag> constPoolTagsMap;   // the map from integers to constant pool tags
    static
    {
        constPoolTagsMap = new HashMap<>();
        
        constPoolTagsMap.put(7, ConstPoolTag.CLASS);
        constPoolTagsMap.put(9, ConstPoolTag.FIELDREF);
        constPoolTagsMap.put(10, ConstPoolTag.METHODREF);
        constPoolTagsMap.put(11, ConstPoolTag.INTERFACE_METHODREF);
        
        constPoolTagsMap.put(8, ConstPoolTag.STRING);
        constPoolTagsMap.put(3, ConstPoolTag.INTEGER);
        constPoolTagsMap.put(4, ConstPoolTag.FLOAT);
        constPoolTagsMap.put(5, ConstPoolTag.LONG);
        constPoolTagsMap.put(6, ConstPoolTag.DOUBLE);
        
        constPoolTagsMap.put(12, ConstPoolTag.NAME_AND_TYPE);
        constPoolTagsMap.put(1, ConstPoolTag.UTF8);
        
        constPoolTagsMap.put(15, ConstPoolTag.METHOD_HANDLE);
        constPoolTagsMap.put(16, ConstPoolTag.METHOD_TYPE);
        
        constPoolTagsMap.put(17, ConstPoolTag.DYNAMIC);
        constPoolTagsMap.put(18, ConstPoolTag.INVOKE_DYNAMIC);

        constPoolTagsMap.put(19, ConstPoolTag.MODULE);
        constPoolTagsMap.put(20, ConstPoolTag.PACKAGE);
    }
    
    private final ConstPoolRetriever consts;
    
    /**
     * Constructor for objects of class Constant.
     * 
     * @param consts the constant pool retriever
     */
    protected Constant(ConstPoolRetriever consts)
    {
        this.consts = consts;
    }
    
    /**
     * Gets a constant from the constant pool.
     * 
     * @param index the index of the constant
     * @return the constant requested
     */
    protected final Constant getConstFromPool(int index)
    {
        return consts.getConstPool(index);
    }
    
    /**
     * Creates the actual constant from the input stream.
     * To be implemented by the subclass.
     * 
     * @param inStream the input stream to read the .class file
     * @return the newly created constant, or null if operation failed
     */
    // protected static abstract Constant createActualConst(DataInputStream inStream) throws IOException;
    
    /**
     * Creates a constant from the input stream.
     * 
     * @param inStream the input stream to read the .class file
     * @param consts the constant pool retriever
     * @return the newly created constant, or null if operation failed
     * @throws IOException if the input stream fails to read the entire constant
     * @throws JavaClassFormatException if the format of the constant is invalid
     */
    public static final Constant createConst(DataInputStream inStream, ConstPoolRetriever consts) throws IOException, JavaClassFormatException
    {
        final int tagNumber = inStream.readUnsignedByte();
        final ConstPoolTag tag = constPoolTagsMap.get(tagNumber);
        if (tag == null) {
            throw new JavaClassFormatException("Const Tag = " + tagNumber + " is not a valid tag.");
        }
        switch (tag) {
            case CLASS:
                return ConstantClass.createActualConst(inStream, consts);
            case FIELDREF:
                return ConstantFieldref.createActualConst(inStream, consts);
            case METHODREF:
                return ConstantMethodref.createActualConst(inStream, consts);
            case INTERFACE_METHODREF:
                return ConstantInterfaceMethodref.createActualConst(inStream, consts);
            case STRING:
                return ConstantString.createActualConst(inStream, consts);
            case INTEGER:
                return ConstantInteger.createActualConst(inStream, consts);
            case FLOAT:
                return ConstantFloat.createActualConst(inStream, consts);
            case LONG:
                return ConstantLong.createActualConst(inStream, consts);
            case DOUBLE:
                return ConstantDouble.createActualConst(inStream, consts);
            case NAME_AND_TYPE:
                return ConstantNameAndType.createActualConst(inStream, consts);
            case UTF8:
                return ConstantUTF8.createActualConst(inStream, consts);
            case METHOD_HANDLE:
                return ConstantMethodHandle.createActualConst(inStream, consts);
            case METHOD_TYPE:
                return ConstantMethodType.createActualConst(inStream, consts);
            case DYNAMIC:
                return ConstantDynamic.createActualConst(inStream, consts);
            case INVOKE_DYNAMIC:
                return ConstantInvokeDynamic.createActualConst(inStream, consts);
            case MODULE:
                return ConstantModule.createActualConst(inStream, consts);
            case PACKAGE:
                return ConstantPackage.createActualConst(inStream, consts);
            default:
                return null;
        }
    }
}
