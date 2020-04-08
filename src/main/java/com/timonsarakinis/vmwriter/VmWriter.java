package com.timonsarakinis.vmwriter;

import com.timonsarakinis.engine.ArithmaticType;

public interface VmWriter {
    void writePush(String segment, int value);

    void writePop(String segment, int value);

    void writeArithmatic(ArithmaticType arithmetic);

    void writeLabel(String label);

    void writeGoto(String label);

    void writeIf(String label);

    void writeCall(String name, int nArgs);

    void writeFunction(String name, int nLocals);

    void writeReturn();
}
