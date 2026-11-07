package org.openapitools.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.openapitools.configuration.ClientConfiguration;

@FeignClient(name="${api.name:api}", contextId="${api.contextId:${api.name}}", url="${api.url:http://dev-api.uadb.sn/v1}", configuration = ClientConfiguration.class)
public interface ApiApiClient extends ApiApi {
}
