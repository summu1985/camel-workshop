package com.redhat.demo;

import java.nio.charset.StandardCharsets;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;

public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
 
        restConfiguration().scheme("http").bindingMode(RestBindingMode.json);
        
        rest("/lead")
        .get()
        .to("direct:getLead");

        from("direct:getLead")
        .setHeader(Exchange.CONTENT_TYPE, simple("application/json"))
        .setHeader(Exchange.HTTP_METHOD, constant("GET"))
        
        .to("{{LEAD_BACKEND_ADDRESS}}?bridgeEndpoint=true")
        //.convertBodyTo(String.class)
        .process(exchange->{
            byte[] leadBackendResponseBytes = (byte[]) exchange.getIn().getBody();
                String leadBackendResponseBytesToString = new String(leadBackendResponseBytes, StandardCharsets.UTF_8);
                System.out.println("backend response : " + leadBackendResponseBytesToString);
                exchange.getIn().setBody(leadBackendResponseBytesToString, String.class);
        })
        .unmarshal()
        .json(JsonLibrary.Jackson)
        .to("log:DEBUG?showBody=true&showHeaders=true")
        .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

        rest("/token")
            .post().type(input.class)
            .to("direct:getToken");

            from("direct:getToken")
            .removeHeaders("*")
            .process(exchange->{
                input customerInput = exchange.getIn().getBody(input.class);
                exchange.getIn().setHeader("client_id", customerInput.getClientid());
                exchange.getIn().setHeader("client_secret", customerInput.getClientsecret());
            })
            .setHeader(Exchange.HTTP_METHOD, constant("POST"))
            .setHeader(Exchange.CONTENT_TYPE, constant("application/x-www-form-urlencoded"))
            .setHeader(Exchange.HTTP_PATH, constant("/token"))
            // ensure that you use correct client if and client secret
            .setBody(simple("grant_type=client_credentials&client_id=${header.client_id}&client_secret=${header.client_secret}"))
        
            // Change the below url to your keycloak token endpoint
            // show the body
            .to("{{SSO_TOKEN_ENDPOINT}}?bridgeEndpoint=true")
            .to("log:DEBUG?showBody=true&showHeaders=true")
            .process(exchange->{
                byte[] ssoResponseBytes = (byte[]) exchange.getIn().getBody();
                    String ssoResponseBytesToStrings = new String(ssoResponseBytes, StandardCharsets.UTF_8);
                    System.out.println("sso response : " + ssoResponseBytesToStrings);
                    exchange.getIn().setBody(ssoResponseBytesToStrings, String.class);    
            })
            .unmarshal()
            .json(JsonLibrary.Jackson, SsoResponse.class)
            .to("log:DEBUG?showBody=true&showHeaders=true")
            .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

    }
    
}
