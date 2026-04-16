package org.jenkinsci.plugin.gitea.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GiteaOwnerJacksonTest {

    @Test
    public void testDeserializationWithLoginNameWorks() throws Exception {
        String json = "{\"id\":123,\"login\":\"testuser\",\"login_name\":\"testuser_login\",\"full_name\":\"Test User\",\"email\":\"test@example.com\",\"avatar_url\":\"http://avatar\",\"username\":\"testuser\"}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        GiteaOwner owner = mapper.readValue(json, GiteaOwner.class);
        assertNotNull(owner);
        // login_name should have overwritten login if it comes after it, or vice versa.
        // In the JSON above, login_name comes after login.
        assertEquals("testuser_login", owner.getLogin());
    }

    @Test
    public void testDeserializationWithAnnotationsDisabled() throws Exception {
        // This simulates the Jenkins environment where annotations might be ignored
        String json = "{\"id\":123,\"login\":\"testuser\",\"login_name\":\"testuser_login\",\"full_name\":\"Test User\",\"avatar_url\":\"http://avatar\"}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(com.fasterxml.jackson.databind.MapperFeature.USE_ANNOTATIONS);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        GiteaOwner owner = mapper.readValue(json, GiteaOwner.class);
        assertNotNull(owner);
        assertEquals("testuser_login", owner.getLogin());
        assertEquals("Test User", owner.getFullName());
        assertEquals("http://avatar", owner.getAvatarUrl());
    }

    @Test
    public void testGiteaOrganizationDeserialization() throws Exception {
        String json = "{\"id\":456,\"username\":\"org1\",\"login_name\":\"org1_login\",\"full_name\":\"Organization One\",\"description\":\"Desc\"}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        GiteaOrganization org = mapper.readValue(json, GiteaOrganization.class);
        assertNotNull(org);
        assertEquals("org1_login", org.getLogin());
        assertEquals("org1_login", org.getUsername());
    }

    @Test
    public void testGiteaUserDeserialization() throws Exception {
        String json = "{\"id\":789,\"login\":\"user1\",\"login_name\":\"user1_login\",\"full_name\":\"User One\"}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        GiteaUser user = mapper.readValue(json, GiteaUser.class);
        assertNotNull(user);
        assertEquals("user1_login", user.getLogin());
        // For GiteaUser, username and login are separate fields in GiteaOwner, but usually login sets username in constructor.
        // During deserialization they are set independently if present.
    }
}
