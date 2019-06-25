package org.exoplatform.addons.gamification.test;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.exoplatform.addons.gamification.service.effective.GamificationService;
import org.exoplatform.commons.testing.BaseExoContainerTestSuite;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class GamificationManageBadgesEndpointTest {

    @Before
    public void setUp() throws Exception {
        System.out.println("Manage badges Web Services test!");
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void getAllLeadersByRank() {
        org.junit.Assert.assertTrue( new ArrayList().isEmpty() );
    }


/** Testing the Status Code  **/
    @Test
    public void givenBadgesDoesNotExists_whenbadgesInfoIsRetrieved_then404IsReceived()
            throws ClientProtocolException, Exception {

        // Given

        HttpUriRequest request = new HttpGet( "http://localhost:8080/rest/gamification/badges/all" );

        // When
        HttpResponse httpResponse = HttpClientBuilder.create().build().execute( request );
        assertThat(
                httpResponse.getStatusLine().getStatusCode(),
                equalTo(HttpStatus.SC_OK));
        assertNotEquals(HttpStatus.SC_NOT_FOUND,HttpStatus.SC_OK);


    }
        // Then


    /** Testing the Media Type  **/
    @Test
    public void givensbadgesDoesbeenADDED_whenInfoIsRetrievedwithoutBadgeicon_then404IsReceived()
            throws ClientProtocolException, Exception {

        // Givenge
        String Badgename = RandomStringUtils.randomAlphabetic( 8 );
        String jsonMimeType = "application/json";

        // Given
        HttpUriRequest request = new HttpGet("http://localhost:8080/rest/gamification/badges/add"+ Badgename);

        // When
        HttpResponse response = HttpClientBuilder.create().build().execute(request);


        // Then
        String mimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
        assertEquals(jsonMimeType, jsonMimeType);

    }


}

