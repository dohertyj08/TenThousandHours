package com.james.tenthousandhours;

import android.content.Context;
import android.os.AsyncTask;

public class Helpers {

    // someday I'll replace this with an algorithm, but until then, its based on the runescape
    // xp rates
    private final long[] levels = {0L, 229239L, 480573L, 762288L, 1071623L, 1414100L,
            1795245L, 2212294L, 2676296L, 3187250L, 3750681L, 4374874L, 5062591L,
            5819356L, 6658978L, 7584220L, 8603367L, 9730229L, 10973091L, 12345763L,
            13859293L, 15533014L, 17375211L, 19410743L, 21658943L, 24139143L, 26873440L,
            29894975L, 33228608L, 36907479L, 40967496L, 45450085L, 50396676L, 55856983L,
            61883483L, 68536938L, 75878110L, 83984333L, 92932940L, 102809551L, 113710832L,
            125747261L, 139032075L, 153697848L, 169890960L, 187763317L, 207494442L, 229277672L,
            253325672L, 279873206L, 309182656L, 341538499L, 377261117L, 416695749L, 460234589L,
            508302970L, 561370419L, 619956176L, 684640242L, 756052335L, 834893981L, 921938518L,
            1018039375L, 1124141130L, 1241282262L, 1370611728L, 1513400009L, 1671047397L, 1845100564L,
            2037263613L, 2249428456L, 2483672052L, 2742292317L, 3027827451L, 3343080798L, 3691142942L,
            4075430373L, 4499713106L, 4968156109L, 5485352448L, 6056378525L, 6686838267L, 7382915602L,
            8151443511L, 8999962023L, 9936798315L, 10971141279L, 12113143719L, 13374010725L, 14766118444L,
            16303121785L, 18000103571L, 19873720916L, 21942354368L, 24226304009L, 26747985546L, 29532140221L,
            32606088904L, 36000000000L };

    public int getCurrentLevel(long milliseconds) {
        int level = 0;
        for (long n : levels) {
            if (milliseconds >= n) {
                level++;
            } else {
                break;
            }
        }
        return level;
    }

    public long getTrainingTimeRemainingInMinutes(long milliseconds) {
        for (long n : levels) {
            if (milliseconds < n) {
                return (n - milliseconds) / 1000 / 60;
            }
        }
        return -1; //max level???
    }

    public int percentToNextLevel(long milliseconds) {
        long previousLevel = 0;
        for (long n : levels) {
            if (milliseconds < n) {
                double truePercent = (double) (milliseconds - previousLevel) / (double) (n - previousLevel);
                int percent = (int) (truePercent * 100);
                return percent;
            }
            previousLevel = n;
        }
        return 0;
    }

    public String getMotivational() {
        int random = (int) (Math.random() * 10 + 1);

        switch (random) {
            case 1:
                return "You're doing great!";
            case 2:
                return "Be better.";
            case 3:
                return "Practice makes perfect!";
            case 4:
                return "Enjoy the grind!";
            case 5:
                return "Follow your dreams!";
            case 6:
                return "Never give up!";
            case 7:
                return "Good Job!";
            case 8:
                return "git gud";
            case 9:
                return "Nice.";
            default:
                return "Keep it up";
        }
    }

    public int[] getColorArray(Context c, String scheme) {
        int[] colors = new int[10];

        if (scheme.equals("green")) {
            colors[0] = c.getResources().getColor(R.color.green1);
            colors[1] = c.getResources().getColor(R.color.green2);
            colors[2] = c.getResources().getColor(R.color.green3);
            colors[3] = c.getResources().getColor(R.color.green4);
            colors[4] = c.getResources().getColor(R.color.green5);
            colors[5] = c.getResources().getColor(R.color.green6);
            colors[6] = c.getResources().getColor(R.color.green7);
            colors[7] = c.getResources().getColor(R.color.green8);
            colors[8] = c.getResources().getColor(R.color.green9);
            colors[9] = c.getResources().getColor(R.color.green0);
        } else if (scheme.equals("mono")) {
            colors[0] = c.getResources().getColor(R.color.mono1);
            colors[1] = c.getResources().getColor(R.color.mono2);
            colors[2] = c.getResources().getColor(R.color.mono3);
            colors[3] = c.getResources().getColor(R.color.mono4);
            colors[4] = c.getResources().getColor(R.color.mono5);
            colors[5] = c.getResources().getColor(R.color.mono6);
            colors[6] = c.getResources().getColor(R.color.mono7);
            colors[7] = c.getResources().getColor(R.color.mono8);
            colors[8] = c.getResources().getColor(R.color.mono9);
            colors[9] = c.getResources().getColor(R.color.mono0);
        } else {
            colors[0] = c.getResources().getColor(R.color.blue1);
            colors[1] = c.getResources().getColor(R.color.blue2);
            colors[2] = c.getResources().getColor(R.color.blue3);
            colors[3] = c.getResources().getColor(R.color.blue4);
            colors[4] = c.getResources().getColor(R.color.blue5);
            colors[5] = c.getResources().getColor(R.color.blue6);
            colors[6] = c.getResources().getColor(R.color.blue7);
            colors[7] = c.getResources().getColor(R.color.blue8);
            colors[8] = c.getResources().getColor(R.color.blue9);
            colors[9] = c.getResources().getColor(R.color.blue0);
        }
        return colors;
    }
}
