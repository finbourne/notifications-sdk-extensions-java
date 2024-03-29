package com.finbourne.notifications.extensions;

import com.finbourne.notifications.ApiClient;
import com.finbourne.notifications.api.EventTypesApi;
import com.finbourne.notifications.api.NotificationsApi;
import com.finbourne.notifications.api.EventsApi;
import com.finbourne.notifications.api.DeliveriesApi;
import com.finbourne.notifications.api.ApplicationMetadataApi;
import com.finbourne.notifications.api.SubscriptionsApi;
import com.finbourne.notifications.model.Notification;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ApiFactoryTest {

    private ApiFactory apiFactory;
    private ApiClient apiClient;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp(){
        apiClient = mock(ApiClient.class);
        apiFactory = new ApiFactory(apiClient);
    }

    // General Cases

    @Test
    public void build_ForEventsApi_ReturnEventsApi(){
        EventsApi eventsApi = apiFactory.build(EventsApi.class);
        assertThat(eventsApi, instanceOf(EventsApi.class));
    }

    @Test
    public void build_ForEventTypesApi_ReturnEventTypesApi(){
        EventTypesApi eventTypesApi = apiFactory.build(EventTypesApi.class);
        assertThat(eventTypesApi, instanceOf(EventTypesApi.class));
    }

    @Test
    public void build_ForNotificationsApi_ReturnNotificationsApi(){
        NotificationsApi notificationsApi = apiFactory.build(NotificationsApi.class);
        assertThat(notificationsApi, instanceOf(NotificationsApi.class));
    }

    @Test
    public void build_ForDeliveriesApi_ReturnDeliveriesApi(){
        DeliveriesApi deliveriesApi = apiFactory.build(DeliveriesApi.class);
        assertThat(deliveriesApi, instanceOf(DeliveriesApi.class));
    }

    @Test
    public void build_ForApplicationMetadataApi_ReturnApplicationMetadataApi(){
        ApplicationMetadataApi applicationMetadataApi = apiFactory.build(ApplicationMetadataApi.class);
        assertThat(applicationMetadataApi, instanceOf(ApplicationMetadataApi.class));
    }

    @Test
    public void build_ForASubscriptionsApi_ReturnSubscriptionsApi(){
        SubscriptionsApi subscriptionsApi = apiFactory.build(SubscriptionsApi.class);
        assertThat(subscriptionsApi, instanceOf(SubscriptionsApi.class));
    }

    @Test
    public void build_ForAnyApi_SetsTheApiFactoryClientAndNotTheDefault(){
        EventsApi eventsApi = apiFactory.build(EventsApi.class);
        assertThat(eventsApi.getApiClient(), equalTo(apiClient));
    }

    // Singleton Check Cases

    @Test
    public void build_ForSameApiBuiltAgainWithSameFactory_ReturnTheSameSingletonInstanceOfApi(){
        EventsApi eventsApi = apiFactory.build(EventsApi.class);
        EventsApi eventsApiSecond = apiFactory.build(EventsApi.class);
        assertThat(eventsApi, sameInstance(eventsApiSecond));
    }

    @Test
    public void build_ForSameApiBuiltWithDifferentFactories_ReturnAUniqueInstanceOfApi(){
        EventsApi eventsApi = apiFactory.build(EventsApi.class);
        EventsApi eventsApiSecond = new ApiFactory(mock(ApiClient.class)).build(EventsApi.class);
        assertThat(eventsApi, not(sameInstance(eventsApiSecond)));
    }

    // Error Cases

    @Test
    public void build_ForNonApiPackageClass_ShouldThrowException(){
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("com.finbourne.notifications.model.Notification class is not a supported API class. " +
                "Supported API classes live in the " + ApiFactory.API_PACKAGE + " package.");
        apiFactory.build(Notification.class);
    }



}
