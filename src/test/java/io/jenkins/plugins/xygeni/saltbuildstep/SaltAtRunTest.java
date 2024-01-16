package io.jenkins.plugins.xygeni.saltbuildstep;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.xygeni.saltbuildstep.model.Item;
import io.jenkins.plugins.xygeni.saltbuildstep.model.OutputOptions;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

class SaltAtRunTest {

    @Test
    void runSaltRun(@TempDir File tempDir) throws IOException, InterruptedException {

        String expected = "salt at --never-fail run --pipeline=MyPipeline --basedir=" + tempDir.getPath()
                + " -o out.json --pretty-print --max-out=10 --max-err=9 --timeout=11 --step=my-step --name=item-name --type=product --digest=sha256:abcde -- ng build --env=prod";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);

        runAdd(
                10,
                9,
                "my-step",
                11,
                "ng build --env=prod",
                List.of(new Item("item-name", "product", null, "", "", "", "sha256:abcde")),
                tempDir,
                printStream);

        assertLogContains(expected, baos);
    }

    @Test
    void runSaltRunWithNullParams(@TempDir File tempDir) throws IOException, InterruptedException {

        String expected = "salt at --never-fail run --pipeline=MyPipeline --basedir=" + tempDir.getPath()
                + " -o out.json --pretty-print --step=my-step --name=item-name --type=product --digest=sha256:abcde -- ng build --env=prod";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);

        runAdd(
                null,
                null,
                "my-step",
                null,
                "ng build --env=prod",
                List.of(new Item("item-name", "product", null, "", "", "", "sha256:abcde")),
                tempDir,
                printStream);

        assertLogContains(expected, baos);
    }

    private void assertLogContains(String expected, ByteArrayOutputStream baos) {
        String output = baos.toString();
        Assertions.assertTrue(
                output.contains(expected), "Expected: \n" + expected + "\nnot found in: \n" + baos.toString());
    }

    private void runAdd(
            Integer maxout,
            Integer maxerr,
            String step,
            Integer timeout,
            String commandline,
            List<Item> items,
            File tempDir,
            PrintStream printStream)
            throws IOException, InterruptedException {

        Run mockRun = Mockito.mock(Run.class);
        Mockito.when(mockRun.getResult()).thenReturn(Result.SUCCESS);
        Mockito.when(mockRun.getRootDir()).thenReturn(tempDir);
        Mockito.when(mockRun.getFullDisplayName()).thenReturn("MyPipeline");

        TaskListener mockTList = Mockito.mock(TaskListener.class);
        Mockito.when(mockTList.getLogger()).thenReturn(printStream);

        EnvVars mockEVars = Mockito.mock(EnvVars.class);
        Mockito.when(mockEVars.expand(Mockito.anyString())).thenReturn("");

        FilePath mockWorkspace = Mockito.mock(FilePath.class);
        Mockito.when(mockWorkspace.list(Mockito.anyString())).thenReturn(new FilePath[] {new FilePath(tempDir)});

        SaltAtRunStepBuilder p = new SaltAtRunStepBuilder(new OutputOptions("out.json", true, null));
        p.setItems(items);
        p.setMaxout(maxout);
        p.setStep(step);
        p.setMaxerr(maxerr);
        p.setTimeout(timeout);
        p.setCommand(commandline);
        p.perform(mockRun, mockWorkspace, mockEVars, new Launcher.DummyLauncher(mockTList), mockTList);
    }
}
