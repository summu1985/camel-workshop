package com.redhat.ssl;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import jakarta.inject.Named;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.TrustManagersParameters;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.apache.camel.support.jsse.SSLContextParameters;

@Singleton
public class SslConfig {
    @ConfigProperty(name = "sms.endpoint.truststore.password")
    String trustStorePass;

    @Produces
    @Named("sslContextParameters")
    public SSLContextParameters sslContextParameters() {
        KeyStoreParameters trustStore = new KeyStoreParameters();
        // classpath resource (bundled in the app)
        trustStore.setResource("truststore.jks");
        trustStore.setPassword(trustStorePass);
        trustStore.setType("JKS");

        TrustManagersParameters tm = new TrustManagersParameters();
        tm.setKeyStore(trustStore);

        SSLContextParameters scp = new SSLContextParameters();
        scp.setTrustManagers(tm);
        return scp;
    }
}
