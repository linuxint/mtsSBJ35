package com.devkbil.mtssbj;

import java.io.File;

import org.eclipse.jgit.api.Git;

import com.devkbil.mtssbj.common.util.JgitUtil;

public class JgitUtilTest {
    public static void main(String[] args) throws Exception {
        File dir = new File("D:/test/tmp");
        JgitUtil.checkOut(dir);
        Git git = JgitUtil.open(dir);
        JgitUtil.remoteAdd(git);
        JgitUtil.pull(git);
        JgitUtil.add(git, "404.md");
        JgitUtil.rm(git, "ReadMe.md");
        JgitUtil.commit(git, "8888");
        JgitUtil.push(git);
    }
}
