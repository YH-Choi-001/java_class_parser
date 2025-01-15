/**
 * 
 *  JavaClassParser.java - A class that retrieves data from a java .class file.
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

package personal.yhchoi.java.lib.java_class_parser;

/**
 * An exception of illegal java class file format.
 * 
 * @author Yui Hei Choi
 * @version 2025.01.15
 */
public class JavaClassFormatException extends Exception
{
    /**
     * Constructor for JavaClassFormatException exceptions.
     */
    public JavaClassFormatException()
    {
        super();
    }

    /**
     * Constructor for JavaClassFormatException exceptions.
     * 
     * @param message the error message
     */
    public JavaClassFormatException(String message)
    {
        super(message);
    }

}