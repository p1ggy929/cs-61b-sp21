package gitlet;

import java.util.Scanner;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 * Supports both command-line mode and interactive mode.
 * 
 * Usage: 
 *   Command-line: java gitlet.Main <command> [args...]
 *   Interactive:  java gitlet.Main
 *
 * @author Piggy Zhao
 */
public class Main {

    private static boolean interactiveMode = false;

    /**
     * Main entry point for Gitlet.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            // No command provided - output error and exit
            Methods.setInteractiveMode(false);
            executeCommand(args, false);
        } else {
            // Execute single command (original behavior)
            Methods.setInteractiveMode(false);
            executeCommand(args, false);
        }
    }

    /**
     * Runs the interactive mode where users can enter commands repeatedly.
     */
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("========================================");
        System.out.println("Gitlet Interactive Mode");
        System.out.println("========================================");
        System.out.println("Type 'help' for available commands");
        System.out.println("Type 'quit' or 'exit' to exit");
        System.out.println();

        while (true) {
            System.out.print("gitlet> ");
            
            if (!scanner.hasNextLine()) {
                // EOF (Ctrl+D or Ctrl+Z)
                System.out.println("\nGoodbye!");
                break;
            }
            
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }

            // Handle exit commands
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            // Handle help command
            if (input.equalsIgnoreCase("help")) {
                printHelp();
                System.out.println();
                continue;
            }

            // Parse and execute command
            String[] commandArgs = parseCommand(input);
            if (commandArgs.length > 0) {
                executeCommand(commandArgs, true);
            }
            System.out.println();
        }
        
        scanner.close();
    }

    /**
     * Parses a command string into arguments, handling quoted strings.
     * Example: commit "my message" -> ["commit", "my message"]
     */
    private static String[] parseCommand(String input) {
        java.util.List<String> args = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = '"';

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if ((c == '"' || c == '\'') && !inQuotes) {
                inQuotes = true;
                quoteChar = c;
            } else if (c == quoteChar && inQuotes) {
                inQuotes = false;
                quoteChar = '"';
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (current.length() > 0) {
                    args.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            args.add(current.toString());
        }

        return args.toArray(new String[0]);
    }

    /**
     * Executes a command with the given arguments.
     * @param args Command arguments (first element is the command name)
     * @param continueOnError If true (interactive mode), catches errors and continues
     */
    private static void executeCommand(String[] args, boolean continueOnError) {
        try {
            if (args.length == 0) {
                if (!continueOnError) {
                    Methods.exit("Please enter a command.");
                    return;
                } else {
                    System.out.println("Please enter a command.");
                    return;
                }
            }

            String firstArg = args[0];
            
            // Execute the command
            switch (firstArg) {
                case "init" -> GitletUtils.init(args);
                case "add" -> GitletUtils.add(args);
                case "commit" -> GitletUtils.commit(args);
                case "rm" -> GitletUtils.remove(args);
                case "log" -> GitletUtils.log(args);
                case "global-log" -> GitletUtils.globalLog(args);
                case "find" -> GitletUtils.find(args);
                case "checkout" -> GitletUtils.checkout(args);
                case "status" -> GitletUtils.status(args);
                case "branch" -> GitletUtils.branch(args);
                case "rm-branch" -> GitletUtils.removeBranch(args);
                case "reset" -> GitletUtils.reset(args);
                case "merge" -> GitletUtils.merge(args);
                case "add-remote" -> GitletUtils.addRemote(args);
                case "rm-remote" -> GitletUtils.rmRemote(args);
                case "fetch" -> GitletUtils.fetch(args);
                case "push" -> GitletUtils.push(args);
                case "pull" -> GitletUtils.pull(args);
                default -> {
                    String errorMsg = "No command with that name exists.";
                    if (!continueOnError) {
                        Methods.exit(errorMsg);
                    } else {
                        System.out.println(errorMsg);
                        System.out.println("Type 'help' for available commands.");
                    }
                }
            }
            
        } catch (Methods.ExitException e) {
            // ExitException is thrown by Methods.exit() in interactive mode
            // The error message should have already been printed by Methods.exit()
            // In interactive mode, we just continue the loop
        } catch (GitletException e) {
            // GitletException contains error message
            String message = e.getMessage();
            if (message != null && !message.isEmpty()) {
                System.out.println(message);
            } else {
                System.out.println("An error occurred.");
            }
            
            if (!continueOnError) {
                System.exit(1);
            }
        } catch (Exception e) {
            // Handle unexpected exceptions
            System.out.println("Unexpected error: " + e.getMessage());
            if (!continueOnError) {
                e.printStackTrace();
                System.exit(1);
            } else {
                // In interactive mode, print stack trace for debugging but continue
                System.err.println("Stack trace:");
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints help information showing all available commands.
     */
    private static void printHelp() {
        System.out.println();
        System.out.println("Available Gitlet commands:");
        System.out.println();
        System.out.println("Repository Management:");
        System.out.println("  init                           Initialize a new Gitlet repository");
        System.out.println();
        System.out.println("File Operations:");
        System.out.println("  add <file>                     Add a file to staging area");
        System.out.println("  rm <file>                      Remove a file from staging");
        System.out.println("  commit \"<message>\"            Commit staged changes");
        System.out.println();
        System.out.println("Information:");
        System.out.println("  status                         Show working directory status");
        System.out.println("  log                            Display commit history");
        System.out.println("  global-log                     Display all commits");
        System.out.println("  find \"<message>\"              Find commits by message");
        System.out.println();
        System.out.println("Checkout:");
        System.out.println("  checkout -- <file>             Checkout a file from HEAD");
        System.out.println("  checkout <id> -- <file>        Checkout a file from a commit");
        System.out.println("  checkout <branch>              Checkout a branch");
        System.out.println();
        System.out.println("Branching:");
        System.out.println("  branch <name>                  Create a new branch");
        System.out.println("  rm-branch <name>               Remove a branch");
        System.out.println();
        System.out.println("Advanced:");
        System.out.println("  reset <commit-id>              Reset HEAD to a commit");
        System.out.println("  merge <branch>                 Merge a branch into current");
        System.out.println();
        System.out.println("Remote Operations:");
        System.out.println("  add-remote <name> <dir>        Add a remote repository");
        System.out.println("  rm-remote <name>               Remove a remote");
        System.out.println("  fetch <remote> <branch>        Fetch from remote");
        System.out.println("  push <remote> <branch>         Push to remote");
        System.out.println("  pull <remote> <branch>         Pull from remote");
        System.out.println();
        System.out.println("Other:");
        System.out.println("  help                           Show this help message");
        System.out.println("  quit / exit                    Exit interactive mode");
    }
}
