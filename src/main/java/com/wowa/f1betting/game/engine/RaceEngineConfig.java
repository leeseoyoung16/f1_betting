package com.wowa.f1betting.game.engine;

public class RaceEngineConfig {
    public static final double ACCEL_VARIATION = 0.04;  // 가속
    public static final double GENERAL_VARIATION = 0.03; // 전체
    public static final double LUCK_BOOST = 1.9;       // 운 보정
    public static final double MALFUNCTION_PENALTY = 0.4; // 고장
    public static final double SCALE = 0.7;           // 게임 속도 비율

    private RaceEngineConfig() {}
}
