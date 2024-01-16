package io.jenkins.plugins.xygeni.saltcommand;

import hudson.model.Run;
import hudson.util.ArgumentListBuilder;
import io.jenkins.plugins.xygeni.saltbuildstep.model.Item;
import java.util.Arrays;
import java.util.List;

public class XygeniSaltAtRunCommandBuilder extends XygeniSaltAtCommandBuilder {

    private static final String INIT_COMMAND = "run";

    private Integer maxout;
    private String step;
    private Integer maxerr;
    private Integer timeout;
    private final List<Item> items;

    private String commandline;

    public String getStep() {
        return step;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public String getCommandLine() {
        return this.commandline;
    }

    public XygeniSaltAtRunCommandBuilder(
            Integer maxout, String step, Integer maxerr, Integer timeout, List<Item> items, String commandline) {
        this.maxout = maxout;
        this.step = step;
        this.maxerr = maxerr;
        this.timeout = timeout;
        this.items = items;
        this.commandline = commandline;
    }

    @Override
    protected String getCommand() {
        return INIT_COMMAND;
    }

    @Override
    protected void addCommandArgs(ArgumentListBuilder args, Run<?, ?> build) {

        if (maxout != null) {
            args.add("--max-out=" + maxout);
        }
        if (maxerr != null) {
            args.add("--max-err=" + maxerr);
        }
        if (timeout != null) {
            args.add("--timeout=" + timeout);
        }
        args.add("--step=" + getStep());

        for (Item item : items) {
            args.add("--name=" + item.getName());
            if (item.getType() != null) {
                args.add("--type=" + item.getType());
            }
            if (item.isValue()) {
                args.add("--value=" + item.getValue());
            } else if (item.isFile()) {
                args.add("--file=" + item.getFile());
            } else if (item.isDigest()) {
                args.add("--digest=" + item.getDigest());
            } else {
                args.add("--image=" + item.getImage());
            }
        }
        if (commandline != null) {
            String[] csplit = commandline.split(" ");
            args.add("--");
            args.add(Arrays.asList(csplit));
        }
    }
}
