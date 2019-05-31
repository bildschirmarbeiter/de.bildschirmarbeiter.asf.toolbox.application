package de.bildschirmarbeiter.asf.toolbox.application;

import javax.xml.parsers.DocumentBuilderFactory;

import com.github.jknack.handlebars.Handlebars;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration<CloseableHttpClient> closeableHttpClientServiceRegistration;

    private ServiceRegistration<DocumentBuilderFactory> documentBuilderFactoryServiceRegistration;

    private ServiceRegistration<Handlebars> handlebarsServiceRegistration;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {
        closeableHttpClientServiceRegistration = bundleContext.registerService(CloseableHttpClient.class, closeableHttpClient(), null);
        documentBuilderFactoryServiceRegistration = bundleContext.registerService(DocumentBuilderFactory.class, documentBuilderFactory(), null);
        handlebarsServiceRegistration = bundleContext.registerService(Handlebars.class, handlebars(), null);
    }

    @Override
    public void stop(final BundleContext bundleContext) throws Exception {
        closeableHttpClientServiceRegistration.unregister();
        documentBuilderFactoryServiceRegistration.unregister();
        handlebarsServiceRegistration.unregister();
    }

    private CloseableHttpClient closeableHttpClient() {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        //connectionManager.setDefaultMaxPerRoute(200);
        //connectionManager.setMaxTotal(1000);
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    private DocumentBuilderFactory documentBuilderFactory() {
        return DocumentBuilderFactory.newInstance();
    }

    private Handlebars handlebars() {
        return new Handlebars();
    }

}
