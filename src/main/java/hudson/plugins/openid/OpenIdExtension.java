package hudson.plugins.openid;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Hudson;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.MessageExtension;

/**
 * An OpenID extension for extending an authentication request and processing an authentication success.
 *
 * TODO currently there is no mechanism to add general properties to the User or the OpenIdUserProperty
 *
 * @author Paul Sandoz
 */
public abstract class OpenIdExtension implements ExtensionPoint {
    /**
     * Extend the authentication request.
     * <p>
     * The implementation may add extensions to <code>authRequest</code> using
     * {@link AuthRequest#addExtension(org.openid4java.message.MessageExtension)}.
     *
     * @param authRequest the authentication request
     * @throws MessageException if there is a message error extending the request
     */
    public abstract void extend(AuthRequest authRequest) throws MessageException;

    /**
     * Process the authentication success.
     * <p>
     * The implementation may extract {@link MessageExtension} implementations from <code>authSuccess</code>
     * and add information to <code>id</code>.
     *
     * @param authSuccess the authentication success.
     * @param id the identity.
     * @throws MessageException if there is a message error processing the success.
     */
    public abstract void process(AuthSuccess authSuccess, Identity id) throws MessageException;

    /**
     * Obtain an extended response message from an {@link AuthSuccess} instance given the class
     * and URI type of the response message.
     *
     * @param c the class of the response message.
     * @param authSuccess the authorization success.
     * @param typeUri the URI type of the response message.
     * @param <T> the type of the response message.
     * @return the response message, otherwise null if there is not such response message available.
     * @throws MessageException if an error obtaining the response message.
     */
    protected <T> T getMessageAs(Class<T> c, AuthSuccess authSuccess, String typeUri) throws MessageException {
        MessageExtension me = authSuccess.getExtension(typeUri);
        return c.cast(me);
    }

    /**
     * All registered extension points.
     */
    public static ExtensionList<OpenIdExtension> all() {
        return Hudson.getInstance().getExtensionList(OpenIdExtension.class);
    }

    /**
     * Extend the authentication request.
     * <p>
     * All extension points will be iterated through and each one will extend the request.
     *
     * @param authRequest the authentication request.
     * @throws MessageException if there is a message error extending the request
     */
    public static void extendRequest(AuthRequest authRequest) throws MessageException {
        for (OpenIdExtension e : all()) {
            e.extend(authRequest);
        }
    }

    /**
     * Process the authentication success.
     * <p>
     * All extension points will be iterated through and each one will process the success.
     *
     * @param authSuccess the authentication success.
     * @param id the identity.
     * @throws MessageException if there is a message error processing the success.
     */
    public static void processResponse(AuthSuccess authSuccess, Identity id) throws MessageException {
        for (OpenIdExtension e : all()) {
            e.process(authSuccess, id);
        }
    }
}