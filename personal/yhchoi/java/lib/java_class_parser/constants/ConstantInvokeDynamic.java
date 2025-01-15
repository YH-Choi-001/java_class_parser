/**
 * 
 *  ConstantInvokeDynamic.java - A class that holds a constant invoke dynamic in a .class file.
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
 * Constant invoke dynamic in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public class ConstantInvokeDynamic extends Constant
{
    // fields
    private final int bootstrapMethodAttrIndex;
    private final int nameAndTypeIndex;

    /**
     * Constructor for objects of class ConstantInvokeDynamic.
     * 
     * @param consts the constant pool retriever
     * @param bootstrapMethodAttrIndex the index of bootstrap method attr
     * @param nameAndTypeIndex the index of name and type
     */
    private ConstantInvokeDynamic(ConstPoolRetriever consts, int bootstrapMethodAttrIndex, int nameAndTypeIndex)
    {
        super(consts);
        this.bootstrapMethodAttrIndex = bootstrapMethodAttrIndex;
        this.nameAndTypeIndex = nameAndTypeIndex;
    }
    
    /**
     * Creates the actual constant from the input stream.
     * 
     * @param inStream the input stream to read the .class file
     * @param consts the constant pool retriever
     * @return the newly create constant, or null if operation failed
     * @throws IOException if the input stream fails to read the entire constant
     */
    protected static final Constant createActualConst(DataInputStream inStream, ConstPoolRetriever consts) throws IOException
    {
        final int bootstrapMethodAttrIndex = inStream.readUnsignedShort();
        final int nameAndTypeIndex = inStream.readUnsignedShort();
        
        return new ConstantInvokeDynamic(consts, bootstrapMethodAttrIndex, nameAndTypeIndex);
    }
    
    /**
     * Gets the index of the bootstrap method attr.
     * 
     * @return the index of bootstrap method attr
     */
    public final int getBootstrapMethodAttrIndex()
    {
        return bootstrapMethodAttrIndex;
    }
    
    /**
     * Gets the name and type.
     * 
     * @return the name and type
     */
    public final ConstantNameAndType getNameAndType()
    {
        return (ConstantNameAndType)getConstFromPool(nameAndTypeIndex);
    }
}
