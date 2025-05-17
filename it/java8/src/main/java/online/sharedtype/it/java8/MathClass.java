package online.sharedtype.it.java8;

import lombok.Data;
import online.sharedtype.SharedType;

import java.math.BigDecimal;
import java.math.BigInteger;

@SharedType(
    rustMacroTraits = {"PartialEq", "Eq", "Hash", "serde::Serialize", "serde::Deserialize"}
)
@Data
public final class MathClass {
    private BigInteger bigInteger;
    private BigDecimal bigDecimal;
}
