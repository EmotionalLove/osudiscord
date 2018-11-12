package com.sasha.osudiscord;

import com.github.francesco149.koohii.Koohii;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public class PpCompute {

    private boolean calcFcSpeed;
    private boolean calcFcAim;
    private String downloadurl;
    private String filename;
    private Koohii.Map beatmap;

    private double aim;
    private double speed;

    public PpCompute(String downloadUrl) {
        if (!downloadUrl.toLowerCase().startsWith("https://osu.ppy.sh/osu/")) {
            throw new IllegalArgumentException("Not a valid osu beatmap download!");
        }
        this.calcFcSpeed = true;
        this.calcFcAim = true;
        this.downloadurl = downloadUrl;
        this.filename = UUID.randomUUID().toString().replace("-", "");
    }

    public PpCompute(String id, double aim, double speed) {
        String downloadUrl = "https://osu.ppy.sh/osu/" + id;
        this.calcFcSpeed = false;
        this.calcFcAim = false;
        if (!downloadUrl.toLowerCase().startsWith("https://osu.ppy.sh/osu/")) {
            throw new IllegalArgumentException("Not a valid osu beatmap download!");
        }
        if (aim != -1.0) {
            this.aim = aim;
        }else {
            this.calcFcAim = true;
        }
        if (speed != -1.0) {
            this.speed = aim;
        }else {
            this.calcFcSpeed = true;
        }
        this.speed = speed;
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
        Koohii.PPv2 pp = new Koohii.PPv2(calcFcAim ? stars.aim : this.aim, calcFcSpeed ? stars.speed : this.speed, beatmap);
        return pp.total;
    }

    public Koohii.Map getMap() {
        return beatmap;
    }

}
