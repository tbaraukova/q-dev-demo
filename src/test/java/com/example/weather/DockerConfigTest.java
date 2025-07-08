package com.example.weather;

import org.testng.annotations.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class DockerConfigTest {

    @Test
    public void testDockerProfileConfiguration() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application-docker.properties")) {
            props.load(input);
        }
        
        assertEquals(props.getProperty("server.port"), "8080", "Server port should be 8080 in Docker profile");
        assertEquals(props.getProperty("spring.thymeleaf.cache"), "true", "Thymeleaf cache should be enabled in Docker profile");
    }
}