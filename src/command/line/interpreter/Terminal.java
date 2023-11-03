/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package command.line.interpreter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
    private Vector<String> commands = new Vector<>();

    public void open() throws IOException {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.print(System.getProperty("user.dir") + " >");
            parser.parse(input.nextLine());
            this.chooseCommandAction(parser);
            parser.clearArgs();
        }
    }

    public void chooseCommandAction(Parser parser) throws IOException {
        if (parser.getCommandName().equals("echo")) {
            this.echo(parser.getArgs());
        } else if (parser.getCommandName().equals("cd")) {
            this.cd(parser.getArgs());
        } else if (parser.getCommandName().equals("pwd")) {
            this.pwd();
        } else if (parser.getCommandName().equals("ls") || parser.getCommandName().equals("ls -r")) {
            this.ls();
        } else if (parser.getCommandName().equals("wc")) {
            this.wc(parser.getArgs());
        } else if (parser.getCommandName().equals("touch")) {
            this.touch(parser.getArgs());
        } else if (parser.getCommandName().equals("cp")) {
            this.cp(parser.getArgs());
        } else if (parser.getCommandName().equals("cat")) {
            this.cat(parser.getArgs());
        } else if (parser.getCommandName().equals("history")) {
            this.history();
        }

        // store the command into vector for History usage
        commands.add(parser.getFullCommand());


        if (parser.getCommandName().equals("exit")) {
            System.exit(0);
        }

    }

    public void echo(Vector<String> args) {
        if (args.isEmpty()) {
            System.out.println("Erorr this command takes arguments ");
        } else {
            System.out.println(parser.getFullCommand().replace("echo ", ""));
        }
    }

    public void pwd() {
        if (!parser.getArgs().isEmpty()) {
            System.out.println("Error this command takes no arguments ");
        } else {
            System.out.println(System.getProperty("user.dir"));
        }
    }

    // this fun = ls and ls -r
    public void ls() {
        if (!parser.getArgs().isEmpty()) {
            System.out.println("Error this command takes no arguments ");
        } else {
            File currentDirectory = new File(System.getProperty("user.dir"));
            Vector<File> filesList = new Vector<File>();
            for (File i : currentDirectory.listFiles()) {
                filesList.add(i);
            }

            filesList.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));

            if (parser.getCommandName().equals("ls -r")) {
                Collections.reverse(filesList);
            }

            for (File i : filesList) {
                System.out.println(i.getName());
            }
        }
    }

    public void cd(Vector<String> args) {
        if (args.isEmpty()) {
            System.setProperty("user.dir", System.getProperty("user.home"));
        } else if (args.size() == 1 && (args.get(0).equals("..") || (args.get(0).equals("\"..\"")))) {
            File previousDirectory = new File(System.getProperty("user.dir")).getParentFile();
            System.setProperty("user.dir", previousDirectory.getAbsolutePath());
        } else if (args.size() == 1) {
            Path path;
            String fullpath = parser.getArgs().get(0);
            try {
                if ((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length() - 1) == '"')) {
                    fullpath = parser.getArgs().get(0).substring(1, parser.getArgs().get(0).length() - 1);
                }
                path = Paths.get(fullpath);
                if (Files.exists(path)) {
                    System.setProperty("user.dir", fullpath);
                } else {
                    System.out.println("Error: Invalid file path ");
                }
            } catch (InvalidPathException e) {
                System.err.println("Error: Invalid file path ");
            }
        } else {
            System.out.println("Error this command thke 0 or 1 argument \nNote: path with spases must be in \" \" ");
        }
    }

    public void wc(Vector<String> args) {
        if (args.size() != 1) {
            System.out.println("Error this command take 1 argument \nNote: path with spaces must be in \" \" ");
        } else {
            Path path;
            String fullpath = parser.getArgs().get(0);
            try {
                if ((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length() - 1) == '"')) {
                    fullpath = parser.getArgs().get(0).substring(1, parser.getArgs().get(0).length() - 1);
                }
                path = Paths.get(fullpath);
                if (Files.exists(path)) {
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
                } else {
                    System.out.println("Error: Invalid file path ");
                }
            } catch (InvalidPathException e) {
                System.err.println("Error: Invalid file path ");
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage() + " (file not found)");
            }
        }
    }

    public void touch(Vector<String> args) {
        if (args.size() != 1) {
            System.out.println("Error this command take 1 argument \nNote: path with spaces must be in \" \" ");
        } else {
            String fullpath = parser.getArgs().get(0);
            if ((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length() - 1) == '"')) {
                fullpath = parser.getArgs().get(0).substring(1, parser.getArgs().get(0).length() - 1);
            }
            try {
                Path path = Paths.get(fullpath);
                if (!Files.exists(path)) {
                    Files.createFile(path);
                    System.out.println("File created: " + path.getFileName());
                } else {
                    if (Files.isDirectory(path)) {
                        System.err.println("Error: Cannot create a file in a directory without file name.");
                    } else {
                        System.out.println("Warning: File already exists: " + path.getFileName());
                    }
                }
            } catch (InvalidPathException e) {
                System.err.println("Error: Invalid file path ");
            } catch (IOException e) {
                System.err.println("Error: Invalid file path ");
            }
        }
    }

    public void cat(Vector<String> args) {
        if (args.isEmpty() || args.size() > 2) {
            System.out.println("Error this command take 1 argument \nNote: path with spaces must be in \" \" ");
        } else {
            try {
                if (args.size() == 1) {
                    String fullpath = args.get(0);
                    if ((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length() - 1) == '"')) {
                        fullpath = parser.getArgs().get(0).substring(1, parser.getArgs().get(0).length() - 1);
                    }

                    Path path = Paths.get(fullpath);
                    if (!Files.exists(path)) {
                        System.err.println("Error: Invalid file path ");
                    } else {
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
                } else if (args.size() == 2) {
                    String fullpath = args.get(0);
                    String fullpath2 = args.get(1);

                    if ((parser.getArgs().get(0).charAt(0) == '"') || (parser.getArgs().get(0).charAt(parser.getArgs().get(0).length() - 1) == '"')) {
                        fullpath = parser.getArgs().get(0).substring(1, parser.getArgs().get(0).length() - 1);
                    }
                    if ((parser.getArgs().get(1).charAt(0) == '"') || (parser.getArgs().get(1).charAt(parser.getArgs().get(1).length() - 1) == '"')) {
                        fullpath2 = parser.getArgs().get(0).substring(1, parser.getArgs().get(0).length() - 1);
                    }

                    Path path = Paths.get(fullpath);
                    Path path2 = Paths.get(fullpath2);

                    if (!Files.exists(path) || !Files.exists(path2)) {
                        System.err.println("Error: Invalid file path ");
                    } else {
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
            } catch (InvalidPathException e) {
                System.err.println("Error: Invalid file path ");
            }
        }
    }


//    public void cp(Vector<String> args) throws IOException {
//        String firstFile, secFile;
//
//        // cp ----- cp a.txt -- both wrong
//        if (args.isEmpty() || args.size() == 1) {
//            System.out.println("Error: Missing file name(s). 2 file names are required");
//        } else {
//            // to remove the "cp" from the command and copy the file name
//            String fileName = parser.getFullCommand().replace("cp ", "");
//
//            // to handle if the first file even not created
//            firstFile = fileName.split(" ")[0];
//            Path file1 = Paths.get(firstFile);
//            if (!Files.exists(file1)) {
//                System.out.print("Error: File " + firstFile + " is not exists!\n");
//                return;
//            }
//
//            // cut the second file name from the whole command --- cp a.txt b.txt = b.txt
//            secFile = fileName.split(" ")[1];
//            Path file2 = Paths.get(secFile);
////            System.out.println("\n" + file2);
//
//            // checks if the second file exists if not creates one
//            if (!Files.exists(file2)) {
//                try {
//                    Files.createFile(file2);
//                    System.out.println("File created: " + file2.getFileName());
//
//                    // the problem that it is not in the current dir so its cannot see file
//
//                    // copy code here
//
//                    File myFile1 = new File(file1.getFileName().toString());
//                    File myFile2 = new File(file2.getFileName().toString());
//                    int e;
//
////                    BufferedReader reader = Files.newBufferedReader(file1);
//                    FileInputStream ins = new FileInputStream(myFile1);
//                    FileOutputStream out = new FileOutputStream(myFile2);
//
////                    String line;
////                    while((line = reader.readLine()) != null) {
//
////                    }
//                    // the problem is in this line
////                    FileInputStream in = new FileInputStream(myFile1);
////                    FileOutputStream out = new FileOutputStream(myFile2);
//
//                    try {
//                        // the problem is that its not enter the loop!!
//                        while ((e = ins.read()) != -1) {
//                            System.out.println("(char) i");
//                            out.write(e);
//                        }
//                    } catch (Exception s) {
//                        System.out.println("Error Found: " + s.getMessage());
//                    }
//                    System.out.println("\n after loop");
//
//
//                    ins.close();
//                    out.close();
//
//
//                    System.out.println("Copied successfully to: " + file2.getFileName());
//                } catch (IOException e) {
//                    System.err.println("Error: " + e.getMessage());
//                }
//            } else {
//                // copy code
////                    File myFile1 = new File(file1.getFileName().toString());
////                    File myFile2 = new File(file2.getFileName().toString());
//                int i;
//                String str = "";
////                    FileInputStream in = new FileInputStream(myFile1);
////                    FileOutputStream out = new FileOutputStream(myFile2);
//
//                FileReader in = new FileReader(firstFile);
//                FileWriter out = new FileWriter(secFile);
//
//                while ((i = in.read()) != -1) {
//                    str += (char) i;
//                }
//                System.out.println(str);
//
//                out.write(str);
//
//                in.close();
//                out.close();
//                System.out.println("Copied successfully to: " + file2.getFileName());
//
//            }
//
//        }
//
//
//    }

    public void cp(Vector<String> args) {
        String firstFile, secFile;


        // cp ----- cp a.txt -- both wrong
        if (args.isEmpty() || args.size() == 1) {
            System.out.println("Error: Missing file name(s). 2 file names are required");
        } else {
            // to remove the "cp" from the command and copy the file name
            String fileName = parser.getFullCommand().replace("cp ", "");

            // to handle if the first file even not created
            firstFile = fileName.split(" ")[0];
            Path file1 = Paths.get(firstFile);
            if (!Files.exists(file1)) {
                System.out.print("Error: File " + firstFile + " is not exists!\n");
                return;
            }

            // cut the second file name from the whole command --- cp a.txt b.txt = b.txt
            secFile = fileName.split(" ")[1];

            System.out.println(System.getProperty("user.dir"));
            try {

                // Create input and output streams
                FileInputStream inputStream = new FileInputStream(System.getProperty("user.dir") + "\\" + firstFile);
                FileOutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") + "\\" +secFile);


                int bytesRead;

                // Read data from the source file and copy it to the target file
                while ((bytesRead = inputStream.read()) != -1) {
                    outputStream.write(bytesRead);
                }

                // Close the streams
                inputStream.close();
                outputStream.close();

                System.out.println("File copied successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


        public void history () {

            // to check if it has no args
            if (parser.getFullCommand().length() != 7) {
                System.out.println("Error: No arguments needed in history command");
                return;
            }

            int i = 1;
            for (String str : commands) {
                System.out.println(i + "    " + str);
                i++;
            }
        }


    }


