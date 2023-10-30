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
            System.out.print(System.getProperty("user.dir") + " >");
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
        if (args.size() != 1) {
            System.out.println("Error this command take 1 argument \nNote: path with spaces must be in \" \" ");
        } else {
            Path path ;
            String fullpath = parser.getArgs().get(0) ;
            try {
                if((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length()-1) == '"')){
                    fullpath = parser.getArgs().get(0).substring(1,parser.getArgs().get(0).length()-1) ;
                }
                path = Paths.get(fullpath);
                if(Files.exists(path)){
                    BufferedReader reader = Files.newBufferedReader(path);
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
                }
                else{
                    System.out.println("Error: Invalid file path ");
                }
            }
            catch (InvalidPathException e) {
                System.err.println("Error: Invalid file path ");
            }
            catch (IOException e) {
                System.err.println("Error: " + e.getMessage() + " (file not found)");
            }
        }
    }
    
    public void touch(Vector<String> args) {
        if (args.size() != 1) {
            System.out.println("Error this command take 1 argument \nNote: path with spaces must be in \" \" ");
        } 
        else{
            String fullpath = parser.getArgs().get(0) ;
            if((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length()-1) == '"')){
                fullpath = parser.getArgs().get(0).substring(1,parser.getArgs().get(0).length()-1) ;
            }
            try {
                Path path = Paths.get(fullpath);
                if (!Files.exists(path)) {
                        Files.createFile(path);
                        System.out.println("File created: " + path.getFileName());
                }
                else{
                    if (Files.isDirectory(path)) {
                        System.err.println("Error: Cannot create a file in a directory without file name.");
                    } else {
                        System.out.println("Warning: File already exists: " + path.getFileName());
                    }
                }
            }
            catch (InvalidPathException e) {
                System.err.println("Error: Invalid file path ");
            }
            catch (IOException e) {
                System.err.println("Error: Invalid file path ");
            }
        }
    }

    public void cat(Vector<String> args) {
        if (args.isEmpty() || args.size() > 2) {
            System.out.println("Error this command take 1 argument \nNote: path with spaces must be in \" \" ");
        }
        else{
            try{
                if (args.size() == 1) {
                    String fullpath = args.get(0);
                    if((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length()-1) == '"')){
                        fullpath = parser.getArgs().get(0).substring(1,parser.getArgs().get(0).length()-1) ;
                    }
                    
                    Path path = Paths.get(fullpath);
                    if (!Files.exists(path)) {
                        System.err.println("Error: Invalid file path ");
                    }
                    else{   
                        try {
                            File file = new File(fullpath);
                            Scanner scanner = new Scanner(file);
                            while (scanner.hasNextLine()) {
                                System.out.println(scanner.nextLine());
                            }
                            scanner.close();
                        } catch (FileNotFoundException e) {
                            System.out.println("File not found: " + fullpath);
                        }
                    }
                } 
                else if (args.size() == 2) {
                    String fullpath = args.get(0);
                    String fullpath2 = args.get(1);

                    if((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length()-1) == '"')){
                        fullpath = parser.getArgs().get(0).substring(1,parser.getArgs().get(0).length()-1) ;
                    }
                    if((parser.getArgs().get(1).charAt(0) == '"') || (parser.getArgs().get(1).charAt(parser.getArgs().get(1).length()-1) == '"')){
                        fullpath2 = parser.getArgs().get(0).substring(1,parser.getArgs().get(0).length()-1) ;
                    }
                        
                    Path path = Paths.get(fullpath);
                    Path path2 = Paths.get(fullpath2);

                    if (!Files.exists(path) || !Files.exists(path2) ) {
                        System.err.println("Error: Invalid file path ");
                    }
                    else{
                        try {
                            File file1 = new File(fullpath);
                            Scanner scanner1 = new Scanner(file1);
                            File file2 = new File(fullpath2);
                            Scanner scanner2 = new Scanner(file2);

                            while (scanner1.hasNextLine() || scanner2.hasNextLine()) {
                                if (scanner1.hasNextLine()) {
                                    System.out.println(scanner1.nextLine());
                                }
                                if (scanner2.hasNextLine()) {
                                    System.out.println(scanner2.nextLine());
                                }
                            }
                            scanner1.close();
                            scanner2.close();
                        } catch (FileNotFoundException e) {
                            System.out.println("File not found: " + fullpath + " or " + fullpath2);
                        }
                    }
                }
            }
            catch (InvalidPathException e) {
                System.err.println("Error: Invalid file path ");
            }
        }
    }
    
}
