/*|~^~|Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 |~^~|All rights reserved.
 |~^~|
 |~^~|Redistribution and use in source and binary forms, with or without
 |~^~|modification, are permitted provided that the following conditions are met:
 |~^~|
 |~^~|-1. Redistributions of source code must retain the above copyright notice, this
 |~^~|ist of conditions and the following disclaimer.
 |~^~|
 |~^~|-2. Redistributions in binary form must reproduce the above copyright notice,
 |~^~|this list of conditions and the following disclaimer in the documentation
 |~^~|and/or other materials provided with the distribution.
 |~^~|
 |~^~|-3. Neither the name of the copyright holder nor the names of its contributors
 |~^~|may be used to endorse or promote products derived from this software without
 |~^~|specific prior written permission.
 |~^~|
 |~^~|THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 |~^~|AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 |~^~|IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 |~^~|DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 |~^~|FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 |~^~|DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 |~^~|SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 |~^~|CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 |~^~|OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 |~^~|OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*/
/**
 *
 */
package scout.edu.mit.ll.nics.android.api.data;

/**
 * @author Glenn L. Primmer
 *
 * This defines the different types of images supported in the nics mobile application.
 */
public enum ImageFileType {
    /**
     * JPEG image format.
     */
    JPEG (0, "jpeg"),
    /**
     * GIF image format.
     */
    GIF (1, "gif"),

    /**
     * PNG image format.
     */
    PNG (2, "png"),

    /**
     * Unknown format.
     */
    UNKNOWN (3, "unknown");

    /**
     * The numerical representation of the image file type.
     */
    int id;

    /**
     * The textual representation of the image file type.
     */
    String text;

    /**
     * Constructor.
     *
     * @param id The numerical representation of the image file type.
     *
     * @param text The textual representation of the image file type.
     */
    ImageFileType (final int    id,
                   final String text) {
        this.id   = id;
        this.text = text;
    }

    /**
     * Returns the numerical representation of the image file type.
     *
     * @return The numerical representation of the image file type.
     */
    public int getId () {
        return id;
    }

    /**
     * Returns the textual representation of the image file type.
     *
     * @return The textual representation of the image file type.
     */
    public String getText () {
        return text;
    }

    /**
     * Looks up an image file type for the provided numerical representation of an image file type.
     *
     * @param id Numerical representation of an ICS position type.
     *
     * @return The image file type that corresponds to the numerical representation provided.  If there is no
     * image file type that corresponds to the numerical representation provided then 'null' will be returned.
     */
    public static ImageFileType lookUp (final int id) {
        ImageFileType value = null;

        for (ImageFileType imageFileType : ImageFileType.values ()) {
            if (imageFileType.getId () == id) {
                value = imageFileType;

                break;
            }
        }

        return value;
    }

    /**
     * Looks up an image file type for the provided textual representation of an image file type.
     *
     * @param text Textual representation of an ICS position type.
     *
     * @return The image file type that corresponds to the textual representation provided.  If there is no image file
     * type that corresponds to the textual representation provided then 'null' will be returned.
     */
    public static ImageFileType lookUp (final String text) {
        ImageFileType value = null;

        for (ImageFileType imageFileType : ImageFileType.values ()) {
            if (imageFileType.getText ().equals (text)) {
                value = imageFileType;

                break;
            }
        }

        return value;
    }
}

