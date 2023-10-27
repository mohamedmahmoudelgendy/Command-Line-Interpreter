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
    private String fullcommand;
    private Parser parser = new Parser();
    
    
    public void open(){
        Scanner input = new Scanner(System.in);
        fullcommand = input.nextLine();
        parser.parse(fullcommand);
//        this.pwd();
        this.cd(parser.getArgs());
//        this.echo(parser.getArgs());
//        this.ls();
//        this.pwd();
    }
    
    public void echo(Vector<String> args){
        if(args.isEmpty()){
            System.out.println("Erorr this command takes arguments ");
        }
        else{
            System.out.println(fullcommand.replace("echo ",""));
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
            System.out.println(System.getProperty("user.dir"));
        }
        else{
            String fullpath = fullcommand ;
            fullpath = fullpath.replace("cd ","");
            boolean flag = false ;
            
            for(String i : new File(System.getProperty("user.dir")).list()){
                if(fullpath.equals(i)){
                    System.setProperty("user.dir",new File(fullpath).getAbsolutePath());
                    System.out.println(System.getProperty("user.dir"));
                    flag = true ;
                    break ;
                }
            }
            
            Path path = Paths.get(fullpath);
            if(flag == false){
                if(Files.exists(path)){
                    System.setProperty("user.dir", fullpath);
                    System.out.println(System.getProperty("user.dir"));
                }
                else{
                    System.out.println("Error wrong path"); 
                }
            }
        }
    }
    
}
