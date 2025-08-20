/*
 * Copyright (c) 2012 IBM Corporation.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *  Contributors:
 *
 *     Paul McMahan <pmcmahan@us.ibm.com>     - initial implementation
 */
package org.eclipse.lyo.samples.client.automation;

import jakarta.ws.rs.client.ClientBuilder;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.eclipse.lyo.client.JEEFormAuthenticator;
import org.eclipse.lyo.client.OslcClient;
import org.eclipse.lyo.oslc.domains.auto.AutomationRequest;
import org.eclipse.lyo.oslc.domains.auto.AutomationResult;
import org.eclipse.lyo.oslc.domains.auto.Oslc_autoDomainConstants;
import org.eclipse.lyo.oslc.domains.auto.ParameterInstance;
import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.core.model.OslcMediaType;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Sample of registering an external agent (adapter) with an Automation Service Provider and executing Automation
 * Requests like an ETM test execution adapter.
 */
@Slf4j
public class ETMAutomationSample implements IConstants, IAutomationRequestHandler, UncaughtExceptionHandler {

    private AutomationAdapter adapter;

    /**
     * Start the sample
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        new ETMAutomationSample().begin();
    }

    /**
     * Login to the Automation Service Provider and start polling for Automation Requests
     *
     * @throws Exception
     */
    private void begin() throws Exception {

        configureAdapter();

        try {

            log.info("Starting heart beat thread for adapter at {}", adapter.getAbout());

            // create a heartbeat thread and start it
            Thread heartbeatThread = new Thread(adapter.new HeartbeatRunnable(), "Adapter Heartbeat Thread");
            heartbeatThread.setUncaughtExceptionHandler(this);
            heartbeatThread.start();

            log.info("Starting adapter polling at {}", adapter.getAssignedWorkUrl());

            // start polling the service provider for Automation Requests.
            // this call will block until adapter.stop() is called in
            // handleAutomationRequest() below
            adapter.start(this);

        } finally {

            // stop the adapter, in case we are in this catch block due to
            // an exception being thrown
            adapter.stop();
        }
    }

    /**
     * Configure the adapter by reading its properties from a Properties file and logging into the Service Provider.
     *
     * @throws Exception
     */
    private void configureAdapter() throws Exception {

        // For the sake of simplicity this sample loads the adapter
        // properties from a file in the class loader. A real world adapter
        // would load the adapter properties from a stable location in the
        // filesystem or from a database.
        URI propertiesFileUri =
                ETMAutomationSample.class.getResource("adapter.properties").toURI();

        log.info("Loading cached adapter properties from {}", propertiesFileUri.toString());

        Properties properties = new WriteThroughProperties(propertiesFileUri);

        adapter = new AutomationAdapter(properties);

        log.info("Logging into service provider at {}", adapter.getServerUrl());

        // Have to use HttpUrlConnection if using multipart requests when usin Jersey
        ClientConfig clientConfig = new ClientConfig().connectorProvider(new HttpUrlConnectorProvider());
        // HttpUrlConnection follows redirects by default, need to turn this off for JEE Forms
        // authentication to work
        clientConfig.property(ClientProperties.FOLLOW_REDIRECTS, false);
        ClientBuilder clientBuilder = ClientBuilder.newBuilder();
        clientBuilder.withConfig(clientConfig);

        // Setup SSL support to ignore self-assigned SSL certificates - for testing only!!
        SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
        sslContextBuilder.loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE);
        clientBuilder.sslContext(sslContextBuilder.build());
        clientBuilder.hostnameVerifier(NoopHostnameVerifier.INSTANCE);

        // IBM jazz-apps use JEE Form based authentication
        clientBuilder.register(
                new JEEFormAuthenticator(adapter.getServerUrl(), adapter.getUsername(), adapter.getPassword()));
        clientBuilder.register(MultiPartFeature.class);

        adapter.setClient(new OslcClient(clientBuilder));

        log.info("Registering with service provider");

        // this call will establish the adapter's "about" property
        // as the URL for the adapter.
        adapter.register();

