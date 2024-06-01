package com.jingmin.blog.frontmatter.yaml.util;

import org.apache.commons.cli.*;

import java.util.Arrays;

public class CommandLineParser {

    /**
     * 命令行参数解析(commons-cli)
     * {@link <a href="https://commons.apache.org/proper/commons-cli/usage.html">}
     * {@link <a href="https://linux.cn/article-13699-1.html">}
     * {@link <a href="https://www.baeldung.com/apache-commons-cli">}
     */
    public static CommandLine parseArgs(String[] args) {
        //命令行参数解析
        Options options = new Options();
        Option k = Option.builder().option("k").argName("key").longOpt("key").hasArg(true).desc("Specify the key of the yaml front matter.").required().build();
        options.addOption(k);

        Option v = Option.builder().option("v").argName("value_i").longOpt("value").hasArg(true).desc("Set the value of the given key. The option could be set multi times for array values of given key.").required(false).build();
        options.addOption(v);

        options.addOption(new Option("h", "help", false, "print this message"));
        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();
        // create the parser
        org.apache.commons.cli.CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            // parse the command line arguments
            commandLine = parser.parse(options, args);

            if(commandLine.hasOption("h")) {
                formatter.printHelp("app <OPTION> <file-path>", options);
            }
        }
        catch (ParseException exp) {
            // oops, something went wrong
            //exp.printStackTrace();
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            formatter.printHelp("app", options);
        }
        String[] remainedArgs = commandLine.getArgs();
        if(remainedArgs == null || remainedArgs.length != 1) {
            formatter.printHelp("app [OPTION] <file-path>", options);
        }
        return commandLine;
    }


    public static void printDebugInfo(CommandLine commandLine) {
        for(Option option: commandLine.getOptions()) {
            System.out.println(option.getKey() + ": " + option.getValue());
        }
        System.out.println(commandLine.getArgList());
        System.out.println(Arrays.toString(commandLine.getArgs()));
    }
}
