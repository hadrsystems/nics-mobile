
package gov.nasa.worldwind.util;

/**
 * @author tag
 * @version $Id$
 */
public class WWUtil
{

    /**
     * Determine whether an object reference is null or a reference to an empty string.
     *
     * @param s the reference to examine.
     *
     * @return true if the reference is null or is a zero-length {@link String}.
     */
    public static boolean isEmpty(Object s)
    {
        return s == null || (s instanceof String && ((String) s).length() == 0);
    }
}
