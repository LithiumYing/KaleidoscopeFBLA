package io.github.lithiumying.kioskclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.*;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

// import org.json.*;

/*
 * non object oriented
 */

public class GraphQLClient {

    // GraphQL Server URL
    private final static String url = "https://kaleidoscope-fbla.herokuapp.com/graphql/";

    /**
     * 
     * @param studentId A String student ID which finds the first and last name of a
     *                  student
     * @return A String first and last name concatenated with a space if student is
     *         found, otherwise returns "Not Found"
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public static String checkStudentId(String studentId)
            throws URISyntaxException, IOException, InterruptedException {
        String query = "query {" +
                "  studentById(studentId: " + studentId + ") {" +
                "    firstName" +
                "    lastName" +
                "  }" +
                "}";
        String jsonString = graphqlQuery(query);
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONObject student = json.getJSONObject("data").getJSONObject("studentById");
            String firstName = student.getString("firstName");
            String lastName = student.getString("lastName");

            return firstName + " " + lastName;
        } catch (JSONException e) {
            return "Not Found";
        }
    }

    /**
     * @param event   an ArrayList of Strings in which all event names are added to
     * @param eventId an ArrayList of String in which all available event IDS are
     *                added to
     */
    public static void getAllEvent(ArrayList<String> event, ArrayList<String> eventId)
            throws URISyntaxException, IOException, InterruptedException {
        String query = "query allEventId{ " +
                "  availableEvents {" +
                "name " +
                "  id" +
                "  }" +
                "}";
        String jsonString = graphqlQuery(query);
        JSONObject json = new JSONObject(jsonString);
        JSONArray eventArray = json.getJSONObject("data").getJSONArray("availableEvents");

        for (int i = 0; i < eventArray.length(); i++) {
            event.add(eventArray.getJSONObject(i).getString("name"));
            eventId.add(eventArray.getJSONObject(i).getString("id"));
        }
    }

    /**
     * Attempts to check in/out a student to an event
     * 
     * @param eventId   A String event ID that is used to mutate the event to either
     *                  check in or check out a student
     * @param studentId A String student ID that is used to check a student in or
     *                  out of an event
     * @return A boolean indicating whether or not the operation was successful.
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public static boolean updateStudentToEvent(String eventId, String studentId)
            throws URISyntaxException, IOException, InterruptedException {
        String mutation = "mutation {" +
                "  checkInOrOutStudentFromEvent(eventId: " + eventId + ", studentId: " + studentId + ") {" +
                "    student {" +
                "      firstName" +
                "      lastName" +
                "    }" +
                "    event {" +
                "      name" +
                "    }" +
                "  }" +
                "}";
        String jsonString = graphqlQuery(mutation);
        System.out.println(jsonString);
        try {
            JSONObject json = new JSONObject(jsonString);
            // just need to make sure that the json is valid, so we don't need anything
            // within the student json object
            JSONObject student = json.getJSONObject("data").getJSONObject("checkInOrOutStudentFromEvent");
            return true;
        } catch (JSONException e) {
            System.out.println("Could not sign up for event");
            return false;
        }

    }

    /**
     * @param query A String GraphQL Query/Mutation that is used to send an HTTP
     *              request to a
     *              GraphQL server
     * @return a JSON object as a String
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    private static String graphqlQuery(String query)
            throws URISyntaxException, IOException, InterruptedException {

        // String requestBody = "{\"query\":\"" + query + "\"}";
        // System.out.println(requestBody);

        String queryString = "{\"query\": \"" + query + "\"}";

        byte[] queryByte = queryString.getBytes(StandardCharsets.UTF_8);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Accept", "application/json")
                .header("Authorization",
                        "JWT eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6InNlMjYwOTVAc3R1ZGVudHMubWNwYXNkLmsxMi53aS51cyIsImV4cCI6MTY4MDcxODc5NCwib3JpZ0lhdCI6MTY4MDI4Njc5NH0.luMN5h1pAKcGpVRTUoJsAzrThfiCh0Kpr8630pvF7Zw")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(queryByte))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    // testing stuff ignore this
    public static void main(String[] args) {
        ArrayList<String> event = new ArrayList<String>();
        ArrayList<String> eventId = new ArrayList<String>();
        try {
            updateStudentToEvent("6", "26095");
            System.out.println(event);
            System.out.println(eventId);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}