package net.iGap.interfaces;

import net.iGap.proto.ProtoUserIVandGetScore;

public interface OnUserIVandGetScore {
    void getScore(ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder ivandGetScore);
    void onError();
}
