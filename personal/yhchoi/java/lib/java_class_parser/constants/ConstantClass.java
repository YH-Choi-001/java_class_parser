/**
 * 
 *  ConstantClass.java - A class that holds a constant class in a .class file.
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
 * Constant class in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public class ConstantClass extends Constant
{
    // fields
    private final int nameIndex;

    /**
     * Constructor for objects of class ConstantClass.
     */
    private ConstantClass(ConstPoolRetriever consts, int nameIndex)
    {
        super(consts);
        this.nameIndex = nameIndex;
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
        return new ConstantClass(consts, nameIndex);
    }
    
    /**
     * Gets the name of the class.
     * 
     * @return the class name in format of <code>java.lang.Object</code>
     */
    public final String getName()
    {
        String className = ((ConstantUTF8)getConstFromPool(nameIndex)).getString();
        // className = className.replace('/', '.');
        // className = className.replace('$', '.');
        return className;
    }
}
