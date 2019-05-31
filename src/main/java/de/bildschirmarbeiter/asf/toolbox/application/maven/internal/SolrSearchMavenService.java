package de.bildschirmarbeiter.asf.toolbox.application.maven.internal;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.jayway.jsonpath.JsonPath;
import de.bildschirmarbeiter.application.message.spi.MessageService;
import de.bildschirmarbeiter.asf.toolbox.application.maven.MavenService;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static de.bildschirmarbeiter.asf.toolbox.application.message.system.LogMessage.info;

@Component(
    service = MavenService.class
)
public class SolrSearchMavenService implements MavenService {

    @Reference
    private volatile CloseableHttpClient httpClient;

    @Reference
    private volatile MessageService messageService;

    private static final String SEARCH_TEMPLATE = "https://search.maven.org/solrsearch/select?q=g:%%22%s%%22+AND+a:%%22%s%%22&core=gav&rows=1&wt=json";

    public SolrSearchMavenService() {
    }

    public String findLatestVersion(final String groupId, final String artifactId) throws Exception {
        final String uri = String.format(SEARCH_TEMPLATE, groupId, artifactId);
        final HttpGet httpGet = new HttpGet(uri);
        try (final CloseableHttpResponse response = httpClient.execute(httpGet)) {
            httpGet.releaseConnection();
            final InputStream content = response.getEntity().getContent();
            final String json = IOUtils.toString(content, StandardCharsets.UTF_8);
            messageService.send(info(this, json));
            final List<String> versions = JsonPath.parse(json).read("$..docs[0].v");
            if (versions != null && versions.size() > 0) {
                return versions.get(0);
            } else {
                return null;
            }
        }
    }

}
