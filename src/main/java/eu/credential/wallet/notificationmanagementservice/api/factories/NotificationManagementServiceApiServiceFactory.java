package eu.credential.wallet.notificationmanagementservice.api.factories;

import eu.credential.wallet.notificationmanagementservice.api.NotificationManagementServiceApiService;
import eu.credential.wallet.notificationmanagementservice.api.impl.NotificationManagementServiceApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-05-10T15:19:22.672+02:00")
public class NotificationManagementServiceApiServiceFactory {
    private final static NotificationManagementServiceApiService service = new NotificationManagementServiceApiServiceImpl();

    public static NotificationManagementServiceApiService getNotificationManagementServiceApi() {
        return service;
    }
}
