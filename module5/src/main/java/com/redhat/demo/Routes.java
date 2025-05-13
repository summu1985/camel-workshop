package com.redhat.demo;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Exchanger;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;

public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
 
        restConfiguration().scheme("http");
        
        rest("/lead")
        .post()
        .to("direct:postLeadToKafka");
        

        from("direct:postLeadToKafka")
        .wireTap("kafka:{{kafka.topic.name}}")
        .setBody(constant("Sent input request to Kafka. Expect response shortly."));

        // from("direct:postLead")
        // .setHeader(Exchange.CONTENT_TYPE, simple("application/json"))
        // .setHeader(Exchange.HTTP_METHOD, constant("POST"))
        // .setHeader(Exchange.HTTP_PATH, constant("/lead"))
        // .to("{{LEAD_BACKEND_ADDRESS}}?bridgeEndpoint=true")
       
        // convert the body back to XML
        // .to("xj:json2xml-lead.xsl?transformDirection=JSON2XML")
        // .setHeader(Exchange.CONTENT_TYPE, constant("application/xml"));

        // Kafka consumer
        from("kafka:{{kafka.topic.name}}")
        .log("Received : \"${body}\"");

    }
    
}
