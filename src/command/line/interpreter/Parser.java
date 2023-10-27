/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package command.line.interpreter;

import java.util.*;

public class Parser {
   
    private String commandName ;
    private Vector<String> args = new Vector<String>() ;
   
    public boolean parse(String input){
        if(input.isEmpty()){
             return false;
        }
        else{
             for(String i : input.split("\\s+")){
                 args.add(i);
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
             return true ;
        }
    }

    public String getCommandName() {
        return commandName;
    }

    public Vector<String> getArgs() {
        return args;
    }
    
}


