package org.pentaho.platform.spring.security.saml.resources;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.util.resource.ResourceException;
import org.opensaml.xml.util.DatatypeHelper;

public class PentahoHttpResource extends org.opensaml.util.resource.HttpResource {

    private final String resourceUrl;
    private final HttpClient httpClient;

    public PentahoHttpResource(String resource) {
        super(resource);
        resourceUrl = DatatypeHelper.safeTrimOrNullString(resource);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Resource URL may not be null or empty");
        } else {
            httpClient = new HttpClient();
            HttpConnectionManagerParams connMgrParams = httpClient.getHttpConnectionManager().getParams();
            connMgrParams.setConnectionTimeout(90000);
            connMgrParams.setSoTimeout(90000);
        }
    }

    @Override
    public boolean exists() throws ResourceException {
        GetMethod getMethod = new GetMethod(super.getLocation());
        getMethod.addRequestHeader("Connection", "close");
        getMethod.addRequestHeader("Accept", "*/*");

        boolean var8;
        try {
            this.httpClient.executeMethod(getMethod);
            if (getMethod.getStatusCode() != 200) {
                var8 = false;
                return var8;
            }
            var8 = true;
        } catch (IOException e) {
            throw new ResourceException("Unable to contact resource URL: " + this.resourceUrl, e);
        } finally {
            getMethod.releaseConnection();
        }
        return var8;
    }
    @Override
    public DateTime getLastModifiedTime() throws ResourceException {
        GetMethod getMethod = new GetMethod(this.resourceUrl);
        getMethod.addRequestHeader("Connection", "close");
        getMethod.addRequestHeader("Accept", "*/*");

        DateTime dateTime;
        try {
            this.httpClient.executeMethod(getMethod);
            if (getMethod.getStatusCode() != 200) {
                throw new ResourceException("Unable to retrieve resource URL " + this.resourceUrl + ", received HTTP status code " + getMethod.getStatusCode());
            }

            Header lastModifiedHeader = getMethod.getResponseHeader("Last-Modified");
            if (lastModifiedHeader == null || DatatypeHelper.isEmpty(lastModifiedHeader.getValue())) {
                dateTime = new DateTime();
                return dateTime;
            }


            long lastModifiedTime = DateUtil.parseDate(lastModifiedHeader.getValue()).getTime();
            dateTime = new DateTime(lastModifiedTime, ISOChronology.getInstanceUTC());
        } catch (IOException e) {
            throw new ResourceException("Unable to contact resource URL: " + this.resourceUrl, e);
        } catch (DateParseException e) {
            throw new ResourceException("Unable to parse last modified date for resource:" + this.resourceUrl, e);
        } finally {
            getMethod.releaseConnection();
        }
        return dateTime;
    }
}
