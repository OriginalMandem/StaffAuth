package originalalex.github.io.helper;

import java.util.UUID;

public class CodeGenerator {

    public static String generateCode() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }

}
