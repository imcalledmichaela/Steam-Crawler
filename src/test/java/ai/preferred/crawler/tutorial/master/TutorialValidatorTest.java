package ai.preferred.crawler.tutorial.master;

import ai.preferred.crawler.tutorial.master.TutorialValidator;
import ai.preferred.venom.request.Request;
import ai.preferred.venom.request.VRequest;
import ai.preferred.venom.response.BaseResponse;
import ai.preferred.venom.response.Response;
import ai.preferred.venom.validator.Validator;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class TutorialValidatorTest {

  private byte[] getContent(String filename) throws IOException {
    final InputStream stream = getClass().getClassLoader().getResourceAsStream(filename);
    Assertions.assertNotNull(stream);
    return IOUtils.toByteArray(
        new BufferedInputStream(
            new GZIPInputStream(stream)
        )
    );
  }

  @Test
  public void testEx05ValidateAbout() throws IOException {
    final String page = "About – Preferred.AI.html.gz";

    final int statusCode = 200;
    final String url = "https://preferred.ai/";
    final ContentType contentType = ContentType.create("text/html", StandardCharsets.UTF_8);
    final Header[] headers = {};
    final HttpHost proxy = null;

    final Request request = new VRequest(page);
    final Response response = new BaseResponse(statusCode, url, getContent(page), contentType, headers, proxy);

    Assertions.assertEquals(new TutorialValidator().isValid(request, response), Validator.Status.INVALID_CONTENT);
  }

  @Test
  public void testEx05ValidateJoinUs() throws IOException {
    final String page = "Join Us – Preferred.AI.html.gz";

    final int statusCode = 200;
    final String url = "https://preferred.ai/";
    final ContentType contentType = ContentType.create("text/html", StandardCharsets.UTF_8);
    final Header[] headers = {};
    final HttpHost proxy = null;

    final Request request = new VRequest(page);
    final Response response = new BaseResponse(statusCode, url, getContent(page), contentType, headers, proxy);

    Assertions.assertEquals(new TutorialValidator().isValid(request, response), Validator.Status.INVALID_CONTENT);
  }

  @Test
  public void testEx05ValidateOurProjects() throws IOException {
    final String page = "Get to Know Our Projects – Preferred.AI.html.gz";

    final int statusCode = 200;
    final String url = "https://preferred.ai/";
    final ContentType contentType = ContentType.create("text/html", StandardCharsets.UTF_8);
    final Header[] headers = {};
    final HttpHost proxy = null;

    final Request request = new VRequest(page);
    final Response response = new BaseResponse(statusCode, url, getContent(page), contentType, headers, proxy);

    Assertions.assertEquals(new TutorialValidator().isValid(request, response), Validator.Status.INVALID_CONTENT);
  }

  @Test
  public void testEx05ValidateOurPapersDec2017() throws IOException {
    final String page = "Read Our Papers – Preferred.AI - 201712.html.gz";

    final int statusCode = 200;
    final String url = "https://preferred.ai/";
    final ContentType contentType = ContentType.create("text/html", StandardCharsets.UTF_8);
    final Header[] headers = {};
    final HttpHost proxy = null;

    final Request request = new VRequest(page);
    final Response response = new BaseResponse(statusCode, url, getContent(page), contentType, headers, proxy);

    Assertions.assertEquals(new TutorialValidator().isValid(request, response), Validator.Status.VALID);
  }

  @Test
  public void testEx05ValidateOurPapersJan2017() throws IOException {
    final String page = "Read Our Papers – Preferred.AI - 201701.html.gz";

    final int statusCode = 200;
    final String url = "https://preferred.ai/";
    final ContentType contentType = ContentType.create("text/html", StandardCharsets.UTF_8);
    final Header[] headers = {};
    final HttpHost proxy = null;

    final Request request = new VRequest(page);
    final Response response = new BaseResponse(statusCode, url, getContent(page), contentType, headers, proxy);

    Assertions.assertEquals(new TutorialValidator().isValid(request, response), Validator.Status.VALID);
  }

  @Test
  public void testEx05ValidateOurPapersJul2018() throws IOException {
    final String page = "Read Our Papers – Preferred.AI - 201807.html.gz";

    final int statusCode = 200;
    final String url = "https://preferred.ai/";
    final ContentType contentType = ContentType.create("text/html", StandardCharsets.UTF_8);
    final Header[] headers = {};
    final HttpHost proxy = null;

    final Request request = new VRequest(page);
    final Response response = new BaseResponse(statusCode, url, getContent(page), contentType, headers, proxy);

    Assertions.assertEquals(new TutorialValidator().isValid(request, response), Validator.Status.VALID);
  }

  @Test
  public void testEx05ValidateOurPapersSep2018() throws IOException {
    final String page = "Read Our Papers – Preferred.AI - 201810.html.gz";

    final int statusCode = 200;
    final String url = "https://preferred.ai/";
    final ContentType contentType = ContentType.create("text/html", StandardCharsets.UTF_8);
    final Header[] headers = {};
    final HttpHost proxy = null;

    final Request request = new VRequest(page);
    final Response response = new BaseResponse(statusCode, url, getContent(page), contentType, headers, proxy);

    Assertions.assertEquals(new TutorialValidator().isValid(request, response), Validator.Status.VALID);
  }


}
