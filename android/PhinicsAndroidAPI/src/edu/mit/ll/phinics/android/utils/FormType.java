/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.phinics.android.utils;

/**
 * @author Glenn L. Primmer
 *
 * This defines the enumeration of the different log levels.
 */
public enum FormType {
    ROC      (0, "Report on Condition"),
    RESC     (1, ""),
    ABC      (2, ""),
    TWO_15   (3, "215"),
    SITREP	 (4, "SITREP"),
    ASSGN    (5, "Assignment Form"),
    SR       (6, "US Coast Guard Simple Report"),
    FR       (7, "US Coast Guard Field Report"),
    TASK	 (8, "Task"),
    RESREQ   (9, "Resource Request"),
    NINE_110 (10, "9110 - Notification Report"),
    DR       (11, "Damage Report"),
    UXO      (12, "Explosive Report"),
    SVRRPT   (13, "Catan Survivor Request"),
    AGRRPT	 (14, "Catan Survivor Aggrogate Request"),
    WR		 (15, "Weather Report");

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
