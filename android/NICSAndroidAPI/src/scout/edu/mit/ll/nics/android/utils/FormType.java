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
package scout.edu.mit.ll.nics.android.utils;

/**
 * @author Glenn L. Primmer
 *
 * This defines the enumeration of the different log levels.
 */
public enum FormType {
	NONE     (0, ""),
    ROC      (1, "Report on Condition"),
    RESC     (2, ""),
    ABC      (3, ""),
    TWO_15   (4, "215"),
    SITREP	 (5, "SITREP"),
    ASSGN    (6, "Assignment Form"),
    SR       (7, "US Coast Guard Simple Report"),
    FR       (8, "US Coast Guard Field Report"),
    TASK	 (9, "Task"),
    RESREQ   (10, "Resource Request"),
    NINE_110 (11, "9110 - Notification Report"),
    DR       (12, "Damage Report"),
    UXO      (13, "Explosive Report"),
    SVRRPT   (14, "Catan Survivor Request"),
    AGRRPT	 (15, "Catan Survivor Aggrogate Request"),
    MITAM	 (16, "MITAM"),
    WR		 (17, "Weather Report");

    /**
     * The log levels numerical value (safer than using ordinal)
     */
    int    id;

    /**
     * The log level textual representation.
     */
    String text;

    /**
     * Enumeration constructor, extended so that textual representation and an integer value
     * can be defined (safer than use of ordinal values).
     *
     * @param id The numerical representation of the enumerated value.
     *
     * @param text The textual representation of the enumerated value.
     */
    FormType (final int    id,
              final String text) {
        this.id   = id;
        this.text = text;
    }

    /**
     * Gets the numerical representation of the enumerated value.
     *
     * @return The numerical representation of the enumerated value.
     */
    public int getId () {
        return id;
    }

    /**
     * Gets the textual representation of the enumerated value.
     *
     * @return The textual representation of the enumerated value.
     */
    public String getText () {
        return text;
    }

    /**
     * Looks up an Form Type for the provided numerical representation of a Form Type.
     *
     * @param id Numerical representation of a Form Type.
     *
     * @return The Form Type that corresponds to the numerical representation provided.  If there is no Form Type
     * that corresponds to the numerical representation provided then 'null' will be returned.
     */
    public static FormType lookUp (final int id) {
        FormType value = null;

        for (FormType formType : FormType.values ()) {
            if (formType.getId () == id) {
                value = formType;

                break;
            }
        }

        return value;
    }

    /**
     * Looks up an Form Type for the provided textual representation of a Form Type.
     *
     * @param text Textual representation of an Form Type.
     *
     * @return The Log Level that corresponds to the textual representation provided.  If there is no Form Type
     * that corresponds to the textual representation provided then 'null' will be returned.
     */
    public static FormType lookUp (final String text) {
        FormType value = null;

        for (FormType formType : FormType.values ()) {
            if (formType.getText ().equals (text)) {
                value = formType;

                break;
            }
        }

        return value;
    }
}
