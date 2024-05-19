
package org.mq.captiway.sparkbase.transactionWsdl;

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
@WebServiceClient(name = "SparkbaseTransactionWsdlImplService", targetNamespace = "urn:SparkbaseTransactionWsdl", wsdlLocation = "https://trans.api.factor4gift.com/v4/transaction?wsdl")
public class SparkbaseTransactionWsdlImplService
    extends Service
{

    private final static URL SPARKBASETRANSACTIONWSDLIMPLSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(SparkbaseTransactionWsdlImplService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = SparkbaseTransactionWsdlImplService.class.getResource(".");
            url = new URL(baseUrl, "https://trans.api.factor4gift.com/v4/transaction?wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'https://trans.api.factor4gift.com/v4/transaction?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        SPARKBASETRANSACTIONWSDLIMPLSERVICE_WSDL_LOCATION = url;
    }

    public SparkbaseTransactionWsdlImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SparkbaseTransactionWsdlImplService() {
        super(SPARKBASETRANSACTIONWSDLIMPLSERVICE_WSDL_LOCATION, new QName("urn:SparkbaseTransactionWsdl", "SparkbaseTransactionWsdlImplService"));
    }

    /**
     * 
     * @return
     *     returns SparkbaseTransactionWsdlPort
     */
    @WebEndpoint(name = "SparkbaseTransactionWsdlPortPort")
    public SparkbaseTransactionWsdlPort getSparkbaseTransactionWsdlPortPort() {
        return super.getPort(new QName("urn:SparkbaseTransactionWsdl", "SparkbaseTransactionWsdlPortPort"), SparkbaseTransactionWsdlPort.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns SparkbaseTransactionWsdlPort
     */
    @WebEndpoint(name = "SparkbaseTransactionWsdlPortPort")
    public SparkbaseTransactionWsdlPort getSparkbaseTransactionWsdlPortPort(WebServiceFeature... features) {
        return super.getPort(new QName("urn:SparkbaseTransactionWsdl", "SparkbaseTransactionWsdlPortPort"), SparkbaseTransactionWsdlPort.class, features);
    }

}
