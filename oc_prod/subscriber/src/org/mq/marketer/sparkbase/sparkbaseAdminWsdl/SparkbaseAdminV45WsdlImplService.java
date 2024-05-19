
package org.mq.marketer.sparkbase.sparkbaseAdminWsdl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "SparkbaseAdminV45WsdlImplService", targetNamespace = "urn:SparkbaseAdminV45Wsdl", wsdlLocation = "https://api.sparkbase.com/v4.5/admin.wsdl")
public class SparkbaseAdminV45WsdlImplService
    extends Service
{

    private final static URL SPARKBASEADMINV45WSDLIMPLSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(org.mq.marketer.sparkbase.sparkbaseAdminWsdl.SparkbaseAdminV45WsdlImplService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = org.mq.marketer.sparkbase.sparkbaseAdminWsdl.SparkbaseAdminV45WsdlImplService.class.getResource(".");
            url = new URL(baseUrl, "https://api.sparkbase.com/v4.5/admin.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'https://api.sparkbase.com/v4.5/admin.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        SPARKBASEADMINV45WSDLIMPLSERVICE_WSDL_LOCATION = url;
    }

    public SparkbaseAdminV45WsdlImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SparkbaseAdminV45WsdlImplService() {
        super(SPARKBASEADMINV45WSDLIMPLSERVICE_WSDL_LOCATION, new QName("urn:SparkbaseAdminV45Wsdl", "SparkbaseAdminV45WsdlImplService"));
    }

    /**
     * 
     * @return
     *     returns SparkbaseAdminV45WsdlPort
     */
    @WebEndpoint(name = "SparkbaseAdminV45WsdlPortPort")
    public SparkbaseAdminV45WsdlPort getSparkbaseAdminV45WsdlPortPort() {
        return super.getPort(new QName("urn:SparkbaseAdminV45Wsdl", "SparkbaseAdminV45WsdlPortPort"), SparkbaseAdminV45WsdlPort.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SparkbaseAdminV45WsdlPort
     */
    @WebEndpoint(name = "SparkbaseAdminV45WsdlPortPort")
    public SparkbaseAdminV45WsdlPort getSparkbaseAdminV45WsdlPortPort(WebServiceFeature... features) {
        return super.getPort(new QName("urn:SparkbaseAdminV45Wsdl", "SparkbaseAdminV45WsdlPortPort"), SparkbaseAdminV45WsdlPort.class, features);
    }

}
