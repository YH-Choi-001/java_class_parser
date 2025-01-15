/**
 * 
 *  ConstantNameAndType.java - A class that holds a constant name and type in a .class file.
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
 * Constant name and type in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public class ConstantNameAndType extends Constant
{
    // fields
    private final int nameIndex;
    private final int descriptorIndex;

    /**
     * Constructor for objects of class ConstantClass.
     * 
     * @param consts the constant pool retriever
     * @param nameIndex the index of name
     * @param descriptorIndex the index of descriptor
     */
    private ConstantNameAndType(ConstPoolRetriever consts, int nameIndex, int descriptorIndex)
    {
        super(consts);
        this.nameIndex = nameIndex;
        this.descriptorIndex = descriptorIndex;
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
        final int nameIndex = inStream.readUnsignedShort();
        final int descriptorIndex = inStream.readUnsignedShort();
        
        return new ConstantNameAndType(consts, nameIndex, descriptorIndex);
    }
    
    /**
     * Gets the name of the name and type.
     * 
     * @return the name
     */
    public final String getName()
    {
        return ((ConstantUTF8)getConstFromPool(nameIndex)).getString();
    }
    
    /**
     * Gets the descriptor of the name and type.
     * 
     * @return the descriptor
     */
    public final String getDescriptor()
    {
        return ((ConstantUTF8)getConstFromPool(descriptorIndex)).getString();
    }
}
