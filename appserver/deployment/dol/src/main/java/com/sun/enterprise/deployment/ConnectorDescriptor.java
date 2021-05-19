/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

 package com.sun.enterprise.deployment;

import com.sun.enterprise.deployment.node.connector.ConnectorNode;
import com.sun.enterprise.deployment.runtime.connector.SunConnector;
import com.sun.enterprise.deployment.util.ComponentVisitor;
import com.sun.enterprise.deployment.util.ConnectorTracerVisitor;
import com.sun.enterprise.deployment.util.ConnectorValidator;
import com.sun.enterprise.deployment.util.ConnectorVisitor;
import com.sun.enterprise.deployment.util.DOLUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.glassfish.apf.AnnotationInfo;
import org.glassfish.api.deployment.archive.ArchiveType;
import org.glassfish.deployment.common.DescriptorVisitor;

/**
 * Deployment Information for connector
 *
 * <!ELEMENT connector (display-name?, description?, icon?, vendor-name,
 * spec-version, eis-type, version, license?, resourceadapter)>
 *
 * @author Tony Ng
 * @author Qingqing Ouyang
 */
public class ConnectorDescriptor extends CommonResourceBundleDescriptor {

    private String  displayName = "";
    private String  connectorDescription = "";
    private String  largeIcon = "";
    private String  smallIcon = "";
    private String  vendorName = "";
    private String  eisType = "";
    private String  version = "";
    private String resourceAdapterVersion = "";
    private LicenseDescriptor licenseDescriptor = null;

    //connector1.0 old stuff, need clean up
    private Set configProperties;
    private Set authMechanisms;
    private Set securityPermissions;
    private String  managedConnectionFactoryImpl = "";
    private int     transactionSupport = PoolManagerConstants.LOCAL_TRANSACTION;
    private boolean reauthenticationSupport = false;
    private String connectionInterface;
    private String connectionClass;
    private String connectionFactoryInterface;
    private String connectionFactoryClass;

    //connector1.5 begin
    private String  resourceAdapterClass = "";
    private ConnectorConfigProperty configProperty = null;
    private OutboundResourceAdapter outboundRA = null;
    private InboundResourceAdapter  inboundRA = null;
    private Set adminObjects;

    //FIXME "inboundResourceAdapterClass" no longer valid
    //Use "resourceAdapterClass" instead
    private String inboundResourceAdapterClass = "";

    //FIXME remove messagelisteners
    private Set messageListeners;
    //connector1.5 end

    // following are all the get and set methods for the
    // various variables listed above
    private Set requiredWorkContexts;

    /*Set variables indicates that a particular attribute is set by DD processing so that
      annotation processing need not (must not) set the values from annotation */
    private boolean specVersionSet = false;

/*
    private boolean moduleNameSet = false;
*/

    // book keeping annotations that cannot be processed up-front. These will be processed
    // during validation phase of the descriptor
    private transient Set<AnnotationInfo> connectorAnnotations;

    private boolean validConnectorAnnotationProcessed ;

    private transient Map<String, Set<AnnotationInfo>> configPropertyAnnotations ;

    //default resource names for this resource-adpater
    private Set<String> defaultResourceNames;

    private transient Set<String> configPropertyProcessedClasses;

    public ConnectorDescriptor() {
        this.configProperties = new OrderedSet();
        this.authMechanisms = new OrderedSet();
        this.securityPermissions = new OrderedSet();
        this.adminObjects = new OrderedSet();
        this.requiredWorkContexts = new OrderedSet();

            //FIXME.  need to remove the following
        this.messageListeners = new OrderedSet();

        this.connectorAnnotations = new OrderedSet<>();
        this.configPropertyAnnotations = new HashMap<>();
        this.configPropertyProcessedClasses = new HashSet<>();
        this.defaultResourceNames = new OrderedSet<>();
    }


    public Set getRequiredWorkContexts() {
        return this.requiredWorkContexts;
    }

