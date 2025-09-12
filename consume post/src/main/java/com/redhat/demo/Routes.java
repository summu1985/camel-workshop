package com.redhat.demo;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        restConfiguration().scheme("http").bindingMode(RestBindingMode.json);

        rest("/send-sms")
                .post()

                .to("direct:sendSMS");

        from("direct:sendSMS")
                .log("Incoming to camel = ${body}")
                .convertBodyTo(String.class)
                //.to("log:DEBUG?showBody=true&showHeaders=true")
                .removeHeaders("*")
                // .process(exchange->{
                // input customerInput = exchange.getIn().getBody(input.class);
                // // exchange.getIn().setHeader("client_id", customerInput.getClientid());
                // // exchange.getIn().setHeader("client_secret",
                // customerInput.getClientsecret());
                // })
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setHeader(Exchange.HTTP_PATH, constant("/msg/v1/send-sms"))
                // ensure that you use correct client if and client secret
                // .setBody(simple("grant_type=client_credentials&client_id=${header.client_id}&client_secret=${header.client_secret}"))

                // Change the below url to your keycloak token endpoint
                // show the body
                .to("{{SEND_SMS_ENDPOINT}}?bridgeEndpoint=true")
                .convertBodyTo(String.class)
                .unmarshal()
                .json()
                .log("Recieved from SMS endpoint = ${body}")
                //.to("log:DEBUG?showBody=true&showHeaders=true")
                // .process(exchange->{
                // byte[] ssoResponseBytes = (byte[]) exchange.getIn().getBody();
                // String ssoResponseBytesToStrings = new String(ssoResponseBytes,
                // StandardCharsets.UTF_8);
                // System.out.println("sso response : " + ssoResponseBytesToStrings);
                // exchange.getIn().setBody(ssoResponseBytesToStrings, String.class);
                // })
                // .unmarshal()
                // .json(JsonLibrary.Jackson, SsoResponse.class)
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"));

    }

}
