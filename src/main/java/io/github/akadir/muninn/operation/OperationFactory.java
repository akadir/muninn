package io.github.akadir.muninn.operation;

import io.github.akadir.muninn.enumeration.TelegramOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author akadir
 * Date: 2.05.2020
 * Time: 23:40
 */
@Lazy
@Component
public class OperationFactory {
    private final Logger logger = LoggerFactory.getLogger(OperationFactory.class);
    private final Map<String, Operation> operationMap;


    @Autowired
    public OperationFactory(@Qualifier("start") Operation loginOperation,
                            @Qualifier("stop") Operation logoutOperation,
                            @Qualifier("help") Operation helpOperation) {
        this.operationMap = new HashMap<>();

        operationMap.put(TelegramOption.LOGIN.getOption(), loginOperation);
        operationMap.put(TelegramOption.LOGOUT.getOption(), logoutOperation);
        operationMap.put(TelegramOption.HELP.getOption(), helpOperation);
    }

    public Operation getOperation(String text) {
        Operation operation;

        if (operationMap.containsKey(text)) {
            operation = operationMap.get(text);
        } else {
            if (text != null && text.length() == 7 && isNumeric(text)) {
                operation = operationMap.get(TelegramOption.LOGIN.getOption());
            } else {
                operation = operationMap.get(TelegramOption.HELP.getOption());
            }
        }

        logger.info("Found operation for command: {} as: {}", text, operation.getOption());

        return operation;
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
