/**
 * 
 *  SignatureAttribute.java - A class that holds a Signature Attribute in a .class file.
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

import java.io.DataInputStream;
import java.io.IOException;
import personal.yhchoi.java.lib.java_class_parser.constants.ConstantUTF8;

/**
 * A Signature Attribute in a .class file.
 *
 * @author Yui Hei Choi
 * @version 2025.01.11
 */
public class SignatureAttribute extends Attribute
{
    private final int signatureIndex;
    
    /**
     * Constructor for objects of class SignatureAttribute.
     */
    private SignatureAttribute(Attribute attr) throws IOException
    {
        super(attr);
        
        final DataInputStream infoInStream = getInfoInStream();
        signatureIndex = infoInStream.readUnsignedShort();
    }
    
    /**
     * Recovers details from the attribute.
     *
     * @param attr the general attribute
     * @return the new attribute built with this class, or null if attribute details don't match
     */
    protected static final SignatureAttribute recoverAttribute(Attribute attr) throws IOException
    {
        if (!"Signature".equals(attr.getName()) || attr.getInfoLength() != 2) {
            return null;
        }
        return new SignatureAttribute(attr);
    }
    
    /**
     * Gets the embedded signature.
     * 
     * @return the embedded signature
     */
    public final String getSignature()
    {
        return ((ConstantUTF8)getConstFromPool(signatureIndex)).getString();
    }
}
