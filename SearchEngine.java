import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.net.URI;

class Handler implements URLHandler {
    // The one bit of state on the server: a number that will be manipulated by
    // various requests.
    // Error message
    final String syntax = String.format("Syntax: /add?s=<string_to_add>&folder=<folder_to_put>&file=<file_to_put>\n" +
                                        "\tValid folder types are: 1, 2, 3\n" +
                                        "\tValid file types are: 1, 2\n" +
                                        "\t/search?s=<string_to_search>\n");

    private static void getFiles(File start, ArrayList<String> out, String target) {
      if(!start.exists()){
        System.out.println("File: " + start.getPath() + " does not exist");
      }
      if(start.isDirectory()){
        File[] paths = start.listFiles();
        for(File subFile: paths){
          File test = new File(subFile.getPath());
          getFiles(test, out, target);
        }
      }
      else{
        try(Scanner reader = new Scanner(start);){
          while(reader.hasNextLine()){
            if(reader.nextLine().contains(target)){
              out.add(start.getPath());
              break;
            }
          }
        } catch(Exception e){
          System.out.println("Error: " + e.getMessage());
        } 
      }
    }

    public String handleRequest(URI url) {
        String output = ""; // output string
        // Handle call to root
        if (url.getPath().equals("/")) {
            return syntax;
        } 
        // Handle call to add
        else if (url.getPath().contains("add")) {
            String[] parameters = url.getQuery().split("&");  // get all queries
            String key = "";
            String folder = "";
            String file = "";
            if(parameters.length != 3){
              return syntax;
            }
            if(parameters[0].charAt(0) == 's'){
              key = parameters[0].substring(parameters[0].indexOf("=") + 1);
            } else{
              return syntax;
            }
            String call = parameters[1].substring(0, parameters[1].indexOf("="));
            String param = parameters[1].substring(parameters[1].indexOf("=") + 1);
            if(call.equals("folder")){
              switch(param.charAt(0)){
                case '1':
                  folder = "Add1";
                  break;
                case '2':
                  folder = "Add1/Add2";
                  break;
                case '3':
                  folder = "Add1/Add2/Add3";
                  break;
                default:
                  return syntax;
              }
            }
            call = parameters[2].substring(0, parameters[2].indexOf("="));
            param = parameters[2].substring(parameters[2].indexOf("=") + 1);
            if(call.equals("file")){
              if(param.charAt(0) < '1' || param.charAt(0) > '2'){
                return syntax;
              }
              file = "file" + param;
            } else{
              return syntax;
            }
            File out = new File(folder + "/" + file);
            try{
              Files.write(out.toPath(), key.getBytes());
            } catch(Exception e){
              return String.format("Error: %s", e.getMessage());
            }
            output = String.format("Added: %s to %s\n\n", key, out.getPath());
        } 
        // Handle call to search 
        else if(url.getPath().contains("search")){
            String[] parameters = url.getQuery().split("=");  // get all queries
              if(parameters[0].equals("s")){
                output = String.format("Searching for \"%s\":\n", parameters[1]);
                ArrayList<String> results = new ArrayList<>();
                getFiles(new File("."), results, parameters[1]);
                output += String.format("Found in %d files:\n", results.size());
                for(String s : results){
                  output += s + ", ";
                }
                output = output.substring(0, output.length() - 2);
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
