/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.oai.provider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.qucosa.oai.provider.application.config.SetConfigMapper;
import de.qucosa.oai.provider.persistence.PersistenceDaoInterface;
import org.glassfish.jersey.process.internal.RequestScoped;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Path("/sets")
@RequestScoped
public class SetController {
    private PersistenceDaoInterface setDao;

    @Inject
    public SetController(PersistenceDaoInterface setDao) {
        this.setDao = setDao;
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(String input) {

        if (input.isEmpty()) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Sets input data is empty.").build();
        }

        Set<de.qucosa.oai.provider.persistence.pojos.Set> saveRes;

        try {
            saveRes = buildSqlSets(input);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot build set objects.").build();
        }

        Set<de.qucosa.oai.provider.persistence.pojos.Set> result;

        try {
            result = setDao.create(saveRes);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(result).build();
    }

    @PUT
    @Path("{setspec}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("setspec") String setspec, String input) {
        ObjectMapper om = new ObjectMapper();
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets;

        try {
            sets = buildSqlSets(input);
        } catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot build set objects.").build();
        }

        for (de.qucosa.oai.provider.persistence.pojos.Set set : sets) {

            if (set.getSetSpec() == null || !set.getSetSpec().equals(setspec)) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Request param setspec and json data setspec are unequal.").build();
            }
        }

        try {
            setDao.update(sets);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).entity(true).build();
    }

    @DELETE
    @Path("{setspec}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("setspec") String setspec) {

        if (setspec == null || setspec.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The setspec param is null or empty.").build();
        }

        try {
            setDao.deleteByKeyValue("setspec", setspec);
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.OK).build();
    }
    
    private Set<de.qucosa.oai.provider.persistence.pojos.Set> buildSqlSets(String input) throws IOException {
        ObjectMapper om = new ObjectMapper();
        Set<de.qucosa.oai.provider.persistence.pojos.Set> sets = new HashSet<>();
        Set<SetConfigMapper.Set> json = om.readValue(input, om.getTypeFactory().constructCollectionType(Set.class, SetConfigMapper.Set.class));
        
        for (SetConfigMapper.Set set : json) {
            de.qucosa.oai.provider.persistence.pojos.Set data = new de.qucosa.oai.provider.persistence.pojos.Set();
            data.setSetSpec(set.getSetSpec());
            data.setSetName(set.getSetName());
            data.setSetDescription(set.getSetDescription());
            sets.add(data);
        }
        
        return sets;
    }
}
