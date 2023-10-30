/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package command.line.interpreter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private String fullCommand;
    private String commandName ;
    private Vector<String> args = new Vector<String>() ;
   
    public boolean parse(String input){
        if(input.isEmpty()){
             return false;
        }
        else{
            fullCommand = input ;
            Pattern pattern = Pattern.compile("\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)?\"|(?:\\S+)");
            Matcher matcher = pattern.matcher(input);

            while (matcher.find()) {
                args.add(matcher.group());
            }

            if(args.size() == 1){
                commandName = args.get(0) ;
                args.remove(0);
            }
            else{
                if(args.get(1).equals("-r")){
                    commandName = args.get(0) + " " + args.get(1) ;
                    args.remove(0);
                    args.remove(0);
                }
                else{
                    commandName = args.get(0);
                    args.remove(0);
                }
            }
            commandName.toLowerCase();
            return true ;
        }
    }

    public String getFullCommand() {
        return fullCommand;
    }
    
    public String getCommandName() {
        return commandName;
    }

    public Vector<String> getArgs() {
        return args;
    }
    
    public void clearFullCommand(){
        fullCommand = "" ;
    }
    
    public void clearCommandName(){
        commandName = "" ;
    }
    
    public void clearArgs(){
        args.clear();
    }
    
}
