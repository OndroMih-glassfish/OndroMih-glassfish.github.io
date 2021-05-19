/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

package sqetests.ejb.stateful.passivate.ejb.stateful;

import jakarta.ejb.*;
import java.rmi.RemoteException;
import sqetests.ejb.stateful.passivate.util.*;
import java.util.HashMap;

/**
 * Created Oct 17, 2003 4:06:59 PM
 * Code generated by the Sun ONE Studio EJB Builder
 * @author dsingh
 */

public interface SessionRemote extends jakarta.ejb.EJBObject {

    public void badMethod() throws RemoteException;

    public String getMessage() throws RemoteException;

    public String txMethod() throws RemoteException;

    public boolean afterActivationBusinessMethod() throws RemoteException;

    public HashMap getEJBRecorder() throws RemoteException;


}

