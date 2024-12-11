package com.acme;

import java.time.Duration;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.client.api.WebClientApplicationException;

import com.acme.api.VehiclesApi;

import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    private static final Logger LOG = Logger.getLogger(GreetingResource.class);

    // Api Extension for adding Request Filter Provider => Log duplicate authorization header values
    @RegisterRestClient(baseUri="", configKey="reproducer_json")
    @org.eclipse.microprofile.rest.client.annotation.RegisterProvider(com.acme.api.auth.CompositeAuthenticationProvider.class)
    @org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders(com.acme.api.auth.AuthenticationPropagationHeadersFactory.class)
    @org.eclipse.microprofile.rest.client.annotation.RegisterProvider(com.acme.ReproducerClientRequestFilterProvider.class)
    public interface VehiclesApiPlus extends VehiclesApi {
    
    }

    @Inject
    @RestClient
    // VehiclesApi api;
    VehiclesApiPlus api;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> hello() {
        Uni<String> ret = api.listVehicles(null, null, null, null, null, null, null)
        .onItem().transform( it -> {
            return "Got vehicles list";
        })
        .onFailure( ClientWebApplicationException.class ).invoke( th -> { 
            // ClientWebApplicationException cWebEx = (ClientWebApplicationException) th;
            // if ( cWebEx.getResponse().getStatus() == 401 ) {
            //     LOG.warnf("First Error : Unauthorized : => Correct");
            // }
            // else {
            //     LOG.warnf( "Following Error : Bad Request %d => due to Auth Header", cWebEx.getResponse().getStatus() );
            // }
        })
        .onFailure().retry().withBackOff(Duration.ofMillis(200)).atMost(4)
        .onFailure().recoverWithItem( th -> {
            return "All went wrong as expected for reproducer !";
        });
        
        return ret;
    }
}
