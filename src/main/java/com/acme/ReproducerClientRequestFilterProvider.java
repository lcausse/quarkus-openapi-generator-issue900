package com.acme;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.logging.Logger;

import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.HttpHeaders;

// Filter to reinit request in case of retry => Prevent CodeGen Bug
// Alternative : Run first (priority 0) => before all other filters
// and clean header
// Log headers
@jakarta.annotation.Priority(Priorities.USER)
public class ReproducerClientRequestFilterProvider implements jakarta.ws.rs.client.ClientRequestFilter {

    private static final Logger LOG = Logger.getLogger(GreetingResource.class);

    @java.lang.Override
    public void filter(jakarta.ws.rs.client.ClientRequestContext context) throws java.io.IOException {
        // Alternative with Priority.0
        // context.getHeaders().replace(HttpHeaders.AUTHORIZATION, Collections.emptyList());

        computeDuplicates(context.getHeaders().get(HttpHeaders.AUTHORIZATION)).entrySet().forEach( entry -> {
            LOG.warnf("Authorization Header Value : ...%s... (%d)", entry.getKey(), entry.getValue() );
        });
    };

    public static Map<Object,Integer> computeDuplicates( List<Object> list ) {
        Map<Object, Integer> map = new HashMap<Object, Integer>();
        list.forEach(it ->{
            Integer freq = map.get( it );
            if ( freq == null ) {
                map.put(it, Integer.valueOf(  1 ));
            }
            else {
                map.put(it, Integer.valueOf( freq + 1 ));
            }
        });
        return map;
    }
}
