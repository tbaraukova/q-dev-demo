package com.example.weather;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@SpringBootTest
@ActiveProfiles("docker")
public class DockerConfigTest {

    @Value("${server.port}")
    private int serverPort;

    @Value("${spring.thymeleaf.cache}")
    private boolean thymeleafCache;

    @Test
    public void testDockerProfileConfiguration() {
        // Verify that the Docker profile loads the correct configuration
        assertEquals(serverPort, 8080, "Server port should be 8080 in Docker profile");
        assertTrue(thymeleafCache, "Thymeleaf cache should be enabled in Docker profile");
    }
}