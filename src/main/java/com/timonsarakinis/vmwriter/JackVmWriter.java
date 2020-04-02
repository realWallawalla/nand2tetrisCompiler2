package com.timonsarakinis.vmwriter;

import com.timonsarakinis.utils.IOUtils;

public class JackVmWriter implements VmWriter {
    private String fileName;

    public JackVmWriter(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void writePush(VmSegmentType segment, int value) {
        writeTofile(("push " + segment.toString().toLowerCase() + " " + value).getBytes());
    }

    @Override
    public void writePop(VmSegmentType segment, int value) {
        writeTofile(("pop " + segment.toString().toLowerCase() + " " + value).getBytes());
    }

    @Override
    public void writeArithmatic(ArithmeticType arithmetic) {
        writeTofile(arithmetic.toString().toLowerCase().getBytes());
    }

    @Override
    public void writeLabel(String label) {
        writeTofile(("label " + label).getBytes());
    }

    @Override
    public void writeGoto(String label) {
        writeTofile(("goto " + label).getBytes());
    }

    @Override
    public void writeIf(String label) {
        writeTofile(("if goto " + label).getBytes());
    }

    @Override
    public void writeCall(String name, int nArgs) {
        writeTofile(("call " + name + " " + nArgs).getBytes());
    }

    @Override
    public void writeFunction(String name, int nLocals) {
        writeTofile(("function " + name + " " + nLocals).getBytes());
    }

    @Override
    public void writeReturn() {
        writeTofile(("return").getBytes());
    }

    private void writeTofile(byte[] command) {
        IOUtils.writeToFile(command, fileName);
    }
}
