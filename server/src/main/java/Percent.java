import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@AllArgsConstructor @NoArgsConstructor @ToString
public class Percent {
    public BigDecimal percent = new BigDecimal("0.01");
}