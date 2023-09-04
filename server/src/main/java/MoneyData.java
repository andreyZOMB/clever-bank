import java.math.BigDecimal;

final class MoneyData {
    public String currency;
    public BigDecimal value;

    public MoneyData(String currency, BigDecimal value) {
        this.currency = currency;
        this.value = value;
    }
}