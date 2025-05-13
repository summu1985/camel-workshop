package com.redhat.demo;

import org.apache.camel.builder.RouteBuilder;

public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:foo?period=2000")
        .log("Welcome to Camel Workshop");
    }
    
}
