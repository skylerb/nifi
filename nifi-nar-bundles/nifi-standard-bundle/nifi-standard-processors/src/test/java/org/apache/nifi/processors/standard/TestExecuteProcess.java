/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processors.standard.util.ArgumentUtils;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Ignore;
import org.junit.Test;

public class TestExecuteProcess {

    @Test
    public void testSplitArgs() {
        final List<String> nullArgs = ArgumentUtils.splitArgs(null, ' ');
        assertNotNull(nullArgs);
        assertTrue(nullArgs.isEmpty());

        final List<String> zeroArgs = ArgumentUtils.splitArgs("  ", ' ');
        assertNotNull(zeroArgs);
        assertTrue(zeroArgs.isEmpty());

        final List<String> singleArg = ArgumentUtils.splitArgs("    hello   ", ' ');
        assertEquals(1, singleArg.size());
        assertEquals("hello", singleArg.get(0));

        final List<String> twoArg = ArgumentUtils.splitArgs("   hello    good-bye   ", ' ');
        assertEquals(2, twoArg.size());
        assertEquals("hello", twoArg.get(0));
        assertEquals("good-bye", twoArg.get(1));

        final List<String> singleQuotedArg = ArgumentUtils.splitArgs("  \"hello\" ", ' ');
        assertEquals(1, singleQuotedArg.size());
        assertEquals("hello", singleQuotedArg.get(0));

        final List<String> twoQuotedArg = ArgumentUtils.splitArgs("   hello \"good   bye\"", ' ');
        assertEquals(2, twoQuotedArg.size());
        assertEquals("hello", twoQuotedArg.get(0));
        assertEquals("good   bye", twoQuotedArg.get(1));
    }

    @Ignore   // won't run under Windows
    @Test
    public void testEcho() {
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.nifi", "TRACE");

        final TestRunner runner = TestRunners.newTestRunner(ExecuteProcess.class);
        runner.setProperty(ExecuteProcess.COMMAND, "echo");
        runner.setProperty(ExecuteProcess.COMMAND_ARGUMENTS, "test-args");
        runner.setProperty(ExecuteProcess.BATCH_DURATION, "500 millis");

        runner.run();

        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(ExecuteProcess.REL_SUCCESS);
        for (final MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile);
            System.out.println(new String(flowFile.toByteArray()));
        }
    }

    // @Test
    public void testBigBinaryInputData() {
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.nifi", "TRACE");
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.nifi.processors.standard", "DEBUG");

        String workingDirName = "/var/test";
        String testFile = "eclipse-java-luna-SR2-win32.zip";

        final TestRunner runner = TestRunners.newTestRunner(ExecuteProcess.class);
        runner.setProperty(ExecuteProcess.COMMAND, "cmd");
        runner.setProperty(ExecuteProcess.COMMAND_ARGUMENTS, " /c type " + testFile);
        runner.setProperty(ExecuteProcess.WORKING_DIR, workingDirName);

        File inFile = new File(workingDirName, testFile);
        System.out.println(inFile.getAbsolutePath());

        runner.run();

        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(ExecuteProcess.REL_SUCCESS);
        long totalFlowFilesSize = 0;
        for (final MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile);
            totalFlowFilesSize += flowFile.getSize();
            // System.out.println(new String(flowFile.toByteArray()));
        }

        assertEquals(inFile.length(), totalFlowFilesSize);
    }

    @Test
    public void testBigInputSplit() {
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.nifi", "TRACE");
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.nifi.processors.standard", "DEBUG");

        String workingDirName = "/var/test";
        String testFile = "Novo_dicionário_da_língua_portuguesa_by_Cândido_de_Figueiredo.txt";
        // String testFile = "eclipse-java-luna-SR2-win32.zip";

        final TestRunner runner = TestRunners.newTestRunner(ExecuteProcess.class);
        runner.setProperty(ExecuteProcess.COMMAND, "cmd");
        runner.setProperty(ExecuteProcess.COMMAND_ARGUMENTS, " /c type " + testFile);
        runner.setProperty(ExecuteProcess.WORKING_DIR, workingDirName);
        runner.setProperty(ExecuteProcess.BATCH_DURATION, "150 millis");

        File inFile = new File(workingDirName, testFile);
        System.out.println(inFile.getAbsolutePath());

        // runner.run(1,false,true);

        ProcessContext processContext = runner.getProcessContext();

        ExecuteProcess processor = (ExecuteProcess) runner.getProcessor();
        processor.updateScheduledTrue();
        processor.setupExecutor(processContext);

        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());

        // runner.run(5,true,false);

        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(ExecuteProcess.REL_SUCCESS);
        long totalFlowFilesSize = 0;
        for (final MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile);
            totalFlowFilesSize += flowFile.getSize();
            // System.out.println(new String(flowFile.toByteArray()));
        }

        // assertEquals(inFile.length(), totalFlowFilesSize);
    }
}
