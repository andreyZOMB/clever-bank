import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor @NoArgsConstructor @ToString
public class DBdata {
    public String driver = "org.postgresql.Driver";
    public String DBurl = "jdbc:postgresql://localhost:5432/clever_bank";
    public String username = "postgres";
    public String password = "SeredinovA13";
}
