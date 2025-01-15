/**
 * 
 *  ConstantUTF8.java - A class that holds a constant UTF-8 string in a .class file.
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
 * Constant UTF-8 string in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public class ConstantUTF8 extends Constant
{
    // fields
    private String string;

    /**
     * Constructor for objects of class ConstantUTF8.
     * 
     * @param consts the constant pool retriever
     * @param bytes the array of bytes
     */
    private ConstantUTF8(ConstPoolRetriever consts, byte[] bytes)
    {
        super(consts);
        this.string = "";
        for (int i = 0; i < bytes.length; i++) {
            final byte x = bytes[i];
            char toAppend;
            if ((x & 0b11110000) == 0b11100000) {
                // 0b1110xxxx : UTF-8 uses 3 bytes for this character
                i++;
                final byte y = bytes[i];
                i++;
                final byte z = bytes[i];
                toAppend = (char)(((x & 0xf) << 12) | ((y & 0x3f) << 6) | (z & 0x3f));
            } else if ((x & 0b11100000) == 0b11000000) {
                // 0b110xxxxx : UTF-8 uses 2 bytes for this character
                i++;
                final byte y = bytes[i];
                toAppend = (char)(((x & 0x1f) << 6) + (y & 0x3f));
            } else {
                toAppend = (char)x;
            }
            string += toAppend;
        }
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
        final int length = inStream.readUnsignedShort();
        final byte[] bytes = new byte[length];
        inStream.read(bytes);
        return new ConstantUTF8(consts, bytes);
    }
    
    /**
     * Gets the string.
     * 
     * @return the string
     */
    public String getString()
    {
        return string;
    }
}
