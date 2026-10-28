package org.example.saga.pedido;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.example.saga.PedidoSaga;

import java.net.URI;
import java.util.UUID;

@Path("/pedidos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PedidoResource {

    @Inject
    PedidoService pedidoService;

    @Inject
    PedidoSaga pedidoSaga;

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") UUID id) {
        return pedidoService.buscarPorId(id)
                .map(pedido -> Response.ok(pedido).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(@Valid CriarPedidoRequest request,
                          @Context UriInfo uriInfo) {
        Pedido pedido = pedidoService.criar(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(pedido.getId().toString())
                .build();

        return Response.created(location).entity(pedido).build();
    }

    @POST
    @Path("/{id}/processar")
    public Response processar(@PathParam("id") UUID pedidoId) {
        return pedidoSaga.processar(pedidoId)
                .map(pedido -> Response.ok(pedido).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }
}
