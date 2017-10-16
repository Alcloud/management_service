package eu.credential.wallet.notificationmanagementservice.api.factories;

import eu.credential.wallet.notificationmanagementservice.api.StatusApiService;
import eu.credential.wallet.notificationmanagementservice.api.impl.StatusApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-04-27T14:47:45.402+02:00")
public class StatusApiServiceFactory {
    private final static StatusApiService service = new StatusApiServiceImpl();

    public static StatusApiService getStatusApi() {
        return service;
    }
}
