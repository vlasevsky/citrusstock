package com.citrusmall.citrusstock.util.codegen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeGeneratorFactory {

    @Autowired
    private QRCodeGenerator qrCodeGenerator;



    public CodeGenerator getGenerator(String type) {
        if ("QR".equalsIgnoreCase(type)) {
            return qrCodeGenerator;
        }  else {
            throw new IllegalArgumentException("Unknown code generator type: " + type);
        }
    }
}
