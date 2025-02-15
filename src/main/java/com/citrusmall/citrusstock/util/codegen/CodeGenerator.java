package com.citrusmall.citrusstock.util.codegen;

import java.awt.image.BufferedImage;

public interface CodeGenerator {

    BufferedImage generateCodeImage(String content, int width, int height) throws Exception;
}
