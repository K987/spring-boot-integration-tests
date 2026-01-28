package com.examples.application;

import com.github.tomakehurst.wiremock.client.WireMock;

public final class WiremockHelper {

    private static final class WireMockClientHolder {
        private static final WireMock wireMockClient = WireMock.create()
                .host(TestContainerConfiguration.wiremockServer.getHost())
                .port(TestContainerConfiguration.wiremockServer.getPort())
                .build();
    }

    public static WireMock getWireMockClient() {
        return WireMockClientHolder.wireMockClient;
    }

    private WiremockHelper() {}
}
