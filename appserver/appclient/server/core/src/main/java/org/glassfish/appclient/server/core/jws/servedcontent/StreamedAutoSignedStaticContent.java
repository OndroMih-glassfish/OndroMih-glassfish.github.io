/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.appclient.server.core.jws.servedcontent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;
import org.glassfish.appclient.server.core.AppClientDeployerHelper;
import org.glassfish.appclient.server.core.jws.AppClientHTTPAdapter;
import static org.glassfish.appclient.server.core.jws.RestrictedContentAdapter.DATE_HEADER_NAME;
import static org.glassfish.appclient.server.core.jws.RestrictedContentAdapter.LAST_MODIFIED_HEADER_NAME;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.Session;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.grizzly.http.util.MimeType;

/**
 * Auto-signed content that is delivered not as an on-disk file but as an
 * output stream.
 * <p>
 * This implementation supports the requirement to "sign" a JNLP document by
 * including it in a signed JAR so the Java Web Start client can verify that
 * the JNLP delivered is the correct document (as compared to the copy in the
 * signed JAR delivered to the client).
 * <p>
 * Most signed JARs served by the Java Web Start support in GlassFish are
 * created on-disk by signing their unsigned counterparts once.  Then the on-disk
 * signed JAR is served when the client asks for the JAR.  The main JAR has to
 * be handled differently to meet the Java Web start security requirements.
 * The signed copy of the main JAR is created on-the-fly by signing the unsigned
 * JAR's contents plus the previously-generated JNLP.  The signed output is
 * sent directly to the HTTP response's output stream rather than creating a
 * temporary file.
 * <p>
 * We cannot create the signed main JAR once and for all because the JNLP
 * document which launches the app might change from one invocation to the next,
 * particularly regarding command-line arguments which appear in the JNLP as
 * <argument> elements.
 *
 * @author tjquinn
 */
public class StreamedAutoSignedStaticContent extends AutoSignedContent {

    private static final String SIGNED_JNLP_PATH = "JNLP-INF/APPLICATION.JNLP";

    private static final Logger logger = Logger.getLogger(AppClientDeployerHelper.ACC_MAIN_LOGGER, AppClientDeployerHelper.LOG_MESSAGE_RESOURCE);

    public StreamedAutoSignedStaticContent(final File unsignedFile,
            final String userProvidedAlias,
            final ASJarSigner jarSigner,
            final String relativeURI,
            final String appName) throws FileNotFoundException {
        super(unsignedFile, null /* signedFile */, userProvidedAlias, jarSigner, relativeURI, appName);
    }

    @Override
    public void process(String relativeURIString, Request gReq, Response gResp) throws IOException {
        logger.log(Level.FINE, "Processing main JAR for {0}", gReq.getRequestURI());
        final long now = System.currentTimeMillis();
        gResp.setDateHeader(LAST_MODIFIED_HEADER_NAME, now);
        gResp.setDateHeader(DATE_HEADER_NAME, now);
        gResp.setStatus(HttpStatus.OK_200);
        gResp.setContentType(contentType(unsignedFile()));

        /*
         * This is the main JAR, so combine it with the previously-generated
         * and saved main JNLP document.
         */
        final Session session = gReq.getSession();
        final Object jwsObj = session.getAttribute(AppClientHTTPAdapter.GF_JWS_SESSION_CACHED_JNLP_NAME);
        if (jwsObj == null) {
            logger.log(Level.FINE, "Session {0} did not contain cached JNLP", session.getIdInternal());
            throw new NullPointerException();
        }
        if ( ! (jwsObj instanceof byte[])) {
            logger.log(Level.FINE, "Session {0} cached JNLP is not a byte[] as expected", session.getIdInternal());
            throw new IllegalArgumentException(jwsObj.getClass().toString());
        }

        /*
         * We do not know the finished length so we cannot set Content-Length.
         * Instead we'll just write to the output stream and Grizzly will
         * chunk it for us if needed.
         */
        final ZipOutputStream zos = new ZipOutputStream(gResp.getOutputStream());

        logger.log(Level.FINE, "Request's session contains cached JNLP");
        final byte[] jnlpContent = (byte[]) jwsObj;
        final Map<String,byte[]> addedContent = new HashMap<String,byte[]>();
        addedContent.put(SIGNED_JNLP_PATH, jnlpContent);
        try {
            jarSigner().signJar(unsignedFile(), zos, userProvidedAlias(), createJWSAttrs(AppClientHTTPAdapter.requestURI(gReq), appName()), addedContent);
            /*
             * Create an on-disk copy of the signed JAR for debugging purposes
             * if logging is detailed enough.
             */
            if (logger.isLoggable(Level.FINEST)) {
                final File debugSignedJARFile = new File(unsignedFile().getAbsolutePath()+".debug");
                final ZipOutputStream dbgZos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(debugSignedJARFile)));
                try {
                    jarSigner().signJar(unsignedFile(), dbgZos, userProvidedAlias(), createJWSAttrs(AppClientHTTPAdapter.requestURI(gReq), appName()), addedContent);
                } finally {
                    dbgZos.close();
                }
                logger.log(Level.FINEST, "Created on-disk signed JAR {0}", debugSignedJARFile.getAbsolutePath());
            }
            zos.close();
        } catch (IOException ioex) {
            throw ioex;
        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            zos.close();
        }
    }

    @Override
    public boolean isAvailable(URI requestURI) throws IOException {
        return true;
    }


    private String contentType(final File file) {
        final String path = file.getPath();
        String substr;
        int dot = path.lastIndexOf('.');
        if (dot < 0) {
            substr = file.toString();
            dot = substr.lastIndexOf('.');
        } else {
            substr = path;
        }

        if (dot > 0) {
            String ext = substr.substring(dot + 1);
            String ct = MimeType.get(ext);
            return ct;
        } else {
            return MimeType.get("html");
        }
    }
}
