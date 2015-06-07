package Util;

import android.util.Log;

import java.util.Comparator;

import Database.CommentOnlineDBFormat;

public class CommentUtil implements Comparator<CommentOnlineDBFormat> {
    public static final String TAG = CommentUtil.class.getSimpleName();

    @Override
    public int compare(CommentOnlineDBFormat lhs, CommentOnlineDBFormat rhs) {
        String lPath = lhs.getParent_path() + String.valueOf(lhs.getComment_id());
        String rPath = rhs.getParent_path() + String.valueOf(rhs.getComment_id());
        return compare(lPath, rPath, lhs.getScore(), rhs.getScore());
    }

    public int compare(String lhs, String rhs, int lscore, int rscore) {

        String[] lhsPath = lhs.split("\\.");
        String[] rhsPath = rhs.split("\\.");

        int lhsPathLength = lhsPath.length;
        int rhsPathLength = rhsPath.length;
        int min = Math.min(lhsPathLength, rhsPathLength);

        for (int i = 0; i < min; i++) {
            int lhsItem = Integer.parseInt(lhsPath[i]);
            int rhsItem = Integer.parseInt(rhsPath[i]);

            if (lhsItem == rhsItem) {
                // Check next time of parent path hierarchy
                continue;
            } else {
                return integerCompare(lhsItem, rhsItem);
            }
        }

        if (integerCompare(lhsPathLength, rhsPathLength) == 0) {
            return integerCompare(lscore, rscore);
        } else {
            // Comment with short path comes first (less nested)
            return integerCompare(lhsPathLength, rhsPathLength);
        }
    }

    private int integerCompare(int l, int r) {
        if (l < r) {
            return -1;
        } else if (l == r) {
            return 0;
        } else {
            return 1;
        }
    }
}
