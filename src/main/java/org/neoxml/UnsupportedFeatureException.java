package org.neoxml;

/**
 * <p>
 * <code>UnsupportedFeatureException</code> is thrown when a method is not yet implemented.
 * </p>
 *
 * @version $neoxml: 1.0$
 */
@SuppressWarnings("serial")
public class UnsupportedFeatureException extends UnsupportedOperationException {
    public UnsupportedFeatureException() {
        super("Not supported yet.");
    }
}