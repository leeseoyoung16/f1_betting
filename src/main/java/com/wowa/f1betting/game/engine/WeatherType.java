package com.wowa.f1betting.game.engine;

public enum WeatherType {
    SUNNY("☀️", 1.2, "트랙 컨디션이 아주 좋습니다! 빠른 기록이 기대됩니다."),
    CLOUDY("☁️", 1.1, "기온이 낮아 엔진 효율이 괜찮을 것 같습니다."),
    RAINY("🌧️", 0.9, "노면이 미끄럽습니다. 스핀에 주의해야겠군요."),
    STORM("⛈️", 0.8, "강한 비바람이 불고 있습니다. 매우 위험한 레이스가 되겠군요."),
    WINDY("🍃", 1.1, "바람이 강합니다. 직선 구간에서 흔들릴 수 있어요."),
    SNOWY("☃️", 0.7, "눈이 쌓여 제어가 매우 어렵습니다. 정말 조심해야 합니다.");

    private final String icon;
    private final double penalty;
    private final String commentary;

    WeatherType(String icon, double penalty, String commentary) {
        this.icon = icon;
        this.penalty = penalty;
        this.commentary = commentary;
    }

    public String getIcon() {
        return icon;
    }

    public double getPenalty() {
        return penalty;
    }

    public String getCommentary() {
        return commentary;
    }

    public static WeatherType random() {
        WeatherType[] values = WeatherType.values();
        return values[(int) (Math.random() * values.length)];
    }
}
