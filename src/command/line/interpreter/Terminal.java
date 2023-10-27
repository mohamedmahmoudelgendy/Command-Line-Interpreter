/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package command.line.interpreter;
import java.io.File;
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
            System.out.println(System.getProperty("user.dir"));
        }
        else if(args.size() == 1 && args.get(0).equals("..")){
            File previousDirectory = new File(System.getProperty("user.dir")).getParentFile();
            System.setProperty("user.dir", previousDirectory.getAbsolutePath());
        }
        else{
            String fullpath = parser.getFullCommand().replace("cd ","");
            boolean flag = false ;
            
            for(String i : new File(System.getProperty("user.dir")).list()){
                if(fullpath.equals(i)){
                    System.setProperty("user.dir",new File(fullpath).getAbsolutePath());
                    flag = true ;
                    break ;
                }
            }
            
            Path path = Paths.get(fullpath);
            if(flag == false){
                if(Files.exists(path)){
                    System.setProperty("user.dir", fullpath);
                }
                else{
                    System.out.println("Error wrong path"); 
                }
            }
        }
    }
    
}
