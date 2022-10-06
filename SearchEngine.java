import java.util.ArrayList;
import java.io.IOException;
import java.net.URI;

class Handler implements URLHandler {
    // The one bit of state on the server: a number that will be manipulated by
    // various requests.
    ArrayList<String> strings = new ArrayList<>();

    public String handleRequest(URI url) {
        String output = "";
        if (url.getPath().equals("/")) {
            output = String.format("Syntax: /add?s=<string_to_add>\n/search?s=<string_to_search>\n");
        } else if (url.getPath().contains("/add")) {
            String[] parameters = url.getQuery().split("=");
            if(parameters[0].equals("s")){
              strings.add(parameters[1]);
            }
            output = String.format("Added: %s\n", parameters[1]);
        } else if(url.getPath().contains("/search")){
            String[] parameters = url.getQuery().split("=");
            if(parameters[0].equals("s")){
              for(String s : strings){
                if(s.contains(parameters[1])){
                  output += s + "\n";
                }
              }
            }
        } else{
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
