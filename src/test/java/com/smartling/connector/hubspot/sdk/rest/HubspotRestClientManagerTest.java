package com.smartling.connector.hubspot.sdk.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.google.common.collect.Maps;
import com.smartling.connector.hubspot.sdk.HubspotApiException;
import com.smartling.connector.hubspot.sdk.RefreshTokenData;
import com.smartling.connector.hubspot.sdk.rest.token.HubspotTokenProvider;
import com.smartling.connector.hubspot.sdk.rest.token.TokenProvider;

public class HubspotRestClientManagerTest
{
    private static final int STR_LENGHT = 36;
    private static final String INIT_OK = "init";
    private static final String RETURN_TOKEN = "token";

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        
    }

    @Test
    public void testGetToken() throws Exception
    {
        String expectedToken = RandomStringUtils.random(STR_LENGHT);
        HubspotRestClientManager clientManager = new HubspotRestClientManager(createConfiguration(true, true, expectedToken));
        TokenProvider provider = (TokenProvider)Whitebox.getInternalState(clientManager, "tokenProvider");
        assertTrue(provider instanceof FakeTokenProvider);
        clientManager.execute(token -> function(token, expectedToken));
    }

    @Test
    public void testCreateTokenProviderNoDecorator() throws Exception
    {
        HubspotRestClientManager clientManager = new HubspotRestClientManager(createConfiguration(false, false, null));
        TokenProvider provider = (TokenProvider)Whitebox.getInternalState(clientManager, "tokenProvider");
        assertTrue(provider instanceof HubspotTokenProvider);
    }

    @Test
    public void testCreateTokenProviderDecoratorFails() throws Exception
    {
        HubspotRestClientManager clientManager = new HubspotRestClientManager(createConfiguration(true, false, null));
        TokenProvider provider = (TokenProvider)Whitebox.getInternalState(clientManager, "tokenProvider");
        assertTrue(provider instanceof HubspotTokenProvider);
    }

    private Void function(String token, String expectedToken)
    {
        assertEquals(expectedToken, token);
        return null;
    }

    private Configuration createConfiguration(boolean decorate, boolean init, String token)
    {
        Configuration configuration = Configuration.build(RandomStringUtils.random(STR_LENGHT), RandomStringUtils.random(36));
        Map<String, String> extProperties = Maps.newHashMap();
        if (decorate)
        {
            if (init)
                extProperties.put(INIT_OK, "");
            extProperties.put(RETURN_TOKEN, token);
            extProperties.put(TokenProvider.TOKEN_PROVIDER_DECORATOR_CLASS, FakeTokenProvider.class.getName());
            configuration.setProperties(extProperties);
        }
        return configuration;
    }

    public static class FakeTokenProvider implements TokenProvider
    {
        private String token;

        public FakeTokenProvider(Configuration configuration, TokenProvider tokenProvider) throws HubspotApiException
        {
            assertTrue(tokenProvider instanceof HubspotTokenProvider);
            this.token = configuration.getPropertyValue(RETURN_TOKEN);
            if (null == configuration.getPropertyValue(INIT_OK))
                throw new HubspotApiException("Test exception");
        }

        @Override
        public RefreshTokenData getTokenData() throws HubspotApiException
        {
            RefreshTokenData token = new RefreshTokenData();
            token.setAccessToken(this.token);
            return token;
        }
    }
}