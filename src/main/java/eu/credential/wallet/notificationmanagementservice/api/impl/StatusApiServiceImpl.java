package eu.credential.wallet.notificationmanagementservice.api.impl;

import eu.credential.wallet.notificationmanagementservice.api.*;
import eu.credential.wallet.notificationmanagementservice.model.*;

import eu.credential.wallet.notificationmanagementservice.model.Error;
import eu.credential.wallet.notificationmanagementservice.model.StatusResponse;

import java.util.List;
import eu.credential.wallet.notificationmanagementservice.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-04-27T14:47:45.402+02:00")
public class StatusApiServiceImpl extends StatusApiService {
    @Override
    public Response statusGet(SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
    }
}
