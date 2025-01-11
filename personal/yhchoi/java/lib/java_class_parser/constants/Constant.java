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
import java.util.ArrayList;
import java.util.HashMap;
import personal.yhchoi.java.lib.java_class_parser.ConstPoolRetriever;

/**
 * A constant in the constant pool of a .class file.
 *
 * @author Yui Hei Choi
 * @version 2024.12.21
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
        CLASS, FIELDREF, METHODREF, INTERFACE_METHODREF,
        STRING, INTEGER, FLOAT, LONG, DOUBLE,
        NAME_AND_TYPE, UTF8,
        METHOD_HANDLE, METHOD_TYPE,
        INVOKE_DYNAMIC
    }
    
    private static final HashMap<Integer, ConstPoolTag> constPoolTagsMap;
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
        
        constPoolTagsMap.put(18, ConstPoolTag.INVOKE_DYNAMIC);
    }
    
    private final ConstPoolRetriever consts;
    
    private static final HashMap<HashMap<String, int[]>, ArrayList<int[]>> m = new HashMap<>();
    
    /**
     * Constructor for objects of class Constant.
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
     * @return the newly created constant, or null if operation failed
     */
    public static final Constant createConst(DataInputStream inStream, ConstPoolRetriever consts) throws IOException
    {
        final int tagNumber = inStream.readUnsignedByte();
        final ConstPoolTag tag = constPoolTagsMap.get(tagNumber);
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
            case INVOKE_DYNAMIC:
                return ConstantInvokeDynamic.createActualConst(inStream, consts);
            default:
                return null;
        }
    }
}
