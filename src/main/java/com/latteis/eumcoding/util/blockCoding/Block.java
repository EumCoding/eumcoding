package com.latteis.eumcoding.util.blockCoding;

public class Block {
    private String block;
    private String value;

    public Block(String block, String value) {
        this.block = block;
        this.value = value;
    }

    public String getBlock() {
        return block;
    }

    public String getValue() {
        return value;
    }
}
