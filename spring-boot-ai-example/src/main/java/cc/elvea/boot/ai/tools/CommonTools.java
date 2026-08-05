package cc.elvea.boot.ai.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;

import java.time.LocalDateTime;

/**
 * @author elvea
 */
@RegisterReflection(memberCategories = MemberCategory.INVOKE_DECLARED_METHODS)
public class CommonTools {

    @Tool(name = "getCurrentDateTime", description = "获取系统当前时间")
    public String getCurrentDateTime() {
        return LocalDateTime.now().toString();
    }

}
