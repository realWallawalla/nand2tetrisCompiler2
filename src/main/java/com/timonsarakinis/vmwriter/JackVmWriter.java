package com.timonsarakinis.vmwriter;

import com.timonsarakinis.engine.ArithmaticType;
import com.timonsarakinis.utils.IOUtils;

public class JackVmWriter implements VmWriter {
    private String fileName;

    public JackVmWriter(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void writePush(String segment, int value) {
        writeTofile(("push " + segment.toLowerCase() + " " + value + System.lineSeparator()).getBytes());
    }

    @Override
    public void writePop(String segment, int value) {
        writeTofile(("pop " + segment.toLowerCase() + " " + value + System.lineSeparator()).getBytes());
    }

    @Override
    public void writeArithmatic(ArithmaticType arithmetic) {
        writeTofile((arithmetic.getVmValue() + System.lineSeparator()).getBytes());
    }

    @Override
    public void writeLabel(String label) {
        writeTofile(("label " + label + System.lineSeparator()).getBytes());
    }

    @Override
    public void writeGoto(String label) {
        writeTofile(("goto " + label + System.lineSeparator()).getBytes());
    }

    @Override
    public void writeIf(String label) {
        writeTofile(("if-goto " + label + System.lineSeparator()).getBytes());
    }

    @Override
    public void writeCall(String name, int nArgs) {
        writeTofile(("call " + name + " " + nArgs + System.lineSeparator()).getBytes());
    }

    @Override
    public void writeFunction(String name, int nLocals) {
        writeTofile(("function " + name + " " + nLocals + System.lineSeparator()).getBytes());
    }

    @Override
    public void writeReturn() {
        writeTofile(("return" + System.lineSeparator()).getBytes());
    }

    private void writeTofile(byte[] command) {
        IOUtils.writeToFile(command, fileName);
    }
}
