package rest;

import rest.controllers.PersonController;
import rest.exceptions.PersistanceException;
import rest.model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/peopleManager")
@Produces({"application/json", "application/xml"})
public class PeopleManager {

    private static List<Person> listPeople = new ArrayList<>();

    static{


        listPeople.add(PersonController.getInstance().CreatePerson("Corentin", "Houdayer", "Coco", 21));
        listPeople.add(PersonController.getInstance().CreatePerson("Corentin", "Houdayer", "Cocorico", 21));
        listPeople.add(PersonController.getInstance().CreatePerson("Test", "Test", "Cocorico", 23));
        try {
            saveStaticDataInDb();
        } catch (PersistanceException e) {
            e.printStackTrace();
        }

    }

    @GET
    @Path("people")
    public List<Person> getAllPeople() throws PersistanceException {
        System.out.println(listPeople);
        System.out.println(listPeople.size());
        List<Person> listPeople2 = Person.findAll();
        System.out.println("LIST ALL PEOPLE FROM DB : " + listPeople2.size());
        return listPeople2;
    }

    @GET
    @Path("people/{id}")
    public Response getPersonById(@PathParam("id")int personId) throws PersistanceException {
        System.out.println("Trying to get person " + personId);
        Response rp = null;
        Person p = Person.findById(personId);
        while(p == null){
            System.out.println("null");
        }
        rp = Response.status(200).entity(p).build();



        return rp;
    }

    @PUT
    @Path("update")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updatePerson(Person person){
        for (Person p:listPeople) {
            if(p.getId() == person.getId()){
                PersonController.getInstance().UpdatePerson(p);
            }
        }
    }

    @POST
    @Path("post")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPerson(Person person) throws PersistanceException {
        String result = "Record retrieved " + person;
        System.out.println(person + " created");
        EntityManager entityManager = EntityManager.getInstance();
        Person newPerson = new Person(person.getFirstname(),person.getLastname(),person.getSurname(),21);
        entityManager.persist(newPerson);
        entityManager.dispose();
        return Response.status(201).entity(result).build();
    }

    @DELETE
    @Path("delete/{id}")
    public void deletePerson(@PathParam("id") int personId) throws PersistanceException {
        System.out.println("Person with id " + personId + " has been deleted.");
        listPeople.remove(personId);
        Person p = Person.findById(personId);
        EntityManager entityManager = EntityManager.getInstance();
        entityManager.remove(p);
    }

    public static void saveStaticDataInDb() throws PersistanceException {
        for (Person p:listPeople) {
            EntityManager entityManager = EntityManager.getInstance();
            entityManager.persist(p);
            entityManager.dispose();
        }
    }



}
