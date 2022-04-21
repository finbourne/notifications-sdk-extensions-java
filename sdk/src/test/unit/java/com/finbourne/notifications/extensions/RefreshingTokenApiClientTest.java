package com.finbourne.notifications.extensions;

import com.finbourne.notifications.ApiCallback;
import com.finbourne.notifications.ApiClient;
import com.finbourne.notifications.ApiException;
import com.finbourne.notifications.Pair;
import com.finbourne.notifications.extensions.auth.RefreshingTokenProvider;
import com.finbourne.notifications.extensions.auth.FinbourneToken;
import com.finbourne.notifications.extensions.auth.FinbourneTokenException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class RefreshingTokenApiClientTest {

    private RefreshingTokenApiClient refreshingTokenApiClient;

    // mock dependencies
    private ApiClient defaultApiClient;
    private RefreshingTokenProvider tokenProvider;

    // mock tokens
    private FinbourneToken finbourneToken = new FinbourneToken("access_01", "refresh_01", LocalDateTime.now());
    private FinbourneToken anotherFinbourneToken = new FinbourneToken("access_02", "refresh_01", LocalDateTime.now());

    // call params
    private String path = "/get_portfolios";
    private String method = "GET";
    private List<Pair> queryParams = new ArrayList<>();
    private List<Pair> collectionQueryParams = new ArrayList<>();
    private Object body = new Object();
    private Map<String,String> headerParams = new HashMap<>();
    private Map<String,String> cookieParams = new HashMap<>();
    private Map<String,Object> formParams = new HashMap<>();
    private String[] authNames = new String[]{};
    private ApiCallback apiCallback = mock(ApiCallback.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws FinbourneTokenException {
        defaultApiClient = mock(ApiClient.class);
        tokenProvider = mock(RefreshingTokenProvider.class);

        doReturn(finbourneToken).when(tokenProvider).get();

        refreshingTokenApiClient = new RefreshingTokenApiClient(defaultApiClient, tokenProvider);
    }

    @Test
    public void buildCall_ShouldUpdateAuthHeaderAndDelegateBuildCall() throws ApiException {
        refreshingTokenApiClient.buildCall(path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams, formParams, authNames, apiCallback);
        verify(defaultApiClient).addDefaultHeader("Authorization", "Bearer access_01");
        verify(defaultApiClient).buildCall(path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams,formParams, authNames, apiCallback);
    }

    @Test
    public void buildCall_ShouldUpdateAuthHeaderOnEveryCall() throws ApiException, FinbourneTokenException {
        refreshingTokenApiClient.buildCall(path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams, formParams, authNames, apiCallback);
        verify(defaultApiClient).addDefaultHeader("Authorization", "Bearer access_01");
        verify(defaultApiClient).buildCall(path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams,  formParams, authNames, apiCallback);

        // mock our token expiring and we now have an updated token to call api with
        doReturn(anotherFinbourneToken).when(tokenProvider).get();

        // ensure that before delegating to buildCall in the default api client we firstly update it's header to use
        // the new token
        refreshingTokenApiClient.buildCall(path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams, formParams, authNames, apiCallback);
        verify(defaultApiClient).addDefaultHeader("Authorization", "Bearer access_02");
        verify(defaultApiClient, times(2)).buildCall(path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams, formParams, authNames, apiCallback);
    }

    @Test
    public void buildCall_OnExceptionRetrievingToken_ShouldThrowApiException() throws FinbourneTokenException, ApiException {
        // mocking behaviour of an exception being thrown when attempting to retrieve an access token
        FinbourneTokenException finbourneTokenException = new FinbourneTokenException("Failed to create token for some reason");
        doThrow(finbourneTokenException).when(tokenProvider).get();

        thrown.expect(ApiException.class);
        thrown.expectCause(equalTo(finbourneTokenException));
        refreshingTokenApiClient.buildCall(path, method, queryParams, collectionQueryParams, body, headerParams, cookieParams, formParams, authNames, apiCallback);
    }

    @Test
    public void buidCall_OnUnderlyingApiClientException_ShouldRethrowExactException() throws ApiException {
        // mocking behaviour of an exception when making a remote api call
        ApiException apiException = new ApiException("An API call failure");
        doThrow(apiException).when(defaultApiClient).buildCall(path, method, queryParams, collectionQueryParams, body, headerParams,  cookieParams, formParams, authNames, apiCallback);
        try {
            refreshingTokenApiClient.buildCall(path, method, queryParams, collectionQueryParams, body, headerParams,  cookieParams, formParams, authNames, apiCallback);
        } catch (ApiException e){
            // ensure that the exception rethrown is the exact instance of the exception thrown by the API call and
            // is unchanged
            assertThat(e, sameInstance(apiException));
        }
    }

}
