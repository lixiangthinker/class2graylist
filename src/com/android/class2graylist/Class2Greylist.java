/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.class2graylist;

import org.apache.commons.cli.*;

import java.io.IOException;

/**
 * Build time tool for extracting a list of members from jar files that have the @UsedByApps
 * annotation, for building the greylist.
 */
public class Class2Greylist {

    private static final String ANNOTATION_TYPE = "Landroid/annotation/UnsupportedAppUsage;";

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(Option.builder("d").
                longOpt("debug").
                hasArg(false).
                desc("Enable debug").
                build());

        options.addOption(Option.builder("h").
                longOpt("help").
                hasArg(false).
                desc("Show this help").
                build());
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            help(options);
            return;
        }
        if (cmd.hasOption('h')) {
            help(options);
        }

        String[] jarFiles = cmd.getArgs();
        if (jarFiles.length == 0) {
            System.err.println("Error: no jar files specified.");
            help(options);
        }

        Status status = new Status(cmd.hasOption('d'));

        for (String jarFile : jarFiles) {
            status.debug("Processing jar file %s", jarFile);
            try {
                JarReader reader = new JarReader(status, jarFile);
                reader.stream().forEach(clazz -> new AnnotationVisitor(
                        clazz, ANNOTATION_TYPE, status).visit());
                reader.close();
            } catch (IOException e) {
                status.error(e);
            }
        }
        if (status.ok()) {
            System.exit(0);
        } else {
            System.exit(1);
        }

    }

    private static void help(Options options) {
        new HelpFormatter().printHelp(
                "class2greylist path/to/classes.jar [classes2.jar ...]",
                "Extracts greylist entries from classes jar files given",
                options, null, true);
        System.exit(1);
    }
}
