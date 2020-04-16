package ai.preferred.crawler.tutorial.master;

import ai.preferred.venom.request.Request;
import ai.preferred.venom.response.Response;
import ai.preferred.venom.response.VResponse;
import ai.preferred.venom.validator.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameValidator implements Validator {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameValidator.class);

    /**
     * Use this to positively validate your page.
     * <p>
     * For example, if you are crawling store ABC, you would find.
     * </p>
     *
     * @param request  The request used to fetch.
     * @param response The response fetched using request.
     * @return status of the validation
     */
    @Override
    public Validator.Status isValid(Request request, Response response) {
        final VResponse vResponse = new VResponse(response);

        if (vResponse.getHtml().contains("Steam")) {
            return Status.VALID;
        }

        LOGGER.info("Invalid content");
        return Status.INVALID_CONTENT;
    }

}
