/**
 * 
 *  ConstantMethodType.java - A class that holds a constant method type in a .class file.
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
 * Constant method type in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2024.12.21
 */
public class ConstantMethodType extends Constant
{
    // fields
    private final int descriptorIndex;

    /**
     * Constructor for objects of class ConstantMethodType.
     */
    private ConstantMethodType(ConstPoolRetriever consts, int descriptorIndex)
    {
        super(consts);
        this.descriptorIndex = descriptorIndex;
    }
    
    /**
     * Creates the actual constant from the input stream.
     * 
     * @param inStream the input stream to read the .class file
     * @param consts the constant pool retriever
     * @return the newly create constant, or null if operation failed
     */
    protected static final ConstantMethodType createActualConst(DataInputStream inStream, ConstPoolRetriever consts) throws IOException
    {
        final int descriptorIndex = inStream.readUnsignedShort();
        return new ConstantMethodType(consts, descriptorIndex);
    }
    
    public final String getDescriptor()
    {
        return ((ConstantUTF8)getConstFromPool(descriptorIndex)).getString();
    }
}
