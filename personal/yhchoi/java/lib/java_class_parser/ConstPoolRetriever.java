/**
 * 
 *  ConstantPoolRetriever.java - An interface to allow retrieving constants from the constant pool in a .class file.
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

package personal.yhchoi.java.lib.java_class_parser;

import personal.yhchoi.java.lib.java_class_parser.constants.Constant;

/**
 * An interface to allow retrieving constants from the constant pool.
 *
 * @author Yui Hei Choi
 * @version 2025.01.30
 */
public interface ConstPoolRetriever
{
    /**
     * Gets a constant element from the constant pool.
     * 
     * @param index the index of the constant element to be obtained
     * @return the constant element to be obtained
     * @throws IndexOutOfBoundsException if <code>((index &lt; 1) || (index &gt;= constPool.length + 1))</code>
     */
    public Constant getConstPool(int index) throws IndexOutOfBoundsException;
}