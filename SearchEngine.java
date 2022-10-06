import java.util.ArrayList;
import java.io.IOException;
import java.net.URI;

class Handler implements URLHandler {
    // The one bit of state on the server: a number that will be manipulated by
    // various requests.
    ArrayList<String> strings = new ArrayList<>(); // Array list of added strings
    // Error message
    final String syntax = String.format("Syntax: /add?s=<string_to_add>\n" +
                                        "\t/search?s=<string_to_search>\n");
    /*
     * getStrings
     *   Returns all strings in the list
     * @return String
     * @param void
     */
    private String getStrings(){
      String output = "";
      for(String s : strings){
        output += "\"" + s + "\", ";
      }
      output += "\n";
      return output;
    }

    public String handleRequest(URI url) {
        String output = ""; // output string
        // Handle call to root
        if (url.getPath().equals("/")) {
            output = syntax;
        } 
        // Handle call to add
        else if (url.getPath().contains("add")) {
            String[] parameters = url.getQuery().split("=");  // get all queries
            if(parameters[0].equals("s")){
              strings.add(parameters[1]);
            }
            output = String.format("Added: %s\nStrings are now: %s\n", parameters[1], this.getStrings());
        } 
        // Handle call to search 
        else if(url.getPath().contains("search")){
            String[] parameters = url.getQuery().split("=");  // get all queries
            if(parameters[0].equals("s")){
              output = String.format("Searching for %s:\n", parameters[1]);
              String param = parameters[1].substring(1, parameters[1].length() - 1); // ignore quotes
              for(String s : strings){
                if(s.contains(param)){
                  output += s + "\n";
                }
              }
            }
        }
        // Invalid url
        else{
            System.out.println("Path: " + url.getPath());
            output = "404 Not Found!";
        }
        return output;
    }
}

class SearchEngine {
    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.out.println("Missing port number! Try any number between 1024 to 49151");
            return;
        }

        int port = Integer.parseInt(args[0]);
        Server.start(port, new Handler());
    }
}
