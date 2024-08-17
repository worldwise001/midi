package sh.shh.midi;

import sh.shh.midi.roland.BlobExtractor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            BlobExtractor.extractAll("lib/test.jar", "out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}