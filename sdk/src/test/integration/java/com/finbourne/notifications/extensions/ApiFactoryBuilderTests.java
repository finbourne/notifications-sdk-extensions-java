package com.finbourne.notifications.extensions;

import com.finbourne.notifications.ApiException;
import com.finbourne.notifications.api.EventTypesApi;
import com.finbourne.notifications.model.ResourceListOfEventTypeSchema;
import com.finbourne.notifications.extensions.auth.FinbourneTokenException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

public class ApiFactoryBuilderTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void build_WithExistingConfigurationFile_ShouldReturnFactory() throws ApiException, ApiConfigurationException, FinbourneTokenException {
        ApiFactory apiFactory = ApiFactoryBuilder.build(CredentialsSource.credentialsFile);
        assertThat(apiFactory, is(notNullValue()));
        assertThatFactoryBuiltApiCanMakeApiCalls(apiFactory);
    }

    private static void assertThatFactoryBuiltApiCanMakeApiCalls(ApiFactory apiFactory) throws ApiException {
        EventTypesApi eventTypesApi = apiFactory.build(EventTypesApi.class);
        ResourceListOfEventTypeSchema listOfEventTypeSchema = eventTypesApi.listEventTypes();
        assertThat("Event type API created by factory should return event types"
                , listOfEventTypeSchema, is(notNullValue()));
        assertThat("Event types contents types returned by the event types API should not be empty",
                listOfEventTypeSchema.getValues(), not(empty()));
    }

}