    public void addRequiredWorkContext (String workContextClass){
        this.requiredWorkContexts.add(workContextClass);
    }

    public void removeRequiredWorkContext(String workContextClass) {
        this.requiredWorkContexts.remove(workContextClass);
    }

    /**
     * @return the default version of the deployment descriptor
     * loaded by this descriptor
     */
    @Override
    public String getDefaultSpecVersion() {
        return ConnectorNode.SPEC_VERSION;
    }

    ////////////////////////////////////////////////////////////////////
    // The following are access methods for connector1.0's
    // resourceadapter element.
    // We no longer support them.
    ////////////////////////////////////////////////////////////////////

    //QQ. FIXME.  After verifier stops using this interface,
    //this REALLY needs to be removed. (for 1.0 cases only)

    // The methods for connection and connection factories have now shifted
    //to OutboundResourceAdapter to maintain backward compatibility
    //Sheetal. These methods should be removed from here once we start
    //using new DOL

    /**
     * @deprecated
     */
    @Deprecated
    public String getConnectionFactoryInterface() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setConnectionFactoryInterface(String connectionFactoryInterface) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getConnectionFactoryImpl() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setConnectionFactoryImpl(String connectionFactoryImpl) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getConnectionInterface() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setConnectionInterface(String connectionInterface) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getConnectionImpl() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setConnectionImpl(String connectionImpl) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getManagedConnectionFactoryImpl() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setManagedConnectionFactoryImpl(String managedConnectionFactoryImpl) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean supportsReauthentication() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getReauthenticationSupport() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setReauthenticationSupport(boolean reauthenticationSupport) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setReauthenticationSupport(String reauthSupport) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getTransSupport() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public int getTransactionSupport() {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setTransactionSupport(int transactionSupport) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setTransactionSupport(String support) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Set getAuthMechanisms() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return a set of service-ref from this bundle or null
     *         if none
     */
    public Set getServiceReferenceDescriptors() {
        return new OrderedSet();
    }

    /**
     * Set of SecurityPermission objects
     */
    public Set getSecurityPermissions() {
        if (securityPermissions == null) {
            securityPermissions = new OrderedSet();
        }
        return securityPermissions;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean addAuthMechanism(AuthMechanism mech) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean removeAuthMechanism(AuthMechanism mech) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean addAuthMechanism(int mech) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean removeAuthMechanism(int mech) {
        throw new UnsupportedOperationException();
    }

    /**
     * Add a SecurityPermission object to the set
     */
    public void addSecurityPermission(SecurityPermission permission) {
        securityPermissions.add(permission);
    }

    /**
     * Remove a SecurityPermission object to the set
     */
    public void removeSecurityPermission(SecurityPermission permission) {
        securityPermissions.remove(permission);
    }

    ///////////////////////////////////////////////////////////////////////
    // The following are specific to connector1.5 elements
    // <!ELEMENT resourceadapter (resourceadapter-class, config-property?,
    // outbound-resourceadapter?, inbound-resourceadapter?, adminobject*)>
    ///////////////////////////////////////////////////////////////////////

    //connector1.5 begin

    public String getResourceAdapterClass() {
        return resourceAdapterClass;
    }

    public void setResourceAdapterClass (String raClass) {
        resourceAdapterClass = raClass;
    }

    /**
     * Set of ConnectorConfigProperty
     */
    public Set getConfigProperties() {
        return configProperties;
    }

    /**
     * add a configProperty to the set
     */
    public void addConfigProperty(ConnectorConfigProperty configProperty) {
        this.configProperties.add(configProperty);
    }

    /**
     * remove a configProperty from the set
     */
    public void removeConfigProperty(ConnectorConfigProperty configProperty) {
        configProperties.remove(configProperty);
    }

    public LicenseDescriptor getLicenseDescriptor() {
        return licenseDescriptor;
    }

    public void setLicenseDescriptor(LicenseDescriptor licenseDescriptor) {
        this.licenseDescriptor = licenseDescriptor;
    }

    public OutboundResourceAdapter getOutboundResourceAdapter() {
        return this.outboundRA;
    }

    public void setOutboundResourceAdapter(OutboundResourceAdapter outboundRA) {
        this.outboundRA = outboundRA;
    }

    public InboundResourceAdapter getInboundResourceAdapter() {
        return this.inboundRA;
    }

    public void setInboundResourceAdapter(InboundResourceAdapter inboundRA) {
        this.inboundRA = inboundRA;
    }

    /**
     * @return admin objects
     */
    public Set getAdminObjects() {
        return adminObjects;
    }

    /**
     * set admin object
     */
    public void addAdminObject(AdminObject admin) {
        boolean duplicate = false; // hasAdminObject(admin.getAdminObjectInterface(),
                                   // admin.getAdminObjectClass(), adminObjects);
        if (!duplicate) {
            adminObjects.add(admin);
        } else {
            throw new IllegalStateException("Cannot add duplicate admin object with interface " + "[ "
                + admin.getAdminObjectInterface() + " ], class [ " + admin.getAdminObjectClass() + " ]");
        }
    }

  /*
    private boolean hasAdminObject(String intfClass, String implClass, Set adminObjects) {
        boolean found = false;
        Iterator adminObjectsIterator = adminObjects.iterator();
        while(adminObjectsIterator.hasNext()){
            AdminObject ao = (AdminObject)adminObjectsIterator.next();
            if(ao.getAdminObjectClass().equals(implClass) &&
                    ao.getAdminObjectInterface().equals(intfClass)){
                found = true;
                break;
            }
        }
        return found;
    }
  */

    public void removeAdminObject(AdminObject admin) {
        adminObjects.remove(admin);
    }

    public boolean hasAdminObjects() {
        return adminObjects.size() > 0;
    }

    public boolean getOutBoundDefined() {
        return outboundRA != null;
    }

    //for the purpose of writing this optional node back to XML format.
    //if this node is present then write it else donot.

    public boolean getInBoundDefined() {
        return inboundRA != null;
    }


    //connector1.5 end


    /////////////////////////////////////////////////////////////
    // The following are the accessor methods for elements that
    // are common to both connector1.0 and connector1.5
    /////////////////////////////////////////////////////////////

    /**
     * get the connector description
     */
    public String getConnectorDescription() {
        return connectorDescription;
    }

    /**
     * set the connector description
     */
    public void setConnectorDescription(String description) {
        connectorDescription = description;
    }

    /**
     * get value for vendorName
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * set value for vendorName
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * get eisType
     */
    public String getEisType() {
        return eisType;
    }

    /**
     * set eisType
     */
    public void setEisType(String eisType) {
        this.eisType = eisType;
    }

    /**
     * get value for version
     */
    public String getVersion() {
        return version;
        // throw new UnsupportedOperationException();
    }

    /**
     * set value for version
     */
    public void setVersion(String version) {
        //TODO V3 validate version ?
        this.version = version;
        //TODO V3 need to have "set" variable ?
//        versionSet = true;
        //throw new UnsupportedOperationException();
    }

    /** get value for resourceadapter version (1.5 schema
    */
    public String getResourceAdapterVersion() {
        return resourceAdapterVersion;
    }

    /** set value for resourceadater version (1.5 schema)
    */
    public void setResourceAdapterVersion(String resourceAdapterVersion) {
        this.resourceAdapterVersion = resourceAdapterVersion;
    }

    ////////////////////////////////////////////////////////
    // Other MISC methods
    ////////////////////////////////////////////////////////

    /** return name used for deployment
    */
    public String getDeployName() {
        return getModuleDescriptor().getArchiveUri();
    }


    /**
     * visit the descriptor and all sub descriptors with a DOL visitor implementation
     *
     * @param aVisitor visitor to traverse the descriptors
     */
    @Override
    public void visit(DescriptorVisitor aVisitor) {
        if (aVisitor instanceof ConnectorVisitor) {
            visit((ComponentVisitor) aVisitor);
        } else {
            super.visit(aVisitor);
        }
    }

    /**
     * @param type The full qualified name for connection factory interface
     */
    public ConnectionDefDescriptor getConnectionDefinitionByCFType(String type) {
        return getConnectionDefinitionByCFType(type, true);
    }

    /**
     * @param type The full qualified name for connection factory interface
     * @param useDefault This param is to support the backward compatibility
     *                   of connector 1.0 resource adapter where there is
     *                   only one connection factory type.  If type is null
     *                   and useDefault is true, the only CF will be returned.
     */
    public ConnectionDefDescriptor getConnectionDefinitionByCFType(String type, boolean useDefault) {
        if (this.outboundRA == null) {
            return null;
        }
        Iterator it = this.outboundRA.getConnectionDefs().iterator();
        while (it.hasNext()) {
            ConnectionDefDescriptor desc = (ConnectionDefDescriptor) it.next();

            if (type == null) {
                if (useDefault && this.outboundRA.getConnectionDefs().size() == 1) {
                    return desc;
                } else {
                    return null;
                }
            }

            if (desc.getConnectionFactoryIntf().equals(type)) {
                return desc;
            }
        }
        return null;
    }

    public int getNumOfSupportedCFs() {
        if (outboundRA == null) {
            return 0;
        }
        return outboundRA.getConnectionDefs().size();
    }

    public AdminObject getAdminObject(String adminObjectInterface, String adminObjectClass) {
        Iterator i = getAdminObjects().iterator();
        while (i.hasNext()) {
            AdminObject ao = (AdminObject) i.next();
            if (adminObjectInterface.equals(ao.getAdminObjectInterface())
                && adminObjectClass.equals(ao.getAdminObjectClass())) {
                return ao;
            }
        }

        return null;
    }

    public List<AdminObject> getAdminObjectsByType(String type) {
        List<AdminObject> adminObjects = new ArrayList<>();
        Iterator i = getAdminObjects().iterator();
        while (i.hasNext()) {
            AdminObject ao = (AdminObject) i.next();
            if (type.equals(ao.getAdminObjectInterface())) {
                adminObjects.add(ao);
            }
        }
        return adminObjects;
    }

    public List<AdminObject> getAdminObjectsByClass(String adminObjectClass) {
        List<AdminObject> adminObjects = new ArrayList<>();
        Iterator i = getAdminObjects().iterator();
        while (i.hasNext()) {
            AdminObject ao = (AdminObject) i.next();
            if (adminObjectClass.equals(ao.getAdminObjectClass())) {
                adminObjects.add(ao);
            }
        }
        return adminObjects;
    }


    /**
     * A formatted string representing my state.
     */
    @Override
    public void print(StringBuffer toStringBuffer) {
        StringBuffer buf = toStringBuffer;
        super.print(buf);

        buf.append("\n displayName : " + super.getName());
        buf.append("\n connector_description : " + connectorDescription);
        buf.append("\n smallIcon : " + super.getSmallIconUri());
        buf.append("\n largeIcon : " + super.getLargeIconUri());
        buf.append("\n vendorName : " + vendorName);
        buf.append("\n eisType : " + eisType);
        // buf.append("\n version : " + version);
        buf.append("\n resourceadapter version : " + resourceAdapterVersion);

        // license info
        if (getLicenseDescriptor() != null) {
            buf.append("\n license_description : " + getLicenseDescriptor().getDescription());
            buf.append("\n licenseRequired : " + getLicenseDescriptor().getLicenseRequiredValue());
        }

        buf.append("\n resourceAdapterClass : " + resourceAdapterClass);

        buf.append("\n resourceAdapterClass [" + resourceAdapterClass + "] config properties :");
        appendConfigProperties(this.configProperties, buf);

        if (this.outboundRA == null) {
            buf.append("\n Outbound Resource Adapter NOT available");
        } else {
            buf.append("\n Outbound Resource Adapter Info : ");

            buf.append("\n connection-definitions: ");
            for (Object element : this.outboundRA.getConnectionDefs()) {
                buf.append("\n------------\n");

                ConnectionDefDescriptor conDef = (ConnectionDefDescriptor) element;
                buf.append("MCF : " + conDef.getManagedConnectionFactoryImpl() + ", ");
                buf.append("\n MCF [" + conDef.getManagedConnectionFactoryImpl() + "] config properties :");
                appendConfigProperties(conDef.getConfigProperties(), buf);

                buf.append("[CF Interface : " + conDef.getConnectionFactoryIntf() + "], ");
                buf.append("[CF Class : " + conDef.getConnectionFactoryImpl() + "], ");
                buf.append("[Connection Interface : " + conDef.getConnectionIntf() + "], ");
                buf.append("[Connection Class : " + conDef.getConnectionImpl() + "] ");

                buf.append("\n------------\n");
            }

            buf.append("\n transaction-support : " + this.outboundRA.getTransSupport());

            buf.append("\n authentication-mechanism: ");
            for (Object element : this.outboundRA.getAuthMechanisms()) {
                AuthMechanism conf = (AuthMechanism) element;
                buf.append("\n------------\n");
                buf.append("[Type : " + conf.getAuthMechType() + "], ");
                buf.append("[Interface : " + conf.getCredentialInterface() + "]");
                buf.append("\n------------");
            }

            buf.append("\n reauthenticate-support : " + this.outboundRA.getReauthenticationSupport());

            buf.append("\n security-permission : ");
            for (Object element : getSecurityPermissions()) {
                SecurityPermission conf = (SecurityPermission) element;
                buf.append("\n------------\n");
                buf.append("[persmission : " + conf.getPermission() + "], ");
                buf.append("[discription : " + conf.getDescription() + "]");
                buf.append("\n------------");
            }

        } // outbound resourceadapter

        if (this.inboundRA == null) {
            buf.append("\n Inbound Resource Adapter NOT available");
        } else {
            buf.append("\n Inbound Resource Adapter Info : ");

            buf.append("\n Message Listeners Info : ");
            for (Object element : this.inboundRA.getMessageListeners()) {
                buf.append("\n------------\n");
                MessageListener l = (MessageListener) element;
                buf.append("[Type : " + l.getMessageListenerType() + "], ");
                buf.append("[AS Class : " + l.getActivationSpecClass() + "]");
                buf.append("\n------------ ");
            }

        } // inbound resourceadapter

        if (this.adminObjects.size() == 0) {
            buf.append("\n Admin Objects NOT available");
        } else {
            buf.append("\n Admin Objects Info : ");
            for (Object element : this.adminObjects) {
                buf.append("\n------------\n");
                AdminObject a = (AdminObject) element;
                buf.append("[Type : " + a.getAdminObjectInterface() + "], ");
                buf.append("[Class : " + a.getAdminObjectClass() + "]");
                appendConfigProperties(a.getConfigProperties(), buf);
                buf.append("\n------------ ");
            }

        } // admin objects
    }


    private StringBuffer appendConfigProperties(Set props, StringBuffer buf) {
        buf.append("\n------------");
        for (Object prop : props) {
            ConnectorConfigProperty config = (ConnectorConfigProperty) prop;
            buf.append("[Name : " + config.getName() + "], ");
            buf.append("[Value: " + config.getValue() + "], ");
            buf.append("[Type : " + config.getType() + "]");
            buf.append("[Confidential : " + config.isConfidential() + "]");
            buf.append("[Ignore : " + config.isIgnore() + "]");
            buf.append("[SupportsDynamicUpdates : " + config.isSupportsDynamicUpdates() + "]");
        }
        buf.append("\n------------");
        return buf;
    }

    /**
     * @return the module type for this bundle descriptor
     */
    @Override
    public ArchiveType getModuleType() {
        return DOLUtils.rarType();
    }

    /**
     * @return the tracer visitor for this descriptor
     */
    @Override
    public DescriptorVisitor getTracerVisitor() {
        return new ConnectorTracerVisitor();
    }

    /**
     * @return the visitor for this bundle descriptor
     */
    @Override
    public ComponentVisitor getBundleVisitor() {
        return new ConnectorValidator();
    }

    /**
     *@param type message listener type
     */
    public MessageListener getSupportedMessageListener(String type) {
        if (this.inboundRA == null) {
            return null;
        }

        Iterator i = this.inboundRA.getMessageListeners().iterator();
        while (i.hasNext()) {
            MessageListener l = (MessageListener) i.next();
            if ((l.getMessageListenerType()).equals(type)) {
                return l;
            }
        }
        return null;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public boolean isMessageListenerSupported(String type) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean isEmpty() {
        return false;
    }

    /***********************************************************************************************
     * START
     * Deployment Consolidation to Suppport Multiple Deployment API Clients
     * Member Variable: sunConnector
     * Methods: setSunDescriptor, getSunDescriptor
     ***********************************************************************************************/

    private SunConnector sunConnector = null;

    /**
     * This returns the extra ejb sun specific info not in the RI DID.
     *
     * @return object representation of connector deployment descriptor
     */
    public SunConnector getSunDescriptor(){
        return sunConnector;
    }

    /**
     * This sets the extra connector sun specific info not in the RI DID.
     *
     * @param connector SunConnector object representation of connector deployment descriptor
     */
    public void setSunDescriptor(SunConnector connector){
        this.sunConnector = connector;
    }

    /*******************************************************************************************
     * END
     * Deployment Consolidation to Suppport Multiple Deployment API Clients
     *******************************************************************************************/
    public boolean isSpecVersionSet() {
        return specVersionSet;
    }

/*
    public boolean isModuleNameSet(){
        return moduleNameSet;
    }

    public void setModuleNameSet(boolean value){
        moduleNameSet = value;
    }
*/

    public void addConnectorAnnotation(AnnotationInfo c){
        connectorAnnotations.add(c);
    }

    public Set<AnnotationInfo> getConnectorAnnotations(){
        return connectorAnnotations;
    }

    public void setValidConnectorAnnotationProcessed(boolean processed){
        this.validConnectorAnnotationProcessed = processed;
    }

    public boolean getValidConnectorAnnotationProcessed(){
        return validConnectorAnnotationProcessed;
    }

    public void addConfigPropertyAnnotation(String className, AnnotationInfo info){
        Set<AnnotationInfo> configProperties = configPropertyAnnotations.get(className);
        if(configProperties == null){
            configProperties = new HashSet<>();
            configPropertyAnnotations.put(className, configProperties);
        }
        configProperties.add(info);
    }

    public Collection<AnnotationInfo> getConfigPropertyAnnotations(String className){
        return configPropertyAnnotations.get(className);
    }

    public Map<String, Set<AnnotationInfo>> getAllConfigPropertyAnnotations(){
        return configPropertyAnnotations;
    }

    public Set<String> getConfigPropertyProcessedClasses(){
        return configPropertyProcessedClasses;
    }

    public void addConfigPropertyProcessedClass(String className){
        configPropertyProcessedClasses.add(className);
    }

    /**
     * names of default resources created for this resource-adapter
     * computed at runtime (during RAR start)
     * Used while detecting RARs referred by deployed applications
     * @return default resources' names
     */
    public Collection<String> getDefaultResourcesNames(){
        return defaultResourceNames;
    }

    /**
     * add a default resource to list of default resource names
     * @param resourceName resource-name
     */
    public void addDefaultResourceName(String resourceName){
        defaultResourceNames.add(resourceName);
    }
}
