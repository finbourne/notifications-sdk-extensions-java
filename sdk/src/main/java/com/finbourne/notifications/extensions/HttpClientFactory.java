package com.finbourne.notifications.extensions;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
* Builds http client to communicate to notifications API instances.
*
*/
public class HttpClientFactory {

    /**
    *  Builds a {@link OkHttpClient} from a {@link ApiConfiguration} to make
    *  calls to the notifications API.
    *
    * @param apiConfiguration configuration to connect to notifications API
    * @return an client for http calls to notifications API
    */
    public OkHttpClient build(ApiConfiguration apiConfiguration) {
        return this.build(apiConfiguration, 10, 10);
    }

    /**
     * Builds a {@link OkHttpClient} from a {@link ApiConfiguration} to make
     * calls to the notifications API.
     *
     * @param apiConfiguration configuration to connect to notifications API
     * @param readTimeout read timeout in seconds
     * @param writeTimeout write timeout in seconds
     * @return n client for http calls to notifications API
     */
    public OkHttpClient build(ApiConfiguration apiConfiguration, int readTimeout, int writeTimeout){
        final OkHttpClient httpClient;

        //  use a proxy if given
        if (apiConfiguration.getProxyAddress() != null) {

            InetSocketAddress proxy = new InetSocketAddress(apiConfiguration.getProxyAddress(), apiConfiguration.getProxyPort());

            httpClient = new OkHttpClient.Builder()
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .proxy(new Proxy(Proxy.Type.HTTP, proxy))
                .proxyAuthenticator((route, response) -> {
                    String credential = Credentials.basic(apiConfiguration.getProxyUsername(), apiConfiguration.getProxyPassword());
                    return response.request().newBuilder()
                            .header("Proxy-Authorization", credential)
                            .build();
            })
            .build();
        }
        else {
            httpClient = new OkHttpClient.Builder()
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .build();
        }
        return httpClient;
    }

}
