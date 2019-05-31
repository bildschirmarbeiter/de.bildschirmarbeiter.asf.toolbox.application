package de.bildschirmarbeiter.asf.toolbox.application.features;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.bildschirmarbeiter.asf.toolbox.application.maven.Coordinates;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.felix.cm.file.ConfigurationHandler;
import org.joox.Match;
import org.ops4j.pax.url.mvn.MavenResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import static org.joox.JOOX.$;

@Component(
    service = FeatureService.class
)
public class FeatureService {

    @Reference
    private volatile DocumentBuilderFactory documentBuilderFactory;

    @Reference
    private volatile MavenResolver mavenResolver;

    private final Logger logger = LoggerFactory.getLogger(FeatureService.class);

    public FeatureService() {
    }

    public List<Feature> readFeatures(final byte[] xml) throws Exception {
        final List<Feature> features = new LinkedList<>();
        final Document document = buildDocument(xml);
        final Match featureMatch = $(document).children("feature");
        featureMatch.each(f -> {
            final String name = $(f).attr("name");
            final Feature feature = new Feature(name);
            // bundles
            final Match bundleMatch = $(f).children("bundle");
            bundleMatch.each(b -> {
                final String[] gav = $(b).text().substring(4).split("/");
                final Bundle bundle = new Bundle(gav[0], gav[1], gav[2]);
                feature.addBundle(bundle);
            });
            // configs
            final Match configMatch = $(f).children("config");
            configMatch.each(c -> {
                try {
                    final Configuration configuration = parseConfig(c.element());
                    if (configuration != null) {
                        feature.addConfiguration(configuration);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            });

            final Match configfileMatch = $(f).children("configfile");
            configfileMatch.each(c -> {
                try {
                    final Configuration configuration = parseConfigfile(c.element());
                    feature.addConfiguration(configuration);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            });

            // child features
            final Match featMatch = $(f).children("feature");
            featMatch.each(fc -> {
                final String fcName = $(fc).text();
                feature.addFeature(Feature.forJava(fcName));
            });
            features.add(feature);
        });
        return features;
    }

    public List<Bundle> readBundles(final byte[] xml) throws Exception {
        final List<Bundle> bundles = new LinkedList<>();
        final Document document = buildDocument(xml);
        final Match match = $(document).find("bundle");
        match.each(context -> {
            final String[] gav = $(context).text().substring(4).split("/");
            final Bundle bundle = new Bundle(gav[0], gav[1], gav[2]);
            bundles.add(bundle);
        });
        return bundles;
    }

    public byte[] updateBundles(final byte[] xml, final List<BundleUpdate> updates) throws Exception {
        final Document document = buildDocument(xml);

        for (final BundleUpdate bundleUpdate : updates) {
            final String match = String.format("mvn:%s/%s/%s", bundleUpdate.getBundle().getGroupId(), bundleUpdate.getBundle().getArtifactId(), bundleUpdate.getBundle().getVersion());
            final String update = String.format("mvn:%s/%s/%s", bundleUpdate.getBundle().getGroupId(), bundleUpdate.getBundle().getArtifactId(), bundleUpdate.getVersion());
            $(document).find("bundle").matchText(match).each(context -> {
                $(context).text(update);
            });
        }

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        $(document).write(out);
        return out.toByteArray();
    }

    private Configuration buildConfiguration(final String name, final String content) throws Exception {
        final Configuration configuration = new Configuration(name);
        if (name.endsWith(".cfg")) {
            final Reader reader = new StringReader(content);
            final Properties properties = new Properties();
            properties.load(reader);
            properties.forEach((key, value) -> configuration.addEntry(
                key.toString(),
                StringEscapeUtils.escapeJava(value.toString())
            ));
            reader.close();
        } else { // ) { if (name.endsWith(".config")
            final InputStream inputStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
            final Dictionary config = ConfigurationHandler.read(inputStream);
            final Enumeration keys = config.keys();
            while (keys.hasMoreElements()) {
                final Object key = keys.nextElement();
                final String value = ConfigurationUtil.toJavaString(config.get(key));
                configuration.addEntry((String) key, value);
            }
            inputStream.close();
        }
        return configuration;
    }

    private String readConfigfile(final Coordinates coordinates) throws Exception {
        final File file = mavenResolver.resolve(coordinates.getGroupId(), coordinates.getArtifactId(), coordinates.getClassifier(), coordinates.getExtension(), coordinates.getVersion());
        final Path path = file.toPath();
        final byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private Configuration parseConfig(final Element element) throws Exception {
        final String name = element.getAttribute("name");
        final String external = element.getAttribute("external");
        if (external != null && Boolean.valueOf(external)) {
            final String[] gavec = element.getTextContent().substring(4).split("/");
            final Coordinates coordinates = new Coordinates(gavec);
            final String content = readConfigfile(coordinates);
            return buildConfiguration(name, content);
        } else {
            final String content = element.getTextContent();
            return buildConfiguration(name, content);
        }
    }

    private Configuration parseConfigfile(final Element element) throws Exception {
        final String finalname = element.getAttribute("finalname");
        final int index = finalname.lastIndexOf("/");
        final String name;
        if (index > -1) {
            name = finalname.substring(index + 1);
        } else {
            name = finalname;
        }

        final String url = element.getTextContent();
        if (url.startsWith("mvn:")) {
            final String[] gavec = url.substring(4).split("/");
            final Coordinates coordinates = new Coordinates(gavec);
            final String content = readConfigfile(coordinates);
            return buildConfiguration(name, content);
        } else {
            final String message = String.format("URL not supported: %s", url);
            throw new RuntimeException(message);
        }
    }

    private Document buildDocument(final byte[] xml) throws ParserConfigurationException, IOException, SAXException {
        final InputStream inputStream = new ByteArrayInputStream(xml);
        final DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        final Document document = builder.parse(inputStream);
        inputStream.close();
        return document;
    }

}
