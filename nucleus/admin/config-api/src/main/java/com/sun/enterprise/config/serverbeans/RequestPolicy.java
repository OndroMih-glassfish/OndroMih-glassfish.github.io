/*
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.config.serverbeans;

import jakarta.validation.constraints.Pattern;

import java.beans.PropertyVetoException;

import org.jvnet.hk2.config.Attribute;
import org.jvnet.hk2.config.ConfigBeanProxy;
import org.jvnet.hk2.config.Configured;

/**
 * Used to define the authentication policy requirements associated with the request processing
 * performed by an
 * authentication provider (i.e. when a client provider's ClientAuthModule.initiateRequest() method
 * is called or when a
 * server provider's ServerAuthModule.validateRequest() method is called)
 */

/* @XmlType(name = "") */

@Configured
public interface RequestPolicy extends ConfigBeanProxy {

    String AUTH_RECIPIENT_TIMINGS = "(before-content|after-content)";
    String AUTH_SOURCES = "(sender|content|username-password)";

    /**
     * Gets the value of the authSource property.
     * Defines a requirement for message layer sender authentication (e.g. username password) or
     * content authentication
     * (e.g. digital signature)
     *
     * @return possible object is {@link String }
     */
    @Attribute
    @Pattern(regexp = AUTH_SOURCES, message = "Valid values: " + AUTH_SOURCES)
    String getAuthSource();

    /**
     * Sets the value of the authSource property.
     *
     * @param value allowed object is {@link String }
     */
    void setAuthSource(String value) throws PropertyVetoException;

    /**
     * Specifies whether recipient authentication occurs before or after content authentication.
     * Allowed values are 'before-content' and 'after-content'.
     * Defines a requirement for message layer authentication of the reciever of a message to its
     * sender (e.g. by XML encryption). before-content indicates that recipient authentication
     * (e.g. encryption) is to occur before any content authentication (e.g. encrypt then sign) with
     * respect to the target of the containing auth-policy. after-content indicates that recipient
     * authentication (e.g. encryption) is to occur after any content authentication (e.g. sign
     * then encrypt) with respect to the target of the containing auth-policy
     *
     * @return possible object is {@link String }
     */
    @Attribute
    @Pattern(regexp = AUTH_RECIPIENT_TIMINGS, message = "Valid values: " + AUTH_RECIPIENT_TIMINGS)
    String getAuthRecipient();

    /**
     * Sets the value of the authRecipient property.
     *
     * @param value allowed object is {@link String }
     */
    void setAuthRecipient(String value) throws PropertyVetoException;

}
