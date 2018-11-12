package com.sasha.osudiscord;

import com.github.francesco149.koohii.Koohii;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.UUID;

public class PpCompute {

    private boolean calcFc;
    private String downloadurl;
    private String filename;
    private Koohii.Map beatmap;

    private double aim;
    private double speed;

    public PpCompute(String downloadUrl) {
        if (!downloadUrl.toLowerCase().startsWith("https://osu.ppy.sh/osu/")) {
            throw new IllegalArgumentException("Not a valid osu beatmap download!");
        }
        this.calcFc = true;
        this.downloadurl = downloadUrl;
        this.filename = UUID.randomUUID().toString().replace("-", "");
    }

    public PpCompute(String downloadUrl, double aim, double speed) {
        this.calcFc = false;
        if (!downloadUrl.toLowerCase().startsWith("https://osu.ppy.sh/osu/")) {
            throw new IllegalArgumentException("Not a valid osu beatmap download!");
        }
        this.downloadurl = downloadUrl;
        this.filename = UUID.randomUUID().toString().replace("-", "");
    }

    public double calc() throws IOException {
        System.out.println("Downloading beatmap...");
        FileUtils.copyURLToFile(new URL(downloadurl), new File(filename));
        System.out.println("Downloaded beatmap...");
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        beatmap = new Koohii.Parser().map(reader);
        Koohii.DiffCalc stars = new Koohii.DiffCalc().calc(beatmap);
        System.out.println(stars.total + " star difficulty!");
        Koohii.PPv2 pp = new Koohii.PPv2(calcFc ? stars.aim : this.aim, calcFc ? stars.speed : this.speed, beatmap);
        return pp.total;
    }

    public Koohii.Map getMap() {
        return beatmap;
    }

}
