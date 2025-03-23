package com.citrusmall.citrusstock.util.codegen;

public interface CodeGenerator {

    byte[] generateCodeImage(String content, int width, int height) throws Exception;
}
