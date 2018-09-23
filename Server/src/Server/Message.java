package Server;

import java.util.ArrayList;

public class Message {
    public Message() {
    }

    public int Vote(ArrayList<Player> players) {
        int count = 0;

        for(int i = 0; i < players.size(); ++i) {
            if (((Player)players.get(i)).getVote() == 1) {
                ++count;
            }
        }

        if (count == players.size()) {
            return 1;
        } else {
            return -1;
        }
    }

    public int Score(String word) {
        int finalPoint = 0;
        char[] var6;
        int var5 = (var6 = word.toCharArray()).length;

        for(int var4 = 0; var4 < var5; ++var4) {
            char var10000 = var6[var4];
            ++finalPoint;
        }

        return finalPoint;
    }

    public int Ready(ArrayList<Player> players) {
        int count = 0;

        for(int i = 0; i < players.size(); ++i) {
            if (((Player)players.get(i)).getReady() == 1) {
                ++count;
            }
        }

        if (count == players.size()) {
            return 1;
        } else {
            return -1;
        }
    }
}