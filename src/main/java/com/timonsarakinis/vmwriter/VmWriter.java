package com.timonsarakinis.vmwriter;

public interface VmWriter {
    void writePush(VmSegmentType segment, int value);

    void writePop(VmSegmentType segment, int value);

    void writeArithmatic(ArithmeticType arithmetic);

    void writeLabel(String label);

    void writeGoto(String label);

    void writeIf(String label);

    void writeCall(String name, int nArgs);

    void writeFunction(String name, int nLocals);

    void writeReturn();
}
