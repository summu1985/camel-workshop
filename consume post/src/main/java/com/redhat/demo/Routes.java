package com.redhat.demo;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import org.apache.camel.support.jsse.*;

public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Defining SSL parameters for the https downstream
        

        restConfiguration().scheme("http").bindingMode(RestBindingMode.json);

        rest("/token")
                .post()

                .to("direct:sendSMS");

        from("direct:sendSMS")
                //.streamCaching()
                .log("Incoming to camel = ${body}")
                //.convertBodyTo(String.class)
                .marshal().json()
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("log:DEBUG?showBody=true&showHeaders=true")
                .toD("{{SEND_SMS_ENDPOINT}}?bridgeEndpoint=true&throwExceptionOnFailure=false&sslContextParameters=#sslContextParameters")
                //.log("Recieved from SMS endpoint = ${body}")
                .to("log:DEBUG?showBody=true&showHeaders=true")
                .convertBodyTo(String.class)
                .unmarshal()
                .json()
                .log("Recieved from SMS endpoint = ${body}")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

    }

}
