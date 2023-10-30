/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package command.line.interpreter;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Scanner;
import java.util.Vector;
/**
 *
 * @author Medo
 */
public class Terminal {
    private Parser parser = new Parser();
    
    
    public void open(){
        Scanner input = new Scanner(System.in);
        while(true){
            parser.parse(input.nextLine());
            this.chooseCommandAction(parser);
            parser.clearArgs();
        }
    }
    
    public void chooseCommandAction(Parser parser){
        if(parser.getCommandName().equals("echo")){
            this.echo(parser.getArgs());
        }
        else if(parser.getCommandName().equals("cd")){
            this.cd(parser.getArgs());
        }
        else if(parser.getCommandName().equals("pwd")){
            this.pwd();
        }
        else if(parser.getCommandName().equals("ls") || parser.getCommandName().equals("ls -r")){
            this.ls();
        }
        else if(parser.getCommandName().equals("wc")){
            this.wc(parser.getArgs());
        }
        else if(parser.getCommandName().equals("touch")){
            this.touch(parser.getArgs());
        }
        else if(parser.getCommandName().equals("exit")){
            System.exit(0);
        }
    }
    
    public void echo(Vector<String> args){
        if(args.isEmpty()){
            System.out.println("Erorr this command takes arguments ");
        }
        else{
            System.out.println(parser.getFullCommand().replace("echo ",""));
        }
    }
    
    public void pwd(){
        if(!parser.getArgs().isEmpty()){
            System.out.println("Error this command takes no arguments ");
        }
        else{
            System.out.println(System.getProperty("user.dir"));
        }
    }
    
    // this fun = ls and ls -r
    public void ls(){
        if(!parser.getArgs().isEmpty()){
            System.out.println("Error this command takes no arguments ");
        }
        else{
            File currentDirectory = new File(System.getProperty("user.dir"));
            Vector<File> filesList = new Vector<File>();
            for (File i : currentDirectory.listFiles()) {
                filesList.add(i);
            }
            
            filesList.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
            
            if(parser.getCommandName().equals("ls -r")){
                Collections.reverse(filesList);
            }
            
            for (File i : filesList) {
                System.out.println(i.getName());
            }
        }
    }
    
    public void cd(Vector<String> args){
        if(args.isEmpty()){
            System.setProperty("user.dir", System.getProperty("user.home"));
        }
        else if(args.size() == 1 && (args.get(0).equals("..") || (args.get(0).equals("\"..\"")))){
            File previousDirectory = new File(System.getProperty("user.dir")).getParentFile();
            System.setProperty("user.dir", previousDirectory.getAbsolutePath());
        }
        else if(args.size() == 1){
            Path path ;
            String fullpath = parser.getArgs().get(0) ;
            try {
                if((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length()-1) == '"')){
                    fullpath = parser.getArgs().get(0).substring(1,parser.getArgs().get(0).length()-1) ;
                }
                path = Paths.get(fullpath);
                if(Files.exists(path)){
                    System.setProperty("user.dir", fullpath);
                }
                else{
                    System.out.println("Error: Invalid file path ");
                }
            }catch (InvalidPathException e) {
                System.err.println("Error: Invalid file path ");
            }
        }
        else{
            System.out.println("Error this command thke 0 or 1 argument \nNote: path with spases must be in \" \" ");
        }
    }
    
    public void wc(Vector<String> args) {
        if (args.isEmpty()) {
            System.out.println("Error: Missing file path.");
        } else {
            String filePath = parser.getFullCommand().replace("wc ", "");

            boolean flag = false ;
            Path path ;
            for(String i : new File(System.getProperty("user.dir")).list()){
                if(filePath.equals(i)){
                    path = Paths.get(System.getProperty("user.dir"), filePath);
                    flag = true ;
                    break ;
                }
            }
            
            path = Paths.get(filePath);
            if(flag == false){
                if (Files.exists(path)) {
                    path = Paths.get(filePath);
                } 
                else{
                    System.out.println("Error wrong path");
                    return ;
                }
            }

            try (BufferedReader reader = Files.newBufferedReader(path)) {
                int lineCount = 0;
                int wordCount = 0;
                int charCount = 0;

                String line;
                while ((line = reader.readLine()) != null) {
                    lineCount++;
                    charCount += line.length();
                    String[] words = line.split("\\s+"); // Split by whitespace
                    wordCount += words.length;
                }

                System.out.println(lineCount + " " + wordCount + " " + charCount + " " + path.getFileName());
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage() + " (file not found)");
            }
        }
    }
    
    public void touch(Vector<String> args) {
        if (args.isEmpty()) {
            System.out.println("Error: Missing file path.");
        } else {
            String filePath = parser.getFullCommand().replace("touch ", "");

            Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                try {
                    Files.createFile(path);
                    System.out.println("File created: " + path.getFileName());
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            } else {
                if (Files.isDirectory(path)) {
                    System.err.println("Error: Cannot create a file in a directory.");
                } else {
                    System.out.println("Warning: File already exists: " + path.getFileName());
                }
            }
        }
    }
    
}
