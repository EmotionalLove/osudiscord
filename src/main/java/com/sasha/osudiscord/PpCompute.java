package com.sasha.osudiscord;

import com.github.francesco149.koohii.Koohii;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.UUID;

public class PpCompute {

    private boolean calcFcSpeed;
    private boolean calcFcAcc;
    private String downloadurl;
    private String filename;
    private Koohii.Map beatmap;

    private double acc;

    public PpCompute(String downloadUrl) {
        if (!downloadUrl.toLowerCase().startsWith("https://osu.ppy.sh/osu/")) {
            throw new IllegalArgumentException("Not a valid osu beatmap download!");
        }
        this.calcFcSpeed = true;
        this.calcFcAcc = true;
        this.downloadurl = downloadUrl;
        this.filename = UUID.randomUUID().toString().replace("-", "");
    }

    public PpCompute(String id, double acc) {
        String downloadUrl = "https://osu.ppy.sh/osu/" + id;
        this.calcFcSpeed = false;
        this.calcFcAcc = false;
        if (!downloadUrl.toLowerCase().startsWith("https://osu.ppy.sh/osu/")) {
            throw new IllegalArgumentException("Not a valid osu beatmap ID!");
        }
        if (acc != -1.0) {
            this.acc = acc;
        }else {
            this.calcFcAcc = true;
        }
        this.downloadurl = downloadUrl;
        this.filename = UUID.randomUUID().toString().replace("-", "");
    }

    public double calc() throws IOException {
        System.out.println("Downloading beatmap...");
        FileUtils.copyURLToFile(new URL(downloadurl), new File(filename));
        System.out.println("Downloaded beatmap...");
        BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
        // calc
        beatmap = new Koohii.Parser().map(reader); // the beatmap
        int objects = beatmap.ncircles + beatmap.nsliders + beatmap.nspinners; // the total objects in the beatmap
        Koohii.DiffCalc stars = new Koohii.DiffCalc().calc(beatmap); // the difficulty
        System.out.println(stars.total + " star difficulty!");
        Koohii.PPv2Parameters parameters = builder(beatmap, stars);
        parameters.nmiss = objects - ((int) (objects * (acc)));
        parameters.score_version = 1;
        System.out.println(parameters.nmiss + " missed out of " + objects + " possible");
        Koohii.PPv2 pp = new Koohii.PPv2(parameters); // the big UwU
        return dround(pp.total, 3);
    }
    public static double dround(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value + "");
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private Koohii.PPv2Parameters builder(Koohii.Map b, Koohii.DiffCalc stars) {
        Koohii.PPv2Parameters p = new Koohii.PPv2Parameters();
        p.n300 = b.ncircles + b.nsliders + b.nspinners;
        p.base_ar = b.ar;
        p.base_od = b.od;
        p.max_combo = b.max_combo();
        p.speed_stars = stars.speed;
        p.aim_stars = stars.aim;
        return p;
    }

    public Koohii.Map getMap() {
        return beatmap;
    }

}