        properties.setProperty(
                AutomationAdapter.PROPERTY_ABOUT, adapter.getAbout().toString());
    }

    /**
     * Example callback that demonstrates a few useful things an adapter can do.
     *
     * @see IAutomationRequestHandler#handleAutomationRequest(AutomationRequest, AutomationAdapter)
     */
    public AutomationResult handleAutomationRequest(AutomationRequest request, AutomationAdapter adapter)
            throws AutomationException {

        log.info("Adapter has been assigned an Automation Request at {}", request.getAbout());

        AutomationResult result = null;

        try {

            // Create a new automation result
            result = new AutomationResult();

            // Save the start time in the result
            result.getExtendedProperties().put(PROPERTY_RQM_START_TIME, new Date(System.currentTimeMillis()));

            // An example of how to get the script for the AutomationRequest.
            // The script might contain references to resources needed to
            // execute the test.
            adapter.sendMessageForRequest(new Message("LYO_1", "Downloading script document"), request);

            Document script = adapter.getScriptDocument(request);

            adapter.sendMessageForRequest(new Message("LYO_2", "Script document successfully downloaded"), request);

            // update progress indication
            adapter.sendProgressForRequest(50, request);

            // execute the script with the parameters from the Automation Request
            executeScript(script, request.getInputParameter(), adapter, request);

            // Upload an attachment for the result
            File attachment = getSampleFile();
            URI attachmentURI = adapter.uploadAttachment(attachment, request);

            // Set the attachment URI in the result
            result.getExtendedProperties().put(PROPERTY_RQM_ATTACHMENT, attachmentURI);

            // Add some rich text to the result
            Element xhtmlTableElement = createXhtmlTable();
            QName contributionQname = new QName(Oslc_autoDomainConstants.AUTOMATION_NAMSPACE, "contribution");
            result.getExtendedProperties().put(contributionQname, xhtmlTableElement);

            // Set the verdict for the result
            result.addVerdict(new Link(new URI(Oslc_autoDomainConstants.AUTOMATION_NAMSPACE + "passed")));

            // Save the end time in the result
            result.getExtendedProperties().put(PROPERTY_RQM_END_TIME, new Date(System.currentTimeMillis()));

            // update progress indication
            adapter.sendProgressForRequest(99, request);

            log.info(
                    "Returning a result with verdict {}",
                    result.getVerdict().iterator().next().getValue());

        } catch (AutomationRequestCanceledException e) {

            log.info(
                    "Automation Request \"{}\" was canceled.",
                    e.getCanceledRequest().getTitle());

            // clean up any resources created for test execution here

            result = null;

        } catch (Exception e) {

            // cancel the request since it could not be completed
            adapter.cancel(request);

            throw new AutomationException(e);
        }

        return result;
    }

    /**
     * Execute the script with the provided input parameters.
     *
     * @param script
     * @param inputParameters
     * @throws InterruptedException
     * @throws AutomationException
     * @throws URISyntaxException
     * @throws IOException
     */
    private void executeScript(
            Document script, Set<Link> inputParameters, AutomationAdapter adapter, AutomationRequest request)
            throws InterruptedException, AutomationException, IOException, URISyntaxException {

        String scriptTitle = script.getDocumentElement()
                .getElementsByTagNameNS(NAMESPACE_URI_DC_ELEMENTS, "title")
                .item(0)
                .getTextContent();

        log.info("Running script named '{}'", scriptTitle);

        log.info("Input parameters:");
        for (Link parameterLink : inputParameters) {
            ParameterInstance parameter = adapter.getClient()
                    .getResource(parameterLink.getValue().toString(), OslcMediaType.APPLICATION_RDF_XML)
                    .readEntity(ParameterInstance.class);
            String paramStr = "\t" + parameter.getName() + ": " + parameter.getValue();
            log.info(paramStr);
        }

        /*
         * Add code here to execute the test script
         */
        Thread.sleep(1000);

        // Update the request status
        StatusResponse statusResponse =
                new StatusResponse(StatusResponse.STATUS_OK, "Script '" + scriptTitle + "' was executed successfully.");

        adapter.sendStatusForRequest(statusResponse, request);
    }

    /**
     * Create an element for a simple XHTML table
     *
     * @return
     * @throws ParserConfigurationException
     */
    private Element createXhtmlTable() throws ParserConfigurationException {

        Document document =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element divElement = document.createElementNS(NAMESPACE_URI_XHTML, "div");

        Element tableElement = document.createElementNS(NAMESPACE_URI_XHTML, "table");
        divElement.appendChild(tableElement);
        tableElement.setAttribute("border", "1");

        Element tr1Element = document.createElementNS(NAMESPACE_URI_XHTML, "tr");
        Element tr2Element = document.createElementNS(NAMESPACE_URI_XHTML, "tr");
        tableElement.appendChild(tr1Element);
        tableElement.appendChild(tr2Element);

        Element th1Element = document.createElementNS(NAMESPACE_URI_XHTML, "th");
        Element th2Element = document.createElementNS(NAMESPACE_URI_XHTML, "th");
        tr1Element.appendChild(th1Element);
        tr1Element.appendChild(th2Element);
        th1Element.setTextContent("Column 1");
        th2Element.setTextContent("Column 2");

        Element td1Element = document.createElementNS(NAMESPACE_URI_XHTML, "td");
        Element td2Element = document.createElementNS(NAMESPACE_URI_XHTML, "td");
        tr2Element.appendChild(td1Element);
        tr2Element.appendChild(td2Element);
        td1Element.setTextContent("Value 1");
        td2Element.setTextContent("Value 2");

        return divElement;
    }

    /**
     * Get a sample file from the class loader
     *
     * @return
     * @throws URISyntaxException
     */
    private File getSampleFile() throws URISyntaxException {

        String packagePath = getClass().getPackage().getName().replace('.', '/');

        String sampleFilePath = packagePath + "/sample.png";

        URL sampleURL = getClass().getClassLoader().getResource(sampleFilePath);

        File sampleImageFile = new File(sampleURL.toURI());

        return sampleImageFile;
    }

    /**
     * Called when the heartbeat thread throws an uncaught Exception
     *
     * @param thread
     * @param throwable
     */
    public void uncaughtException(Thread thread, Throwable throwable) {

        log.error("Adapter heartbeat running in Thread {} threw an uncaught exception.", thread.getName());

        throwable.printStackTrace(System.err);

        adapter.stop();
    }
}
