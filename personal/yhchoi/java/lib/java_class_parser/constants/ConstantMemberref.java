/**
 * 
 *  ConstantMemberref.java - A class that holds a constant memberref in a .class file.
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
import personal.yhchoi.java.lib.java_class_parser.ConstPoolRetriever;

/**
 * Constant memberref in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public abstract class ConstantMemberref extends Constant
{
    // fields
    private final int classIndex;
    private final int nameAndTypeIndex;
    
    /**
     * The types of a member inside a class.
     */
    protected static enum MemberType
    {
        /** A field. */
        FIELD,

        /** A method. */
        METHOD,

        /** A method inside an interface. */
        INTERFACE_METHOD
    }

    /**
     * Constructor for objects of class ConstantFieldref.
     * 
     * @param consts constant pool retriever
     * @param classIndex the index of class
     * @param nameAndTypeIndex the index of name and type
     */
    protected ConstantMemberref(ConstPoolRetriever consts, int classIndex, int nameAndTypeIndex)
    {
        super(consts);
        this.classIndex = classIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }
    
    /**
     * Creates the actual constant from the input stream.
     * 
     * @param inStream the input stream to read the .class file
     * @param consts the constant pool retriever
     * @param type the type of the member to be created
     * @return the newly create constant, or null if operation failed
     * @throws IOException if the input stream fails to read the entire constant
     */
    protected static final Constant createActualConst(DataInputStream inStream, ConstPoolRetriever consts, MemberType type) throws IOException
    {
        final int classIndex = inStream.readUnsignedShort();
        final int nameAndTypeIndex = inStream.readUnsignedShort();
        
        if (type == null) {
            return null;
        }
        
        switch (type) {
            case FIELD:
                return new ConstantFieldref(consts, classIndex, nameAndTypeIndex);
            case METHOD:
                return new ConstantMethodref(consts, classIndex, nameAndTypeIndex);
            case INTERFACE_METHOD:
                return new ConstantInterfaceMethodref(consts, classIndex, nameAndTypeIndex);
            default:
                return null;
        }
    }
    
    /**
     * Gets the wrapping class.
     * 
     * @return the wrapping class
     */
    public final ConstantClass getThisClass()
    {
        return (ConstantClass)getConstFromPool(classIndex);
    }

    /**
     * Gets the name of the wrapping class.
     * 
     * @return the class name in format of <code>java.lang.Object</code>
     */
    public final String getClassName()
    {
        return getThisClass().getName();
    }
    
    /**
     * Gets the name and type of this member.
     * 
     * @return the name and type of this member
     */
    public final ConstantNameAndType getNameAndType()
    {
        return (ConstantNameAndType)getConstFromPool(nameAndTypeIndex);
    }
}
