/**
 * 
 *  ConstantValueAttribute.java - A class that holds a ConstantValue Attribute in a .class file.
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

import personal.yhchoi.java.lib.java_class_parser.constants.Constant;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * A ConstantValue Attribute in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.11
 */
public class ConstantValueAttribute extends Attribute
{
    private final int constantValueIndex;
    
    /**
     * Constructor for objects of class ConstantValueAttribute.
     */
    private ConstantValueAttribute(Attribute attr) throws IOException
    {
        super(attr);
        
        final DataInputStream infoInStream = getInfoInStream();
        constantValueIndex = infoInStream.readUnsignedShort();
    }
    
    /**
     * Recovers details from the attribute.
     *
     * @param attr the general attribute
     * @return the new attribute built with this class, or null if attribute details don't match
     */
    protected static final ConstantValueAttribute recoverAttribute(Attribute attr) throws IOException
    {
        if (!"ConstantValue".equals(attr.getName()) || attr.getInfoLength() != 2) {
            return null;
        }
        return new ConstantValueAttribute(attr);
    }
    
    /**
     * Gets the embedded constant value.
     * 
     * @return the embedded constant value
     */
    public final Constant getConstantValue()
    {
        return getConstFromPool(constantValueIndex);
    }
}
