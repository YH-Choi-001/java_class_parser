/**
 * 
 *  Field.java - A class that holds a field in a .class file.
 *  Copyright (C) 2024 - 2025 YH Choi
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
import personal.yhchoi.java.lib.java_class_parser.ConstPoolRetriever;
import personal.yhchoi.java.lib.java_class_parser.attributes.Attribute;

/**
 * A field in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.30
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
     * 
     * @param consts the constant pool retriever
     * @param accessFlags the access flags of this field
     * @param nameIndex the index of name of this field
     * @param descriptorIndex the index of descriptor of this field
     * @param attributes attributes of this field
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
     * @throws IOException if the input stream fails to read the entire field
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
     * Checks if this field is <code>volatile</code>.
     * If this is the case, then this field cannot be cached.
     * 
     * @return <code>true</code> if declared <code>volatile</code>; cannot be cached
     */
    public final boolean isVolatile()
    {
        return checkAccessFlag(ACC_VOLATILE);
    }
    
    /**
     * Checks if this field is <code>transient</code>.
     * If this is the case, then this field is not written or read by a persistent object manager.
     * 
     * @return <code>true</code> if declared <code>transient</code>; not written or read by a persistent object manager
     */
    public final boolean isTransient()
    {
        return checkAccessFlag(ACC_TRANSIENT);
    }
    
    /**
     * Checks if this field is an element of an <code>enum</code>.
     * 
     * @return <code>true</code> if declared as an element of an <code>enum</code>
     */
    public final boolean isEnum()
    {
        return checkAccessFlag(ACC_ENUM);
    }
}
