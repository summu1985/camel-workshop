package com.redhat.demo;

import org.apache.camel.builder.RouteBuilder;

public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // from("timer:foo?period=1000")
        // .setBody(simple("Welcome,to,Camel,Workshop"))
        // .split(body().tokenize(",")).parallelProcessing()
        // .to("log:split");

        /* snippet 2 */
        // from("file:inbox")
        // .split().tokenizeXML("order").streaming()
        // .log("${body}");

        /* Snippet 3 */
        // from("file:inbox")
        // .split().tokenizeXML("order").parallelProcessing()
        // .choice()
        // .when(body().contains("Sachin"))
        // .to("direct:sachin")
        // .when(body().contains("Dhoni"))
        // .to("direct:dhoni")
        // .otherwise()
        // .to("direct:unknown");

        // from("direct:sachin")
        // .log("Sachin....Sachin");

        // from("direct:dhoni")
        // .log("Thala for a reason....");

        // from("direct:unknown")
        // .log("Who are you ??");

        /* Snippet 4 */
        from("file:inbox")
        .split().tokenizeXML("order").parallelProcessing()
        .choice()
        .when(body().contains("Sachin"))
        .to("direct:sachin")
        .when(body().contains("Dhoni"))
        .to("direct:dhoni")
        .otherwise()
        .to("direct:unknown");

        from("direct:sachin")
        .process(exchange->{
            String orderString = exchange.getIn().getBody(String.class);
            String outputString = orderString.toUpperCase();
            exchange.getIn().setBody(outputString, String.class);
        })
        .log("Sachin....Sachin: ${body}");

        from("direct:dhoni")
        .process(exchange->{
            String orderString = exchange.getIn().getBody(String.class);
            String outputString = orderString.toUpperCase();
            exchange.getIn().setBody(outputString, String.class);
        })
        .log("Thala for a reason : ${body}");
        
        from("direct:unknown")
        .process(exchange->{
            String orderString = exchange.getIn().getBody(String.class);
            String outputString = orderString.toUpperCase();
            exchange.getIn().setBody(outputString, String.class);
        })
        .log("Who are you ?? : ${body}");
        

    }
    
}
