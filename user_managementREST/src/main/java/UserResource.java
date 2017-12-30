import com.kumuluz.ee.logs.cdi.Log;
import com.kumuluz.ee.logs.cdi.LogParams;
import com.rso.streaming.ententies.User;
import com.rso.streaming.ententies.logic.UserBean;
import io.swagger.annotations.Api;
import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Api(value = "Users")
@RequestScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Log(LogParams.METRICS)
public class UserResource {

    @Inject
    private UserBean userBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    @Path("/{userId}")
    @Timed(name = "UsersGetTime")
    public Response getUser(@PathParam("userId") long userId) {
        User album = userBean.getUser(userId);

        if (album == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(album).build();
    }

    @GET
    @Path("/{username}/{userpass}")
    @Timed(name = "UsersGetTime")
    public Response getCheckUser(@PathParam("username") String username, @PathParam("userpass") String userpass) {
        User u = userBean.getUser(username, userpass);

        if (u == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(u).build();
    }

    @GET
    @Path("/healthResponseCheck")
    public Response getCheck() {
        return Response.status(Response.Status.OK).build();
    }

    @POST
    @Metered(name = "UserCreation")
    public Response createUser(User user) {

        if (user.getName().isEmpty() || user.getPassword().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            user = userBean.createUser(user);
        }

        if (user != null && user.getID() != null) {
            return Response.status(Response.Status.CREATED).entity(user).build();
        } else {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    @DELETE
    @Path("{userId}")
    @Metered(name = "UserDeletion")
    public Response deleteUser(@PathParam("userId") Long userId) {
        boolean deleted = userBean.deleteUser(userId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
