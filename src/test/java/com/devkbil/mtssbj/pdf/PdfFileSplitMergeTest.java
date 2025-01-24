package com.devkbil.mtssbj.pdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.devkbil.mtssbj.common.util.PdfUtil.merge;
import static com.devkbil.mtssbj.common.util.PdfUtil.separate;

public class PdfFileSplitMergeTest {
    public static void main(String[] args) throws IOException {
        Path resourceDirectory = Paths.get("src", "test", "resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "/";

        // pdf split
        separate(new File(absolutePath + "Demo_merge2.pdf"),1);
        // pdf merge
        File file1 = new File(absolutePath + "Demo1.pdf");
        File file2 = new File(absolutePath + "Demo2.pdf");
        List<File> files = List.of(file1,file2);
        merge(files,absolutePath + "Demo_merge.pdf");
        file1 = new File(absolutePath + "Demo1.pdf");
        file2 = new File(absolutePath + "Demo_merge.pdf");
        files = List.of(file1,file2);
        merge(files,absolutePath + "Demo_merge2.pdf");
    }
}
