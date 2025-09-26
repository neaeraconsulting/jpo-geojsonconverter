package us.dot.its.jpo.geojsonconverter.converter.srm;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.common.serialization.VoidSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import us.dot.its.jpo.geojsonconverter.partitioner.RsuVehicleIdKey;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.Point;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.ProcessedSrm;
import us.dot.its.jpo.geojsonconverter.pojos.geojson.srm.SrmProperties;
import us.dot.its.jpo.geojsonconverter.serialization.deserializers.JsonDeserializer;
import us.dot.its.jpo.geojsonconverter.validator.SrmJsonValidator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Slf4j
@RunWith(Parameterized.class)
public class SrmTopologyTest {
    final String inputTopicName = "topic.OdeSrmJson";
    final String outputTopicName = "topic.ProcessedSrm";

    @Parameter(0)
    public String inputJson;

    @Parameters
    public static Collection<Object[]> params() throws IOException {
        return Arrays.asList(new Object[][] {
                { loadResource("json/valid.srm.json") }
        });
    }

    @Test
    public void topologyTest() {
        Resource jsonSchemaResource = getResource("schemas/srm.schema.json");
        SrmJsonValidator validator = new SrmJsonValidator(jsonSchemaResource);
        SrmConverter converter = new SrmConverter();
        Topology topology = SrmTopology.build(inputTopicName, outputTopicName, validator, converter);
        try (var driver = new TopologyTestDriver(topology)) {
            var inputTopic = driver.createInputTopic(inputTopicName, new VoidSerializer(), new StringSerializer());
            var outputTopic = driver.createOutputTopic(outputTopicName,
                    new JsonDeserializer<>(RsuVehicleIdKey.class), new JsonDeserializer<>(ProcessedSrm.class));

            inputTopic.pipeInput(inputJson);

            List<KeyValue<RsuVehicleIdKey, ProcessedSrm>> results = outputTopic.readKeyValuesToList();

            assertThat(results, hasSize(1));

            KeyValue<RsuVehicleIdKey, ProcessedSrm> result = results.getFirst();
            RsuVehicleIdKey key = result.key;
            assertThat(key, notNullValue());
            assertThat(key.getRsuId(), equalTo("172.18.0.1"));
            ProcessedSrm processedSrm = result.value;
            assertThat(processedSrm, notNullValue());
            SrmProperties properties = processedSrm.getProperties();
            assertThat(properties, notNullValue());
            assertThat(properties.getAsn1(), notNullValue());
            assertThat(properties.getOdeReceivedAt(), notNullValue());
            assertThat(properties.getMessageType(), equalTo("SRM"));
            assertThat(properties.getRequests(), hasSize(greaterThan(0)));

            Point geometry = processedSrm.getGeometry();
            assertThat(geometry, notNullValue());
        }
    }

    private static String loadResource(String path) throws IOException {
        Resource resource = getResource(path);
        return readResource(resource);
    }

    private static Resource getResource(String path) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        return resourceLoader.getResource("classpath:" + path);
    }

    private static String readResource(Resource resource) throws IOException {
        byte[] bytes = resource.getInputStream().readAllBytes();
        return  new String(bytes, StandardCharsets.UTF_8);
    }
}
